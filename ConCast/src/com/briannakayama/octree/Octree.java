package com.briannakayama.octree;

/**
 * Basic interface for creating an Octree.
 * 
 * Used for compressing a 3D image to a small bytestream.
 * 
 * @author brian
 * 
 */
public interface Octree<K> {

	/**
	 * There are up to 8 potential children for each Octree. The index passed in
	 * follows the following bit encoding scheme: <br>
	 * <br>
	 * xyz in [01][01][01]<br>
	 * <br>
	 * 
	 * For each 0 represents the negative orthant, and 1 repesents the postive
	 * orthant.
	 * 
	 * @param child
	 *            The child's address in xyz.
	 * @return
	 */
	public K getChild(int child);
}
