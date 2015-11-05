package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Selection {
	public static void main(String[] args) throws IOException {
		BufferedReader br1 = new BufferedReader(new FileReader("num1.txt"));
		BufferedReader br2 = new BufferedReader(new FileReader(
				"RawTrainingSet10000.txt"));
		BufferedWriter bw1 = new BufferedWriter(new FileWriter("selection.txt"));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(
				"RawTrainingSet9800.txt"));
		HashMap<String, String> map = new HashMap<String, String>();

		String s = null;
		while ((s = br2.readLine()) != null) {
			String[] words = s.split("\\t");
			String key = words[0];
			String value = words[1];
			map.put(key, value);
		}
		s = null;
		while ((s = br1.readLine()) != null) {
			String value = map.get(s);
			String temp = s + "\t" + value;
			System.out.println(temp);
			bw1.write(temp);
			bw1.newLine();

		}
		bw1.close();

	}
}
