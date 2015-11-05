package main;

import java.io.IOException;
import segmentation.SplitedWords;
import classifier.BayesClassifier;
import classifier.Prediction;

public class Main {
	public static void main(String[] args) throws IOException {

		// 先对训练集做了分词，构建了如下的结构。HashMap<List<String>, String>类的preMap对象
		// {[第一条分词结果]：极性，[第二条分词结果]：极性，[第三条分词结果]：极性，......}
		SplitedWords splitedwords = new SplitedWords("training set.txt", true);		
		//SplitedWords splitedwords = new SplitedWords("TrainingSet50.txt", true);

		// 利用刚才构建的 splitedwords 进行训练。
		// 得出了下一步预测所需要的全部参数。
		BayesClassifier bayesclassifier = new BayesClassifier(splitedwords);

		// 对要预测的做分割
		SplitedWords rawWords = new SplitedWords("test2\\200samples.txt", false);
		// 进行预测
		Prediction prediction = new Prediction(rawWords, bayesclassifier);

		// 输出结果，传入输出地址
		prediction.printResult("test2\\200samples2.txt");
		System.out.println("预测结束");
	}
}
