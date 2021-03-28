import java.rmi.Remote;
import java.util.Hashtable;

public interface NameNode extends Remote{
	public void registor(Host h) throws Exception;
	public Hashtable<String,Host> consult() throws Exception;
}
