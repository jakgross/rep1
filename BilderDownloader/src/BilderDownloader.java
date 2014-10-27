import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

import javax.imageio.ImageIO;


public class BilderDownloader {
	BufferedImage image;
	static BilderDownloader b;
	
	public BilderDownloader() throws IOException {
		image = null;
		URL url = null;
		
		// legt fest ob vorangegangene 0
		boolean numb = false;
		
		boolean imageav = true;
		
		int counter = 1;
		int zehn = 0;
		
		int hundert = 0;
		String rootFolder = "C:/folder2save";
		String pathname = "folder-path";
		
		String urlP = "http://url.com/image"; // image is named 'image###.jpg"
		
		if(!new File(rootFolder+pathname).exists())
			new File(rootFolder).mkdir();
		
		new File(rootFolder+pathname).mkdir();
		
		while (imageav) {
			try {
				if(counter > 9) {
					zehn++;
					counter = 0;
				}
				
				if(numb) {
					if(zehn>9) {
						zehn=0;
						hundert++;
					}
					url = new URL(urlP+hundert+zehn+counter+".jpg");
					System.out.println("url: "+url.toString());
					ImageIO.write(ImageIO.read(url), "jpg", new File(rootFolder+pathname+"/filename-"+hundert+zehn+counter+".jpg"));
				} else {
					System.out.println(urlP+zehn+counter+".jpg");
					url = new URL(urlP+zehn+counter+".jpg");
					ImageIO.write(ImageIO.read(url), "jpg", new File(rootFolder+pathname+"/filename-"+zehn+counter+".jpg"));
				}
		    	counter++;
		    	System.out.println("image saved");
			} catch (IOException e) {
				System.out.println("no images left!");
				imageav = false;
			} catch (IllegalArgumentException f) {
				System.out.println("images == null!");
				imageav = false;
			}
		}		
	}
		
	public static void main(String[] args) {
		try {
//			b = new BilderDownloader();
//			new BilderDownloaderWithoutZero();
			new BilderDownloader();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
class BilderDownloaderWithoutZero {

	BufferedImage image;
	
	public BilderDownloaderWithoutZero() throws IOException {
		image = null;
		URL url = null;
			
		boolean imageav = true;
		
		int counter = 1;
		
		String rootFolder = "C:/folder2save/";
		String pathname = "filepath";
		
		String urlP = "http://url.com/image";
		
		if(!new File(rootFolder+pathname).exists())
			new File(rootFolder).mkdir();
		
		new File(rootFolder+pathname).mkdir();
		
		while (imageav) {
			try {
				
				System.out.println(urlP+counter+".jpg");
				url = new URL(urlP+counter+".jpg");
				ImageIO.write(ImageIO.read(url), "jpg", new File(rootFolder+pathname+"/file-name"+(counter+20)+".jpg"));

		    	counter++;
		    	System.out.println("image saved");
			} catch (IOException e) {
				System.out.println("no images left!");
				imageav = false;
			} catch (IllegalArgumentException f) {
				System.out.println("images == null!");
				imageav = false;
			}
		}		
	}
}

