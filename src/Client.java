import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.util.Hashtable;

import java.util.*;
import java.io.*;

public class Client implements ManageFile{
	public static byte fileContent[] = new byte[1024]; 
	public static int port[] = new int[10];
	public static String host[] = new String[10];
	public static void store(int addressIndex, String filename) {
		UploadThread up = new UploadThread(addressIndex);
		up.start();
	}

	@Override
	public void download(String filename) {
	
	}
	public static void main(String args[]) {
		NameNode nn;
		int index = 0;
		try {
			nn = (NameNode) Naming.lookup("//localhost/nameNode");
			Hashtable<String, Host> listOfDataNode = nn.consult();
			int numberOfDataNode = listOfDataNode.size();
			System.out.println("Number of data node: "+numberOfDataNode);
			System.out.println(listOfDataNode.get("8082").getHost());
			List<Host> ListInformationOfDataNode = new ArrayList<>(listOfDataNode.values());
			Iterator<Host> itr = ListInformationOfDataNode.iterator();
			System.out.println("Hello");
			while(itr.hasNext()) {
				Host a = itr.next();
				port[index] = a.getPort();
				host[index] = a.getHost();
				index = index+1;
				System.out.println(a.getHost()+":"+a.getPort());
			}
			for(int i = 0; i < ListInformationOfDataNode.size(); i++) {
				String fileName = "Non";
				store(i,fileName);
				//UploadThread up = new UploadThread(i);
				//up.start();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
/*
public class downloadThread extends Thread{

}*/
class UploadThread extends Thread{
	int addressIndex;
	public UploadThread(int i) {
		this.addressIndex = i;
	}
	public void run() {
			String host = Client.host[this.addressIndex];
			int port = Client.port[this.addressIndex];
			
			String sendServer = "Send to server";
			try {
				Socket threadSocket = new Socket(host,port);
				OutputStream cos = threadSocket.getOutputStream();
				byte bufferOutput[] = sendServer.getBytes();
				cos.write(bufferOutput,0,bufferOutput.length);
				
				InputStream is = threadSocket.getInputStream();
				byte bufferInput[] = new byte[1024];
				is.read(bufferInput);
				String strReceiv = new String(bufferInput,StandardCharsets.UTF_8);
				System.out.println("From server: "+strReceiv);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}

