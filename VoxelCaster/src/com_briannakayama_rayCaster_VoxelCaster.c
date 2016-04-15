/*
 ============================================================================
 Name        : VoxelCaster.c
 Author      : Brian Nakayama
 Version     : 0.01
 Copyright   : Copyright (C) 2015 Brian Nakayama
 Description : C, Ansi-style
 ============================================================================
 */

#include <jni.h>
#include "com_briannakayama_rayCaster_VoxelCaster.h"
#include "jawt_md.h"
#include "com_briannakayama_rayCaster_render.h"
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define SET(v, x, y, z) v.x = (x); v.y = (y); v.z = (z);
#define SUB(r, a, b) SET(r, a.x-b.x, a.y-b.y, a.z-b.z);

#define HSMART(h, s, d) h.x = s.z*d.y - s.y*d.z;\
	h.y = s.x*d.z - s.z*s.x;\
	h.z = s.y*d.x - s.x*d.y;

//Assumes x collides first.
#define ONE_PLANE(r, h, y, z, d_m) r.x = h.x;\
	r.y = h.y + d_m.z;\
	r.z = h.z - d_m.y;

#define X_PLANE(r,h,dm) ONE_PLANE(r,h,y,z,dm)
#define Y_PLANE(r,h,dm) ONE_PLANE(r,h,z,x,dm)
#define Z_PLANE(r,h,dm) ONE_PLANE(r,h,x,y,dm)

//Assumes y and z collides first.
#define TWO_PLANE(r, h, y, z, d_m) r.x = d_m.y - d_m.z;\
		r.y = h.y - d_m.x;\
		r.z = h.z + d_m.x;

#define YZ_PLANE(r,h,dm) TWO_PLANE(r,h,y,z,dm)
#define ZX_PLANE(r,h,dm) TWO_PLANE(r,h,z,x,dm)
#define XY_PLANE(r,h,dm) TWO_PLANE(r,h,x,y,dm)

//All three collide
#define THREE_PLANE(r, d_m) r.x = d_m.y - d_m.z;\
	d_m.z - d_m.x;\
	d_m.x - d_m.y;


//Local variables for rendering

unsigned int width, height;

JAWT awt;
GC gc_display, gc_buffer;
Display* display;
Drawable drawable;
Pixmap buffer;

JNIEXPORT void JNICALL Java_com_briannakayama_rayCaster_VoxelCaster_paint(
		JNIEnv *env, jobject canvas) {
	JAWT_DrawingSurface* ds;

	/* Get the drawing surface */
	ds = awt.GetDrawingSurface(env, canvas);

	/* Lock the drawing surface */
	ds->Lock(ds);

	//Testing the render method
	render(display, gc_buffer, buffer, 400, 400);

	//Copy the buffer onto the screen.
	XCopyArea(display, buffer, drawable, gc_display, 0, 0, 400, 400, 0, 0);
	/*for (z = 0; z < 16; z++) {
	 for (y = 0; y < 16; y++) {
	 for (x = 0; x < 16; x++) {
	 XSetForeground(dsi_x11->display, gc,
	 scene_int[(z << 8) | (y << 4) | (x)]);
	 XDrawPoint(dsi_x11->display, dsi_x11->drawable, gc, x,
	 y + (z << 4));
	 }
	 }
	 }*/

	/* Unlock the drawing surface */
	ds->Unlock(ds);

	/* Free the drawing surface */
	awt.FreeDrawingSurface(ds);
}


JNIEXPORT jint JNICALL Java_com_briannakayama_rayCaster_VoxelCaster_initBrick(
		JNIEnv *env, jobject canvas, jobject brick, jobject camera, jint scr_width, jint scr_height) {
	scene = (*env)->GetDirectBufferAddress(env, brick);
	jbyte* c = (*env)->GetDirectBufferAddress(env, camera);
	c_pos = (jint *) c;
	c_angle = (jfloat *) (c + 12);
	width = (unsigned int) scr_width;
	height = (unsigned int) scr_height;

	JAWT_DrawingSurface* ds;
	JAWT_DrawingSurfaceInfo* dsi;
	JAWT_X11DrawingSurfaceInfo* dsi_x11;

	/* Get the AWT */
	awt.version = JAWT_VERSION_1_3;
	if (JAWT_GetAWT(env, &awt) == JNI_FALSE) {
		printf("AWT Not found\n");
		return 1;
	}

	/* Get the drawing surface */
	ds = awt.GetDrawingSurface(env, canvas);
	if (ds == NULL ) {
		printf("NULL drawing surface\n");
		return 2;
	}

	jint lock = ds->Lock(ds);
	if ((lock & JAWT_LOCK_ERROR) != 0) {
		printf("Error locking surface. Has canvas been added to a JFrame?\n");
		awt.FreeDrawingSurface(ds);
		return 3;
	}

	/* Get the drawing surface info */
	dsi = ds->GetDrawingSurfaceInfo(ds);
	if (dsi == NULL ) {
		printf("Error getting surface info\n");
		awt.FreeDrawingSurface(ds);
		return 4;
	}

	/* Get the platform-specific drawing info */
	dsi_x11 = (JAWT_X11DrawingSurfaceInfo*) dsi->platformInfo;

	//Save the display.
	display = dsi_x11->display;
	//Save the drawable.
	drawable = dsi_x11->drawable;

	//Create a double buffer.
	buffer = XCreatePixmap(display, drawable, 400, 400, dsi_x11->depth);

	//Create graphics
	gc_display = XCreateGC(display, drawable, 0, 0);
	gc_buffer = XCreateGC(display, buffer, 0, 0);

	/* Free the drawing surface info */
	ds->FreeDrawingSurfaceInfo(dsi);

	ds->Unlock(ds);

	/* Free the drawing surface */
	awt.FreeDrawingSurface(ds);

	return 0;
}

JNIEXPORT jint JNICALL Java_com_briannakayama_rayCaster_VoxelCaster_stop(
		JNIEnv *env, jobject canvas) {
	//Can assume 24 bit true color display.

	XFreeGC(display, gc_display);
	XFreeGC(display, gc_buffer);
	XFreePixmap(display, buffer);
	return 0;
}
