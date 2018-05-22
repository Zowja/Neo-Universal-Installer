package install;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import gui.GUI;

public class ThreadExtract extends Thread {
	
	public static File extract;
	public static LinkedList<File> mods, dirExtract, jarExtract;
	static Boolean isOnlyResources = false;
	
	public ThreadExtract(LinkedList<File> mods) throws Exception{
		extract = new File("./temp");
		this.mods = mods;
		dirExtract = new LinkedList<File>();
		jarExtract = new LinkedList<File>();
	}
	
	public void run(){
		try {
				GUI.addText("Installing!");
				
				GUI.addText("Extracting mod. . .");
				int order = 0;
				for(File mod : mods){
					System.out.println(mod);
					unZip(mod, new File(extract, "/" + order));
					order++;
				}
				
				for(File file : extract.listFiles()){
					detectSetup(file);
					GUI.addText("Detecting setup...");
					
					GUI.addText("Detecting preset name...");
					if(dirExtract.getLast() != null)
					GUI.name = detectName(dirExtract.getLast());
				}
				if(GUI.name != null){
					GUI.addText(GUI.name);
					GUI.install();
				}
				else {
					GUI.addText("No name detected.");
					GUI.addText("Give your mod a name:");
					GUI.scroll2.setVisible(true);
					GUI.finish.setVisible(true);
					return;
				}
		} catch (Exception e) {
			e.printStackTrace();
			try{
				ThreadInstall.deleteFile(extract);
			} catch (Exception e2) {
				
			}
			GUI.addText("Extract failed.");
		}
	}
	
	
	public void unZip(File zipFilePath, File destDirectory)throws Exception{
		try {
        if (!destDirectory.exists()) {
        	destDirectory.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            File filePath = new File(destDirectory, entry.getName());
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                filePath.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			GUI.addText("ERROR: Could not extract the zip!");
			throw new Exception("Failed to install.");
		}
    }
	
	 private void extractFile(ZipInputStream zipIn, File file) throws IOException {
		 	new File(file, "..").mkdirs();
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
	        byte[] bytesIn = new byte[4096];
	        int read = 0;
	        while ((read = zipIn.read(bytesIn)) != -1) {
	            bos.write(bytesIn, 0, read);
	        }
	        bos.close();
	    }
	 
	 private void detectSetup(File extract){
		 System.out.println(extract);
		 boolean detectedJar = false, detectedDir = false;
		 for(File file : extract.listFiles()){
			 if(file.isDirectory()){
				 if(file.getName().toLowerCase().contains("jar")){
					 jarExtract.add(file);
					 detectedJar = true;
				 }
				 else if(file.getName().toLowerCase().contains("dir")){
					 dirExtract.add(file);
					 detectedDir = true;
				 }
			 }
		 }
		 for(File file : extract.listFiles()){
			 if(detectedDir == false && file.getName().toLowerCase().contains("resources")){
				 dirExtract.add(file);
				 isOnlyResources = true;
				 detectedDir = true;
			 }
			 else if(detectedJar == false && file.getName().toLowerCase().contains("minecraft")){
				 jarExtract.add(file);
				 detectedJar = true;
			 }
		 }
		 if(!detectedJar) jarExtract.add(null);
		 if(!detectedDir) dirExtract.add(null);
	 }
	 
	 private String detectName(File extractPath){
		 File versionsFile = new File(extractPath, "/versions");
		 if(versionsFile.exists() && versionsFile.isDirectory()){
			 for(File file : versionsFile.listFiles()){
				 if(file.isDirectory()){
					 return file.getName();
				 }
			 }
		 }
		 return null;
	 }
	 


}
