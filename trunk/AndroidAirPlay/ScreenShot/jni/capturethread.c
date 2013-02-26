#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/ioctl.h>

#include <getopt.h>
#include <sys/vt.h>   /* to handle vt changing */
//#include <png.h>      /* PNG lib */
#include <linux/fb.h> /* to handle framebuffer ioctls */

#include <pthread.h>

#include "log.h"
#include "node.h"

extern volatile int g_bIsStop;
extern pthread_mutex_t g_rgb_queue_lock;
extern struct list_head g_rgb_queue;

static void chvt(int num)
{
	int fd;

	if(-1 == (fd = open("/dev/console", O_RDWR)))
	{
		fatal_error("Cannot open /dev/console");
	}

	if (ioctl(fd, VT_ACTIVATE, num) != 0)
	{
		fatal_error("ioctl VT_ACTIVATE");
	}

	if (ioctl(fd, VT_WAITACTIVE, num) != 0)
	{
		fatal_error("ioctl VT_WAITACTIVE");
	}

	(void) close(fd);
}

static unsigned short int change_to_vt(unsigned short int vt_num)
{
	int fd;
	unsigned short int old_vt;
	struct vt_stat vt_info;

	memset(&vt_info, 0, sizeof(struct vt_stat));

	if(-1 == (fd=open("/dev/console", O_RDONLY)))
	{
		fatal_error("Couldn't open /dev/console");
	}

	if (ioctl(fd, VT_GETSTATE,  &vt_info) != 0)
	{
		fatal_error("ioctl VT_GETSTATE");
	}

	(void) close (fd);

	old_vt = vt_info.v_active;

	chvt((int) vt_num); /* go there for information */

	return old_vt;
}

static void get_framebufferdata(char *device, struct fb_var_screeninfo *fb_varinfo_p)
{
	int fd;

	/* now open framebuffer device */
	int index = 0;
	while((-1 == (fd=open(device, O_RDONLY))) && (index++ > 100))
	{
		fatal_error ("Error: Couldn't open framebuffer device.");
	}


	if (ioctl(fd, FBIOGET_VSCREENINFO, fb_varinfo_p) != 0)
	{
		fatal_error("ioctl FBIOGET_VSCREENINFO");
	}

	(void) close(fd);
}

static void read_framebuffer(char *device, size_t bytes, unsigned char *buf_p, int offset)
{
	int fd = 0;

	int index = 0;
	while((-1 == (fd=open(device, O_RDONLY))) && (index++ > 100))
	{
		fatal_error ("Error: Couldn't open framebuffer device.");
	}

	//fseek(fd, 0, SEEK_SET);
	if (buf_p == NULL || read(fd, buf_p, bytes) != (ssize_t) bytes)
	{
		fatal_error("Error: Not enough memory or data\n");
	}

	(void) close(fd);
}

void* capture_thread(void *arg)
{
	int old_vt = -1;
	int vt_num = 1;

	char* device = "/dev/graphics/fb0";
	struct fb_var_screeninfo fb_varinfo;

	size_t buf_size;
	unsigned char *buf_p;

	char debugStr[1024];
	static int nFileIndex = 1;

	int width = 0;
	int height = 0;

	LOGI("Enter capture_thread");

	//	old_vt = (int) change_to_vt((unsigned short int) vt_num);
	//	(void) sleep(3);

	//device = getenv("FRAMEBUFFER");
	get_framebufferdata(device, &fb_varinfo);

	sprintf(debugStr, "bitdepth:%d, width:%d, height:%d xres_virtual:%d yres_virtual:%d xoffset:%d  yoffset:%d  bits_per_pixel:%d grayscale:%d"
			" red:%d %d green:%d %d  blue:%d %d\n"
			" left_margin: %d right_margin:%d, upper_margin:%d lower_margin:%d"
			"hsync_len:%d, vsync_len:%d",
			fb_varinfo.bits_per_pixel, fb_varinfo.xres, fb_varinfo.yres,
			fb_varinfo.xres_virtual, fb_varinfo.yres_virtual,
			fb_varinfo.xoffset, fb_varinfo.yoffset,
			fb_varinfo.bits_per_pixel, fb_varinfo.grayscale,
			fb_varinfo.red.offset,  fb_varinfo.red.length,
			fb_varinfo.green.offset,  fb_varinfo.green.length,
			fb_varinfo.blue.offset,  fb_varinfo.blue.length,
			fb_varinfo.left_margin, fb_varinfo.right_margin,
			fb_varinfo.upper_margin, fb_varinfo.lower_margin,
			fb_varinfo.hsync_len, fb_varinfo.vsync_len
	);
	LOGI(debugStr);

	//buf_size = fb_varinfo.xres * fb_varinfo.yres * (((unsigned int) fb_varinfo.bits_per_pixel + 7) >> 3);
	buf_size = fb_varinfo.xres_virtual * fb_varinfo.yres_virtual * (((unsigned int) fb_varinfo.bits_per_pixel + 7) >> 3);

	width = fb_varinfo.xres_virtual/(fb_varinfo.xres_virtual/fb_varinfo.xres);
	height = fb_varinfo.yres_virtual/(fb_varinfo.yres_virtual/fb_varinfo.yres);

	buf_p = malloc(buf_size);
	int isBGR = 0;
	if( fb_varinfo.red.offset == 24 )
	{
		LOGI("B G R will be set!");
		isBGR = 1;
	}
	else
	{
		LOGI("R G B will be set!");
	}
	if(buf_p == NULL)
	{
		fatal_error("Not enough memory");
	}

	int seekoff = (((unsigned int) fb_varinfo.bits_per_pixel + 7) >> 3) * fb_varinfo.xres_virtual*fb_varinfo.yoffset;
	seekoff = 0;
	while(!g_bIsStop)
	{
		//memset(buf_p, 0, buf_size);
		//read_framebuffer(s_frmbufferfd, buf_size, buf_p, seekoff);
		read_framebuffer(device, buf_size, buf_p, seekoff);

		// Raw data to RGB
		unsigned char* pRgbBuffer = malloc(width * height * 3);
		if(pRgbBuffer == NULL)
		{
			fatal_error("Not enough memory");
		}

		convert_raw_to_rgb(buf_p, pRgbBuffer, width, height, fb_varinfo.bits_per_pixel, 0, fb_varinfo.xoffset, fb_varinfo.yoffset, fb_varinfo.xres_virtual,isBGR);

		struct RgbNode* pNode = malloc(sizeof(struct RgbNode));
		pNode->width = width;
		pNode->height = height;
		pNode->pdata = pRgbBuffer;

        //pthread_mutex_lock(&g_rgb_queue_lock);
		list_add_tail(&pNode->q_next, &g_rgb_queue);
        //pthread_mutex_unlock(&g_rgb_queue_lock);

		// Debug
		//        	SaveImage(pRgbBuffer, width, height, nFileIndex++);

		usleep(1000000/STREAM_FRAME_RATE);
	}

	(void) free(buf_p);

	//(void) change_to_vt((unsigned short int) old_vt);

	LOGI("Exit capture_thread");
}
