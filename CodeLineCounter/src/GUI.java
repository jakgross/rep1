import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GUI extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
	static GUI g;
	JTextField directory;
	JTextField excludedDirs;
	CodeCounter cc;
	static JTextArea results;
	JCheckBox wantSave;
	String saveDir;
	String[] docTypes = {"all (allowed)", "Java", "C/C++", "HTML/PHP", "JS", "doc/docx", "txt"};
	String[] fileEndings = null;
	
	public GUI() {
		super();
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		setLayout(new FlowLayout());
		directory = new JTextField(33);
		directory.setToolTipText("enter the directory to be searched like: C:/my programs/directory");
		directory.setText("please enter your project directory");
		Font font = new Font("Verdana", Font.ITALIC, 12);
        directory.setFont(font);
        directory.setForeground(Color.gray);
		directory.addMouseListener(getProjectDir);
		
		JButton browseDir = new JButton("browse");
		browseDir.addActionListener(browse);
		browseDir.setToolTipText("browse for a folder on your harddisk");
		
		excludedDirs = new JTextField(39);
		excludedDirs.setText("please enter the folder you want to exclude from statistics separated by \",\"");
		excludedDirs.setToolTipText("enter directories (only their name) to exclude like: directory1, directory2, ...");
		excludedDirs.setForeground(Color.gray);
		excludedDirs.setFont(font);
		excludedDirs.addMouseListener(clear);
		
		JButton start = new JButton("start");
		start.addActionListener(search);
		
		results = new JTextArea();
		results.setEditable(false);
		results.setLineWrap(true);
		
		JScrollPane resultsScroll = new JScrollPane(results);
		resultsScroll.setPreferredSize(new Dimension(475, 150));
		
		wantSave = new JCheckBox();
		wantSave.setToolTipText("mark, if you want to save a txt-file with the statistics");
		JLabel saveStat = new JLabel("save statistics");
		saveStat.setFont(font);
		saveStat.setToolTipText("mark, if you want to save a txt-file with the statistics");
		
		final JComboBox<String> combo1 = new JComboBox<String>();
		for (String s : docTypes)
			combo1.addItem(s);
		combo1.setSelectedIndex(0);
		setType(0);
		combo1.setToolTipText("set the type of your project or search for all files");
		
		combo1.addItemListener(new ItemListener() {
			public void itemStateChanged( ItemEvent e ) {
				setType(combo1.getSelectedIndex());
			}
		});
		
		
		add(directory);
		add(browseDir);
		add(excludedDirs);
		add(combo1, BorderLayout.WEST);
		add(start);
		add(wantSave);
		add(saveStat);
		add(resultsScroll);
		
		
		Font font2 = font.deriveFont(Font.ITALIC, 12);
		JLabel copy = new JLabel("\u00a9 by J. Groß");
		copy.setFont(font2);
		copy.setForeground(Color.gray);
		copy.setToolTipText("click to send me an email");
		copy.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}			
			@Override
			public void mouseExited(MouseEvent e) {}			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				Desktop desktop = Desktop.getDesktop(); 
				try {
					desktop.mail(new URI("mailto:jayspam@gmx.net?subject=I%20want%20to%20ask%20you..."));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		add(copy);
		setVisible(true);
		setTitle("CodeLineCounter");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(500, 315));
		setResizable(false);
		
		// Get the size of the default screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int locWidth = (int) ((dim.getWidth()/2)-250);
		int locHeight = (int) ((dim.getHeight()/2)-150);
				
		// Set Location proportional to screen
		setLocation(locWidth,locHeight);

	}

	public static void main(String[] args) {
		g = new GUI();
	}

	public void setType(int index) {
		//String[] docTypes = {"java", "C/C++", "HTML", "PHP", "JS"};
		if(index==0) {
			fileEndings = new String[]{".java", ".C", ".cc", ".cpp", ".CPP", ".c++", ".cp", ".cxx", ".h", ".html", ".htm", ".php", ".doc", ".docx", ".js", ".txt"};
		} else if(index==1) {
			fileEndings = new String[]{".java"};
		} else if(index==2) {
			fileEndings = new String[]{".C", ".cc", ".cpp", ".CPP", ".c++", ".cp", ".cxx", ".h"};
		} else if(index==3) {
			fileEndings = new String[]{".html", ".htm", ".php"};
		} else if(index==4) {
			fileEndings = new String[]{".js"};
		} else if(index==5) {
			fileEndings = new String[]{".doc", ".docx"};
		} else if(index==6) {
			fileEndings = new String[]{".txt"};
		}
	}
	
	ActionListener browse = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int rueckgabeWert = fc.showOpenDialog(getParent());
			
			fc.setDialogTitle("enter your java project dir");
		    if(rueckgabeWert == JFileChooser.APPROVE_OPTION)
		    {
		    	if(fc.getSelectedFile().exists()) {
		    		directory.setText(fc.getSelectedFile().toString());
		    		Font font = new Font("Verdana", Font.PLAIN, 12);
				    directory.setFont(font);
					directory.setForeground(Color.black);
		        } 
		    }			
		}
	};
	
	// ActionListener for starting the algorithm
	ActionListener search = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			//results.setText("");
			String dir = directory.getText();
			if(dir.contains("\\"))
				dir.replace("\\", "/");
			try {
				if(!new File(dir).exists()) {
					JOptionPane.showMessageDialog(null, "Could not read \""+dir+"\"!\nPlease enter valid path", "Path don't exist", JOptionPane.ERROR_MESSAGE);
				} else {
					String[] excludedDirectories;
					if(excludedDirs.getText().length()>0 && !excludedDirs.getText().equals("please enter the folder you want to exclude from statistics separated by \",\""))
						excludedDirectories = excludedDirs.getText().replaceAll(" ", "").split(",");
					else 
						excludedDirectories = null;
			
					saveDir = dir;
			
					boolean save = false;
					if (wantSave.isSelected()) {
						saveDir = getStatisticsSaveDir(save);
						save = true;
						if(saveDir == null) {
							save = false;
							JOptionPane.showMessageDialog(null, "You dint't choose a folder to save the statistics.", "Statistics wont be saved", JOptionPane.WARNING_MESSAGE);
						}
					}

					String text = new CodeCounter(dir, excludedDirectories, save, saveDir, fileEndings).getText();
					results.append(text);
					results.setEditable(false);
					results.setCaretPosition(results.getDocument().getLength());
				}
			} catch (NullPointerException n) {
			 JOptionPane.showMessageDialog(null, "Could not read \""+dir+"\"!\nPlease enter valid path", "Path don't exist", JOptionPane.ERROR_MESSAGE);
			}
		}
	};
	
	String getStatisticsSaveDir(boolean save) {
		save = true;
		JFileChooser fc = new JFileChooser(saveDir);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int rueckgabeWert = fc.showOpenDialog(getParent());
		
		// if searched path exist save it, else dont save
		if(rueckgabeWert == JFileChooser.APPROVE_OPTION) {
			if(fc.getSelectedFile().exists()) {
				saveDir = fc.getSelectedFile().toString();
				return saveDir;
			} else {
				saveDir = null;
				return getStatisticsSaveDir(true);
			}
		} else {
			saveDir = null;
			return saveDir;
		}
	}

	
	// MouseListener for clearing directory-textfield
	MouseListener getProjectDir = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			if(directory.getText().equals("please enter your project directory")) {
				Font font = new Font("Verdana", Font.PLAIN, 12);
		        directory.setFont(font);
		        directory.setForeground(Color.black);
				directory.setText("");
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	};
	
	// MouseListener for clearing excluded-folder-textfield
	MouseListener clear = new MouseListener() {	
		@Override
		public void mouseReleased(MouseEvent e) {}	
		@Override
		public void mousePressed(MouseEvent e) {
			if(excludedDirs.getText().equals("please enter the folder you want to exclude from statistics separated by \",\"")) {
				excludedDirs.setText("");
				Font font = new Font("Verdana", Font.PLAIN, 12);
			    excludedDirs.setFont(font);
				excludedDirs.setForeground(Color.black);
			} 	
		}
		@Override
		public void mouseExited(MouseEvent e) {}	
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseClicked(MouseEvent e) {}
	};
	
	@Override
	public void run() {
//		new GUI();
		
	}
}
