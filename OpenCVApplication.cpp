// OpenCVApplication.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "common.h"
#include <omp.h>
#include <random>
#include <algorithm>
#include <fstream>
#include <iostream>
#include <string>

void testOpenImage()
{
	char fname[MAX_PATH];
	while(openFileDlg(fname))
	{
		Mat src;
		src = imread(fname);
		imshow("image",src);
		waitKey();
	}
}

void testOpenImagesFld()
{
	char folderName[MAX_PATH];
	if (openFolderDlg(folderName)==0)
		return;
	char fname[MAX_PATH];
	FileGetter fg(folderName,"bmp");
	while(fg.getNextAbsFile(fname))
	{
		Mat src;
		src = imread(fname);
		imshow(fg.getFoundFileName(),src);
		if (waitKey()==27) //ESC pressed
			break;
	}
}

void testImageOpenAndSave()
{
	Mat src, dst;

	src = imread("Images/Lena_24bits.bmp", CV_LOAD_IMAGE_COLOR);	// Read the image

	if (!src.data)	// Check for invalid input
	{
		printf("Could not open or find the image\n");
		return;
	}

	// Get the image resolution
	Size src_size = Size(src.cols, src.rows);

	// Display window
	const char* WIN_SRC = "Src"; //window for the source image
	namedWindow(WIN_SRC, CV_WINDOW_AUTOSIZE);
	cvMoveWindow(WIN_SRC, 0, 0);

	const char* WIN_DST = "Dst"; //window for the destination (processed) image
	namedWindow(WIN_DST, CV_WINDOW_AUTOSIZE);
	cvMoveWindow(WIN_DST, src_size.width + 10, 0);

	cvtColor(src, dst, CV_BGR2GRAY); //converts the source image to a grayscale one

	imwrite("Images/Lena_24bits_gray.bmp", dst); //writes the destination to file

	imshow(WIN_SRC, src);
	imshow(WIN_DST, dst);

	printf("Press any key to continue ...\n");
	waitKey(0);
}

void testNegativeImage()
{
	char fname[MAX_PATH];
	while(openFileDlg(fname))
	{
		double t = (double)getTickCount(); // Get the current time [s]
		
		Mat src = imread(fname,CV_LOAD_IMAGE_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		Mat dst = Mat(height,width,CV_8UC1);
		// Asa se acceseaaza pixelii individuali pt. o imagine cu 8 biti/pixel
		// Varianta ineficienta (lenta)
		for (int i=0; i<height; i++)
		{
			for (int j=0; j<width; j++)
			{
				uchar val = src.at<uchar>(i,j);
				uchar neg = MAX_PATH-val;
				dst.at<uchar>(i,j) = neg;
			}
		}

		// Get the current time again and compute the time difference [s]
		t = ((double)getTickCount() - t) / getTickFrequency();
		// Print (in the console window) the processing time in [ms] 
		printf("Time = %.3f [ms]\n", t * 1000);

		imshow("input image",src);
		imshow("negative image",dst);
		waitKey();
	}
}

void testParcurgereSimplaDiblookStyle()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
		int height = src.rows;
		int width = src.cols;
		Mat dst = src.clone();

		double t = (double)getTickCount(); // Get the current time [s]

		// the fastest approach using the “diblook style”
		uchar *lpSrc = src.data;
		uchar *lpDst = dst.data;
		int w = (int) src.step; // no dword alignment is done !!!
		for (int i = 0; i<height; i++)
			for (int j = 0; j < width; j++) {
				uchar val = lpSrc[i*w + j];
				lpDst[i*w + j] = 255 - val;
			}

		// Get the current time again and compute the time difference [s]
		t = ((double)getTickCount() - t) / getTickFrequency();
		// Print (in the console window) the processing time in [ms] 
		printf("Time = %.3f [ms]\n", t * 1000);

		imshow("input image",src);
		imshow("negative image",dst);
		waitKey();
	}
}

void testColor2Gray()
{
	char fname[MAX_PATH];
	while(openFileDlg(fname))
	{
		Mat src = imread(fname);

		int height = src.rows;
		int width = src.cols;

		Mat dst = Mat(height,width,CV_8UC1);

		// Asa se acceseaaza pixelii individuali pt. o imagine RGB 24 biti/pixel
		// Varianta ineficienta (lenta)
		for (int i=0; i<height; i++)
		{
			for (int j=0; j<width; j++)
			{
				Vec3b v3 = src.at<Vec3b>(i,j);
				uchar b = v3[0];
				uchar g = v3[1];
				uchar r = v3[2];
				dst.at<uchar>(i,j) = (r+g+b)/3;
			}
		}
		
		imshow("input image",src);
		imshow("gray image",dst);
		waitKey();
	}
}

void testBGR2HSV()
{
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		Mat src = imread(fname);
		int height = src.rows;
		int width = src.cols;

		// Componentele d eculoare ale modelului HSV
		Mat H = Mat(height, width, CV_8UC1);
		Mat S = Mat(height, width, CV_8UC1);
		Mat V = Mat(height, width, CV_8UC1);

		// definire pointeri la matricele (8 biti/pixeli) folosite la afisarea componentelor individuale H,S,V
		uchar* lpH = H.data;
		uchar* lpS = S.data;
		uchar* lpV = V.data;

		Mat hsvImg;
		cvtColor(src, hsvImg, CV_BGR2HSV);

		// definire pointer la matricea (24 biti/pixeli) a imaginii HSV
		uchar* hsvDataPtr = hsvImg.data;

		for (int i = 0; i<height; i++)
		{
			for (int j = 0; j<width; j++)
			{
				int hi = i*width * 3 + j * 3;
				int gi = i*width + j;

				lpH[gi] = hsvDataPtr[hi] * 510 / 360;		// lpH = 0 .. 255
				lpS[gi] = hsvDataPtr[hi + 1];			// lpS = 0 .. 255
				lpV[gi] = hsvDataPtr[hi + 2];			// lpV = 0 .. 255
			}
		}

		imshow("input image", src);
		imshow("H", H);
		imshow("S", S);
		imshow("V", V);

		waitKey();
	}
}

void testResize()
{
	char fname[MAX_PATH];
	while(openFileDlg(fname))
	{
		Mat src;
		src = imread(fname);
		Mat dst1,dst2;
		//without interpolation
		resizeImg(src,dst1,320,false);
		//with interpolation
		resizeImg(src,dst2,320,true);
		imshow("input image",src);
		imshow("resized image (without interpolation)",dst1);
		imshow("resized image (with interpolation)",dst2);
		waitKey();
	}
}

void testCanny()
{
	char fname[MAX_PATH];
	while(openFileDlg(fname))
	{
		Mat src,dst,gauss;
		src = imread(fname,CV_LOAD_IMAGE_GRAYSCALE);
		double k = 0.4;
		int pH = 50;
		int pL = (int) k*pH;
		GaussianBlur(src, gauss, Size(5, 5), 0.8, 0.8);
		Canny(gauss,dst,pL,pH,3);
		imshow("input image",src);
		imshow("canny",dst);
		waitKey();
	}
}

void testVideoSequence()
{
	VideoCapture cap("Videos/rubic.avi"); // off-line video from file
	//VideoCapture cap(0);	// live video from web cam
	if (!cap.isOpened()) {
		printf("Cannot open video capture device.\n");
		waitKey(0);
		return;
	}
		
	Mat edges;
	Mat frame;
	char c;

	while (cap.read(frame))
	{
		Mat grayFrame;
		cvtColor(frame, grayFrame, CV_BGR2GRAY);
		Canny(grayFrame,edges,40,100,3);
		imshow("source", frame);
		imshow("gray", grayFrame);
		imshow("edges", edges);
		c = cvWaitKey(0);  // waits a key press to advance to the next frame
		if (c == 27) {
			// press ESC to exit
			printf("ESC pressed - capture finished\n"); 
			break;  //ESC pressed
		};
	}
}


void testSnap()
{
	VideoCapture cap(0); // open the deafult camera (i.e. the built in web cam)
	if (!cap.isOpened()) // openenig the video device failed
	{
		printf("Cannot open video capture device.\n");
		return;
	}

	Mat frame;
	char numberStr[256];
	char fileName[256];
	
	// video resolution
	Size capS = Size((int)cap.get(CV_CAP_PROP_FRAME_WIDTH),
		(int)cap.get(CV_CAP_PROP_FRAME_HEIGHT));

	// Display window
	const char* WIN_SRC = "Src"; //window for the source frame
	namedWindow(WIN_SRC, CV_WINDOW_AUTOSIZE);
	cvMoveWindow(WIN_SRC, 0, 0);

	const char* WIN_DST = "Snapped"; //window for showing the snapped frame
	namedWindow(WIN_DST, CV_WINDOW_AUTOSIZE);
	cvMoveWindow(WIN_DST, capS.width + 10, 0);

	char c;
	int frameNum = -1;
	int frameCount = 0;

	for (;;)
	{
		cap >> frame; // get a new frame from camera
		if (frame.empty())
		{
			printf("End of the video file\n");
			break;
		}

		++frameNum;
		
		imshow(WIN_SRC, frame);

		c = cvWaitKey(10);  // waits a key press to advance to the next frame
		if (c == 27) {
			// press ESC to exit
			printf("ESC pressed - capture finished");
			break;  //ESC pressed
		}
		if (c == 115){ //'s' pressed - snapp the image to a file
			frameCount++;
			fileName[0] = NULL;
			sprintf(numberStr, "%d", frameCount);
			strcat(fileName, "Images/A");
			strcat(fileName, numberStr);
			strcat(fileName, ".bmp");
			bool bSuccess = imwrite(fileName, frame);
			if (!bSuccess) 
			{
				printf("Error writing the snapped image\n");
			}
			else
				imshow(WIN_DST, frame);
		}
	}

}

void MyCallBackFunc(int event, int x, int y, int flags, void* param)
{
	//More examples: http://opencvexamples.blogspot.com/2014/01/detect-mouse-clicks-and-moves-on-image.html
	Mat* src = (Mat*)param;
	if (event == CV_EVENT_LBUTTONDOWN)
		{
			printf("Pos(x,y): %d,%d  Color(RGB): %d,%d,%d\n",
				x, y,
				(int)(*src).at<Vec3b>(y, x)[2],
				(int)(*src).at<Vec3b>(y, x)[1],
				(int)(*src).at<Vec3b>(y, x)[0]);
		}
}

void testMouseClick()
{
	Mat src;
	// Read image from file 
	char fname[MAX_PATH];
	while (openFileDlg(fname))
	{
		src = imread(fname);
		//Create a window
		namedWindow("My Window", 1);

		//set the callback function for any mouse event
		setMouseCallback("My Window", MyCallBackFunc, &src);

		//show the image
		imshow("My Window", src);

		// Wait until user press some key
		waitKey(0);
	}
}

/* Histogram display function - display a histogram using bars (simlilar to L3 / PI)
Input:
name - destination (output) window name
hist - pointer to the vector containing the histogram values
hist_cols - no. of bins (elements) in the histogram = histogram image width
hist_height - height of the histogram image
Call example:
showHistogram ("MyHist", hist_dir, 255, 200);
*/
void showHistogram(const std::string& name, int* hist, const int  hist_cols, const int hist_height)
{
	Mat imgHist(hist_height, hist_cols, CV_8UC3, CV_RGB(255, 255, 255)); // constructs a white image

	//computes histogram maximum
	int max_hist = 0;
	for (int i = 0; i<hist_cols; i++)
	if (hist[i] > max_hist)
		max_hist = hist[i];
	double scale = 1.0;
	scale = (double)hist_height / max_hist;
	int baseline = hist_height - 1;

	for (int x = 0; x < hist_cols; x++) {
		Point p1 = Point(x, baseline);
		Point p2 = Point(x, baseline - cvRound(hist[x] * scale));
		line(imgHist, p1, p2, CV_RGB(255, 0, 255)); // histogram bins colored in magenta
	}

	imshow(name, imgHist);
}

void negativeImage()
{
	char pressedKey;

	char fname[MAX_PATH];
	openFileDlg(fname);
	Mat img = imread(fname,CV_LOAD_IMAGE_GRAYSCALE);
	Mat negImg(img.rows, img.cols, CV_8UC1);
	imshow("Negative image", img);

	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			negImg.data[i*img.cols + j] = 255 - img.data[i * img.cols + j];
 		}
	}

	imshow("Original image", img);
	imshow("Negative image", negImg);
	waitKey();
}

void negativeWebCam()
{
	VideoCapture cap(0); // open the deafult camera (i.e. the built in web cam)
	if (!cap.isOpened()) // openenig the video device failed
	{
		printf("Cannot open video capture device.\n");
		return;
	}

	Mat camFrame;


	Size capS = Size((int)cap.get(CV_CAP_PROP_FRAME_WIDTH), (int)cap.get(CV_CAP_PROP_FRAME_HEIGHT));

	//display main window
	const char *WIN_DST = "Src image";
	namedWindow(WIN_DST, CV_WINDOW_AUTOSIZE);
	cvMoveWindow(WIN_DST, 0, 0);


	//display gray image
	const char *WIN_GRAY = "Gray image";
	namedWindow(WIN_GRAY, CV_WINDOW_AUTOSIZE);
	cvMoveWindow(WIN_GRAY, capS.width + 10, 0);

	//display 
	const char *WIN_NEG_ORIG = "Negative original image";
	namedWindow(WIN_NEG_ORIG, CV_WINDOW_AUTOSIZE);
	cvMoveWindow(WIN_NEG_ORIG, 0, capS.height + 30);

	//display gray image
	const char *WIN_GRAY_NEG = "Gray neg image";
	namedWindow(WIN_GRAY_NEG, CV_WINDOW_AUTOSIZE);
	cvMoveWindow(WIN_GRAY_NEG, capS.width + 10, capS.height + 30);

	//display 
	char pressedKey;
	

	while (true)
	{
		cap >> camFrame;
		Mat grayFrame(camFrame.rows, camFrame.cols,CV_8UC1);
		Mat origNegFrame(camFrame.rows, camFrame.cols, CV_8UC3);
		Mat grayNegFrame(camFrame.rows, camFrame.cols, CV_8UC1);

		
#pragma omp parallel for
		for (int i = 0; i < camFrame.rows; ++i)
		{
#pragma omp parallel for
			for (int j = 0; j < camFrame.cols; ++j)
			{
				Vec3b pixelColor = camFrame.at<Vec3b>(i, j);
				unsigned char b = pixelColor[0];
				unsigned char g = pixelColor[1];
				unsigned char r = pixelColor[2];
				grayFrame.data[i*camFrame.cols + j] = 0.2989 * r + 0.5870 * g + 0.1140 * b;
			
				pixelColor[0] = 255 - pixelColor[0];
				pixelColor[1] = 255 - pixelColor[1];
				pixelColor[2] = 255 - pixelColor[2];

				origNegFrame.at<Vec3b>(i, j) = pixelColor;
				grayNegFrame.data[i*camFrame.cols + j] = 0.2989 * (255 - r) + 0.5870 * (255 - g) + 0.1140 *(255 - b);
			}
		}

		//cvtColor(camFrame, grayFrame, CV_RGB2GRAY);
		
		if (camFrame.empty())
		{
			printf("End of transmission!\n");
			break;
		}
		imshow(WIN_DST,camFrame);
		imshow(WIN_GRAY, grayFrame);
		imshow(WIN_NEG_ORIG, origNegFrame);
		imshow(WIN_GRAY_NEG, grayNegFrame);
		

		pressedKey = cvWaitKey(10);
		if (pressedKey == 27)
		{
			// press ESC to exit
			printf("ESC pressed - capture finished");
			break;  //ESC pressed
		}
		
	
	}
}


