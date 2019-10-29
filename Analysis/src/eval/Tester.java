/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2009. 09. 07
 */
package eval;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.snu.ids.ha.constants.POSTag;
import org.snu.ids.ha.ma.Morpheme;
import org.snu.ids.ha.ma.MorphemeAnalyzer;


/**
 * <pre>
 * 
 * </pre>
 * @author 	therocks
 * @since	2009. 09. 07
 */
public class Tester
{

	/**
	 * <pre>
	 * 
	 * </pre>
	 * @author	therocks
	 * @since	2009. 09. 07
	 * @param args
	 */
	public static void main(String[] args)
	{
		String[] files = new String[] { "doc/testdata/Reviews/Review01.xml", "doc/testdata/Reviews/Review02.xml",
				"doc/testdata/Reviews/Review03.xml", "doc/testdata/Reviews/Review04.xml",
				"doc/testdata/Reviews/Review05.xml", "doc/testdata/Reviews/Review06.xml",
				"doc/testdata/Reviews/Review07.xml", "doc/testdata/Reviews/Review08.xml",
				"doc/testdata/Reviews/Review09.xml", "doc/testdata/Reviews/Review10.xml" };
		
		files = new String[] { "doc/testdata/Reviews/Review05.xml" };
		//		files = new String[] { "doc/testdata/News/News01.xml", "doc/testdata/News/News02.xml",
		//				"doc/testdata/News/News03.xml", "doc/testdata/News/News04.xml", "doc/testdata/News/News05.xml",
		//				"doc/testdata/News/News06.xml", "doc/testdata/News/News07.xml", "doc/testdata/News/News08.xml",
		//				"doc/testdata/News/News09.xml", "doc/testdata/News/News10.xml" };
		//		files = new String[] { "doc/testdata/Blogs/Blog01.xml", "doc/testdata/Blogs/Blog02.xml",
		//				"doc/testdata/Blogs/Blog03.xml", "doc/testdata/Blogs/Blog04.xml", "doc/testdata/Blogs/Blog05.xml",
		//				"doc/testdata/Blogs/Blog06.xml", "doc/testdata/Blogs/Blog07.xml", "doc/testdata/Blogs/Blog08.xml",
		//				"doc/testdata/Blogs/Blog09.xml", "doc/testdata/Blogs/Blog10.xml" };
		//		
		files = new String[] { "doc/testdata/sejong/sent.xml" };
		test(files);
		//getDic(files);
	}


	public static void test(String[] files)
	{

		//System.setProperty("DO_DEBUG", "DO_DEBUG");
		ANSSentence ts = null;
		try {
			TestDocument td = new TestDocument();

			PrintWriter pw = new PrintWriter("sejong.txt", "UTF-8");

			MorphemeAnalyzer ma = new MorphemeAnalyzer();

			int ansCnt = 0;
			double sumMAAccuracy1 = 0d, sumMAAccuracy2 = 0d;
			double sumSPAccuracy1 = 0d, sumSPAccuracy2 = 0d;

			for( int j = 0; j < files.length; j++ ) {
				td.read(files[j]);
				for( int i = 0; i < td.size(); i++ ) {

					ts = td.get(i);

//					for( Morpheme mp : ts.getSimpleResult() ) {
//						if( !mp.isTagOf(POSTag.E) && !mp.isTagOf(POSTag.S) && !mp.isTagOf(POSTag.J) && !mp.isTagOf(POSTag.O) && !mp.isTagOf(POSTag.ON) ) System.out.println(mp.getSmplStr());
//						//System.out.println(mp.getSmplStr());
//					}
					

					ANSSentence analResult1 = new ANSSentence(ma.leaveJustBest(ma.postProcess(ma.analyze(ts.getSentence()))));
					ANSSentence analResult2 = new ANSSentence(ma.leaveJustBest(ma.postProcess(ma.analyze(ts.getUnspaced()))));

					ts.merge();
					analResult1.merge();
					analResult2.merge();

					TestResult tr1 = new TestResult(ts, analResult1);
					TestResult tr2 = new TestResult(ts, analResult2);

					//					if( ts.sentence.equals("이 때 피검자가 눈동자를 이동시키지 않도록 주지시켜야 한다.")) {
					//						System.out.println(tr.getTable());
					//					}

					ansCnt++;
					sumMAAccuracy1 += tr1.getAccuracy();
					sumMAAccuracy2 += tr2.getAccuracy();
					

					double spa1 = getEditSim(ts.getOrg(), analResult1.getSentence());
					double spa2 = getEditSim(ts.getOrg(), analResult2.getSentence());
					
					sumSPAccuracy1 += spa1;
					sumSPAccuracy2 += spa2;
					
					pw.println(String.format("%s\t%4d%4d%4d%4d%8.3f%8.3f", files[j], i, tr1.ansLen, tr1.candLen, tr1.editDist, tr1.getAccuracy(), spa1));
					pw.println(String.format("%s\t%4d%4d%4d%4d%8.3f%8.3f", files[j], i, tr2.ansLen, tr2.candLen, tr2.editDist, tr2.getAccuracy(), spa2));
					pw.println(ts.getSentence());
					pw.println(ts.getUnspaced());
					pw.println(ts.getOrg());
					pw.println(analResult1.getSentence());
					pw.println(analResult2.getSentence());
					pw.println(tr1.getAnsMrpListStr());
					pw.println(tr1.getCandMrpListStr());
					pw.println(tr2.getCandMrpListStr());
					pw.println();
					pw.println();

					if( ansCnt % 50 == 0 ) {
						System.out.println(ansCnt);
					}
				}
			}

			System.out.println(sumMAAccuracy1 / ansCnt);
			System.out.println(sumMAAccuracy2 / ansCnt);
			System.out.println();
			
			System.out.println(sumSPAccuracy1 / ansCnt);
			System.out.println(sumSPAccuracy2 / ansCnt);

			pw.close();
		} catch (Exception e) {
			System.err.println(ts.getSentence());
			e.printStackTrace();
		}
	}


