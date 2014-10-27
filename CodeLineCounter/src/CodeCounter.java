import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;


public class CodeCounter {
	File folder;
	static int counterLines = 0;
	private BufferedReader br;
	static int charCount = 0;
	static int wordCount = 0;
	BufferedWriter output = null;
	boolean saveFile;
	static File file;
	static String dir;
	static String[] types;
	static String savePath;
	static String completeStatistics;
	boolean isDoc = false;
	
	/**
	 * 
	 * @param directory project directory
	 * @param excluded excluded folder as strings
	 * @param save boolean if statistics should be saved
	 * @param saveP folder where statistics will be saved
	 * @param docTypes types of documents who will be counted
	 */
	public CodeCounter(String directory, String[] excluded, boolean save, String saveP, String[] docTypes) {
		dir = directory;
		charCount = 0;
		counterLines = 0;
		wordCount = 0;
		types = docTypes;
		for(int i=0;i<types.length;i++)
			if(types[i].equals(".doc") || types[i].equals(".docx")) {
				isDoc = true;
			}
		
		saveFile = save;
		savePath = saveP;
		
		// generate file from dir-path
		if (new File(directory).exists()) {
			folder = new File(directory);
		}
		
		// delete statistic-file and make new
		if(save) {
			if(new File(savePath + "/"+folder.getName()+" - statistics.txt").exists()) {
				new File(savePath + "/"+folder.getName()+" - statistics.txt").delete();
			}
			file = new File(savePath + "/"+folder.getName()+" - statistics.txt");
		}

		// get sub-directories and start algorithm
		File[] fold = folder.listFiles();

		completeStatistics = "---- Folder: "+saveP+" ---- \n";
		getSubdirectory(fold, excluded);
		
		// add the summary
		completeStatistics += "\nTotal: \nchars: " + charCount + "\n"
				+"words: " + wordCount + "\n" +
				"LOC: " + counterLines + "\n -------------------------- \n";

		
		// save the statistic-file
		if(save) {
			try {
				output = new BufferedWriter(new FileWriter(file, true));
				output.write(completeStatistics);
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param f sub folders (or files) 
	 * @param excluded excluded folders
	 */
	void getSubdirectory(File[] f, String[] excluded) {
		
		for (int i = 0; i < f.length; i++) {

			boolean excludedDir = false;
			// if no excluded folder given continue
			if(excluded == null) {
				excludedDir = false;
			} else {
				// check if actual file is excluded & directory
				for (int j = 0; j < excluded.length; j++) {
					if (f[i].getName().equals(excluded[j]) && f[i].isDirectory())
						excludedDir = true;
				}
			}
			try {
				// if file is folder & not included, start method recursively
				if (f[i].isDirectory() && !excludedDir) {
					File[] dir = f[i].listFiles();
					getSubdirectory(dir, excluded);
				}
			} catch (NullPointerException n) {
				/* 
				 * if folder couldn't be read add Error-Message
				 * this could occur for special windows folders
				 */
				completeStatistics += "\nError: \""+f[i].getAbsolutePath()+"\" could not be read!\n";
			}
			// check if the file's document to be counted
			if (containsFile(f[i].getName())) {
				String docDocument = "";
				FileReader fr;
				int tempChar = 0;
				int tempLines = 0;
				int tempWords = 0;
				
				completeStatistics += "\nFile: " + f[i].getName() + "\n";
				
				// read properties of word document
				if(isFileDoc(f[i])) {
					try {						
						XWPFDocument doc = new XWPFDocument(new FileInputStream(new File(f[i].getAbsolutePath())));
						XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
						docDocument = extractor.getText();
												
						tempLines += extractor.getDocument().getProperties().getExtendedProperties().getUnderlyingProperties().getLines();
						tempWords += countWordsInSentence(docDocument);
						tempChar += docDocument.replace(" ", "").length();
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				} else {

					// read document & count words, LOC, and chars
					try {
						fr = new FileReader(f[i]);
						br = new BufferedReader(fr);
						while (br.ready()) {
							String line = br.readLine();
							tempWords += countWordsInSentence(line);
							tempChar += line.replace(" ", "").length();
							if(!line.equals(""))
								tempLines++;
						}
						br.close();

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				charCount += tempChar;
				counterLines += tempLines;
				wordCount += tempWords;

				// add counted things to string
				completeStatistics += "chars: " + tempChar + "\n" + "words: " + tempWords + "\n" + "LOC: " + tempLines + "\n";
			}
		}
	}

	private static int countWordsInSentence(final String input) {
		int count = 0;
		StringTokenizer stk = new StringTokenizer(input," ");
	    while(stk.hasMoreTokens()){
	        stk.nextToken();
	        count++;
	    }
		return count;
	}
	

	public String getText() {
		return completeStatistics;
	}
	
	public boolean containsFile(String file2beProofed) {
		boolean accepted = false; 
		for(int i = 0; i<types.length; i++) {
			if(file2beProofed.endsWith(types[i])) {
				return true;
			} else {
				accepted = false;
			}
		}
		return accepted;
	}
	
	boolean isFileDoc(File f) {
		if(f.getName().endsWith(".doc") || f.getName().endsWith(".docx"))
			return true;
		else 
			return false;
	}
}
