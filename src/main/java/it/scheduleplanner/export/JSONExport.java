package it.scheduleplanner.export;

import java.time.LocalDate;
import java.util.Set;

import it.scheduleplanner.utils.Employee;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONExport {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * This method may be primarily used for debug purposes but can also be used to get an overview over all the employees.<br>
	 * It exports a JSON file containing all the necessary information bound to every Employee.
	 * 
	 * @param employees Set of Employees to be exported
	 * @param pathToDirectory String describing the path to the desired directory
	 * @return false if an error occurred
	 * <li> true if everything functioned
	 */
	protected static Boolean employeeExport(Set<Employee> employees, String pathToDirectory){
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
		
		return null;
	}
	
}


