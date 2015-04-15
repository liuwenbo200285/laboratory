package com.wenbo.lambda;

public class Person implements Comparable<Person> {
	private String firstName;
	private String lastName;
	private Integer age;

	public Person(String fn, String ln, Integer a) {
		this.firstName = fn;
		this.lastName = ln;
		this.age = a;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Integer getAge() {
		return age;
	}
	
	public void setAge(Integer age){
		this.age = age;
	}

	@Override
	public int compareTo(Person o) {
		return this.getAge() - o.getAge();
	}
}
