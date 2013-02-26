#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include <sys/socket.h>
#include <netinet/in.h>

#include "node.h"
#include "log.h"

#define USE_TCP 0

extern char g_destIP[];
int sendSocket;
int srvConnected = 0;
struct sockaddr_in serv_addr;

void init_socket()
{
	LOGI("init_socket");

	if (srvConnected == 1)
	{
		return;
	}

#if USE_TCP
	sendSocket = socket(AF_INET,SOCK_STREAM,0);
#else
	sendSocket = socket(AF_INET,SOCK_DGRAM,0);
#endif
	if (sendSocket == -1)
	{
		LOGI("sendSocket == -1");
	    return;
	}

	serv_addr.sin_family=AF_INET;
	serv_addr.sin_port = htons(9070);
	serv_addr.sin_addr.s_addr = inet_addr(g_destIP);
	bzero(&(serv_addr.sin_zero),8);

#if USE_TCP
	LOGI("connect service");
	if (connect(sendSocket, (struct sockaddr *) &serv_addr, sizeof(struct sockaddr)) == -1)
	{
		LOGI("connect failed");
		return;
	}
#endif

	srvConnected = 1;

	LOGI("srvConnected");
}

void send_buffer(char *buf, int buf_size)
{
	if( srvConnected )
	{
//		sprintf(dbgMsg,"send buffer, buf_size is %d", buf_size);
//		LOGI(dbgMsg);
#if USE_TCP
		int sendbytes = send(sendSocket,buf,buf_size,0);
#else
		int j = 0;
		for(j=0; j<buf_size/PACK_SIZE; j++)
		{
			int sendbytes = sendto (sendSocket, buf+j*PACK_SIZE, PACK_SIZE, 0,
					(struct sockaddr*)&serv_addr, sizeof (struct sockaddr_in));
			usleep(1000);
		}

		if (buf_size%PACK_SIZE != 0)
		{
			int sendbytes = sendto (sendSocket, buf+j*PACK_SIZE, buf_size%PACK_SIZE, 0,
					(struct sockaddr*)&serv_addr, sizeof (struct sockaddr_in));
		}
#endif
//		sprintf(dbgMsg,"send bytes = %d", sendbytes);
//		LOGI(dbgMsg);
	}
}
