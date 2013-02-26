#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/ioctl.h>

#include <pthread.h>
#include "log.h"
#include "node.h"

#include "com_example_screenshot_MainActivity.h"

extern void* video_thread(void *arg);
extern void* capture_thread(void *arg);
extern void* encode_thread(void *arg);

struct list_head 	g_rgb_queue;
pthread_mutex_t 	g_rgb_queue_lock;
volatile int g_bIsStop = 0;
volatile int g_bPlayState = STOP;
char g_destIP[64] = {0};
char g_videoname[256] = {0};

void fatal_error(char *message)
{
	LOGE(message);
    exit(EXIT_FAILURE);
}

JNIEXPORT jboolean JNICALL Java_com_example_screenshot_ScreenshotService_stopScreenShot
  (JNIEnv *env, jobject obj)
{
	g_bIsStop = 1;
	return 1;
}

JNIEXPORT jboolean JNICALL Java_com_example_screenshot_ScreenshotService_startScreenShot
  (JNIEnv *env, jobject obj, jstring ip)
{
	g_bIsStop = 0;

	memset(g_destIP, 0, sizeof(g_destIP));
	const char *str = (*env)->GetStringUTFChars(env, ip, 0);
	strcpy(g_destIP, str);
	(*env)->ReleaseStringUTFChars(env, ip, str);

	pthread_mutex_init(&g_rgb_queue_lock, NULL);
	list_init_head(&g_rgb_queue);

	LOGI("Enter startScreenShot.");

	pthread_t capture_tid, encode_tid;
	int err = 0;

	err = pthread_create(&capture_tid, NULL, capture_thread, NULL);
	err = pthread_create(&encode_tid, NULL, encode_thread, NULL);

	pthread_join(capture_tid, NULL);
	pthread_join(encode_tid, NULL);

	while(1)
	{
		if (list_empty(&g_rgb_queue))
		{
			break;
		}

    	struct RgbNode* pNode = list_entry(struct RgbNode, g_rgb_queue.next, q_next);
    	list_del(&pNode->q_next);

    	free(pNode->pdata);
    	free(pNode);

    	LOGI("Remove list data.");
	}

    LOGI("Exit startScreenShot.");

	return 1;
}


JNIEXPORT jboolean JNICALL Java_com_example_screenshot_MainActivity_isFbAvaliable(JNIEnv *env, jobject obj)
{
	int fd;

	char* device = "/dev/graphics/fb0";

	/* now open framebuffer device */
	if((fd=open(device, O_RDONLY)) < 0)
	{
		return 0;
	}
	else
	{
		(void) close(fd);
	}

	return 1;
}

JNIEXPORT jboolean JNICALL Java_com_example_screenshot_MainActivity_playVideo(JNIEnv *env, jobject obj, jstring ip, jstring filename, jint status)
{
	char *str = (*env)->GetStringUTFChars(env, ip, 0);
	strcpy(g_destIP, str);
	(*env)->ReleaseStringUTFChars(env, ip, str);

	str = (*env)->GetStringUTFChars(env, filename, 0);
	strcpy(g_videoname, str);
	(*env)->ReleaseStringUTFChars(env, filename, str);

	pthread_t video_tid;

	switch(status)
	{
	case STOP:
		g_bPlayState = status;
		break;
	case PAUSE:
		if (g_bPlayState == PLAY)
		{
			g_bPlayState = status;
		}
		break;
	case PLAY:
		if (g_bPlayState == PLAY)
		{

		}
		else if (g_bPlayState == STOP)
		{
			g_bPlayState = status;
			pthread_create(&video_tid, NULL, video_thread, NULL);
		}
		else if (g_bPlayState == PAUSE)
		{
			g_bPlayState = status;
		}
		break;
	default:
		break;
	}

	return 1;
}

