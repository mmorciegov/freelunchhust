#ifndef _NODE_H_
#define _NODE_H_

#include "list.h"

#define STREAM_FRAME_RATE 20 /* 1 images/s */
#define ENCODE_ID CODEC_ID_MPEG4
#define MAX_DATA_IN_QUEUE	STREAM_FRAME_RATE

#define PACK_SIZE (8*1024)

enum
{
	STOP = 0,
	PLAY,
	PAUSE,
};

struct RgbNode
{
	int width;
	int height;
	unsigned char* pdata;
	struct list_head q_next;
};

struct YuvNode
{
	int width;
	int height;
	unsigned char* pdata;
	struct list_head q_next;
};

#endif

