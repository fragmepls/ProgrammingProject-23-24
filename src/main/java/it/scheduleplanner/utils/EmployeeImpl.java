package it.scheduleplanner.utils;

public class EmployeeImpl implements Employee{
	String name;
	
	public EmployeeImpl(String name){
		this.name = name;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

}
