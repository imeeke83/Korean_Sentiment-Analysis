import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.snu.ids.ha.index.Keyword;
import org.snu.ids.ha.index.KeywordExtractor;
import org.snu.ids.ha.index.KeywordList;
import org.snu.ids.ha.ma.MExpression;
import org.snu.ids.ha.ma.MorphemeAnalyzer;
import org.snu.ids.ha.ma.Sentence;
import org.snu.ids.ha.sp.ParseTree;
import org.snu.ids.ha.sp.ParseTreeEdge;
import org.snu.ids.ha.sp.ParseTreeNode;
import org.snu.ids.ha.sp.Parser;
import org.snu.ids.ha.util.Timer;

import SyntaxAnalysis.Preprocessing;
import SyntaxAnalysis.TreeNode;
import SyntaxAnalysis.SentenceStructure;
import SentimentAnalysis.SentimentTree;


public class Example
{

	public static void main(String[] args)
	{
		String string = "오늘 하루는 너무 힘들었어. 나는 정말 많은 기대를 가지고 이 활동을 시작했었어. 나는 이 활동이 재미있는 줄 알았는데 재미있지 않았다.";
		
		Test(string);
		//maTest();
		//keTest();
		//parseTest();
	}

	public static void Test(String string) {
		try {
			MorphemeAnalyzer ma = new MorphemeAnalyzer();
			Preprocessing Preprocess = new Preprocessing();
			SentenceStructure ParseTree = new SentenceStructure();
			SentimentTree SentimentTree = new SentimentTree();
			
			double Total = 0;
			
			ma.createLogger(null);
			List<MExpression> ret = ma.analyze(string);
			ret = ma.postProcess(ret);
			ret = ma.leaveJustBest(ret);
			
			List<List<String[]>> AllText = new ArrayList<List<String[]>>();
			List<TreeNode> TreeList = new ArrayList<TreeNode>();
			
			List<Sentence> stl = ma.divideToSentences(ret);
			for( int i = 0; i < stl.size(); i++ ) {
				Sentence st = stl.get(i);
				AllText.add(Preprocess.doPreprocess(st));
			}
			System.out.println("");
			
			for(int i = 0; i < AllText.size(); i++) {
				for(int j = 0; j < AllText.get(i).size(); j++) {
					System.out.print(Arrays.toString(AllText.get(i).get(j)));	
				}
				System.out.println("");
				TreeList.add(ParseTree.createParseTree(AllText.get(i)));
			}

			System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
			Total = SentimentTree.createSentmentTree(TreeList);
			System.out.println("");
			System.out.println("전체 문장의 감정 수치 : " + Total);
			
			ma.closeLogger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void maTest()
	{
		String string = "저는 대학생이구요. 소프트웨어 관련학과 입니다. DB는 수업을 한번 들은 적이 있으며, 수학은 대학에서 통계학, 선형대수학, 이산수학, 대학수학 등을 배웠지만... 자주 사용을 안하다보니 모두 까먹은 상태입니다.";
		string = "그러면 조개가 쏘옥 올라온다. 그는 노발대발 성을 내었다.";
		
		System.setProperty("DO_DEBUG", "DO_DEBUG");
		try {
			MorphemeAnalyzer ma = new MorphemeAnalyzer();
			ma.createLogger(null);
			Timer timer = new Timer();
			timer.start();
			List<MExpression> ret = ma.analyze(string);
			timer.stop();
			timer.printMsg("Time");

			ret = ma.postProcess(ret);

			ret = ma.leaveJustBest(ret);

			List<Sentence> stl = ma.divideToSentences(ret);
			for( int i = 0; i < stl.size(); i++ ) {
				Sentence st = stl.get(i);
				System.out.println("=============================================  " + st.getSentence());
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
		String strToExtrtKwrd = "저는 대학생이구요. 소프트웨어 관련학과 입니다. DB는 수업을 한번 들은 적이 있으며, 수학은 대학에서 통계학, 선형대수학, 이산수학, 대학수학 등을 배웠지만... 자주 사용을 안하다보니 모두 까먹은 상태입니다.";

		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(strToExtrtKwrd, true);
		for( int i = 0; i < kl.size(); i++ ) {
			Keyword kwrd = kl.get(i);
			System.out.println(kwrd.getString() + "\t" + kwrd.getCnt());
		}
	}
	
	
	public static void parseTest()
	{
		Parser parser = Parser.getInstance();

		//System.setProperty("DO_DEBUG", "DO_DEBUG");

		MorphemeAnalyzer ma = new MorphemeAnalyzer();
		List<MExpression> mel;

		String string = "배송이 빨라서 좋네요.";

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
