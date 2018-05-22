package install;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import gui.GUI;

public class ThreadInstall extends Thread {
	
	File oldjar, newjar, dir, extract;
	String dirString, name, version;
	ArrayList<String> installFiles;
	
	public ThreadInstall(File version, String name){
		this.name = name;
		this.version = version.getName();
		this.dir = new File(version, "../..");
		this.oldjar = new File(version, "/" + version.getName() + ".jar");
		this.newjar = new File(dir, "/versions/" + name + "/" + name + ".jar");
		this.extract = new File("../temp");
		dirString = Directory.getAppDir().getPath().replace("\\", "/");
	}
	
	@Override
	public void run(){
		try {
			copyAndRenameJar(dirString);
			deleteMETAINF(newjar);
			
			for(int i = 0; i < ThreadExtract.mods.size(); i++){
				installFiles = new ArrayList<String>();
				File jarExtract = (ThreadExtract.jarExtract.get(i) == null) ? new File(extract, "/" + i) : ThreadExtract.jarExtract.get(i);
				listFilesForFolder(jarExtract, jarExtract);
				inputToBetaJar(newjar);
				if(ThreadExtract.dirExtract.get(i) != null){
					installFiles = new ArrayList<String>();
					listFilesForFolder(ThreadExtract.dirExtract.get(i), ThreadExtract.dirExtract.get(i));
					if(ThreadExtract.isOnlyResources) dirString += "/resources";
					inputToGameDir(dirString);
				}
			}
			moveAndManipulateJson();
			GUI.addText("Install successful! You may now exit.");
		} catch (Exception e) {
			GUI.addText("Install failed.");
			e.printStackTrace();
		} finally {
			try{
				//TODO: Deactivate install button once done
				deleteFile(extract);
			} catch (Exception e) {
				
			}
		}
	}
	
	private void moveAndManipulateJson() throws IOException{
		File json = new File(oldjar.toString().substring(0, oldjar.toString().length()-3) + "json");
		File jsonNew = new File(newjar.toString().substring(0, newjar.toString().length()-3) + "json");
		if(jsonNew.exists()) return;
		Files.copy(json.toPath(), jsonNew.toPath(), StandardCopyOption.REPLACE_EXISTING);
		Scanner temp = new Scanner(jsonNew);
		temp.useDelimiter("\\Z");
		String wholeFile = temp.next();
		wholeFile = wholeFile.replaceAll("client", "removed");
		wholeFile = wholeFile.replaceAll(version, name);
		temp.close();
		
		PrintWriter jsonout = new PrintWriter(jsonNew);
		jsonout.print(wholeFile);
		jsonout.flush();
		jsonout.close();
	}
	
	 private void copyAndRenameJar(String path)throws Exception{
		 GUI.addText("Moving and renaming jar.");
		 try {
		 Path betaJar = oldjar.toPath();
		 newjar.mkdirs();
		 Path modplace = newjar.toPath();
		 Files.copy(betaJar, modplace, StandardCopyOption.REPLACE_EXISTING);
		 } catch(Exception e) {
			 e.printStackTrace();
			 GUI.addText("ERROR: Could not move the jar!");
			 throw new Exception("Failed to install.");
		 }
	 }
	
	public void listFilesForFolder(File folder, File originalPath) {
	    for (File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry, originalPath);
	        } else {
	        	installFiles.add(fileEntry.getPath().substring(originalPath.getPath().length()));
	            installFiles.add(fileEntry.getPath());
	        }
	    }
	}
	
	public void listEverything(final File folder){
		File[] list = folder.listFiles();
		if(list != null && list.length > 0){
			for(int i = 0; i < list.length; i++){
				listEverything(list[i]);
			}
		}
		else
		installFiles.add(folder.getPath());
	}
	
	public void inputToBetaJar(File path)throws Exception{
		GUI.addText("Installing mod into jar.");
		try {
		/* Define ZIP File System Properies in HashMap */    
        Map<String, String> zip_properties = new HashMap<>(); 
        /* We want to read an existing ZIP File, so we set this to False */
        zip_properties.put("create", "false");
        /* Specify the encoding as UTF -8 */
        zip_properties.put("encoding", "UTF-8");
        /* Create ZIP file System */
        try (FileSystem zipfs = FileSystems.newFileSystem(URI.create("jar:" + path.toURI().toString()), zip_properties)) {
			for(int i = 0; i < installFiles.size(); i+= 2){
             /* Create a Path in ZIP File */
            Path ZipFilePath = zipfs.getPath(installFiles.get(i));
            /* Path where the file to be added resides */
            Path addNewFile = Paths.get(installFiles.get(i+1));  
            /* Append file to ZIP File */
            Files.createDirectories(ZipFilePath.getParent());
            Files.copy(addNewFile,ZipFilePath, StandardCopyOption.REPLACE_EXISTING); 
			}
        } 
		} catch (Exception e) {
			GUI.addText(e.toString());
			GUI.addText("ERROR: Could not install mod into jar.");
			throw new Exception("Failed to install.");
		}
	}
	
	public void inputToGameDir(String path)throws Exception{
		GUI.addText("Copying files to game directory.");
		try {
		for(int i = 0; i < installFiles.size(); i+= 2){
        Path gameDirPath = Paths.get(path + installFiles.get(i));
        Path fileBeingCopied = Paths.get(installFiles.get(i+1));  
        Files.createDirectories(gameDirPath.getParent());
        Files.copy(fileBeingCopied,gameDirPath, StandardCopyOption.REPLACE_EXISTING); 
		}
		} catch(Exception e) {
			GUI.addText(e.toString());
			GUI.addText("ERROR: Could not copy files to directory!");
			throw new Exception("Failed to install.");
		}
	}
	
	public void deleteMETAINF(File path) throws Exception{ 
		GUI.addText("Deleting META-INF from jar.");
		try{
        Map<String, String> zip_properties = new HashMap<>(); 
        zip_properties.put("create", "false"); 
        try (FileSystem zipfs = FileSystems.newFileSystem(URI.create("jar:" + path.toURI().toString()), zip_properties)) {
            Path pathInZipfile = zipfs.getPath("META-INF/MANIFEST.MF");
            Files.delete(pathInZipfile);
            
            pathInZipfile = zipfs.getPath("META-INF/MOJANG_C.DSA");
            Files.delete(pathInZipfile);
            
            pathInZipfile = zipfs.getPath("META-INF/MOJANG_C.SF");
            Files.delete(pathInZipfile);
            
            pathInZipfile = zipfs.getPath("META-INF/");
            Files.delete(pathInZipfile);
        } 
		} catch (Exception e) {
			e.printStackTrace();
			GUI.addText("WARNING: Cannot fully delete META-INF!");
		}
    }
	
	public static void deleteFile(File file) throws Exception{
		try {
			if(file.isFile()){
				Files.delete(file.toPath());
			} else {
				File[] files = file.listFiles();
				for(File inside : files){
					if(inside.isFile()){
					Files.delete(inside.toPath());
					} else {
						deleteFile(inside);
					}
				}
				Files.delete(file.toPath());
			}
		} catch (Exception e) {
			GUI.addText(e.toString());
			GUI.addText("ERROR: Could not delete " + file.getName() + "!");
			throw new Exception("Failed to install.");
		}
	}

}
