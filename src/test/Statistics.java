package test;

/**
 * 传入机器标记的文件地址和手工标记的文件地址，统计出两个之间相同的数目，计算准确率。
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Statistics {
	public static void main(String[] args) throws IOException {
		BufferedReader br1 = new BufferedReader(new FileReader("test6\\result_after_learning.txt"));
		BufferedReader br2 = new BufferedReader(new FileReader("test6\\result.txt"));
		HashMap<String, String> map = new HashMap<String, String>();
		int num = 0;
		String s = null;		
		while ((s = br1.readLine()) != null) {
			String[] words = s.split("\\t");
			String key = words[0];
			String value = words[1];	
			map.put(key, value);
		}
		
		while ((s = br2.readLine()) != null) {
			String[] words = s.split("\\t");			
			String key = words[0];
			String value = words[1];
			String t = map.get(key);
			if(t != null){
				if(t.equals(value))
				num++;
			}
		}
		System.out.println("测试总数目："+map.size());
		System.out.println("相同的数目："+num);
		System.out.println("正确率："+(double)num/map.size());		
		br1.close();
		br2.close();
	}
}
