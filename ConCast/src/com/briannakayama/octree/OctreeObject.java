package com.briannakayama.octree;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * An OctreeObject representation for a 3d volume. Due to the inherent
 * properties of an Octree, the tree can be though of as the minimal cube that
 * can encapsulate the volumetric data.
 * 
 * @author brian
 * 
 */
public class OctreeObject {
	private LODOctree o;
	private int depth;

	public static void main(String[] args) {
		int[][][] map = {
				{ { 0, 0, 0xFFFF0000, 0xFFFF0000 },
						{ 0, 0, 0xFFFF0000, 0xFFFF0000 } },
				{ { 0, 0, 0xFFFF0000, 0xFFFF0000 },
						{ 0, 0, 0xFFFF0000, 0xFFFF0000 } },
				{ { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
				{ { 0xFFFF0000, 0xFFFF0000, 0, 0 },
						{ 0xFFFF0000, 0xFFFF0000, 0, 0 } } };
		OctreeObject o = getOctree(map);
		System.out.println(o.toString());
	}

	OctreeObject(LODOctree o,
			int depth) {
		this.o = o;
		this.depth = depth;
	}

	/**
	 * Get an Octree object for a specific map with coordinates x, y, z
	 * 
	 * @param map
	 *            A 3d ARGB map for an Octree.
	 * @return An OctreeObject for the map
	 */
	public static OctreeObject getOctree(int[][][] map) {
		int largest_length = (map.length > map[0].length) ? map.length
				: map[0].length;
		largest_length = (largest_length > map[0][0].length) ? largest_length
				: map[0][0].length;
		int depth = (int) Math.round(Math.log(largest_length));
		int new_width = (int) Math.round(Math.pow(2, depth));
		if (new_width < largest_length) {
			depth++;
			new_width *= 2;
		}

		map = Arrays.copyOf(map, new_width);

		for (int x = 0; x < map.length; x++) {
			if (map[x] == null) {
				map[x] = new int[new_width][new_width];
			} else {
				map[x] = Arrays.copyOf(map[x], new_width);
				for (int y = 0; y < map[x].length; y++) {
					if (map[x][y] == null) {
						map[x][y] = new int[new_width];
					} else {
						map[x][y] = Arrays.copyOf(map[x][y], new_width);
					}
				}
			}
		}

		LODOctree o = getTree(map, 0, 0, 0, depth);
		return new OctreeObject(o, depth);

	}

	/*
	 * This recursive method goes down to the children first, and tries to find
	 * a visible child. If there is one, then it returns a non null value.
	 * 
	 * If there is at least one non null child, then a parent will also return
	 * not null, etc.
	 */
	private static LODOctree getTree(int[][][] map, int x0, int y0, int z0,
			int depth) {
		if (depth == 0) {
			int color = map[x0][y0][z0];
			if (((color >> 24) & 0xFF) == 0) {
				return null;
			}
			return new OctreeUnit(color);
		}

		int width = (int) Math.round(Math.pow(2, depth - 1));
		LODOctree[] children = new LODOctree[8];
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				for (int z = 0; z < 2; z++) {
					children[(x << 2) | (y << 1) | z] = getTree(map, x0 + x
							* width, y0 + y * width, z0 + z * width, depth - 1);
				}
			}
		}
		for (LODOctree child : children) {
			if (child != null) {
				return new Octree8Unit(children);
			}
		}
		return null;
	}

	/**
	 * Get an Octree from a BufferedImage. <br>
	 * <br>
	 * Each frame should be an xy slice of the 3d object. The frames can then be
	 * repeated either to the left or right.
	 * 
	 * @param map
	 *            The buffered image containing the slides of the Octree
	 * @param x_length
	 *            The x length of the slices
	 * @param y_length
	 *            The y length of the slices
	 * @param z_length
	 *            The z length of the slices
	 * @return An OctreeObject for the map.
	 */
	public static OctreeObject getOctree(BufferedImage map, int x_length,
			int y_length, int z_length) {
		
		int[][][] imap = OctreeFactory.createMap(map, x_length, y_length, z_length);
		
		return getOctree(imap);
	}

	/**
	 * @return the internal Octree
	 */
	public LODOctree getOctree() {
		return o;
	}

	/**
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}

	@Override
	public String toString() {
		String s = "Max depth: " + depth + "\n\n";
		s += o.toString();
		return s;
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof OctreeObject){
			OctreeObject oo = (OctreeObject)o;
			if (oo.depth == this.depth){
				return oo.o.equals(this.o);
			}
		}
		return false;
	}

}
