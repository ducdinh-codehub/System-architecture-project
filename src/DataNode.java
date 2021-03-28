import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public interface DataNode {
	public static void main(String args[]) {
		try {
			NameNode nn = (NameNode) Naming.lookup("//localhost/nameNode");
			Scanner sc = new Scanner(System.in);
			System.out.println("Typing host name: ");
			String host = sc.nextLine();
			System.out.println("Typing port");
			sc.close();
			int port = sc.nextInt();
			Host h = new HostImpl(host,port);
			System.out.println("Start server at: //"+h.getHost()+":"+h.getPort());
			try {
				nn.registor(h);
				ServerSocket ss = new ServerSocket(h.getPort());
				while(true) {
					Socket s = ss.accept();
					InputStream is = s.getInputStream();
					byte buffer[] = new byte[1024];
					is.read(buffer);
					String strReceiv = new String(buffer, StandardCharsets.UTF_8);
					System.out.println("From client: "+strReceiv);
					ss.close();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
