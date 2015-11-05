package classifier;

/**
 * 传进来预处理之后的 preMap。HashMap<List<String>, String> preMap
 * 结构：{[第一条分词结果]：极性，[第二条分词结果]：极性，[第三条分词结果]：极性，......}，其中极性均为“0”，表示未分类。 生成了一个预测列表
 * preMap。就是原来的列表，只是将极性做了处理。根据训练做了分类。
 * 
 * @author WJLUCK
 * 
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import segmentation.SplitedWords;


public class Prediction {
	private HashMap<List<String>, String> decMap;
	private BayesClassifier bc = null;

	//下面这些词不计算概率
	private String stopPath = "stopwords.txt";
	private HashSet<String> stopWords = new HashSet<String>();

	public Prediction(SplitedWords sw, BayesClassifier bc) throws IOException {
		this.decMap = sw.preMap;
		this.bc = bc;
		initStopWords();
		getAllEmotion();

	}

	/**
	 * 该方法将经过预处理传进来的 decMap中的每条微博，更新了下情绪标签。
	 */
	private void getAllEmotion() {

		// 全部待预测的微博组成的List集合。
		Set<List<String>> decSet = decMap.keySet();

		for (List<String> words : decSet) {
			Double prob = getAEmotion(words);
			String flag = null;

			// 下面这句是主动学习时用来输出低概率微博标号的语句。
			// activeLearning(words, prob);

			if (prob > 0) {
				flag = "1";
			} else {
				flag = "-1";
			}
			decMap.put(words, flag);
		}
	}

	/**
	 * 主动学习选择微博部分。 判断传进来的微博是否容易确定其极性，不易确定的话，就将其标号输出至 “num.txt”。
	 * 0.965912174237E-190 表示的是概率的临界值。
	 * 
	 * @param words
	 *            要被判断的一条微博
	 * @param prob
	 *            这条微博的概率。
	 */
	private void activeLearning(List<String> words, Double prob) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("num.txt"));
			if (Math.abs(prob) < 0.965912174237E-190) {
				String s = words.get(0);
				System.out.println(s);
				bw.write(s);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 计算每个分好词的句子的情绪极性，积极的概率和消极概率之差。
	 * 
	 * @param words
	 *            一条分好词的微博。
	 * @return 返回表示微博情绪积极的概率和消极概率之差。
	 */
	private Double getAEmotion(List<String> words) {
		double pPos = getPWeibo(words, bc.TKPosProbability, bc.pPosNum,
				bc.pWordPos0);
		double pNeg = getPWeibo(words, bc.TKNegProbability, bc.pNegNum,
				bc.pWordNeg0);

		return (pPos - pNeg);
	}

	/**
	 * 对传进去的一条微博，利用贝叶斯计算其属于某一类的概率，调用时要分别计算其属于正向和负向的概率。
	 * 
	 * @param words
	 *            传进来的分好词的一条微博
	 * @param pro
	 *            特征值的对应极性Map，“键-概率”对的形式。
	 * @param pD
	 *            训练文档的在对应极性下的概率。
	 * @param pC
	 *            其中的某一个词若不是特征值，该词的概率用这个值表示。
	 * @return 文档属于该分类的概率。
	 */
	private double getPWeibo(List<String> words, HashMap<String, Double> pro,
			double pD, double pC) {

		double p = pD;
		Set<String> set = pro.keySet();
		for (String word : words) {
			if (isNumber(word) || stopWords.contains(word)) {
				p *= 1; // 不计算其概率
			} else if (!set.contains(word)) {
				p *= pC;
			} else {
				Double pp = pro.get(word);
				if (pp != null) {
					p *= pro.get(word);
				}
			}
		}
		return p;
	}

	/**
	 * 用来判断一个字符串是不是数字
	 * 
	 * @param word需要判断的字符串
	 * @return 该字符串若为数字返回true，否则返回false。
	 */
	private boolean isNumber(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 将结果写回文件
	 * @param path写回文件的地址。
	 * @throws IOException
	 */
	
	public void printResult(String path) throws IOException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		Set<List<String>> decSet = decMap.keySet();

		for (List<String> words : decSet) {
			String flag = decMap.get(words);
			String num = words.remove(0);

			String string = num + "\t" + flag + "\t" + listToString(words);

			bw.write(string);
			bw.newLine();
		}
		bw.close();

	}

	/**
	 * 将一个List连接成一个String
	 * 
	 * @param list
	 *            需要拼接的List
	 * @return 拼接好的String
	 */
	public String listToString(List<String> list) {
		String string = "";
		for (String s : list) {
			string += s;
		}
		return string;
	}
	/**
	 * 构建一个集合，在该集合中出现的词不计算概率。
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
}
