package SentimentAnalysis;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import SyntaxAnalysis.TreeNode;

public class SentimentTree {
	// 감정 단어 목록
	protected HashMap<String, Double> SentimentWord = new HashMap<String, Double>();
	
	/**
	 * <pre> Tree 순회 및 문장 감정 수치 계산 </pre>
	 */
	protected BigDecimal treeTraversal(TreeNode SyntaxTree) {
		if (SyntaxTree != null) {
			TreeNode SentmentTree = new TreeNode();
			SentmentTree = SyntaxTree;
			// 현재 노드까지의 감정 수치
			BigDecimal NodeSentimentData = new BigDecimal("0");
			
			// 재귀적 방법을 이용하여, 자식노드부터 순회
			NodeSentimentData = this.treeTraversal(SentmentTree.getComponentNode());
			// 해당 Node에 감정 수치 삽입
			this.putSentimentData(SentmentTree);
			// 자식 노드의 감정 수치값 합산
			NodeSentimentData = NodeSentimentData.add(BigDecimal.valueOf(SentmentTree.getSentimentData()));
			
			// 합산된 감정 수치값 반환
			return NodeSentimentData;
		}
		return BigDecimal.valueOf(0);
	}
	
	/**
	 * <pre> Node에 감정 수치 삽입 </pre>
	 */
	protected void putSentimentData(TreeNode Tree) {
		// Node의 전체 감정 수치
		BigDecimal NodeSentimentData = new BigDecimal("0");
		// 감정 단어의 갯수
		int WordTime = 0;
		
		// 문장 구성요소인 WordData가 감정 단어인 경우
		if(SentimentWord.containsKey(Tree.getWordData()[0])) {
			// WordData의 감정 수치 삽입
			NodeSentimentData = NodeSentimentData.add(BigDecimal.valueOf(SentimentWord.get(Tree.getWordData()[0])));
			// 감정 단어 갯수 증가
			WordTime++;
			
			// WordData에 부정 표현이 사용된 경우
			if(Tree.getNegative()) {
				BigDecimal NegativeData = new BigDecimal(String.valueOf(SentimentWord.get(Tree.getWordData()[0])));
				
				// 사용된 부정 표현 파악
				String Word = Tree.getNegativeWord()[0];
				// 1~7 범위에서의 절대값을 알아내기 위하여 중간 값인 4로 뺄셈 연산 
				NegativeData = NegativeData.subtract(BigDecimal.valueOf(4));
				
				// 단형 부정, 장형 부정 확인 
				if (Word.equals("안")) {
					// 단형 부정일 경우, 감정 수치를 절대값과의 차이만큼 변경
					NegativeData = NegativeData.multiply(BigDecimal.valueOf(2));
				} else if (Word.equals("지않")) {
					// 장형 부정일 경우, 감정 수치를 절대값과의 차이의 반만큼 변경 
					NegativeData = NegativeData.multiply(BigDecimal.valueOf(1.5));
				}
				
				// WordData가 긍정 또는 부정 단어인지 파악 
				if(NegativeData.doubleValue() < 0) {
					// 부정 단어였을 경우, 감정 수치값을 더하여 긍정 단어로 변경
					NodeSentimentData = NodeSentimentData.add(NegativeData.abs());
				} else if (NegativeData.doubleValue() > 0) {
					// 긍정 단어였을 경우, 감정 수치값을 빼 부정 단어로 변경
					NodeSentimentData = NodeSentimentData.subtract(NegativeData);
				}
			}
		}

		// NounList가 존재할 경우
		if(!Tree.getNounList().isEmpty()) {
			// NounList의 각 명사들을 비교
			for (int i = 0; i < Tree.getNounList().size(); i++) {
				// NounList에 있는 명사가 감정단어인 경우
				if(SentimentWord.containsKey(Tree.getNounList().get(i))) {
					// 해당 명사의 감정 수치 합산
					NodeSentimentData = NodeSentimentData.add(BigDecimal.valueOf(SentimentWord.get(Tree.getNounList().get(i))));
					WordTime++;
				}
			}
		}
		
		// 위의 과정을 통해, 감정 수치가 파악된 경우
		if(NodeSentimentData.doubleValue() > 0) {
			// 전체 감정 수치의 합/감정 단어의 갯수 로 감정 단어의 평균치 계산
			NodeSentimentData = NodeSentimentData.divide(BigDecimal.valueOf(WordTime), 2, BigDecimal.ROUND_HALF_UP);
		}
		
		// Node의 SentimentData값을 계산된 전체 감정 수치 값으로 수정
		Tree.setSentimentData(NodeSentimentData.doubleValue());
	}
	
	/**
	 * <pre> 문단 감정 수치 계산 </pre>
	 */
	public double createSentmentTree (List<TreeNode> SyntaxTreeList) {
		List<TreeNode> SentmentTreeList = new ArrayList<TreeNode>();
		WordList Word = new WordList();
		// 문단 전체의 감정 수치
		BigDecimal SentimentData = new BigDecimal("0");
		int SentimentTime = 0;
		
		SentmentTreeList = SyntaxTreeList;
		// 감정 단어 불러옴
		SentimentWord = Word.makeWordList();
		
		// 문장 갯수만큼 반복
		for (int i = 0; i < SentmentTreeList.size(); i++) {
			// 문장 전체의 감정 수치
			BigDecimal TreeSentimentData = new BigDecimal("0");
			TreeSentimentData = this.treeTraversal(SentmentTreeList.get(i));
			// 문장의 감정 수치를 문단 전체의 감정 수치에 저장
			SentimentData = SentimentData.add(TreeSentimentData);
			// 문장에 감정 수치가 존재할 경우
			if(TreeSentimentData.doubleValue() > 0)
				// 감정 수치 문장 갯수 증가
				SentimentTime++;
			System.out.println(i+1 + "번째 문장의 감정 수치 : " + TreeSentimentData.doubleValue());
		}
		// 위의 과정을 통해, 감정 수치가 파악된 경우
		if(SentimentData.doubleValue() > 1) {
			// 전체 감정 수치의 합/감정 문장 갯수로 문장 감정 수치의 평균값 계산
			return SentimentData.divide(BigDecimal.valueOf(SentimentTime), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
		} else {
			return SentimentData.doubleValue();
		}
	}
}
