package com.gmail.shellljx.MultiServerChat_Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.bukkit.ChatColor;

public class SocketUtil {

	private Client plugin;
	
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private MessageThread messageThread;
	private boolean isConnected = false;
	
	public SocketUtil(Client plugin){
		this.plugin = plugin;
	}
	
	public boolean connectServer(int port, String hostIp, String name) {
		try {
			socket = new Socket(hostIp, port);
			writer = new PrintWriter(socket.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			sendMessage(name + "@" + socket.getLocalAddress().toString());
			messageThread = new MessageThread(reader);
			messageThread.start();
			isConnected = true;
			return true;
		} catch (Exception e) {
			isConnected = false;
			return false;
		}
	}
	
	public boolean getIsConnected(){
		return this.isConnected;
	}
	public synchronized boolean closeConnection() {
		try {
			sendMessage("CLOSE");
			messageThread.stop();
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (socket != null) {
				socket.close();
			}
			isConnected = false;
			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			isConnected = true;
			return false;
		}
	}
	
	public void sendMessage(String message){
		writer.println(message);
		writer.flush();
	}
	
	class MessageThread extends Thread{
		private BufferedReader reader;
		
		public MessageThread(BufferedReader reader) {
			this.reader = reader;
		}
		
		public synchronized void closeCon() {

			try{
				if (reader != null) {
					reader.close();
				}
				if (writer != null) {
					writer.close();
				}
				if (socket != null) {
					socket.close();
				}
				isConnected = false;
			}catch(Exception e){
				System.out.println(e.getStackTrace());
			}
		}

		@Override
		public void run() {
			String message = "";
			
			try {
				while(true){
					message = reader.readLine();
					if(message.equalsIgnoreCase("CLOSE")){
						this.closeCon();
						return;
					}
					plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&',message));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
}
