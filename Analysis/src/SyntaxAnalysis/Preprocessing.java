package SyntaxAnalysis;

import java.util.ArrayList;
import java.util.List;

import org.snu.ids.ha.ma.Sentence;

public class Preprocessing {
	// 전처리시 사용되는 형태소 품사 변수
	protected List<String> PreprocessWord = new ArrayList<String>();
	protected List<String> ECD = new ArrayList<String>();
	protected List<String> ECS = new ArrayList<String>();
	protected List<String> ETD = new ArrayList<String>();
	protected List<String> EPT = new ArrayList<String>();
	protected List<String> JKM = new ArrayList<String>();
	protected List<String> JKO = new ArrayList<String>();
	protected List<String> JKS = new ArrayList<String>();
	protected List<String> JX = new ArrayList<String>();
	protected List<String> VCP = new ArrayList<String>();
	
	public Preprocessing() {
		/* 전처리 과정에서 추출해 낼 형태소 품사를 정의함. */
		// 어근
		PreprocessWord.add("XR");
		// 연결 어미
		PreprocessWord.add("ECD");		PreprocessWord.add("ECS");
		// 종결 어미
		PreprocessWord.add("EFA");		PreprocessWord.add("EFI");		PreprocessWord.add("EFN");
		PreprocessWord.add("EFO");		PreprocessWord.add("EFQ");		PreprocessWord.add("EFR");
		// 전성 어미
		PreprocessWord.add("ETD");
		// 선어말 어미
		PreprocessWord.add("EPT");
		// 조사
		PreprocessWord.add("JKM");		PreprocessWord.add("JKO");		PreprocessWord.add("JKS");
		// 보조사
		PreprocessWord.add("JX");
		// 지정사
		PreprocessWord.add("VCP");
		// 부사
		PreprocessWord.add("MAG");
		// 형용사
		PreprocessWord.add("VA");		PreprocessWord.add("VXA");		PreprocessWord.add("XSA");
		// 명사
		PreprocessWord.add("NNG");		PreprocessWord.add("NNB");		PreprocessWord.add("NNM");
		PreprocessWord.add("NNP");		PreprocessWord.add("NP");		PreprocessWord.add("NR");
		PreprocessWord.add("XSN");
		// 동사
		PreprocessWord.add("VV");		PreprocessWord.add("VXV");		PreprocessWord.add("XSV");
		
		/* 사용할 품사의 특정 단어들을 정의함. */
		ECD.add("라고");		ECD.add("라");		ECD.add("지");
		ECS.add("어");
		ETD.add("ㄴ");
		EPT.add("었");		EPT.add("였");
		JKM.add("에");		JKM.add("에게");		JKM.add("께");		JKM.add("으로");		JKM.add("로");		
		JKM.add("과");		JKM.add("와");		JKM.add("에서");		JKM.add("에게");		JKM.add("보다");		
		JKO.add("을");		JKO.add("를");	
		JKS.add("이");		JKS.add("가");
		JX.add("은");		JX.add("는");		JX.add("라고");		JX.add("도");
		VCP.add("이");
	}
	
	/**
	 * <pre> 형태소 분석 결과에서 해당 품사가 추출해 낼 품사인지 확인함. </pre>
	 */
	public boolean isThisWord(String[] WordList) {
		// 해당 품사가 추출해 낼 품사인지 검사.
		if (0 <= PreprocessWord.indexOf(WordList[2])) {
			// 해당 형태소가 특정 단어만을 사용하는 품사인지 검사.
			switch (WordList[2]) {
			case "ECD":
				if (0 <= ECD.indexOf(WordList[1]))
					return true;
				else
					return false;
			case "ECS":
				if (0 <= ECS.indexOf(WordList[1]))
					return true;
				else
					return false;
			case "ETD":
				if (0 <= ETD.indexOf(WordList[1]))
					return true;
				else
					return false;
			case "EPT":
				if (0 <= EPT.indexOf(WordList[1]))
					return true;
				else
					return false;
			case "JKM":
				if (0 <= JKM.indexOf(WordList[1]))
					return true;
				else
					return false;
			case "JKO":
				if (0 <= JKO.indexOf(WordList[1]))
					return true;
				else
					return false;
			case "JKS":
				if (0 <= JKS.indexOf(WordList[1]))
					return true;
				else
					return false;
			case "JX":
				if (0 <= JX.indexOf(WordList[1]))
					return true;
				else
					return false;
			case "VCP":
				if (0 <= VCP.indexOf(WordList[1]))
					return true;
				else
					return false;
			default:
				return true;
			}
		}
		else
			return false;
			
	}


	/**
	 * <pre> 형태소 분석 결과에서 감정분석에 쓰이는 결과만을 추출함. </pre>
	 */
	public List<String[]> doPreprocess(Sentence SeparateSentence) {
		List<String[]> AllSentence = new ArrayList<String[]>();
		
		for (int i = 0; i < SeparateSentence.size(); i++) {
			String[] SplitPlus = SeparateSentence.get(i).toString().split("\\+"); //분석 결과를 형태소별로 추출하기 위해, '+'로 분석 결과를 나눔
			for(int j = 0; j < SplitPlus.length; j++) {
				String[] SplitSlash = SplitPlus[j].split("/"); // 형태소별로 추출한 분석 결과를 '/'로 나누어 형태소별 분석 결과를 각각 저장.
				if (isThisWord(SplitSlash)) { // 추출해낼 형태소 품사인지 확인.
					String[] FinalSentence = {SplitSlash[1], SplitSlash[2]}; // 분석 결과에서 형태소와 품사만을 저장. 
					AllSentence.add(FinalSentence); // 저장한 품사를 문장 전체를 담당하는 List에 추가함.
				}
			}
		}

		return AllSentence;
	}
	
}