	/**
	 * <pre>
	 * 테스트 문서에 속한 어휘 사전을 추출한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2011. 4. 21.
	 * @param files
	 */
	public static void getDic(String[] files)
	{

		ANSSentence ts = null;
		try {
			TestDocument td = new TestDocument();

			Hashtable<String, Morpheme> table = new Hashtable<String, Morpheme>();

			for( int j = 0; j < files.length; j++ ) {
				td.read(files[j]);
				for( int i = 0; i < td.size(); i++ ) {

					ts = td.get(i);

					for( Morpheme mp : ts.getSimpleResult() ) {
						if( !mp.isTagOf(POSTag.E) && !mp.isTagOf(POSTag.S) && !mp.isTagOf(POSTag.J) && !mp.isTagOf(POSTag.O) && !mp.isTagOf(POSTag.ON) ) {
							if( !table.containsKey(mp.getSmplStr()) ) {
								table.put(mp.getSmplStr(), mp);
							}
						}
					}
				}
			}

			ArrayList<Morpheme> mpList = new ArrayList<Morpheme>(table.values());

			Collections.sort(mpList, new Comparator<Morpheme>()
			{

				@Override
				public int compare(Morpheme mp1, Morpheme mp2)
				{
					int cmp = mp1.getTag().compareTo(mp2.getTag());
					if( cmp == 0 ) {
						return mp1.getString().compareTo(mp2.getString());
					}
					return cmp;
				}
			});
			
			for(Morpheme mp : mpList) {
				System.out.println(mp.getSmplStr());
			}

		} catch (Exception e) {
			System.err.println(ts.getSentence());
			e.printStackTrace();
		}
	}

	
	public static double getEditSim(String str1, String str2)
	{
		int len1 = str1.length(), len2 = str2.length();
		int[][] table = new int[len1+1][len2+1];
		
		for(int i= 0; i <= len1; i++) {
			table[i][0] = i;
		}
		
		for(int j= 0; j <= len2; j++) {
			table[0][j] = j;
		}
		
		for(int i = 0; i < len1; i++) {
			for(int j = 0; j < len2; j++) {
				int cost = 1;
				if( str1.charAt(i) == str2.charAt(j)) cost = 0;
				int deletionCost = table[i][j + 1] + 1;
				int insertionCost = table[i + 1][j] + 1;
				int substitutionCost = table[i][j] + cost;
				table[i + 1][j + 1] = Math.min(substitutionCost, Math.min(deletionCost, insertionCost));
			}
		}
		int editDist = table[len1][len2];
		return (double) (Math.max(len1, len2) - editDist) / (double) Math.max(len1, len2);
	}
}

class TestResult
{
	int				ansLen		= 0;
	int				candLen		= 0;

	int[][]			table		= null;
	int				editDist	= 0;

