package com.briannakayama.octree;

import static com.briannakayama.octree.Voxel.average;
import static com.briannakayama.octree.Voxel.rayAverage;

import java.util.Arrays;

public class Octree8Unit implements LODOctree {

	private LODOctree[] children;
	private int[] faces = new int[6];

	public static void main(String[] args) {
		OctreeUnit o[] = { new OctreeUnit(0xFFFF0000),
				new OctreeUnit(0xFFFF0000), new OctreeUnit(0xFFFF0000),
				new OctreeUnit(0xFFFF0000), new OctreeUnit(0xFF0000FF),
				new OctreeUnit(0xFF0000FF), new OctreeUnit(0x7F00FF00),
				new OctreeUnit(0x7FFFFFFF) };
		Octree8Unit u = new Octree8Unit(o);
		for (int i = 0; i < 6; i++) {
			System.out.println(Integer.toHexString(u.getColor(i)));
		}
	}

	/**
	 * Creates an Octree8Unit for a set of children. 
	 * @param children The 8 children for this Octree.
	 * @throws IllegalArgumentException
	 *             If an incorrect number of children are passed in, or incorrect 
	 *             faces are passed in.
	 */
	public Octree8Unit(LODOctree[] children) {
		if (children.length != 8) {
			throw new IllegalArgumentException(
					"There should be an array of length 8 passed into this method.");
		}
		this.children = children;
		makeFaces();
	}

	/**
	 * Creates and verifies the correct construction of a Octree8Unit given a
	 * set of faces.
	 * 
	 * @param faces
	 *            The pregenerated faces for this Octree. Should be the same as
	 *            the faces generated by {@link #Octree8Unit(Octree[])}
	 * @param children
	 *            The 8 children for this Octree.
	 * @throws IllegalArgumentException
	 *             If an incorrect number of children are passed in, or incorrect 
	 *             faces are passed in.
	 */
	public Octree8Unit(int[] faces, LODOctree[] children) {
		this(children);
		for (int i = 0; i < 6; i++) {
			if (this.faces[i] != faces[i]) {
				throw new IllegalArgumentException(
						"The faces do not match the given children.");
			}
		}
	}

	/*
	 * Generates the faces by shooting 4 rays in each quadrant through each face
	 * of the Octree. Can be determined by the 4 c0, c1 pairs chosen for each
	 * face.
	 * 
	 * In short, it works, but do not touch!!!
	 */
	private void makeFaces() {
		for (int f = 0; f < faces.length; f++) {
			int shift = (f >> 1);
			int rays[] = new int[4];
			for (int c = 0; c < children.length; c += 2) {
				int c0 = (c ^ (f & 1));
				int c1 = c0 ^ 1;
				c0 = ((c0 << shift) | c0 >> (3 - shift)) & 7;
				c1 = ((c1 << shift) | c1 >> (3 - shift)) & 7;
				int c0_color = (children[c0] != null) ? children[c0]
						.getColor(f) : 0;
				int c1_color = (children[c1] != null) ? children[c1]
						.getColor(f) : 0;
				rays[c >> 1] = rayAverage(c0_color, c1_color);
			}
			faces[f] = average(rays);
		}
	}

	@Override
	public int getColor(int direction) {
		return faces[direction];
	}

	@Override
	public LODOctree getChild(int child) {
		return children[child];
	}

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < faces.length; i++) {
			switch (i) {
			case 0:
				s += "x0";
				break;
			case 1:
				s += "x1";
				break;
			case 2:
				s += "y0";
				break;
			case 3:
				s += "y1";
				break;
			case 4:
				s += "z0";
				break;
			case 5:
				s += "z1";
				break;
			}
			s += "[" + Integer.toHexString(faces[i]) + "] ";
		}

		for (int i = 0; i < children.length; i++) {
			s += "\n" + children[i];
		}
		s = s.replaceAll("\n", "\n   ");
		return s;

	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Octree8Unit) {
			Octree8Unit ou = (Octree8Unit) o;
			if (Arrays.equals(ou.faces, this.faces)) {
				boolean allEqual = true;
				for (int i = 0; i < 8; i++) {
					if (ou.children[i] != null && this.children[i] != null) {
						allEqual &= ou.children[i].equals(this.children[i]);
					} else if (ou.children[i] != this.children[i]) {
						allEqual = false;
					}
				}
				return allEqual;
			}
		}
		return false;
	}

}
