import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Scanner;

public interface DataNode {
	public static void main(String args[]) {
			
			Scanner sc1 = new Scanner(System.in);
			System.out.println("Typing host name: ");
			String host = sc1.nextLine();
			sc1.close();
			
			/*
			Scanner sc2 = new Scanner(System.in);
			System.out.println("Typing port");
			int port = sc2.nextInt();
			sc2.close();
			*/
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
					byte buffer[] = new byte[1024];
					is.read(buffer);
					String strReceiv = new String(buffer, StandardCharsets.UTF_8);
					System.out.println("From client: "+strReceiv);
					
					String sendClient = "Complete";
					OutputStream os = s.getOutputStream();
					byte bufferOutput[] = sendClient.getBytes();
					os.write(bufferOutput,0,bufferOutput.length);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
