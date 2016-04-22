package nl.tudelft.in4391.da;

import nl.tudelft.in4391.da.unit.Unit;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

/**
 * Created by sukmawicaksana on 4/5/2016.
 */

public class Benchmark {
	private static final double GAME_SPEED = .1; //ms
	private static Integer TURN_DELAY = 1000;
	private static Integer MAX_KNIGHT = 5;


	public Benchmark() {

	}


	public static void main(String[] args)
	{
		Node node = new Node(1, "127.0.0.1", 1100, 1200);
		Server server = ServerImpl.fromRemoteNode(node);

		for(int i=0;i<MAX_KNIGHT;i++) {
			final int index = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					final String username = "bot-" + index;
					Random rand = new Random();
					Player player = null;
					Unit unit = null;
					Arena arena = new Arena();

					try {
						player = server.login("bot-" + (username), "", "Knight");
						arena = server.getArena();
						arena.syncUnits();
						unit = player.getUnit();

					} catch (RemoteException e) {
						//e.printStackTrace();
						Thread.currentThread().interrupt();
						return;
					}

					while (true) {
						// Dummy unit movement
						ArrayList<Unit> units = new ArrayList<Unit>();
						units.add(0, player.getUnit());
						units.add(1, player.getUnit());

						int action = rand.nextInt(2);
						try {
							switch (action) {
								case 0:
									server.sendEvent(UnitEvent.UNIT_ATTACK, units);
									break;
								case 1:
									server.sendEvent(UnitEvent.UNIT_HEAL, units);
									break;

								case 2:
									server.sendEvent(UnitEvent.UNIT_MOVE, units);
							}

							Thread.currentThread().sleep((int) (unit.getTurnDelay() * GAME_SPEED * TURN_DELAY));

						} catch (RemoteException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}


					}
				}
			}).start();
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