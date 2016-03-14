package com.slang.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.slang.main.Slang;
//TODO Separate to different project later
public class Main {
	static String readFile(File file) {
		String content = null;
		FileReader reader = null;
		try {
			reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return content;
	}

	public static void main(String[] args) {
		String[] scriptArray = new String[] {
				"resources/test_scripts/0_discriminant.sl",
				"resources/test_scripts/1_factorial.sl",
				"resources/test_scripts/2_fibonacci1.sl",
				"resources/test_scripts/3_fibonacci2.sl",
				"resources/test_scripts/4_fibonacci3.sl",
				"resources/test_scripts/5_HelloWorld.sl",
				"resources/test_scripts/6_oneToHundred.sl" };
		try {
			File file = new File(scriptArray[4]);
			String source = readFile(file);
			String name = file.getName().split("\\.")[0].split("_")[1];
			Slang sl = new Slang(name, source);
			sl.interpret();
			sl.compile();
			System.out.println("Compiled Java class can be found in " + System.getProperty("user.dir") + File.separator + name.toUpperCase());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
