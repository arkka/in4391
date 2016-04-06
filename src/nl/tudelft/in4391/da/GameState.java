package nl.tudelft.in4391.da;

/**
 * Created by sukmawicaksana on 4/5/2016.
 */

public class GameState {
	// Is-the-program-actually-running-flag
	private static volatile boolean running = true;
	// Relation between game time and real time
	public static final double GAME_SPEED = 1;
	// The number of players in the game
	private static int playerCount = 0;

	public static void haltProgram() {
		running = false;
	}

	public static boolean getRunningState() {
		return running;
	}

	public static int getPlayerCount() {
		return playerCount;
	}
}
