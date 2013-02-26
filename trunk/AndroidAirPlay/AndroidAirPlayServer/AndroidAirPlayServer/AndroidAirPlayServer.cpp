// AndroidAirPlayServer.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <vector>
#include <list>
#include <iostream>
#include <string>

#include <windows.h>
#include <gst/gst.h>
#include <gst/app/gstappsrc.h>
#include <string>
#include <algorithm>
#include <process.h>
using namespace std;

#define PLAY_FILE 0
#define USE_LOG	0
#define USE_TCP 0
#define BUFF_SIZE (8*1024)
#define MIRROR_TEST	1

CRITICAL_SECTION m_lock;
std::list<GstBuffer *> m_pBufferList;

FILE* m_pLogFile = NULL;

#pragma region Macro
#define S3ConnectReq 1
#define S3CreateThread 2
#pragma endregion Macro

#pragma region Function
unsigned int judgeBuffer(char* buffParam);
void ServiceResp(char* buffParam, SOCKET &serSocket);
void ServiceCreateThread(char* buffParam, SOCKET &serSocket);
unsigned int WINAPI listenThread(void *para);
#pragma endregion Function

extern DWORD WINAPI RecvThreadProc(LPVOID lpParameter);

static vector<string> ipList;
const string title = "[S3Graphics]: ";

unsigned int WINAPI connectThread(void *para)
{
	cout << title << "VideoWall [Server] side connection listen thread!!" << endl;

	int nPort = (int)para;

	WORD myVersionRequest;
	WSADATA wsaData;
	myVersionRequest = MAKEWORD(1,1);
	int err;
	err = WSAStartup(myVersionRequest, &wsaData);

	if (err)
	{
		return -1;
	}

	SOCKADDR_IN addr;
	addr.sin_family = AF_INET;
	addr.sin_addr.S_un.S_addr = htonl(INADDR_ANY);
	addr.sin_port = htons(9050);

	SOCKET serSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
	bind(serSocket, (SOCKADDR*)&addr, sizeof(SOCKADDR));

	char recBuff[128];
	memset(recBuff,0,128);

	while (true)
	{
		recv(serSocket,recBuff,sizeof(recBuff),0);
		cout << "Receive: " << recBuff << endl;
		switch (judgeBuffer(recBuff))
		{
		case S3ConnectReq:
			ServiceResp(recBuff,serSocket);
			break;
		case S3CreateThread:
			ServiceCreateThread(recBuff,serSocket);
			break;
		default:
			break;
		}
		memset(recBuff,0,128);
	}
	return 0;
}

void ServiceCreateThread(char* buffParam, SOCKET &serSocket)
{
	char ip[128];
	memset(ip,0,128);
	int i = 0;
	do 
	{
		ip[i] = buffParam[i+4];
		i++;
	} while (buffParam[i+4] != 0);
	ip[i] = 0;

	const char* svrRspString = "VideoWall Creates Thread"; 
	SOCKADDR_IN addrClient;
	addrClient.sin_family = AF_INET;
	addrClient.sin_addr.S_un.S_addr = inet_addr(ip);
	addrClient.sin_port = htons(9060);

	sendto(serSocket,svrRspString,strlen(svrRspString) + 1,0,(SOCKADDR*)&addrClient, sizeof(SOCKADDR));	

	//TO DO... 
	//Create the listen thread here. Base on TCP//RTP
	string ipstring(ip);
	vector<string>::iterator result = find(ipList.begin(), ipList.end(), ipstring);
	if (result == ipList.end())
	{
		ipList.push_back(ipstring);
		HANDLE listenthreadhandle = (HANDLE)_beginthreadex(NULL,0,listenThread,(LPVOID)&ipList.at((ipList.size() - 1)),0,NULL);
		
		if (listenthreadhandle == NULL)
		{
			cout << "Create listen thread failed!!" << endl;
		}
	}
}

