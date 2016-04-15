package com.briannakayama.octree;

public interface LODOctree extends Octree<LODOctree>{

	/**
	 * There are 6 directions in which a ray can enter this octree cube. The
	 * directions are: <br>
	 * <br>
	 * 
	 * ddo in [012][01]<br>
	 * <br>
	 * 
	 * where 2 in dd is for a ray along the x axis, 1 is for a ray on the y
	 * aixs, and 0 for a ray on the z axis. The direction o represents from
	 * which orthant the ray has entered. the negative orthant is 0, positive is
	 * 1.
	 * 
	 * @param direction
	 * @return
	 */
	public int getColor(int direction);

}
