import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Registration extends Remote{
	public void registerDataNode(Host h) throws RemoteException;
}
