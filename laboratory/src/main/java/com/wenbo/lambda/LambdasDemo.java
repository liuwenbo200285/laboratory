package com.wenbo.lambda;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LambdasDemo {

	public static void main(String[] args){
		List<Person> people = Arrays.asList(
			      new Person("Ted", "Neward", 42),
			      new Person("Charlotte", "Neward", 25),
			      new Person("Michael", "Neward", 19),
			      new Person("Matthew", "Neward", 13),
			      new Person("Neal", "Ford", 45),
			      new Person("Candy", "Ford", 39),
			      new Person("Jeff", "Brown", 23),
			      new Person("Betsy", "Brown", 29)
			    );
		people.stream().sorted((a,b)->a.compareTo(b));
		people.stream().sorted(Comparator.comparing(Person::getAge).reversed())
		.forEach(person->System.out.println(person.getFirstName()+":"+person.getAge()));
		
	
	}
}
