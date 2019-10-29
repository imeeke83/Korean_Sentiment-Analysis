package SyntaxAnalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SentenceStructure {
	protected int ParseTreeType = 0; // 문법에 따른 트리 생성을 위한 트리 타입 구분 변수
	protected int BeforeTreeType; // 오류 방지를 위한 이전 트리 타입 구분 저장 변수
	protected String ChangeMorpheme = ""; // 동사, 형용사, 명사 파생 접미사 처리를 위한 변수
	
	// 종결 어미, 동사, 형용사, 명사의 품사 List
	protected List<String> EFWord = new ArrayList<String>();
	protected List<String> Verb = new ArrayList<String>();
	protected List<String> Adjective = new ArrayList<String>();
	protected List<String> Noun = new ArrayList<String>();

	// 단일 단어 Component 생성을 위한 String 배열
	protected String[] NIWordList = {"은", "는", "이", "가"};		protected String[] NIMorphemeList = {"JX", "JX", "JKS", "JKS"};
	protected String[] NEWordList = {"에", "에게", "께"};			protected String[] NEMorphemeList = {"JKM", "JKM", "JKM"};
	protected String[] NRoWordList = {"으로", "로", "이", "가"};	protected String[] NRoMorphemeList = {"JKM", "JKM", "JKS", "JKS"};
	protected String[] NWaWordList = {"과", "와"};				protected String[] NWaMorphemeList = {"JKM", "JKM"};
	protected String[] NEsoWordList = {"에서", "에게"};			protected String[] NEsoMorphemeList = {"JKM", "JKM"};
	protected String[] NBodaWordList = {"보다"};					protected String[] NBodaMorphemeList = {"JKM"};
	protected String[] NRulWordList = {"을", "를"};				protected String[] NRulMorphemeList = {"JKO", "JKO"};
	
	// 다중 단어 Component의 처리를 위한 구분 변수
	protected boolean IsMultipleComponent = false;
	protected String MultipleComponentType = "";
	protected String[] MultipleComponentWord = new String[2];
	
	// 다중 단어 Component 생성을 위한 Stack
	protected Stack<String> NRulwehaeWordList1 = new Stack<String>();	protected Stack<String> NRulwehaeMorphemeList1 = new Stack<String>();
	protected Stack<String> NRulwehaeWordList2 = new Stack<String>();	protected Stack<String> NRulwehaeMorphemeList2 = new Stack<String>();
	protected Stack<String> NEuihaeWordList = new Stack<String>();		protected Stack<String> NEuihaeMorphemeList = new Stack<String>();
	protected Stack<String> NRagoWordList1 = new Stack<String>();		protected Stack<String> NRagoMorphemeList1 = new Stack<String>();
	protected Stack<String> NRagoWordList2 = new Stack<String>();		protected Stack<String> NRagoMorphemeList2 = new Stack<String>();
	protected Stack<String> NRagoWordList3 = new Stack<String>();		protected Stack<String> NRagoMorphemeList3 = new Stack<String>();
	protected Stack<String> NEdaehaeWordList = new Stack<String>();		protected Stack<String> NEdaehaeMorphemeList = new Stack<String>();

	// 부정표현 처리를 위한 구분 변수
	protected boolean IsMakingNegative = false;
	protected boolean ShortNegative = false;
	protected boolean LongNegative = false;
	
	// 부정표현 처리를 위한 변수
	protected String AnWordList = "안";									protected String AnMorphemeList = "MAG";
	protected String MotWordList = "못";									protected String MotMorphemeList = "MAG";
	protected Stack<String> JianWordList = new Stack<String>();		protected Stack<String> JianMorphemeList = new Stack<String>();
	protected Stack<String> JimotWordList = new Stack<String>();		protected Stack<String> JimotMorphemeList = new Stack<String>();
	
	public SentenceStructure() {
		EFWord.add("EFA");		EFWord.add("EFI");		EFWord.add("EFN");		EFWord.add("EFO");		EFWord.add("EFQ");		EFWord.add("EFR");
		Verb.add("VV");		Verb.add("VXV");
		Adjective.add("VA");		Adjective.add("VXA");
		Noun.add("NNG");		Noun.add("NNB");	Noun.add("NNM");		Noun.add("NNP");		Noun.add("NP");			Noun.add("NR");
		
		this.reStackComponent("NRulwehae");		this.reStackComponent("NEuihae");		this.reStackComponent("NRago");		this.reStackComponent("NEdaehae");
		this.reStackComponent("Jian");			this.reStackComponent("Jimot");
	}
	
	/**
	 * <pre> 품사변환 접미사 확인 </pre>
	 */
	public boolean checkChangeMorpheme(String Morpheme) {
		// 품사변환을 위해 ChangeMorpheme값 수정
		switch (Morpheme) {
		case "XSV":
			ChangeMorpheme = Morpheme;
			return true;
		case "XSA":
			ChangeMorpheme = Morpheme;
			return true;
		case "XSN":
			ChangeMorpheme = Morpheme;
			return true;
		default:
			return false;
		}
	}

	/**
	 * <pre> 단일 단어 Component 확인 </pre>
	 */
	public boolean checkComponent(String Component, String[] WordData) {
		switch(Component) {
		case "NI": // N이
			for(int i = 0; i < NIWordList.length; i++) {
				// 지정된 단어와 형태소 품사가 둘 다 일치할 경우
				if(WordData[0].equals(NIWordList[i]) && WordData[1].equals(NIMorphemeList[i]))
					return true; // true 값 반환
			}
			return false; // 일치하지 않는 경우 false 값 반환
		case "NE": // N에
			for(int i = 0; i < NEWordList.length; i++) {
				if(WordData[0].equals(NEWordList[i]) && WordData[1].equals(NEMorphemeList[i]))
					return true;
			}
			return false;
		case "NRo": // N로
			for(int i = 0; i < NRoWordList.length; i++) {
				if(WordData[0].equals(NRoWordList[i]) && WordData[1].equals(NRoMorphemeList[i]))
					return true;
			}
			return false;
		case "NWa": // N와
			for(int i = 0; i < NWaWordList.length; i++) {
				if(WordData[0].equals(NWaWordList[i]) && WordData[1].equals(NWaMorphemeList[i]))
					return true;
			}
			return false;
		case "NEso": // N에서
			for(int i = 0; i < NEsoWordList.length; i++) {
				if(WordData[0].equals(NEsoWordList[i]) && WordData[1].equals(NEsoMorphemeList[i]))
					return true;
			}
			return false;
		case "NBoda": // N보다
			for(int i = 0; i < NBodaWordList.length; i++) {
				if(WordData[0].equals(NBodaWordList[i]) && WordData[1].equals(NBodaMorphemeList[i]))
					return true;
			}
			return false;
		case "NRul": // N를
			for(int i = 0; i < NRulWordList.length; i++) {
				if(WordData[0].equals(NRulWordList[i]) && WordData[1].equals(NRulMorphemeList[i]))
					return true;
			}
			return false;
		default:
			// 인수 오류 검사를 위한 Print
			// System.out.println("Please Write Right Component Word." + Component);
			return false;
		}
	}
	
	/**
	 * <pre> Component Stack 초기화 </pre>
	 */
	protected void reStackComponent(String StackType) {
		// 다중 단어 Component 연산시 pop() 연산을 사용하므로, 다중 단어 Component 연산이 종료될 경우를 위한 초기화 메소드
		switch(StackType) {
		case "NRulwehae":
			NRulwehaeWordList1.clear();			NRulwehaeWordList2.clear();
			NRulwehaeMorphemeList1.clear();			NRulwehaeMorphemeList2.clear();
			NRulwehaeWordList1.push("을");			NRulwehaeWordList1.push("위하");			NRulwehaeWordList1.push("어");
			NRulwehaeWordList2.push("를");			NRulwehaeWordList2.push("위하");			NRulwehaeWordList2.push("어");
			NRulwehaeMorphemeList1.push("JKO");		NRulwehaeMorphemeList1.push("VV");		NRulwehaeMorphemeList1.push("ECS");
			NRulwehaeMorphemeList2.push("JKO");		NRulwehaeMorphemeList2.push("VV");		NRulwehaeMorphemeList2.push("ECS");
			break;
		case "NEuihae":
			NEuihaeWordList.clear();
			NEuihaeMorphemeList.clear();
			NEuihaeWordList.push("에");				NEuihaeWordList.push("의하");				NEuihaeWordList.push("어");
			NEuihaeMorphemeList.push("JKM");		NEuihaeMorphemeList.push("VV");			NEuihaeMorphemeList.push("ECS");
			break;
		case "NRago":
			NRagoWordList1.clear();			NRagoWordList2.clear();			NRagoWordList3.clear();
			NRagoMorphemeList1.clear();		NRagoMorphemeList2.clear();		NRagoMorphemeList3.clear();
			NRagoWordList1.push("라고");
			NRagoWordList2.push("이");				NRagoWordList2.push("라고");
			NRagoWordList3.push("이");				NRagoWordList3.push("라");
			NRagoMorphemeList1.push("JX");
			NRagoMorphemeList2.push("VCP");			NRagoMorphemeList2.push("ECD");
			NRagoMorphemeList3.push("VCP");			NRagoMorphemeList3.push("ECD");
			break;
		case "NEdaehae":
			NEdaehaeWordList.clear();
			NEdaehaeMorphemeList.clear();
			NEdaehaeWordList.push("에");				NEdaehaeWordList.push("대");				NEdaehaeWordList.push("하");				NEdaehaeWordList.push("어");
			NEdaehaeMorphemeList.push("JKM");		NEdaehaeMorphemeList.push("NNG");		NEdaehaeMorphemeList.push("XSV");		NEdaehaeMorphemeList.push("ECS");
			break;
		case "Jian":
			JianWordList.clear();
			JianMorphemeList.clear();
			JianWordList.push("지");					JianWordList.push("않");
			JianMorphemeList.push("ECD");			JianMorphemeList.push("VXV");
			break;
		case "Jimot":
			JimotWordList.clear();
			JimotMorphemeList.clear();
			JimotWordList.push("지");				JimotWordList.push("못");
			JimotMorphemeList.push("ECD");			JimotMorphemeList.push("MAG");
			break;
		}
	}
	
	/**
	 * <pre> 다중 단어 Component 작업 수행 </pre>
	 */
	protected void doMultipleComponent(Stack<String> WordList, Stack<String> MorphemeList) {
		// pop() 연산을 통해  다중 단어 Component의 일부분인 현재의 형태소 단어와 품사를 저장함
		MultipleComponentWord[0] = WordList.pop();				MultipleComponentWord[1] = MorphemeList.pop();
		// 다중 단어 Component 연산 모드로 전환
		IsMultipleComponent = true;
		// Stack에 저장되어있는 다중 단어 Component를 사용하여 완전한 다중 단어 Component 생성 완료시,
		// 다중 단어 Component 연산 모드를 종료하고 환경변수 및 스택 초기화
		if(WordList.empty() && MorphemeList.empty()) {
			IsMultipleComponent = false;
			this.reStackComponent(MultipleComponentType);
			MultipleComponentType = "";
		}
	}
	
	/**
	 * <pre> 다중 단어 Component 확인 </pre>
	 */
	public boolean checkMultipleComponent(String Component, String[] WordData) {
		// 다중 단어 Component 구분 변수가 비어있을 경우 (= 다중 단어 연산 시작여부를 알아볼 때)
		if (MultipleComponentType.isEmpty()) {
			// 인자값을 구분 변수에 저장함.
			MultipleComponentType = Component;
		}
		switch(MultipleComponentType) {
		case "NRulwehae": // N를 위해
			// 지정된 단어와 형태소 품사가 둘 다 일치할 경우
			if(WordData[0].equals(NRulwehaeWordList1.peek()) && WordData[1].equals(NRulwehaeMorphemeList1.peek())) {
				// 같은 범주의 다중 단어 Component인 서로 다른 Stack의 peek 값이 같을 경우  
				if(NRulwehaeWordList1.peek().equals(NRulwehaeWordList2.peek()) && NRulwehaeMorphemeList1.peek().equals(NRulwehaeMorphemeList2.peek())) {
					// 값이 같은 Stack의 값을 제거
					NRulwehaeWordList2.pop();
					NRulwehaeMorphemeList2.pop();
				}
				// 다중 단어 Component 작업 수행
				this.doMultipleComponent(NRulwehaeWordList1, NRulwehaeMorphemeList1);
				// 작업을 마친 뒤 true 값 반환
				return true;
			}
			if(WordData[0].equals(NRulwehaeWordList2.peek()) && WordData[1].equals(NRulwehaeMorphemeList2.peek())) {
				this.doMultipleComponent(NRulwehaeWordList2, NRulwehaeMorphemeList2);
				return true;
			}
			// 지정된 단어와 형태소 품사가 둘 다 일치하지 않는 경우, 구분 변수를 초기화하고 false 값 반환
			MultipleComponentType = "";
			return false;
		case "NEuihae": // N에 의해
			if(WordData[0].equals(NEuihaeWordList.peek()) && WordData[1].equals(NEuihaeMorphemeList.peek())) {
				this.doMultipleComponent(NEuihaeWordList, NEuihaeMorphemeList);
				return true;
			}
			MultipleComponentType = "";
			return false;
		case "NRago": // N라고
			if(WordData[0].equals(NRagoWordList1.peek()) && WordData[1].equals(NRagoMorphemeList1.peek())) {
				this.doMultipleComponent(NRagoWordList1, NRagoMorphemeList1);
				return true;
			}
			if(WordData[0].equals(NRagoWordList2.peek()) && WordData[1].equals(NRagoMorphemeList2.peek())) {
				this.doMultipleComponent(NRagoWordList2, NRagoMorphemeList2);
				return true;
			}
			if(WordData[0].equals(NRagoWordList3.peek()) && WordData[1].equals(NRagoMorphemeList3.peek())) {
				this.doMultipleComponent(NRagoWordList3, NRagoMorphemeList3);
				return true;
			}
			MultipleComponentType = "";
			return false;
		case "NEdaehae": // N에 대해
			if(WordData[0].equals(NEdaehaeWordList.peek()) && WordData[1].equals(NEdaehaeMorphemeList.peek())) {
				this.doMultipleComponent(NEdaehaeWordList, NEdaehaeMorphemeList);
				return true;
			}
			MultipleComponentType = "";
			return false;
		default:
			// 인수 오류 검사를 위한 Print
			// System.out.println("Please Write Right Multiple Component Word." + Component);
			MultipleComponentType = "";
			return false;
		}
	}
	
	/**
	 * <pre> 품사 변환 작업 수행 </pre>
	 */
	public TreeNode changeMorphemeComponent(TreeNode ChangeNode) {
		switch (ChangeMorpheme) {
		case "XSV":
			ChangeNode.WordData[1] = "VV";
			break;
		case "XSA":
			ChangeNode.WordData[1] = "VA";
			break;
		case "XSN":
			ChangeNode.WordData[1] = "NNG";
			break;
		}
		// 파생 접미사 환경 변수 초기화
		ChangeMorpheme = "";
		return ChangeNode;
	}
	
	/**
	 * <pre> 부정 확인 </pre>
	 */
	public boolean checkNegative(TreeNode MorphemeNode, String[] NegativeWordInfo) {
		// 장형 부정 ~지 않 분기
		if (MorphemeNode.WordData[0].equals(JianWordList.peek()) && MorphemeNode.WordData[1].equals(JianMorphemeList.peek())) {
			IsMakingNegative = true;
			NegativeWordInfo[0] = JianWordList.pop();
			NegativeWordInfo[1] = JianMorphemeList.pop();
			return true;
		// 못 부정 분기
		} else if(MorphemeNode.WordData[0].equals(JimotWordList.peek()) && MorphemeNode.WordData[1].equals(JimotMorphemeList.peek())) {
			IsMakingNegative = true;
			NegativeWordInfo[0] = JimotWordList.pop();
			NegativeWordInfo[1] = JimotMorphemeList.pop();
			return true;
		// 단형 부정 안 ~ 확정
		} else if(MorphemeNode.WordData[0].equals(AnWordList) && MorphemeNode.WordData[1].equals(AnMorphemeList)) {
			NegativeWordInfo[0] = AnWordList;
			NegativeWordInfo[1] = AnMorphemeList;
			ShortNegative = true;
			return true;
		}
		return false;
	}
	
	/**
	 * <pre> 부정 연산 </pre>
	 */
	protected void doNegative(TreeNode MorphemeNode, String[] NegativeWordInfo) {	
		// 장형 부정 ~지 않- 진행
		if (JianWordList.size() < JimotWordList.size()) {
			// 장형 부정 ~지 않- 확정
			if (MorphemeNode.WordData[0].equals(JianWordList.peek()) && MorphemeNode.WordData[1].equals(JianMorphemeList.peek())) {
				IsMakingNegative = false;
				NegativeWordInfo[0] = JianWordList.pop() + NegativeWordInfo[0];
				NegativeWordInfo[1] = JianMorphemeList.pop() + NegativeWordInfo[1];
				LongNegative = true;
				this.reStackComponent("Jian");
			}
		// 못 부정형 진행
		} else if (JianWordList.size() > JimotWordList.size()){
			// 장형 부정 ~지 못- 확정
			if(MorphemeNode.WordData[0].equals(JimotWordList.peek()) && MorphemeNode.WordData[1].equals(JimotMorphemeList.peek())) {
				IsMakingNegative = false;
				NegativeWordInfo[0] = JimotWordList.pop() + NegativeWordInfo[0];
				NegativeWordInfo[1] = JimotMorphemeList.pop() + NegativeWordInfo[1];
				LongNegative = true;
				this.reStackComponent("Jimot");
			// 단형 부정 못 ~ 확정
			} else if(NegativeWordInfo[0].equals(MotWordList) && NegativeWordInfo[1].equals(MotMorphemeList)){
				IsMakingNegative = false;
				ShortNegative = true;
			}
		} else {
			System.out.println("Error !!");
			IsMakingNegative = false;
			this.reStackComponent("Jian");			this.reStackComponent("Jimot");
			NegativeWordInfo = null;
		}
	}
	
	/**
	 * <pre> 동사 중심 Parse Tree 생성 </pre>
	 */
	public TreeNode createVerbTree(TreeNode RootNode, TreeNode MorphemeNode, List<String[]> Sentence, int index) {
		// Parse Tree 생성을 위한 PointNode
		TreeNode PointNode = new TreeNode();
		// RootNode 생성
		RootNode = MorphemeNode;
		// Tree생성을 위해 PointNode의 초기값을 RootNode로 설정
		PointNode = RootNode;
		// 부정 표현 저장을 위한 String 배열
		String[] NegativeWordInfo = new String[2];
		
		for(int i = index-1; i >= 0; i--) {
			
			// 단어별로 임시 Node를 생성하여 연산을 수행함.
			TreeNode TempNode = new TreeNode(Sentence.get(i));
			
			// 임시 Node가 명사가 아닌 Component를 구성하는 형태소인 경우
			if(!Noun.contains(TempNode.WordData[1])) {
				// Component에 부정 적용
				if(ShortNegative) {
					PointNode.Negative = true;
					PointNode.NegativeWord = NegativeWordInfo;
					ShortNegative = false;
				}
				if(LongNegative) {
					TempNode.Negative = true;
					TempNode.NegativeWord = NegativeWordInfo;
					LongNegative = false;
				}
			}

			// 부정 연산
			if (IsMakingNegative) {
				this.doNegative(TempNode, NegativeWordInfo);
				continue;
			}
			
			// 부정 확인
			if(this.checkNegative(TempNode, NegativeWordInfo))
				continue;
			
			// 파생 접미사로 인한 품사 변경 처리를 위해 파생 접미사 검색
			if (this.checkChangeMorpheme(TempNode.WordData[1]))
				continue;
			
			// 파생 접미사가 검색될 경우, 앞 형태소의 품사를 해당 파생 접미사에 맞게 변경함
			if (!ChangeMorpheme.isEmpty())
				TempNode = changeMorphemeComponent(TempNode);
			
			// 다중 단어 Component 여부 확인
			if(IsMultipleComponent) {
				if(this.checkMultipleComponent(MultipleComponentType, TempNode.WordData)) {
					// 다중 단어 Component 완성을 위한 단어 및 형태소 추가
					PointNode.WordData[0] = MultipleComponentWord[0] + PointNode.WordData[0];
					PointNode.WordData[1] = MultipleComponentWord[1] + PointNode.WordData[1];
					continue;
				}
				// 다중 단어 Component 모드에 잘못 진입하였을 경우를 위한 환경 변수 초기화
				MultipleComponentType = "";
				IsMultipleComponent = false;
				ParseTreeType = BeforeTreeType;
			}
			
			if(ParseTreeType == 0) { // Parse Tree Level Down (Root -1)
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 1번 유형 확정 또는 2-3번 유형 분기
					// Parse Tree의 특정 유형에 일치할경우, 새로운 Component Node를 자식 Node로 생성
					RootNode.addComponent(TempNode);
					// PointNode의 위치 변경
					PointNode = RootNode.Component;
					// 트리 타입 선언
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 1;					continue;
				} else if(this.checkComponent("NE", TempNode.WordData)) { // 동사 중심 4-5번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 2;					continue;
				}  else if(this.checkComponent("NRo", TempNode.WordData)) { // 동사 중심 6-7번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 3;					continue;
				}  else if(this.checkComponent("NWa", TempNode.WordData)) { // 동사 중심 8번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 4;					continue;
				}  else if(this.checkComponent("NEso", TempNode.WordData)) { // 동사 중심 9번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 5;					continue;
				}  else if(this.checkMultipleComponent("NRulwehae", TempNode.WordData)) { // 동사 중심 10번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 6;					continue;
				}  else if(this.checkMultipleComponent("NEuihae", TempNode.WordData)) { // 동사 중심 11번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 7;					continue;
				}  else if(0 <= Adjective.indexOf(TempNode.WordData[1])) { // 동사 중심 12-13번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 8;					continue;
				}  else if(this.checkComponent("NRul", TempNode.WordData)) { // 동사 중심 14-19번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 9;					continue;
				}  else if(this.checkMultipleComponent("NRago", TempNode.WordData)) { // 동사 중심 20번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 10;					continue;
				}
			}
			
			switch(ParseTreeType) { // Parse Tree Level Down (Root -2 ~)
			case 1:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 2번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 12;
				} else if(this.checkComponent("NWa", TempNode.WordData)) { // 동사 중심 3번 유형 분기
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 13;
				}				break;
			case 13:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 3번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 133;
				}				break;
			case 2:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 4번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 24;
				} else if(this.checkComponent("NWa", TempNode.WordData)) { // 동사 중심 5번 유형 분기
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 25;
				}				break;
			case 25:
				if(this.checkComponent("NRul", TempNode.WordData)) { // 동사 중심 5번 유형 분기
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 255;
				}				break;
			case 255:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 5번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 2555;
				}				break;
			case 3:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 6번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 36;
				} else if(this.checkComponent("NRul", TempNode.WordData)) { // 동사 중심 7번 유형 분기
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 37;
				}				break;
			case 37:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 7번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 377;
				}				break;
			case 4:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 8번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 48;
				}				break;
			case 5:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 9번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 59;
				}				break;
			case 6:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 10번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 60;
				}				break;
			case 7:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 11번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 71;
				}				break;
			case 8:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 12번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 82;
				} else if(this.checkComponent("NRul", TempNode.WordData)) { // 동사 중심 13번 유형 분기
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 83;
				}				break;
			case 83:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 13번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 833;
				}				break;
			case 9:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 14번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 94;
				} else if(this.checkComponent("NE", TempNode.WordData)) { // 동사 중심 15번 유형 분기
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 95;
				} else if(this.checkComponent("NWa", TempNode.WordData)) { // 동사 중심 16-17번 유형 분기
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 96;
				} else if(this.checkComponent("NEso", TempNode.WordData)) { // 동사 중심 18번 유형 분기
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 98;
				} else if(this.checkMultipleComponent("NEdaehae", TempNode.WordData)) { // 동사 중심 19번 유형 분기
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 99;
				}				break;
			case 95:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 15번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 955;
				}				break;
			case 96:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 16번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 966;
				} else if(this.checkComponent("NE", TempNode.WordData)) { // 동사 중심 17번 유형 분기
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 967;
				}				break;
			case 967:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 17번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 9677;
				}				break;
			case 98:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 18번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 988;
				}				break;
			case 99:
				if(this.checkComponent("NWa", TempNode.WordData)) { // 동사 중심 19번 유형 분기
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 999;
				}				break;
			case 999:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 19번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 9999;
				}				break;
			case 10:
				if(this.checkComponent("NRul", TempNode.WordData)) { // 동사 중심 20번 유형 분기
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 100;
				}				break;
			case 100:
				if(this.checkComponent("NI", TempNode.WordData)) { // 동사 중심 20번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 1000;
				}				break;
			}
			
			// Component 요소가 아니면서 품사가 명사일 경우, Component Node 내부 명사 List에 해당 요소 추가
			if (0 <= Noun.indexOf(TempNode.WordData[1])) {
				PointNode.NounList.add(TempNode.WordData[0]);
			}
		}
		// Parse Tree 생성이 안되는 문장 검출
		switch(ParseTreeType) {
		case 1:				break;
		case 12:			ParseTreeType = 2;			break;
		case 133:			ParseTreeType = 3;			break;
		case 24:			ParseTreeType = 4;			break;
		case 2555:			ParseTreeType = 5;			break;
		case 36:			ParseTreeType = 6;			break;
		case 377:			ParseTreeType = 7;			break;
		case 48:			ParseTreeType = 8;			break;
		case 59:			ParseTreeType = 9;			break;
		case 60:			ParseTreeType = 10;			break;
		case 71:			ParseTreeType = 11;			break;
		case 82:			ParseTreeType = 12;			break;
		case 833:			ParseTreeType = 13;			break;
		case 94:			ParseTreeType = 14;			break;
		case 955:			ParseTreeType = 15;			break;
		case 966:			ParseTreeType = 16;			break;
		case 9677:			ParseTreeType = 17;			break;
		case 988:			ParseTreeType = 18;			break;
		case 9999:			ParseTreeType = 19;			break;
		case 1000:			ParseTreeType = 20;			break;
		default:
			System.out.println("ParseTreeType Not Complete :" + ParseTreeType + " / Sentence Type : Verb");
			return RootNode;
		}
		System.out.println("ParseTreeType Complete :" + ParseTreeType + " / Sentence Type : Verb");
		// 문장의 Parse Tree 생성이 완료되었으므로 트리 타입 초기화
		ParseTreeType = 0;
		// 생성한 Parse Tree 반환
		return RootNode;
	}
		
	/**
	 * <pre> 형용사 중심 Parse Tree 생성 </pre>
	 */
	public TreeNode createAdjectiveTree(TreeNode RootNode, TreeNode MorphemeNode, List<String[]> Sentence, int index) {
		TreeNode PointNode = new TreeNode();
		RootNode = MorphemeNode;
		PointNode = RootNode;
		String[] NegativeWordInfo = new String[2];

		for(int i = index-1; i >= 0; i--) {
			TreeNode TempNode = new TreeNode(Sentence.get(i));
			
			if(!Noun.contains(TempNode.WordData[1])) {
				if(ShortNegative) {
					PointNode.Negative = true;
					PointNode.NegativeWord = NegativeWordInfo;
					ShortNegative = false;
				}
				if(LongNegative) {
					TempNode.Negative = true;
					TempNode.NegativeWord = NegativeWordInfo;
					LongNegative = false;
				}
			}

			if (IsMakingNegative) {
				this.doNegative(TempNode, NegativeWordInfo);
				continue;
			}
			
			if(this.checkNegative(TempNode, NegativeWordInfo))
				continue;
			
			if (this.checkChangeMorpheme(TempNode.WordData[1]))
				continue;
			
			if (!ChangeMorpheme.isEmpty())
				TempNode = changeMorphemeComponent(TempNode);
			
			if(ParseTreeType == 0) { // Parse Tree Level Down (Root -1)
				if(this.checkComponent("NI", TempNode.WordData)) { // 형용사 중심 1번 유형 확정 또는 2-4번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					ParseTreeType = 1;					continue;
				} else if(this.checkComponent("NE", TempNode.WordData)) { // 형용사 중심 5번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					ParseTreeType = 2;					continue;
				}  else if(this.checkComponent("NWa", TempNode.WordData)) { // 형용사 중심 6번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					ParseTreeType = 3;					continue;
				}  else if(this.checkComponent("NBoda", TempNode.WordData)) { // 형용사 중심 7번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					ParseTreeType = 4;					continue;
				}  else if(this.checkComponent("NRo", TempNode.WordData)) { // 형용사 중심 8번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					ParseTreeType = 5;					continue;
				}
			}
			
			switch(ParseTreeType) { // Parse Tree Level Down (Root -2 ~)
			case 1:
				if(this.checkComponent("NI", TempNode.WordData)) { // 형용사 중심 2번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					ParseTreeType = 12;
				} else if(this.checkComponent("NRo", TempNode.WordData)) { // 형용사 중심 3번 유형 분기
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					ParseTreeType = 13;
				} else if(this.checkComponent("NWa", TempNode.WordData)) { // 형용사 중심 4번 유형 분기
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					ParseTreeType = 14;
				}				break;
			case 13:
				if(this.checkComponent("NI", TempNode.WordData)) { // 형용사 중심 3번 유형 확정
						PointNode.addComponent(TempNode);
						PointNode = PointNode.Component;
						ParseTreeType = 133;
				}				break;
			case 14:
				if(this.checkComponent("NI", TempNode.WordData)) { // 형용사 중심 4번 유형 확정
						PointNode.addComponent(TempNode);
						PointNode = PointNode.Component;
						ParseTreeType = 144;
				}				break;
			case 2:
				if(this.checkComponent("NI", TempNode.WordData)) { // 형용사 중심 5번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					ParseTreeType = 21;
				}				break;
			case 3:
				if(this.checkComponent("NI", TempNode.WordData)) { // 형용사 중심 6번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					ParseTreeType = 31;
				}				break;
			case 4:
				if(this.checkComponent("NI", TempNode.WordData)) { // 형용사 중심 7번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					ParseTreeType = 41;
				}				break;
			case 5:
				if(this.checkComponent("NI", TempNode.WordData)) { // 형용사 중심 8번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					ParseTreeType = 51;
				}				break;
			}
			
			if (0 <= Noun.indexOf(TempNode.WordData[1])) {
				PointNode.NounList.add(TempNode.WordData[0]);
			}
		}
		switch(ParseTreeType) {
		case 1:				break;
		case 12:			ParseTreeType = 2;			break;
		case 133:			ParseTreeType = 3;			break;
		case 144:			ParseTreeType = 4;			break;
		case 21:			ParseTreeType = 5;			break;
		case 31:			ParseTreeType = 6;			break;
		case 41:			ParseTreeType = 7;			break;
		case 51:			ParseTreeType = 8;			break;
		default:
			System.out.println("ParseTreeType Not Complete :" + ParseTreeType + " / Sentence Type : Adjective");
			return RootNode;
		}
		System.out.println("ParseTreeType Complete :" + ParseTreeType + " / Sentence Type : Adjective");
		ParseTreeType = 0;
		return RootNode;
	}
	
	/**
	 * <pre> 명사 중심 Parse Tree 생성 </pre>
	 */
	public TreeNode createNounTree(TreeNode RootNode, TreeNode MorphemeNode, TreeNode EFNode, List<String[]> Sentence, int index) {
		TreeNode PointNode = new TreeNode();
		// N이다 Component 구성을 위해 종결 어미를 RootNode로 지정.
		RootNode = EFNode;
		PointNode = RootNode;
		// 종결 어미 앞에 있는 명사를 RootNode Component의 명사 List에 추가
		RootNode.addNoun(MorphemeNode.WordData[0]);
		String[] NegativeWordInfo = new String[2];
		
		for(int i = index-1; i >= 0; i--) {
			TreeNode TempNode = new TreeNode(Sentence.get(i));
			
			if(!Noun.contains(TempNode.WordData[1])) {
				if(ShortNegative) {
					PointNode.Negative = true;
					PointNode.NegativeWord = NegativeWordInfo;
					ShortNegative = false;
				}
				if(LongNegative) {
					TempNode.Negative = true;
					TempNode.NegativeWord = NegativeWordInfo;
					LongNegative = false;
				}
			}

			if (IsMakingNegative) {
				this.doNegative(TempNode, NegativeWordInfo);
				continue;
			}
			
			if(this.checkNegative(TempNode, NegativeWordInfo))
				continue;
			
			if (this.checkChangeMorpheme(TempNode.WordData[1]))
				continue;
			
			if (!ChangeMorpheme.isEmpty())
				TempNode = changeMorphemeComponent(TempNode);
			
			if(IsMultipleComponent) {
				if(this.checkMultipleComponent(MultipleComponentType, TempNode.WordData)) {
					PointNode.WordData[0] = MultipleComponentWord[0] + PointNode.WordData[0];
					PointNode.WordData[1] = MultipleComponentWord[1] + PointNode.WordData[1];
					continue;
				}
				MultipleComponentType = "";
				IsMultipleComponent = false;
				ParseTreeType = BeforeTreeType;
			}
			
			if(ParseTreeType == 0) { // Parse Tree Level Down (Root -1)
				if(this.checkComponent("NI", TempNode.WordData)) { // 명사 중심 1번 유형 확정 또는 2번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 1;					continue;
				} else if(this.checkComponent("NE", TempNode.WordData)) { // 명사 중심 3번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 2;					continue;
				}  else if(this.checkComponent("NWa", TempNode.WordData)) { // 명사 중심 4번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 3;					continue;
				}  else if(this.checkComponent("NBoda", TempNode.WordData)) { // 명사 중심 5번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 4;					continue;
				}  else if(this.checkMultipleComponent("NEdaehae", TempNode.WordData)) { // 명사 중심 6번 유형 분기
					RootNode.addComponent(TempNode);
					PointNode = RootNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 5;					continue;
				}
			}
			
			switch(ParseTreeType) { // Parse Tree Level Down (Root -2 ~)
			case 1:
				if(this.checkComponent("NI", TempNode.WordData)) { // 명사 중심 2번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 12;
				}				break;
			case 2:
				if(this.checkComponent("NI", TempNode.WordData)) { // 명사 중심 3번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 23;
				}				break;
			case 3:
				if(this.checkComponent("NI", TempNode.WordData)) { // 명사 중심 4번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 34;
				}				break;
			case 4:
				if(this.checkComponent("NI", TempNode.WordData)) { // 명사 중심 5번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 45;
				}				break;
			case 5:
				if(this.checkComponent("NI", TempNode.WordData)) { // 명사 중심 6번 유형 확정
					PointNode.addComponent(TempNode);
					PointNode = PointNode.Component;
					BeforeTreeType = ParseTreeType;
					ParseTreeType = 56;
				}				break;
			}
			
			if (0 <= Noun.indexOf(TempNode.WordData[1])) {
				PointNode.NounList.add(TempNode.WordData[0]);
			}
		}
		switch(ParseTreeType) {
		case 1:				break;
		case 12:			ParseTreeType = 2;			break;
		case 23:			ParseTreeType = 3;			break;
		case 34:			ParseTreeType = 4;			break;
		case 45:			ParseTreeType = 5;			break;
		case 56:			ParseTreeType = 6;			break;
		default:
			System.out.println("ParseTreeType Not Complete :" + ParseTreeType + " / Sentence Type : Noun");
			return RootNode;
		}
		System.out.println("ParseTreeType Complete :" + ParseTreeType + " / Sentence Type : Noun");
		ParseTreeType = 0;
		return RootNode;
	}
	
	/**
	 * <pre> 완전한 Parse Tree 생성 </pre>
	 */
	public TreeNode createParseTree(List<String[]> Sentence) {
		TreeNode RootNode = new TreeNode();
		TreeNode EFNode = new TreeNode();
		String[] NegativeWordInfo = new String[2];
		
		// 최초 문장 유형 결정을 위한 반복문
		for(int i = Sentence.size()-1; i > 0; i--) {
			TreeNode ChangeMorphemeNode = new TreeNode(Sentence.get(i));

			// 부정 연산
			if (IsMakingNegative) {
				this.doNegative(ChangeMorphemeNode, NegativeWordInfo);
				continue;
			}
			
			// 부정 확인
			if(this.checkNegative(ChangeMorphemeNode, NegativeWordInfo))
				continue;
			
			// 품사 변환 형태소 확인
			if (this.checkChangeMorpheme(ChangeMorphemeNode.WordData[1]))
				continue;
			
			// 품사 변환 작업 수행
			if (!ChangeMorpheme.isEmpty())
				ChangeMorphemeNode = changeMorphemeComponent(ChangeMorphemeNode);
			
			
			// N이다 처리를 위한 종결 어미 검색
			if (0 <= EFWord.indexOf(ChangeMorphemeNode.WordData[1])) {
				EFNode.setWordData(ChangeMorphemeNode.WordData);
				continue;
			}
			
			
			// 동사, 형용사, 명사 중심 문장 구분
			if(0 <= Verb.indexOf(ChangeMorphemeNode.WordData[1])) {
				// 문장의 마지막에 부정이 존재할 경우, RootNode에 부정 적용
				if(LongNegative) {
					ChangeMorphemeNode.Negative = true;
					ChangeMorphemeNode.NegativeWord = NegativeWordInfo;
					LongNegative = false;
				}
				RootNode = createVerbTree(RootNode, ChangeMorphemeNode, Sentence, i);
				break;
			} else if(0 <= Adjective.indexOf(ChangeMorphemeNode.WordData[1])) {
				if(LongNegative) {
					ChangeMorphemeNode.Negative = true;
					ChangeMorphemeNode.NegativeWord = NegativeWordInfo;
					LongNegative = false;
				}
				RootNode = createAdjectiveTree(RootNode, ChangeMorphemeNode, Sentence, i);
				break;
			} else if(0 <= Noun.indexOf(ChangeMorphemeNode.WordData[1])) {
				RootNode = createNounTree(RootNode, ChangeMorphemeNode, EFNode, Sentence, i);
				break;
			}
		}
		// 최종 완성된 Parse Tree 반환
		return RootNode;
	}
}
