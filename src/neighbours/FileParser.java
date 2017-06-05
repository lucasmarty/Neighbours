package neighbours;

import java.io.*;

public class FileParser {
	
	public static void readFile(String path) throws IOException {
		FileInputStream fin = new FileInputStream(path);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(fin));
		String line = null;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		
		br.close();
	}

}
