package org.snu.ids.ha.dic;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.snu.ids.ha.constants.POSTag;
import org.snu.ids.ha.ma.Morpheme;
import org.snu.ids.ha.util.Timer;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * </pre>
 * @author 	therocks
 * @since	2009. 10. 12
 */
final public class PDDictionary
{
	private static final Hashtable<Long, Float>		LNPR_POS				= new Hashtable<Long, Float>(50);
	private static final Hashtable<String, Float>	LNPR_MORP				= new Hashtable<String, Float>(80000);
	private static final Hashtable<String, Float>	LNPR_MORPS_G_EXP		= new Hashtable<String, Float>(70000);
	private static final Hashtable<String, Float>	LNPR_POS_G_EXP			= new Hashtable<String, Float>(70000);
	private static final Hashtable<String, Float>	LNPR_POS_G_MORP_INTRA	= new Hashtable<String, Float>(60000);
	private static final Hashtable<String, Float>	LNPR_POS_G_MORP_INTER	= new Hashtable<String, Float>(520000);

	static {
		System.out.println("Prob Dic Loading!");
		Timer timer = new Timer();
		timer.start();
		loadLnprPos("/dic/prob/lnpr_pos.dic");
		System.out.println(LNPR_POS.size() + " loaded!");
		loadLnprMorp("/dic/prob/lnpr_morp.dic");
		System.out.println(LNPR_MORP.size() + " loaded!");
		loadLnprPosGExp("/dic/prob/lnpr_pos_g_exp.dic");
		System.out.println(LNPR_POS_G_EXP.size() + " loaded!");
		loadLnprMorpsGExp("/dic/prob/lnpr_morps_g_exp.dic");
		loadLnprPosGMorp("/dic/prob/lnpr_pos_g_morp_intra.dic", LNPR_POS_G_MORP_INTRA);
		System.out.println(LNPR_POS_G_MORP_INTRA.size() + " loaded!");
		loadLnprPosGMorp("/dic/prob/lnpr_pos_g_morp_inter.dic", LNPR_POS_G_MORP_INTER);
		System.out.println(LNPR_POS_G_MORP_INTER.size() + " loaded!");
		timer.stop();
		System.out.println("(Loading time : " + timer.getInterval() + " secs!");
	}


	static final private void loadLnprPos(String fileName)
	{
		ProbDicReader dr = null;
		try {
			dr = new ProbDicReader(fileName);
			String[] arr = null;
			while( (arr = dr.read()) != null ) {
				long pos = POSTag.getTagNum(arr[0]);
				float lnpr = Float.parseFloat(arr[1]);
				LNPR_POS.put(pos, lnpr);
			}
			dr.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(dr.line);
			System.err.println("Loading error: " + fileName);
		}
	}


	static final private void loadLnprMorp(String fileName)
	{
		ProbDicReader dr = null;
		try {
			dr = new ProbDicReader(fileName);
			String[] arr = null;
			while( (arr = dr.read()) != null ) {
				String exp = arr[0];
				long pos = POSTag.getTagNum(arr[1]);
				float lnpr = Float.parseFloat(arr[2]);
				LNPR_MORP.put(exp + ":" + pos, lnpr);
			}
			dr.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(dr.line);
			System.err.println("Loading error: " + fileName);
		}
	}


	static final private void loadLnprPosGExp(String fileName)
	{
		ProbDicReader dr = null;
		try {
			dr = new ProbDicReader(fileName);
			String[] arr = null;
			while( (arr = dr.read()) != null ) {
				String exp = arr[0];
				long pos = POSTag.getTagNum(arr[1]);
				float lnpr = Float.parseFloat(arr[2]);
				LNPR_POS_G_EXP.put(pos + "|" + exp, lnpr);
			}
			dr.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(dr.line);
			System.err.println("Loading error: " + fileName);
		}
	}


	static final private void loadLnprMorpsGExp(String fileName)
	{
		ProbDicReader dr = null;
		try {
			dr = new ProbDicReader(fileName);
			String[] arr = null;
			while( (arr = dr.read()) != null ) {
				String morps = arr[0];
				float lnpr = Float.parseFloat(arr[1]);
				LNPR_MORPS_G_EXP.put(morps, lnpr);
			}
			dr.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(dr.line);
			System.err.println("Loading error: " + fileName);
		}
	}


