public class HostImpl implements Host{
	private static final long serialVersionUID = 1L;
	String host;
	int port;
	public HostImpl(String hos, int por) {
		this.host = hos;
		this.port = por;
	}
	public String getHost(){
		return this.host;
	}
	public int getPort() {
		return this.port;
	}
	public void setHost(String hos) {
		this.host = hos;
	}
	public void setPort(int por) {
		this.port = por;
	}
}
