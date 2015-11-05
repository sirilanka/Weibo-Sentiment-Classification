package test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Word;

public class Test {

	private static ComplexSeg complexseg = new ComplexSeg(
			Dictionary.getInstance(""));

	private static ArrayList<String> getSplitedWords(String sentence) {
		if (StringUtils.isBlank(sentence)) {// 句子为空的话，返回null
			return null;
		}

		ArrayList<String> words = new ArrayList<String>();

		StringReader reader = new StringReader(sentence);

		MMSeg mmSeg = new MMSeg(reader, complexseg);

		Word word = null;

		try {
			while ((word = mmSeg.next()) != null) {
				words.add(word.getString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return words;
	}
	private static boolean isNumber(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	public static void main(String[] args) throws IOException {
		String txt1 = "2	1	最近貌似忙乎乎的出差，围着工作转来转去，我要的和期待的生活离我远去，还好现在回到我要的世界。趁着周末终于将手机换了，从诺基亚E72I将通讯录导到三星里，智能手机太强大，搞得我一下子不适应。感谢老公送的结婚周年礼物，三星这款是我的最爱，好于4S。";
		List<String> words = getSplitedWords(txt1);
		System.out.println(words.toString());
		
		System.out.println("----------------------");
		System.out.println(isNumber("jfakljfka"));
		System.out.println(isNumber("12345"));
		
	}
}