void changeGrayLevel()
{
	VideoCapture cap(0);
	if (!cap.isOpened())
	{
		printf("Cannot open cam!\n");
		return;
	}

	Mat camFrame;
	Mat grayFrame1;
	Mat grayFrame2;
	Size capS = Size((int)cap.get(CV_CAP_PROP_FRAME_WIDTH), (int)cap.get(CV_CAP_PROP_FRAME_HEIGHT));

	//Dispaly window
	const char* WIN_SRC1 = "DIFFERENT GRAY LEVELS";
	moveWindow(WIN_SRC1, 0, 0);
	
	//Dispaly window
	const char* WIN_SRC2 = "DIFFERENT GRAY LEVELS - MUL";

	moveWindow(WIN_SRC2, capS.width + 10, 0);

	char pressedKey;
	char grayAdditiveTerm = 5;
	char grayMulTerm = 1;
	while (true)
	{
		cap >> camFrame;
		cvtColor(camFrame, grayFrame1, CV_RGB2GRAY);
		cvtColor(camFrame, grayFrame2, CV_RGB2GRAY);
#pragma omp parallel for
		for (int i = 0; i < grayFrame1.rows; ++i)
		{
#pragma omp parallel for
			for (int j = 0; j < grayFrame1.cols; ++j)
			{
				unsigned char& currentPixelIntensity = grayFrame1.data[i * grayFrame1.cols + j];
				//currentPixelIntensity += grayAdditiveTerm;
				currentPixelIntensity = saturate_cast<uchar>(currentPixelIntensity + grayAdditiveTerm);
				if (currentPixelIntensity < 0)
				{
					currentPixelIntensity = 0;
				}

				unsigned char& currentPixelIntensity2 = grayFrame2.data[i * grayFrame2.cols + j];
				currentPixelIntensity2 = saturate_cast<uchar>(currentPixelIntensity2 / grayMulTerm);
			}
		}
		imshow(WIN_SRC1, grayFrame1);
		imshow(WIN_SRC2, grayFrame2);
		pressedKey = cvWaitKey(1);
		if (pressedKey == 27)
		{
			// press ESC to exit
			printf("ESC pressed - capture finished");
			break;  //ESC pressed
		}
		else if (pressedKey == 'w')
		{
			grayAdditiveTerm--;
		}
		else if (pressedKey == 's')
		{
			grayAdditiveTerm++;
		}
		else if (pressedKey == 'e')
		{
			grayMulTerm--;
			if (grayMulTerm <= 0)
			{
				grayMulTerm = 1;
			}
		}
		else if (pressedKey == 'd')
		{
			grayMulTerm++;
		}
			
	}
}

void computeInverse(float m[][3])
{
	for (int i = 0; i < 3; ++i)
	{
		for (int j = 0; j < 3; ++j)
		{
			printf("%10f ", m[i][j]);
		}
		printf("\n");
	}

		double determinant = m[0][0] * m[1][1] * m[2][2] +
			m[0][1] * m[1][2] * m[2][0] +
			m[0][2] * m[1][0] * m[2][1] -
			m[0][0] * m[2][1] * m[1][2] -
			m[0][1] * m[1][0] * m[2][2] -
			m[0][2] * m[1][1] * m[2][0];
		double invdet = 1 / determinant;

		float inverseMatrix[3][3];
		inverseMatrix[0][0] = (m[1][1] * m[2][2] - m[2][1] * m[1][2]) * invdet;
		inverseMatrix[0][1] = (m[0][2] * m[2][1] - m[0][1] * m[2][2]) * invdet;
		inverseMatrix[0][2] = (m[0][1] * m[1][2] - m[0][2] * m[1][1]) * invdet;
		inverseMatrix[1][0] = (m[1][2] * m[2][0] - m[1][0] * m[2][2]) * invdet;
		inverseMatrix[1][1] = (m[0][0] * m[2][2] - m[0][2] * m[2][0]) * invdet;
		inverseMatrix[1][2] = (m[1][0] * m[0][2] - m[0][0] * m[1][2]) * invdet;
		inverseMatrix[2][0] = (m[1][0] * m[2][1] - m[2][0] * m[1][1]) * invdet;
		inverseMatrix[2][1] = (m[2][0] * m[0][1] - m[0][0] * m[2][1]) * invdet;
		inverseMatrix[2][2] = (m[0][0] * m[1][1] - m[1][0] * m[0][1]) * invdet;

		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 3; ++j)
			{
				printf("%10f ", inverseMatrix[i][j]);
			}
			printf("\n");
		}

	}

void squaredImage()
{
	Mat img(256, 256, CV_8UC3);

	Vec3b white(255, 255, 255);
	Vec3b red(0, 0, 255);
	Vec3b green(0, 255, 0);
	Vec3b yellow(0, 255, 255);


	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (i < img.rows / 2)
			{
				if (j < img.cols / 2)
				{
					img.at<Vec3b>(i, j) = white;
				}
				else
				{
					img.at<Vec3b>(i, j) = red;
				}
			}
			else
			{
				if (j < img.cols / 2)
				{
					img.at<Vec3b>(i, j) = green;
				}
				else
				{
					img.at<Vec3b>(i, j) = yellow;
				}
			}


		}
	}
	imshow("Image", img);
	cvWaitKey(0);
}

void additiveGrayChange(Mat &img, int additiveFactor)
{

	imshow("Image", img);
	cvWaitKey();

	bool successWriting =  imwrite("OriginalGrey.bmp", img);
	if (!successWriting)
	{
		printf("Could not save image to the disk!\n");
	}


	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			img.data[i * img.cols + j] += additiveFactor;
		}
	}

	imshow("Image", img);
	cvWaitKey();

	successWriting = imwrite("AdditiveGrey.bmp", img);
	if (!successWriting)
	{
		printf("Could not save image to the disk!\n");
	}
}

void multiplicativeGrayChange(Mat &img, int multiplicativeFactor)
{
	if (multiplicativeFactor <= 0)
	{
		printf("Invalid multiplicative factor!\n");
		return;
	}

	imshow("Image", img);
	cvWaitKey();
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			img.data[i * img.cols + j] /= multiplicativeFactor;
		}
	}
	imshow("Image", img);
	cvWaitKey();
	
	bool successWriting = imwrite("MultiplicativeGrey.bmp", img);
	if (!successWriting)
	{
		printf("Could not save image to the disk!\n");
	}
}

void l2e1_extractRGBGrays(Mat& img,bool waitKey)
{
	Mat redGray(img.rows,img.cols,CV_8UC1);
	Mat greenGray(img.rows, img.cols, CV_8UC1);
	Mat blueGray(img.rows, img.cols, CV_8UC1);
#pragma omp parallel for
	for (int i = 0; i < img.rows; ++i)
	{
#pragma omp parallel for
		for (int j = 0; j < img.cols; ++j)
		{
			redGray.data	[i * img.cols + j]	= img.data[img.channels() *(i * img.cols + j) + 2];
			greenGray.data	[i * img.cols + j]	= img.data[img.channels() *(i * img.cols + j) + 1];
			blueGray.data	[i * img.cols + j]	= img.data[img.channels() *(i * img.cols + j) + 0];
		}
	}
	imshow("Original", img);
	imshow("RedGray", redGray);
	imshow("GreenGray", greenGray);
	imshow("BlueGray", blueGray);
	if (waitKey)
	{
		cvWaitKey();
	}

}

void l2e2_rgb24ToGrayScale(Mat& img, bool waitKey)
{
	Mat medGray(img.rows, img.cols, CV_8UC1);
	Mat ponderGray(img.rows, img.cols, CV_8UC1);

#pragma omp parallel for
	for (int i = 0; i < img.rows; ++i)
	{
#pragma omp parallel for
		for (int j = 0; j < img.cols; ++j)
		{
			unsigned char r = img.data[img.channels() * (i * img.cols + j) + 2];
			unsigned char g = img.data[img.channels() * (i * img.cols + j) + 1];
			unsigned char b = img.data[img.channels() * (i * img.cols + j) + 0];
			ponderGray.data	[ i * img.cols + j] = 0.2989 * r + 0.5870 * g + 0.1140 * b;
			medGray.data	[i * img.cols + j]	= (r + g + b) / 3;
		} 
	}

	imshow("MedGray", medGray);
	imshow("PonderGray", ponderGray);
	if (waitKey)
	{
		cvWaitKey();
	}
}

void l2e3_grayToBinary(Mat& img,unsigned char treshold , bool waitKey)
{
	Mat binaryImg(img.rows, img.cols, CV_8UC1);
#pragma omp parallel for
	for (int i = 0; i < img.rows; ++i)
	{
#pragma omp parallel for
		for (int j = 0; j < img.cols; ++j)
		{
			if (img.data[i * img.cols + j] < treshold)
			{
				binaryImg.data[i * img.cols + j] = 0;
			}
			else
			{
				binaryImg.data[i * img.cols + j] = 255;
			}
		}
	}
	imshow("Original", img);
	imshow("Binary Img", binaryImg);
	if (waitKey)
	{
		cvWaitKey();
	}
}

void l2e4_RGBtoHSV(Mat& img, bool waitKey)
{
	Mat V(img.rows, img.cols, CV_8UC1);
	Mat S(img.rows, img.cols, CV_8UC1);
	Mat H(img.rows, img.cols, CV_8UC1);

#pragma omp parallel for
	for (int i = 0; i < img.rows; ++i)
	{
#pragma omp parallel for
		for (int j = 0; j < img.cols; ++j)
		{
			unsigned char R = img.data[img.channels() * (i * img.cols + j) + 2];
			unsigned char G = img.data[img.channels() * (i * img.cols + j) + 1];
			unsigned char B = img.data[img.channels() * (i * img.cols + j) + 0];

			float r = static_cast<float>(R) / 255;
			float g = static_cast<float>(G) / 255;
			float b = static_cast<float>(B) / 255;

			float M = max(max(r, g), b);
			float m = min(min(r, g), b);
			float C = M - m;

			V.data[i * img.cols + j] = 255 * M;
			if (C)
			{
				S.data[i * img.cols + j] = (C / M) * 255;
				if (M == r)
				{
					H.data[i * img.cols + j] = (60 * (g - b) / C) * 255 / 360;
				}
				else if (M == g)
				{
					H.data[i * img.cols + j] = (120 + 60 * (b - r) / C) * 255 / 360;
				}
				else if (M == b)
				{
					H.data[i * img.cols + j] = (240 + 60 * (r - g) / C) * 255 / 360;
				}

			}
			else
			{
				S.data[i * img.cols + j] = 0;
				H.data[i * img.cols + j] = 0;
			}

			if(H.data[i * img.cols + j] < 0)
			{
				H.data[i * img.cols + j] = 0;
			}
		}
	}

	imshow("Original", img);
	imshow("Hue", H);
	imshow("Saturation", S);
	imshow("Value", V);
	if (waitKey)
	{
		cvWaitKey();
	}
}

void webCam_l2e1()
{
	VideoCapture cap(0);
	unsigned char pressedKey;
	if (!cap.isOpened())
	{
		printf("Cannot open webcam!\n");
		return;
	}
	Mat frame;
	while (true)
	{
		cap >> frame;
		l2e1_extractRGBGrays(frame, false);
		pressedKey = cvWaitKey(1);
		if (pressedKey == 27)
		{
			//esc pressed
			printf("ESC pressed - capture finished \n");
			return;
		}
	}
}

void  webCam_l2e2()
{
	VideoCapture cap(0);
	if (!cap.isOpened())
	{
		printf("Cannot open webcam\n");
		return;
	}
	Mat frame;
	unsigned char pressedKey;
	while (true)
	{
		cap >> frame;
		l2e2_rgb24ToGrayScale(frame, false);
		pressedKey = cvWaitKey(1);
		if (pressedKey == 27)
		{
			//esc pressed 
			printf("ESC pressed - capture finished \n");
			return;
		}
	}
}

void webCam_l2e3()
{
	VideoCapture cap(0);
	if (!cap.isOpened())
	{
		printf("Cannot open webcam\n");
		return;
	}
	Mat frame;
	unsigned char pressedKey;
	unsigned char treshold = 150;

	while (true)
	{
		cap >> frame;
		cvtColor(frame, frame, CV_RGB2GRAY);
		l2e3_grayToBinary(frame, treshold, false);
		pressedKey = cvWaitKey(1);
		if (pressedKey == 27)
		{
			//esc pressed
			printf("ESC pressed  - capture finished \n");
			return;
		}
		else if (pressedKey == 'w')
		{
			treshold++;
		}
		else if (pressedKey == 's')
		{
			treshold--;
		}
	}
}

void webCam_l2e4()
{
	VideoCapture cap(0);
	if (!cap.isOpened())
	{
		printf("Cannot open webcam\n");
		return;
	}

	Mat frame;
	unsigned char pressedKey;
	while (true)
	{
		cap >> frame;
		l2e4_RGBtoHSV(frame, false);
		pressedKey = cvWaitKey(1);
		if (pressedKey == 27)
		{
			//esc pressed 
			printf("ESC pressed - capture finished \n");
			return;
		}
	}
}

void l3e1_showHistogram(Mat& img,bool waitKey)
{
	int histValues[256];
	float pdfHistValues[256];
	for (int i = 0; i < 256; ++i)
	{
		histValues[i] = 0;
	}
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			histValues[img.data[i * img.cols + j]]++;
		}
	}
	for (int i = 0; i < 256; ++i)
	{
		pdfHistValues[i] = histValues[i] / img.rows / img.cols;
	}
	imshow("Image", img);
	showHistogram("Histogrma", histValues, 256, 500);
	if (waitKey)
	{
		cvWaitKey();
	}
}

Mat l3e5_multilevelTresholding(Mat& img, bool waitKey)
{

	Mat tresholdImage(img.rows,img.cols,CV_8UC1);
	int histValues[256];
	double pdfHistValues[256];
	for (int i = 0; i < 256; ++i)
	{
		histValues[i] = 0;
	}
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			histValues[img.data[i * img.cols + j]]++;
		}
	}
	for (int i = 0; i < 256; ++i)
	{
		pdfHistValues[i] = (static_cast<float>(histValues[i]) / img.rows) / img.cols;
	}
	int WH = 5;
	double TH = 0.0003;

	std::vector<int> localMaxima;
	localMaxima.push_back(0);
	int counter = 0;
	//histogram computations
	for (int k = 5; k < 256 - WH; ++k)
	{
		double average = 0 ;
		bool pdfKIsMaximum = true;
		//kernel operations
		for (int i = k - WH; i < k + WH + 1; ++i)
		{
			average += pdfHistValues[i];
			if (pdfHistValues[i] > pdfHistValues[k])
			{
				pdfKIsMaximum = false;
			}
		}
		average /= 2 * WH + 1;
		if ((pdfHistValues[k] > (average + TH)) && pdfKIsMaximum)
		{
			counter++;
			localMaxima.push_back(k);
		}
	}
	localMaxima.push_back(255);
#pragma omp parallel for
	for (int i = 0; i < img.rows; ++i)
	{
#pragma omp parallel for
		for (int j = 0; j < img.cols; ++j)
		{
			unsigned char nearestMax = 0;
			unsigned char pixelIntensity = img.data[i * img.cols + j];
			unsigned char intensityDifference = std::abs(0 - pixelIntensity);
			for (auto k : localMaxima)
			{
				if (std::abs(k - pixelIntensity) < intensityDifference)
				{
					intensityDifference = std::abs(k - pixelIntensity);
					nearestMax = k;
				}
			}
			tresholdImage.data[i * img.cols + j] = nearestMax;
		}
	}

	imshow("Original", img);
	imshow("Tresholded img", tresholdImage);
	//l3e1_showHistogram(img, false);
	//l3e1_showHistogram(tresholdImage, false);
	if (waitKey)
	{
		cvWaitKey();
	}
	return tresholdImage;
}

void l3e6_floydDithering(Mat& img, bool waitKey)
{
	Mat tresholdImage = img.clone();
	int histValues[256];
	double pdfHistValues[256];
	for (int i = 0; i < 256; ++i)
	{
		histValues[i] = 0;
	}
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			histValues[img.data[i * img.cols + j]]++;
		}
	}
	for (int i = 0; i < 256; ++i)
	{
		pdfHistValues[i] = (static_cast<float>(histValues[i]) / img.rows) / img.cols;
	}
	int WH = 5;
	double TH = 0.0003;

	std::vector<int> localMaxima;
	localMaxima.push_back(0);
	int counter = 0;
	//histogram computations
	for (int k = 5; k < 256 - WH; ++k)
	{
		double average = 0;
		bool pdfKIsMaximum = true;
		//kernel operations
		for (int i = k - WH; i < k + WH + 1; ++i)
		{
			average += pdfHistValues[i];
			if (pdfHistValues[i] > pdfHistValues[k])
			{
				pdfKIsMaximum = false;
			}
		}
		average /= 2 * WH + 1;
		if ((pdfHistValues[k] > (average + TH)) && pdfKIsMaximum)
		{
			counter++;
			localMaxima.push_back(k);
		}
	}

	
	localMaxima.push_back(255);
