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
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class GameClient  {
    public Server server;
    public Arena arena;
    public Player player;

    public JPanel panel;
    public JTextField username;
    public JProgressBar progressBar1;
    public JButton loginButton;
    private JButton upButton;
    private JButton leftButton;
    private JButton rightButton;
    private JButton downButton;
    public JTextArea consoleArea;
    private JPanel arenaPanel;

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

        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (server.checkSurrounding(player.getUnit(), player.getUnit().getX(), player.getUnit().getY()+ 1)){
                        player.setUnit(server.removeUnit(player.getUnit(), player.getUnit().getX(), player.getUnit().getY()));
                        player.setUnit(server.moveUnit(player.getUnit(), player.getUnit().getX() , player.getUnit().getY() + 1));
                        // Player set to new coordinate
                        consoleArea.append("[Knight " + player.getUnit().getName() + "] Moved to coord (" + player.getUnit().getX() + "," + player.getUnit().getY() + ") of the arena.\n");
                    }
                } catch (RemoteException re) {
                    re.printStackTrace();
                }
            }
        });

        downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (server.checkSurrounding(player.getUnit(), player.getUnit().getX(), player.getUnit().getY() - 1)){
                        player.setUnit(server.removeUnit(player.getUnit(), player.getUnit().getX(), player.getUnit().getY()));
                        player.setUnit(server.moveUnit(player.getUnit(), player.getUnit().getX() , player.getUnit().getY() - 1));
                        // Player set to new coordinate
                        consoleArea.append("[Knight " + player.getUnit().getName() + "] Moved to coord (" + player.getUnit().getX() + "," + player.getUnit().getY() + ") of the arena.\n");
                    }
                } catch (RemoteException re) {
                    re.printStackTrace();
                }
            }
        });

        rightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (server.checkSurrounding(player.getUnit(), player.getUnit().getX() + 1, player.getUnit().getY() + 1)) {
                        player.setUnit(server.removeUnit(player.getUnit(), player.getUnit().getX(), player.getUnit().getY()));
                        player.setUnit(server.moveUnit(player.getUnit(), player.getUnit().getX() + 1, player.getUnit().getY()));

                        // Player set to new coordinate
                        consoleArea.append("[Knight " + player.getUnit().getName() + "] Moved to coord (" + player.getUnit().getX() + "," + player.getUnit().getY() + ") of the arena.\n");
                    }
                } catch (RemoteException re) {
                    re.printStackTrace();
                }
            }
        });

        leftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (server.checkSurrounding(player.getUnit(), player.getUnit().getX() - 1, player.getUnit().getY())){
                        player.setUnit(server.removeUnit(player.getUnit(), player.getUnit().getX(), player.getUnit().getY()));
                        player.setUnit(server.moveUnit(player.getUnit(), player.getUnit().getX() - 1, player.getUnit().getY()));
                        // Player set to new coordinate
                        consoleArea.append("[Knight " + player.getUnit().getName() + "] Moved to coord (" + player.getUnit().getX() + "," + player.getUnit().getY() + ") of the arena.\n");
                    }
                } catch (RemoteException re) {
                    re.printStackTrace();
                }
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
        consoleArea.append("[System] Authenticating to server as "+ username +"...\n");
        try {
            player = server.login( username, "");
            syncArena();
        } catch (RemoteException re) {
            re.printStackTrace();
        }

        if(player!=null && player.isAuthenticated()){
            consoleArea.append("[System] Successfully logged in as "+player.getUsername()+".\n");
            try {
                Knight knight = new Knight(player.getUsername());
                knight = (Knight) server.spawnUnit(knight);
                player.setUnit(knight);
                consoleArea.append("[ " + knight.getName() + "] Spawned at coord (" + knight.getX() + "," + knight.getY() + ") of the arena.\n");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void syncArena() {

        try {
            arena = server.getArena();
        } catch (RemoteException re) {
            re.printStackTrace();
        }

        Unit[][] unitCell = arena.unitCell;
        Component[] components = arenaPanel.getComponents();

        int cellIndex = 0;
        for(int j=0;j<25;j++) {
            for(int i=0;i<25;i++) {
                Component component = components[cellIndex];
                if (component instanceof JLabel)
                {
                    Unit unit = unitCell[i][j];
                    if(unit!=null) {
                        if(unit.getType().equals("dragon"))  ((JLabel) component).setText("D");
                        else  ((JLabel) component).setText("K");
                    } else {
                        ((JLabel) component).setText(" ");
                    }


                }
                cellIndex++;
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
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        //arena = new Arena();
        //Unit[][] unitCell = arena.unitCell;
        arenaPanel = new JPanel(new GridLayout(0, 24));
        arenaPanel.setBorder(new LineBorder(Color.BLACK));

        for(int j=0;j<25;j++) {
            for(int i=0;i<25;i++) {
                /*
                Unit unit = unitCell[i][j];
                if(unit!=null) {
                    if(unit.getType().equals("dragon")) arenaPanel.add(new JLabel("D"));
                    else arenaPanel.add(new JLabel("K"));
                } else {
                    arenaPanel.add(new JLabel(" "));
                }
                */
                JLabel cellLabel =  new JLabel(" ");
                cellLabel.setBorder(new LineBorder(Color.GRAY));
                arenaPanel.add(cellLabel);
            }
        }
    }
}
