package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/2/16.
 */

import nl.tudelft.in4391.da.unit.Unit;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class GameClient {
    private static Integer GAME_SPEED = 100; //ms

    public ArrayList<Node> serverNodes;
    public Server server;
    public Player player;
    public Arena arena;

    public JPanel panel;
    public JTextField username;
    public JProgressBar progressBar;
    public JButton loginButton;
    private JButton upButton;
    private JButton leftButton;
    private JButton rightButton;
    private JButton downButton;
    public JTextArea console;
    private JPanel arenaPanel;

    public GameClient() {
        // Server Nodes
        serverNodes = new ArrayList<Node>();
        serverNodes.add(new Node(1, "127.0.0.1", 1100, 1200));
        serverNodes.add(new Node(2, "127.0.0.1", 1101, 1201));

        // Server object based on latency
        server = findServer();
        player = null;
        arena = new Arena();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(player == null) { // LOGIN
                    if(server == null) server = findServer();
                    login(username.getText(),"");
                    loginButton.setText("Logout");
                } else { // LOGOUT
                    logout();
                    loginButton.setText("Login");
                }

            }
        });

        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*
                try {
                    if (server.checkSurrounding(player.getUnit(), player.getUnit().getX(), player.getUnit().getY()+ 1)){
                        player.setUnit(server.moveUnit(player.getUnit(), player.getUnit().getX() , player.getUnit().getY() + 1));
                        // Player set to new coordinate
                        consoleArea.append("[Knight " + player.getUnit().getName() + "] Moved to coord (" + player.getUnit().getX() + "," + player.getUnit().getY() + ") of the arena.\n");
                        syncArena();
                    } else { // There are unit, Dragon or Knight
                        Unit adjacentunit = server.actionToSurroundingUnit(player.getUnit(), player.getUnit().getX(), player.getUnit().getY()+ 1);
                        if (adjacentunit.getType().equals("dragon")){
                            // Damage dragon
                            consoleArea.append("[Knight " + player.getUnit().getName() + "] damage " + adjacentunit.getName() + " by " + player.getUnit().getAttackPoints() + ".\n");
                            consoleArea.append("[" + adjacentunit.getName() + "] damaged to " + adjacentunit.getHitPoints() + "/" + adjacentunit.getMaxHitPoints() + " HP.\n");
                        } else { // Knight
                            // Heal player
                            consoleArea.append("[Knight " + player.getUnit().getName() + "] heal " + adjacentunit.getName() + " by " + player.getUnit().getAttackPoints() + ".\n");
                            consoleArea.append("[Knight " + adjacentunit.getName() + "] healed to " + adjacentunit.getHitPoints() + "/" + adjacentunit.getMaxHitPoints() + " HP.\n");
                        }
                        // Check whether unit dead after damage
                        // Case1: Knight attack Dragon
                        // Case2: Dragon attack Knight
                        // Delete unit
                        if (server.checkDead(adjacentunit)) {
                            server.deleteUnit(adjacentunit);
                            syncArena();
                        }
                    }
                } catch (RemoteException re) {
                    re.printStackTrace();
                }
                */
            }
        });

        downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*
                try {
                    if (server.checkSurrounding(player.getUnit(), player.getUnit().getX(), player.getUnit().getY() - 1)){
                        player.setUnit(server.moveUnit(player.getUnit(), player.getUnit().getX() , player.getUnit().getY() - 1));
                        // Player set to new coordinate
                        consoleArea.append("[Knight " + player.getUnit().getName() + "] Moved to coord (" + player.getUnit().getX() + "," + player.getUnit().getY() + ") of the arena.\n");
                        syncArena();
                    } else { // There are unit, Dragon or Knight
                        Unit adjacentunit = server.actionToSurroundingUnit(player.getUnit(), player.getUnit().getX(), player.getUnit().getY() - 1);
                        if (adjacentunit.getType().equals("dragon")){
                            // Damage dragon
                            consoleArea.append("[Knight " + player.getUnit().getName() + "] damage " + adjacentunit.getName() + " by " + player.getUnit().getAttackPoints() + ".\n");
                            consoleArea.append("[" + adjacentunit.getName() + "] damaged to " + adjacentunit.getHitPoints() + "/" + adjacentunit.getMaxHitPoints() + " HP.\n");
                        } else { // Knight
                            // Heal player
                            consoleArea.append("[Knight " + player.getUnit().getName() + "] heal " + adjacentunit.getName() + " by " + player.getUnit().getAttackPoints() + ".\n");
                            consoleArea.append("[Knight " + adjacentunit.getName() + "] healed to " + adjacentunit.getHitPoints() + "/" + adjacentunit.getMaxHitPoints() + " HP.\n");
                        }
                        // Check whether unit dead after damage
                        // Case1: Knight attack Dragon
                        // Case2: Dragon attack Knight
                        // Delete unit
                        if (server.checkDead(adjacentunit)) {
                            server.deleteUnit(adjacentunit);
                            syncArena();
                        }
                    }
                } catch (RemoteException re) {
                    re.printStackTrace();
                }
                */
            }
        });

        rightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*
                try {
                    if (server.checkSurrounding(player.getUnit(), player.getUnit().getX() + 1, player.getUnit().getY())) {
                        player.setUnit(server.moveUnit(player.getUnit(), player.getUnit().getX() + 1, player.getUnit().getY()));

                        // Player set to new coordinate
                        consoleArea.append("[Knight " + player.getUnit().getName() + "] Moved to coord (" + player.getUnit().getX() + "," + player.getUnit().getY() + ") of the arena.\n");
                        syncArena();
                    } else { // There are unit, Dragon or Knight
                        Unit adjacentunit = server.actionToSurroundingUnit(player.getUnit(), player.getUnit().getX() + 1, player.getUnit().getY());
                        if (adjacentunit.getType().equals("dragon")){
                            // Damage dragon
                            consoleArea.append("[Knight " + player.getUnit().getName() + "] damage " + adjacentunit.getName() + " by " + player.getUnit().getAttackPoints() + ".\n");
                            consoleArea.append("[" + adjacentunit.getName() + "] damaged to " + adjacentunit.getHitPoints() + "/" + adjacentunit.getMaxHitPoints() + " HP.\n");
                        } else { // Knight
                            // Heal player
                            consoleArea.append("[Knight " + player.getUnit().getName() + "] heal " + adjacentunit.getName() + " by " + player.getUnit().getAttackPoints() + ".\n");
                            consoleArea.append("[Knight " + adjacentunit.getName() + "] healed to " + adjacentunit.getHitPoints() + "/" + adjacentunit.getMaxHitPoints() + " HP.\n");
                        }
                        // Check whether unit dead after damage
                        // Case1: Knight attack Dragon
                        // Case2: Dragon attack Knight
                        // Delete unit
                        if (server.checkDead(adjacentunit)) {
                            server.deleteUnit(adjacentunit);
                            syncArena();
                        }
                    }
                } catch (RemoteException re) {
                    re.printStackTrace();
                }
                */
            }
        });

        leftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*
                try {
                    if (server.checkSurrounding(player.getUnit(), player.getUnit().getX() - 1, player.getUnit().getY())){
                        player.setUnit(server.moveUnit(player.getUnit(), player.getUnit().getX() - 1, player.getUnit().getY()));
                        // Player set to new coordinate
                        consoleArea.append("[Knight " + player.getUnit().getName() + "] Moved to coord (" + player.getUnit().getX() + "," + player.getUnit().getY() + ") of the arena.\n");
                        syncArena();
                    } else { // There are unit, Dragon or Knight
                        Unit adjacentunit = server.actionToSurroundingUnit(player.getUnit(), player.getUnit().getX() - 1, player.getUnit().getY());
                        if (adjacentunit.getType().equals("dragon")){
                            // Damage dragon
                            consoleArea.append("[Knight " + player.getUnit().getName() + "] damage " + adjacentunit.getName() + " by " + player.getUnit().getAttackPoints() + ".\n");
                            consoleArea.append("[" + adjacentunit.getName() + "] damaged to " + adjacentunit.getHitPoints() + "/" + adjacentunit.getMaxHitPoints() + " HP.\n");
                        } else { // Knight
                            // Heal player
                            consoleArea.append("[Knight " + player.getUnit().getName() + "] heal " + adjacentunit.getName() + " by " + player.getUnit().getAttackPoints() + ".\n");
                            consoleArea.append("[Knight " + adjacentunit.getName() + "] healed to " + adjacentunit.getHitPoints() + "/" + adjacentunit.getMaxHitPoints() + " HP.\n");
                        }
                        // Check whether unit dead after damage
                        // Case1: Knight attack Dragon
                        // Case2: Dragon attack Knight
                        // Delete unit
                        if (server.checkDead(adjacentunit)) {
                            server.deleteUnit(adjacentunit);
                            syncArena();
                        }
                    }
                } catch (RemoteException re) {
                    re.printStackTrace();
                }
                */
            }
        });


        /*
         *  SHUTDOWN THREAD
         *  Exit gracefully
         */

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                consoleLog("[System] Logout and disconnect from server...");
                logout();
                consoleLog("Bye!");
            }
        });
    }

    public Server findServer() {
        Server bestServer = null;
        Node bestNode = null;

        long t = 0;
        long latency = 0;
        long maxLatency = 10000; // 10 seconds
        long bestLatency = maxLatency;

        // Ping all server and find the best latency
        for (Node n : serverNodes) {
            Server s = ServerImpl.fromRemoteNode(n);
            if(s!=null) {
                t = System.currentTimeMillis();

                try {
                    if (s.ping()) {
                        latency = System.currentTimeMillis() - t;
                        consoleLog("[System] Game server " + n.getFullName() + " is available. ("+ latency +"ms)");
                    }
                } catch (RemoteException e) {
                    //e.printStackTrace();
                    latency = maxLatency;
                    consoleLog("[System] Game server " + n.getFullName() + " is down.");
                }
                n.setLatency(latency);

                if (latency < bestLatency) {
                    bestLatency = latency;
                    bestServer = s;
                    bestNode = n;
                }
            }
        }
        if(bestServer!=null)
            consoleLog("[System] Connected to Game Server " + bestNode.getFullName() + ". ("+ bestLatency +"ms)");
        else
            consoleLog("[System] No available game server. Please try again later.");

        return bestServer;
    }


    public void login(String username, String password) {
        consoleLog("[System] Authenticating to server as `"+ username +"`...");
        try {
            player = server.login( username, "");
            if(player!=null) {
                consoleLog("[System] Successfully logged in as `"+ username +"`");
                updateArena();
            }
        } catch (RemoteException re) {
            re.printStackTrace();
            consoleLog("[System] Authentication as "+ username +" failed.");
        }
    }

    public void logout() {
        try {
            server.logout(player);
            server = null;
            player = null;
            arena = new Arena();
            renderArena();

            consoleLog("[System] Successfully logged out from server.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateArena() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(server!=null) {
                        //consoleLog("[System] Sync arena map.");
                        arena = server.getArena();
                        renderArena();
                        Thread.sleep(GAME_SPEED);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void renderArena() {
        Component[] components = arenaPanel.getComponents();

        int cellIndex = 0;
        for(int j=24;j>0;j--) {
            for(int i=0;i<25;i++) {
                Component component = components[cellIndex];
                if (component instanceof JLabel)
                {
                    Unit unit = arena.unitCell[i][j];
                    if(unit!=null) {
                        if(unit.getType().equals("Dragon"))  ((JLabel) component).setText("D");
                        else  ((JLabel) component).setText("K");
                    } else {
                        ((JLabel) component).setText(" ");
                    }
                }
                cellIndex++;
            }
        }
    }

    public void consoleLog(String message) {
        console.append(message);
        console.append("\n");
        console.setCaretPosition(console.getDocument().getLength());
    }

    public static void main(String[] args)
    {
        GameClient ui = new GameClient();
        JFrame frame = new JFrame("Dragon Arena: Distributed Reborn");
        frame.setContentPane(ui.panel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1250,700);
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        arenaPanel = new JPanel(new GridLayout(0, 25));
        arenaPanel.setBorder(new LineBorder(Color.BLACK));

        for(int j=0;j<25;j++) {
            for(int i=0;i<25;i++) {
                JLabel cellLabel =  new JLabel(" ");
                cellLabel.setBorder(new LineBorder(Color.GRAY));
                arenaPanel.add(cellLabel);
            }
        }
    }


}
