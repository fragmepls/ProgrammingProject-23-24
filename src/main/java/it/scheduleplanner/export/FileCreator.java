package it.scheduleplanner.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Map;

class FileCreator {
	
	/**
	 * 
	 * @param title
	 * @param pathToDirectory
	 * @param fileExtension
	 * @param overrideExisting
	 * @return Map{@literal <Boolean, String>}; {@literal <false = error, path to file>}
	 */
	protected static Map<Boolean, String> create(String title, String pathToDirectory, String fileExtension, boolean overrideExisting) {
		String file = title.strip() + fileExtension.strip();
		String fileName = "";
		pathToDirectory = pathToDirectory.strip();
		if (!pathToDirectory.endsWith("/")) {
			pathToDirectory += "/";
		}
		
		fileName = pathToDirectory + file;
		
		try {
			if(overrideExisting) {
				if(!Files.exists(Path.of(fileName))) { // file doesn't already exist
					Files.createFile(Path.of(fileName)); // create file
				}
//				System.out.println(fileName + " might get overwritten");
				return Map.of(true, fileName);
			}
			int i = 1; //variable to alter filename
			while(true) {
				if (!Files.exists(Path.of(fileName))) { //if file doesn't already exist
//		    		System.out.println("create file: " + fileName);
		    		Files.createFile(Path.of(fileName)); //create file
					return Map.of(true, fileName);
			    } 
			    else { //file already exists
//			    	add prefix to file
			    	fileName = pathToDirectory + "(" + i + ")" + file;
			    	i++;
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return Map.of(false, null);
	}
}
