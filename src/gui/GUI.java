package gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import install.Directory;
import install.ThreadExtract;
import install.ThreadInstall;

public class GUI {
	
	public static String version, name;
	public static JButton select, addMod, install, finish;
	private static JList<String> versionList;
	private static JTextArea text;
	private static LinkedList<File> mods;
	public static JScrollPane scroll, scroll2;
	private Thread extractThread;
	private static JTextArea nameInput;
	
	public GUI() throws IOException{
		mods = new LinkedList<File>();
		
		JFrame frame = new JFrame("New Frontier Craft Installer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(488,480);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		JPanel p = new JPanel();
		p.setBackground(Color.BLACK);
		frame.getContentPane().add(p);
		frame.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				try {
					File deleteTemp = new File("../temp");
					if(deleteTemp.exists())
					ThreadInstall.deleteFile(deleteTemp);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				
			}
			
		});
		
		
		ImageIcon image = new ImageIcon(this.getClass().getClassLoader().getResource("logo.png"));
		JLabel label = new JLabel("", image, JLabel.CENTER);
		p.add(label);
		text = new JTextArea();
		text.setEditable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		scroll = new JScrollPane(text);
		scroll.setPreferredSize(new Dimension(460, 160));
		p.add(scroll);
		
		versionList = new JList<String>(Directory.getVersionNames());
		versionList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		versionList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		versionList.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(versionList);
		listScroller.setPreferredSize(new Dimension(460, 160));
		p.add(listScroller);
		
		nameInput = new JTextArea("", 1, 20);
		nameInput.setEditable(true);
		nameInput.setVisible(true);
		scroll2 = new JScrollPane(nameInput);
		scroll2.setVisible(false);
		p.add(scroll2);
		
		JFileChooser fc = new JFileChooser(new File(".."));
		
		select = new JButton(" Select ");
		p.add(select);
		select.setEnabled(true);
		select.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if(versionList.getSelectedIndex() == -1){
					addText("No version selected. Please select a version to install on.");
					return;
				}
				
				addText("Select the Mod file.");
				
				JFileChooser fc = new JFileChooser(new File(".."));
				fc.setDialogTitle("Choose the Mod .zip File");
				fc.getActionMap().get("viewTypeDetails").actionPerformed(null);
				select.setEnabled(false);
				fc.showOpenDialog(null);
				
				File temp = fc.getSelectedFile();
				if(temp != null){
					if(!temp.getPath().substring(temp.getPath().length()-4).contains(".zip")){
						addText("WARNING: Unable to use file to install!");
						addText("Please select a mod file. Current compatible files: Only zip files.");
						select.setEnabled(true);
						return;
					}
					mods.add(temp);
					listScroller.setVisible(false);
					select.setVisible(false);
					install.setVisible(true);
					addMod.setVisible(true);
					addText("You can add another mod on top of that, or would you like to install now?");
				} 
				else addText("No mod selected. Install cancelled.");
			}
			
		});
		
		install = new JButton(" Install ");
		p.add(install);
		install.setEnabled(true);
		install.setVisible(false);
		install.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
					try {
						extractThread = new ThreadExtract(mods);
						extractThread.start();
						install.setVisible(false);
						addMod.setVisible(false);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
			
		});
		
		addMod = new JButton(" Add Another Mod ");
		p.add(addMod);
		addMod.setEnabled(true);
		addMod.setVisible(false);
		addMod.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addMod.setEnabled(false);
				addText("Select the Mod file.");
				
				JFileChooser fc = new JFileChooser(new File(".."));
				fc.setDialogTitle("Choose the Mod .zip File");
				install.setEnabled(false);
				addMod.setEnabled(false);
				fc.showOpenDialog(null);
				
				File temp = fc.getSelectedFile();
				if(temp != null){
					if(!temp.getPath().substring(temp.getPath().length()-4).contains(".zip")){
						addText("WARNING: Unable to use file to install!");
						addText("You can add another mod on top of that, or would you like to install now?");
						install.setEnabled(true);
						addMod.setEnabled(true);
						return;
					}
					mods.add(temp);
					install.setEnabled(true);
					addMod.setEnabled(true);
					addText("You can add another mod on top of that, or would you like to install now?");
				} 
				else {
					addText("WARNING: No mod selected!");
					addText("You can add another mod on top of that, or would you like to install now?");
					install.setEnabled(true);
					addMod.setEnabled(true);
				}
			}
			
		});
		
		finish = new JButton(" Install ");
		p.add(finish);
		finish.setVisible(false);
		finish.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				name = nameInput.getText();
				if(!name.equals(""))
					try {
						finish.setEnabled(false);
						install();
					} catch (Exception e) {
						e.printStackTrace();
					}
				else addText("There is no name for your mod.");
			}
			
		});

		frame.setVisible(true);
		addText("Neo-Universal Installer 1.0");
		addText("Select the version you want to install on.");
	}
	
	public static void install(){
		new ThreadInstall(Directory.getFile(versionList.getSelectedIndex()), name).start();;
	}
	
	public static void addText(String string){
		text.setText(text.getText() + " " + string + "\n" );
		scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
	}
	
}
