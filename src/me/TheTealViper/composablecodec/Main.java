package me.TheTealViper.composablecodec;

public class Main {
	public static void main(String[] args) {
		Thing t = ThingBuilder.create()
			.add(5).add(2)
			.append()
			.add(3)
			.append()
			.add(4)
			.build();
		System.out.println(t);
	}
}
