/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2009. 09. 07
 */
package eval;


import java.util.ArrayList;
import java.util.List;

import org.snu.ids.ha.constants.POSTag;
import org.snu.ids.ha.ma.MExpression;
import org.snu.ids.ha.ma.Morpheme;


/**
 * <pre>
 * 
 * </pre>
 * @author 	therocks
 * @since	2009. 09. 07
 */
public class ANSEojeol
	extends ArrayList<Morpheme>
{
	String	exp	= null;


	public ANSEojeol()
	{
		super();
	}


	public ANSEojeol(MExpression me)
	{
		super();
		exp = me.getExp();
		addAll(me.get(0));
	}


	/**
	 * @return the exp
	 */
	public String getExp()
	{
		return exp;
	}


	/**
	 * @param exp the exp to set
	 */
	public void setExp(String exp)
	{
		this.exp = exp;
	}


	void merge()
	{
		List<Morpheme> mpl = new ArrayList<Morpheme>();
		Morpheme mpPrev = null, mp = null;
		for( int i = 0, size = size(); i < size; i++ ) {
			mp = get(i);

			if( mpPrev != null ) {
				// 용언 접두사 결합
				if( mpPrev.isTagOf(POSTag.XPV) ) {
					mpl.remove(mpl.size() - 1);
					mp.setString(mpPrev.getString() + mp.getString());
				}
				// 명사형 접두사 결합
				else if( mpPrev.isTagOf(POSTag.XPN) ) {
					mpl.remove(mpl.size() - 1);
					mp.setString(mpPrev.getString() + mp.getString());
				}
				// 명사형 접미사 결합
				else if( mp.isTagOf(POSTag.XSN) ) {
					mpPrev.setString(mpPrev.getString() + mp.getString());
					mpPrev.setTag(POSTag.NNG);
					continue;
				}
				// 형용사형 접미사 결합
				else if( mp.isTagOf(POSTag.XSA) ) {
					mpPrev.setString(mpPrev.getString() + mp.getString());
					mpPrev.setTag(POSTag.VA);
					continue;
				}
				// 동사형 접미사 결합
				else if( mp.isTagOf(POSTag.XSV) ) {
					mpPrev.setString(mpPrev.getString() + mp.getString());
					mpPrev.setTag(POSTag.VV);
					continue;
				}
			}

			mpPrev = mp;
			mpl.add(mp);
		}
		this.clear();
		this.addAll(mpl);
	}


	public boolean isLastSymbol()
	{
		if( this.get(this.size() - 1).isTagOf(POSTag.S) ) return true;
		return false;
	}


	public boolean isFirstSymbol()
	{
		if( this.get(0).isTagOf(POSTag.S) ) return true;
		return false;
	}


	public int getLastIdx()
	{
		return this.get(0).getIndex() + getExp().length() - 1;
	}


	public int getIdx()
	{
		return this.get(0).getIndex();
	}
}
