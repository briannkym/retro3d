package com.briannakayama.model;

import java.nio.ByteBuffer;

/**
 * Stores a map according to an Octree specification.
 * @author brian
 *
 */
public class RetroOctMap {
	
	public static final int DEFAULT_DEPTH = 5;
	private ByteBuffer cache;
	private int start;
	private int depth;
	
	public RetroOctMap(ByteBuffer cache, int start){
		this(cache, start, DEFAULT_DEPTH);
	}
	
	
	public RetroOctMap(ByteBuffer cache, int start, int depth){
		this.cache = cache;
		this.start = start;
		this.depth = depth;
	}
	
	
	public boolean put(RetroObject o, int x, int y){
		
	}
	
	
	
}
