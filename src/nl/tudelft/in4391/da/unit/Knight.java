package nl.tudelft.in4391.da.unit;

import nl.tudelft.in4391.da.GameBot;
import nl.tudelft.in4391.da.GameState;

/**
 * Created by arkkadhiratara on 3/22/16.
 */

public class Knight extends Unit {

    // Minimum and maximum delay between turns
    public static final int MIN_TIME_BETWEEN_TURNS = 2;
    public static final int MAX_TIME_BETWEEN_TURNS = 7;

    // The minimum and maximum amount of hitpoints that a particular player starts with
    public static final int MIN_HITPOINTS = 10;
    public static final int MAX_HITPOINTS = 20;
    // The minimum and maximum amount of attackpoints that a particular player has
    public static final int MIN_ATTACKPOINTS = 1;
    public static final int MAX_ATTACKPOINTS = 10;

    public Knight(String name) {
        super(name,"knight");

        // Initialize hitpoints and attackpoints for each Knight
        this.hitPoints = (int) (Math.random() * (MAX_HITPOINTS - MIN_HITPOINTS) + MIN_HITPOINTS);
        this.attackPoints = (int)(Math.random() * (MAX_ATTACKPOINTS - MIN_ATTACKPOINTS) + MIN_ATTACKPOINTS);

        // Assign max health for each Knight
        this.maxHitPoints = this.hitPoints;

        /* Create a random delay for each Knight*/
        this.timeBetweenTurns = (int)(Math.random() * (MAX_TIME_BETWEEN_TURNS - MIN_TIME_BETWEEN_TURNS)) + MIN_TIME_BETWEEN_TURNS;


        // Create thread for each knight
//        runnerThread = new Thread(this, name);
//        runnerThread.start();
//
//        // Check thread running or not
//        if (runnerThread.isAlive()) {
//            System.out.println("Starting " +  name );
//        }

    }

//    public void run() {
//
//        GameBot bot = new GameBot();
//
//        // Connect to server
//        bot.findAndConnectServer();
//        bot.login(getName(), "");
//
//        while (GameState.getRunningState() && runnerThread.isAlive()) {
//
//            try {
//                Thread.currentThread().sleep((int) (timeBetweenTurns * 500 * GameState.GAME_SPEED));
//
//
//
//
//
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//
//        }
//    }
}
