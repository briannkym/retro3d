package com.briannakayama.model;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.briannakayama.octree.OctreeFactory;

public class DrawMemory {
	public static final int MIN_MEMORY = 10000;
	private Map<String, Integer> registered = new HashMap<String, Integer>();
	private ByteBuffer mem;

	public DrawMemory(int bytes) {
		if (bytes > MIN_MEMORY) {
			this.mem = ByteBuffer.allocateDirect(bytes);
		} else {
			throw new IllegalArgumentException("Requested memory size " + bytes
					+ "bytes is too small.");
		}
	}

	public int register(File f) {
		if (!registered.containsKey(f.getAbsolutePath())) {
			try {
				byte[] b = OctreeFactory.loadBytes(f);
				int pointer = mem.position();
				registered.put(f.getAbsolutePath(), pointer);
				mem.put(b);
				return pointer;
			} catch (Exception e) {
			}
		}
		return -1;
	}

}
