package rmi;

import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.lang.reflect.Proxy;
import java.io.Serializable;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

public class RMIInvocationHandler implements InvocationHandler, Serializable {

	private String hostname;
	private int port;
	private InetSocketAddress address;
	private Class intface;

	public RMIInvocationHandler(InetSocketAddress address, Class c) {
		this.hostname = address.getHostName();
		this.port = address.getPort();
		this.address = address;
		this.intface = c;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Exception  {

		if(method.getName().equals("toString") &&
				method.getReturnType().getName().equals("java.lang.String") &&
				method.getParameterTypes().length == 0)
			return this.intface.getName() + ":" + this.address.toString();


		if(method.getName().equals("hashCode") &&
				method.getReturnType().getName().equals("int") &&
				method.getParameterTypes().length == 0)
			return this.intface.hashCode() * this.address.hashCode();

		if(method.getName().equals("equals") &&
				method.getReturnType().getName().equals("boolean") &&
				method.getParameterTypes().length == 1 &&
				method.getParameterTypes()[0].toString().contains("Object")) {
			if(args[0] != null){
				try{
					RMIInvocationHandler p = (RMIInvocationHandler) java.lang.reflect.Proxy.getInvocationHandler(args[0]);
					return p.compare(this.intface, this.address);
				} catch (Exception e){
					return false;
				}
			} else
				return false;
		}

		Socket connection;
		int error;
		Object message;

		try {
			connection = new Socket(hostname, port);
			ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(connection.getInputStream());

			out.writeObject(method.getName());
			out.writeObject(method.getParameterTypes());
			out.writeObject(args);
			error = (int) in.readObject();
			message = (Object) in.readObject();
			connection.close();
		} catch (Exception e) {
			throw new RMIException(e);
		}

		if(error == 1)
			throw (Exception) message;
		else if (error == 2){
			throw new RMIException((Exception) message);
		}

		return message;
	}

	private Object convert(Class<?> targetType, String text) {
    	PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
    	editor.setAsText(text);
    	return editor.getValue();
	}

	public boolean compare(Class i, InetSocketAddress ad){
		return this.intface.equals(i) && this.address.equals(ad);
	}
}