unsigned int WINAPI listenThread(void *para)
{
	string clientIP = *(string*)para;
	cout << "Client IP address is " << clientIP << endl;

	WORD wVersionRequested;
	WSADATA wsaData;
	int err;

	wVersionRequested = MAKEWORD(1,1);

	err = WSAStartup(wVersionRequested, &wsaData);
	if (err != 0)
	{
		return -1;
	}

	if (LOBYTE(wsaData.wVersion) != 1 || HIBYTE(wsaData.wVersion) != 1)
	{
		WSACleanup();
		return -1;
	}
#if USE_TCP
	SOCKET sockSrv = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

	SOCKADDR_IN addrSrv;
	addrSrv.sin_addr.S_un.S_addr = htonl(INADDR_ANY);
	addrSrv.sin_family = AF_INET;
	addrSrv.sin_port = htons(9070);

	bind(sockSrv, (SOCKADDR*)&addrSrv, sizeof(SOCKADDR));
	listen(sockSrv,30);

	SOCKADDR_IN addrClient;
	int len = sizeof(SOCKADDR);
	//const char* svrRspString = "VideoWall Listen Thread for TCP connection!"; 
	SOCKET sockConn = accept(sockSrv, (SOCKADDR*)&addrClient, &len);

	//send(sockConn,svrRspString,strlen(svrRspString) + 1, 0);
	char recvBuf[20480];
	memset(recvBuf,0,20480);

	int iResult;
	do {
		iResult = recv(sockConn,recvBuf,20480,0);
		if ( iResult > 0 )
		{
			cout << "Bytes received[TCP]: " << iResult << endl;
			if (recvBuf[0] == 'S' && recvBuf[1] == '3' && recvBuf[2] == 'S' && recvBuf[3] == 'T' && recvBuf[4] == 'O' && recvBuf[5] == 'P')
			{
				break;
			}

#if USE_LOG
			fwrite(pRecvBuf, 1, nRecvLen, m_pLogFile);
			fflush(m_pLogFile);
			continue;
#endif

			guint8 *pdata = (guint8 *)g_malloc(iResult);
			memcpy(pdata, recvBuf, iResult);

			GstBuffer *gst_buffer = gst_buffer_new();
			GST_BUFFER_MALLOCDATA(gst_buffer) = pdata;
			GST_BUFFER_SIZE(gst_buffer) = iResult;
			GST_BUFFER_DATA(gst_buffer) = GST_BUFFER_MALLOCDATA(gst_buffer);

			EnterCriticalSection(&m_lock);
			m_pBufferList.push_back(gst_buffer);
			LeaveCriticalSection(&m_lock);
		}
		else if ( iResult == 0 )
		{
			cout << "Connection closed!" << endl;
		}
		else
		{
			cout << "Recv failed: " << WSAGetLastError() << endl;
		}
	} while( iResult > 0 );

#if USE_LOG
	unsigned char file_buf[4];
	file_buf[0] = 0x00;
	file_buf[1] = 0x00;
	file_buf[2] = 0x01;
	file_buf[3] = 0xb7;
	fwrite(file_buf, 1, 4, m_pLogFile);

	fclose(m_pLogFile);
#endif

	closesocket(sockConn);
	WSACleanup();
	vector<string>::iterator result = find(ipList.begin(), ipList.end(), clientIP);
	if (result != ipList.end())
	{
		ipList.erase(result);
		cout << "Return from this thread, clear this IP" << clientIP << " !!" << endl;
	}
	return 0;
#else //USE_UDP
	SOCKET m_sockfd;
	if ((m_sockfd = socket (AF_INET, SOCK_DGRAM, IPPROTO_UDP)) < 0)
	{
		perror ("socket error");
		return 0;
	}

	struct sockaddr_in localAddr;
	memset (&localAddr, 0, sizeof (localAddr));
	localAddr.sin_family = AF_INET;
	localAddr.sin_port = htons (9070);
	localAddr.sin_addr.s_addr = INADDR_ANY;	

	if (bind(m_sockfd, (struct sockaddr *) &localAddr, sizeof (localAddr)) < 0)
	{
		perror ("bind error");
		closesocket(m_sockfd);
		return 0;
	}	

	//unsigned char pRecvBuf[BUFF_SIZE];
	char pRecvBuf[20480];
	memset(pRecvBuf,0,20480);
	int nRecvLen = 0;
	struct sockaddr_in remoteAddr;
	int fromlen = sizeof (struct sockaddr);

	while(1)
	{
		//memset(pRecvBuf, 0, BUFF_SIZE);
		//nRecvLen = recvfrom (m_sockfd, (char*)pRecvBuf, BUFF_SIZE, 0, (struct sockaddr *) &remoteAddr, &fromlen);
		nRecvLen = recv(m_sockfd,pRecvBuf,20480,0);
		if (-1 == nRecvLen)
		{
			perror ("recvfrom error\n");
			break;
		}
		cout << "Bytes received[UDP]: " << nRecvLen << endl;

		if (pRecvBuf[0] == 'S' && pRecvBuf[1] == '3' && pRecvBuf[2] == 'S' && pRecvBuf[3] == 'T' && pRecvBuf[4] == 'O' && pRecvBuf[5] == 'P')
		{
			break;
		}

#if USE_LOG
		fwrite(pRecvBuf, 1, nRecvLen, m_pLogFile);
		fflush(m_pLogFile);
		continue;
#endif

		guint8 *pdata = (guint8 *)g_malloc(nRecvLen);
		memcpy(pdata, pRecvBuf, nRecvLen);

		GstBuffer *gst_buffer = gst_buffer_new();
		GST_BUFFER_MALLOCDATA(gst_buffer) = pdata;
		GST_BUFFER_SIZE(gst_buffer) = nRecvLen;
		GST_BUFFER_DATA(gst_buffer) = GST_BUFFER_MALLOCDATA(gst_buffer);

		EnterCriticalSection(&m_lock);
		m_pBufferList.push_back(gst_buffer);
		LeaveCriticalSection(&m_lock);
	}

#if USE_LOG
	unsigned char file_buf[4];
	file_buf[0] = 0x00;
	file_buf[1] = 0x00;
	file_buf[2] = 0x01;
	file_buf[3] = 0xb7;
	fwrite(file_buf, 1, 4, m_pLogFile);

	fclose(m_pLogFile);
#endif

	closesocket(m_sockfd);
	WSACleanup();

	vector<string>::iterator result = find(ipList.begin(), ipList.end(), clientIP);
	if (result != ipList.end())
	{
		ipList.erase(result);
		cout << "Return from this thread, clear this IP" << clientIP << " !!" << endl;
	}
	return 0;

#endif
}

