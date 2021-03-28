import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;

public interface Consulation extends Remote{
	public Hashtable<String, Host> listDataNode() throws RemoteException;
}
