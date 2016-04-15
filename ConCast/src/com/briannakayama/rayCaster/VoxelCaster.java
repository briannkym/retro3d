package com.briannakayama.rayCaster;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import clock.Cinterface;

public class VoxelCaster extends Canvas implements Cinterface, KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ByteBuffer brick;
	private ByteBuffer camera = ByteBuffer.allocateDirect(24);
	private float rotatex = 0.0f;
	private float rotatez = 0.0f;
	private int movex = 0;
	private int movey = 0;
	
	private byte[] screenData;

	public VoxelCaster() {
		camera.order(ByteOrder.nativeOrder());
		this.setSize(400, 400);
		JFrame frame = new JFrame("Brick Render Tester");
		frame.add(this);
		frame.addKeyListener(this);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			BufferedImage b = ImageIO.read(new File("test.png"));
			brick = ByteBuffer
					.allocateDirect(b.getHeight() * b.getHeight() * 4);
			brick.order(ByteOrder.nativeOrder());

			for (int y = 0; y < b.getHeight(); y++) {
				for (int x = 0; x < b.getWidth(); x++) {
					brick.putInt(b.getRGB(x, y));
				}
			}

			camera.putInt(-5000);//x position
			camera.putInt(-5000);//y position
			camera.putInt(-5000);//z position
			camera.putFloat(3*3.14f/4.0f); //x rotation
			camera.putFloat(0); //y rotation
			camera.putFloat(-3.14f/4.0f); //z rotation
			initBrick(brick, camera, 400, 400);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void paint(Graphics g){	
	}

	public native void paint();

	public native int initBrick(ByteBuffer brick, ByteBuffer camera, int width, int height);
	
	public native int stop();

	static {
		System.loadLibrary("voxelCaster");
	}

	@Override
	public void update() {
		int xpos = camera.getInt(0);
		camera.putInt(0, xpos + movex);
		int ypos = camera.getInt(4);
		camera.putInt(4, ypos + movey);
		
		float xrot = camera.getFloat(12);
		camera.putFloat(12, xrot+rotatex);
		float zrot = camera.getFloat(20);
		camera.putFloat(20, zrot+rotatez);
		this.paint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_RIGHT:
			rotatez = -0.0314f;
			break;
		case KeyEvent.VK_LEFT:
			rotatez = 0.0314f;
			break;
		case KeyEvent.VK_UP:
			rotatex = -0.0314f;
			break;
		case KeyEvent.VK_DOWN:
			rotatex = 0.0314f;
			break;
		case KeyEvent.VK_D:
			movex = 64;
			break;
		case KeyEvent.VK_A:
			movex = -64;
			break;
		case KeyEvent.VK_W:
			movey = 64;
			break;
		case KeyEvent.VK_S:
			movey = -64;
			break;
			
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_RIGHT:
			rotatez = 0.0f;
			break;
		case KeyEvent.VK_LEFT:
			rotatez = 0.0f;
			break;
		case KeyEvent.VK_UP:
			rotatex = 0.0f;
			break;
		case KeyEvent.VK_DOWN:
			rotatex = 0.0f;
			break;
		case KeyEvent.VK_D:
			movex = 0;
			break;
		case KeyEvent.VK_A:
			movex = 0;
			break;
		case KeyEvent.VK_W:
			movey = 0;
			break;
		case KeyEvent.VK_S:
			movey = 0;
			break;
		}
	}
}
