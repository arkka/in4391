package nl.tudelft.in4391.da;

/**
 * Created by sukmawicaksana on 4/5/2016.
 */

public class GameBot {
	private static Integer MAX_KNIGHT = 100;
	private static Integer MAX_DRAGON = 20;

	public GameBot() {

	}


	public static void main(String[] args)
	{

		for(int j=0;j<MAX_DRAGON/2;j++) {
			new Bot("Dragon "+(j+1),"Dragon").start();
		}

		for(int i=0;i<MAX_KNIGHT/4;i++) {
			new Bot("Knight "+(i+1),"Knight").start();
		}


		for(int j=MAX_DRAGON/2;j<MAX_DRAGON;j++) {
			new Bot("Dragon "+(j+1),"Dragon").start();
		}

		for(int i=MAX_DRAGON/4;i<MAX_KNIGHT;i++) {
			new Bot("Knight "+(i+1),"Knight").start();
		}



	}


}