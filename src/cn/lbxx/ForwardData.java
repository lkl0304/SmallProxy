package cn.lbxx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class ForwardData extends Thread {
	private List<String> buff;
	private Socket       source;
	private Socket       gold;
	
	public ForwardData(List<String> buff, Socket sou, Socket go) {
		this.buff   = buff;
		this.gold   = go  ;
		this.source = sou ;
	}
	
	@Override
	public void run() {
		try {
			OutputStream outStream = gold.getOutputStream();
			InputStream  inStream  = source.getInputStream();

			if(buff != null && buff.size() > 0){
				for(String str : buff)
				{
					outStream.write((str + "\r\n").getBytes());
					outStream.flush();
				}
				outStream.write("\r\n".getBytes());
				outStream.flush();
			}

			byte[] bs = new byte[4096];
			int len;
			while ((len = inStream.read(bs)) != -1) {
				outStream.write(bs, 0, len);
				outStream.flush();
			}
			
			outStream.close();
			inStream.close();
		} catch (IOException ie) {
			System.err.println("http Êý¾Ý×ª·¢Òì³£ " + ie);
		}
	}
}
