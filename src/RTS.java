import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RTS {
	int coverage = 0;
	File f1, f2;
	// counts the # of lines in a file and returns that number
	public int lineCount(File f1) throws Exception {

		File filename = f1;

		BufferedReader reader = new BufferedReader(new FileReader(filename));
		int lines = 0;
		while (reader.readLine() != null)
			lines++;
		reader.close();
		return lines;
	}

	// delete line or add line from one file to another
	public int deleteORadd() throws Exception {
		int count = 0;
		try {
			InputStream is1 = new FileInputStream(f1); 
			InputStream is2 = new FileInputStream(f2);
			BufferedReader buf1 = new BufferedReader(new InputStreamReader(is1));
			BufferedReader buf2 = new BufferedReader(new InputStreamReader(is2));
			
			int size1 = lineCount(f1);
			int size2 = lineCount(f2);
		
			String[] arrText1 = new String[size1];
			String[] arrText2 = new String[size2];
			
			String text1 = buf1.readLine();
			String text2 = buf2.readLine();
		
			int i = 0;
		
			while(text1 != null) {
				arrText1[i] = text1;
				i++;
				text1 = buf1.readLine();
			}
			i = 0;
			while(text2 != null) {
				arrText2[i] = text2;
				i++;
				text2 = buf2.readLine();
			}
			buf1.close();
			buf2.close();
			
			for(int j = 0; j < arrText2.length; j++) {
//				System.out.println("1 "+arrText2[j]);
				for(int k = 0; k < arrText1.length; k++) {
//					System.out.println("2 "+arrText1[k]);
					if(arrText2[j].equals(arrText1[k])) {
//						System.out.println("FOUND ["+arrText2[j]+
//								"\n"+arrText1[k]+"]");
//						for(int m = j; m < arrText1.length; m++ ) {
//							arrText1[m] = arrText1[m+1];	
//						}
						count++;
					}
				}
			}
		}
catch (IOException e) {
}
		return count;
}

	public int countDivide(int danLine, int coverage) throws Exception {
		return coverage / danLine * 100;
	}

	public void testing(String file1, String file2) throws Exception {
		this.f1 = new File(file1);
		this.f2 = new File(file2);
		System.out.println(lineCount(f1));
		System.out.println(lineCount(f2));
		
		coverage = deleteORadd();
		if (coverage > 0)
			System.out.println("Test Covered " + 
					countDivide(lineCount(f1), coverage) 
					     + "% " + "\n of Dangerous Edges\n");
		else
			System.out.println("Test Failure: No Coverage\n");
	}
}