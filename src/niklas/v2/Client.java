package niklas.v2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import tools.XMLparser;

import InterFaces.Observer;
import InterFaces.Subject;

public class Client implements Runnable, Subject{
	
	String me; //My name
	String color;
	String ip;
	int port;
	
	PrintWriter out;
	BufferedReader in;
	Scanner scanner;
	
	String chattHistory = "";
	Socket clientSocket = null;
	
	ArrayList<Observer> observers;
	
	//Reason for connection failed
	int reason = 0;
	
	Client(String name,String color ,String ip,int port){
		me = name;
		this.color = color;
		this.ip = ip;
		this.port = port;
	}
	
	Client(String name,String color, Socket s) {
		clientSocket = s;
		me = name;
		this.color = color;
	}

	@Override
	public void run() {
		try {
			
			if (clientSocket == null) {
				clientSocket = new Socket(ip,port);
			}
			
			out = new PrintWriter(clientSocket.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			scanner = new Scanner(in).useDelimiter("</message>");
			
		} catch (UnknownHostException e) {
			//chattHistory += "UnknownHostException \n";
			reason = 1;
			notifyObservers();
		} catch (IOException e) {
			//chattHistory += "IOException \n";
			reason = 2;
			notifyObservers();
		}
		
		chattHistory += "<font color=#0000ff> Connection sucsessful </font><br>";
		notifyObservers();
		
		String instr;
		try {
			while ((instr = scanner.next()) != null) {
				//chattHistory += instr+"\n";

				int i = XMLparser.type(instr);
		        switch (i) {
		        
		        	//case -1: chattHistory+="Broken tag \n";
       			 	//		 break;
		        	case 0:  chattHistory+="Unknown tag \n";
		        			 break;
		            case 1:  chattHistory+=XMLparser.getTextMessage(instr);
		                     break;
		            case 2:  reason = 3;
		                     break;
		            default: chattHistory += "<font color=#0000ff> something went wrong </font><br>";
		                     break;
		        }
				notifyObservers();
			}
		}catch (Exception e){};
		
		//connection lost
		switch(reason) {
			case 1: chattHistory += "<font color=RED> UnknownHostException </font><br>";
				break;
			case 2: chattHistory += "<font color=RED> IOEcxeption </font><br>";
				break;
			case 3: chattHistory += "<font color=RED> partner has disconnected </font><br>";
				break;
			default: chattHistory += "<font color=RED> something went wrong</font><br>";
				break;
		}
		notifyObservers();
		disconnect();
	}
	
	public void disconnect() {
		try{
			out.println("<message sender="+'"'+me+'"'+'>'+"<disconnect/></message>");
		}catch (Exception e) {}
		
		try {
			out.close();
			scanner.close();
			in.close();
			clientSocket.close();
		} catch (Exception e) {}
	}
	
	/**sends a string*/
	public void send(String s) {
		if (clientSocket != null && out != null && s.length() != 0) {
			chattHistory += "<font color=#"+color+">me: "+s+"</font><br>";
			out.println("<message sender="+'"'+me+'"'+'>'+ "<text color=#"+color+">"
					+XMLparser.replace(s)+ "</text>" + "</message>");
			notifyObservers();
		}
	}

	@Override
	public void registerObserver(Observer observer) {
		if (observers == null) {
			observers = new ArrayList<Observer>(); 
		}
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
		notifyObservers();
	}

	@Override
	public void removeObserver(Observer observer) {
		observers.clear();
	}

	@Override
	public void notifyObservers() {
		if (observers != null) {
			for (Observer o:observers) {
				try {
					o.update(chattHistory);
				}
				catch(Exception e) {
				}
			}
		}
		
	}
	
	@Override
	public void requestUpdate() {
		notifyObservers();
	}	
}
