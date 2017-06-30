package cn.lbxx;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LocalServer{
	private static int   LPort   ; 
	private ServerSocket loSocket; 
	
	public LocalServer() throws IOException {
		loSocket = new ServerSocket(LPort);
	}
	public void start()
	{
		try {
			System.out.println("run on " + LPort);
			while(true)
			{
				Socket ct = loSocket.accept();
				new HandleRequest(ct).start(); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		LPort = 8853;
		try {
			new LocalServer().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