#pragma omp parallel for
	for (int i = 1; i < img.rows - 1; ++i)
	{
#pragma omp parallel for
		for (int j = 1; j < img.cols - 1; ++j)
		{
			unsigned char oldPixel = tresholdImage.data[i * tresholdImage.cols + j];

			unsigned char nearestMax = 0;
			unsigned char pixelIntensity = tresholdImage.data[i * tresholdImage.cols + j];
			unsigned char intensityDifference = std::abs(0 - pixelIntensity);
			for (auto k : localMaxima)
			{
				if (std::abs(k - pixelIntensity) < intensityDifference)
				{
					intensityDifference = std::abs(k - pixelIntensity);
					nearestMax = k;
				}
			}
			unsigned char newPixel = nearestMax;
			tresholdImage.data[i * tresholdImage.cols + j] = newPixel;
			char error = oldPixel - newPixel;

			tresholdImage.data[i * tresholdImage.cols + j + 1]			=  saturate_cast<uchar>(tresholdImage.data[i * tresholdImage.cols + j + 1] + (error * 7 / 16));
			tresholdImage.data[(i + 1) * tresholdImage.cols + j - 1]	=  saturate_cast<uchar>(tresholdImage.data[(i + 1) * tresholdImage.cols + j - 1] + (error * 3 / 16));
			tresholdImage.data[(i + 1) * tresholdImage.cols + j]		=  saturate_cast<uchar>(tresholdImage.data[(i + 1) * tresholdImage.cols + j] + (error * 5 / 16));
			tresholdImage.data[(i + 1) * tresholdImage.cols + j + 1]	=  saturate_cast<uchar>(tresholdImage.data[(i + 1) * tresholdImage.cols + j + 1] + (error * 1 / 16));
		}
	}


	imshow("Original", img);
	imshow("Tresholded img", tresholdImage);
	if (waitKey)
	{
		cvWaitKey();
	}
}


Mat l3e7_hsvToRGB(Mat& H, Mat& S, Mat&V)
{
	Mat rgbImage(H.rows, H.cols, CV_8UC3);

#pragma omp parallel for
	for (int i = 0; i < H.rows; ++i)
	{
#pragma omp parallel for
		for (int j = 0; j < H.cols; ++j)
		{

			float fR;
			float fG;
			float fB;

			float fV = static_cast<float>(V.data[i * V.cols + j]) / 255;
			float fS = static_cast<float>(S.data[i * S.cols + j]) / 255;
			float fH = static_cast<float>(H.data[i * H.cols + j]);

			float fC = fV * fS; // Chroma
			float fHPrime = fmod(fH / 60.0,6);
			float fX = fC * (1 - fabs(fmod(fHPrime, 2) - 1));
			float fM = fV - fC;
			if (0 <= fHPrime && fHPrime < 1) 
			{
				fR = fC;
				fG = fX;
				fB = 0;
			}
			else if (1 <= fHPrime && fHPrime < 2) 
			{
				fR = fX;
				fG = fC;
				fB = 0;
			}
			else if (2 <= fHPrime && fHPrime < 3) 
			{
				fR = 0;
				fG = fC;
				fB = fX;
			}
			else if (3 <= fHPrime && fHPrime < 4) 
			{
				fR = 0;
				fG = fX;
				fB = fC;
			}
			else if (4 <= fHPrime && fHPrime < 5) 
			{
				fR = fX;
				fG = 0;
				fB = fC;
			}
			else if (5 <= fHPrime && fHPrime < 6) 
			{
				fR = fC;
				fG = 0;
				fB = fX;
			}
			else 
			{
				fR = 0;
				fG = 0;
				fB = 0;
			}

			fR += fM;
			fG += fM;
			fB += fM;

			rgbImage.data[rgbImage.channels()*(i * rgbImage.cols + j) + 2] = (fR * 255);
			rgbImage.data[rgbImage.channels()*(i * rgbImage.cols + j) + 1] = (fG * 255);
			rgbImage.data[rgbImage.channels()*(i * rgbImage.cols + j) + 0] = (fB * 255);

		}
	}

	//imshow("RGB Image ", rgbImage);
	return rgbImage;
}

void l3e7_multilevelColorTresholding(Mat& img, bool waitKey)
{
	Mat V(img.rows, img.cols, CV_8UC1);
	Mat S(img.rows, img.cols, CV_8UC1);
	Mat H(img.rows, img.cols, CV_8UC1);

#pragma omp parallel for
	for (int i = 0; i < img.rows; ++i)
	{
#pragma imp parallel for
		for (int j = 0; j < img.cols; ++j)
		{
			unsigned char R = img.data[img.channels() * (i * img.cols + j) + 2];
			unsigned char G = img.data[img.channels() * (i * img.cols + j) + 1];
			unsigned char B = img.data[img.channels() * (i * img.cols + j) + 0];

			float r = static_cast<float>(R) / 255;
			float g = static_cast<float>(G) / 255;
			float b = static_cast<float>(B) / 255;

			float M = max(max(r, g), b);
			float m = min(min(r, g), b);
			float C = M - m;

			V.data[i * img.cols + j] = 255 * M;
			if (C)
			{
				S.data[i * img.cols + j] = (C / M) * 255;
				if (M == r)
				{
					H.data[i * img.cols + j] = (60 * (g - b) / C) * 255 / 360;
				}
				else if (M == g)
				{
					H.data[i * img.cols + j] = (120 + 60 * (b - r) / C) * 255 / 360;
				}
				else if (M == b)
				{
					H.data[i * img.cols + j] = (240 + 60 * (r - g) / C) * 255 / 360;
				}
			}
			else
			{
				S.data[i * img.cols + j] = 0;
				H.data[i * img.cols + j] = 0;
			}

			if (H.data[i * img.cols + j] < 0)
			{
				H.data[i * img.cols + j] = 0;
			}
		}
	}

	V = l3e5_multilevelTresholding(V, false);

	Mat rgbImage = l3e7_hsvToRGB(H, S, V);

	//imshow("Value", V);
	//imshow("Hue", H);
	//imshow("Saturation", S);
	imshow("Multilevel tresholding", rgbImage);
	

	if (waitKey)
	{
		cvWaitKey();
	}

}

void  webCam_l3e1()
{
	VideoCapture cap(0);
	if (!cap.isOpened())
	{
		printf("Cannpt open webcam \n");
		return;
	}

	Mat frame;
	unsigned char pressedKey;
	while (true)
	{
		cap >> frame;
		cvtColor(frame, frame, COLOR_RGB2GRAY);
		l3e1_showHistogram(frame, false);
		pressedKey = cvWaitKey(1);
		if (pressedKey == 27)
		{
			//esc pressed
			printf("ESC pressed - capture finished\n");
			return;
		}

	}

}


void webCam_l3e5() 
{
	VideoCapture cap(0);
	if (!cap.isOpened())
	{
		printf("Cannpt open webcam \n");
		return;
	}

	Mat frame;
	unsigned char pressedKey;
	while (true)
	{
		cap >> frame;
		cvtColor(frame, frame, COLOR_RGB2GRAY);
		l3e5_multilevelTresholding(frame, false);
		pressedKey = cvWaitKey(1);
		if (pressedKey == 27)
		{
			//esc pressed
			printf("ESC pressed - capture finished\n");
			return;
		}

	}
}

void webCam_l3e7()
{
	VideoCapture cap(0);
	if (!cap.isOpened())
	{
		printf("Cannot open webcam\n");
		return;
	}

	Mat frame;
	unsigned char pressedKey;
	while (true)
	{
		cap >> frame;
		l3e7_multilevelColorTresholding(frame, false);
		
		
		pressedKey = cvWaitKey(1);
		if (pressedKey == 27)
		{
			printf("ESC pressed - capture finished\n");
			return;
		}

	}
}

typedef struct _Cluster
{
	bool isValid;
	int id;
	int area;
	int perimeter;
	int upper_boundary;
	int lower_boundary;
	int left_boundary;
	int right_boundary;
	float aspect_ration;
	float thinness_ratio;
	double axis_of_elongation;
	std::pair<int, int> center_of_mass;

}Cluster;

typedef struct _MouseParams
{
	Mat original;
	Mat objects;
	std::vector<Cluster> clusters;
}MouseParams;
bool l4e1_isEdge(Mat img, int i, int j)
{
	if (
		img.data[img.channels()*(i * img.cols + j - 1) + 2] == 255 &&
		img.data[img.channels()*(i * img.cols + j - 1) + 1] == 255 &&
		img.data[img.channels()*(i * img.cols + j - 1) + 0] == 255
		)
	{
		return true;
	}
	if (
		img.data[img.channels()*(i * img.cols + j + 1) + 2] == 255 &&
		img.data[img.channels()*(i * img.cols + j + 1) + 1] == 255 &&
		img.data[img.channels()*(i * img.cols + j + 1) + 0] == 255
		)
	{
		return true;
	}
	if (
		img.data[img.channels()*((i + 1) * img.cols + j) + 2] == 255 &&
		img.data[img.channels()*((i + 1) * img.cols + j) + 1] == 255 &&
		img.data[img.channels()*((i + 1) * img.cols + j) + 0] == 255
		)
	{
		return true;
	}
	if (
		img.data[img.channels()*((i - 1) * img.cols + j) + 2] == 255 &&
		img.data[img.channels()*((i - 1) * img.cols + j) + 1] == 255 &&
		img.data[img.channels()*((i - 1) * img.cols + j) + 0] == 255
		)
	{
		return true;
	}
	return false;
}

void l4e1_onClick(int event, int j, int i, int flags, void* param)
{
	//More examples: http://opencvexamples.blogspot.com/2014/01/detect-mouse-clicks-and-moves-on-image.html
	MouseParams* mp = (MouseParams *)param;
	Mat img = (*mp).original;
	Mat objects = (*mp).objects;
	int selectedObject = 0;
	if (event == CV_EVENT_LBUTTONDOWN)
	{
		printf("Pos(x,y): %d,%d  Intensity: %d\n",
			i, j,
			(*mp).original.data[i * mp->original.cols + j]);
		selectedObject = objects.data[i * img.cols + j];
		printf("You pressed on object nr : %d\n", selectedObject);

		int centerI ;
		int centerJ;

		if (selectedObject != 0)
		{
			Cluster cluster = (*mp).clusters.at(selectedObject - 1);
			printf("Cluster %d :\n area : %d\n perimeter: %d\n center of mass: (%d,%d) \n  aspect ratio: %f\n  thinness ratio: %f\n", 
				cluster.id, cluster.area, cluster.perimeter, cluster.center_of_mass.first, cluster.center_of_mass.second,
				cluster.aspect_ration, cluster.thinness_ratio);
			centerI = (*mp).clusters.at(selectedObject - 1).center_of_mass.first;
			centerJ = (*mp).clusters.at(selectedObject - 1).center_of_mass.second;
			
			Mat pressedImg = (*mp).original.clone();
			int den = 0;
			int nom1 = 0;
			int nom2 = 0;

			for (int i = 0; i < pressedImg.rows; ++i)
			{
				for (int j = 0; j < pressedImg.cols; ++j)
				{
					if (objects.data[i* pressedImg.cols + j] != selectedObject
						)
					{
						pressedImg.data[img.channels()*(i * img.cols + j) + 2] = 255;
						pressedImg.data[img.channels()*(i * img.cols + j) + 1] = 255;
						pressedImg.data[img.channels()*(i * img.cols + j) + 0] = 255;
					}
					else
					{
						den += (i - centerI) * (j - centerJ);
						nom1 += (j - centerJ) * (j - centerJ);
						nom2 += (i - centerI) * (i - centerI);
						if (!l4e1_isEdge(img, i, j))
						{
							pressedImg.data[img.channels()*(i * img.cols + j) + 2] = 255;
							pressedImg.data[img.channels()*(i * img.cols + j) + 1] = 255;
							pressedImg.data[img.channels()*(i * img.cols + j) + 0] = 255;
						}
					}
				}
			}

			Mat oyProjection(objects.rows, objects.cols, CV_8UC1);


			for (int i = 0; i < objects.rows; ++i)
			{
				int lineProjection = 0;
				for (int j = 0; j < objects.cols; ++j)
				{
					if (objects.data[i * objects.cols + j] == selectedObject)
					{
						lineProjection++;
					}
				}
				memset((void*)&oyProjection.data[i * oyProjection.cols], 0, lineProjection);
			}
			for (int j = 0; j < objects.cols; ++j)
			{
				int colProjection = 0;
				for (int i = 0; i < objects.rows; ++i)
				{
					if (objects.data[i * objects.cols + j] == selectedObject)
					{
						colProjection++;
					}
				}
				for (int i = oyProjection.rows - colProjection; i < oyProjection.rows; ++i)
				{
					oyProjection.data[i * oyProjection.cols + j] = 0;
				}

			}
			
			imshow("OY Projection", oyProjection);
			line(pressedImg, Point(centerJ - 2, centerI), Point(centerJ + 2, centerI), Scalar(0, 0, 255));
			line(pressedImg, Point(centerJ, centerI - 2), Point(centerJ, centerI + 2), Scalar(0, 0, 255));
			(*mp).clusters.at(selectedObject - 1).axis_of_elongation = atan2(static_cast<double>(den), static_cast<double>(nom1 - nom2)) / 2;
			printf(" axis of elongation: %f\n", (*mp).clusters.at(selectedObject - 1).axis_of_elongation);
			Point a(centerJ, centerI);
			Point b;
			int length = 150;
			b.x = (int)round(a.x + length * cos((*mp).clusters.at(selectedObject - 1).axis_of_elongation));
			b.y = (int)round(a.y + length * sin((*mp).clusters.at(selectedObject - 1).axis_of_elongation));
			line(pressedImg, a, b, Scalar(0, 0, 255));
			b.x = (int)round(a.x - length * cos((*mp).clusters.at(selectedObject - 1).axis_of_elongation));
			b.y = (int)round(a.y - length * sin((*mp).clusters.at(selectedObject - 1).axis_of_elongation));
			line(pressedImg, a, b, Scalar(0, 0, 255));
			imshow("Pressed object", pressedImg);
		}
		
	}
	
	
	
}



void l4e1_bfs(Mat& img, Mat& clustersMatrix, std::queue<std::pair<int, int>>& bfsQueue, std::vector<Cluster>& foundObjects, int& objId)
{
	int pixelCounter = 0;
	int center_of_massX = 0;
	int center_of_massY = 0;
	int perimeter = 0;

	int left = 5000;
	int right = -5000;
	int up = 5000;
	int down = -5000;

	while (!bfsQueue.empty())
	{
		std::pair<int, int> currentPixel = bfsQueue.front();
		bfsQueue.pop();
		int i = currentPixel.first;
		int j = currentPixel.second;

		pixelCounter++;
		center_of_massX += i;
		center_of_massY += j;


		int di[] = { 0,1,1,1,0,-1,-1,-1 };
		int dj[] = { 1,-1,0,1,-1,-1,0,1 };

		for (int k = 0; k < 8; ++k)
		{
			if (((i + di[k]) >= 0 && (i + di[k]) < img.rows) &&
				((j + dj[k]) >= 0 && (j + dj[k]) < img.cols) &&
				img.data[(i + di[k])*img.cols + (j + dj[k])] != 255 &&
				clustersMatrix.data[(i + di[k]) * img.step + (j + dj[k])] == 0)
			{
				bfsQueue.push(std::pair<int, int>(i + di[k], j + dj[k]));
				clustersMatrix.data[(i + di[k]) * img.cols + j + dj[k]] = objId;
				if (l4e1_isEdge(img, i + di[k], j + dj[k]))
				{
					perimeter++;
				}
			}
		}


		
		if (i < up)
		{
			up = i;
		}
		if (i > down)
		{
			down = i;
		}
		if (j > right)
		{
			right = j;
		}
		if (j < left)
		{
			left = j;
		}
	}

	Cluster currentCluster;
	currentCluster.id = objId++;
	currentCluster.area = pixelCounter;
	currentCluster.perimeter = perimeter;
	currentCluster.center_of_mass = std::pair<int, int>(center_of_massX / pixelCounter, center_of_massY / pixelCounter);
	currentCluster.left_boundary = left;
	currentCluster.right_boundary = right;
	currentCluster.upper_boundary = up;
	currentCluster.lower_boundary = down;
	currentCluster.aspect_ration = static_cast<float>(right - left + 1) / static_cast<float>(down - up + 1);
	currentCluster.thinness_ratio = 4.0 * PI * pixelCounter / pow(perimeter, 2);
	foundObjects.push_back(currentCluster);

	//imshow("Cluster", clustersMatrix);
	//cvWaitKey();
}

void l4e1_extractShape(Mat& img, bool waitKey)
{

	std::queue<std::pair<int, int>> bfsQueue;
	std::vector<Cluster> foundObjects;
	int objId = 1;

	Mat g_img; 
	cvtColor(img, g_img, CV_RGB2GRAY);

	imshow("Image", img);
	

	Mat clustersMatrix(img.rows, img.cols, CV_8UC1,cvScalar(0));


	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (
				g_img.data[i * img.cols + j] != 255 &&
				clustersMatrix.data[i * img.cols + j] == 0
				)
			{
				bfsQueue.push(std::pair<int, int>(i, j));
				clustersMatrix.data[i * img.cols + j] = objId;
			 l4e1_bfs(g_img, clustersMatrix, bfsQueue, foundObjects, objId);
			}
		}
	}
	MouseParams mP;
	mP.original = img;
	mP.objects = clustersMatrix;
	mP.clusters = foundObjects;

	setMouseCallback("Image", l4e1_onClick, &mP);

	imshow("Clusters", clustersMatrix);

	for (auto cluster : foundObjects)
	{
		printf("Cluster %d :\n area : %d\n perimeter: %d\n center of mass: (%d,%d) \n",cluster.id,cluster.area,cluster.perimeter,cluster.center_of_mass.first,cluster.center_of_mass.second);
	}

	if (waitKey)
	{
		cvWaitKey();
	}
}

