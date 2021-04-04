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
import java.util.concurrent.Semaphore;
import java.nio.file.Files;

public class DataNode {
	
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
						OutputStream os = new FileOutputStream("/home/duc/eclipse-workspace/testData/videoServer/"+args[0]+"Video/"+nameFile);
						//OutputStream os = new FileOutputStream("/videoServer/"+args[0]+"Video/"+nameFile);
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
						File f = new File("/home/duc/eclipse-workspace/testData/videoServer/"+args[0]+"Video/"+nameFile);
						//File f = new File("/videoServer/"+args[0]+"Video/"+nameFile);
						int totalSize = (int) f.length();
						InputStream is = new FileInputStream(f);
						
						int hnb = Integer.parseInt(rqSplit[3]);
						
						int nb;
						System.out.println("Total number of host: "+hnb);
						System.out.println("Total size: "+totalSize);
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
						int maxByte = 1024;
						byte buffer[] = new byte[maxByte];
						int nbr = 0;
						int countTotalByte = 0;
						int checkFirstTime = 1;
						int toTalByte = 0;
						while(true) {
							
							nbr = fis.read(buffer,0,buffer.length);
							if(nbr==-1) {
								break;
							}
							countTotalByte+=nbr;
							int limitedEnd = countTotalByte + maxByte;
							if(limitedEnd > endPosition) {
								fos.write(buffer,0,nbr);
								toTalByte+=nbr;
								System.out.println("Pre ending at byte: "+countTotalByte);
								int sendBytes = endPosition - (limitedEnd - maxByte);
								if(sendBytes > maxByte) {
									System.out.println("sendBytes > maxByte");
									int numRead = sendBytes / maxByte;
									int remainBytes = sendBytes & maxByte;
									for(int i = 0; i < numRead; i++) {
										fos.write(buffer,0,maxByte);
										countTotalByte+=1024;
										toTalByte+=nbr;
									}
									if(remainByte > 0) {
										fos.write(buffer,0,remainBytes);
										countTotalByte+=remainBytes;
										toTalByte+=remainBytes;
									}
									System.out.println("Ending at byte: "+countTotalByte);
								}
								else {
									System.out.println("sendBytes < maxByte");
									fos.write(buffer,0,sendBytes);
									System.out.println("Ending at byte: "+sendBytes);
									toTalByte+=sendBytes;
								}
								break;
							}
							fos.write(buffer, 0, nbr);
							toTalByte+=nbr;
							/*
							if(countTotalByte >= startPosition+1 && countTotalByte <= endPosition) {
								if(checkFirstTime==1) {
									System.out.println("Start at byte: "+countTotalByte);
									checkFirstTime = 0;
									
								}
								fos.write(buffer, 0, nbr);
								toTalByte+=nbr;
							}*/
						}
						System.out.println("Total bytes are written: "+toTalByte);
						
