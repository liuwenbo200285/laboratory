package com.wenbo.lambda;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class LambdasDemo {

	public static void main(String[] args){
//		demo1();
//		demo2();
		demo3();
	}
	
	
	public static void demo1(){
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	    Stream<Integer> stream = numbers.stream();
	    stream.filter((x) -> {
	        return x % 2 == 0;
	    }).map((x) -> {
	        return x * x;
	    }).forEach(System.out::println);
	}
	
	public static void demo2(){
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
		Collections.sort(people,(p1,p2)->{
			return p1.getAge().compareTo(p2.getAge());
		});
		people.stream().forEach(person->System.out.println(person.getFirstName()));
		people.stream().sorted((a,b)->a.compareTo(b));
		people.stream().sorted(Comparator.comparing(Person::getAge).reversed())
		.forEach(person->System.out.println(person.getFirstName()+":"+person.getAge()));
	}
	
	static class NaturalSupplier implements Supplier<Long> {

	    long value = 0;

	    public Long get() {
	        this.value = this.value + 1;
	        return this.value;
	    }
	}
	
	public static void demo3(){
		Stream<Long> natural = Stream.generate(new NaturalSupplier());
	    natural.map((x) -> {
	        return x * x;
	    }).limit(10).forEach(System.out::println);
	}
}