void l4e2_filterObjects(Mat& img, bool waitKey)
{
	std::queue<std::pair<int, int>> bfsQueue;
	std::vector<Cluster> foundObjects;
	int objId = 1;

	Mat g_img;
	cvtColor(img, g_img, CV_RGB2GRAY);

	imshow("Image", img);


	Mat clustersMatrix(img.rows, img.cols, CV_8UC1, cvScalar(0));


	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (
				g_img.data[i * img.cols + j] != 255 &&
				clustersMatrix.data[i * img.cols + j] == 0
				)
			{
				bfsQueue.push(std::pair<int, int>(i, j));
				clustersMatrix.data[i * img.cols + j] = objId;
				l4e1_bfs(g_img, clustersMatrix, bfsQueue, foundObjects, objId);
			}
		}
	}
	int areaTresholding;
	float angleTresholding;
	printf("Insert area tresholding : "), scanf("%d", &areaTresholding);
	printf("Insert area tresholding : "), scanf("%f", &angleTresholding);
	for (auto cluster : foundObjects)
	{
		int selectedObject = cluster.id;
		if (cluster.area > areaTresholding)
		{
			
			
			Mat pressedImg = img.clone();
			int den = 0;
			int nom1 = 0;
			int nom2 = 0;
			
			int centerI = cluster.center_of_mass.first;
			int centerJ = cluster.center_of_mass.second;
			for (int i = 0; i < pressedImg.rows; ++i)
			{
				for (int j = 0; j < pressedImg.cols; ++j)
				{
					if (clustersMatrix.data[i* pressedImg.cols + j] == selectedObject
						)
					{
						den  += (i - centerI) * (j - centerJ);
						nom1 += (j - centerJ) * (j - centerJ);
						nom2 += (i - centerI) * (i - centerI);
					}
				}
			}
			cluster.axis_of_elongation = atan2(static_cast<double>(den), static_cast<double>(nom1 - nom2)) / 2;
			foundObjects.at(selectedObject - 1).axis_of_elongation = atan2(static_cast<double>(den), static_cast<double>(nom1 - nom2)) / 2;
			if (cluster.axis_of_elongation > angleTresholding)
			{
				foundObjects.at(selectedObject - 1).isValid = true;
				printf("Cluster %d :\n area : %d\n perimeter: %d\n center of mass: (%d,%d) \n  axis of elongation: %f\n",
					cluster.id, cluster.area, cluster.perimeter, cluster.center_of_mass.first, cluster.center_of_mass.second, cluster.axis_of_elongation);
			}
			else 
			{
				foundObjects.at(selectedObject - 1).isValid = false;
			}
			
		}
		else
		{
			foundObjects.at(selectedObject - 1).isValid = false;
		}
		
	}
	Mat filteredImg = img.clone();
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (
				clustersMatrix.data[i * img.cols + j] != 0 &&
				!foundObjects.at(clustersMatrix.data[i * img.cols + j] - 1).isValid)
			{
				filteredImg.data[img.channels() * (i * img.cols + j) + 2] = 255;
				filteredImg.data[img.channels() * (i * img.cols + j) + 1] = 255;
				filteredImg.data[img.channels() * (i * img.cols + j) + 0] = 255;

			}
		}
	}
	imshow("Filtered image", filteredImg);
	if (waitKey)
	{
		cvWaitKey();
	}
}

Vec3b generateObjectColor(int id)
{
	std::default_random_engine gen;
	std::uniform_int_distribution<int> distribution(0, 255 - id);

	Vec3b color(distribution(gen) + id, distribution(gen) + id, distribution(gen) + id);
	return color;

}

void l5e1_bfsClusters(Mat& img, bool waitKey)
{
	std::queue<std::pair<int, int>> bfsQueue;
	std::vector<Cluster> foundObjects;
	int objId = 1;

	imshow("Image", img);


	Mat clustersMatrix(img.rows, img.cols, CV_8UC1, cvScalar(0));

	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (
				img.data[i * img.cols + j] != 255 &&
				clustersMatrix.data[i * img.cols + j] == 0
				)
			{
				bfsQueue.push(std::pair<int, int>(i, j));
				clustersMatrix.data[i * img.cols + j] = objId;
				l4e1_bfs(img, clustersMatrix, bfsQueue, foundObjects, objId);
			}
		}
	}
	std::vector<Vec3b> objectColor;
	for (Cluster c : foundObjects)
	{
		objectColor.push_back(generateObjectColor(c.id));
	}
	Mat colorImg(img.rows, img.cols, CV_8UC3);
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (clustersMatrix.data[i * img.cols + j] != 0)
			{
				colorImg.at<Vec3b>(i, j) = objectColor.at(clustersMatrix.data[i * img.cols + j] - 1);
			}
		}
	}
	imshow("Color image", colorImg);
	if (waitKey)
	{
		cvWaitKey();
	}

}

void l5e2_twoPassClustering(Mat& img, bool waitKey)
{
	int objectId = 1;
	Mat clusterMatrix(img.rows, img.cols, CV_8UC1, cvScalar(0));
	std::vector<std::vector<int>> equivalenceClasses;
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (img.data[i * img.step + j] != 255 &&
				clusterMatrix.data[i * img.step + j] == 0
				)
			{
				std::vector<int> neighbourIds;
				int di[] = { 0,-1,-1,-1 };
				int dj[] = { -1,-1,0,1 };
				for (int k = 0; k < 4; ++k)
				{
					if (((i + di[k]) >= 0 && (i + di[k]) < img.rows) &&
						((j + dj[k]) >= 0 && (j + dj[k]) < img.cols) &&
						clusterMatrix.data[(i + di[k]) * img.step + (j + dj[k])] != 0)
					{
						neighbourIds.push_back(clusterMatrix.data[(i + di[k]) * img.step + (j + dj[k])]);
					}
				}

				if (neighbourIds.size() == 0)
				{
					clusterMatrix.data[i * img.step + j] = objectId++;
					equivalenceClasses.push_back(std::vector<int>());
				}
				else
				{
					auto minId = std::min_element(neighbourIds.begin(), neighbourIds.end());
					clusterMatrix.data[i * img.step + j] = *minId;
					for (auto id : neighbourIds)
					{
						if (id != *minId)
						{
							equivalenceClasses.at(id - 1).push_back(*minId);
							equivalenceClasses.at(*minId - 1).push_back(id);
						}
					}
				}
			}
		}
	}
	std::vector<Vec3b> objectColor;
	for (int i = 0; i < objectId - 1; ++i)
	{
		objectColor.push_back(generateObjectColor(i));
	}

	Mat colorImg(img.rows, img.cols, CV_8UC3);
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (clusterMatrix.data[i * img.cols + j] != 0)
			{
				colorImg.at<Vec3b>(i, j) = objectColor.at(clusterMatrix.data[i * img.cols + j] - 1);
			}
		}
	}



	imshow("Original", img);
	imshow("Intermidiate", colorImg);

	int newId = 1;
	std::vector<int> newIds;
	for (int i = 0; i < objectId - 1; ++i)
	{
		newIds.push_back(0);
	}



	std::queue<int> queue;

	for (int i = 0; i < objectId - 1; ++i)
	{
		if (newIds.at(i) == 0)
		{
			newIds.at(i) = newId;
			queue.push(i);
			while (!queue.empty())
			{
				int currentId = queue.front();
				queue.pop();
				for (auto y : equivalenceClasses.at(currentId))
				{
					if (newIds.at(y - 1) == 0)
					{
						newIds.at(y - 1) = newId;
						queue.push(y - 1);
					}
				}
			}
			newId++;
		}
	}

	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (clusterMatrix.data[i * img.cols + j] != 0)
			{
				clusterMatrix.data[i * img.step + j] = newIds.at(clusterMatrix.data[i * img.step + j] - 1);
			}

		}
	}

	Mat colorImgF(img.rows, img.cols, CV_8UC3);
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (clusterMatrix.data[i * img.cols + j] != 0)
			{
				colorImgF.at<Vec3b>(i, j) = objectColor.at(clusterMatrix.data[i * img.cols + j] - 1);
			}
		}
	}

	imshow("Final", colorImgF);
	if (waitKey)
	{
		cvWaitKey();
	}
}

void traceBorder(Mat& img, Mat& borders, int i, int j)
{
	std::ofstream file;
	file.open("chaincode.txt", std::ios::trunc);
	file << i << ", " << j << std::endl;

	std::vector<int> chainCode;
	std::vector<int> chainCOdeDerivative;

	std::vector<Point> d;
	d.push_back(Point(0, 1));
	d.push_back(Point(-1, 1));
	d.push_back(Point(-1, 0));
	d.push_back(Point(-1, -1));
	d.push_back(Point(0, -1));
	d.push_back(Point(1, -1));
	d.push_back(Point(1, 0));
	d.push_back(Point(1, 1));

	int dir = 7;
	Point p0(i, j);
	Point previous(i,j);
	Point nextp(i, j);



	int starting_neighbour;
	int size = d.size();
	do
	{
		//std::cout << nextp << std::endl;
		
		if (dir % 2 == 0)
		{
			starting_neighbour = (dir + 7) % 8;
		}
		else
		{
			starting_neighbour = (dir + 6) % 8;
		}

		borders.data[nextp.x * img.cols + nextp.y] = 0;
		for (int k = starting_neighbour; k < starting_neighbour + 8; k++)
		{
			Point verified_point = d.at(k % size);
			verified_point.x = nextp.x + verified_point.x;
			verified_point.y = nextp.y + verified_point.y;
			if (img.data[(verified_point.x) * img.cols + (verified_point.y)] != 255 && 
				previous != verified_point )
			{
				previous = nextp;
				nextp = verified_point;
				dir = k % size;
				chainCode.push_back(dir);
				break;
			}
		}
	} while (nextp != p0);


	for (int i = 0; i < chainCode.size() - 1; ++i)
	{
		file << chainCode.at(i) << " ";
		int derivative = (8 - (chainCode.at(i) - chainCode.at(i + 1))) % 8;
		chainCOdeDerivative.push_back(derivative);
	}
	file << chainCode.at(chainCode.size() - 1) <<"\n";
	chainCOdeDerivative.push_back((8 - (chainCode.at(chainCode.size() - 1) - chainCode.at(0))) % 8);
	for (auto c : chainCOdeDerivative)
	{
		file << c << " ";
	}
	file.close();
}

void l6e1_borderTracing(Mat& img, bool waitKey)
{
	Mat borders(img.rows, img.cols, CV_8UC1, cvScalar(255));
	bool found = false;

	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (img.data[i * img.step + j] != 255 &&
				borders.data[i * img.step + j] == 255 && 
				!found
				)
			{
				traceBorder(img, borders, i, j);
				found = true;
			}
		}
	}
	imshow("Border", borders);
	imshow("Original", img);
	if (waitKey)
	{
		cvWaitKey();
	}
}

void l6e3_borderReconstruct()
{
	std::ifstream file("C:/Users/MGA4CLJ/Desktop/Fac/pi/OpenCVApplication-VS2017_OCV340_basic/Images/reconstruct.txt");
	std::string line;
	Mat img(300, 900, CV_8UC1, cvScalar(255));
	if (file.is_open())
	{
		std::vector<int> chainCode;
		int i,j;
		file >> i;
		std::cout << i << " ";
		file >> j;
		std::cout << j << std::endl;
		int dir;
		file >> dir;

		while (!file.eof())
		{
			file >> dir;
			chainCode.push_back(dir);
		}

	
		std::vector<Point> d;
		d.push_back(Point(0, 1));
		d.push_back(Point(-1, 1));
		d.push_back(Point(-1, 0));
		d.push_back(Point(-1, -1));
		d.push_back(Point(0, -1));
		d.push_back(Point(1, -1));
		d.push_back(Point(1, 0));
		d.push_back(Point(1, 1));

		Point p0(i, j);

		for (auto dir : chainCode)
		{
			img.data[p0.x * img.cols + p0.y] = 0;
			Point nextp = d.at(dir);
			p0 = p0 + nextp;
		}


		file.close();
	}

	else std::cout << "Unable to open file";

	imshow("Reconstruction", img);

	cvWaitKey();

}

Mat l7e1_dilation(Mat& img, bool waitKey)
{
	Mat dilatedImg(img.rows, img.cols, CV_8UC1,cvScalar(255));
	for (int i = 1; i < img.rows - 1; ++i)
	{
		for (int j = 1; j < img.cols - 1; ++j)
		{
			
			if (img.data[i * img.cols + j] != 255) // not background
			{
				dilatedImg.data[(i)*img.cols + (j)] = 0;
				int di[] = { 0,1,1,1,0,-1,-1,-1 };
				int dj[] = { 1,-1,0,1,-1,-1,0,1 };
				for (int k = 0; k < 8; ++k)
				{
					dilatedImg.data[(i + di[k])*img.cols + (j + dj[k])] = 0;
				}
			}
		}
	}
	imshow("Original", img);
	imshow("Dilated", dilatedImg);
	if (waitKey)
	{
		cvWaitKey();
	}
	return dilatedImg;
}

Mat l7e2_erosion(Mat& img,bool waitKey)
{
	Mat erosionImg(img.rows, img.cols, CV_8UC1, cvScalar(255));
	for (int i = 1; i < img.rows - 1; ++i)
	{
		for (int j = 1; j < img.cols - 1; ++j)
		{

			if (img.data[i * img.cols + j] != 255) // not background
			{
				erosionImg.data[i * img.cols + j] = img.data[i * img.cols + j];
				int di[] = { 0,1,1,1,0,-1,-1,-1 };
				int dj[] = { 1,-1,0,1,-1,-1,0,1 };
				for (int k = 0; k < 8; ++k)
				{
					if (img.data[(i + di[k])*img.cols + (j + dj[k])] == 255)
					{
						erosionImg.data[i * img.cols + j] = 255;
						break;
					}
					
				}
			}
		}
	}
	imshow("Original", img);
	imshow("Erosion", erosionImg);
	if (waitKey)
	{
		cvWaitKey();
	}
	return erosionImg;
}

Mat l7e3_opening(Mat& img, bool waitKey)
{
	Mat computedImg = img.clone();
	computedImg = l7e2_erosion(computedImg, false);
	computedImg = l7e1_dilation(computedImg, false);

	imshow("Original", img);
	imshow("Opening algorithm", computedImg);
	if (waitKey)
	{
		cvWaitKey();
	}
	return computedImg;
}

Mat l7e3_closing(Mat& img, bool waitKey)
{
	Mat computedImg = img.clone();
	computedImg = l7e1_dilation(computedImg, false);
	computedImg = l7e2_erosion(computedImg, false);
	imshow("Original", img);
	imshow("Closing algorithm", computedImg);
	if (waitKey)
	{
		cvWaitKey();
	}
	return computedImg;
}

Mat l7e4_boundaryExtraction(Mat &img, bool waitKey)
{
	Mat computedImg = img.clone();
	computedImg = l7e2_erosion(computedImg, false);

	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			computedImg.data[i * img.cols + j] = img.data[i * img.cols + j] | ~computedImg.data[i * img.cols + j];
		}
	}

	imshow("Border", computedImg);
	if (waitKey)
	{
		cvWaitKey();
	}
	return computedImg;
}

void l7_ntimesDilation(Mat& img)
{
	int n = 10;
	Mat tr = img.clone();
	imshow("Original s", img);
	for (int i = 0; i < n; i++)
	{
		tr = l7e3_opening(tr, false);
	}
	imshow("Final", tr);
	cvWaitKey();
}

