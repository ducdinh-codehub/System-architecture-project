import java.net.Socket;

public interface ManageFile {
	public void store(int addresIndex, String filename);
	public void download(String filename);
}
