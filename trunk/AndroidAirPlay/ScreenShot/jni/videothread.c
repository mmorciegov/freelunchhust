#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include "node.h"
#include "log.h"

extern char g_videoname[];
extern volatile int g_bPlayState;
extern int sendSocket;
extern int srvConnected;

void* video_thread(void *arg)
{
	FILE *pfile = NULL;
	pfile = fopen(g_videoname, "rb");
	if (pfile == NULL)
	{
		return;
	}

	char pBuffer[PACK_SIZE];
	int readlen = 0;

	init_socket();

	while(1)
	{
		switch(g_bPlayState)
		{
		case STOP:
			fclose(pfile);
			return;

		case PAUSE:
			usleep(1000000);
			continue;
			break;

		case PLAY:
		default:
			break;
		}

		readlen = fread(pBuffer, 1, PACK_SIZE, pfile);
		if (readlen > 0)
		{
			send_buffer(pBuffer, readlen);
		}
		if (readlen < PACK_SIZE)
		{
			break;
		}
		usleep(15000);
	}

	g_bPlayState = STOP;

	fclose(pfile);
}
