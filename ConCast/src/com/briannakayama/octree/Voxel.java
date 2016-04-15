package com.briannakayama.octree;

public class Voxel {

	public static void main(String[] args){
		int[] ARGB = {0x7FFFFFFF, 0x7FFFFF00, 0x7FFF00FF, 0xFF00FFFF, 0xFF000000};
		System.out.println(Integer.toHexString(average(ARGB)));
		//expect b26d9191

		int[] ARGB2 = {0x7F00FF00, 0xFFFF0000};
		System.out.println(Integer.toHexString(rayAverage(ARGB)));
		//expect ffdee3ce
		
		System.out.println(Integer.toHexString(rayAverage(ARGB2)));
		//expect ff807f00, has a lot of rounding error, but works.
	}
	
	/**
	 * An averaging function for pixels based on alpha transparency.
	 * @param ARGB
	 * @return
	 */
	public static int average(int... ARGB) {
		int vox_avg = 0;

		// Average the alpha value
		int alpha = 0;
		for (int i = 0; i < ARGB.length; i++) {
			alpha += (ARGB[i] >>> 24);
		}
		
		
		for (int offset = 0; offset < 3; offset++) {
			int average = 0;

			for (int i = 0; i < ARGB.length; i++) {
				average += ((ARGB[i] >>> (8 * offset)) & 0xFF)
						* (ARGB[i] >>> 24);
			}
			// Average the alpha.
			average /= alpha;
			vox_avg |= (average << (8 * offset));
		}
		alpha /= ARGB.length;
		vox_avg |= (alpha << 24);

		
		return vox_avg;
	}

	/**
	 * An averaging function for a ray going through a line of pixels.
	 * @param ARGB
	 * @return
	 */
	public static int rayAverage(int... ARGB) {
		int vox_avg = ARGB[0];
		for (int i = 1; i < ARGB.length; i++){
			if ((vox_avg>>>24) == 0xFF){
				break;
			} else {
				int difference = ((0xFF -(vox_avg >>> 24)) * (ARGB[i] >>> 24)) / 0xFF;
				int next_avg = (difference + (vox_avg >>> 24)) << 24;
				for (int offset = 0; offset < 3; offset++){
					int color = ((0xFF - difference) * ((vox_avg>>>(offset*8))& 0xFF) +  difference * ((ARGB[i]>>>(offset*8))& 0xFF)) / 0xFF;
					next_avg |= (color << 8 * offset);
				}
				vox_avg = next_avg;
			}
		}
		return vox_avg;
	}
	
	
}