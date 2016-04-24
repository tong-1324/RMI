package rmi;

import java.net.*;
import java.io.*;

public class ServerThread extends Thread {
	
	private Skeleton skeleton = null;
	private ServerSocket ssocket = null;
	
	public ServerThread(Skeleton skeleton, ServerSocket ssocket) {
		
		this.skeleton = skeleton;
		this.ssocket = ssocket;
	}
	
	public void run()  {
		while(skeleton.isRunning()) {
			try {
				Socket connection = ssocket.accept();
				RMIReceiver receiver = new RMIReceiver(connection, skeleton);
				receiver.start();
			} catch (Exception e) {
				skeleton.setisRunning(false);
				skeleton.stopped(e);
			}						
		}		
					
	}
}