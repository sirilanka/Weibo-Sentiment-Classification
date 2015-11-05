package segmentation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Word;

/**
 * 这个类的重要作用是构建了HashMap<List<String>, String>类的preMap对象
 * 结构是：{[第一条分词结果]：极性，[第二条分词结果]：极性，[第三条分词结果]：极性，......}
 * 构造方法传入的是文件地址，以及用于预测还是用于测试。
 * 若用于测试的话，词极性返回出去全部为0.
 */
public class SplitedWords {

	// 表示传入的文件路径
	private String path = "";
	// 标签是true 表示用来训练；fales 表示预测。默认true，表示分词的结果用于训练。
	private boolean flag = true;

	// 存储被分割好的微博，格式：{[第一条分词结果]：极性，[第二条分词结果]：极性，[第三条分词结果]：极性，......}
	public HashMap<List<String>, String> preMap = new HashMap<List<String>, String>();

	// 表示停用词的集合，从文件中读。
	private String stopPath = "stopwords.txt";
	private HashSet<String> stopWords = new HashSet<String>();

	private ComplexSeg complexseg = new ComplexSeg(Dictionary.getInstance(""));

	/**
	 * 构造方法，对path，flag，stopWords 做初始化。 得到了 preMap 文件分词结果。
	 * 
	 * @param path需要被分割的文件地址
	 * @param flag表示该分词将来是被用来训练还是用来预测
	 * @throws IOException
	 */
	public SplitedWords(String path, boolean flag) throws IOException {
		this.path = path;
		this.flag = flag;

		// 从文件中构建一个 停用词set。
		initStopWords();
		getPreMap();

	}

	/**
	 * 根据成员变量stopPath 表示的文件，构建一个停用词集合。
	 * @throws IOException
	 */
	private void initStopWords() throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(stopPath));
		String temp = "";
		while ((temp = br.readLine()) != null) {
			stopWords.add(temp);
		}
		br.close();
	}

	/**
	 * 从指定的文件中构造 preMap。 格式：{[第一条分词结果]：极性，[第二条分词结果]：极性，[第三条分词结果]：极性，......}
	 * 
	 * @throws IOException
	 */
	private void getPreMap() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String words = "";
		while ((words = br.readLine()) != null) {
			String[] wordsDic = words.split("\\t");
			
			List<String> wordsList = new ArrayList<String>();			
			String tempFlag = "";
			if (this.flag == true) {
				tempFlag = wordsDic[1];
				wordsList = getAWeibo(wordsDic[0] +" "+ wordsDic[2]);
			} else {
				wordsList = getAWeibo(words);
				tempFlag = "0";
			}
			preMap.put(wordsList, tempFlag);
		}
		br.close();

	}

	/**
	 * 对传入的单条微博进行分词，去停用词处理
	 * @param words需要被分词的微博
	 * @return 经过分词并且去掉停用词的微博。是个List[分词1，分词2，分词3....]。
	 */
	private List<String> getAWeibo(String words) {
		ArrayList<String> wordsList = getSplitedWords(words);
		if (flag == false) {
			return wordsList;
		} else {
			for (String temp : wordsList) {
				if (stopWords.contains(temp)) {
					wordsList.remove(temp);
				}
			}
			return wordsList;
		}
	}

	/**
	 * 这个方法是用来对传入的句子进行分词划分
	 * @param sentence传入的句子
	 * @return 返回一个List,元素为该句子划分的词。
	 */
	private ArrayList<String> getSplitedWords(String sentence) {
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
}