void l7e5_onClick(int event, int j, int i, int flags, void* param)
{


	//More examples: http://opencvexamples.blogspot.com/2014/01/detect-mouse-clicks-and-moves-on-image.html
	Mat* img = (Mat *)param;
	Mat dil(img->rows, img->cols, CV_8UC1, cvScalar(255));
	Mat nextDil = dil.clone();

	int di[] = { -1,0,1,0,-1,-1,1,1 };
	int dj[] = { 0,1,0,-1,-1,1,1,-1 };
	if (event == CV_EVENT_LBUTTONDOWN)
	{
		std::cout << "i = " << i << ", " << "j = " << j;
		dil.data[i * img->cols + j] = 0;
		int x = 0;
		bool equalImages;
		while (true)
		{
			for (int i = 1; i < img->rows - 1; ++i)
			{
				for (int j = 1; j < img->cols - 1; ++j)
				{
					if (dil.data[i * img->cols + j] != 255)
					{
						nextDil.data[i * img->cols + j] = 0;
						for (int k = 0; k < 4; k++)
						{
							if (img->data[(i + di[k]) * img->cols + (j + dj[k])] == 0)
							{
								continue;
							}
							else
							{
								nextDil.data[(i + di[k]) * img->cols + (j + dj[k])] = 0;
							}
						}
					}
				}
			}
			imshow("Filling", dil);
			imshow("Next filling", nextDil);
			cvWaitKey();
			equalImages = true;
			for (int i = 0; i < img->rows; ++i)
			{
				for (int j = 0; j < img->cols; ++j)
				{
					if (nextDil.data[i * img->cols + j] != dil.data[i * img->cols + j])
					{
						equalImages = false;
						break;
					}
				}
				if (equalImages == false)
				{
					break;
				}
			}
			if (equalImages)
			{
				for (int i = 0; i < img->rows; ++i)
				{
					for (int j = 0; j < img->cols; ++j)
					{
						if (img->data[i * img->cols + j] == 0)
						{
							nextDil.data[i * img->cols + j] = 0;
							imshow("Final", nextDil);
						}
					}
				}
				break;
			}
			dil = nextDil.clone();
		}
		
	}

	
}


Mat l7e5_regionFilling(Mat& img, bool waitKey)
{	
	imshow("Original", img);
	setMouseCallback("Original", l7e5_onClick, &img);
	if (waitKey)
	{
		cvWaitKey();
	}
	return Mat();
}

void l8e1_grayScaleMean(Mat& img, bool waitKey)
{
	int histValues[256];
	float meanValue = 0;
	float standardDeviation = 0;

	int imin = 255;
	int imax = 0;
	float treshold;

	int M = img.cols * img.rows;
	for (int i = 0; i < 256; ++i)
	{
		histValues[i] = 0;
	}

	//histogram and imin imax
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			histValues[img.data[i * img.cols + j]]++;
			meanValue += img.data[i * img.cols + j];
			if (img.data[i * img.cols + j] < imin)
			{
				imin = img.data[i * img.cols + j];
			}
			if (img.data[i * img.cols + j] > imax)
			{
				imax = img.data[i * img.cols + j];
			}
		}
	}

	//standard deviation
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			standardDeviation += pow((img.data[i * img.cols + j] - meanValue/M), 2);
		}
	}

	int fhf = 0;
	int hf = 0;
	float mg1 = 0;
	float mg2 = 0;
	float tkm1 = 0;

	//tresholding
	treshold = (imin + imax) / 2;
	while (treshold - tkm1 > 0.1)
	{
		fhf = 0;
		hf = 0;
		for (int i = imin; i <= treshold; ++i)
		{
			fhf += i * histValues[i];
			hf += histValues[i];
		}
		mg1 = fhf / hf;
		fhf = 0;
		hf = 0;
		for (int i = treshold + 1; i < imax; ++i)
		{
			fhf += i * histValues[i];
			hf += histValues[i];
		}
		mg2 = fhf / hf;
		tkm1 = treshold;
		treshold = (mg1 + mg2) / 2;
		//std::cout << treshold << "," << mg1 << "," << mg2 << "," << tkm1 << std::endl;
		//getc(stdin);
	}
	
	standardDeviation /= M;
	standardDeviation = sqrt(standardDeviation);
	Mat binaryImg = img.clone();
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (binaryImg.data[i * img.cols + j] < treshold)
			{
				binaryImg.data[i * img.cols + j] = 0;
			}
			else
			{
				binaryImg.data[i * img.cols + j] = 255;
			}
		}
	}

	showHistogram("Histo", histValues, 256, 200);
	imshow("Original", img);
	imshow("Binarized", binaryImg);
	std::cout << "Mean value  = " << meanValue / M << "\n";
	std::cout << "Standar deviation  = " << standardDeviation << "\n";
	std::cout << "Treshold found = " << treshold << std::endl;

	if (waitKey)
	{
		cvWaitKey();
	}
}

void l8e2_histogramFunction(Mat& img, bool waitKey)
{
	int histValues[256];
	int histValues1[256];
	int imin = 255;
	int imax = 0;
	Mat& transform = img.clone();
	for (int i = 0; i < 256; ++i)
	{
		histValues[i] = 0;
		histValues1[i] = 0;
	}

	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			histValues[img.data[i * img.cols + j]]++;
			if (img.data[i * img.cols + j] < imin)
			{
				imin = img.data[i * img.cols + j];
			}
			if (img.data[i * img.cols + j] > imax)
			{
				imax = img.data[i * img.cols + j];
			}
		}
	}
	int goutmin, goutmax;
	std::cout << "ginmin = " << imin << std::endl;
	std::cout << "ginmax = " << imax << std::endl;
	std::cout << "goutmin = ", std::cin >> goutmin;
	std::cout << "goutmax = ", std::cin >> goutmax;
#pragma omp parallel for
	for (int i = 0; i < img.rows; ++i)
	{
#pragma omp parallel for
		for (int j = 0; j < img.cols; ++j)
		{
			transform.data[i * img.cols + j] =saturate_cast<uchar>( goutmin + (img.data[i * img.cols + j])*(goutmax - goutmin) / (imax - imin));
		}
	}
#pragma omp parallel for
	for (int i = 0; i < img.rows; ++i)
	{
#pragma omp parallel for
		for (int j = 0; j < img.cols; ++j)
		{
			histValues1[transform.data[i * transform.cols + j]]++;
		}
	}
	showHistogram("Original histo", histValues,256,300);
	showHistogram("After histo", histValues1, 256, 300);
	imshow("Original", img);
	imshow("Transform", transform);
	if (waitKey)
	{
		cvWaitKey();
	}
}

void l8e3_gammaCorrection(Mat& img, bool waitKey)
{
	float gamma = 0;
	Mat transform = img.clone();
	std::cout << "gamma = ", std::cin >> gamma;
#pragma omp parallel for
	for (int i = 0; i < img.rows; ++i)
	{
#pragma omp parallel for
		for (int j = 0; j < img.cols; ++j)
		{
			transform.data[i * img.cols + j] =saturate_cast<uchar>( 255 * std::pow((static_cast<float>(img.data[i * img.cols + j]) / 255), gamma));
		}
	}
	imshow("Original", img);
	imshow("Transform", transform);
	if (waitKey)
	{
		cvWaitKey();
	}
}

void l8e4_histogramSlide()
{

	VideoCapture cap(0);
	if (!cap.isOpened())
	{
		printf("Cannpt open webcam \n");
		return;
	}

	Mat frame;
	unsigned char pressedKey;
	unsigned char slideValue = 0;
	while (true)
	{
		cap >> frame;
		cvtColor(frame, frame, COLOR_RGB2GRAY);
		int histValues[256];
		int histValues1[256];
		Mat& transform = frame.clone();
		for (int i = 0; i < 256; ++i)
		{
			histValues[i] = 0;
			histValues1[i] = 0;
		}

		for (int i = 0; i < frame.rows; ++i)
		{
			for (int j = 0; j < frame.cols; ++j)
			{
				histValues[frame.data[i * frame.cols + j]]++;
				transform.data[i * transform.cols + j] = saturate_cast<uchar>(transform.data[i * transform.cols + j] + slideValue);
				histValues1[transform.data[i * transform.cols + j]]++;
			}
		}
		showHistogram("Histogram", histValues, 256, 200);
		showHistogram("Histogram transformed", histValues1, 256, 200);
		imshow("Original", frame);
		imshow("Transform", transform);
		pressedKey = cvWaitKey(1);
		if (pressedKey == 27)
		{
			//esc pressed
			printf("ESC pressed - capture finished\n");
			return;
		}
		else if (pressedKey == 'w')
		{
			slideValue++;
		}
		else if (pressedKey == 's')
		{
			slideValue--;
		}

	}
}

void l8e5_histogramEqualization(Mat& img, bool waitKey)
{
	int histValues[256];
	int cumulativeHisto[256];
	float cdf[256];

	int finalHist[256];
	int finalCumHist[256];

	int M = img.rows * img.cols;
	for (int i = 0; i < 256; ++i)
	{
		histValues[i] = 0;
		cdf[i] = 0;
		cumulativeHisto[i] = 0;
		finalCumHist[i] = 0;
		finalHist[i] = 0;
	}
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			histValues[img.data[i * img.cols + j]]++;
		}
	}
	int cumul = 0;
	for (int i = 0; i < 256; ++i)
	{
		cumulativeHisto[i] = histValues[i] + cumul;
		cumul += histValues[i];
	}
	for (int i = 0; i < 256; ++i)
	{
		cdf[i] = static_cast<float>(cumulativeHisto[i]) / M;
	}

	Mat eqImg = img.clone();
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			eqImg.data[i * img.cols + j] = 255 * (cdf[img.data[i * img.cols + j]]);
		}
	}
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			finalHist[eqImg.data[i * img.cols + j]]++;
		}
	}
	cumul = 0;
	for (int i = 0; i < 256; ++i)
	{
		finalCumHist[i] = finalHist[i] + cumul;
		cumul += finalHist[i];
	}


	showHistogram("Histogram", histValues, 256, 200);
	showHistogram("Histogram transformed", cumulativeHisto, 256, 200);
	showHistogram("Final histogram", finalHist, 256, 200);
	showHistogram("Final cumulative hist", finalCumHist, 256, 200);
	imshow("Original", img);
	imshow("Eq", eqImg);
	if (waitKey);
	{
		cvWaitKey();
	}
}


void l9e1_generalFilter(Mat& img, bool waitKey)
{
	int kernel[3][3] = { {-1,-1,-1},{-1,9,-1 },{-1,-1,-1} };
	bool lowfilter = true;
	int positivesum = 0;
	int negativesum = 0;

	int scaling = 0;

	for (int i = 0; i < 3; ++i)
	{
		for (int j = 0; j < 3; ++j)
		{
			printf("kernel[%d][%d] = ", i, j), scanf("%d", &kernel[i][j]);
			if (kernel[i][j] < 0)
			{
				lowfilter = false;
				negativesum += std::abs(kernel[i][j]);
			}
			else
			{
				positivesum += kernel[i][j];
			}
		}
	}
	printf("YOUR KERNEL\n");
	for (int i = 0; i < 3; ++i)
	{
		for (int j = 0; j < 3; ++j)
		{
			printf("%4d", kernel[i][j]);
		}
		printf("\n");
	}
	if (lowfilter)
	{
		scaling = positivesum;
	}
	else
	{
		printf("AAAAAAA");
		scaling = 2 * max(positivesum,negativesum);
	}
	printf("Computed scalling arg : %d,%d\n", scaling,positivesum);

	Mat transform;
	transform = img.clone();

	for (int i = 1; i < img.rows - 1; ++i)
	{
		for (int j = 1; j < img.cols - 1; ++j)
		{
			int conv_result = kernel[0][0] * img.data[(i - 1) * img.cols + (j - 1)] +
				kernel[0][1] * img.data[(i - 1) * img.cols + (j)] +
				kernel[0][2] * img.data[(i - 1) * img.cols + (j + 1)] +
				kernel[1][0] * img.data[(i)* img.cols + (j - 1)] +
				kernel[1][1] * img.data[(i)* img.cols + (j)] +
				kernel[1][2] * img.data[(i)* img.cols + (j + 1)] +
				kernel[2][0] * img.data[(i + 1) * img.cols + (j - 1)] +
				kernel[2][1] * img.data[(i + 1) * img.cols + (j)] +
				kernel[2][2] * img.data[(i + 1) * img.cols + (j + 1)];

			if (lowfilter)
			{
				transform.data[i * img.cols + j] = (static_cast<uchar>(static_cast<float>(conv_result) / scaling));
			}
			else
			{
				uchar a = saturate_cast<uchar>(static_cast<uchar>(static_cast<float>(conv_result) / scaling) + (255 / 2));
				
				transform.data[i * img.cols + j] = a;
			}
		}
	}


	imshow("Originnal", img);
	imshow("Transform", transform);
	if (waitKey)
	{
		cvWaitKey();
	}
}

void centering_transform(Mat img) {
	//expects floating point image
	for (int i = 0; i < img.rows; i++) {
		for (int j = 0; j < img.cols; j++) {
			img.at<float>(i, j) = ((i + j) & 1) ? -img.at<float>(i, j) : img.at<float>(i, j);
		}
	}
}

Mat generic_frequency_domain_filter(Mat src) {
	//convert input image to float image
	Mat srcf;
	src.convertTo(srcf, CV_32FC1);
	//centering transformation
	centering_transform(srcf);
	//perform forward transform with complex image output
	Mat fourier;
	dft(srcf, fourier, DFT_COMPLEX_OUTPUT);
	//split into real and imaginary channels
	Mat channels[] = { Mat::zeros(src.size(), CV_32F), Mat::zeros(src.size(), CV_32F) };
	split(fourier, channels); // channels[0] = Re(DFT(I)), channels[1] = Im(DFT(I))
							  //calculate magnitude and phase in floating point images mag and phi
	
	

	Mat mag, phi;
	magnitude(channels[0], channels[1], mag);
	phase(channels[0], channels[1], phi);
	//display the phase and magnitude images here

	mag += Scalar::all(1);
	log(mag, mag);
	//log(phi, phi);

	Mat p, m;
	normalize(mag, m, 0, 255, NORM_MINMAX, CV_8UC1);
	normalize(phi, p, 0, 255, NORM_MINMAX, CV_8UC1);

	imshow("Phase", p);
	imshow("Mag", m);
	// ......


	//insert filtering operations on Fourier coefficients here
	// ......
	
	//store in real part in channels[0] and imaginary part in channels[1]
	split(fourier, channels);
	// ......




	//perform inverse transform and put results in dstf Mat dst, dstf;

	for (int i = 0; i < channels[0].rows; ++i)
	{
		for (int j = 0; j < channels[0].cols; ++j)
		{

			float poz = (static_cast<float>(channels[0].rows) / 2 - i) * (static_cast<float>(channels[0].rows) / 2 - i) + (static_cast<float>(channels[0].cols) / 2 - j) * (static_cast<float>(channels[0].cols) / 2 - j);
			//printf("poz = %f\n", poz);
			if (poz > 100)
			{
				channels[0].data[i * channels[0].cols + j] = 0;
				channels[1].data[i * channels[1].cols + j] = 0;
			}
			/*float coef = exp(-poz / 100);
			channels[0].data[i * channels[0].cols + j] = channels[0].data[i * channels[0].cols + j] * coef;
			channels[1].data[i * channels[0].cols + j] = channels[1].data[i * channels[0].cols + j] * coef;*/

		}
	}
	Mat dst, dstf;
	merge(channels, 2, fourier);

	dft(fourier, dstf, DFT_INVERSE | DFT_REAL_OUTPUT | DFT_SCALE);

	//inverse centering transformation 
	centering_transform(dstf);

	//normalize the result and put in the destination image 
	normalize(dstf, dst, 0, 255, NORM_MINMAX, CV_8UC1);
	imshow("DST", dst);
	cvWaitKey();

	return dst;
}


void l10e1_medianFilter(Mat& img, bool waitKey)
{
	int w;
	printf("Introduceti dimensiunea filtrului(3,5 sau 7): "), scanf("%d", &w);

	int padding = w / 2;

	Mat filtered = img.clone();
	double t = (double)getTickCount();
	for (int i = padding; i < img.rows - padding; ++i)
	{
		for (int j = padding; j < img.cols - padding; ++j)
		{
			std::vector<int> values;
			for (int ki = i - padding; ki <= i + padding; ++ki)
			{
				for (int kj = j - padding; kj <= j + padding; ++kj)
				{
					values.push_back(img.data[ki * img.cols + kj]);
				}
			}
			int median = values.size() / 2;
			std::sort(values.begin(), values.end());
			filtered.data[i * img.cols + j] = values.at(median);
		}
	}
	t = ((double)getTickCount() - t) / getTickFrequency();
	printf("Time = %.3f [ms]\n", t * 1000);
	imshow("Original", img);
	imshow("Noise", filtered);
	if (waitKey)
	{
		cvWaitKey();
	}
}


