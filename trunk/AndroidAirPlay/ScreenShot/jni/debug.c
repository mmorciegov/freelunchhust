#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

void SaveImage(unsigned char *pFrame, int width, int height, int nFileIndex)
{
	char szFileName[128];
	memset(szFileName, 0, sizeof(szFileName));
	sprintf(szFileName, "/data/data/com.example.screenshot/%d.ppm", nFileIndex);

	FILE *pfile = NULL;
	int x = 0;
	int y = 0;

	pfile = fopen(szFileName, "wb");
	if (pfile == NULL)
		return;

	fprintf(pfile, "P6\n%d %d\n255\n", width, height);
	fwrite(pFrame, 1, width*height*3, pfile);

//	for (y=0; y<height; y++)
//	{
//		for (x=0; x<width; x++)
//		{
//			fwrite(pFrame+y*width*4+x*4, 1, 1, pfile);
//			fwrite(pFrame+y*width*4+x*4+1, 1, 1, pfile);
//			fwrite(pFrame+y*width*4+x*4+2, 1, 1, pfile);
//		}
//	}

	fclose(pfile);
}

FILE* SaveVideo(unsigned char *pFrame, int bufsize, FILE* pfile, int nFileIndex, int isEnd)
{
	char szFileName[128];
	memset(szFileName, 0, sizeof(szFileName));
	sprintf(szFileName, "/data/data/com.example.screenshot/%d.avi", nFileIndex);

	if (pfile == NULL)
	{
		pfile = fopen(szFileName, "wb");
		if (pfile == NULL)
		{
			return NULL;
		}
	}

	fwrite(pFrame, 1, bufsize, pfile);

	if (isEnd)
	{
		unsigned char encode_buf[4];
		encode_buf[0] = 0x00;
		encode_buf[1] = 0x00;
		encode_buf[2] = 0x01;
		encode_buf[3] = 0xb7;
		fwrite(encode_buf, 1, 4, pfile);

		fclose(pfile);
	}

	return pfile;
}
