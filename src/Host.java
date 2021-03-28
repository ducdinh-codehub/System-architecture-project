import java.io.Serializable;

public interface Host extends Serializable{
	public String getHost();
	public int getPort();
	public void setHost(String host);
	public void setPort(int port);
}
