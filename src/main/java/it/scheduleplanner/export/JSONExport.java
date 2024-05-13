package it.scheduleplanner.export;

import java.time.LocalDate;
import java.util.List;

import it.scheduleplanner.utils.EmployeeInterface;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONExport {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	protected static Boolean employeeExport(List<EmployeeInterface> employees, String pathToDirectory){
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		String file = createFile("employees_" + LocalDate.now().toString(), pathToDirectory);
		if (file == null) {
			return false;
		}
		
		try {
			  mapper.writeValue(new File(file), employees);
		  }
		  catch (IOException e) {
			  e.printStackTrace();
			  return false;
		  }
		
		return true;
	}
	
	private static String createFile(String title, String pathToDirectory) {
		String file = title + ".json";
//		String fileNew = "";
		if (!pathToDirectory.endsWith("/")) {
			pathToDirectory += "/";
		}
		
		file = pathToDirectory + file;
		try {
		    while (true) {
		    	if (Files.exists(Path.of(file))) { //if file already exist
		    		System.out.println(file + " gets overwritten");
		    		// TODO log
		    		return file; //return path to file
			    } 
			    else { //file doesn't already exist
			    	System.out.println("create file: " + file);
		    		Files.createFile(Path.of(file)); //create file
		    		return file; //return path to file
				}
			}
		} 
		catch (IOException e) {
			//TODO log
			e.printStackTrace();
		}
//		fileNew = pathToDirectory + file;
//		int i = 1; //variable to alter filename
//		try {
//		    while (true) {
//		    	if (!Files.exists(Path.of(fileNew))) { //if file doesn't already exist
//		    		System.out.println("create file: " + fileNew);
//		    		Files.createFile(Path.of(fileNew)); //create file
//		    		// TODO log
//		    		return fileNew; //return path to file
//			    } 
//			    else { //file already exists
//			    	//add prefix to file
//			    	fileNew = pathToDirectory + "(" + i + ")" + file;
//			    	i++;
//				}
//			}
//		} 
//		catch (IOException e) {
//			//TODO log
//			e.printStackTrace();
//		}
		return null;
	}
	
}