void ServiceResp(char* buffParam, SOCKET &serSocket)
{
	char ip[128];
	memset(ip,0,128);
	int i = 0;
	do 
	{
		ip[i] = buffParam[i+2];
		i++;
	} while (buffParam[i+2] != 0);
	ip[i] = 0;

	const char* svrRspString = "S3Graphics VideoWall"; 
	SOCKADDR_IN addrClient;
	addrClient.sin_family = AF_INET;
	addrClient.sin_addr.S_un.S_addr = inet_addr(ip);
	addrClient.sin_port = htons(9060);

	sendto(serSocket,svrRspString,strlen(svrRspString) + 1,0,(SOCKADDR*)&addrClient, sizeof(SOCKADDR));	
}

unsigned int judgeBuffer(char* buffParam)
{
	if (buffParam[0] == 'S' && buffParam[1] == '3' && buffParam[2] != 'W' && buffParam[3] != 'H')
	{
		return 1;
	}
	else if(buffParam[0] == 'S' && buffParam[1] == '3' && buffParam[2] == 'W' && buffParam[3] == 'H')
	{
		return 2;
	}
	else
	{
		return 0;
	}
}

typedef struct {
	GstPipeline *pipeline;
	GstAppSrc *src;
	GstElement *typefind;
	GstElement *decoder;
	GstElement *ffmpeg;
	GstElement *videoscale;
	GstElement *sink;

	GMainLoop *loop;
	guint sourceid;
	FILE *file;
}gst_app_t;

static gst_app_t gst_app;

