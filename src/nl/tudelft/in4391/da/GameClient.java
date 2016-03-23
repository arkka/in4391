package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import nl.tudelft.in4391.da.ui.ClientUI;
import nl.tudelft.in4391.da.unit.Knight;
import nl.tudelft.in4391.da.unit.Unit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

public class GameClient  {
    public Server server;
    public Player player;

    public JPanel panel;
    public JTextField username;
    public JProgressBar progressBar1;
    public JButton loginButton;
    private JButton upButton;
    private JButton leftButton;
    private JButton rightButton;
    private JButton downButton;
    private JTable table1;
    public JTextArea consoleArea;

    public GameClient() {
        server = null;
        player = null;

        findAndConnectServer();


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login(username.getText(),"");
            }
        });
    }

    public void findAndConnectServer(){
        // Pre-defined game coordinator nodes
        ArrayList<Node> masterNodes = new ArrayList<Node>();
        masterNodes.add(new Node(1,"127.0.0.1",1100,1200));
        masterNodes.add(new Node(2,"127.0.0.1",1101,1201));

        while(server == null) {
            for (Node n : masterNodes) {
                try {
                    Registry remoteRegistry = LocateRegistry.getRegistry(n.getHostAddress(), n.getRegistryPort());
                    server = (Server) remoteRegistry.lookup(n.getName());
                    consoleArea.append("[System] Connected to Master Server " + n.getFullName() + ".\n");
                    break;
                } catch (Exception e) {
                    consoleArea.append("[Error] Unable to connect to game coordinator server " + n.getFullName() + ".\n");
                }
            }

            if(server==null) {
                consoleArea.append("[System] Retrying connect to server in 5 seconds.\n");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        consoleArea.append("[System] Welcome to Dragon Arena: Distributed Reborn!\n");
    }

    public void login(String username, String password) {
        consoleArea.append("[System] Authenticating to server as "+ username +"...");
        try {
            player = server.login( username, "");
        } catch (RemoteException re) {
            re.printStackTrace();
        }

        if(player!=null && player.isAuthenticated()){
            consoleArea.append("[System] Successfully logged in as "+player.getUsername()+".");
            try {
                Knight knight = new Knight(player.getUsername());
                knight = (Knight) server.spawnUnit(knight);
                player.setUnit(knight);
                consoleArea.append("[ " + knight.getName() + "] Spawned at coord (" + knight.getX() + "," + knight.getY() + ") of the arena.");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args)
    {
        GameClient ui = new GameClient();
        JFrame frame = new JFrame("Dragon Arena: Distributed Reborn");
        frame.setContentPane(ui.panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);



        /*
        // Login
        Scanner s = new Scanner(System.in);


        do {
            // Input authentication credentials
            System.out.print("Username: ");
            username = s.nextLine().trim();

            // TO-DO: Implement password authentication
            //System.out.println("Password: ");
            //password = s.nextLine().trim();

            System.out.println("[System] Authenticating to server as "+username+"...");
            try {
                player = server.login(username,password);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } while(!player.isAuthenticated());



        // COMMAND
        String command = "";

        while(true){
            command = s.nextLine().trim();
            Unit unit = player.getUnit();
            int posX = unit.getX();
            int posY = unit.getY();
            switch(command) {
                case "up":
                    try {
                        if (server.checkSurrounding(player.getUnit(), posX, posY+ 1)){
                            player.setUnit(server.removeUnit(unit, posX, posY));
                            player.setUnit(server.moveUnit(unit, posX , posY + 1));
                            // Player set to new coordinate
                            System.out.println("[Knight " + unit.getName() + "] Moved to coord (" + unit.getX() + "," + unit.getY() + ") of the arena.");
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case "down":
                    try {
                        if (server.checkSurrounding(player.getUnit(), posX, posY - 1)){
                            player.setUnit(server.removeUnit(unit, posX, posY));
                            player.setUnit(server.moveUnit(unit, posX , posY - 1));
                            // Player set to new coordinate
                            System.out.println("[Knight " + unit.getName() + "] Moved to coord (" + unit.getX() + "," + unit.getY() + ") of the arena.");
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case "left":
                    try {
                        if (server.checkSurrounding(player.getUnit(), posX - 1, posY)){
                            player.setUnit(server.removeUnit(unit, posX, posY));
                            player.setUnit(server.moveUnit(unit, posX - 1, posY));
                            // Player set to new coordinate
                            System.out.println("[Knight " + unit.getName() + "] Moved to coord (" + unit.getX() + "," + unit.getY() + ") of the arena.");
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case "right":
                    try {
                        if (server.checkSurrounding(player.getUnit(), posX + 1, posY+ 1)){
                            player.setUnit(server.removeUnit(unit, posX, posY));
                            player.setUnit(server.moveUnit(unit, posX + 1 , posY));
                            // Player set to new coordinate
                            System.out.println("[Knight " + unit.getName() + "] Moved to coord (" + unit.getX() + "," + unit.getY() + ") of the arena.");
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:
                    break;
            }

        }
        */
    }
}
