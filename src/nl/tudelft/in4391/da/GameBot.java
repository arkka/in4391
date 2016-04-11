package nl.tudelft.in4391.da;

import java.util.Scanner;

/**
 * Created by sukmawicaksana on 4/5/2016.
 */

public class GameBot {
	private static Integer MAX_KNIGHT = 10;
	private static Integer MAX_DRAGON = 3;

	public GameBot() {

	}


	public static void main(String[] args)
	{

		for(int j=0;j<MAX_DRAGON;j++) {
			new Bot("Dragon "+(j+1),"Dragon").start();
		}

		for(int i=0;i<MAX_KNIGHT;i++) {
			new Bot("Knight "+(i+1),"Knight").start();
		}

		// Input
		Scanner s = new Scanner(System.in);

		// Command
		String command = "";

		while(true) {
			command = s.nextLine().trim();

			switch (command) {
				case "exit":
					System.exit(-1);
			}
		}


	}


}