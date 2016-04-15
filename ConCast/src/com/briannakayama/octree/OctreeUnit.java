package com.briannakayama.octree;

/**
 * A basic 
 * @author brian
 *
 */
public class OctreeUnit implements LODOctree {

	private int color;

	public OctreeUnit(int color) {
		this.color = color;
	}

	/**
	 * {@inheritDoc}
	 * <br><br>
	 * For a single brick, the color returned from all directions is the same.
	 */
	@Override
	public int getColor(int direction) {
		return color;
	}

	/**
	 * {@inheritDoc}
	 * <br><br>
	 * The unit bricks do not have any children, so calling this method will throw an exception.
	 */
	@Override
	public LODOctree getChild(int child) {
		throw new UnsupportedOperationException(
				"OctreeUnits do not have children. Either the tree is " +
				"incorrect, or the traversal has gone too deep.");
	}
	
	@Override
	public String toString(){
		return "[" + Integer.toHexString(color) + "]";
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof OctreeUnit){
			if (((OctreeUnit)o).color == this.color){
				return true;
			}
		}
		return false;
	}

}
