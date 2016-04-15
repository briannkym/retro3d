package com.briannakayama.octree;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.imageio.ImageIO;

public class OctreeFactory {

	private static final byte[] V_FORMAT = { (byte) 159 };
	private static final byte[] VERSION = { (byte) 0 };
	private static final String EXT = "retro3d";
	private static final int[] count = { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2,
			3, 3, 4 };
	public static void main(String[] args) {
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(new File("test2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int[][][] map = createMap(bi, 64, 64, 64);
		// int[][][] map = {
		// { { 0, 0, 0xFFFF0000, 0xFFFF0000 },
		// { 0, 0, 0xFFFF0000, 0xFFFF0000 } },
		// { { 0, 0, 0xFFFF0000, 0xFFFF0000 },
		// { 0, 0, 0xFFFF0000, 0xFFFF0000 } },
		// { { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
		// { { 0xFFFF0000, 0xFFFF0000, 0, 0 },
		// { 0xFFFF0000, 0xFFFF0000, 0, 0 } } };

		optimizeMap(map);
		OctreeObject o = OctreeObject.getOctree(map);
		byte[] b = octreeToBytes(OctreeObject.getOctree(map));
		System.out.println(b.length);
		OctreeObject o2 = null;
		try {
			o2 = bytesToOctree(b);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(o.equals(o2));
		try {
			save(o, new File("test"));
			o2 = loadOctree(new File("test"));
			System.out.println(o.equals(o2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save an OctreeObject to a file.
	 * 
	 * @param o
	 *            The OctreeObject to save.
	 * @param dest
	 *            The file to save to.
	 * @throws IOException
	 *             If the file cannot be accessed.
	 */
	public static void save(OctreeObject o, File dest) throws IOException {
		if (!dest.getName().endsWith("." + EXT)) {
			dest = new File(dest.getAbsolutePath() + "." + EXT);
		}
		try (FileOutputStream f = new FileOutputStream(dest);
				DeflaterOutputStream d = new DeflaterOutputStream(f);) {
			f.write(V_FORMAT);
			f.write(VERSION);

			byte[] data = octreeToBytes(o);

			d.write(data);
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Load an OctreeObject from a file.
	 * 
	 * @param dest
	 *            The file to load the OctreeObject from.
	 * @return The OctreeObject for the given file.
	 * @throws IOException
	 *             If the file cannot be accessed.
	 * @throws DataFormatException
	 *             If the file is not in the correct format or cannot be read.
	 */
	public static OctreeObject loadOctree(File dest) throws IOException,
			DataFormatException {
		return bytesToOctree(loadBytes(dest));
	}

	/**
	 * Load a byte array in the OctreeObject format from a file.
	 * 
	 * @param dest
	 *            The file to load the OctreeObject from.
	 * @return The OctreeObject for the given file.
	 * @throws IOException
	 *             If the file cannot be accessed.
	 * @throws DataFormatException
	 *             If the file is not in the correct format or cannot be read.
	 */
	public static byte[] loadBytes(File dest) throws IOException,
			DataFormatException {
		if (!dest.getName().endsWith("." + EXT)) {
			dest = new File(dest.getAbsolutePath() + "." + EXT);
		}
		try (FileInputStream f = new FileInputStream(dest);
				InflaterInputStream i = new InflaterInputStream(f);
				ByteArrayOutputStream b = new ByteArrayOutputStream();) {
			byte[] magic_number = new byte[1];
			f.read(magic_number);
			if (magic_number[0] != V_FORMAT[0]) {
				throw new DataFormatException(
						"The magic number does not match. The file is either corrupted"
								+ "or not an Octree object.");
			}

			/*
			 * If there's a new file version, this part of the code must be
			 * updated.
			 */
			byte[] file_version = new byte[1];
			f.read(file_version);

			byte[] buffer = new byte[1024];
			int count;
			while ((count = i.read(buffer)) >= 0) {
				b.write(buffer, 0, count);
			}
			b.close();
			return b.toByteArray();
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Converts an OctreeObject into an array of bytes.
	 * 
	 * @param obj
	 *            The object to convert.
	 * @return An array of bytes in the Octree format.
	 */
	public static byte[] octreeToBytes(OctreeObject obj) {
		try (ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream d = new DataOutputStream(b);) {
			byte depth = (byte) obj.getDepth();
			int count = 0;
			d.writeByte(0);
			d.writeByte(0);
			d.writeByte(0);
			count += countBits(obj.getDepth());
			d.writeByte(depth);
			Queue<LODOctree> queue = new LinkedList<LODOctree>();
			queue.add(obj.getOctree());
			int current = 0;
			int next = 7;// Next is 7 since the pointer is at 0, and the sides
							// are 1-6
			while (!queue.isEmpty()) {
				LODOctree o = queue.poll();

				int pointer = (next - current) << 8;
				if (o instanceof OctreeUnit) {
					count += countBits(o.getColor(0));
					d.writeInt(o.getColor(0));
					current += 1;
				} else {

					current += 7;
					for (int c = 0; c < 8; c++) {
						LODOctree child = o.getChild(c);
						if (child != null) {
							pointer |= (1 << (7 - c));
							if (child instanceof OctreeUnit) {
								next += 1;
							} else {
								next += 7;
							}
							queue.add(child);
						}

					}

					count += countBits(pointer);
					d.writeInt(pointer);
					for (int f = 0; f < 6; f++) {

						count += countBits(o.getColor(f));
						d.writeInt(o.getColor(f));
					}
				}
			}
			d.close();
			byte[] bytes = b.toByteArray();
			bytes[2] = (byte) (count & 0xFF);
			return bytes;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * Counts the bits in an integer for signal error detection and correction.
	 */
	private static int countBits(int b) {
		int total = 0;
		for (int i = 0; i < 32; i += 4) {
			total += count[(b >> i) & 0xF];
		}
		return total;
	}

	/**
	 * Gets an OctreeObject given an array of bytes.
	 * 
	 * @param b
	 *            The bytes in the uncompressed OctreeObject format.
	 * @return The OctreeObject corresponding to the byte array.
	 */
	public static OctreeObject bytesToOctree(byte[] b)
			throws DataFormatException {
		try {
			int depth = b[3] & 0xFF;
			LODOctree o = bytesToOctree(b, 0, depth);
			return new OctreeObject(o, depth);
		} catch (Exception e) {
			throw new DataFormatException(
					"There was an error in the Octree data."
							+ " The file is either corrupted or not an Octree object.");
		}
	}

	/*
	 * Recursive method for finding the bytes for an Octree.
	 */
	private static LODOctree bytesToOctree(byte[] b, int offset, int depth) {
		if (depth == 0) {
			return new OctreeUnit(getInt(b, 4, offset));
		} else {
			int address = getInt(b, 4, offset) >>> 8;
			int children = getInt(b, 4, offset) & 0xFF;
			LODOctree[] c_octree = new LODOctree[8];
			int[] faces = new int[6];

			for (int f = 1; f < 7; f++) {
				faces[f - 1] = getInt(b, 4, offset + f);
			}

			int c_present = 0;
			if (depth > 1) {
				for (int c = 0; c < 8; c++) {
					if (((children >> (7 - c)) & 1) == 1) {
						c_octree[c] = bytesToOctree(b, offset + address
								+ c_present, depth - 1);
						c_present += 7;
					}
				}
			} else {
				for (int c = 0; c < 8; c++) {
					if (((children >> (7 - c)) & 1) == 1) {
						c_octree[c] = bytesToOctree(b, offset + address
								+ c_present, depth - 1);
						c_present += 1;
					}
				}
			}
			return new Octree8Unit(faces, c_octree);
		}
	}

	private static int getInt(byte[] b, int offset, int index) {
		index *= 4;

		int result = ((b[offset + index] & 0xFF) << 24)
				| ((b[offset + index + 1] & 0xFF) << 16)
				| ((b[offset + index + 2] & 0xFF) << 8)
				| (b[offset + index + 3] & 0xFF);
		return result;
	}

	/**
	 * Remove all voxels that cannot be rendered from outside the object.
	 * 
	 * @param map
	 *            The map to optimize/cull unseen pixels from.
	 */
	public static void optimizeMap(int[][][] map) {
		int[] length = { map.length, map[0].length, map[0][0].length };
		int[] p = { length[0], length[1], length[2] };
		boolean[][][] mapStat = new boolean[length[0] + 1][length[1] + 1][length[2] + 1];
		Queue<int[]> queue = new LinkedList<int[]>();
		queue.add(p);
		while (!queue.isEmpty()) {
			p = queue.poll();
			boolean addNeighbor = false;
			if (!mapStat[p[0]][p[1]][p[2]]) {
				if (p[0] != length[0] && p[1] != length[1] && p[2] != length[2]) {
					int alpha = (map[p[0]][p[1]][p[2]] >> 24) & 0xFF;

					//Looks odd, but this is correct.
					if (alpha != 0xFF) {
						addNeighbor = true;
					}

				} else {
					addNeighbor = true;
				}
				mapStat[p[0]][p[1]][p[2]] = true;
			}
			if (addNeighbor) {
				for (int d = 0; d < 3; d++) {
					int value = p[d];
					if (p[d] == 0) {
						p[d] = length[d];
						queue.add(Arrays.copyOf(p, p.length));
						p[d] = 1;
						queue.add(Arrays.copyOf(p, p.length));
					} else if (p[d] == length[d]) {
						p[d] -= 1;
						queue.add(Arrays.copyOf(p, p.length));
						p[d] = 0;
						queue.add(Arrays.copyOf(p, p.length));
					} else {
						p[d] += 1;
						queue.add(Arrays.copyOf(p, p.length));
						p[d] -= 2;
						queue.add(Arrays.copyOf(p, p.length));
					}
					p[d] = value;
				}
			}
		}

		for (int x = 0; x < length[0]; x++) {
			for (int y = 0; y < length[1]; y++) {
				for (int z = 0; z < length[2]; z++) {
					if (!mapStat[x][y][z]) {
						map[x][y][z] = 0;
					}
				}
			}
		}
	}

	/**
	 * Get a 3d array from a BufferedImage. <br>
	 * <br>
	 * Each frame should be an xy slice of the 3d object. The frames can then be
	 * repeated either to the left or right.
	 * 
	 * @param map
	 *            The buffered image containing the slides of the a 3d volume
	 * @param x_length
	 *            The x length of the slices
	 * @param y_length
	 *            The y length of the slices
	 * @param z_length
	 *            The z length of the slices
	 * @return A 3d array for the map.
	 */
	public static int[][][] createMap(BufferedImage map, int x_length,
			int y_length, int z_length) {
		boolean wformat;
		if (map.getWidth() == x_length
				&& map.getHeight() == y_length * z_length) {
			wformat = true;
		} else if (map.getHeight() == y_length
				&& map.getWidth() == x_length * z_length) {
			wformat = false;
		} else {
			throw new IllegalArgumentException(
					"The provided map does not fit the dimensions provided.");
		}

		int[][][] imap = new int[x_length][y_length][z_length];
		if (wformat) {
			for (int z = 0; z < z_length; z++) {
				for (int x = 0; x < x_length; x++) {
					for (int y = 0; y < y_length; y++) {
						imap[x][y][z] = map.getRGB(x, y + z * y_length);
					}
				}
			}
		} else {
			for (int z = 0; z < z_length; z++) {
				for (int x = 0; x < x_length; x++) {
					for (int y = 0; y < y_length; y++) {
						imap[x][y][z] = map.getRGB(x + z * x_length, y);
					}
				}
			}
		}

		return imap;
	}

}