	List<Morpheme>	ansMrpList	= null;
	List<Morpheme>	candMrpList	= null;


	public TestResult(ANSSentence answer, ANSSentence cand)
	{
		ansMrpList = answer.getSimpleResult();
		candMrpList = cand.getSimpleResult();

		ansLen = ansMrpList.size();
		candLen = candMrpList.size();

		table = new int[ansLen + 1][candLen + 1];

		for( int i = 0; i <= ansLen; i++ ) {
			table[i][0] = i;
		}

		for( int i = 0; i <= candLen; i++ ) {
			table[0][i] = i;
		}

		for( int i = 0; i < ansLen; i++ ) {
			for( int j = 0; j < candLen; j++ ) {
				int cost = 1;
				Morpheme mpAns = ansMrpList.get(i);
				Morpheme mpCand = candMrpList.get(j);
				// 동일한 형태소인지 확인
				if( getWeakStr(mpAns).equals(getWeakStr(mpCand)) ) cost = 0;
				int deletionCost = table[i][j + 1] + 1;
				int insertionCost = table[i + 1][j] + 1;
				int substitutionCost = table[i][j] + cost;
				table[i + 1][j + 1] = Math.min(substitutionCost, Math.min(deletionCost, insertionCost));
			}
		}

		editDist = table[ansLen][candLen];
	}


	public String getTable()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0; i < ansLen + 1; i++ ) {
			for( int j = 0; j < candLen + 1; j++ ) {
				sb.append(String.format("%4d", table[i][j]));
			}
			sb.append("\n");
		}
		return sb.toString();
	}


	public double getAccuracy()
	{
		return (double) (Math.max(candLen, ansLen) - editDist) / (double) Math.max(candLen, ansLen);
	}


	String getAnsMrpListStr()
	{
		return getMrpListStr(ansMrpList);
	}


	String getCandMrpListStr()
	{
		return getMrpListStr(candMrpList);
	}


	String getMrpListStr(List<Morpheme> mrpList)
	{
		StringBuffer sb = new StringBuffer();

		for( int i = 0, size = mrpList.size(); i < size; i++ ) {
			Morpheme mp = mrpList.get(i);
			if( i > 0 ) sb.append(", ");
			sb.append(getWeakStr(mp));
		}

		return sb.toString();
	}


	final static long getWeakTag(long tag)
	{
		if( ((POSTag.N | POSTag.UN | POSTag.XSN) & tag) > 0 ) {
			return POSTag.N;
		} else if( (POSTag.V & tag) > 0 ) {
			return POSTag.V;
		} else if( ((POSTag.MD) & tag) > 0 ) {
			return POSTag.MD;
		} else if( (POSTag.EP & tag) > 0 ) {
			return POSTag.EP;
		} else if( (POSTag.EM & tag) > 0 ) {
			return POSTag.EM;
		} else if( (POSTag.S & tag) > 0 ) {
			return POSTag.S;
		} else if( (POSTag.J & tag) > 0 ) {
			return POSTag.J;
		}
		return tag;
	}


	final static String getWeakStr(Morpheme mp)
	{
		StringBuffer sb = new StringBuffer();
		// 았, 었, 였 통일
		if( mp.isTagOf(POSTag.EP) ) {
			if( mp.getString().equals("았") | mp.getString().equals("었") | mp.getString().equals("였") ) {
				sb.append("았");
			} else {
				sb.append(normalize(mp.getString()));
			}
		}
		// 아, 어, 여 통일
		else if( mp.isTagOf(POSTag.EM) ) {
			if( mp.getString().equals("아") | mp.getString().equals("어") | mp.getString().equals("여") ) {
				sb.append("아");
			} else if( mp.getString().equals("아서") | mp.getString().equals("어서") | mp.getString().equals("여서") ) {
				sb.append("아서");
			} else {
				sb.append(normalize(mp.getString()));
			}
		} else {
			sb.append(normalize(mp.getString()));
		}
		sb.append("/");
		sb.append(POSTag.getTag(getWeakTag(mp.getTagNum())));
		return sb.toString();
	}


	final static String normalize(String str)
	{
		str = str.replaceAll("ᆫ", "ㄴ");
		str = str.replaceAll("ᆯ", "ㄹ");
		str = str.replaceAll("ᄆ", "ㅁ");
		str = str.replaceAll("ᄇ", "ㅂ");
		return str;
	}
}
