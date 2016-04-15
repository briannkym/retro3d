/*
 * com_briannakayama_rayCaster_render.h
 *
 *  Created on: Jan 7, 2016
 *      Author: brian
 */

#ifndef COM_BRIANNAKAYAMA_RAYCASTER_RENDER_H_
#define COM_BRIANNAKAYAMA_RAYCASTER_RENDER_H_

#include <jni.h>
#include "jawt_md.h"
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define MAX_ACCURACY 32
#define FOCAL_LENGTH 32768

//The camera position, (0,0,0) origin.
jint* c_pos;
//The camera angle, (0,0,0) looking down (bird's eye view).
jfloat* c_angle;
//The scene to be rendered.
jbyte* scene;

inline void render(Display*, GC, Drawable, unsigned int, unsigned int);



#endif /* COM_BRIANNAKAYAMA_RAYCASTER_RENDER_H_ */
