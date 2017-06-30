package cn.lbxx;

import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HandleRequest extends Thread {
	private static String KHost = "Host:";
	
	private   Socket       clSocket   ;
	private   Socket       seSocket   ;
	private   Scanner      cdata      ;
	
	protected String       remotehost ;
	protected int          remoteport ;
	protected boolean      https      ;
	private   List<String> bufflist   ;
	
	public HandleRequest(Socket c){
		this.clSocket = c;
		this.bufflist = new ArrayList<String>();
	}
	
	public void run()
	{
		try {
			cdata = new Scanner(clSocket.getInputStream());
			int beginIndex = KHost.length() + 1;
			String line;
			while(cdata.hasNextLine() && (line = cdata.nextLine()).length() != 0)
			{
				if(line.length() > 5)
				{
					if(line.substring(0, KHost.length()).equals(KHost))
					{
						int hend;
						if((hend = line.indexOf(':', beginIndex)) != -1)
						{
							remotehost = line.substring(beginIndex, hend);
							remoteport = Integer.parseInt(line.substring(hend + 1));
						} else {
							remotehost = line.substring(beginIndex);
							remoteport = 80;
						}
					}
					if(line.substring(0, line.indexOf(' ')).equals("CONNECT")){
						https = true;
					}
				}
				bufflist.add(line);
			}
			
			System.out.println(remotehost + " -> " + remoteport + "  " + https);
			
			if(remotehost != null)
			{
				seSocket = new Socket(remotehost, remoteport);
				if (https) {
					List<String> list = new ArrayList<>();
					list.add("HTTP/1.1 200 Connection Established");
					
					new ForwardData(list, seSocket, clSocket).start();
					new ForwardData(null, clSocket, seSocket).start();
				} else {
					toUri(bufflist);
					new ForwardData(bufflist, clSocket, seSocket).start();
					new ForwardData(null, seSocket, clSocket).start();
				}
			}
		} catch (ConnectException c) {
			System.err.println("连接超时");
		} catch (SocketException se) {
			System.err.println("无法连接-> " + remotehost + ":" + remoteport);
		} catch (Exception e) {
			System.err.println("发生错误" + e);
		}
	}
	
	private void toUri(List<String> buff)
	{
		for (int i = 0; i < buff.size(); i++)
        {
			String line = buff.get(i);
			
			String head = line.substring(0, line.indexOf(' '));
			int    hlen = head.length() + 1;
			if(line.substring(hlen, hlen + 7).equals("http://"))
			{
				String uri = line.substring(line.indexOf('/', hlen + 7));
				buff.set(i, head + " " + uri);
				break;
			}
        }
	}
}