						/*while(true) {
							nbr = fis.read(buffer,0,buffer.length);
							if(nbr==-1) {
								break;
							}
							countTotalByte += nbr;
							// If number of the ending sending bytes is bigger than the expected ending bytes
							int limitEnd = countTotalByte + maxByte;
							if(limitEnd > endPosition) {
								System.out.println("Reach the ending limit: "+limitEnd);
								int sendBytes = endPosition - countTotalByte;
								if(sendBytes > maxByte) {
									System.out.println("Ending Send bytes > max bytes");
									int splitSendBytes = sendBytes / maxByte;
									int remainSplitSendBytes = sendBytes % maxByte;
									for(int i = 0; i < splitSendBytes; i++) {
										fos.write(buffer, 0, buffer.length);
										countTotalByte += buffer.length;
									}
									if(remainSplitSendBytes > 0) {
										fos.write(buffer, 0, remainSplitSendBytes);
										countTotalByte += remainSplitSendBytes;
									}
								}
								else {
									
									System.out.println("Ending Send bytes < max bytes");
									fos.write(buffer, 0, sendBytes);
									
//									int splitSendBytes = sendBytes / maxByte;
//									int remainSplitSendBytes = sendBytes % maxByte;
//									for(int i = 0; i < splitSendBytes; i++) {
//										fos.write(buffer, 0, buffer.length);
//										countTotalByte += buffer.length;
//									}
//									if(remainSplitSendBytes > 0) {
//										fos.write(buffer, 0, remainSplitSendBytes);
//										countTotalByte += remainSplitSendBytes;
//									}
								}
								System.out.println("End writting at bytes: "+countTotalByte);
								break;
							}
							
							// If number of sending bytes in the range from start position to end position write on the stream
							// If starting byte is not equal our expected starting byte
							if(countTotalByte >= startPosition && countTotalByte <= endPosition) {
								if(checkFirstTime == 1) {
									System.out.println("Checking the first time");
									checkFirstTime = 0;
									int startByte = 0;
									int countTotalByteTmp = countTotalByte;
									int sendBytes = 0;
									if(countTotalByte > startPosition) {
										
										if(countTotalByteTmp > maxByte) {
											while(countTotalByteTmp > startPosition) {
												if(countTotalByte <= startPosition) {
													break;
												}
												countTotalByteTmp -= maxByte;
											}
											sendBytes = startPosition - countTotalByteTmp;
											startByte = sendBytes;
											System.out.println("start position: "+startPosition+" - count total byte: "+countTotalByte+" = "+sendBytes);
										}else {
											sendBytes = countTotalByteTmp;
										}
										if(sendBytes >= maxByte) {
											System.out.println("Starting Send bytes > max bytes");
											int splitSendBytes = sendBytes / maxByte;
											int remainSplitSendBytes = sendBytes % maxByte;
											for(int i = 0; i < splitSendBytes; i++) {
												fos.write(buffer, 0, buffer.length);
												startByte+=maxByte;
											}
											if(remainSplitSendBytes > 0) {
												startByte += remainSplitSendBytes;
												fos.write(buffer, 0, remainSplitSendBytes);
											}
										}else {
											System.out.println("Starting Send bytes < max bytes");
											fos.write(buffer, 0, sendBytes);
											
//											System.out.println("sendBytes:"+sendBytes);
//											int splitSendBytes = startPosition / maxByte;
//											int remainSplitSendBytes = startPosition % maxByte;
//											for(int i = 0; i < splitSendBytes; i++) {
//												fos.write(buffer, 0, buffer.length);
//												startByte+=maxByte;
//											}
//											if(remainSplitSendBytes > 0) {
//												startByte += remainSplitSendBytes;
//												fos.write(buffer, 0, remainSplitSendBytes);
//											}
										}
										System.out.println("Start writting at bytes: "+startByte);
									}
								}else {
									System.out.println("Start writting at bytes: "+countTotalByte);
									fos.write(buffer,0,nbr);
								}
							}
						}*/
						// Read file, seperating and sending each small part to client.
						/*
						while(true) {
							nbr = fis.read(buffer,0,buffer.length);
							if(nbr==-1) {
								break;
							}
							countTotalByte += nbr;
							// If number of the ending sending bytes is bigger than the expected ending bytes
							int limitEnd = countTotalByte + maxByte;
							if(limitEnd > endPosition) {
								System.out.println("Reach the ending limit: "+limitEnd);
								int sendBytes = endPosition - countTotalByte;
								if(sendBytes > maxByte) {
									int splitSendBytes = sendBytes / maxByte;
									int remainSplitSendBytes = sendBytes % maxByte;
									for(int i = 0; i < splitSendBytes; i++) {
										fos.write(buffer, 0, buffer.length);
										countTotalByte += buffer.length;
									}
									if(remainSplitSendBytes > 0) {
										fos.write(buffer, 0, remainSplitSendBytes);
										countTotalByte += remainSplitSendBytes;
									}
								}
								else {
									//countTotalByte = countTotalByte + sendBytes;
									int splitSendBytes = sendBytes / maxByte;
									int remainSplitSendBytes = sendBytes % maxByte;
									for(int i = 0; i < splitSendBytes; i++) {
										fos.write(buffer, 0, buffer.length);
										countTotalByte += buffer.length;
									}
									if(remainSplitSendBytes > 0) {
										fos.write(buffer, 0, remainSplitSendBytes);
										countTotalByte += remainSplitSendBytes;
									}
								}
								System.out.println("End writting at bytes: "+countTotalByte);
								break;
							}
							
							// If number of sending bytes in the range from start position to end position write on the stream
							// If starting byte is not equal our expected starting byte
							if(countTotalByte >= startPosition && countTotalByte <= endPosition) {
								if(checkFirstTime == 1) {
									System.out.println("Checking the first time");
									checkFirstTime = 0;
									int startByte = 0;
									int countTotalByteTmp = countTotalByte;
									int sendBytes = 0;
									if(countTotalByte > startPosition) {
										startByte = sendBytes;
										if(countTotalByteTmp > maxByte) {
											while(countTotalByteTmp > startPosition) {
												if(countTotalByte <= startPosition) {
													break;
												}
												countTotalByteTmp -= maxByte;
											}
											sendBytes = startPosition - countTotalByteTmp;
										}else {
											sendBytes = countTotalByteTmp;
										}
										if(sendBytes >= maxByte) {
											System.out.println("Send bytes > max bytes");
											int splitSendBytes = sendBytes / maxByte;
											int remainSplitSendBytes = sendBytes % maxByte;
											for(int i = 0; i < splitSendBytes; i++) {
												fos.write(buffer, 0, buffer.length);
												startByte+=maxByte;
											}
											if(remainSplitSendBytes > 0) {
												startByte += remainSplitSendBytes;
												fos.write(buffer, 0, remainSplitSendBytes);
											}
										}else {
											System.out.println("Send bytes < max bytes");
											//startByte = countTotalByteTmp + sendBytes;
											int splitSendBytes = startPosition / maxByte;
											int remainSplitSendBytes = startPosition % maxByte;
											for(int i = 0; i < splitSendBytes; i++) {
												fos.write(buffer, 0, buffer.length);
												startByte+=maxByte;
											}
											if(remainSplitSendBytes > 0) {
												startByte += remainSplitSendBytes;
												fos.write(buffer, 0, remainSplitSendBytes);
											}
										}
										System.out.println("Start writting at bytes: "+startByte);
									}
								}else {
									System.out.println("Start writting at bytes: "+countTotalByte);
									fos.write(buffer,0,nbr);
								}
							}
							
						}*/
						fis.close();
						fos.close();
						s.close();
						
						/*
						if (numberByteEachSplitFile > maxByte) {
							long numReads = numberByteEachSplitFile/maxByte;
							long numberRemainRead = numberByteEachSplitFile % maxByte;
							for(int i =0; i < numReads; i++) {
								buffer = new byte[maxByte];
								int val = fis.read(buffer);
								if(val == -1) {
									fos.write(buffer);
								}
							}
							if(numberRemainRead > 0) {
								buffer = new byte[remainByte];
								int val = fis.read(buffer);
								if(val == -1) {
									fos.write(buffer);
								}
							}
						}
						else {
							buffer = new byte[maxByte];
							int val = fis.read(buffer);
							if(val == -1) {
								fos.write(buffer);
							}
						}*/
						
						/*
						while(true) {
							nbr = fis.read(buffer, 0, buffer.length);
							countTotalByte += nbr;
							
							if(nbr == -1) {
								System.out.println("End writting at byte: "+countTotalByte);
								break;
							}
							if(countTotalByte >= startPosition && countTotalByte <= endPosition) {
								System.out.println("Start writting at bytes: "+countTotalByte);
								fos.write(buffer,0,nbr);
							}
						}*/
						
						
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
