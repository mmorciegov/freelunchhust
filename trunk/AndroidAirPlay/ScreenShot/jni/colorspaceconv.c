#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include "log.h"
static void convert1555to32(int width, int height,
		unsigned char *inbuffer,
		unsigned char *outbuffer)
{
	unsigned int i;

	for (i=0; i < (unsigned int) height*width*2; i+=2)
	{
		/* BLUE  = 0 */
		outbuffer[(i<<1)+0] = (inbuffer[i+1] & 0x7C) << 1;
		/* GREEN = 1 */
		outbuffer[(i<<1)+1] = (((inbuffer[i+1] & 0x3) << 3) |
				((inbuffer[i] & 0xE0) >> 5)) << 3;
		/* RED   = 2 */
		outbuffer[(i<<1)+2] = (inbuffer[i] & 0x1f) << 3;
		/* ALPHA = 3 */
		outbuffer[(i<<1)+3] = '\0';
	}
}

static void convert1555to24(int width, int height,
		unsigned char *inbuffer,
		unsigned char *outbuffer)
{
	unsigned int i;
	unsigned int index = 0;

	for (i=0; i < (unsigned int) height*width*2; i+=2)
	{
		/* BLUE  = 0 */
		outbuffer[index++] = (inbuffer[i+1] & 0x7C) << 1;
		/* GREEN = 1 */
		outbuffer[index++] = (((inbuffer[i+1] & 0x3) << 3) |
				((inbuffer[i] & 0xE0) >> 5)) << 3;
		/* RED   = 2 */
		outbuffer[index++] = (inbuffer[i] & 0x1f) << 3;
	}
}

static void convert565to32(int width, int height,
		unsigned char *inbuffer,
		unsigned char *outbuffer)
{
	unsigned int i;

	for (i=0; i < (unsigned int) height*width*2; i+=2)
	{
		/* BLUE  = 0 */
		outbuffer[(i<<1)+0] = (inbuffer[i] & 0x1f) << 3;
		/* GREEN = 1 */
		outbuffer[(i<<1)+1] = (((inbuffer[i+1] & 0x7) << 3) |
				(inbuffer[i] & 0xE0) >> 5) << 2;
		/* RED   = 2 */
		outbuffer[(i<<1)+2] = (inbuffer[i+1] & 0xF8);
		/* ALPHA = 3 */
		outbuffer[(i<<1)+3] = '\0';
	}
}

static void convert565to24(int width, int height,
		unsigned char *inbuffer,
		unsigned char *outbuffer)
{
	unsigned int i;
	unsigned int index = 0;

	for (i=0; i < (unsigned int) height*width*2; i+=2)
	{
		/* BLUE  = 0 */
		outbuffer[index++] = (inbuffer[i] & 0x1f) << 3;
		/* GREEN = 1 */
		outbuffer[index++] = (((inbuffer[i+1] & 0x7) << 3) |
				(inbuffer[i] & 0xE0) >> 5) << 2;
		/* RED   = 2 */
		outbuffer[index++] = (inbuffer[i+1] & 0xF8);
	}
}

static void convert888to32(int width, int height,
		unsigned char *inbuffer,
		unsigned char *outbuffer)
{
	unsigned int i;

	for (i=0; i < (unsigned int) height*width; i++)
	{
		/* BLUE  = 0 */
		outbuffer[(i<<2)+0] = inbuffer[i*3+0];
		/* GREEN = 1 */
		outbuffer[(i<<2)+1] = inbuffer[i*3+1];
		/* RED   = 2 */
		outbuffer[(i<<2)+2] = inbuffer[i*3+2];
		/* ALPHA */
		outbuffer[(i<<2)+3] = '\0';
	}
}

static void convert888to24(int width, int height,
		unsigned char *inbuffer,
		unsigned char *outbuffer)
{
	unsigned int i;

	memcpy(outbuffer, inbuffer, width*height*3);
}

static void convert8888to24(int width, int height,
		unsigned char *inbuffer,
		unsigned char *outbuffer,
		int xoffset, int yoffset, int stride, int isBGR)
{
	int i,j;
	unsigned int index = 0;

	for (i=0; i<height; i++)
	{
		for (j=0; j<width; j++)
		{
			if( isBGR == 1 )
			{
				outbuffer[index++] = inbuffer[(yoffset+i)*stride*4 + xoffset*4 + j*4 + 2];
				outbuffer[index++] = inbuffer[(yoffset+i)*stride*4 + xoffset*4 + j*4 + 1];
				outbuffer[index++] = inbuffer[(yoffset+i)*stride*4 + xoffset*4 + j*4 + 0];				

			}
			else
			{
				outbuffer[index++] = inbuffer[(yoffset+i)*stride*4 + xoffset*4 + j*4 + 0];
				outbuffer[index++] = inbuffer[(yoffset+i)*stride*4 + xoffset*4 + j*4 + 1];
				outbuffer[index++] = inbuffer[(yoffset+i)*stride*4 + xoffset*4 + j*4 + 2];	
			}

		}
	}
}

void convert_raw_to_rgb(unsigned char *inbuffer, unsigned char *outbuffer,
		int width, int height, int bits, int isAlpha, int xoffset, int yoffset, int stride, int isBGR)
{
	/*	
	char szDgb[1024] = {0};
	static int index = 0;
	sprintf(szDgb, "index: %4d. convert_raw_to_rgb isAlpha:%d bits %d", index++,  isAlpha, bits);
	LOGI(szDgb);
	 */
	switch(isAlpha)
	{
	case 0:
		switch(bits)
		{
		case 15:
			convert1555to24(width, height, inbuffer, outbuffer);
			break;
		case 16:
			convert565to24(width, height, inbuffer, outbuffer);
			break;
		case 24:
			convert888to24(width, height, inbuffer, outbuffer);
			break;
		case 32:
			convert8888to24(width, height, inbuffer, outbuffer, xoffset, yoffset, stride, isBGR);
			break;
		default:
			fatal_error("cur bits per pixel are not supported!");
		}
		break;
		case 1:
		default:
			switch(bits)
			{
			case 15:
				convert1555to32(width, height, inbuffer, outbuffer);
				break;
			case 16:
				convert565to32(width, height, inbuffer, outbuffer);
				break;
			case 24:
				convert888to32(width, height, inbuffer, outbuffer);
				break;
			case 32:
				/* No conversion needed */
				memcpy(outbuffer, inbuffer, width*height*4);
				break;
			default:
				fatal_error("cur bits per pixel are not supported!");
			}
			break;
	}
}