void l10e2_gaussianFilter(Mat& img, bool waitKey)
{
	int w;
	printf("Introduceti dimensiunea filtrului(3,5 sau 7): "), scanf("%d", &w);
	int padding = w / 2;

	float sigma = static_cast<float>(w) / 6;
	std::cout << "Sigma " << sigma << "\n";
	Mat kernel(w, w, CV_32F);

	float sigma2 = 2 * sigma * sigma;

	float sum = 0;

	for (int i = 0; i < w; ++i)
	{
		for (int j = 0; j < w; ++j)
		{
			float gxy = 1 / (PI * sigma2) * exp(-(pow((i - padding), 2) + pow(j - padding,2)) / sigma2);
			kernel.at<float>(i,j) = gxy;
			sum += gxy;
		}
	}
	for (int i = 0; i < w; ++i)
	{
		for (int j = 0; j < w; ++j)
		{
			printf("%8.4f", kernel.at<float>(i, j));
		}
		printf("\n");
	}
	printf("S = %.4f\n", sum);

	Mat dst = img.clone();
	Point anchor;
	double delta;
	int ddepth;
	anchor = Point(-1, -1);
	delta = 0;
	ddepth = -1;
	double t = (double)getTickCount();
	filter2D(img, dst, ddepth, kernel, anchor, delta, BORDER_DEFAULT);
	t = ((double)getTickCount() - t) / getTickFrequency();
	printf("Time = %.3f [ms]\n", t * 1000);
	imshow("original", img);
	imshow("Filtered", dst);
	if (waitKey)
	{
		cvWaitKey();
	}
}

void l10e2_gaussianFilter2(Mat& img,bool waitKey)
{
	int w;
	printf("Introduceti dimensiunea filtrului(3,5 sau 7): "), scanf("%d", &w);
	int padding = w / 2;

	float sigma = static_cast<float>(w) / 6;
	float sigma2 = 2 * sigma * sigma;
	float pisigma = sqrt(2 * PI) * sigma;

	std::cout << "Sigma " << sigma << "\n";

	Mat kernelx(1, w, CV_32F);
	Mat kernely(w, 1, CV_32F);

	for (int i = 0; i < w; ++i)
	{
		kernelx.at<float>(0, i) = 1 / pisigma * exp(-(pow(i - padding, 2)/sigma2));
		kernely.at<float>(i, 0) = 1 / pisigma * exp(-(pow(i - padding, 2) / sigma2));
	}
	for (int i = 0; i < w; ++i)
	{
		printf("%8.4f", kernelx.at<float>(0, i));
	}
	printf("\n");
	for (int i = 0; i < w; ++i)
	{
		printf("%8.4f", kernely.at<float>(i, 0));
	}
	printf("\n");

	Mat dsty = img.clone();
	Mat dstx = img.clone();
	Point anchor;
	double delta;
	int ddepth;
	anchor = Point(-1, -1);
	delta = 0;
	ddepth = -1;
	double t = (double)getTickCount();

	filter2D(img,dsty, ddepth, kernely, anchor, delta, BORDER_DEFAULT);
	filter2D(dsty, dstx, ddepth, kernelx, anchor, delta, BORDER_DEFAULT);

	t = ((double)getTickCount() - t) / getTickFrequency();
	printf("Time = %.3f [ms]\n", t * 1000);

	imshow("original", img);
	imshow("After Gy", dsty);
	imshow("After Gx", dstx);
	if (waitKey)
	{
		cvWaitKey();
	}
}

void l11e1_xyderivatives(Mat& img, bool waitKey)
{
	Point anchor;
	double delta;
	int ddepth;
	anchor = Point(-1, -1);
	delta = 0;
	ddepth = -1;


	Mat wtf(img.rows, img.cols, CV_32F);
	img.convertTo(wtf, CV_32F);

	//------------------------------------------------------------------------prewitt
	float pv[] = { -1,0,1,-1,0,1,-1,0,1 };
	float ph[] = { 1,1,1,0,0,0,-1,-1,-1 };
 	Mat prewittx(3, 3, CV_32F, pv);
	Mat prewitty(3, 3, CV_32F, ph);

	Mat prewittdstx(img.rows,img.cols,CV_32F);
	Mat prewittdsty(img.rows,img.cols,CV_32F);

	Mat prewittfilteredx = img.clone();
	Mat prewittfilteredy = img.clone();
	Mat prewittmagnitude = img.clone();

	filter2D(wtf, prewittdstx, ddepth, prewittx, anchor, delta, BORDER_DEFAULT);
	filter2D(wtf, prewittdsty, ddepth, prewitty, anchor, delta, BORDER_DEFAULT);
	filter2D(img, prewittfilteredx, ddepth, prewittx, anchor, delta, BORDER_DEFAULT);
	filter2D(img, prewittfilteredy, ddepth, prewitty, anchor, delta, BORDER_DEFAULT);
	
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			prewittmagnitude.data[i * img.cols + j] = saturate_cast<uchar>(sqrt(pow(prewittdstx.at<float>(i, j), 2) + pow(prewittdsty.at<float>(i, j), 2))) / 4.2;
		}
	}



	//-------------------------------------------------------------------sobel
	float sv[] = { -1,0,1,-2,0,2,-1,0,1 };
	float sh[] = { 1,2,1,0,0,0,-1,-2,-1 };
	Mat sobelx(3, 3, CV_32F, sv);
	Mat sobely(3, 3, CV_32F, sh);

	Mat sobeldstx(img.rows,img.cols,CV_32F);
	Mat sobeldsty(img.rows, img.cols, CV_32F);
	
	Mat sobelfilteredx = img.clone();
	Mat sobelfilteredy = img.clone();
	Mat sobelmagnitude = img.clone();


	filter2D(wtf, sobeldstx, ddepth, sobelx, anchor, delta, BORDER_DEFAULT);
	filter2D(wtf, sobeldsty, ddepth, sobely, anchor, delta, BORDER_DEFAULT);



	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			sobelmagnitude.data[i * img.cols + j] = sqrt(pow(sobeldstx.at<float>(i,j), 2) + pow(sobeldsty.at<float>(i, j), 2)) / 5.65 ;
			//sobelfilteredx.data[i * img.cols + j] = sobeldstx.at<float>(i, j) / 16;
			//sobelfilteredy.data[i * img.cols + j] = sobeldsty.at<float>(i, j) / 16;
		}
	}
	filter2D(img, sobelfilteredx, ddepth, sobelx, anchor, delta, BORDER_DEFAULT);
	filter2D(img, sobelfilteredy, ddepth, sobely, anchor, delta, BORDER_DEFAULT);
	//---------------------------------------------------------------------------roberts
	float rv[] = { 1,0,0,-1 };
	float rh[] = { 0,-1,1,0 };
	Mat robertsx(2, 2, CV_32F, rv);
	Mat robertsy(2, 2, CV_32F, rh);

	Mat robertsdstx(img.rows,img.cols,CV_32F);
	Mat robertsdsty(img.rows,img.cols,CV_32F);
	Mat robertsfilteredx = img.clone();
	Mat robertsfilteredy = img.clone();
	Mat robertsmagnitude = img.clone();

	
	
	filter2D(img, robertsfilteredx, ddepth, robertsx, anchor, delta, BORDER_DEFAULT);
	filter2D(img, robertsfilteredy, ddepth, robertsy, anchor, delta, BORDER_DEFAULT);
	filter2D(wtf, robertsdstx, ddepth, robertsx, anchor, delta, BORDER_DEFAULT);
	filter2D(wtf, robertsdsty, ddepth, robertsy, anchor, delta, BORDER_DEFAULT);

	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			robertsmagnitude.data[i * img.cols + j] = sqrt(pow(robertsdstx.at<float>(i, j), 2) + pow(robertsdsty.at<float>(i, j), 2));
		}
	}

	int tresholdsobel,tresholdroberts,tresholdprewitt;
	printf("Introdu un treshold pt sobel: "), scanf("%d", &tresholdsobel);
	printf("Introdu un treshold pt roberts : "), scanf("%d", &tresholdroberts);
	printf("Introdu un treshold pt prewitt: "), scanf("%d", &tresholdprewitt);
	Mat& tresholdedsobel = img.clone();
	Mat& tresholdedprewitt = img.clone();
	Mat& tresholdedroberts = img.clone();
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (sobelmagnitude.at<uchar>(i, j) < tresholdsobel)
			{
				tresholdedsobel.at<uchar>(i, j) = 0;
			}
			else
			{
				tresholdedsobel.at<uchar>(i, j) = sobelmagnitude.at<uchar>(i, j);
			}

			if (robertsmagnitude.at<uchar>(i, j) < tresholdroberts)
			{
				tresholdedroberts.at<uchar>(i, j) = 0;
			}
			else
			{
				tresholdedroberts.at<uchar>(i, j) = robertsmagnitude.at<uchar>(i, j);
			}

			if (prewittmagnitude.at<uchar>(i, j) < tresholdprewitt)
			{
				tresholdedprewitt.at<uchar>(i, j) = 0;
			}
			else
			{
				tresholdedprewitt.at<uchar>(i, j) = prewittmagnitude.at<uchar>(i, j);
			}
		}
	}

	imshow("Original", img);
	imshow("Prewitt x", prewittfilteredx);
	imshow("Prewitt y", prewittfilteredy);
	imshow("Magnitude", prewittmagnitude);

	imshow("Sobel x", sobelfilteredx);
	imshow("Sobel y", sobelfilteredy);
	imshow("Magnitude sobel", sobelmagnitude);

	imshow("Roberts x", robertsfilteredx);
	imshow("Roberts y", robertsfilteredy);
	imshow("Magnitude roberts", robertsmagnitude);

	imshow("Tresholded sobel", tresholdedsobel);
	imshow("Tresholded roberts", tresholdedroberts);
	imshow("Tresholded prewitt", tresholdedprewitt);
	if (waitKey)
	{
		cvWaitKey();
	}
}

void l11_canny(Mat& img, bool waitKey)
{
	//-------------------------------------------------------------------------------applying gaussian filter
	int w;
	printf("Introduceti dimensiunea filtrului(3,5 sau 7): "), scanf("%d", &w);
	int padding = w / 2;

	float sigma = static_cast<float>(w) / 6;
	float sigma2 = 2 * sigma * sigma;
	float pisigma = sqrt(2 * PI) * sigma;

	std::cout << "Sigma " << sigma << "\n";

	Mat kernelx(1, w, CV_32F);
	Mat kernely(w, 1, CV_32F);

	for (int i = 0; i < w; ++i)
	{
		kernelx.at<float>(0, i) = 1 / pisigma * exp(-(pow(i - padding, 2) / sigma2));
		kernely.at<float>(i, 0) = 1 / pisigma * exp(-(pow(i - padding, 2) / sigma2));
	}

	Mat dsty = img.clone();
	Mat dstx = img.clone();
	Point anchor;
	double delta;
	int ddepth;
	anchor = Point(-1, -1);
	delta = 0;
	ddepth = -1;

	filter2D(img, dsty, ddepth, kernely, anchor, delta, BORDER_DEFAULT);
	filter2D(dsty, dstx, ddepth, kernelx, anchor, delta, BORDER_DEFAULT);

	//------------------------------------------------------------------------------applying sobel filter
	float sv[] = { -1,0,1,-2,0,2,-1,0,1 };
	float sh[] = { 1,2,1,0,0,0,-1,-2,-1 };
	Mat sobelx(3, 3, CV_32F, sv);
	Mat sobely(3, 3, CV_32F, sh);

	Mat sobeldstx(dstx.rows, dstx.cols, CV_32F);
	Mat sobeldsty(dstx.rows, dstx.cols, CV_32F);
	Mat sobelmagnitude = dstx.clone();
	Mat sobelorientation = dstx.clone();

	//filter2D(dstx, sobeldstx, ddepth, sobelx, anchor, delta, BORDER_DEFAULT);
	//filter2D(dstx, sobeldsty, ddepth, sobely, anchor, delta, BORDER_DEFAULT);
	int max = 0;

	for (int i = 1; i < img.rows - 1; ++i)
	{
		for (int j = 1; j < img.cols - 1; ++j)
		{
			if (i == 0 || j == 0 || i == img.rows - 1 || j == img.cols - 1)
			{
				sobeldstx.at<float>(i, j) = 0;
			}
			int conv_result = sobelx.at<float>(0, 2) * dstx.data[(i - 1) * dstx.cols + (j + 1)] +
				sobelx.at<float>(1, 2) * dstx.data[(i)* dstx.cols + (j + 1)] +
				sobelx.at<float>(2, 2) * dstx.data[(i + 1) * dstx.cols + (j + 1)] +
				sobelx.at<float>(0, 0) * dstx.data[(i - 1) * dstx.cols + (j - 1)] +
				sobelx.at<float>(1, 0) * dstx.data[(i + 0) * dstx.cols + (j - 1)] +
				sobelx.at<float>(2, 0) * dstx.data[(i + 1) * dstx.cols + (j - 1)];
			sobeldstx.at<float>(i, j) = conv_result;
			conv_result = sobely.at<float>(0, 0) * dstx.data[(i - 1) * dstx.cols + (j - 1)] +
				sobely.at<float>(0, 1) * dstx.data[(i - 1) * dstx.cols + (j + 0)] +
				sobely.at<float>(0, 2) * dstx.data[(i - 1) * dstx.cols + (j + 1)] +
				sobely.at<float>(2, 0) * dstx.data[(i + 1) * dstx.cols + (j - 1)] +
				sobely.at<float>(2, 1) * dstx.data[(i + 1) * dstx.cols + (j + 0)] +
				sobely.at<float>(2, 2) * dstx.data[(i + 1) * dstx.cols + (j + 1)];
			sobeldsty.at<float>(i, j) = conv_result;
		}
	}


	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			int dir;
			sobelmagnitude.data[i * img.cols + j] = sqrt(pow(sobeldstx.at<float>(i, j), 2) + pow(sobeldsty.at<float>(i, j), 2)) / 5.65;
			float teta = atan2((float)sobeldsty.at<float>(i, j), (float)sobeldstx.at<float>(i, j));
			if ((teta > 3 * PI / 8 && teta < 5 * PI / 8) || (teta > -5 * PI / 8 && teta < -3 * PI / 8)) dir = 0;
			if ((teta > PI / 8 && teta < 3 * PI / 8) || (teta > -7 * PI / 8 || teta < -5 * PI / 8)) dir = 1;
			if ((teta > -PI / 8 && teta < PI / 8) || teta > 7 * PI / 8 && teta < -7 * PI / 8) dir = 2;
			if ((teta > 5 * PI / 8 && teta < 7 * PI / 8) || (teta > -3 * PI / 8 && teta < -PI / 8)) dir = 3;
			sobelorientation.at<uchar>(i, j) = dir;
		}
	}


	//------------------------------------------------------------------------------------------non maxima supression
	Mat nms = sobelmagnitude.clone();
	for (int i = 1; i < img.rows - 1; ++i)
	{
		for (int j = 1; j < img.cols - 1; ++j)
		{
			int dir = sobelorientation.at<uchar>(i, j);
			switch (dir)
			{
			case 0:
			{
				if (i == 0)
				{
					if (sobelmagnitude.at<uchar>(i, j) < sobelmagnitude.at<uchar>(i + 1, j))
					{
						nms.at<uchar>(i, j) = 0;
					}
				}
				else if (i == img.rows - 1)
				{
					if (sobelmagnitude.at<uchar>(i, j) < sobelmagnitude.at<uchar>(i - 1, j))
					{
						nms.at<uchar>(i, j) = 0;
					}
				}
				else
				{
					if (sobelmagnitude.at<uchar>(i, j) < sobelmagnitude.at<uchar>(i - 1, j) ||
						sobelmagnitude.at<uchar>(i, j) < sobelmagnitude.at<uchar>(i + 1, j)
						)
					{
						nms.at<uchar>(i, j) = 0;
					}
				}
				break;
			}
			case 1:
			{

				if (sobelmagnitude.at<uchar>(i, j) < sobelmagnitude.at<uchar>(i - 1, j + 1) ||
					sobelmagnitude.at<uchar>(i, j) < sobelmagnitude.at<uchar>(i + 1, j - 1)
					)
				{
					nms.at<uchar>(i, j) = 0;
				}
				break;
			}
			case 2:
			{
				if (j == 0)
				{
					if (sobelmagnitude.at<uchar>(i, j) < sobelmagnitude.at<uchar>(i, j + 1))
					{
						nms.at<uchar>(i, j) = 0;
					}
				}
				else if (j == img.cols - 1)
				{
					if (sobelmagnitude.at<uchar>(i, j) < sobelmagnitude.at<uchar>(i, j - 1))
					{
						nms.at<uchar>(i, j) = 0;
					}
				}
				else
				{
					if (sobelmagnitude.at<uchar>(i, j) < sobelmagnitude.at<uchar>(i, j - 1) ||
						sobelmagnitude.at<uchar>(i, j) < sobelmagnitude.at<uchar>(i, j + 1)
						)
					{
						nms.at<uchar>(i, j) = 0;
					}
				}
				break;
			}
			default:
			{
				if (sobelmagnitude.at<uchar>(i, j) < sobelmagnitude.at<uchar>(i - 1, j - 1) ||
					sobelmagnitude.at<uchar>(i, j) < sobelmagnitude.at<uchar>(i + 1, j + 1)
					)
				{
					nms.at<uchar>(i, j) = 0;
				}
				break;
			}
			}
		}
	}

	//-------------------------------------------------------------------------------------------histogram compute
	int hist[256];
	for (int i = 0; i < 255; ++i)
	{
		hist[i] = 0;
	}
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			hist[nms.data[i * img.cols + j]]++;
		}
	}
	showHistogram("Histograma", hist, 256, 200);
	float p = 0.1, k = 0.4;
	float nrnonmuchie = (1 - p) * (nms.rows * nms.cols - hist[0]);
	int pragadaptiv;
	int sum = 0;
	for (int i = 1; i < 255; i++)
	{
		sum += hist[i];
		if (sum > nrnonmuchie)
		{
			pragadaptiv = i;
			break;
		}

	}
	printf("Pragul adaptiv = %d\n", pragadaptiv);
