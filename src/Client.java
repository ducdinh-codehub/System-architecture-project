import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.util.Hashtable;

import java.util.*;
import java.awt.CardLayout;
import java.io.*;

public class Client implements ManageFile{
	public static int port[] = new int[10];
	public static String host[] = new String[10];
	public static int hnb = 0;
	public static Hashtable<String, Host> listOfDataNode;

	public static void store(int addressIndex, String filename, String filePath) {
		UploadThread up = new UploadThread(addressIndex,filename, filePath);
		up.start();
	}

	
	public static void download(int addressIndex, String filename, OutputStream osDW) {
		DownloadThread dt = new DownloadThread(addressIndex, filename, osDW);
		dt.start();
	}
	public static void main(String args[]) {
		NameNode nn;
		int index = 0;
		try {
			nn = (NameNode) Naming.lookup("//localhost/nameNode");
			listOfDataNode = nn.consult();
			int numberOfDataNode = listOfDataNode.size();
			List<Host> ListInformationOfDataNode = new ArrayList<>(listOfDataNode.values());
			Iterator<Host> itr = ListInformationOfDataNode.iterator();
			System.out.println("Start client");
			while(itr.hasNext()) {
				Host a = itr.next();
				port[index] = a.getPort();
				host[index] = a.getHost();
				index = index+1;
			}
			hnb = index;
			Scanner sc = new Scanner(System.in);
			System.out.println("----------------------------------SETUP SECTION----------------------------------");
			System.out.println("*Note: you should choose 'use mode' ");
			
			System.out.println("Do you want to use application or test application(type u for use/ t for test)");
			String chooseMode = sc.nextLine();
			OutputStream osDW = null;
			String upLoadFilePath = ""; // For upload file
			if(chooseMode.equals("u")) {
				osDW = new FileOutputStream("/video/video_newFunc.mp4");
				upLoadFilePath = "/video/";
				
			}
			if(chooseMode.equals("t")){
				osDW = new FileOutputStream("/home/duc/eclipse-workspace/testData/testDownload/video_newFunc.mp4");
				upLoadFilePath = "/home/duc/eclipse-workspace/Project/src/video/";
			}
			if(chooseMode.isEmpty()==false) {
				Scanner sc2 = new Scanner(System.in);
				System.out.println("Do you want to store or download(type s for store/ d dor download)");
				String chooseFeature = sc2.nextLine();
				System.out.println("---------------------------------------------------------------------------------");
				
				System.out.println("----------------------------------SEARCH SECTION----------------------------------");
				Scanner sc3 = new Scanner(System.in);
				String typingFileName;
				File f;
				while(true) {
					System.out.println("File name you want to store: ");
					typingFileName = sc3.nextLine();
					typingFileName = "./video/" + typingFileName;
					System.out.println("typingFileName"+typingFileName);
					f = new File(typingFileName);
					if(f.exists()) {
						System.out.println("File exist");
						break;
					}else {
						System.out.println("file is not exist please try again");
						
					}
				}
				System.out.println("----------------------------------------------------------------------------------");
				
				if(chooseFeature.equals("s")) {
					for(int i = 0; i < ListInformationOfDataNode.size(); i++) {
						//String fileName = "video171mb.mp4";
					    //String fileName = "videoTest.mp4";
					    String fileName = typingFileName;
					    //String fileName = "video1gb.mp4";
						//String fileName = "video28mb.mp4";
						store(i,fileName,upLoadFilePath);
						
					}
					System.out.println("Upload done");
				}
				if(chooseFeature.equals("d")) {
					System.out.println("Choose feature: "+chooseFeature);
					for(int i = 0; i < ListInformationOfDataNode.size(); i++) {
						//String fileName = "video171mb.mp4";
					    String fileName = "videoTest.mp4";
					    //String fileName = "video1gb.mp4";
						//String fileName = "video28mb.mp4";
						//store(i,fileName);
					    download(i, fileName, osDW);
						
					}
					System.out.println("Download done");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}

class DownloadThread extends Thread{
	int addressIndex;
	String filename;
	OutputStream os;
	public DownloadThread(int i, String fn, OutputStream sp) {
		this.addressIndex = i;
		this.filename = fn;
		this.os = sp;
	}
	public void run() {
		String host = Client.host[this.addressIndex];
		int port = Client.port[this.addressIndex];
		try {
			// sending name to server.
			Socket threadSocket = new Socket(host,port);
			OutputStream outputname = threadSocket.getOutputStream();
			ObjectOutputStream outputNameObject = new ObjectOutputStream(outputname);
			outputNameObject.writeObject("download-"+this.filename+"-"+this.addressIndex+"-"+Client.hnb);	
		
			InputStream is = threadSocket.getInputStream();
			byte buffer[] = new byte[1024];
			int nbr = 0;
			int countTotalByte = 0;
			while(true) {
				nbr = is.read(buffer, 0, buffer.length);
				countTotalByte+=nbr;
				System.out.println("From: "+port+" client receive: "+countTotalByte);
				if(nbr == -1) {
					break;
				}
				os.write(buffer, 0, nbr);
			}
			//is.close();
			//os.close();
			threadSocket.close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
	
}

class UploadThread extends Thread{
	int addressIndex;
	String filename;
	String filepath;
	public UploadThread(int i, String fn, String fp) {
		this.addressIndex = i;
		this.filename = fn;
		this.filename = fp;
	}
	public void run() {
			String host = Client.host[this.addressIndex];
			int port = Client.port[this.addressIndex];
			
			String sendServer = "Send to server";
			try {
				// Open file
				//File f = new File("/home/duc/eclipse-workspace/Project/src/video/"+this.filename);
				File f = new File(filepath + this.filename);
				//File f = new File("/video/"+this.filename);
				Socket threadSocket = new Socket(host,port);
				
				//Sending file name
				OutputStream nameoutput = threadSocket.getOutputStream();
				ObjectOutputStream nameOutputObject = new ObjectOutputStream(nameoutput);
				nameOutputObject.writeObject("store-"+this.filename);
				
				//Sending file to server
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
				int index = 0;
				while(true) {
					if(index == Client.port.length) break;
					System.out.println(index+"-"+Client.port[index]);
					index++;
				}
				System.out.println("Sended: "+bytes.length+" bytes");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}

