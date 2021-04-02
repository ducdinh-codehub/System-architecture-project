import java.io.*;
import java.util.*;
import java.nio.file.Files;
public class testReadFile {
	public static void main(String args[]) {
		System.out.println("Hello World");
		String FileName = "/home/duc/eclipse-workspace/Project/src/videoServer/8081Video/video2gb.mp4";
		File file = new File(FileName);
		System.out.println("Size: "+file.length());
		//long numberOfEachSplittingFile = 3;
		//long sizeForEachSplittingFile = file.length()/numberOfEachSplittingFile;
		//long remainSizeFoerEachSplittingFile = totalSize % numberOfEachSplittingFile;
		//System.out.println("Size for each splitting file: "+sizeForEachSplittingFile);
		//System.out.println("Remain size: "+remainSizeFoerEachSplittingFile);
		System.out.println(file.getPath());
		byte fileContent[] = new byte[(int) file.length()];
		int byteIndex = 0;
		try {
			InputStream a = new FileInputStream(file.getPath());
			try {
				int data = a.read();
				while(byteIndex < file.length()){
					data = a.read();
					byteIndex = byteIndex + 1;
					if(byteIndex==file.length()) {
						System.out.println(byteIndex);
					}
				}
				System.out.println(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
}