#define WEAK 128
#define STRONG 255
	float pH = pragadaptiv;
	float pL = k * pH;
	//binarizarea
	Mat binarizat = nms.clone();
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (nms.data[i * img.cols + j] < pL)
			{
				binarizat.data[i * img.cols + j] = 0;
			}
			else if (nms.data[i * img.cols + j] > pH)
			{
				binarizat.data[i * img.cols + j] = STRONG;
			}
			else if (nms.data[i * img.cols + j] < pH && nms.data[i * img.cols + j] > pL)
			{
				binarizat.data[i * img.cols + j] = WEAK;
			}
		}
	}
	//-------------------------------------------------------------------------------------------edge extension
	std::queue<Point> queue;
	Mat visited = Mat::zeros(img.rows, img.cols, CV_8UC1);

	int ki[] = { -1,-1,-1,0,0,1,1,1 };
	int kj[] = { -1,0,1,-1,1,-1,0,1 };

	Mat extension = binarizat.clone();

	for (int i = 1; i < binarizat.rows - 1; ++i)
	{
		for (int j = 1; j < binarizat.cols - 1; ++j)
		{
			if (binarizat.data[i * binarizat.cols + j] == STRONG &&
				visited.data[i * binarizat.cols + j] == 0
				)
			{
				queue.push(Point(i, j));
				while (!queue.empty())
				{
					Point oldest = queue.front();
					queue.pop();
					visited.at<uchar>(oldest.x, oldest.y) = 1;
					int x = oldest.x;
					int y = oldest.y;
					for (int k = 0; k < 8; ++k)
					{
						if (binarizat.data[(x + ki[k]) * binarizat.cols + (y + kj[k])] == WEAK && visited.data[(x + ki[k]) * binarizat.cols + (y + kj[k])] == 0)
						{
							extension.data[(x + ki[k]) * binarizat.cols + (y + kj[k])] = STRONG;
							queue.push(Point(x + ki[k], y + kj[k]));
						}
					}
				}
			}
		}
	}
	for (int i = 0; i < binarizat.rows; ++i)
	{
		for (int j = 0; j < binarizat.cols; ++j)
		{
			if (extension.data[i * img.cols + j] == WEAK)
			{
				extension.data[i * img.cols + j] = 0;
			}
		}
	}
	//------------------------------------------------------------------------------------------visualize only

	imshow("Original", img);
	imshow("Gauss filter", dstx);
	imshow("Sobel magnitude", sobelmagnitude);
	imshow("Nms", nms);
	imshow("Binarizat", binarizat);
	imshow("Extension", extension);
	if (waitKey)
	{
		cvWaitKey();
	}
}


#define DENSITY_TRESHOLD 0.45
#define TRESHOLD 60
#define MIN_AREA 1000
#define MAX_AREA 15000

#define MIN_ASPECT_RATIO 1
#define MAX_ASPECT_RATIO 6

#define ANGLE_TRESHOLD 7

Mat lpr_gaussian(Mat& img)
{
	int w = 5;
	int padding = w / 2;

	float sigma = static_cast<float>(w) / 6;
	float sigma2 = 2 * sigma * sigma;
	float pisigma = sqrt(2 * PI) * sigma;

	std::cout << "Sigma " << sigma << "\n";

	Mat kernelx(1, w, CV_32F);
	Mat kernely(w, 1, CV_32F);

	for (int i = 0; i < w; ++i)
	{
		kernelx.at<float>(0, i) = 1 / pisigma * exp(-(pow(i - padding, 2) / sigma2));
		kernely.at<float>(i, 0) = 1 / pisigma * exp(-(pow(i - padding, 2) / sigma2));
	}

	Mat dsty = img.clone();
	Mat dstx = img.clone();
	Point anchor;
	double delta;
	int ddepth;
	anchor = Point(-1, -1);
	delta = 0;
	ddepth = -1;

	filter2D(img, dsty, ddepth, kernely, anchor, delta, BORDER_DEFAULT);
	filter2D(dsty, dstx, ddepth, kernelx, anchor, delta, BORDER_DEFAULT);

	return dstx;
}

Mat lpr_soble(Mat& img)
{
	float sv[] = { -1,0,1,-2,0,2,-1,0,1 };
	float sh[] = { 1,2,1,0,0,0,-1,-2,-1 };
	Mat sobelx(3, 3, CV_32F, sv);
	Mat sobely(3, 3, CV_32F, sh);

	Mat sobeldstx(img.rows, img.cols, CV_32F);
	Mat sobeldsty(img.rows, img.cols, CV_32F);
	Mat sobelmagnitude = img.clone();
	Mat sobelorientation = img.clone();

	Point anchor;
	double delta;
	int ddepth;
	anchor = Point(-1, -1);
	delta = 0;
	ddepth = -1;

	filter2D(img, sobeldstx, ddepth, sobelx, anchor, delta, BORDER_DEFAULT);
	//filter2D(dstx, sobeldsty, ddepth, sobely, anchor, delta, BORDER_DEFAULT);
	/*int max = 0;

	for (int i = 1; i < img.rows - 1; ++i)
	{
		for (int j = 1; j < img.cols - 1; ++j)
		{
			if (i == 0 || j == 0 || i == img.rows - 1 || j == img.cols - 1)
			{
				sobeldstx.at<float>(i, j) = 0;
				sobeldsty.at<float>(i, j) = 0;
			}
			int conv_result = sobelx.at<float>(0, 2) * img.data[(i - 1) * img.cols + (j + 1)] +
				sobelx.at<float>(1, 2) * img.data[(i)* img.cols + (j + 1)] +
				sobelx.at<float>(2, 2) * img.data[(i + 1) * img.cols + (j + 1)] +
				sobelx.at<float>(0, 0) * img.data[(i - 1) * img.cols + (j - 1)] +
				sobelx.at<float>(1, 0) * img.data[(i + 0) * img.cols + (j - 1)] +
				sobelx.at<float>(2, 0) * img.data[(i + 1) * img.cols + (j - 1)];
			sobeldstx.at<float>(i, j) = conv_result;
			conv_result = sobely.at<float>(0, 0) * img.data[(i - 1) * img.cols + (j - 1)] +
				sobely.at<float>(0, 1) * img.data[(i - 1) * img.cols + (j + 0)] +
				sobely.at<float>(0, 2) * img.data[(i - 1) * img.cols + (j + 1)] +
				sobely.at<float>(2, 0) * img.data[(i + 1) * img.cols + (j - 1)] +
				sobely.at<float>(2, 1) * img.data[(i + 1) * img.cols + (j + 0)] +
				sobely.at<float>(2, 2) * img.data[(i + 1) * img.cols + (j + 1)];
			sobeldsty.at<float>(i, j) = conv_result;
		}
	}
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			sobelmagnitude.data[i * img.cols + j] = sqrt(pow(sobeldstx.at<float>(i, j), 2) + pow(sobeldsty.at<float>(i, j), 2)) / 5.65;
		}
	}*/
	return sobeldstx;
}

Mat lpr_treshold(Mat& img)
{
	Mat tresh = img.clone();
	uchar treshold = TRESHOLD;
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (img.data[i * img.cols + j] < treshold)
			{
				tresh.data[i * img.cols + j] = 0;
			}
		}
	}
	return tresh;
}

Mat lpr_closing(Mat& img)
{
	int kernel_i = 7;
	int kernel_j = 19;

	int padding_i = kernel_i / 2;
	int padding_j = kernel_j / 2;

	Mat dilatation = img.clone();

	for (int i = padding_i; i < img.rows - padding_i; ++i)
	{
		for (int j = padding_j; j < img.cols - padding_j; ++j)
		{
			if (img.data[i * img.cols + j] >= TRESHOLD)
			{
				dilatation.data[i * img.cols + j] = 255;
				for (int ki = i - padding_i; ki <= i + padding_i; ++ki)
				{
					for (int kj = j - padding_j; kj <= j + padding_j; ++kj)
					{
						dilatation.data[ki * img.cols + kj] = 255;
					}
				}
			}
		}
	}

	Mat erosion = dilatation.clone();
	for (int i = padding_i; i < img.rows - padding_i; ++i)
	{
		for (int j = padding_j; j < img.cols - padding_j; ++j)
		{
			if (dilatation.data[i * img.cols + j] == 255)
			{
				bool erode = false;
				for (int ki = i - padding_i; ki <= i + padding_i; ++ki)
				{
					for (int kj = j - padding_j; kj <= j + padding_j; ++kj)
					{
						if (dilatation.data[ki * img.cols + kj] == 0)
						{
							erode = true;
							break;
						}
					}
				}

				if (erode)
				{
					erosion.data[i * img.cols + j] = 0;
				}
			}
		}
	}
	//imshow("Dilatare", dilatation);
	//imshow("Eroziune", erosion);
	return erosion;
}

std::vector<RotatedRect> lpr_boundingBoxes(Mat& img)
{
	Mat contours;
	cvtColor(img, contours, CV_GRAY2RGB);
	std::vector<std::vector<Point>> contoursarray;
	std::vector<RotatedRect> rectcontours;
	findContours(img, contoursarray, RETR_EXTERNAL, CHAIN_APPROX_NONE);



	for (int i = 0; i < contoursarray.size(); ++i)
	{
		//std::cout << contoursarray.at(i) << std::endl;
		
		//drawContours(contours, contoursarray, i,Scalar(0,0, 255),1);
		RotatedRect box = minAreaRect(contoursarray.at(i));
		rectcontours.push_back(box);
		Point2f rect_points[4];
		box.points(rect_points);
		for (int j = 0; j < 4; j++)
			line(contours, rect_points[j], rect_points[(j + 1) % 4], Scalar(0, 0, 255), 2, 8);
	}
	imshow("All bounding boxes",contours);
	return rectcontours;
}

std::vector<RotatedRect> lpr_filterContours(std::vector<RotatedRect>& contours)
{
	std::vector<RotatedRect> filtered;
	for (auto rect : contours)
	{
		float area = rect.size.height * rect.size.width;
		if (area <= MIN_AREA || area >= MAX_AREA)
		{
			continue;
		}

		float width = max(rect.size.width, rect.size.height);
		float height= min(rect.size.width, rect.size.height);


		float aspect_ration = width / height;
		if (aspect_ration < MIN_ASPECT_RATIO || aspect_ration > MAX_ASPECT_RATIO)
		{
			continue;
		}
		
		filtered.push_back(rect);
	}

	return filtered;
}

std::vector<float> densities;

std::vector<RotatedRect> lpr_edgeDensity(std::vector<RotatedRect>& contours,Mat& img)
{
	int rows = img.rows;
	int cols = img.cols;
	std::vector<RotatedRect> filtered;

	std::vector<std::vector<int>> boxes;
	densities.clear();
	for (int i = 0; i < contours.size(); ++i)
	{
		Point2f rect_points[4];
		contours.at(i).points(rect_points);
		int mini = 5000;
		int minj = 5000;
		int maxi = -5000;
		int maxj = -5000;

		for (int j = 0; j < 4; j++)
		{
			if (rect_points[j].y < mini)
			{
				mini = rect_points[j].y;
			}
			if (rect_points[j].y > maxi)
			{
				maxi = rect_points[j].y;
			}
			if (rect_points[j].x < minj)
			{
				minj = rect_points[j].x;
			}
			if (rect_points[j].x > maxj)
			{
				maxj = rect_points[j].x;
			}
		}

		if (
			mini < 0 || mini > rows ||
			maxi < 0 || maxi > rows ||
			minj < 0 || minj > cols ||
			maxj < 0 || maxj> cols 
			)
		{
			continue;
		}
		//for (int j = 0; j < 4; j++)
		//	std::cout << rect_points[j]<<std::endl;
		//
		//printf("hight %f\n", contours.at(i).size.height);
		//printf("width %f\n", contours.at(i).size.width);
		//printf("mini %d\n", mini);
		//printf("maxi %d\n", maxi);
		//printf("minj %d\n", minj);
		//printf("maxj %d\n", maxj);
		//printf("----------------------------------------------------\n");
		filtered.push_back(contours.at(i));
		std::vector<int> box;
		box.push_back(mini);
		box.push_back(maxi);
		box.push_back(minj);
		box.push_back(maxj);

		boxes.push_back(box);
	}
	int i = 0;

	for (auto box : boxes)
	{
		int mini = box.at(0);
		int maxi = box.at(1);
		int minj = box.at(2);
		int maxj = box.at(3);

		int total = 0;
		int whites = 0;
		for (int i = mini; i < maxi; ++i)
		{
			for (int j = minj; j < maxj; ++j)
			{
				total += 1;
				if (img.at<uchar>(i, j) == 255)
				{
					whites += 1;
				}
			}
		}

		//printf("mini %d\n", mini);
		//printf("maxi %d\n", maxi);
		//printf("minj %d\n", minj);
		//printf("maxj %d\n", maxj);

		//printf("hight %f\n", contours.at(i).size.height);
		//printf("width %f\n", contours.at(i).size.width);

		densities.push_back(static_cast<float>(whites) / (filtered.at(i).size.area()));
		//printf("Box 1 : %f %d %f\n", filtered.at(i).size.height * filtered.at(i).size.width, whites, static_cast<float>(whites) / (filtered.at(i).size.area()));
		i++;
	}

	return filtered;
}

typedef struct LPR_CLUSTER_
{
	int id;

	Point2f center;
	float area;
	float perimeter;

	int mini;
	int maxi;
	int minj;
	int maxj;

	float axis;

	RotatedRect contur;

	bool operator==(const LPR_CLUSTER_& o)
	{
		return this->id == o.id;
	}

}LPR_CLUSTER;

std::vector<LPR_CLUSTER> lpr_extractProps(Mat& img, std::vector<RotatedRect>& contours)
{
	Mat fill = img.clone();
	std::queue<Point> queue;

	Mat visited = Mat::zeros(img.rows, img.cols, CV_8UC1);

	int ki[] = { -1,0,0,1 };
	int kj[] = { 0,-1,1,0 };

	for (auto c : contours)
	{
		Point2f center = c.center;
		Point pixel((int)center.y, (int)center.x);

		queue.push(pixel);

		while (!queue.empty())
		{
			Point current = queue.front();
			queue.pop();

			int ci = current.x;
			int cj = current.y;

			visited.at<uchar>(ci, cj) = 1;
			fill.at<uchar>(ci, cj) = 0;

			for (int k = 0; k < 4; ++k)
			{
				if (img.at<uchar>(ci + ki[k], cj + kj[k]) == 255 && visited.at<uchar>(ci + ki[k], cj + kj[k]) == 0)
				{
					queue.push(Point(ci + ki[k], cj + kj[k]));
					visited.at<uchar>(ci + ki[k], cj + kj[k]) = 1;
				}
			}

		}
	}
	
	visited = Mat::zeros(img.rows, img.cols, CV_8UC1);

	Mat clusters = Mat::zeros(img.rows, img.cols, CV_8UC1);

	int contur = 0;
	int cluster_id = 1;

	std::vector<LPR_CLUSTER> lpr_clusters;

	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			if (fill.at<uchar>(i, j) == 0 && visited.at<uchar>(i, j) == 0)
			{
				Point pixel(i, j);
				queue.push(pixel);

				float perimeter = 0;
				float area = 0;

				float den = 0;
				float nom1 = 0;
				float nom2 = 0;

				int mini = 50000;
				int maxi = -50000;
				int minj = 50000;
				int maxj = -50000;

				Point contur_center = contours.at(contur).center;
				RotatedRect contur;
				float current_min_length = 1351351;

				for (auto c : contours)
				{
					Point2f center = c.center;
					Point2f mypoint(j, i);

					float length = sqrt((center.x - mypoint.x) * (center.x - mypoint.x) + (center.y - mypoint.y)*(center.y - mypoint.y));
					if (length < current_min_length)
					{
						current_min_length = length;
						contur_center = c.center;
						contur = c;
					}
				}

				

				while (!queue.empty())
				{
					Point currentp = queue.front();
					queue.pop();

					float ci = currentp.x;
					float cj = currentp.y;

					if (cj < minj)
					{
						minj = cj;
					}
					if (cj > maxj)
					{
						maxj = cj;
					}
					if (ci < mini)
					{
						mini = ci;
					}
					if (ci > mini)
					{
						mini = ci;
					}

					visited.at<uchar>(ci, cj) = 1;
					clusters.at<uchar>(ci, cj) = cluster_id;

					area += 1;

					den += (ci - contur_center.y) * (cj - contur_center.x);
					nom1 += (cj - contur_center.x) * (cj - contur_center.x);
					nom2 += (ci - contur_center.y) * (ci - contur_center.y);

					for (int k = 0; k < 4; ++k)
					{
						if ((ci + ki[k]) < 0 || (ci + ki[k]) >= img.rows || (cj + kj[k]) < 0 || (cj + kj[k]) >= img.cols) continue;
						if (fill.at<uchar>(ci + ki[k], cj + kj[k]) == 255)
						{
							perimeter += 1;
						}

						if (fill.at<uchar>(ci + ki[k], cj + kj[k]) == 0 && visited.at<uchar>(ci + ki[k], cj + kj[k]) == 0)
						{
							queue.push(Point(ci + ki[k], cj + kj[k]));
							visited.at<uchar>(ci + ki[k], cj + kj[k]) = 1;
						}
					}
				}

				LPR_CLUSTER lprcluster;
				lprcluster.area = area;
				lprcluster.perimeter = perimeter;
				lprcluster.id = cluster_id++;
				lprcluster.center = contur_center;
				lprcluster.contur = contur;
				lprcluster.axis = atan2(static_cast<double>(den), static_cast<double>(nom1 - nom2)) / 2;

				lprcluster.mini = mini;
				lprcluster.maxi = maxi;
				lprcluster.minj = minj;
				lprcluster.maxj = maxj;

				lpr_clusters.push_back(lprcluster);
			}
		}
	}

	for (auto c : lpr_clusters)
	{
		printf("Id : %d\nArea: %f\nPerimetru: %f\nCentru: (%f,%f)\nElongatie: %f - %f\n\n", c.id, c.area, c.perimeter, c.center.y, c.center.x, c.axis,c.axis * 180 / PI);
	}
	

	//imshow("Fill", fill);
	//imshow("Cluster", clusters);
	return lpr_clusters;
}

