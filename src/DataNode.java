import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Scanner;

public interface DataNode {
	public static byte fileContent[] = new byte[1024]; 
	public static void main(String args[]) {
			
			Scanner sc1 = new Scanner(System.in);
			System.out.println("Typing host name: ");
			String host = sc1.nextLine();
			sc1.close();
			
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
					InputStream is = s.getInputStream();
					OutputStream os = new FileOutputStream("/home/duc/eclipse-workspace/Project/src/videoServer/"+args[0]+"_SaveFile.mp4");
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
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
