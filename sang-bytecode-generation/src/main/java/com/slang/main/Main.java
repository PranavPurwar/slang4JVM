package com.slang.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
//TODO Separate to different project later
public class Main {
	static String readFile(String resource) {
		String content = null;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)));){
			String line = null;
			StringBuilder sb = new StringBuilder();
			while(null !=(line = reader.readLine())) {
				sb.append(line);
				sb.append(System.lineSeparator());
			}
			content = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return content;
	}

	public static void main(String[] args) {
		String[] scriptArray = new String[] {
				"0_discriminant.sl",
				"1_factorial.sl",
				"2_fibonacci1.sl",
				"3_fibonacci2.sl",
				"4_fibonacci3.sl",
				"5_HelloWorld.sl",
				"6_oneToHundred.sl" };
		try {
			String source = readFile(scriptArray[4]);
			String name = scriptArray[4];
			Slang sl = new Slang(name, source);
			sl.interpret();
			sl.compile();
			System.out.println("Compiled Java class can be found in " + System.getProperty("user.dir") + File.separator + name.toUpperCase());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
