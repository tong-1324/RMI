package rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.*;

public class RMIReceiver extends Thread {

	private Socket connection;
	private Skeleton skeleton;

	public RMIReceiver(Socket connection, Skeleton skeleton) {
		this.connection = connection;
		this.skeleton = skeleton;
	}

	public void run() {

			try {
				ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(connection.getInputStream());

				Object methodName = in.readObject();
				Object parameterTypes = in.readObject();
				Object returnType = in.readObject();
				Object args = in.readObject();

				try{
					Method serverMethod = skeleton.getIntface().getMethod((String)methodName,(Class[])parameterTypes);
				} catch (Exception e){
					out.writeObject(2);
					out.writeObject(e);
				}

				try {
					Method serverMethod = skeleton.getIntface().getMethod((String)methodName,(Class[])parameterTypes);
					Object serverReturn = serverMethod.invoke(skeleton.getServer(), (Object [])args);
					out.writeObject(0);
					out.writeObject(serverReturn);
				} catch(Exception e){
					out.writeObject(1);
					out.writeObject(e.getCause());
				}
				connection.close();
			}
			catch (Exception e){
				skeleton.service_error(new RMIException(e));
			}
	}
}
