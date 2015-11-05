package classifier;

/**
 * 这个类最主要是对 贝叶斯分类所用用到的 参数进行运算，得出了所需要的全部参数。
 * TKPosProbability 和  TKNegProbability 这两各变量存储每个词的P(t|c)，是最主要的变量。
 * pPosNum 和 pNegNum 表示的是训练集中正面和负面情绪所占的比率。
 * pWordPos0 和 pWordNeg0 这两个变量表示 该词不存在于对应的类别中时，运算时传入的参数。
 * 别的变量都是该类内部使用的。
 * 
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import segmentation.SplitedWords;

public class BayesClassifier {

	// 这个用来保存传进来的 经过分词，去停用词之后的结果.
	// {[第一条分词结果]：极性，[第二条分词结果]：极性，[第三条分词结果]：极性，......}
	private HashMap<List<String>, String> preMap = new HashMap<List<String>, String>();

	// 用来保存所有的特征词
	public HashSet<String> featureWord = new HashSet<String>();

	// HashMap用于存储每个词的P(t|c)
	public HashMap<String, Double> TKPosProbability = new HashMap<String, Double>();
	public HashMap<String, Double> TKNegProbability = new HashMap<String, Double>();

	// 分别表示训练后，训练集中 正面微博的概率 和 负面微博的概率，用来预测最后的文档概率
	public double pPosNum = 0;
	public double pNegNum = 0;

	// 保存正面，负面以及总词汇(包括重复)的数目
	private int posNum = 0;
	private int negNum = 0;
	private int sumNum = 0;

	// 存放总词汇数目，不包括重复
	private int num = 0;

	// 下面这两个变量表示不存在的词在对应分类中的概率。
	public double pWordPos0 = 0;
	public double pWordNeg0 = 0;

	/**
	 * 这个类的总接口，传入一个HashMap<String, String>
	 * 结构：{[第一条分词结果]：极性，[第二条分词结果]：极性，[第三条分词结果]：极性，......}
	 * 
	 * @param sp
	 */
	public BayesClassifier(SplitedWords sp) {
		this.preMap = sp.preMap;
		init();

		getTKprobability();
	}

	/**
	 * 计算的是pPosNum,pNegNum,posNum,negNum,sumNum,num,pWordPos0,pWordNeg0 这几个变量
	 */
	private void init() {

		Set<List<String>> set = preMap.keySet();
		for (List<String> key : set) {
			for (int i = 1; i < key.size(); i++) {
				// 提取 特征词，去掉标号
				featureWord.add(key.get(i));
			}
			String value = preMap.get(key);
			if (value.equals("1")) {
				posNum += key.size();
			} else if (value.equals("-1")) {
				negNum += key.size();
			} else {
				System.out.println("有格式错误");
			}
		}

		num = featureWord.size();
		sumNum = posNum + negNum;
		pPosNum = (double) posNum / sumNum;
		pNegNum = (double) negNum / sumNum;
		pWordPos0 = (double) (0 + 1) / (posNum + num);
		pWordNeg0 = (double) (0 + 1) / (negNum + num);
	}

	/**
	 * 用来计算每个词的概率 TKPosProbability 和 TKNegProbability。
	 * 是一个HashMap<String，Double>对象，分别存储每个单词在两类中的概率。
	 */
	private void getTKprobability() {

		Iterator<String> it = featureWord.iterator();
		while (it.hasNext()) {
			String word = it.next();

			// 计算每个词在该分类中的数目
			int numPos = getCategoryNum(word, "1");
			int numNeg = getCategoryNum(word, "-1");

			Double pPos = new Double(((double) (numPos + 1) / (posNum + num)));
			Double pNeg = new Double(((double) (numNeg + 1) / (negNum + num)));

			TKPosProbability.put(word, pPos);
			TKNegProbability.put(word, pNeg);
		}

	}

	/**
	 * 计算每个词在对应分类中出现的总次数
	 * 
	 * @param word传进去需要被计算的词
	 * @param string用来表示正面和负面的标识
	 *            .“1”表示正面，“-1”表示负面
	 * @return 返回该词在对应分类中出现的总数
	 */
	private int getCategoryNum(String word, String flag) {
		int num = 0;
		Set<List<String>> set = preMap.keySet();
		for (List<String> key : set) {
			String value = preMap.get(key);
			if (value.equals(flag)) {
				for (String s : key) {
					if (word.equals(s)) {
						num++;
					}
				}
			}
		}
		return num;
	}
}
