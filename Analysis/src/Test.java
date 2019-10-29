import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import org.snu.ids.ha.index.KeywordExtractor;
import org.snu.ids.ha.index.KeywordList;
import org.snu.ids.ha.ma.MExpression;
import org.snu.ids.ha.ma.MorphemeAnalyzer;
import org.snu.ids.ha.ma.Sentence;
import org.snu.ids.ha.ma.Token;
import org.snu.ids.ha.ma.Tokenizer;
import org.snu.ids.ha.sp.ParseTree;
import org.snu.ids.ha.sp.ParseTreeEdge;
import org.snu.ids.ha.sp.ParseTreeNode;
import org.snu.ids.ha.sp.Parser;
import org.snu.ids.ha.util.Timer;


/**
 * <pre>
 * 
 * </pre>
 * @author 	therocks
 * @since	2008. 04. 23
 */
public class Test
{

	/**
	 * <pre>
	 * 
	 * </pre>
	 * @author	therocks
	 * @since	2008. 04. 23
	 * @param args
	 */
	public static void main(String[] args)
	{
		//taTest();
		//maTest("sample.txt", "sampleResult.txt");
		maTest("안드로보이가 사라지죠");
		//keTest();
		//jarTest();
		//parseTest();
	}


	public static void jarTest()
	{
		BufferedReader br = null;

		InputStream is = null;
		try {
			is = Test.class.getResourceAsStream("dic/noun.dic");
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = null;
			while( (line = br.readLine()) != null ) {
				System.out.println(line);
			}
			br.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void taTest()
	{
		String str = "매목 매과의 조류이고,몸길이가 33∼48cm이다. 배면은 청회색이고 가슴에는 굵은 세로무늬가 있으며,뺨에는 수염과 같은 흑색 반문이 있고,부리에는 치상돌기(齒狀突起)가 있으며 콧구멍은 둥글고 속에 돌기가 있으며 그것으로 콧구멍에서의 풍압(風壓)을 조절하고,눈은 다른 새보다 크며 눈 밑에 있는 검은 빛깔의 띠는 광선을 흡수하여 눈이 부시는 것을 방지한다. 대개 해안이나 섬의 절벽 바위의 선반과 같은 곳에 둥지를 마련하지만 외국에서는 도시의 고층 건물에 둥지를 틀기도 한다. 오리류,도요,물떼새류,비둘기류 등을 포식하며,최대한의 속력으로 급강하하여 먹이를 낚아챈 후 큰 새는 땅 위에서 뜯어 먹으며 시속 60km이나 급강하시에는 200km 이상의 속력을 내고,참매,붉은배새매,새매,황조롱이와 함께 천연기념물 제 323호로 지정하여 보호하고 있으며,유라시아 대륙,북아메리카,아프리카,오스트레일리아 등 전세계에 널리 분포하고,한국에서는 해안 절벽에서 번식하는 드문 텃새이다. 한국에서는 오래 전부터 꿩사냥에 이용해 왔음";
		//		str = "동주형 안녕하세요~ 잘 지내시는지요? ㅎㅎ 전 미쿡에 잠시 와서 고생좀 하고 있습니다 -_-; 우리나라 사이트 갖고 이것저것 해 보려고 하는데, 역시 한글 처리가 문제더군요. 러 문서를 받아서 자주 나오는 term을 추출해서 모아보려고 합니다. 뭐.. 네이버 실시간 인기 검색어 비슷한 것이 되겠지요? 일단 이렇게 term을 추출하고서 그 원본 document와 갖고 놀고 싶은데... 관련 라이브러리나 알고리즘을 참고할 것이 있을까요? 순수하게 추출 알고리즘만 쓸 수 있을 것이라고 생각은 안하지만 (사전을 이용해야 하겠죠..)  추출을 하지 않으면 무지막지한 작업이 될테니 -_-;;확 적당히 n-gram 잘라서 모두 보관하고 있다가 많이 나오는 걸 처리하기에는.. 좀 무지막지한듯 해서 한번 조언을 구해봅니다. Chulki Lee";
		//		str = "1,000,000 1.000.000 0.123 100,200,300 0,001";
		str = "(2) 반의성 검증법 (Antonym Test)";
		List<Token> tl = Tokenizer.tokenize(str);
		for( int i = 0, size = tl.size(); i < size; i++ ) {
			System.out.println(tl.get(i));
		}
	}


	public static void maTest(String sampleFileName, String resultFileName)
	{
		BufferedReader br = null;
		PrintWriter pw = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(sampleFileName), "UTF-8"));
			pw = new PrintWriter(resultFileName, "UTF-8");

			String line = null;
			MorphemeAnalyzer ma = new MorphemeAnalyzer();
			Parser parser = Parser.getInstance();
			
			Timer timer = new Timer();

			while( (line = br.readLine()) != null ) {
				pw.println(line);
				// 트윗 태그 제거
				line = line.replaceAll("(^|[ \t]+)#[^ \t]+", "");
				line = line.replaceAll("(^|[ \t]+)@[^ \t]+", "");
				// 반복되는기호 제거
				line = line.replaceAll("[?]+", "?");
				line = line.replaceAll("[~]+", "~");
				line = line.replaceAll("[.]{3,}", "...");
				line = line.trim();
				pw.println(line);
				timer.start();
				List<MExpression> ret = ma.leaveJustBest(ma.postProcess(ma.analyze(line)));
				List<Sentence> stl = ma.divideToSentences(ret);
				for( int i = 0; i < stl.size(); i++ ) {
					Sentence st = stl.get(i);
					pw.println("[[ " + st.getSentence() + " ]]");
					for( int j = 0; j < st.size(); j++ ) {
						pw.println(st.get(j));
					}
					
					ParseTree tree = parser.parse(stl.get(i));
					StringBuffer sb = new StringBuffer();
					tree.traverse(sb);
					pw.println(sb);

//					List<ParseTreeNode> nodeList = tree.getNodeList();
//					List<ParseTreeEdge> edgeList = tree.getEdgeList();
//
//					for( int j = 0; j < nodeList.size(); j++ ) {
//						ParseTreeNode node = nodeList.get(j);
//						pw.println("<Node id=\"" + node.getId() + " name=\"" + node.getExp() + "\" label=\"" + node.getMorpXmlStr() + "\" />");
//					}
//					for( int j = 0; j < edgeList.size(); j++ ) {
//						ParseTreeEdge edge = edgeList.get(j);
//						pw.println("<Edge fromId=\"" + edge.getFromId() + " toId=\"" + edge.getToId() + "\" label=\"" + edge.getRelation() + "\" />");
//					}
				}
				timer.stop();
				pw.println("PROCESSING TIME\t" + timer.getInterval());
				pw.println();
				pw.println();
				pw.flush();
			}
			pw.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void maTest(String string)
	{
		if( string == null ) {
			string = "귀에 솔깃할 야담, 전설, 영웅담도 좋지만, 외면하곤 하는 문화유산들의 근대 수난사 또한 자자손손 공유할 이야기로 자리매김해야 하지 않을까요.";
			string = "4㎞ 넘게 걸었지만, 눈맛과 대기의 청량감에 피곤함도 몰랐습니다.";
			//string = "잘모르는노래들인데들어보니좋네요.";
			string = "도대체사람들이정신이없어.";
			string = "승객들의 불편이 적지 않았고, 무엇보다 산업현장의 피해가 컸습니다.";
			string = "범죄로부터 아이들을 구하기 위해서는 먼저 폭력에 노출된 어린이들을 보호하는 것이 필요하다. 또한 이들 만을 위한 사법 제도의 개선이 필요하다. 전세계적으로 청소년 범죄는 늘어나는 추세인데 재범 예방 교육 및 관련 예산은 여전히 제자리걸음이다. 교육의 실효성을 높이기 위해 집단상담 프로그램을 늘리는 등 다양한 방법을 고민하고 있지만 예산이 턱없이 부족한 형편이다. ";
			string = "범죄로부터 아이들을 구하기 위해서는 먼저 폭력에 노출된 어린이들을 보호하는 것이 필요하다.";
			string = "또한 이들 만을 위한 사법 제도의 개선이 필요하다.";
			string = "디자인은 괜찬네요 아쉬운 점이 있다면 가죽이 좀 얇구요... 남방은 상품평이 안 좋던데 생각보단 괜찮아요.. 그런데 마직막 단추에 단춧구멍이 없내요;;;; 마지막 단추라 큰 상관은 없으니 그냥 입을게요~ ";
			string = "니가 보고 싶다고 그 것을 보고 있으면 안 되지~";
			string = "나는 아침에이걸먹으려고했었는데,그렇게하지못했습니다.";
			string = "가지마가지마가지마가지마가지마가지마가지마가지마";
			//string = "가지마 가지마";
			string = "그가 규칙을 어겼기 때문에 규칙에 따라서 그를 처벌함으로써 본보기를 보이는 것이다.";
			string = "엘씨디 모니터도 괜찮더라구요.";
			string = "어느 재벌 며느리와 마찬가지로 남편과 다른 부인 사이에 무슨 일이 일어나는 것을 방지하기 위해.";
			string = "사랑스런 삥꾸색 박스에 고이 담겨왔답니다.";
			string = "제 발에는";
			string = "제 발에는 딱이라고생각해요 ㅎㅎㅎㅎㅎ";
			string = "동주형 안녕하세요~ 잘 지내시는지요? ㅎㅎ 전 미쿡에 잠시 와서 고생좀 하고 있습니다 -_-; 우리나라 사이트 갖고 이것저것 해 보려고 하는데, 역시 한글 처리가 문제더군요. 러 문서를 받아서 자주 나오는 term을 추출해서 모아보려고 합니다. 뭐.. 네이버 실시간 인기 검색어 비슷한 것이 되겠지요? 일단 이렇게 term을 추출하고서 그 원본 document와 갖고 놀고 싶은데... 관련 라이브러리나 알고리즘을 참고할 것이 있을까요? 순수하게 추출 알고리즘만 쓸 수 있을 것이라고 생각은 안하지만 (사전을 이용해야 하겠죠..)  추출을 하지 않으면 무지막지한 작업이 될테니 -_-;;확 적당히 n-gram 잘라서 모두 보관하고 있다가 많이 나오는 걸 처리하기에는.. 좀 무지막지한듯 해서 한번 조언을 구해봅니다. Chulki Lee";
			string = "좀 무지막지한 듯해서한번 조언을 구해봅니다.";
			string = "일반 보통청바지보다 얇게나왔어요";
			string = "일반보통청바지보다얇게나왔어요";
			string = "마사지는 림프부종이 없는 쪽부터시작하여림프부종쪽으로 진행한다.";
			string = "1980년대 초 영국과 프랑스에서 참굴 암컷에 수컷 생식기인 페니스가 달린기현상이보고되었다.";
			string = "혈통의단일성, 가문의일관성이새삼스레 다져지는것에수반되어서친족간의위계며관계가 필요한대로의 질서를 회복하게된다.";
			string = "나는 그가얼마나진화생물학 이론에 정통한사람인지는 모르지만 그의 주장은 놀라우리만치철저하게 진화생물학적인 사고의 결과이다.";
			string = "그들은 그녀를치술신모致述神母라 하여사당을 세워준 것이다.";
			string = "그러면, 우리나라 대학의 연구개발활동은 어떠한지 살펴보도록 하자.";
			string = "아리따운수로부인을쳐다보는노인의하얀수염과같은아름다운한국인";
			string = "오로지보여주기만을위한목적으로일체의세간을두지않는모델하우스는마치화려한패션쇼와도같다.";
			string = "그러기에환멸은보통아름다운것, 놀라운것에부치는꿈의사라짐을의미한다.";
			string = "따라서그는최하위인자율성에대한적정여부를따져적정 100점, 부분적정 50점, 부적정 0점으로설정한후인맥에는그 2배수를, 전문성과소득에는그 3배수를각각부여하여평가한다.";
			string = "따라서그는최하위인자율성에대한적정여부를따진다.";
			string = "전문성과소득에는그 3배수를각각부여하여평가한다.";
			string = "펠리컨이란새는자식에게먹일것이없으면자기창자를꺼내먹인다고한다.";
			string = "네티즌수사대여러분 전남영암에 청소년수련관에이상한여자있어요";
			string = "아냐ㅋㅋㅋㅋㅋ보면 힘이 날 경기가될거야";
		}
		

		System.setProperty("DO_DEBUG", "DO_DEBUG");

		try {
			MorphemeAnalyzer ma = new MorphemeAnalyzer();
			ma.createLogger(null);
			Timer timer = new Timer();
			timer.start();
			List<MExpression> ret = ma.analyze(string);
			timer.stop();
			timer.printMsg("Time");

			for( MExpression me : ret )
				System.out.println(me);

			ret = ma.postProcess(ret);
			for( MExpression me : ret )
				System.out.println(me);

			ret = ma.leaveJustBest(ret);
			for( MExpression me : ret )
				System.out.println(me);

			List<Sentence> stl = ma.divideToSentences(ret);
			for( int i = 0; i < stl.size(); i++ ) {
				Sentence st = stl.get(i);
				System.out.println("=============================================" + st.getSentence());
				for( int j = 0; j < st.size(); j++ ) {
					System.out.println(st.get(j));
				}
			}

			ma.closeLogger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void keTest()
	{
		String string = "문서 엔터티의 개념이 명확하지 못하다. 즉, 문서 엔터티에 저장되는 단위개체인 문서가 다른 부서로 발신을 하면 다른 문서가 되는 것인지 수정을 할 때는 문서가 새로 생성되지 않는 것인지, 혹은 결재선으로 발신하면 문서가 그대로 있는 것인지 등에 대한 명확한 정의가 없다. 개발 담당자 마저도 이러한 개념을 명확히 설명하지 못하고 있다.";
		string = "동주형 안녕하세요~ 잘 지내시는지요? ㅎㅎ 전 미쿡에 잠시 와서 고생좀 하고 있습니다 -_-; 우리나라 사이트 갖고 이것저것 해 보려고 하는데, 역시 한글 처리가 문제더군요. 러 문서를 받아서 자주 나오는 term을 추출해서 모아보려고 합니다. 뭐.. 네이버 실시간 인기 검색어 비슷한 것이 되겠지요? 일단 이렇게 term을 추출하고서 그 원본 document와 갖고 놀고 싶은데... 관련 라이브러리나 알고리즘을 참고할 것이 있을까요? 순수하게 추출 알고리즘만 쓸 수 있을 것이라고 생각은 안하지만 (사전을 이용해야 하겠죠..)  추출을 하지 않으면 무지막지한 작업이 될테니 -_-;;확 적당히 n-gram 잘라서 모두 보관하고 있다가 많이 나오는 걸 처리하기에는.. 좀 무지막지한듯 해서 한번 조언을 구해봅니다. Chulki Lee";
		string = "매목 매과의 조류이고,몸길이가 33∼48cm이다. 배면은 청회색이고 가슴에는 굵은 세로무늬가 있으며,뺨에는 수염과 같은 흑색 반문이 있고,부리에는 치상돌기(齒狀突起)가 있으며 콧구멍은 둥글고 속에 돌기가 있으며 그것으로 콧구멍에서의 풍압(風壓)을 조절하고,눈은 다른 새보다 크며 눈 밑에 있는 검은 빛깔의 띠는 광선을 흡수하여 눈이 부시는 것을 방지한다. 대개 해안이나 섬의 절벽 바위의 선반과 같은 곳에 둥지를 마련하지만 외국에서는 도시의 고층 건물에 둥지를 틀기도 한다. 오리류,도요,물떼새류,비둘기류 등을 포식하며,최대한의 속력으로 급강하하여 먹이를 낚아챈 후 큰 새는 땅 위에서 뜯어 먹으며 시속 60km이나 급강하시에는 200km 이상의 속력을 내고,참매,붉은배새매,새매,황조롱이와 함께 천연기념물 제 323호로 지정하여 보호하고 있으며,유라시아 대륙,북아메리카,아프리카,오스트레일리아 등 전세계에 널리 분포하고,한국에서는 해안 절벽에서 번식하는 드문 텃새이다. 한국에서는 오래 전부터 꿩사냥에 이용해 왔음";

		try {
			KeywordExtractor ke = new KeywordExtractor();
			KeywordList ret = ke.extractKeyword(string, false);

			System.out.println(ret.getDocLen());
			for( int i = 0, size = ret.size(); i < size; i++ ) {
				System.out.println(ret.get(i));
			}
			System.out.println(ret.getDocLen());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void parseTest()
	{
		Parser parser = Parser.getInstance();

		//System.setProperty("DO_DEBUG", "DO_DEBUG");

		MorphemeAnalyzer ma = new MorphemeAnalyzer();
		List<MExpression> mel;

		String string = "제가 엔딩 알려드릴께요 ㅎㅎ 31번곡 Dearest 이게 엔딩곡이에요~ 너무죠아요!!강추강추";
		string = "기장이 조금 길어요 그래도 잘 입어 보려구요";
		string = "아직 입어보진않았는데요 괜찮은거 같네요 여러가지 몇개주문했는데 나머지도 입고되는데로 빠른배송부탁드려요.";
		string = "사무라이를 본받고 싶다고 하신 분들- 우리역사 속의 무자비한 사무라이를 본받고 싶다고한건 아닌것같아요 영화속에서 보여준 그런 무사도 정신을 본받고 싶다고 한것 같아요";
		string = "그리고 배경과 소제가 일본과 사무라이일뿐 감독이 일본인인건 아니니까 이영화 감독이 일본역사에 관심이 많다라고 그러더라구요 담번엔 우리의 무사도정신도 영화화 됐으면 좋겠네요";
		string = "승객들의 불편이 적지 않았고, 무엇보다 산업 현장의 피해가 컸습니다.";
		string = "그밤은내가어제먹은밤이다.";
		string = "범죄로부터 아이들을 구하기 위해서는 먼저 폭력에 노출된 어린이들을 보호하는 것이 필요하다.";
		string = "그가 규칙을 어겼기 때문에 규칙에 따라서 그를 처벌함으로써 본보기를 보이는 것이다.";
		string = "진짜 실컷 해놓고.질리니까 환불해달라그러네ㅡㅡ역겹다";

		try {
			mel = ma.postProcess(ma.analyze(string));
			//System.out.println(mel);
			mel = ma.leaveJustBest(mel);
			List<Sentence> stl = ma.divideToSentences(mel);

			for( int i = 0, size = stl.size(); i < size; i++ ) {
				ParseTree tree = parser.parse(stl.get(i));
				StringBuffer sb = new StringBuffer();
				tree.traverse(sb);
				System.out.println(sb);

				List<ParseTreeNode> nodeList = tree.getNodeList();
				List<ParseTreeEdge> edgeList = tree.getEdgeList();

				for( int j = 0; j < nodeList.size(); j++ ) {
					ParseTreeNode node = nodeList.get(j);
					System.out.println("<Node id=\"" + node.getId() + " name=\"" + node.getExp() + "\" label=\"" + node.getMorpXmlStr() + "\" />");
				}
				for( int j = 0; j < edgeList.size(); j++ ) {
					ParseTreeEdge edge = edgeList.get(j);
					System.out.println("<Edge fromId=\"" + edge.getFromId() + " toId=\"" + edge.getToId() + "\" label=\"" + edge.getRelation() + "\" />");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
