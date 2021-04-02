import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Scanner;

import java.nio.file.Files;

public interface DataNodeInterface {
	

	public static void main(String args[]) {
			
			//Scanner sc1 = new Scanner(System.in);
			//System.out.println("Typing host name: ");
			//String host = sc1.nextLine();
			
			String host = "localhost";
			//sc1.close();
			
			int port = Integer.parseInt(args[0]);
			System.out.println("Open port: "+port);
			Host h = new HostImpl(host,port);
			
			System.out.println("Start server at: //"+h.getHost()+":"+h.getPort());
			try {
				NameNode nn = (NameNode) Naming.lookup("//localhost/nameNode");
				nn.registor(h);
				Hashtable<String, Host> listDataNode = nn.consult();
				System.out.println("List data node: "+listDataNode);
				ServerSocket ss = new ServerSocket(h.getPort());
				while(true) {
					Socket s = ss.accept();
					// Get file name
					InputStream nameinput = s.getInputStream();
					ObjectInputStream nameInputObject = new ObjectInputStream(nameinput);
					String rq = (String) nameInputObject.readObject();
					// Get mode and file name
					System.out.println("Request: "+rq);
					String rqSplit[] = rq.split("-");
					String mode = rqSplit[0];
					String nameFile = rqSplit[1];
					System.out.println("Mode: "+mode+"File name: "+nameFile);
					
					if(mode.equals("store")) {
						//Get file from client
						System.out.println("Store mode");
						InputStream is = s.getInputStream();
						OutputStream os = new FileOutputStream("/home/duc/eclipse-workspace/Project/src/videoServer/"+args[0]+"Video/"+nameFile);
						byte buffer[] = new byte[256*1024];
						int nb = 0;
						while(true) {
							nb = is.read(buffer,0,buffer.length);
							if(nb == -1) {
								break;
							}
							os.write(buffer, 0, nb);
						}
						System.out.println("Done");
					}
					else {
						System.out.println("Download mode");
						int downloadOrder = Integer.parseInt(rqSplit[2]);
						System.out.println("Boosting host number: "+downloadOrder);
						File f = new File("/home/duc/eclipse-workspace/Project/src/videoServer/"+args[0]+"Video/"+nameFile);
						int totalSize = (int) f.length();
						InputStream is = new FileInputStream(f);
						
						int hnb = Integer.parseInt(rqSplit[3]);
						
						int nb;
						System.out.println("Total number of host: "+hnb);
						int numberByteEachSplitFile = totalSize / hnb;
						int remainByte = totalSize % hnb;
						System.out.println("Bytes for each splitting file: "+numberByteEachSplitFile);
						System.out.println("Remain bytes: "+remainByte);
						int tmpTotalSize = totalSize;
						int endLimitationLocationArr[] = new int[hnb + 1]; //hnb number of exist host
						int startLimitationLocationArr[] = new int[hnb + 1];
						int startPosition = 0;
						int endPosition = 0;
						int index = 0;
						
						startPosition = downloadOrder * numberByteEachSplitFile;
						endPosition = (downloadOrder + 1) * numberByteEachSplitFile;
						System.out.println("Start byte: "+startPosition+" End byte: "+endPosition);
						
						InputStream fis = new FileInputStream(f);
						OutputStream fos = s.getOutputStream();
						int totalByte = endPosition+remainByte;
						int maxByte = 1024;
						byte buffer[] = new byte[1];
						System.out.println("Number of Byte:" + totalByte);
						int nbr = 0;
						int countTotalByte = 0;
						
						//nbr = fis.read(buffer,startPosition,endPosition);
						/*
						if(totalSize - endPosition == remainByte) {
							endPosition
						}
						*/
						/*
				        int val = raf.read(buf);
				        if(val != -1) {
				            bw.write(buf);
				        }
						for(int i = startPosition; i < endPosition; i++) {
							nbr = fis.read(buffer,i,);
							fos.write(buffer,i,);
						}*/
						/*
						while(true) {
							nbr = fis.read(buffer,0,buffer.length);
							System.out.println("Number of byte: "+nbr);
							countTotalByte += nbr;
							System.out.println("Total size: "+countTotalByte);
							if(nbr == -1) {
								break;
							}
							
							if(countTotalByte > endPosition) {
								break;
							}
							fos.write(buffer,0,nbr);
						}*/
						
						// Seperate file on disk
						/*
						while(index < hnb) {
							if(index == 0) {
								startPosition = endPosition;
							}else {
								startPosition = endPosition + 1;
							}
							endPosition = endPosition + numberByteEachSplitFile;		
							if(totalSize - endPosition == remainByte) {
								endPosition = endPosition + 2;
							}
							endLimitationLocationArr[index] = endPosition;
							startLimitationLocationArr[index] = startPosition;
							
							
							index+=1;
						}*/
						
						// Check Limitation array
						/*
						for(int i = 0; i < hnb; i++) {
							System.out.println("Start "+startLimitationLocationArr[i]+"-End "+endLimitationLocationArr[i]);
						}
						
						//Read all Data
						byte fileContent[] = null;
						fileContent = Files.readAllBytes(f.toPath());
						System.out.println("Size of file: "+fileContent.length);
						int countBytes = 0;
						byte splitArr[][] = new byte[hnb][endLimitationLocationArr[0]+remainByte];
						for(int i = 0; i< hnb; i++) {
							countBytes = 0;
							for(int j = startLimitationLocationArr[i]; j < endLimitationLocationArr[i]; j++) {
								splitArr[i][countBytes] = fileContent[j];
								countBytes += 1;
							}
							System.out.println("Part"+" "+i+": "+countBytes);
						}
						*/
						
						// Download methods
						/*
						InputStream bis = new FileInputStream(f);
						OutputStream bos = s.getOutputStream();
						*/
					
							
						
						
						
						//bos.write(splitArr[downloadOrder], 0, splitArr[downloadOrder].length); //dowloadOrder is the order to download each part.
						/*
						
						index = 0;
						InputStream fis = new FileInputStream(f);
						OutputStream fos = s.getOutputStream();
						int nbr = 0;
						byte buffer[] = new byte[1];
						byte fileContent[] = null; 
						//Get all bytes of the sending file
						//fileContent = Files.readAllBytes(f.toPath());
						//System.out.print("Length of file: "+fileContent.length);
						//fos.write(fileContent);
						int stop = 0;
						while(true) {
							nbr = fis.read(buffer);
							stop = nbr + stop;
							if(stop == endLimitationLocationArr[downloadOrder]) {
								System.out.println(stop);
								break;
							}
							fos.write(buffer,startLimitationLocationArr[downloadOrder],nbr);						
						}
						System.out.println("Buffer size:"+buffer.length);*/
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
