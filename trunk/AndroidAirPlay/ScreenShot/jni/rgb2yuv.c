#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

static int32_t RGBYUV02570[256], RGBYUV05040[256], RGBYUV00980[256];
static int32_t RGBYUV01480[256], RGBYUV02910[256], RGBYUV04390[256];
static int32_t RGBYUV03680[256], RGBYUV00710[256];

void init_rgb2yuv(void)
{
	int i;
	for (i = 0; i < 256; i++)
		RGBYUV02570[i] = (int32_t) (0.2570 * i * 65536);
	for (i = 0; i < 256; i++)
		RGBYUV05040[i] = (int32_t) (0.5040 * i * 65536);
	for (i = 0; i < 256; i++)
		RGBYUV00980[i] = (int32_t) (0.0980 * i * 65536);
	for (i = 0; i < 256; i++)
		RGBYUV01480[i] = -(int32_t) (0.1480 * i * 65536);
	for (i = 0; i < 256; i++)
		RGBYUV02910[i] = -(int32_t) (0.2910 * i * 65536);
	for (i = 0; i < 256; i++)
		RGBYUV04390[i] = (int32_t) (0.4390 * i * 65536);
	for (i = 0; i < 256; i++)
		RGBYUV03680[i] = -(int32_t) (0.3680 * i * 65536);
	for (i = 0; i < 256; i++)
		RGBYUV00710[i] = -(int32_t) (0.0710 * i * 65536);
}

/************************************************************************
*
*  int RGB2YUV (int x_dim, int y_dim, void *bmp, YUV *yuv)
*
*	Purpose :	It takes a 24-bit RGB bitmap and convert it into
*				YUV (4:1:1) format
*
*  Input :		x_dim	the x dimension of the bitmap
*				y_dim	the y dimension of the bitmap
*				bmp		pointer to the buffer of the bitmap
*				yuv		pointer to the YUV structure
*
*  Output :	0		OK
*				1		wrong dimension
*				2		memory allocation error
*
*	Side Effect :
*				None
*
*	Date :		2000-09-28
*
*  Contacts:
*
*  Adam Li
*
*  DivX Advance Research Center <darc@projectmayo.com>
*
************************************************************************/

int RGB2YUV(int x_dim, int y_dim, uint8_t *bmp, uint8_t *y_out,
			uint8_t *u_out, uint8_t *v_out, int x_stride, int flip)
{
	int i, j, size;
	uint8_t *b;
	uint8_t *y, *u, *v;
	uint8_t *y_buffer, *sub_u_buf, *sub_v_buf;

	// check to see if x_dim and y_dim are divisible by 2
	if ((x_dim % 2) || (y_dim % 2))
		return 1;

	size = x_dim * y_dim;
	y_buffer = (uint8_t *) y_out;
	sub_u_buf = (uint8_t *) u_out;
	sub_v_buf = (uint8_t *) v_out;
	b = (uint8_t *) bmp;
	y = y_buffer;
	u = sub_u_buf;
	v = sub_v_buf;

	if (flip)
	{
		for (j = 0; j < y_dim; j++)
		{
			y = y_buffer + (y_dim - j - 1) * x_stride;
			u = sub_u_buf + (y_dim / 2 - j / 2 - 1) * x_stride / 2;
			v = sub_v_buf + (y_dim / 2 - j / 2 - 1) * x_stride / 2;
			if (!(j % 2))
			{
				for (i = 0; i < x_dim / 2; i++)
				{
					y[0] = (uint8_t)
						((RGBYUV02570
						[b[2]] + RGBYUV05040[b[1]] +
						RGBYUV00980[b[0]] + 0x100000) >> 16);

					y[1] =
						(uint8_t) (
						(RGBYUV02570[b[5]] +
						RGBYUV05040[b[4]] +
						RGBYUV00980[b[3]] + 0x100000) >> 16);

					y += 2;

					*u =
						(uint8_t) (
						(RGBYUV01480[b[5]] +
						RGBYUV02910[b[4]] +
						RGBYUV04390[b[3]] +
						0x800000) >> 16);

					*v =
						(uint8_t) (
						(RGBYUV04390[b[5]] +
						RGBYUV03680[b[4]] +
						RGBYUV00710[b[3]] + 0x800000) >> 16);

					u++;
					v++;
					b += 6;
				}
			}
			else
				for (i = 0; i < x_dim; i++)
				{
					*y = (uint8_t)
						((RGBYUV02570
						[b[2]] + RGBYUV05040[b[1]] +
						RGBYUV00980[b[0]] + 0x100000) >> 16);

					y++;
					b += 3;
				}
		}
	}
	else
	{
		for (j = 0; j < y_dim; j++)
		{
			y = y_buffer + j * x_stride;
			u = sub_u_buf + j / 2 * x_stride / 2;
			v = sub_v_buf + j / 2 * x_stride / 2;
			if (!(j % 2))
			{
				for (i = 0; i < x_dim / 2; i++)
				{
					y[0] = (uint8_t)
						((RGBYUV02570
						[b[2]] + RGBYUV05040[b[1]] +
						RGBYUV00980[b[0]] + 0x100000) >> 16);

					y[1] =
						(uint8_t) (
						(RGBYUV02570[b[5]] +
						RGBYUV05040[b[4]] +
						RGBYUV00980[b[3]] + 0x100000) >> 16);

					y += 2;

					*u =
						(uint8_t) (
						(RGBYUV01480[b[5]] +
						RGBYUV02910[b[4]] +
						RGBYUV04390[b[3]] + 0x800000) >> 16);

					*v =
						(uint8_t) (
						(RGBYUV04390[b[5]] +
						RGBYUV03680[b[4]] +
						RGBYUV00710[b[3]] + 0x800000) >> 16);
					u++;
					v++;
					b += 6;
				}
			}
			else
				for (i = 0; i < x_dim; i++)
				{
					*y = (uint8_t)
						((RGBYUV02570
						[b[2]] + RGBYUV05040[b[1]] +
						RGBYUV00980[b[0]] + 0x100000) >> 16);

					y++;
					b += 3;
				}
		}
	}

	return 0;
}

