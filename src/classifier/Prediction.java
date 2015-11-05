package classifier;

/**
 * ������Ԥ����֮��� preMap��HashMap<List<String>, String> preMap
 * �ṹ��{[��һ���ִʽ��]�����ԣ�[�ڶ����ִʽ��]�����ԣ�[�������ִʽ��]�����ԣ�......}�����м��Ծ�Ϊ��0������ʾδ���ࡣ ������һ��Ԥ���б�
 * preMap������ԭ�����б�ֻ�ǽ��������˴�������ѵ�����˷��ࡣ
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

	//������Щ�ʲ��������
	private String stopPath = "stopwords.txt";
	private HashSet<String> stopWords = new HashSet<String>();

	public Prediction(SplitedWords sw, BayesClassifier bc) throws IOException {
		this.decMap = sw.preMap;
		this.bc = bc;
		initStopWords();
		getAllEmotion();

	}

	/**
	 * �÷���������Ԥ���������� decMap�е�ÿ��΢������������������ǩ��
	 */
	private void getAllEmotion() {

		// ȫ����Ԥ���΢����ɵ�List���ϡ�
		Set<List<String>> decSet = decMap.keySet();

		for (List<String> words : decSet) {
			Double prob = getAEmotion(words);
			String flag = null;

			// �������������ѧϰʱ��������͸���΢����ŵ���䡣
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
	 * ����ѧϰѡ��΢�����֡� �жϴ�������΢���Ƿ�����ȷ���伫�ԣ�����ȷ���Ļ����ͽ���������� ��num.txt����
	 * 0.965912174237E-190 ��ʾ���Ǹ��ʵ��ٽ�ֵ��
	 * 
	 * @param words
	 *            Ҫ���жϵ�һ��΢��
	 * @param prob
	 *            ����΢���ĸ��ʡ�
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
	 * ����ÿ���ֺôʵľ��ӵ��������ԣ������ĸ��ʺ���������֮�
	 * 
	 * @param words
	 *            һ���ֺôʵ�΢����
	 * @return ���ر�ʾ΢�����������ĸ��ʺ���������֮�
	 */
	private Double getAEmotion(List<String> words) {
		double pPos = getPWeibo(words, bc.TKPosProbability, bc.pPosNum,
				bc.pWordPos0);
		double pNeg = getPWeibo(words, bc.TKNegProbability, bc.pNegNum,
				bc.pWordNeg0);

		return (pPos - pNeg);
	}

	/**
	 * �Դ���ȥ��һ��΢�������ñ�Ҷ˹����������ĳһ��ĸ��ʣ�����ʱҪ�ֱ��������������͸���ĸ��ʡ�
	 * 
	 * @param words
	 *            �������ķֺôʵ�һ��΢��
	 * @param pro
	 *            ����ֵ�Ķ�Ӧ����Map������-���ʡ��Ե���ʽ��
	 * @param pD
	 *            ѵ���ĵ����ڶ�Ӧ�����µĸ��ʡ�
	 * @param pC
	 *            ���е�ĳһ��������������ֵ���ôʵĸ��������ֵ��ʾ��
	 * @return �ĵ����ڸ÷���ĸ��ʡ�
	 */
	private double getPWeibo(List<String> words, HashMap<String, Double> pro,
			double pD, double pC) {

		double p = pD;
		Set<String> set = pro.keySet();
		for (String word : words) {
			if (isNumber(word) || stopWords.contains(word)) {
				p *= 1; // �����������
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
	 * �����ж�һ���ַ����ǲ�������
	 * 
	 * @param word��Ҫ�жϵ��ַ���
	 * @return ���ַ�����Ϊ���ַ���true�����򷵻�false��
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
	 * �����д���ļ�
	 * @param pathд���ļ��ĵ�ַ��
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
	 * ��һ��List���ӳ�һ��String
	 * 
	 * @param list
	 *            ��Ҫƴ�ӵ�List
	 * @return ƴ�Ӻõ�String
	 */
	public String listToString(List<String> list) {
		String string = "";
		for (String s : list) {
			string += s;
		}
		return string;
	}
	/**
	 * ����һ�����ϣ��ڸü����г��ֵĴʲ�������ʡ�
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