	static final private void loadLnprPosGMorp(String fileName, Hashtable<String, Float> probMap)
	{
		ProbDicReader dr = null;
		try {
			dr = new ProbDicReader(fileName);
			String[] arr = null;
			while( (arr = dr.read()) != null ) {
				// Pr(prevTag|str,tag)
				if( arr.length == 4 ) {
					long prevPos = POSTag.getTagNum(arr[0]);
					String exp = arr[1];
					long pos = POSTag.getTagNum(arr[2]);
					float lnpr = Float.parseFloat(arr[3]);
					probMap.put(getKey(prevPos, exp, pos), lnpr);
				}
				// Pr(prevTag|tag)
				else if( arr.length == 3 ) {
					long prevPos = POSTag.getTagNum(arr[0]);
					long pos = POSTag.getTagNum(arr[1]);
					float lnpr = Float.parseFloat(arr[2]);
					probMap.put(getKey(prevPos, null, pos), lnpr);
				}

			}
			dr.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(dr.line);
			System.err.println("Loading error: " + fileName);
		}
	}


	static final String getKey(long prevPos, String exp, long pos)
	{
		return prevPos + "|" + exp + ":" + pos;
	}


	private static final float	MIN_LNPR_POS	= -9;
	private static final float	MIN_LNPR_MORP	= -18;


	/**
	 * <pre>
	 * Pr(pos)
	 * </pre>
	 * @author	Dongjoo
	 * @since	2011. 4. 11.
	 * @param pos
	 * @return
	 */
	public static float getLnprPos(long pos)
	{
		Float lnpr = LNPR_POS.get(getPrTag(pos));
		if( lnpr == null ) {
			return MIN_LNPR_POS;
		}
		return lnpr;
	}


	/**
	 * <pre>
	 * Pr(exp,pos)
	 * </pre>
	 * @author	Dongjoo
	 * @since	2011. 4. 11.
	 * @param exp
	 * @param pos
	 * @return
	 */
	private static float getLnprMorp(String exp, long pos)
	{
		Float lnpr = LNPR_MORP.get(exp + ":" + getPrTag(pos));
		if( lnpr == null ) {
			return MIN_LNPR_MORP;
		}
		return lnpr;
	}


	/**
	 * <pre>
	 * Pr(exp|pos)
	 * </pre>
	 * @author	Dongjoo
	 * @since	2011. 4. 11.
	 * @param exp
	 * @param pos
	 * @return
	 */
	public static float getLnprPosGExp(String exp, long pos)
	{
		Float lnpr = LNPR_POS_G_EXP.get(getPrTag(pos) + "|" + exp);
		if( lnpr == null ) {
			if( pos == POSTag.NNG ) return UNPDDictionary.getProb2(exp);
			return getLnprPos(pos);
		}
		return lnpr;
	}


	public static float getLnprMorpsGExp(Morpheme preMp, Morpheme curMp)
	{
		return getLnprMorpsGExp(preMp.getString(), preMp.getTagNum(), curMp.getString(), curMp.getTagNum());
	}


	public static float getLnprMorpsGExp(String prevMorp, long prevPos, String curMorp, long curPos)
	{
		String key = prevMorp + "/" + getTag(prevPos) + "+" + curMorp + "/" + getTag(curPos);
		Float lnpr = LNPR_MORPS_G_EXP.get(key);
		if( lnpr == null ) return 1;
		return lnpr;
	}


	private static String getTag(long pos)
	{
		if( (POSTag.VX & pos) > 0 ) {
			return "VX";
		} else if( (POSTag.EC & pos) > 0 ) {
			return "EC";
		} else if( (POSTag.EF & pos) > 0 ) {
			return "EF";
		}
		return POSTag.getTag(pos);
	}


	public static float getLnprPosGMorpIntra(long prevPos, String exp, long pos)
	{
		return getLnprPosGMorp(LNPR_POS_G_MORP_INTRA, prevPos, exp, pos);
	}


	public static float getLnprPosGMorpInter(long prevPos, String exp, long pos)
	{
		return getLnprPosGMorp(LNPR_POS_G_MORP_INTER, prevPos, exp, pos);
	}


	/**
	 * <pre>
	 * Pr(prev_pos|exp,pos)
	 * </pre>
	 * @author	Dongjoo
	 * @since	2011. 4. 11.
	 * @param lnprMap
	 * @param prevPos
	 * @param exp
	 * @param pos
	 * @return
	 */
	private static float getLnprPosGMorp(Hashtable<String, Float> lnprMap, long prevPos, String exp, long pos)
	{
		Float lnpr = lnprMap.get(getKey(getPrTag(prevPos), exp, getPrTag(pos)));
		if( lnpr == null && (getLnprMorp(exp, pos) < -14 || (POSTag.S & prevPos) > 0 || (POSTag.NNP & pos) > 0) ) {
			lnpr = lnprMap.get(getKey(getPrTag(prevPos), null, getPrTag(pos)));
		}
		if( lnpr == null ) return MIN_LNPR_MORP;
		return lnpr;
	}


