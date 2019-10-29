package SyntaxAnalysis;

import java.util.List;
import java.util.ArrayList;

public class TreeNode {
	protected double SentimentData;
	protected String[] WordData;
	protected List<String> NounList = new ArrayList<String>();
	protected boolean Negative;
	protected String[] NegativeWord;
	protected TreeNode Component;
	
	public TreeNode() {
		SentimentData = 0;
		WordData = null;
		Negative = false;
		Component = null;
	}

	public TreeNode(String[] Word) {
		this.WordData = Word;
	}
	
	public TreeNode(double Sentiment) {
		this.SentimentData = Sentiment;
	}
	
	public TreeNode(String[] Word, double Sentiment) {
		this.WordData = Word;
		this.SentimentData = Sentiment;
	}

	/**
	 * <pre> Node의 SentimentData 수정 </pre>
	 */
	public void setSentimentData(double Sentiment) {
		this.SentimentData = Sentiment;
	}
	
	/**
	 * <pre> Node의 WordData 수정 </pre>
	 */
	public void setWordData(String[] Word) {
		this.WordData = Word;
	}

	/**
	 * <pre> Node에 ComponentNode 삽입 </pre>
	 */
	public void addComponent(TreeNode ComponentNode) {
		this.Component = ComponentNode;
	}

	/**
	 * <pre> Node의 NounList에 NounWord 삽입 </pre>
	 */
	public void addNoun(String NounWord) {
		this.NounList.add(NounWord);
	}
	
	/**
	 * <pre> Node의 SentimentData 불러오기 </pre>
	 */
	public double getSentimentData() {
		return this.SentimentData;
	}

	/**
	 * <pre> Node의 WordData 불러오기 </pre>
	 */
	public String[] getWordData() {
		return this.WordData;
	}

	/**
	 * <pre> Node의 NounList 불러오기 </pre>
	 */
	public List<String> getNounList() {
		return this.NounList;
	}
	
	/**
	 * <pre> Node의 Negative 값 불러오기 </pre>
	 */
	public boolean getNegative() {
		return this.Negative;
	}
	
	/**
	 * <pre> Node의 NegativeWord 불러오기 </pre>
	 */
	public String[] getNegativeWord() {
		return this.NegativeWord;
	}

	/**
	 * <pre> Node의 ComponentNode 불러오기 </pre>
	 */
	public TreeNode getComponentNode() {
		return this.Component;
	}
}
