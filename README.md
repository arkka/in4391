# How to Start
1. Start RMI Registry using this command. Change the codebase parameter with the absolute path of your project folder.
```
rmiregistry -J-Djava.rmi.server.codebase=file:///Users/arkkadhiratara/Workspaces/in4391/bin/
```
2. Run GameServer
3. Before running GameClient, please do as follows. (Perlu dilakukan sementara belum ada implementasi ServerDiscovery) 
- Take a note on ServerGame host and port. For example,`[System] Server is ready on ***192.168.1.10***:1101` means server host is `192.168.1.1` and server port `1101`.
- Adjust run parameter (either using IDE run configuration or direct command prompt) for GameClient.java. Specified different client callback address if running on localhost. `java GameClient.java 192.168.1.10 1101 1201`
- Set username 
4. Run Second GameClient, please follow step 3 but use another username (TO-DO: username conflict avoidance)

# Server and Client Port (on localhost implementation)
GameServer (Registry): 1101 ~ 1120
GameClient (Callback): 1201 ~ ....