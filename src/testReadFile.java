import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.io.*;
public class testReadFile {
	public static void main(String args[]) {
		System.out.println("Hello World");
		String FileName = "/home/duc/eclipse-workspace/testData/video/videoTest.mp4";
		File file = new File(FileName);
		try {
			
			int nbr = 0;
			int countByte = 0;
			

			int startArr[] = {0, 1961414, 3922828};
			int stopArr[] = {1961414, 3922828, 5884242};
			//Split file
			InputStream is;
			OutputStream os;
			for(int i = 0; i < 3; i++) {
				is = new FileInputStream(file);
				os = new FileOutputStream("/home/duc/eclipse-workspace/testData/testDownload/test_video_"+i+".mp4");
				int start = startArr[i];
				int stop = stopArr[i];
				byte buffer[] = new byte[1024];
				countByte = 0;
				while(true) {
					nbr = is.read(buffer, 0, buffer.length);
					if(nbr == -1) {
						break;
					}
					countByte+=nbr;
					if(countByte >= start && countByte <= stop) {
						System.out.println("Start write");
						os.write(buffer, 0, nbr);
					}
				}
				is.close();
				os.close();
			}
			// Concaternate
			os = new FileOutputStream("/home/duc/eclipse-workspace/testData/testDownload/joinFIle.mp4");
			for(int i = 0; i < 3; i++) {
				is = new FileInputStream("/home/duc/eclipse-workspace/testData/testDownload/test_video_"+i+".mp4");
				byte buffer2[] = new byte[1024];
				int nbr2 = 0;
				while(true) {
					nbr2 = is.read(buffer2, 0, buffer2.length);
					if(nbr2 == -1) {
						break;
					}
					os.write(buffer2, 0, nbr2);
				}
			}
		}catch(Exception e) {
			System.out.println(e);
		}
		byte allContent[] = null;
		try {
			File FileName2 = new File("/home/duc/eclipse-workspace/testData/testDownload/test_video.mp4");
			allContent = Files.readAllBytes(FileName2.toPath());
			System.out.println(allContent.length);
		}
		catch(Exception e) {
			System.err.println(e);
		}
		
		
		//long numberOfEachSplittingFile = 3;
		//long sizeForEachSplittingFile = file.length()/numberOfEachSplittingFile;
		//long remainSizeFoerEachSplittingFile = totalSize % numberOfEachSplittingFile;
		//System.out.println("Size for each splitting file: "+sizeForEachSplittingFile);
		//System.out.println("Remain size: "+remainSizeFoerEachSplittingFile);
		
		//System.out.println(file.getPath());
		//byte fileContent[] = new byte[(int) file.length()];
		//int byteIndex = 0;
		//byte fileContent[] = null;
		//try {
		//	fileContent = Files.readAllBytes(file.toPath());
		//} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		//for(int i = 0; i < fileContent.length; i++) {
		//	System.out.println("Byte:"+i+" = "+fileContent[i]);
		//}
		//System.out.println("Total size: "+fileContent.length);
		/*try {
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
		}*/
		
	}	
}
