import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.*;

public class Client {
	public static void main(String args[]) {
		NameNode nn;
		try {
			nn = (NameNode) Naming.lookup("//localhost/nameNode");
			Hashtable<String, Host> listOfDataNode = nn.consult();
			int numberOfDataNode = listOfDataNode.size();
			System.out.println("Number of data node: "+numberOfDataNode);
			System.out.println(listOfDataNode.get("8082").getHost());
			List<Host> ListInformationOfDataNode = new ArrayList<>(listOfDataNode.values());
			Iterator<Host> itr = ListInformationOfDataNode.iterator();
			while(itr.hasNext()) {
				System.out.println(itr.next().getPort());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