static gboolean read_data(gst_app_t *app)
{
#if !PLAY_FILE
	GstBuffer *gst_buffer = NULL;
	if (!m_pBufferList.empty())
	{
		printf("buffer length:%d\n", m_pBufferList.size());
		EnterCriticalSection(&m_lock);
		gst_buffer = m_pBufferList.front();
		m_pBufferList.pop_front();	
		LeaveCriticalSection(&m_lock);
	}

	if (gst_buffer == NULL)
	{
		return TRUE;
	}

	GstFlowReturn ret = gst_app_src_push_buffer(app->src, gst_buffer);

	if(ret !=  GST_FLOW_OK){
		g_debug("push buffer returned %d\n", ret);
		return FALSE;
	}

	return TRUE;
#else
	GstBuffer *buffer;
	guint8 *ptr;
	gint size;
	GstFlowReturn ret;

	ptr = (guint8 *)g_malloc(BUFF_SIZE);
	g_assert(ptr);

	size = fread(ptr, 1, BUFF_SIZE, app->file);

	if(size == 0){
		ret = gst_app_src_end_of_stream(app->src);
		g_debug("eos returned %d at %d\n", ret, __LINE__);
		return FALSE;
	}

	buffer = gst_buffer_new();
	GST_BUFFER_MALLOCDATA(buffer) = ptr;
	GST_BUFFER_SIZE(buffer) = size;
	GST_BUFFER_DATA(buffer) = GST_BUFFER_MALLOCDATA(buffer);

	ret = gst_app_src_push_buffer(app->src, buffer);

	if(ret !=  GST_FLOW_OK){
		g_debug("push buffer returned %d for %d bytes \n", ret, size);
		return FALSE;
	}

	if(size != BUFF_SIZE){
		ret = gst_app_src_end_of_stream(app->src);
		g_debug("eos returned %d at %d\n", ret, __LINE__);
		return FALSE;
	}

	return TRUE;
#endif
}

static void start_feed (GstElement * pipeline, guint size, gst_app_t *app)
{
	if (app->sourceid == 0) {
		GST_DEBUG ("start feeding");
		app->sourceid = g_idle_add ((GSourceFunc) read_data, app);
	}
}

static void stop_feed (GstElement * pipeline, gst_app_t *app)
{
	if (app->sourceid != 0) {
		GST_DEBUG ("stop feeding");
		g_source_remove (app->sourceid);
		app->sourceid = 0;
	}
}

static void on_pad_added(GstElement *element, GstPad *pad)
{
	GstCaps *caps;
	GstStructure *str;
	gchar *name;
	GstPad *ffmpegsink;
	GstPadLinkReturn ret;

	g_debug("pad added");

	caps = gst_pad_get_caps(pad);
	str = gst_caps_get_structure(caps, 0);

	g_assert(str);

	name = (gchar*)gst_structure_get_name(str);

	g_debug("pad name %s", name);

	if(g_strrstr(name, "video")){

		ffmpegsink = gst_element_get_pad(gst_app.ffmpeg, "sink");
		g_assert(ffmpegsink);
		ret = gst_pad_link(pad, ffmpegsink);
		g_debug("pad_link returned %d\n", ret);
		gst_object_unref(ffmpegsink);
	}
	gst_caps_unref(caps);
}

static gboolean bus_callback(GstBus *bus, GstMessage *message, gpointer *ptr)
{
	gst_app_t *app = (gst_app_t*)ptr;

	switch(GST_MESSAGE_TYPE(message)){

	case GST_MESSAGE_ERROR:{
		gchar *debug;
		GError *err;

		gst_message_parse_error(message, &err, &debug);
		g_print("Error %s\n", err->message);
		g_error_free(err);
		g_free(debug);
		g_main_loop_quit(app->loop);
						   }
						   break;

	case GST_MESSAGE_EOS:
		g_print("End of stream\n");
		g_main_loop_quit(app->loop);
		break;

	default:
		g_print("got message %s\n", \
			gst_message_type_get_name (GST_MESSAGE_TYPE (message)));
		break;
	}

	return TRUE;
}

