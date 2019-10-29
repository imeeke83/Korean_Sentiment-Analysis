package org.snu.ids.ha.dic;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.snu.ids.ha.constants.POSTag;
import org.snu.ids.ha.util.Timer;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * 
 * </pre>
 * @author 	Dongjoo
 * @since	2011. 4. 2.
 */
public class UNPDDictionary
{
	private static final float							DEFAULT_PROB		= -30f;
	private static final Hashtable<Character, Float>	PROB_AT_NOUN_HASH	= new Hashtable<Character, Float>();
	static {
		load("/dic/prob/lnpr_syllable_uni_noun.dic");
	}


	/**
	 * <pre>
	 * 사전 파일로부터 음절 Unigram에 대한 확률 값을 읽어들인다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 12. 11
	 * @param fileName
	 */
	public static final void load(String fileName)
	{
		System.out.println("Loading " + fileName);
		Timer timer = new Timer();
		timer.start();

		String line = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(SpacingPDDictionary.class.getResourceAsStream(fileName), "UTF-8"));

			while( (line = br.readLine()) != null ) {
				if( !Util.valid(line) || line.startsWith("//") ) continue;
				line = line.trim();
				char ch = line.charAt(0);
				float lnpr = Float.parseFloat(line.substring(1).trim());
				PROB_AT_NOUN_HASH.put(ch, lnpr);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(line);
			System.err.println("Unable to load probability dictionary!!");
		} finally {
			timer.stop();
			System.out.println(PROB_AT_NOUN_HASH.size() + " values are loaded. (Loading time( " + timer.getInterval() + " secs)");
		}
	}


	/**
	 * <pre>
	 * 해당 음절이 명사(고유, 보통)에서 출현할 확률을 구한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2011. 4. 2.
	 * @param ch
	 * @return
	 */
	static final float getProbAtNoun(char ch)
	{
		Float lnpr = PROB_AT_NOUN_HASH.get(ch);
		if( lnpr != null ) {
			return lnpr;
		}
		return DEFAULT_PROB;
	}


	/**
	 * <pre>
	 * 음절간 출현이 독립적이라 보고 
	 * P(pos|abs) = P(pos|a)P(pos|b)P(pos|c)/P(pos)P(pos)
	 *            = P(pos|a)P(pos|b)P(pos|c)로 처리해서 반환
	 * P(pos)P(pos) 부분을 생략하여 신조어에 대한 일종의 패널티로 작용하도록 함. 
	 * </pre>
	 * @author	Dongjoo
	 * @since	2011. 4. 2.
	 * @param str
	 * @return
	 */
	public static float getProb(String str)
	{
		if( !Util.valid(str) ) return Float.MIN_VALUE;
		float prob = 0;
		for( int i = 0, len = str.length(); i < len; i++ ) {
			char ch = str.charAt(i);
			prob += getProbAtNoun(ch);
		}
		return prob;
	}


	/**
	 * <pre>
	 * 명사에 대한 품사 태깅 추정 확률을 구함.
	 * 사전에 등록된 명사에 한하여 추정하는 것으로 페널티를 적용하지 않아서 확률 값이 적당히 크게 나오게 함.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2011. 4. 26.
	 * @param str
	 * @return
	 */
	public static float getProb2(String str)
	{
		if( !Util.valid(str) ) return Float.MIN_VALUE;
		float prob = 0;
		for( int i = 0, len = str.length(); i < len; i++ ) {
			char ch = str.charAt(i);
			prob += getProbAtNoun(ch);
			if( i > 0 ) prob -= PDDictionary.getLnprPos(POSTag.NNA);
		}
		return prob;
	}


	public static void main(String[] args)
	{
		System.out.println(getProb("안드로보이"));
		System.out.println(getProb2("안드로보이"));
	}
}
