#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavformat/avio.h>
#include <libswscale/swscale.h>
#include <sys/socket.h>
#include <netinet/in.h>

#include <pthread.h>

#include "log.h"
#include "node.h"

extern volatile int g_bIsStop;
extern pthread_mutex_t g_rgb_queue_lock;
extern struct list_head g_rgb_queue;

extern int sendSocket;
extern int srvConnected;

static char dbgMsg[256];

int get_list_cnt(struct list_head* phead)
{
	int nCnt = 0;
	struct RgbNode* node = NULL;
	list_for_each_entry(node, phead, q_next)
	{
		nCnt++;
	}

	return nCnt;
}

void* encode_thread(void *arg)
{
	char *destIp = (char*)arg;

	AVCodec *codec = NULL;
	AVCodecContext *context= NULL;
	AVFrame *picture;

	int encode_buf_size, encode_size;
	unsigned char* pYuvBuffer = NULL;
	unsigned char *encode_buf;

	int isSaveVideo = 0;
	FILE* pVideoFile = NULL;

	LOGI("Enter encode_thread");

    encode_buf_size = 100000;
    encode_buf = malloc(encode_buf_size);
    if(encode_buf == NULL)
	{
		fatal_error("Not enough memory");
	}

	init_rgb2yuv();
	init_socket();

    while(!g_bIsStop)
    {
    	//pthread_mutex_lock(&g_rgb_queue_lock);
    	if (list_empty(&g_rgb_queue))
    	{
    		//pthread_mutex_unlock(&g_rgb_queue_lock);
    		usleep(10000);
    		continue;
    	}

  /*  	int nCnt = get_list_cnt(&g_rgb_queue);

    	while( nCnt > MAX_DATA_IN_QUEUE )
    	{
        	struct RgbNode* pNode = list_entry(struct RgbNode, g_rgb_queue.next, q_next);
        	list_del(&pNode->q_next);
        	LOGI("Remove capture data");
        	nCnt--;
        	free(pNode->pdata);
        	free(pNode);
    	}*/
    	//int nCnt = get_list_cnt(&g_rgb_queue);
		//sprintf(dbgMsg,"list cnt = %d", nCnt);
		//LOGI(dbgMsg);

    	struct RgbNode* pNode = list_entry(struct RgbNode, g_rgb_queue.next, q_next);
    	list_del(&pNode->q_next);

    	//pthread_mutex_unlock(&g_rgb_queue_lock);

        if(pYuvBuffer == NULL)
        {
        	pYuvBuffer = malloc(pNode->width * pNode->height * 3 / 2);
        	if(pYuvBuffer == NULL)
        	{
        		fatal_error("Not enough memory");
        	}
        }

        // RGB TO YUV
        RGB2YUV(pNode->width, pNode->height, pNode->pdata, pYuvBuffer,
        		pYuvBuffer+pNode->width*pNode->height,
        		pYuvBuffer+pNode->width*pNode->height+pNode->width*pNode->height/4,
        		pNode->width, 0);

        if (codec == NULL)
        {
            // Codec init
            av_register_all();

        	/* find the mpeg1 video encoder */
        	codec = avcodec_find_encoder(ENCODE_ID);

        	if (!codec) {
        		fatal_error("codec not found\n");
        	}

        	context = avcodec_alloc_context();

        	/* put sample parameters */
        	context->bit_rate = (pNode->width/16)*(pNode->height/16)*2*1000;
        	/* resolution must be a multiple of two */
        	context->width = pNode->width;
        	context->height = pNode->height;
        	/* frames per second */
/*        	context->time_base.num = 1001;
        	context->time_base.den = 30000;*/
        	context->time_base.num = 1;
        	context->time_base.den = STREAM_FRAME_RATE;

        	context->gop_size = STREAM_FRAME_RATE/5; /* emit one intra frame every ten frames */
        	context->max_b_frames= 0;
        	context->pix_fmt = PIX_FMT_YUV420P;
        	context->mpeg_quant = 1;

        	context->codec_type = AVMEDIA_TYPE_VIDEO;
        	context->codec_id = ENCODE_ID;

        	/* open it */
        	if (avcodec_open(context, codec) < 0) {
        		fatal_error("could not open codec\n");
        	}

        	picture= avcodec_alloc_frame();
        	picture->pts = AV_NOPTS_VALUE;
        	picture->data[0] = pYuvBuffer;
        	picture->data[1] = picture->data[0] + pNode->width*pNode->height;
        	picture->data[2] = picture->data[1] + pNode->width*pNode->height / 4;
        	picture->linesize[0] = pNode->width;
        	picture->linesize[1] = pNode->width / 2;
        	picture->linesize[2] = pNode->width / 2;
        }

        // Encode
        encode_size = avcodec_encode_video(context, encode_buf, encode_buf_size, picture);
/*
        char szDbgMsg[1024] = {0};
        sprintf(szDbgMsg, "encode_size: %d", encode_size);

        LOGI(szDbgMsg);
*/
        // Send it
        int i = 0;
        for(i=0; i<1; i++)
        {
        	//encode_size = avcodec_encode_video(context, encode_buf, encode_buf_size, picture);
        	send_buffer(encode_buf, encode_size);
        	//usleep(3000);
        }

        // Save it
        if (isSaveVideo)
        {
        	pVideoFile = SaveVideo(encode_buf, encode_size, pVideoFile, 1, 0);
        }

    	free(pNode->pdata);
    	free(pNode);

    	//picture->pts++;
    }

    if (isSaveVideo)
    {
		for(; encode_size; ) {
			encode_size = avcodec_encode_video(context, encode_buf, encode_buf_size, NULL);
			pVideoFile = SaveVideo(encode_buf, encode_size, pVideoFile, 1, 0);
		}

		pVideoFile = SaveVideo(encode_buf, 0, pVideoFile, 1, 1);
    }

    if( pYuvBuffer != NULL)
    {
    	free(pYuvBuffer);
    	pYuvBuffer = NULL;
    }

    if( encode_buf != NULL )
    {
    	free(encode_buf);
    	encode_buf = NULL;
    }


    //char stopBuf[] = {"S3STOP"};
    //send_buffer(stopBuf, sizeof(stopBuf));
//	close(sendSocket);
//	srvConnected = 0;

	LOGI("Exit encode_thread");
}