	/**
	 * 확률 값을 구할 수 있는 수준의 태그를 반환.
	 * @author	therocks
	 * @since	2009. 10. 14
	 * @param tag
	 * @return 확률값이 학습된 태그
	 */
	public static long getPrTag(long tag)
	{
		if( ((POSTag.NNA | POSTag.UN) & tag) > 0 ) {
			return POSTag.NNA;
		} else if( ((POSTag.NNM | POSTag.NNB) & tag) > 0 ) {
			return POSTag.NNB;
		} else if( (POSTag.VX & tag) > 0 ) {
			return POSTag.VX;
		} else if( ((POSTag.MD) & tag) > 0 ) {
			return POSTag.MD;
		} else if( (POSTag.EP & tag) > 0 ) {
			return POSTag.EP;
		} else if( (POSTag.EF & tag) > 0 ) {
			return POSTag.EF;
		} else if( (POSTag.EC & tag) > 0 ) {
			return POSTag.EC;
		}
		return tag;
	}


	public static float getLnpr(String morps)
	{
		float lnpr = 0f;
		String[] arr = morps.trim().split("[+]");

		ArrayList<Morpheme> mpList = new ArrayList<Morpheme>();
		for( String temp : arr ) {
			if( temp.equals(" ") ) {
				mpList.add(new Morpheme(" ", POSTag.S));
			} else {
				String[] arr2 = temp.split("[/]");
				mpList.add(new Morpheme(arr2[1], POSTag.getTagNum(arr2[2])));
			}
		}

		Morpheme preMp = null;
		boolean spacing = false;
		System.out.println(morps);
		System.out.println(String.format("\tmorp%22s%10s%10s%10s%10s", "PosGExp", "spacing", "PosGMorp", "Pos", "lnpr"));
		for( Morpheme curMp : mpList ) {

			if( curMp.getString().equals(" ") ) {
				spacing = true;
				continue;
			}

			float lnprPosGExp = getLnprPosGExp(curMp.getString(), curMp.getTagNum());
			float lnprPosGMorp = 0f;
			float lnprPos = 0f;
			if( preMp != null ) {
				if( spacing ) {
					lnprPosGMorp = getLnprPosGMorpInter(preMp.getTagNum(), curMp.getString(), curMp.getTagNum());
				} else {

					float tempLnpr = getLnprMorpsGExp(preMp, curMp);
					if( tempLnpr <= 0 ) {
						lnprPosGExp = tempLnpr - getLnprPosGExp(preMp.getString(), preMp.getTagNum());
						lnprPosGMorp = 0;
						System.out.println("\t\t" + preMp + "+" + curMp + "\t" + tempLnpr);
					} else {
						lnprPosGMorp = getLnprPosGMorpIntra(preMp.getTagNum(), curMp.getString(), curMp.getTagNum());
					}
				}
				lnprPos = getLnprPos(preMp.getTagNum());
			}

			lnpr += lnprPosGExp;
			lnpr += lnprPosGMorp;

			System.out.println("\t" + Util.getTabbedString(curMp.getSmplStr(), 4, 16) + String.format("%10.3f%10s%10.3f%10.3f%10.3f", lnprPosGExp, spacing, lnprPosGMorp, lnprPos, lnpr));

			spacing = false;
			preMp = curMp;
		}

		return lnpr;
	}


	public static void main(String[] args)
	{
		System.out.println(getLnpr("12/집/NNG+13/단지/NNG+15/성/XSN+16/이/JKS"));
		System.out.println(getLnpr("12/집단지성/NNG+16/이/JKS"));
		System.out.println(getLnpr("12/집단/NNG+ +14/지성/NNG+16/이/JKS"));
	}
}

class ProbDicReader
{
	private BufferedReader	br		= null;
	String					line	= null;


	ProbDicReader(String fileName)
		throws UnsupportedEncodingException
	{

		br = new BufferedReader(new InputStreamReader(PDDictionary.class.getResourceAsStream(fileName), "UTF-8"));
	}


	public String[] read()
		throws IOException
	{
		while( (line = br.readLine()) != null ) {
			line = line.trim();
			if( !Util.valid(line) || line.startsWith("//") ) continue;
			return line.split("\t");
		}

		return null;
	}


	public void close()
		throws IOException
	{
		if( br != null ) br.close();
	}
}
