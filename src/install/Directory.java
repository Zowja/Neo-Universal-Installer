package install;

import java.io.File;
import java.util.LinkedList;

public class Directory {
	
	private static LinkedList<File> files;
	
	public static String[] getVersionNames(){
		File versions = new File(getAppDir(), "versions");
		LinkedList<String> list = new LinkedList<String>();
		files = new LinkedList<File>();
		for(File version : versions.listFiles()){
				if(version.isDirectory()){ 
					list.add(fileToVersionName(version.getName()));
					files.add(version);
			}
		}
		String[] convert = new String[list.size()];
		int i = 0;
		for(String name : list){
			convert[i] = name;
			i++;
		}
		return convert;
	}
	
	public static File getFile(int index){
		return files.get(index);
	}
	
	public static String fileToVersionName(String fileName){
		
		char age = fileName.charAt(0);
		
		if(age == 'b'){
			return " Beta " + fileName.substring(1); 
		}
		else if(age == 'a'){
			return " Alpha " + fileName.substring(1); 
		}
		return  " " + fileName + " ";
	}
	
	public static File getAppDir() {
		
	 	String s = "minecraft";
		String s1 = System.getProperty("user.home", ".");
		File file;
		switch (EnumOSMappingHelper.enumOSMappingArray[getOs().ordinal()]) {
		case 1: // '\001'
		case 2: // '\002'
			file = new File(s1, (new StringBuilder()).append('.').append(s)
					.append('/').toString());
			break;

		case 3: // '\003'
			String s2 = System.getenv("APPDATA");
			if (s2 != null) {
				file = new File(s2, (new StringBuilder()).append(".").append(s)
						.append('/').toString());
			} else {
				file = new File(s1, (new StringBuilder()).append('.').append(s)
						.append('/').toString());
			}
			break;

		case 4: // '\004'
			file = new File(s1, (new StringBuilder())
					.append("Library/Application Support/").append(s)
					.toString());
			break;

		default:
			file = new File(s1, (new StringBuilder()).append(s).append('/')
					.toString());
			break;
		}
		if (!file.exists() && !file.mkdirs()) {
			throw new RuntimeException((new StringBuilder())
					.append("The working directory could not be created: ")
					.append(file).toString());
		} else {
			return file;
		}
	}

	private static EnumOS2 getOs() {
		String s = System.getProperty("os.name").toLowerCase();
		if (s.contains("win")) {
			return EnumOS2.windows;
		}
		if (s.contains("mac")) {
			return EnumOS2.macos;
		}
		if (s.contains("solaris")) {
			return EnumOS2.solaris;
		}
		if (s.contains("sunos")) {
			return EnumOS2.solaris;
		}
		if (s.contains("linux")) {
			return EnumOS2.linux;
		}
		if (s.contains("unix")) {
			return EnumOS2.linux;
		} else {
			return EnumOS2.unknown;
		}
	}

}
