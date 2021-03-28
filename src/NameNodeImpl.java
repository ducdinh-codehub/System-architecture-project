import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

public class NameNodeImpl extends UnicastRemoteObject implements NameNode, Registration, Consulation{
	
	private static final long serialVersionUID = 1L;
	Hashtable<String, Host> dataNodeTable = new Hashtable<String, Host>();
	
	protected NameNodeImpl() throws RemoteException {
		
	}

	@Override
	public Hashtable<String, Host> listDataNode() throws RemoteException {
		return dataNodeTable;
	}

	@Override
	public void registerDataNode(Host h) throws RemoteException {
		String a = String.valueOf(h.getPort());
		dataNodeTable.put(a, h);	
	}
	
	@Override
	public void registor(Host h) throws Exception{
			System.out.println("Starting registration process");
			System.out.println(dataNodeTable);
			registerDataNode(h);
			
	}

	@Override
	public Hashtable<String, Host> consult() throws Exception {
			return listDataNode();
	}
	
	public static void main(String args[]) {
		try {
			NameNode nn = new NameNodeImpl();
			try {
				Naming.rebind("//localhost/nameNode", nn);
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