std::vector<LPR_CLUSTER> lpr_finalfilters(Mat& img,std::vector<LPR_CLUSTER>& clusters)
{
	std::vector<LPR_CLUSTER> filtered;
	for (auto c : clusters)
	{
		if (c.area == 1)
		{
			continue;
		}
		else
		{
			float angle = c.axis * 180 / PI;
			if (std::abs(angle) > ANGLE_TRESHOLD)
			{
				continue;
			}
			else
			{
				float imgcenter = img.rows / 3;
				if (c.center.y < imgcenter)
				{
					continue;
				}
				filtered.push_back(c);
			}
		}
	}

	return filtered;
}

std::vector<LPR_CLUSTER> lpr_pickOne(Mat& img, std::vector<LPR_CLUSTER>& clusters)
{
	LPR_CLUSTER min_height_cluster = clusters.at(0);

	std::vector<LPR_CLUSTER> filtered;

	for (auto c : clusters)
	{
		if (min_height_cluster.center.y <= c.center.y)
		{
			min_height_cluster = c;
		}
	}
	printf("------------------------------------------------------\n");
	printf("Min height cluster : %d , center(%f,%f)\n", min_height_cluster.id, min_height_cluster.center.y, min_height_cluster.center.x);

	filtered.push_back(min_height_cluster);

	for (auto c : clusters)
	{
		if (!(c == min_height_cluster))
		{
			if ((c.center.x > min_height_cluster.minj && c.center.x < min_height_cluster.maxj))
			{
				continue;
			}
			filtered.push_back(c);
		}
	}

	return filtered;
}

void lpr(Mat& img)
{
	//-------------------------------------STEP 1 - rgb to gray scale
	Mat gray;
	cvtColor(img, gray, CV_RGB2GRAY);

	//-------------------------------------STEP 2 - gaussian filter 5x5
	Mat gauss = lpr_gaussian(gray);

	//-------------------------------------STEP 3 - sobel filter for vertical edges
	Mat sobel = lpr_soble(gauss);

	//-------------------------------------STEP 4 - tresholding
	Mat tresh = lpr_treshold(sobel);

	//-------------------------------------STEP 5 - morphological closing
	Mat closed = lpr_closing(tresh);


	//-------------------------------------STEP 6 - find external contours
	std::vector<RotatedRect> contours = lpr_boundingBoxes(closed);

	//-------------------------------------STEP 7 - filter contours
	contours = lpr_filterContours(contours);
	std::cout << "Remain " << contours.size() << std::endl;
	Mat filter;
	cvtColor(gray, filter, CV_GRAY2RGB);
	for (int i = 0; i < contours.size(); ++i)
	{
		Point2f rect_points[4];
		contours.at(i).points(rect_points);

		for (int j = 0; j < 4; j++)
		{
			line(filter, rect_points[j], rect_points[(j + 1) % 4], Scalar(0, 0, 255), 2, 8);
		}
		//float aspect_ration = contours.at(i).size.width / contours.at(i).size.height;
		//printf("angle %f \n", contours.at(i).angle);1234
	}

	//------------------------------------------STEP 8 - edge density
	Mat density;
	Mat finalimg;
	Mat picked;
	cvtColor(gray, density, CV_GRAY2RGB);

	finalimg = density.clone();
	picked = density.clone();

	contours = lpr_edgeDensity(contours, closed);

	std::vector<RotatedRect> filteredContours;

	for (int i = 0; i < contours.size(); ++i)
	{
		Point2f rect_points[4];
		contours.at(i).points(rect_points);

		Scalar color(0, 0, 255);
		if (densities.at(i) > DENSITY_TRESHOLD)
		{
			color = Scalar(0, 255, 0);
			filteredContours.push_back(contours.at(i));
		}

		for (int j = 0; j < 4; j++)
		{
			line(density, rect_points[j], rect_points[(j + 1) % 4], color, 2, 8);
		}

		std::ostringstream ss;
		ss << densities.at(i);
		putText(density, std::string(ss.str()), contours.at(i).center, cv::FONT_HERSHEY_COMPLEX, 0.5, color, 1, CV_AA);
		//printf("angle %f \n", contours.at(i).angle);
	}

	//---------------------------------------------------------------properties
	Mat wtf = gray.clone();
	for (int i = 0; i < img.rows; ++i)
	{
		for (int j = 0; j < img.cols; ++j)
		{
			Vec3b p = density.at<Vec3b>(i, j);
			if (p == Vec3b(0, 255, 0))
			{
				wtf.at<uchar>(i, j) = 0;
				continue;
			}
			wtf.at<uchar>(i, j) = 255;
		}
	}
	
	std::vector<LPR_CLUSTER> lpr_clusters =  lpr_extractProps(wtf, filteredContours);
	//------------------------------------------------------------------------------------final filter
	lpr_clusters = lpr_finalfilters(img, lpr_clusters);
	for (int i = 0; i < lpr_clusters.size(); ++i)
	{
		Point2f rect_points[4];
		lpr_clusters.at(i).contur.points(rect_points);


		Scalar color(0, 255, 0);

		for (int j = 0; j < 4; j++)
		{
			line(finalimg, rect_points[j], rect_points[(j + 1) % 4], color, 2, 8);
		}

		//std::ostringstream ss;
		//ss << densities.at(i);
		//putText(finalimg, std::string(ss.str()), contours.at(i).center, cv::FONT_HERSHEY_COMPLEX, 0.5, color, 1, CV_AA);
		//printf("angle %f \n", contours.at(i).angle);
	}
	if (lpr_clusters.size() > 1)
	{
		lpr_clusters = lpr_pickOne(img, lpr_clusters);
		for (int i = 0; i < lpr_clusters.size(); ++i)
		{
			Point2f rect_points[4];
			lpr_clusters.at(i).contur.points(rect_points);


			Scalar color(0, 255, 0);

			for (int j = 0; j < 4; j++)
			{
				line(picked, rect_points[j], rect_points[(j + 1) % 4], color, 2, 8);
			}
			imshow("Picked nr", picked);
			//std::ostringstream ss;
			//ss << densities.at(i);
			//putText(finalimg, std::string(ss.str()), contours.at(i).center, cv::FONT_HERSHEY_COMPLEX, 0.5, color, 1, CV_AA);
			//printf("angle %f \n", contours.at(i).angle);
		}

	}

	

	//---------------------------------------------------------------VISU

	

	//imshow("Original", img);
	//imshow("Gray scale image", gray);
	//imshow("Gauss filtered", gauss);
	//imshow("Sobel", sobel);
	//imshow("Tresholded", tresh);
	//imshow("Closing", closed);
	//imshow("Contours", filter);
	imshow("Density", density);
	imshow("Final", finalimg);
	//imshow("WTF", wtf);
	cvWaitKey();
}

int main()
{
	int op;
	do
	{
		system("cls");
		destroyAllWindows();
		printf("Menu:\n");
		printf(" 1 - Open image\n");
		printf(" 2 - Open BMP images from folder\n");
		printf(" 3 - Image negative - diblook style\n");
		printf(" 4 - BGR->HSV\n");
		printf(" 5 - Resize image\n");
		printf(" 6 - Canny edge detection\n");
		printf(" 7 - Edges in a video sequence\n");
		printf(" 8 - Snap frame from live video\n");
		printf(" 9 - Mouse callback demo\n");
		printf(" 10 - Negative image\n");
		printf(" 11 - Extract rgb grays\n");
		printf(" 12 - RGB2Gray conversion\n");
		printf(" 13 - GrayToBinary conversion\n");
		printf(" 14 - RGB to HSV conversion\n");
		printf(" 15 - Webcam extract rgb grays\n");
		printf(" 16 - Webcam rgb24ToGray\n");
		printf(" 17 - Webcam grayToBinary\n");
		printf(" 18 - Webcam rgb24ToHSV\n");
		printf(" 19 - Show histogram\n");
		printf(" 20 - Webcam histogram\n");
		printf(" 21 - Multilevel tresholding\n");
		printf(" 22 - Webcam multilevel tresholding\n");
		printf(" 23 - Weird alg\n");
		printf(" 24 - Colored multilevel tresholding\n");
		printf(" 25 - Webcam color multilevel tresholding\n");
		printf(" 26 - Object caracteristics\n");
		printf(" 27 - Filter objects by area and elongation\n");
		printf(" 28 - Cluster objects(bfs)\n");
		printf(" 29 - Cluster objects(two way)\n");
		printf(" 30 - Extract contour + chainCode gen\n");
		printf(" 31 - ChainCode reconstruct\n");
		printf(" 32 - Dilation alg\n");
		printf(" 33 - Erosion alg\n");
		printf(" 34 - Opening alg\n");
		printf(" 35 - Closing alg\n");
		printf(" 36 - Boundary extraction\n");
		printf(" 37 - Filling algorithm\n");
		printf(" 38 - Mean value algorithm\n");
		printf(" 39 - Histogram functions\n");
		printf(" 40 - Gamma correction\n");
		printf(" 41 - Histogram slide\n");
		printf(" 42 - Histogram equalization\n");
		printf(" 43 - General filter\n");
		printf(" 44 - Freqency domain filter\n");

		printf("1234 - Project OCR \n");
		printf(" 0 - Exit\n\n");
		printf("Option: ");
		scanf("%d",&op);
		switch (op)
		{
			case 1:
				testOpenImage();
				break;
			case 2:
				testOpenImagesFld();
				break;
			case 3:
				testParcurgereSimplaDiblookStyle(); //diblook style
				break;
			case 4:
				//testColor2Gray();
				testBGR2HSV();
				break;
			case 5:
				testResize();
				break;
			case 6:
				testCanny();
				break;
			case 7:
				testVideoSequence();
				break;
			case 8:
				testSnap();
				break;
			case 9:
				testMouseClick();
				break;
			case 10:
				negativeImage();
				break;
			case 11:
					{	char fname[BUFSIZ];
						openFileDlg(fname);
						Mat imgl2e1 = imread(fname, CV_LOAD_IMAGE_COLOR);
						l2e1_extractRGBGrays(imgl2e1, true);
						break;
					}
			case 12:
					{
						char fname[BUFSIZ];
						openFileDlg(fname);
						Mat imgl2e2 = imread(fname, CV_LOAD_IMAGE_COLOR);
						l2e2_rgb24ToGrayScale(imgl2e2, true);
						break;
					}
			case 13:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl2e3 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				unsigned char treshold;
				printf("Insert treshold value(0 - 255) : "), scanf("%d", &treshold);
				l2e3_grayToBinary(imgl2e3, treshold, true);
				break;
			}
			case 14:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl2e4 = imread(fname, CV_LOAD_IMAGE_COLOR);
				l2e4_RGBtoHSV(imgl2e4, true);
				break;
			}
			case 15:
				webCam_l2e1();
				break;
			case 16:
				webCam_l2e2();
				break;
			case 17:
				webCam_l2e3();
				break;
			case 18: 
				webCam_l2e4();
				break;
			case 19:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl3e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l3e1_showHistogram(imgl3e1,true);
				break;
			}
			case 20:
				webCam_l3e1();
				break;
			case 21:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl3e5 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l3e5_multilevelTresholding(imgl3e5, true);
				break;
			}
			case 22:
				webCam_l3e5();
				break;
			case 23:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl3e6 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l3e6_floydDithering(imgl3e6, true);
				break;
			}
			case 24:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl3e7 = imread(fname, CV_LOAD_IMAGE_COLOR);
				l3e7_multilevelColorTresholding(imgl3e7,true);
				break;
			}
			case 25:
				webCam_l3e7();
				break;
			case 26:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl4e1 = imread(fname, CV_LOAD_IMAGE_COLOR);
				l4e1_extractShape(imgl4e1, true);
				break;
			}
			case 27:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl4e1 = imread(fname, CV_LOAD_IMAGE_COLOR);
				l4e2_filterObjects(imgl4e1, true);
				break;
			}
			case 28:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl5e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l5e1_bfsClusters(imgl5e1, true);
				break;
			}
			case 29:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl5e2 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l5e2_twoPassClustering(imgl5e2, true);
				break;
			}
			case 30:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l6e1_borderTracing(imgl6e1, true);
				break;
			}
			case 31:
				l6e3_borderReconstruct();
				break;
			case 32:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l7e1_dilation(imgl6e1, true);
				break;
			}
			case 33:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l7e2_erosion(imgl6e1, true);
				break;
			}
			case 34:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l7e3_opening(imgl6e1, true);
				break;
			}
			case 35:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l7e3_closing(imgl6e1, true);
				break;
			}
			case 36:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l7e4_boundaryExtraction(imgl6e1, true);
				break;
			}
			case 37:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l7e5_regionFilling(imgl6e1, true);
				break;
			}
			case 38:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l8e1_grayScaleMean(imgl6e1, true);
				break;
			}
			case 39:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l8e2_histogramFunction(imgl6e1, true);
				break;
			}
			case 40:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l8e3_gammaCorrection(imgl6e1, true);
				break;
			}
			case 41:
				l8e4_histogramSlide();
				break;
			case 42:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l8e5_histogramEqualization(imgl6e1, true);
				break;
			}
			case 43:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l9e1_generalFilter(imgl6e1, true);
				break;
			}
			case 44:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				generic_frequency_domain_filter(imgl6e1);
				break;
			}
			case 45:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l10e1_medianFilter(imgl6e1,true);
				break;
			}
			case 46:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l10e2_gaussianFilter(imgl6e1, true);
				break;
			}
			case 47:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l10e2_gaussianFilter2(imgl6e1, true);
				break;
			}
			case 48:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l11e1_xyderivatives(imgl6e1, true);
				break;
			}
			case 49:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
				l11_canny(imgl6e1, true);
				break;
			}
			case 1234:
			{
				char fname[BUFSIZ];
				openFileDlg(fname);
				Mat imgl6e1 = imread(fname, CV_LOAD_IMAGE_COLOR);
				lpr(imgl6e1);
				break;
			}
		}
	}
	while (op!=0);
	
	
	//negativeWebCam();
	//changeGrayLevel();
	//squaredImage();
	
	//float m[3][3] = { {0.153,35.2356,6.3461},{67.2631,78263.13,235.321},{1.32136,23.15236,2163.12} };
	//computeInverse(m);
	
	//char fname[MAX_PATH];
	//openFileDlg(fname);
	//Mat img = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
	//additiveGrayChange(img, -10);
	//
	//openFileDlg(fname);
	//img = imread(fname, CV_LOAD_IMAGE_GRAYSCALE);
	//multiplicativeGrayChange(img, 2);

	return 0;
}