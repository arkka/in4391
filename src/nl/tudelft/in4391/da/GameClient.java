package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/2/16.
 */

import nl.tudelft.in4391.da.unit.Knight;
import nl.tudelft.in4391.da.unit.Unit;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class GameClient {
    private static Integer GAME_SPEED = 10; //ms

    public ArrayList<Node> serverNodes;
    public Server server;
    public Player player;
    public Arena arena;
    public Unit unit;

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
        serverNodes.add(new Node(1, Node.DEFAULT_HOST_ADDRESS, 1100, 1200));
        serverNodes.add(new Node(2, Node.DEFAULT_HOST_ADDRESS, 1101, 1201));

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
                try {

                    Unit source = player.getUnit();
                    Unit target = source.clone();

                    target.setCoord(target.getX(), target.getY() + 1);
                    ArrayList<Unit> units = new ArrayList<Unit>();
                    units.add(0, source);
                    units.add(1, target);

                    server.sendEvent(UnitEvent.UNIT_MOVE, units);

                } catch (RemoteException e1) {
//                    e1.printStackTrace();
                    server = findServer();
                }
            }
        });

        downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Unit source = player.getUnit();
                    Unit target = source.clone();

                    target.setCoord(target.getX(), target.getY() - 1);
                    ArrayList<Unit> units = new ArrayList<Unit>();
                    units.add(0, source);
                    units.add(1, target);

                    server.sendEvent(UnitEvent.UNIT_MOVE, units);

                } catch (RemoteException e1) {
//                    e1.printStackTrace();
                    server = findServer();
                }
            }
        });

        rightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Unit source = player.getUnit();
                    Unit target = source.clone();

                    target.setCoord(target.getX() + 1, target.getY());
                    ArrayList<Unit> units = new ArrayList<Unit>();
                    units.add(0, source);
                    units.add(1, target);

                    server.sendEvent(UnitEvent.UNIT_MOVE, units);
                } catch (RemoteException e1) {
//                    e1.printStackTrace();
                    server = findServer();
                }
            }
        });

        leftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Unit source = player.getUnit();
                    Unit target = source.clone();

                    target.setCoord(target.getX() - 1, target.getY());
                    ArrayList<Unit> units = new ArrayList<Unit>();
                    units.add(0, source);
                    units.add(1, target);

                    server.sendEvent(UnitEvent.UNIT_MOVE, units);
                } catch (RemoteException e1) {
//                    e1.printStackTrace();
                    server = findServer();
                }
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

                bestServer = s;

                break;
//                t = System.currentTimeMillis();
//
//                try {
//                    if (s.ping()) {
//                        latency = System.currentTimeMillis() - t;
//                        consoleLog("[System] Game server " + n.getFullName() + " is available. ("+ latency +"ms)");
//                    }
//                } catch (RemoteException e) {
//                    //e.printStackTrace();
//                    latency = maxLatency;
//                    consoleLog("[System] Game server " + n.getFullName() + " is down.");
//                }
//                n.setLatency(latency);
//
//                if (latency < bestLatency) {
//                    bestLatency = latency;
//                    bestServer = s;
//                    bestNode = n;
//                }
            }
        }
//        if(bestServer!=null)
//            consoleLog("[System] Connected to Game Server " + bestNode.getFullName() + ". ("+ bestLatency +"ms)");
//        else
//            consoleLog("[System] No available game server. Please try again later.");

        return bestServer;
    }


    public void login(String username, String password) {
        consoleLog("[System] Authenticating to server as `"+ username +"`...");
        try {
            player = server.login( username, "", "Knight");
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

                        if (arena == null) {
                            Thread.currentThread().interrupt();//preserve the message
                            return;
//                    break;
                        }

                        arena.syncUnits();

                        unit = arena.getMyUnit(player);

                        if (unit == null) {
                            Thread.currentThread().interrupt();//preserve the message
                            return;
//                    break;
                        }

                        player.setUnit(unit);

                        if (player.getUnit() == null){
                            Thread.currentThread().interrupt();//preserve the message
                            return;
//                    break;
                        }
                        renderArena();
                        Thread.sleep(GAME_SPEED);
                    }
                } catch (RemoteException e) {
//                    e.printStackTrace();
                    server = findServer();
                    updateArena();
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    server = findServer();
                    updateArena();
                }
            }
        }).start();
    }

    public void renderArena() {
        Component[] components = arenaPanel.getComponents();

        int cellIndex = 0;
        for(int j=24;j>=0;j--) {
            for(int i=0;i<25;i++) {
                Component component = components[cellIndex];
                if (component instanceof JLabel)
                {
                    Unit unit = arena.unitCell[i][j];
                    if(unit!=null) {
                        if(unit.getType().equals("Dragon"))  {
                            ((JLabel) component).setToolTipText("Dragon ["+unit.getHitPoints()+"/"+unit.getMaxHitPoints()+"]");
                            ((JLabel) component).setText("D");
                            ((JLabel) component).setForeground(Color.WHITE);
                            ((JLabel) component).setBackground(Color.RED);
                        }
                        else  {
                            ((JLabel) component).setText("K");
                            if(unit.equals(player.getUnit())) {
                                ((JLabel) component).setForeground(Color.WHITE);
                                ((JLabel) component).setBackground(Color.BLUE);
                            } else {
                                ((JLabel) component).setForeground(Color.BLACK);
                                ((JLabel) component).setBackground(Color.GRAY);
                            }
                        }

                    } else {
                        ((JLabel) component).setText(" ");
                        ((JLabel) component).setForeground(Color.WHITE);
                        ((JLabel) component).setBackground(Color.WHITE);
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
                cellLabel.setHorizontalTextPosition(JLabel.CENTER);
                cellLabel.setSize(10,10);
                cellLabel.setOpaque(true);
                cellLabel.setBorder(new LineBorder(Color.GRAY));
                arenaPanel.add(cellLabel);
            }
        }
    }


}
