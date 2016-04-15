/*
 * com_briannakayama_rayCaster_render.c
 *
 *  Created on: Jan 7, 2016
 *      Author: Brian Nakayama
 */
#include "com_briannakayama_rayCaster_render.h"

struct discrete_vector {
	jint x, y, z;
};

struct float_vector {
	jfloat x, y, z;
};

#define ROUND(xfloat, xint) xint.x = (jint)((xfloat.x)* 1024);\
	xint.y = (jint)((xfloat.y)* 1024);\
	xint.z = (jint)((xfloat.z)* 1024);


inline int getcolor(struct discrete_vector, struct discrete_vector);


//Local variables for x, y, and z.
struct discrete_vector xintv;
struct discrete_vector yintv;
struct discrete_vector zintv;

//Temporary variable for testing.
int cubewidth = 16 * MAX_ACCURACY;


char count[16] = {0,1,1,2,
		1,2,2,3,
		1,2,2,3,
		2,3,3,4};

inline void render(Display* display, GC gc, Drawable drawable, unsigned int width,
		unsigned int height) {
	jfloat sx = sinf(c_angle[0]);
	jfloat sy = sinf(c_angle[1]);
	jfloat sz = sinf(c_angle[2]);
	jfloat cx = cosf(c_angle[0]);
	jfloat cy = cosf(c_angle[1]);
	jfloat cz = cosf(c_angle[2]);
	struct float_vector xv;
	struct float_vector yv;
	struct float_vector zv;

	//y rotation:
	xv.x = MAX_ACCURACY * cy;
	xv.z = MAX_ACCURACY * sy;

	zv.x = -MAX_ACCURACY * sy;
	zv.z = MAX_ACCURACY * cy;

	//x rotation:
	xv.y = xv.z * sx;
	xv.z = xv.z * cx;

	zv.y = zv.z * sx;
	zv.z = zv.z * cx;

	yv.y = FOCAL_LENGTH * cx;
	yv.z = -FOCAL_LENGTH * sx;

	//z rotation:
	jfloat temp = xv.x * cz - xv.y * sz;
	xv.y = xv.x * sz + xv.y * cz;
	xv.x = temp;

	temp = zv.x * cz - zv.y * sz;
	zv.y = zv.x * sz + zv.y * cz;
	zv.x = temp;

	yv.x = -yv.y * sz;
	yv.y = yv.y * cz;

	//Round to nearest 1024th (multiplies each number by 1024 before casting to int.)
	ROUND(xv, xintv);
	ROUND(yv, yintv);
	ROUND(zv, zintv);

	//Left side of the screen.
	struct discrete_vector startv;
	//Position of ray relative to the focal length.
	struct discrete_vector startray;
	//Direction of individual rays.
	struct discrete_vector directionray;
	//Position of the camera
	struct discrete_vector start;

	startv.x = -((width >> 1) * xintv.x + (height >> 1) * zintv.x);
	startv.y = -((width >> 1) * xintv.y + (height >> 1) * zintv.y);
	startv.z = -((width >> 1) * xintv.z + (height >> 1) * zintv.z);

	unsigned int s_x, s_y;
	int color = 0xFF000000;
	start.x = c_pos[0];
	start.y = c_pos[1];
	start.z = c_pos[2];
	for (s_y = 0; s_y < height; s_y++) {
		startray.x = startv.x;
		startray.y = startv.y;
		startray.z = startv.z;

		for (s_x = 0; s_x < width; s_x++) {
			directionray.x = (startray.x - yintv.x) >> 10;
			directionray.y = (startray.y - yintv.y) >> 10;
			directionray.z = (startray.z - yintv.z) >> 10;

			color = getcolor(start, directionray);

			XSetForeground(display, gc, color);
			XDrawPoint(display, drawable, gc, s_x, s_y);

			startray.x += xintv.x;
			startray.y += xintv.y;
			startray.z += xintv.z;
		}

		startv.x += zintv.x;
		startv.y += zintv.y;
		startv.z += zintv.z;
	}
}

struct discrete_vector hsmart;

inline int getcolor(struct discrete_vector start, struct discrete_vector directionray){
	int color = 0xFF000000;
	hsmart.x = (-cubewidth - start.y) * directionray.z //positive Z first
	- (-cubewidth - start.z) * directionray.y; //negative Y first
	hsmart.y = (-cubewidth - start.z) * directionray.x //positive X first
	- (-cubewidth - start.x) * directionray.z; //negative Z first
	hsmart.z = (-cubewidth - start.x) * directionray.y //positive Y first
	- (-cubewidth - start.y) * directionray.x; //negative X first

	if (hsmart.z < 0) {
		if (hsmart.x >= 0) {
			if ((-cubewidth - start.y) * directionray.z
					<= (cubewidth - start.z) * directionray.y
					&& (-cubewidth - start.y) * directionray.x
							<= (cubewidth - start.x) * directionray.y) {
				//color the pixel yellow
				color = 0xFFFFFF00;
			}
		} else if (hsmart.y >= 0) {
			if ((-cubewidth - start.z) * directionray.y
					<= (cubewidth - start.y) * directionray.z
					&& (-cubewidth - start.z) * directionray.x
							<= (cubewidth - start.x) * directionray.z) {
				//color the pixel teal.
				color = 0xFF00FFFF;
			}
		}
	} else {
		if (hsmart.y < 0) {
			if ((-cubewidth - start.x) * directionray.y
					<= (cubewidth - start.y) * directionray.x
					&& (-cubewidth - start.x) * directionray.z
							<= (cubewidth - start.z) * directionray.x) {
				//color the pixel purple
				color = 0xFFFF00FF;
			}
		} else if (hsmart.x < 0) {
			if ((-cubewidth - start.z) * directionray.y
					<= (cubewidth - start.y) * directionray.z
					&& (-cubewidth - start.z) * directionray.x
							<= (cubewidth - start.x) * directionray.z) {
				//color the pixel teal
				color = 0xFF00FFFF;
			}
		}
	}
	return color;
}





