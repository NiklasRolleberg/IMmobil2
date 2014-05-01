package niklas.v2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
	int port;
	MainActivity main;
	
	ServerSocket serverSocket;
	Socket clientSocket;
	
	String name;
	String color;
	
	int i = 0;
	
	boolean goOn = true;
	
	/**
	 * @param m
	 * InstantMessaging object
	 * 
	 * @param port
	 * the port to listen to
	 */
	public Server(MainActivity m, int port, String name, String color) {
		this.port = port;
		main = m;
		this.name = name;
		this.color = color;
	}
	
	public void stop() {
		goOn = false;
	}
	
	
	@Override
	public void run() {
		//System.out.println("Servern är på!");
		
		try {
		    serverSocket = new ServerSocket(port);
		} catch (IOException e) {
		    System.out.println("Could not listen on port: "+port);
		    System.out.println(e);
		}

		while(goOn){
		    Socket clientSocket = null;
		    try {
		    	clientSocket = serverSocket.accept();
		    } catch (IOException e) {}
		    Client c = new Client(name, color, clientSocket);
		    main.addClient(c, "Incoming"+(++i));
		}		
	}
}
