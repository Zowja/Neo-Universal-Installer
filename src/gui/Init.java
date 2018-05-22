package gui;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class Init {
	
	public static void main(String args[]) throws IOException{
		new GUI();
		
		/*
		String[] aa = {"0", "4aa", "cheese4", "5"};
		JList<String> list = new JList<String>(aa); //data has type Object[]
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(250, 80));
		
		JFileChooser fc = new JFileChooser(new File(".."));
		fc.showOpenDialog(null);
		System.out.println(fc.getSelectedFile());
		*/
	}

}
