import clock.Clock;

import com.briannakayama.rayCaster.VoxelCaster;


public class Main {
	public static void main(String[] args){
		VoxelCaster c = new VoxelCaster();
		//c.test();
		Clock C = new Clock(20.0f, c);
		C.init();
	}
}