int _tmain(int argc, _TCHAR* argv[])
{
	gst_app_t *app = &gst_app;
	GstBus *bus;
	GstStateChangeReturn state_ret;

#if USE_LOG
	m_pLogFile = fopen("log.avi", "wb");
#endif

	int nPort = 9070;
	if(argc == 2){
		nPort = _ttoi(argv[1]);
	}

	InitializeCriticalSection(&m_lock);

	DWORD dwThreadId = 0;
	HANDLE connectthreadhandle = (HANDLE)_beginthreadex(NULL,0,connectThread,(LPVOID)nPort,0,NULL);
	if (connectthreadhandle == NULL)
	{
		cout << "Create connect thread failed!!" << endl;
		return -1;
	}

	app->file = fopen("test.3gp", "rb");

	//g_assert(app->file);

	gst_init(NULL, NULL);

	app->pipeline = (GstPipeline*)gst_pipeline_new("mypipeline");
	bus = gst_pipeline_get_bus(app->pipeline);
	gst_bus_add_watch(bus, (GstBusFunc)bus_callback, app);
	gst_object_unref(bus);

	/*
	app->src = (GstAppSrc*)gst_element_factory_make("appsrc", "mysrc");
	app->decoder = gst_element_factory_make("decodebin", "mydecoder");
	app->ffmpeg = gst_element_factory_make("ffmpegcolorspace", "myffmpeg");
	app->xvimagesink = gst_element_factory_make("xvimagesink", "myvsink");
	*/
	app->src = (GstAppSrc *)gst_element_factory_make("appsrc","app");
	//app->src = (GstAppSrc *)gst_element_factory_make("filesrc","app");
	//app->typefind = gst_element_factory_make("typefind","find");
	app->decoder = gst_element_factory_make("decodebin2","decoder");
	app->ffmpeg= gst_element_factory_make("ffmpegcolorspace","ffmpeg");
	app->videoscale = gst_element_factory_make("videoscale","scaler");
	app->sink = gst_element_factory_make ("autovideosink", "sink");


	g_assert(app->src);
	//g_assert( app->typefind );
	g_assert(app->decoder);
	g_assert(app->ffmpeg);
	g_assert(app->videoscale);
	g_assert(app->sink);

	g_signal_connect(app->src, "need-data", G_CALLBACK(start_feed), app);
	g_signal_connect(app->src, "enough-data", G_CALLBACK(stop_feed), app);

#ifdef MIRROR_TEST
	g_object_set(G_OBJECT(app->src), "do-timestamp", TRUE, NULL);
	//g_object_set(G_OBJECT(app->src), "is-live", TRUE, NULL);
	//g_object_set(G_OBJECT(app->src), "max-bytes",1024*1, NULL);
	g_object_set(G_OBJECT(app->src), "min-latency", 0, NULL);
	g_object_set(G_OBJECT(app->src), "max-latency", 20, NULL);
//	g_object_set(G_OBJECT(app->src), "num-buffers", 100, NULL);
#endif
	//g_object_set (G_OBJECT (app->src), "location", "1.avi", NULL);

	g_signal_connect (app->decoder, "pad-added", G_CALLBACK (on_pad_added), app->ffmpeg);


	//g_signal_connect(app->decoder, "pad-added", G_CALLBACK(on_pad_added), app->decoder);

	/* Build the pipeline */
	gst_bin_add_many (GST_BIN (app->pipeline), (GstElement *)app->src, app->decoder,app->ffmpeg, app->videoscale,app->sink, NULL);

	gst_element_link( (GstElement *)app->src, app->decoder );
	if (gst_element_link_many (app->ffmpeg,app->videoscale ,app->sink, NULL) != TRUE) {
		g_printerr ("Elements could not be linked.\n");
		gst_object_unref (app->pipeline);
		return -1;
	}

	state_ret = gst_element_set_state((GstElement*)app->pipeline, GST_STATE_PLAYING);
	g_warning("set state returned %d\n", state_ret);

	app->loop = g_main_loop_new(NULL, FALSE);
	printf("Running main loop\n");
	g_main_loop_run(app->loop);

	state_ret = gst_element_set_state((GstElement*)app->pipeline, GST_STATE_NULL);
	g_warning("set state null returned %d\n", state_ret);

	return 0;
}
