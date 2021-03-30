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
		UploadThread up = new UploadThread(addressIndex,filename);
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
				String fileName = "videoTest2.mp4";
				//String fileName = "helloWorld.txt";
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
	String filename;
	public UploadThread(int i, String fn) {
		this.addressIndex = i;
		this.filename = fn;
	}
	public void run() {
			String host = Client.host[this.addressIndex];
			int port = Client.port[this.addressIndex];
			
			String sendServer = "Send to server";
			try {
				// Open file
				File f = new File("/home/duc/eclipse-workspace/Project/src/video/"+this.filename);
				Socket threadSocket = new Socket(host,port);
				FileInputStream is = new FileInputStream(f);
				OutputStream os = threadSocket.getOutputStream();
				byte bytes[] = new byte[256*1024]; // 256 KB
				int nb=0;
				while(true){
					nb = is.read(bytes,0,bytes.length);
					if(nb == -1) {
						break;
					}
					os.write(bytes, 0, nb);
					os.flush();
				}
				System.out.println("Sended: "+bytes.length+" bytes");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}

