package com.example.screenshot;

import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private final int PACK_TYPE_OTHER = 0;
	private final int PACK_TYPE_SERVERFOUND = 1;
	private final int PACK_TYPE_SERVERREADY = 2;
	
	private final int STEP_BROADCAST = 0;
	private final int STEP_FOUNDSERVER = 1;
	private final int STEP_CONNECTED = 2;

	private Handler handler = new Handler();
	private Thread m_recvThread;
	private Message m_brodMsg;
	private Message m_respMsg;
	private boolean m_waiteSrv = true;
	private int m_currentStep = STEP_BROADCAST;
	private InetAddress m_serverIP;
	private String m_serverName;
	
	public final static String EXTRA_MESSAGE = "destination_ip";
	
	static private int m_openfileDialogId = 0;
	
	public native boolean isFbAvaliable();
	public native boolean playVideo(String ip, String filename, int status);
	static{
		System.loadLibrary("screenshot");
	}
	
	public static boolean RootCommand(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		Log.d("*** DEBUG ***", "Root SUC ");
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button startBtn = (Button) findViewById(R.id.start);
		Button stopBtn = (Button) findViewById(R.id.stop);
		
		Button videoplayBtn = (Button) findViewById(R.id.videoplay);
		Button videopauseBtn = (Button) findViewById(R.id.videopause);
		Button videostopBtn = (Button) findViewById(R.id.videostop);
		Button browseBtn = (Button) findViewById(R.id.browse);
		
		startBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);
		
		videoplayBtn.setOnClickListener(this);
		videopauseBtn.setOnClickListener(this);	
		videostopBtn.setOnClickListener(this);	
		browseBtn.setOnClickListener(this);

		if (!isFbAvaliable()) {
			RootCommand("chmod 0666 /dev/graphics/fb0");
		}
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		m_waiteSrv = false;
		m_currentStep = STEP_BROADCAST;
		if( m_brodMsg != null )
			m_brodMsg.stop = true;
		if( m_respMsg != null )
			m_respMsg.stop = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == m_openfileDialogId){
			Map<String, Integer> images = new HashMap<String, Integer>();
			// Set icon
			images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root);
			images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up);
			images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder);
			images.put("wav", R.drawable.filedialog_wavfile);
			images.put(OpenFileDialog.sEmpty, R.drawable.filedialog_root);
			Dialog dialog = OpenFileDialog.createDialog(id, this, "Select video file", new CallbackBundle() {
				@Override
				public void callback(Bundle bundle) {
					String filepath = bundle.getString("path");
					EditText filenameCtrl = (EditText) findViewById(R.id.filename);
					filenameCtrl.setText(filepath.toString().substring(1));					
				}
			}, 
			".wav;.3gp;.mp4",
			images);
			return dialog;
		}
		return null;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//String videoname = "/mnt/sdcard/downloads/bluetooth/VIDEO0001.3gp";
		//String videoname = "/mnt/sdcard/DCIM/Camera/VID_20121223_123429.mp4";
		EditText filenameCtrl = (EditText) findViewById(R.id.filename);
		String videoname = filenameCtrl.getText().toString();
		
		EditText ipCtrl = (EditText) findViewById(R.id.server_ip);
		String ipaddr = ipCtrl.getText().toString();
		
		switch (v.getId()) {
		case R.id.start:
			Intent intent = new Intent(this, ScreenshotService.class);
			Bundle bundle = new Bundle();
			bundle.putString(EXTRA_MESSAGE, ipaddr);
			intent.putExtras(bundle);
			
			startService(intent);
			break;
		case R.id.stop:
			stopService(new Intent(this, ScreenshotService.class));
			break;
		case R.id.videoplay:
			playVideo(ipaddr, videoname, 1);
			break;
		case R.id.videopause:
			playVideo(ipaddr, videoname, 2);
			break;	
		case R.id.videostop:
			playVideo(ipaddr, videoname, 0);
			break;	
		case R.id.browse:
			showDialog(m_openfileDialogId);
			break;
		default:
			break;
		}
	}
	
	public void FindService(View v)
	{
		m_waiteSrv = true;
		m_currentStep = STEP_BROADCAST;
		if( m_brodMsg != null )
			m_brodMsg.stop = true;
		if( m_respMsg != null )
			m_respMsg.stop = true;
		
		m_brodMsg = new	Message();
		m_brodMsg.str = "S3" + getWifiIpAddress();
		Thread sendThread = new Thread(new SendThread("255.255.255.255", 9050, m_brodMsg));
		sendThread.start();
		if( m_recvThread==null )
		{
			m_recvThread = new Thread(new ReceiveThread());
			m_recvThread.start();
		}
	}
	
	private String getWifiIpAddress() {
		try {
			WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ip = wifiInfo.getIpAddress();

			String ipString = String.format("%d.%d.%d.%d", (ip & 0xff),
					(ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));

			return ipString;
		} catch (Exception e) {
			Log.e("testWifi", "getWifiIpAddress error");
			return null;
		}
	}

	private int getPackType(DatagramPacket receivePack) {
		String packString = new String(receivePack.getData(), 0,
				receivePack.getLength());
		if (packString.startsWith("S3Graphics VideoWall"))
			return PACK_TYPE_SERVERFOUND;
		if (packString.startsWith("VideoWall Creates Thread"))
			return PACK_TYPE_SERVERREADY;
		return PACK_TYPE_OTHER;
	}
	
	public class Message
	{
		public String str;
		public boolean stop;
	}


	public class ReceiveThread implements Runnable {

		public void run() {
			Log.d("testWifi", "ReceiveThread run()");

			try {
				DatagramSocket receiveSocket = new DatagramSocket(9060);				
				while (m_waiteSrv) {

					byte[] data = new byte[128];
					final DatagramPacket receivePack = new DatagramPacket(data, data.length);
					receiveSocket.receive(receivePack);

					Log.d("testWifi", new String(receivePack.getData()));
					Log.d("testWifi", receivePack.getAddress().toString());

					switch (getPackType(receivePack)) {
					case PACK_TYPE_SERVERFOUND:
						if (m_currentStep == STEP_BROADCAST) {
							m_brodMsg.stop = true;

							m_serverName = new String(receivePack.getData());
							m_respMsg = new Message();
							m_respMsg.str = "S3WH" + getWifiIpAddress();
							Thread respThread;
							respThread = new Thread(new SendThread(
									receivePack.getAddress(), 9050, m_respMsg));
							respThread.start();
							m_currentStep = STEP_FOUNDSERVER;
						}

						break;

					case PACK_TYPE_SERVERREADY:
						if (m_currentStep == STEP_FOUNDSERVER) {
							m_respMsg.stop = true;
							handler.post(new Runnable() {
								@Override
								public void run() {
									m_serverIP = receivePack.getAddress();
									TextView nameTV = (TextView) findViewById(R.id.server_name);
									nameTV.setText(m_serverName);
									
									EditText ipCtrl = (EditText) findViewById(R.id.server_ip);
									ipCtrl.setText(m_serverIP.toString().substring(1));
								}
							});
							m_currentStep = STEP_CONNECTED;
						}

					}
				}
				receiveSocket.close();
			} catch (Exception e) {
				Log.e("testWifi", "ReceiveThread error");
			}

		}
	}

	public class SendThread implements Runnable {
		
		private InetAddress m_ip;
		private int m_port;
		private Message m_message;

		public SendThread(String ip, int port, Message message) {
			try {
				m_ip = InetAddress.getByName(ip);
				m_port = port;
				m_message = message;
			} catch (UnknownHostException e) {
				Log.e("testWifi", e.getMessage());
			}
		}

		public SendThread(InetAddress ip, int port, Message message) {
			m_ip = ip;
			m_port = port;
			m_message = message;
		}

		public void run() {
			Log.d("testWifi", "SendThread run()");
			try {
				DatagramSocket datagramSocket = new DatagramSocket();

				byte[] data = m_message.str.getBytes();
				DatagramPacket thePacket = new DatagramPacket(data,
						data.length, m_ip, m_port);

				while (!m_message.stop) {
					datagramSocket.send(thePacket);
					Log.d("testWifi", "send " + m_message.str);
					Thread.sleep(2000);
				}

				datagramSocket.close();
				Log.d("testWifi", "datagramSocket.close()");
			} catch (Exception e) {
				Log.e("testWifi", "SendThread error");
			}
		}
	}
}
