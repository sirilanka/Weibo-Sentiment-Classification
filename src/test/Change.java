package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 将这样的微博：1 -1 我苦逼的三星双卡双待手机啊。 变成这样：1 我苦逼的三星双卡双待手机啊。
 * 
 * @author WJLUCK
 * 
 */
public class Change {
	public static void main(String[] args) throws IOException {
		
		System.out.println("----------------------");
		BufferedReader br = new BufferedReader(new FileReader("test4\\test1.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				"test4\\test.txt"));
		String s = "";
		while ((s = br.readLine()) != null) {
			String[] words = s.split("\\t");
			String temp = words[0] + "\t" + words[2];
			System.out.println(temp);

			bw.write(temp);
			bw.newLine();
			
		}
		bw.close();
		br.close();
	}
}
