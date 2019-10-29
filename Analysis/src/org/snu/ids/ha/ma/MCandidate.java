/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 4
 */
package org.snu.ids.ha.ma;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.snu.ids.ha.constants.Condition;
import org.snu.ids.ha.constants.POSTag;
import org.snu.ids.ha.dic.PDDictionary;
import org.snu.ids.ha.dic.SpacingPDDictionary;
import org.snu.ids.ha.dic.UNPDDictionary;
import org.snu.ids.ha.util.Hangul;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * 표현형에 대한 하나의 형태소 분석 후보를 저장한다.
 * 분석 후보 형태소 목록에 덧붙여 접속 조건, 합성 조건 등의 부가 정보를 저장한다.
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 4
 */
public class MCandidate
	extends MorphemeList
	implements Comparable<MCandidate>
{
	long	atlEnc	= 0;	// 접속 가능한 품사 정보					[A]ppendable[T]ag[L]ist[Enc]oded
	long	hclEnc	= 0;	// 현재 후보가 가진 접속 조건				[H]aving[C]ondition[L]ist[Enc]oded
	long	cclEnc	= 0;	// 접속할 때 확인해야 하는 조건				[C]hecking[C]ondition[L]ist[Enc]oded
	long	eclEnc	= 0;	// 이전에 나오지 말아야 하는 조건			[E]xclusion[C]ondition[L]ist[Enc]oded
	long	bclEnc	= 0;	// 현재 후보가 뒷 후보와 확인해야 하는 조건 	[B]ackwardChecking[C]ondition[L]ist[Enc]oded
	
	/**
	 * <pre>
	 * 후보간의 Scoring을 위해서 완전한 사전어와, 후보사전어를 구분하여 사전어 길이 판단
	 *  - 완전 사전어 : 완전히 한 단어로 인식된 사전어
	 *    -> 예) 체언, 체언과 결합된 조사, 어간과 어미가 완전히 결합된 단어
	 *  - 후보 사전어 : 사전에서 찾아졌지만, 그 완전성이 미비한 것
	 *    -> 예) 체언과 않은 조사, 결합하지 않은 어미, 어간, 체언과 결합하지 않은 서술격 조사(활용 포함)
	 * 점수 계산
	 *  - MCandidate.calculateScore()
	 * 최종적으로 Sorting할 때에는 MExpression.sortFinally()에 의해서 정렬 순서 재조정 됨
	 * </pre>
	 */
	byte	realDicLen		= 0;		// 실재 사전어로 취급할 수 있는 완전어의 길이
	byte	candDicLen		= 0;		// 후보 사전어의 길이
	byte	numOfApndblMC	= 0;		// 접속 가능한 앞뒤 MCandidate의 갯수
	
	/**
	 * 계산된 확률값을 저장하기 위한 필드
	 * @since	2009. 10. 15
	 * @author	therocks
	 */
	MCandidate	prevBestMC			= null;
	int			diclenOfBestMC		= 0;
	float		spacingLnprOfBestMC	= 0;
	float		taggingLnprOfBestMC	= 0;
	/**
	 * <pre>
	 * 분리된 표현형을 저장하기 위함
	 * 띄어쓰기 기준으로 각각의 표현형을 나타내는 문자열을 저장함
	 * 띄어쓰기가 되어야 하는 시점에 공백 문자열을 추가하는 방법으로 띄어쓰기 구현
	 * MCandidate.derive() 함수에서 띄어쓰기 처리
	 * </pre>
	 * @since	2007. 7. 26
	 * @author	therocks
	 */
	private ArrayList<String> expList = null; // 띄어쓰기 된 표층형 리스트
	/**
	 * <pre>
	 * 중복을 피하기 위해서 hashCode를 사용함.
	 * MCandidate.getEncodedString().hashCode()을 사용하여 계산
	 * hashCode를 중복하여 계산하는 것을 피하기 위해서 구성이 변하지 않으면 이미 계산된 hashCode를 사용하도록 함
	 * <중요!!> 새로 계산되어야 하는 시점을 잘 파악하여 MCandidate.calculateHashCode() 함수를 호출해주어야 함!!
	 * </pre>
	 * @since	2007. 7. 26
	 * @author	therocks
	 */
	int hashCode = 0;		// 중복을 피하기 위한 hashCode를 미리 계산해두기 위함
	
	/**
	 * <pre>
	 * 띄어쓰기에 대한 확률값을 저장하기 위한 변수
	 * </pre>
	 * @since	2009. 12. 11
	 * @author	Dongjoo
	 */
	float lnprOfSpacing = 0f;
	/**
	 * <pre>
	 * 내부 품사 결합에 대한 확률값을 저장하기 위한 변수
	 * </pre>
	 * @since	2011. 4. 1.
	 * @author	Dongjoo
	 */
	float lnprOfTagging = 0f;



	/**
	 * <pre>
	 * default constructor 기본 정보들을 설정해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 */
	private MCandidate()
	{
		super();
		expList = new ArrayList<String>();
	}


	/**
	 * <pre>
	 * 확인되지 않은 어휘에 대한 기분석 결과 생성
	 * 미등록 명사에 대한 후보 생성시에만 사용됨~
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param string
	 * @param index
	 */
	MCandidate(String string, int index)
		throws Exception
	{
		this();
		add(new Morpheme(string, index));
		initConds(string);
		setExp(string);
		calcHashCode();
		// 미등록어에 대한 출현확률 계산
		this.lnprOfTagging = UNPDDictionary.getProb(string);
		// 띄어쓰기 확률 계산
		this.calcLnprOfSpacing();
	}


	/**
	 * <pre>
	 * 활용하지 않는 어휘에 대한 기분석 결과 생성
	 * Dictionary.loadFixed()에서만 사용됨
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param string
	 * @param tag
	 * @param compType
	 */
	MCandidate(String string, String tag, String compType)
		throws Exception
	{
		this();
		add(new Morpheme(string, tag, compType));
		initConds(string);
		setExp(string);
		realDicLen = (byte)string.length();
	}


	/**
	 * <pre>
	 * 활용하는 동사, 형용사에 대한 어간 기분석 결과 생성
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 22
	 * @param string
	 * @param tag
	 * @throws Exception
	 */
	public MCandidate(String string, String tag)
		throws Exception
	{
		this();
		add(new Morpheme(string, tag, "S"));
		initConds(string);
		setExp(string);
	}


	/**
	 * <pre>
	 * 한글 이외의 Token정보를 받아들여서, Token을 형태소의 적합한 부분으로 설정해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param token
	 */
	MCandidate(Token token)
		throws Exception
	{
		this();
		if( token.isCharSetOf(CharSetType.HANGUL) ) {
			throw new Exception("Token이 한글입니다.");
		}
		// 미등록어 분석 결과 추가
		add(new Morpheme(token));
		realDicLen = (byte) token.string.length();
		setExp(token.string);
		initConds();
	}


	/**
	 * <pre>
	 * 해당 표층어와 형태가 동일하고, 분석 결과의 길이가 1인 경우 첫번째 형태소의 품사를 반환
	 * </pre>
	 * @author	therocks
	 * @since	2009. 08. 07
	 * @param exp
	 * @return the tag
	 */
	public String getTag()
	{
		if( size() == 1 ) {
			return get(0).getTag();
		}
		return null;
	}
	
	
	/**
	 * <pre>
	 * 분석 후보가 하나의 형태소이고, 해당 형태소가 주어진 태그 집합중에 하나인지 확인하여 반환
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 15
	 * @param tags
	 * @return 
	 */
	public boolean isTagOf(long tags)
	{
		if( size() == 1 ) {
			return get(0).isTagOf(tags);
		}
		return false;
	}


	public String getATL()
	{
		return POSTag.getTagStr(atlEnc);
	}


	public long getATLEnc()
	{
		return atlEnc;
	}


	public String getHCL()
	{
		return Condition.getCondStr(hclEnc);
	}


	public long getHCLEnc()
	{
		return hclEnc;
	}


	public String getCCL()
	{
		return Condition.getCondStr(cclEnc);
	}


	public long getCCLEnc()
	{
		return cclEnc;
	}


	public String getECL()
	{
		return Condition.getCondStr(eclEnc);
	}


	public long getECLEnc()
	{
		return eclEnc;
	}


	/**
	 * <pre>
	 * 자모 조건을 생성해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 */
	public void initConds(String string)
	{
		// atlEnc: 품사별 기본 접속 가능 품사 초기화	
		addApndblTag(getBasicApndblTags());

		initHavingCond(string);
	}


	/**
	 * <pre>
	 * 품사 및 음운 조건을 초기화 한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 19
	 * @param string
	 */
	public void initHavingCond(String string)
	{
		// hclEnc: 음운 정보 초기화
		addHavingCond(getBasicPhonemeConds(string));

		// hclEnc: 품사별 기본 수반 조건 초기화		
		addHavingCond(getBasicHavingConds());
	}


	/**
	 * <pre>
	 * 특수문자 영문자에 대한 음운 정보 및 선호 조건 정보 설정
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 26
	 */
	private void initConds()
	{
		if( !lastMorp.isCharSetOf(CharSetType.HANGUL) ) {
			if( lastMorp.isCharSetOf(CharSetType.ENGLISH) ) {
				addHavingCond(Condition.ENG);
			}
			addHavingCond(Condition.SET_FOR_UN);
			addHavingCond(Condition.N);
		}
	}


	/**
	 * <pre>
	 * 각 품사별로 띄어쓰기 없이 이전에 올 수 있는 품사를 설정해줌.  
	 * </pre>
	 * @author	therocks
	 * @since	2009. 09. 30
	 * @return the basic appendable tag with encoding
	 */
	public long getBasicApndblTags()
	{
		long tags = 0;

		// 체언은 접두어를 가질 수 있음.
		if( firstMorp.isTagOf(POSTag.NNA | POSTag.XR) ) {
			tags |= POSTag.XPN;
			// 보통 명사는 복합어를 만들어 낼 수 있음.
			// if( firstMorp.isTag(POSTag.NNG) ) tags |= POSTag.NNG;
		}
		// 용언(어근 포함)은 용언 접두어를 가질 수 있음
		else if( firstMorp.isTagOf(POSTag.VV | POSTag.VA | POSTag.XR) ) {
			tags |= POSTag.XPV;
		}
		// 명사형 접미사는 명사와 접속 가능
		else if( firstMorp.isTagOf(POSTag.XSN) ) {
			tags |= POSTag.NNA | POSTag.UN;
		} 
		// 용언 접미사는 어근과 결합 가능
		else if( firstMorp.isTagOf(POSTag.XSA | POSTag.XSV) ) {
			tags |= POSTag.NN | POSTag.XR | POSTag.MAG;
		}
		// 단위 의존 명사는 수사와 붙여서 쓰는 것 용인
		else if( firstMorp.isTagOf(POSTag.NNM | POSTag.NR) ) {
			tags |= POSTag.NR;
		}
		// 외국어에 조사 붙여 쓰일 수 있음.
		else if( firstMorp.isTagOf(POSTag.J) ) {
			tags |= POSTag.O | POSTag.NR;
		}

		return tags;
	}


	/**
	 * <pre>
	 * 기본 음운 정보 조건을 encoding하여 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 09. 30
	 * @param string
	 * @return
	 */
	public long getBasicPhonemeConds(String string)
	{
		long cond = 0;
		char lastCh = string.charAt(string.length() - 1);
		Hangul lastHg = Hangul.split(lastCh);

		// 자음 조건 설정
		if( lastHg.hasJong() ) {
			cond |= Condition.JAEUM;
		} else {
			cond |= Condition.MOEUM;
		}

		if( Hangul.MO_POSITIVE_SET.contains(lastHg.jung) ) {
			cond |= Condition.YANGSEONG;
		} else {
			cond |= Condition.EUMSEONG;
		}
		

		// 동사, 형용사에 대한 추가 설정
		if( lastMorp.isTagOf(POSTag.VP) ) {
			// 동사, 형용사의 겹모음은 었을 붙여주기 위해 자음 조건 추가해줌
			if( Hangul.MO_DOUBLE_SET.contains(lastHg.jung) ) {
				cond |= Condition.JAEUM;
			}
			// '하다'동사에 대한 처리
			if( lastCh == '하' ) {
				cond |= Condition.HA;
			}
			// '가다'동사에 대한 처리
			else if( lastCh == '가' ) {
				cond |= Condition.GADA;
			}
			// '오다'동사에 대한 처리
			else if( lastCh == '오' ) {
				cond |= Condition.ODA;
			}
			// 'ㄹ'받침 용언 설정
			else if( lastHg.jong == 'ㄹ' ) {
				cond |= Condition.LIEUL;
			}
		}
		// 체언 조건 설정
		else if( lastMorp.isTagOf(POSTag.N) ) {
			// 'ㄹ'받침 용언 설정
			if( lastHg.jong == 'ㄹ' ) {
				cond |= Condition.LIEUL;
			}
		}
		// 어미에 대한 추가 설정
		else if( lastMorp.isTagOf(POSTag.ET) ) {
			if( lastMorp.string.equals("ㄴ") ) {
				cond |= Condition.NIEUN;
			} else if( lastMorp.string.equals("ㄹ") ) {
				cond |= Condition.LIEUL;
			} else if( lastMorp.string.equals("ㅁ") ) {
				cond |= Condition.MIEUM;
			}
		}

		return cond;
	}
	
	
	/**
	 * <pre>
	 * 각 품사별로 가지는 기본적인 조건 반환
	 * </pre>
	 * @author	therocks
	 * @since	2009. 09. 30
	 * @return
	 */
	public long getBasicHavingConds()
	{
		long cond = 0;
		// 체언
		if( lastMorp.isTagOf(POSTag.N | POSTag.ETN) ) {
			// 조사와 접속시의 정보를 설정해주어야 함
			cond |= Condition.N;
		}
		// 관형어
		else if( lastMorp.isTagOf(POSTag.MD | POSTag.ETD) ) {
			cond |= Condition.D;
		}
		// 부사어, 부사격 조사
		else if( lastMorp.isTagOf(POSTag.MA | POSTag.JKM ) ) {
			cond |= Condition.A;
		}
		// 동사, 형용사
		// 연결 어미
		else if( lastMorp.isTagOf(POSTag.ECS | POSTag.ECD) ) {
			cond |= Condition.EC;
		}

		return cond;
	}


	/**
	 * <pre>
	 * 복사본을 만들어서 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 5
	 * @return
	 */
	public MCandidate copy()
	{
		MCandidate clone = new MCandidate();
		clone.addAll(this);
		clone.expList.addAll(this.expList);
		clone.atlEnc = this.atlEnc;
		clone.hclEnc = this.hclEnc;
		clone.cclEnc = this.cclEnc;
		clone.bclEnc = this.bclEnc;
		clone.eclEnc = this.eclEnc;
		clone.candDicLen = this.candDicLen;
		clone.realDicLen = this.realDicLen;
		clone.numOfApndblMC = this.numOfApndblMC;
		clone.spacingLnprOfBestMC = this.spacingLnprOfBestMC;
		clone.prevBestMC = this.prevBestMC;
		clone.lnprOfSpacing = this.lnprOfSpacing;
		clone.lnprOfTagging = this.lnprOfTagging;
		clone.hashCode = this.hashCode;
		return clone;
	}


	/**
	 * <pre>
	 * 각 후보들의 분석 결과에 대한 index(offset)정보를 설정해준다.
	 * 분석 결과가 실제 문장보다 길어질 수 있으므로 offset정보는 정확히 일치하지는 않을 수도 있다.
	 * 예를 들어 준말의 경우 원형으로 복원되기 때문에 길어진다.
	 * "하0/길1/ 2/바3/란4/다5/" -> 하0/+기1/+를2/ 3/바라4/+ㄴ다6/
	 * </pre>
	 * @author	therocks
	 * @since	2008. 03. 31
	 * @param index
	 */
	public void setIndex(int index)
	{
		Morpheme mp = null;
		int offset = 0;
		for( int i = 0, size = size(); i < size; i++ ) {
			mp = get(i);
			mp.setIndex(index + offset);
			offset += mp.string.length();
		}
	}


	/**
	 * <pre>
	 * 형태소 정보를 모두 붙여준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param mpList
	 */
	public void addAll(MorphemeList mpList)
	{
		for( int i = 0, stop = mpList.size(); i < stop; i++ ) {
			add(mpList.get(i).copy());
		}
	}


	/**
	 * <pre>
	 * 접속 가능한 품사정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param tag
	 */
	public void addApndblTag(String tag)
	{
		addApndblTag(POSTag.getTagNum(tag));
	}


	/**
	 * <pre>
	 * 접속 가능한 품사정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param tagNum
	 */
	public void addApndblTag(long tagNum)
	{
		atlEnc |= tagNum;
	}


	/**
	 * <pre>
	 * 접속 가능한 품사정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param tags
	 */
	public void addApndblTags(String[] tags)
	{
		for( int i = 0, stop = tags.length; i < stop; i++ ) {
			addApndblTag(tags[i]);
		}
	}


	/**
	 * <pre>
	 * 기분석 결과가 가지는 접속 조건을 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param cond
	 */
	public void addHavingCond(String cond)
	{
		addHavingCond(Condition.getCondNum(cond));
	}
	
	
	/**
	 * <pre>
	 * 기분석 결과가 가지는 접속 조건을 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param conds
	 */
	public void addHavingConds(String[] conds)
	{
		for( int i = 0, stop = conds.length; i < stop; i++ ) {
			addHavingCond(conds[i]);
		}
	}


	/**
	 * <pre>
	 * 기분석 결과가 가지는 접속 조건을 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param condNum
	 */
	public void addHavingCond(long condNum)
	{
		hclEnc |= condNum;
		if( lastMorp.isTag(POSTag.ETD) && Condition.checkAnd(this.hclEnc, Condition.NIEUN) )
			bclEnc |= Condition.NIEUN;
		else if( lastMorp.isTag(POSTag.ETD) && Condition.checkAnd(this.hclEnc, Condition.LIEUL) )
			bclEnc |= Condition.LIEUL;
		else if( lastMorp.isTagOf(POSTag.ETN) && Condition.checkAnd(this.hclEnc, Condition.MIEUM) )
			bclEnc |= Condition.MIEUM;
		else if( lastMorp.isTagOf(POSTag.V) ) {
			// 'ㄹ', 'ㅎ' 탈락에 대한 후위 조건 확인
			bclEnc |= (this.hclEnc & Condition.MINUS_JA_SET);
			// 'ㅂ' 추가에 의한 후위 조건 확인
			if( Condition.checkAnd(hclEnc, Condition.BIEUB) && !Hangul.endsWith(lastMorp.string, "ㅂ")) bclEnc |= Condition.BIEUB;
		}
	}


	/**
	 * <pre>
	 * 주어진 조건을 가지고 있는지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param condNum
	 * @return
	 */
	public boolean isHavingCond(long condNum)
	{
		return Condition.checkAnd(hclEnc, condNum);
	}


	/**
	 * <pre>
	 * 후보 기분석 결과가 가진 조건 정보를 삭제한다.
	 * @since 2009. 10. 15
	 * 동시에 뒷결합 조건도 삭제한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 */
	public void clearHavingCondition()
	{
		this.hclEnc = 0l;
		this.bclEnc = 0l;
	}


	/**
	 * <pre>
	 * 기분석 결과가 접속시 확인해야하는 조건 정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param cond
	 */
	public void addChkCond(String cond)
	{
		cclEnc |= Condition.getCondNum(cond);
	}


	/**
	 * <pre>
	 * 기분석 결과가 접속시 확인해야하는 조건 정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param conds
	 */
	void addChkConds(String[] conds)
	{
		for( int i = 0, stop = conds.length; i < stop; i++ ) {
			addChkCond(conds[i]);
		}
	}


	/**
	 * <pre>
	 * 접속 배제 조건을 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 15
	 * @param cond
	 */
	void addExclusionCond(String cond)
	{
		eclEnc |= Condition.getCondNum(cond);
	}


	/**
	 * <pre>
	 * 배열로 주어진 배제 조건을 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 15
	 * @param conds
	 */
	void addExclusionConds(String[] conds)
	{
		for( int i = 0, stop = conds.length; i < stop; i++ ) {
			addExclusionCond(conds[i]);
		}
	}
	
	
	/**
	 * <pre>
	 * 배제 조건에 해당하여 접속이 불가능한지 확인한다.
	 * 하나라도 조건을 만족하면 배제해야하는 것으로 간주.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 14
	 * @param exlCondEnc
	 * @return
	 */
	private boolean isCondExclusive(long exlCondEnc)
	{
		if( exlCondEnc == 0 ) return false;
		return Condition.checkOr(hclEnc, exlCondEnc);
	}


	/**
	 * <pre>
	 * 표현형을 설정해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 20
	 * @param exp
	 */
	public void setExp(String exp)
	{
		expList.clear();
		expList.add(exp);
	}


	/**
	 * <pre>
	 * 띄어쓰기 된 표층어를 반환
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 20
	 * @return
	 */
	public String getExp()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = expList.size(); i < stop; i++ ) {
			if( i > 0 ) sb.append(" ");
			sb.append(expList.get(i));
		}
		return sb.toString();
	}


	public int getSpaceCnt()
	{
		return expList.size() - 1;
	}


	public char getFirstSyllable()
	{
		String str = expList.get(0);
		return str.charAt(0);
	}


	public char getLastSyllable()
	{
		String str = expList.get(expList.size() - 1);
		return str.charAt(str.length() - 1);
	}


	/**
	 * <pre>
	 * toIdx에 해당하는 순서까지의 표현형을 가져온다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @param toIdx
	 * @return
	 */
	String getExp(int toIdx)
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = Math.min(expList.size(), toIdx + 1); i < stop; i++ ) {
			sb.append(expList.get(i));
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * 주어진 문자열로 띄어쓰기가 된 표층형의 앞부분(head)을 반환해줌.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2011. 4. 21.
	 * @param head
	 * @return
	 */
	String getHead(String head)
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = expList.size(); i < stop; i++ ) {
			sb.append(expList.get(i));
			if( sb.toString().equals(head) ) return head;
		}
		return null;
	}


	/**
	 * <pre>
	 * 띄어쓰기를 /로 연결해서 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 * @return
	 */
	String geExpStrWithSpace()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = expList.size(); i < stop; i++ ) {
			if( i > 0 ) sb.append(" ");
			sb.append(expList.get(i));
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * 접속 가능한 조건인지 확인
	 * 1) lastMorp.isTagOf(mcToAppend.atlEnc)
	 *    뒷 분석 후보의 접속 가능 품사로 끝나는지 확인
	 * 2) isCondApndbl(mcToAppend.cclEnc)
	 *    뒷 분석 후보가 확인해야하는 조건이 있을 때, 이를 만족하는지 확인
	 * 3) !isCondExclusive(mcToAppend.eclEnc)
	 *    뒷 분석 후보가 배제 조건을 가질 때, 이를 하나라도 만족하는지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 27
	 * @param mcToAppend
	 * @return
	 */
	public boolean isApndbl(MCandidate mcToAppend)
	{
		boolean ret = !isHavingCond(Condition.F);
		if( ret ) ret = lastMorp.isTagOf(mcToAppend.atlEnc);
		if( ret ) ret = Condition.checkAnd(hclEnc, mcToAppend.cclEnc);
		if( ret ) ret = !isCondExclusive(mcToAppend.eclEnc);
		// 어간의 변형을 동반한 활용에 의한 어미와의 결합을 확인 
		if( ret && mcToAppend.firstMorp.isTagOf(POSTag.E) ) {
			ret = Condition.checkAnd(mcToAppend.cclEnc, bclEnc);
		}
		return ret;
	}


	/**
	 * <pre>
	 * 띄어쓰기가 되었을 때 연결이 가능한지 확인
	 * 마지막이 활용의 시작이면, 다음은 반드시 선어말 혹은 어말 어미가 와야 함!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param mcToAppend
	 * @return
	 */
	boolean isApndblWithSpace(MCandidate mcToAppend)
	{
		// 띄어쓰기 불가능 확인
		if(
				// 어간, 선어말 어미, 접두사의 경우 뒷부분에 띄어쓰기를 추가하지 못함.
				lastMorp.isTagOf(POSTag.V | POSTag.EP | POSTag.XP)
				// 어미, 조사, 접미사,  
				|| mcToAppend.firstMorp.isTagOf(POSTag.E | POSTag.XS | POSTag.VCP | POSTag.J) 
				// 생략되어서 앞말과 이어져야 하는 경우
				|| mcToAppend.isHavingCond(Condition.SHORTEN) )
		{
			return false;
		}

		return true;
	}


	/**
	 * <pre>
	 * 현재의 후보에 부착할 후보를 생성하여 반환해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param mcToAppend
	 * @return
	 */
	public MCandidate derive(MCandidate mcToAppend)
	{
		// 접속 가능 확인
		boolean isApndbl = this.isApndbl(mcToAppend);
		
		if( !isApndbl ) return null;
		
		// 영문 + 조사로 결합될 때에는 붙여 쓰여있던 경우에만 결합하도록 함.
		if( isApndbl && this.lastMorp.isCharSetOf(CharSetType.ENGLISH) ) {
			if( this.lastMorp.index + this.lastMorp.string.length() != mcToAppend.firstMorp.index ) return null;
		}
		
		// 띄어쓰기 하여 접속 가능한지 확인
		boolean isApndblWithSpace = this.isApndblWithSpace(mcToAppend);

		// 접속 가능하지 않고, 띄어쓰기 오류도 아니면 후보 생성 안함
		if( !isApndbl && !isApndblWithSpace ) return null;
		

		// 분석 후보 생성
		MCandidate mcNew = new MCandidate();
		mcNew.addAll(this);
		mcNew.addAll(mcToAppend);
		mcNew.expList.addAll(this.expList);
		mcNew.atlEnc = this.atlEnc;
		mcNew.hclEnc = mcToAppend.hclEnc;
		mcNew.bclEnc = mcToAppend.bclEnc;
		mcNew.cclEnc = this.cclEnc;
		mcNew.eclEnc = this.eclEnc;

		// 접속 불가능이면, 띄어쓰기 추가
		if( !isApndbl ) {
			// 띄어쓰기, 어절 표현형 구분
			mcNew.add(this.size(), new MorphemeSpace(mcToAppend.atlEnc, this.hclEnc, this.bclEnc, mcToAppend.cclEnc, mcToAppend.eclEnc));
			mcNew.expList.add("");
		}
		
		
		// 띄어쓰기가 처리된 표층어 정리
		// 띄어쓰기가 되었다면 "" 이 추가되었기 때문에 띄어쓰기 처리된 표현형으로 추가된다.
		mcNew.expList.add(
				// 이전 표층어에
				mcNew.expList.remove(mcNew.expList.size() - 1)
				// 바로 다음 표층어를 붙여서
				+ mcToAppend.expList.get(0));
		
		// 나머지 표층어 추가
		mcNew.expList.addAll(mcToAppend.expList.subList(1, mcToAppend.expList.size()));


		if( isApndbl ) {
			// 결합된 위치의 품사가 결합 가능한 품사만 남도록 설정
			mcNew.get(size() - 1).infoEnc &= (0x8000000000000000l | mcToAppend.atlEnc);

			// 어말어미 + 어말어미인 경우에는 두 어말 어미를 합쳐준다.
			if( this.lastMorp.isTagOf(POSTag.EM) && mcToAppend.firstMorp.isTagOf(POSTag.EM) ) {
				mcNew.mergeAt(size() - 1);
			}
			// 부사화 접미사가 합쳐지면 부사로 만들어줌
			else if( mcToAppend.firstMorp.isTagOf(POSTag.XSM) ) {
				mcNew.mergeAt(size() - 1);
			}
		}

		// 띄어쓰기에 의한 확률 처리
		float lnpr = SpacingPDDictionary.getProb(this.getLastSyllable(), mcToAppend.getFirstSyllable(), !isApndbl);

		mcNew.setLnprOfSpacing(this.lnprOfSpacing + mcToAppend.lnprOfSpacing + lnpr);
		mcNew.calcLnprOfTagging();

		// 새로 만들어진 후보의 형태소 인접 확률 계산
		mcNew.calcLnprOfTagging();
//		lnpr = this.lnprOfTagging + mcToAppend.lnprOfTagging;
//		lnpr -= PDDictionary.getProbUni(this.lastMorp.string, this.lastMorp.getTagNum());
//		// 형태소 인접 확률 (어절간)
//		if( isApndbl ) {
//			lnpr += PDDictionary.getProbInterBi(mcToAppend.firstMorp.getTagNum(), mcToAppend.firstMorp.string, this.lastMorp.getTagNum());
//		}
//		// 형태소 인접 확률 (어절내)
//		else {
//			lnpr += PDDictionary.getProbIntraBi(mcToAppend.firstMorp.getTagNum(), mcToAppend.firstMorp.string, this.lastMorp.getTagNum());
//		}
//		mcNew.lnprOfTagging = lnpr;

		// 새로 만들어진 후보의 사전 어휘 길이 계산
		mcNew.calcDicLen();

		return mcNew;
	}


	/**
	 * <pre>
	 * 후보 분석 결과를 띄어쓰기를 기준으로 분리해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 20
	 * @return
	 */
	List<MCandidate> split()
	{
		// 첫번째가 공백으로 시작하면 삭제해준다.
		if( this.get(0) instanceof MorphemeSpace ) {
			expList.remove(0);
			remove(0);
		}

		ArrayList<MCandidate> ret = new ArrayList<MCandidate>();
		MCandidate mc = new MCandidate();
		mc.atlEnc = atlEnc;
		mc.cclEnc = cclEnc;
		mc.eclEnc = eclEnc;
		Morpheme mp = null;
		int expIdx = 0;
		for( int i = 0, stop = size(); i < stop; i++ ) {
			mp = get(i);
			if( mp instanceof MorphemeSpace ) {
				if( i == 0 ) continue;
				mc.setExp(expList.get(expIdx));
				MorphemeSpace mps = ((MorphemeSpace)mp);
				mc.hclEnc = mps.hclEnc;
				mc.bclEnc = mps.bclEnc;
				mc.calcDicLen();
				mc.calcLnprOfSpacing();
				mc.calcLnprOfTagging();
				expIdx++;
				ret.add(mc);
				
				mc = new MCandidate();
				mc.atlEnc = mps.atlEnc;
				mc.cclEnc = mps.cclEnc;
				mc.eclEnc = mps.eclEnc;
			} else {
				mc.add(mp);
			}
		}
		mc.setExp(expList.get(expIdx));
		mc.hclEnc = hclEnc;
		mc.bclEnc = bclEnc;
		mc.calcDicLen();
		mc.calcLnprOfSpacing();
		mc.calcLnprOfTagging();
		ret.add(mc);
		return ret;
	}


	/**
	 * <pre>
	 * head tail 문자를 받아들여서 해당 head, tail로 잘라질 수 있는 위치를 찾아서,
	 * head, tail로 잘라서 반환해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @param headStr
	 * @param headIdx
	 * @param tailStr
	 * @param tailIdx
	 * @return
	 * @throws Exception
	 */
	MCandidate[] divideHeadTailAt(String headStr, int headIdx, String tailStr, int tailIdx)
		throws Exception
	{
		int divideIdx = 0;
		boolean dividable = false;
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = expList.size(); i < stop; i++ ) {
			sb.append(expList.get(i));
			if( sb.toString().equals(headStr) ) {
				dividable = true;
				break;
			}
			divideIdx++;
		}
		if( !dividable ) {
			return new MCandidate[] { new MCandidate(headStr, headIdx), new MCandidate(tailStr, tailIdx) };
		}
		
		
		MCandidate[] ret = new MCandidate[2];

		MCandidate headMC = ret[0] = new MCandidate();
		MCandidate tailMC = ret[1] = new MCandidate();

		// head 생성
		headMC.atlEnc = atlEnc;
		headMC.cclEnc = cclEnc;
		headMC.eclEnc = eclEnc;

		int spaceIdx = 0;
		int idx = 0, stop = size(), accIdx = 0;
		for( ; idx < stop; idx++ ) {
			Morpheme mp = get(idx);
			if( mp instanceof MorphemeSpace ) {
				if( spaceIdx < divideIdx ) {
					headMC.add(mp);
					spaceIdx++;
					continue;
				}
				for( int j = 0, jStop = divideIdx + 1; j < jStop; j++ ) {
					headMC.expList.add(expList.get(j));
				}
				
				MorphemeSpace mps = ((MorphemeSpace) mp);
				headMC.hclEnc = mps.hclEnc;
				headMC.bclEnc = mps.bclEnc;

				// tail 생성
				tailMC.atlEnc = mps.atlEnc;
				tailMC.hclEnc = hclEnc;
				tailMC.bclEnc = bclEnc;
				tailMC.cclEnc = mps.bclEnc;
				tailMC.eclEnc = mps.eclEnc;
				idx++;
				break;
			}
			mp.setIndex(headIdx + accIdx);
			accIdx += mp.getString().length();
			headMC.add(mp);
		}

		// 나머지 분석 결과 삽입
		if( idx < stop ) {
			for( ; idx < stop; idx++ ) {
				tailMC.add(get(idx));
			}
			// 표현형 추가
			for( int i = divideIdx + 1, iStop = expList.size(); i < iStop; i++ ) {
				tailMC.expList.add(expList.get(i));
			}
		}
		headMC.calcDicLen();
		headMC.calcLnprOfSpacing();
		headMC.calcLnprOfTagging();
		tailMC.calcDicLen();
		tailMC.calcLnprOfSpacing();
		tailMC.calcLnprOfTagging();

		return ret;
	}



	/**
	 * <pre>
	 * idx번째 띄어쓰기 다음에 오는 형태소가 미등록어인지 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @param idx
	 * @return
	 */
	boolean isUNBfrOrAftrIthSpace(int idx)
	{
		int spaceIdx = 0;
		for( int i = 0, stop = size() - 1; i < stop; i++ ) {
			Morpheme mp = get(i);
			if( mp instanceof MorphemeSpace ) {
				if( spaceIdx != idx ) {
					spaceIdx++;
					continue;
				}
				mp = get(i + 1);
				return get(i + 1).isTag(POSTag.UN)
					|| get(i - 1).isTag(POSTag.UN);
			}
		}
		return false;
	}


	/**
	 * <pre>
	 * 현재 분석 후보의 hashCode를 반환한다.
	 * hashCode는 calculateDicLen() 호출시에 생성되는 문자열을 바탕으로 한번만 계산한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @return Hash code of the object
	 */
	public int hashCode()
	{
		return hashCode;
	}


	/**
	 * <pre>
	 * hashCode를 계산한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 */
	public void calcHashCode()
	{
		hashCode = getEncStr().hashCode();
	}


	/**
	 * <pre>
	 * 기분석 결과가 동일한지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param obj
	 * @return
	 */
	public boolean equals(Object obj)
	{
		return hashCode() == obj.hashCode();
	}


	/**
	 * <pre>
	 * 사전에 나온 어휘의 길이가 길수록 더 적합한 것이라고 판단하여
	 * Sorting할 때 사용함!!
	 * 분석 어휘의 수가 적은 것일 수록 유리하도록 설정!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param arg0
	 * @return
	 */
	public int compareTo(MCandidate comp)
	{
		if( this.getDicLenWithCand() != comp.getDicLenWithCand() ) {
			return comp.getDicLenWithCand() - getDicLenWithCand();
		}

		if( getLnpr() > comp.getLnpr() ) {
			return -1;
		} else if( getLnpr() < comp.getLnpr() ) {
			return 1;
		}
		return 0;
	}
	
	
	public float getLnpr()
	{
		return lnprOfSpacing + lnprOfTagging;
	}


	/**
	 * <pre>
	 * 사전 어휘 길이를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 21
	 * @return
	 */
	int getDicLenOnlyReal()
	{
		return this.realDicLen;
	}


	/**
	 * <pre>
	 * 종결되지 않은 것까지 사전 어휘로 고려하여 사전 어휘 길이를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 21
	 * @return
	 */
	int getDicLenWithCand()
	{
		return this.candDicLen + this.realDicLen;
	}


	/**
	 * <pre>
	 * 후보 사전어의 길이를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @return
	 */
	int getDicLenOnlyCand()
	{
		return this.candDicLen;
	}


	/**
	 * <pre>
	 * 시작이나 끝이 불완전한 품사이면 candDicLen을 가지고, 이로 인해 불완전한 후보로 인식
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 21
	 * @return
	 */
	boolean isComplete()
		throws Exception
	{
		return candDicLen == 0;
	}


	/**
	 * <pre>
	 * 사전어와 비사전어의 길이를 계산한다.
	 * 오버헤드를 유발하는 함수이므로 꼭 필요한 경우에만 호출한다.
	 * derive() 다음에만 호출한다!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 23
	 */
	private void calcDicLen()
	{
		// 형태소 수 설정
		byte size = (byte) size();

		// 사전 길이 초기화
		realDicLen = 0;
		candDicLen = 0;
		
		int expIdx = 0;
		int nrDicLen = 0;
		boolean hasPreWord = false, hasJo = false;
		boolean hasStem = false, hasEP = false, hasEM = false;

		Morpheme mp = null;
		for( int i = 0, stop = size + 1; i < stop; i++ ) {
			if( i < size )
				mp = get(i);
			else
				mp = null;
			
			if( mp == null || mp instanceof MorphemeSpace ) {
				// [어간(+선어말어미)+어미] 완료 확인
				boolean complete = !hasEP || !(hasStem ^ hasEM);
				// [체언(관형사,부사)+조사] 완료 확인
				complete = complete && (!hasJo || hasPreWord);

				// 사전어, 비사전어 설정
				// 줄임말인 경우에는 줄임말 앞부분의 완료 여부에 따라서 완료 여부 설정
				if( complete ) {
					realDicLen += expList.get(expIdx).length() - nrDicLen;
				} else {
					candDicLen += expList.get(expIdx).length() - nrDicLen;
				}

				if( mp == null ) {
					// 미등록어 + 조사인 경우는 미등록어를 후보 사전어로 취급할 수 있도록 한다.
					// 사전어 길이에서 1을 빼줌으로 다른 완전히 사전어보다는 우선순위를 낮추어준다.
					if( size == 2 && lastMorp.isTagOf(POSTag.J) && firstMorp.isTag(POSTag.UN) ) {
						candDicLen += nrDicLen - 1;
					}
				}

				// 다음을 위해 초기화
				hasPreWord = false;
				hasJo = false;
				hasStem = false;
				hasEP = false;
				hasEM = false;
				nrDicLen = 0;

				// 표제어 인덱스 증가
				expIdx++;
			}
			// 띄어쓰기를 기준으로 표현형에 대한 사전어 여부 처리
			else {
				// 어간존재 확인
				if( mp.isTagOf(POSTag.V) ) {
					hasStem = true;
					hasPreWord = true;
					// 서술격 조사는 어간이며, 조사로 처리
					if( mp.isTag(POSTag.VCP) ) hasJo = true;
				}
				// 선어말 어미 존재 확인
				else if( mp.isTagOf(POSTag.EP) ) {
					hasEP = true;
					hasPreWord = true;
				}
				// 어미 존재 확인
				else if( mp.isTagOf(POSTag.EM) ) {
					hasEM = true;
					hasPreWord = true;
					// 명사형으로 종결이면 조사 앞말 출현으로 설정
					if( mp.isTag(POSTag.ETN) ) hasPreWord = true;
				}
				// 조사 설정
				else if( mp.isTagOf(POSTag.J) ) {
					hasJo = true;
				}
				// 미등록어인 경우
				else if( mp.isTag(POSTag.UN) ) {
					hasPreWord = true;
					nrDicLen += mp.string.length();
				}
				// 앞말 존재로 설정
				else {
					hasPreWord = true;
				}
			}
		}

		// hashCode 계산
		calcHashCode();
	}
	
	
	/**
	 * 띄어쓰기 없이 접속 가능한 품사 정보
	 */
	public static final String	DLMT_ATL	= "#";
	/**
	 * 현재 후보가 가지는 조건 정보
	 */
	public static final String	DLMT_HCL	= "&";
	/**
	 * 접속할 때 확인해야하는 조건 정보 뒤로 맞출 때 확인해야함.
	 */
	public static final String	DLMT_BCL	= "~";
	/**
	 * 접속할 때 확인해야하는 조건 정보
	 */
	public static final String	DLMT_CCL	= "@";
	/**
	 * 접합 배재 조건 정보
	 */
	public static final String	DLMT_ECL	= "￢";
	/**
	 * 띄어쓰기 시 이전에 나오는 품사 정보
	 */
	public static final String	DLMT_PCL	= "%";
	/**
	 * 복합명사 목록
	 */
	public static final String	DLMT_CNL	= "$";


	/**
	 * <pre>
	 * 사전의 후보 분석 결과 문자열로부터 객체를 생성한다.
	 * 공백에 대한 처리를 추가하기 위하여 수정 2007-07-19
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param exp
	 * @param source
	 */
	public static MCandidate create(String exp, String source)
	{
		MCandidate mCandidate = new MCandidate();
		mCandidate.setExp(exp);
		StringTokenizer st = new StringTokenizer(source, "[]", false);

		// 기분석 결과 저장
		String token = null, infos = "";
		String[] arr = null;
		for( int i = 0; st.hasMoreTokens(); i++ ) {
			token = st.nextToken();
			if( i == 0 ) {
				arr = token.split("\\+");
				for( int j = 0; j < arr.length; j++ ) {
					// 앞 뒤조건 정보들을 가지는 공백 문자열 생성
					if( arr[j].startsWith(" ") ) {
						mCandidate.add(new MorphemeSpace(arr[j]));
						mCandidate.expList.add(0, "");
					}
					// 일반적인 형태소 분석 결과 저장
					else {
						mCandidate.add(Morpheme.create(arr[j]));
					}
				}
			} else {
				infos = token;
			}
		}


		// 부가 정보들에 대한 처리 수행
		st = new StringTokenizer(infos, "*" + DLMT_ATL + DLMT_BCL + DLMT_HCL + DLMT_CCL + DLMT_ECL + DLMT_PCL, true);
		while(st.hasMoreTokens()) {
			token = st.nextToken();
			// 접속 가능한 품사 정보
			if(token.equals(DLMT_ATL)) {
				token = st.nextToken().trim();
				token = token.substring(1, token.length() - 1);
				mCandidate.addApndblTags(token.split(","));
			}
			// 현재 후보가 가진 접속 조건
			else if(token.equals(DLMT_HCL)) {
				token = st.nextToken().trim();
				token = token.substring(1, token.length() - 1);
				mCandidate.addHavingConds(token.split(","));
			}
			// 접속 확인 조건
			else if(token.equals(DLMT_CCL)) {
				token = st.nextToken().trim();
				token = token.substring(1, token.length() - 1);
				mCandidate.addChkConds(token.split(","));
			}
			// 접속 배제 조건
			else if(token.equals(DLMT_ECL)) {
				token = st.nextToken().trim();
				token = token.substring(1, token.length() - 1);
				mCandidate.addExclusionConds(token.split(","));
			}
		}
		mCandidate.initConds(exp);
		mCandidate.calcDicLen();
		return mCandidate;
	}


	/**
	 * <pre>
	 * 분석 후보에 대한 조건을 받아들여서 생성
	 * </pre>
	 * @author	therocks
	 * @since	2009. 09. 30
	 * @param exp
	 * @param analResult
	 * @param atl
	 * @param hcl
	 * @param ccl
	 * @param ecl
	 * @param pcl
	 * @return
	 */
	public static MCandidate create(String exp, String analResult, String atl, String hcl, String ccl, String ecl)
	{
		MCandidate mCandidate = new MCandidate();
		mCandidate.setExp(exp);

		// 기분석 결과 저장
		String[] arr = analResult.split("\\+");
		for( int j = 0; j < arr.length; j++ ) {
			// 앞 뒤조건 정보들을 가지는 공백 문자열 생성
			if( arr[j].startsWith(" ") ) {
				mCandidate.add(new MorphemeSpace(arr[j]));
				mCandidate.expList.add(0, "");
			}
			// 일반적인 형태소 분석 결과 저장
			else {
				mCandidate.add(Morpheme.create(arr[j]));
			}
		}
		// 조건 초기화
		mCandidate.initConds(exp);
		mCandidate.calcDicLen();

		// 접속 가능한 품사 정보
		if( Util.valid(atl) ) mCandidate.addApndblTags(atl.split(","));

		// 현재 후보가 가진 접속 조건
		if( Util.valid(hcl) ) mCandidate.addHavingConds(hcl.split(","));

		// 접속할 때 확인해야 하는 조건
		if( Util.valid(ccl) ) mCandidate.addChkConds(ccl.split(","));
		
		// 접속할 때 배제해야 하는 조건
		if( Util.valid(ecl) ) mCandidate.addExclusionConds(ecl.split(","));

		return mCandidate;
	}


	/**
	 * <pre>
	 * 기분석 후보 정보를 반환한다.
	 * 분석 사전에서 { } 내에 들어갈 정보를 반환해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @return
	 */
	public String toString()
	{
		return getString();
	}


	/**
	 * <pre>
	 * 기분석 후보 정보를 반환한다.
	 * 분석 사전에서 { } 내에 들어갈 정보를 반환해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 09. 29
	 * @return
	 */
	public String getString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(String.format("%4d", realDicLen));
		sb.append(String.format("%4d", candDicLen));
		sb.append(String.format("%4d", size()));
		sb.append(String.format("%4d", diclenOfBestMC));
		sb.append(String.format("%8.3f", spacingLnprOfBestMC));
		sb.append(String.format("%8.3f", taggingLnprOfBestMC));
		sb.append(String.format("%8.3f", getBestLnpr()));
		sb.append(String.format("%8.3f", lnprOfSpacing));
		sb.append(String.format("%8.3f", lnprOfTagging));
		sb.append(String.format("%8.3f  ", getLnpr()));

		// 형태소 분석 결과
		sb.append("[" + super.toString() + "]");

		// 접속 가능한 품사 정보
		String temp = POSTag.getZipTagStr(atlEnc);
		if( temp != null ) sb.append(DLMT_ATL + "(" + temp + ")");

		// 현재 후보가 가진 접속 조건
		temp = Condition.getCondStr(hclEnc);
		if( temp != null ) sb.append(DLMT_HCL + "(" + temp + ")");
		
		// 뒷방향 접속 조건 확인
		temp = Condition.getCondStr(bclEnc);
		if( temp != null ) sb.append(DLMT_BCL + "(" + temp + ")");

		// 접속할 때 확인해야 하는 조건
		temp = Condition.getCondStr(cclEnc);
		if( temp != null ) sb.append(DLMT_CCL + "(" + temp + ")");

		// 접속할 때 배제해야 하는 조건
		temp = Condition.getCondStr(eclEnc);
		if( temp != null ) sb.append(DLMT_ECL + "(" + temp + ")");
		
		sb.append("\t" + hashCode);
		
		sb.append("\t" + this.expList);
		
		if( prevBestMC != null ) {
			sb.append("\t" + prevBestMC.lastMorp.getTag());
		}
		
		return sb.toString();
	}


	/**
	 * <pre>
	 * 기본적으로 설정된 조건 외에 추가적으로 설정된 조건만 포함한 문자열을 생성하여 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 09. 30
	 * @return
	 */
	public String getSmplDicStr(String compResult)
	{
		StringBuffer sb = new StringBuffer();

		final long mask = 0xffffffffffffffffl;
		long basicATL = getBasicApndblTags();
		long basicHCL = getBasicHavingConds() | getBasicPhonemeConds(getExp());

		// 형태소 분석 결과
		sb.append(super.getSmplStr2());

		StringBuffer sb2 = new StringBuffer();
		// 접속 가능한 품사 정보
		String temp = POSTag.getZipTagStr(atlEnc & (mask ^ basicATL));
		if( temp != null ) sb2.append(DLMT_ATL + "(" + temp + ")");

		// 현재 후보가 가진 접속 조건
		temp = Condition.getCondStr(hclEnc & (mask ^ basicHCL));
		if( temp != null ) sb2.append(DLMT_HCL + "(" + temp + ")");

		// 접속할 때 확인해야 하는 조건
		temp = Condition.getCondStr(cclEnc);
		if( temp != null ) sb2.append(DLMT_CCL + "(" + temp + ")");
		
		// 접속할 때 배제해야 하는 조건
		temp = Condition.getCondStr(eclEnc);
		if( temp != null ) sb2.append(DLMT_ECL + "(" + temp + ")");

		// 복합명사 분해 결과
		if( Util.valid(compResult) ) sb2.append(DLMT_CNL + "(" + compResult + ")");

		if( sb2.length() > 0 ) {
			sb.append(";");
			sb.append(sb2);
		}

		return sb.toString();
	}
	
	
	public String getRawDicStr()
	{
		StringBuffer sb = new StringBuffer();

		final long mask = 0xffffffffffffffffl;
		long basicATL = getBasicApndblTags();
		long basicHCL = getBasicHavingConds() | getBasicPhonemeConds(getExp());

		String temp = null;
		// 형태소 분석 결과
		sb.append(getExp() + ":{[" + super.getSmplStr2() + "]");

		// 접속 가능한 품사 정보
		if( (temp = POSTag.getZipTagStr(atlEnc & (mask ^ basicATL))) != null ) sb.append(DLMT_ATL + "(" + temp + ")");
		// 현재 후보가 가진 접속 조건
		if( (temp = Condition.getCondStr(hclEnc & (mask ^ basicHCL))) != null ) sb.append(DLMT_HCL + "(" + temp + ")");
		// 접속할 때 확인해야 하는 조건
		if( (temp = Condition.getCondStr(cclEnc)) != null ) sb.append(DLMT_CCL + "(" + temp + ")");
		// 접속할 때 배제해야 하는 조건
		temp = Condition.getCondStr(eclEnc);
		if( (temp = Condition.getCondStr(eclEnc)) != null ) sb.append(DLMT_ECL + "(" + temp + ")");
		sb.append("}");

		return sb.toString();
	}
	
	
	public String toSimpleStr()
	{
		return super.toString();
	}


	/**
	 * <pre>
	 * 형태소 분석 정보에 부가 정보를 encoding된 상태로 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 11
	 * @return
	 */
	String getEncStr()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(super.getEncStr());
		sb.append("!" + atlEnc);
		sb.append("!" + hclEnc);
		sb.append("!" + cclEnc);
		sb.append("!" + eclEnc);
		return sb.toString();
	}


	/**
	 * <pre>
	 * 하나의 형태소의 품사만 제외하고 나머지가 다 동일할 경우 품사 정보를 합쳐준다.
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 15
	 * @param mc 통합할 대상 분석 후보
	 * @return 정상적으로 합쳐진 경우에는 true를 반환하고, 합쳐지지 않은 경우에는 false를 반환한다.
	 */
	boolean merge(MCandidate mc)
	{
		int size = size();
		if( size != mc.size() ) return false;
		if( atlEnc != mc.atlEnc ) return false;
		if( hclEnc != mc.hclEnc ) return false;
		if( cclEnc != mc.cclEnc ) return false;
		if( eclEnc != mc.eclEnc ) return false;

		Morpheme mp1 = null, mp2 = null, catchedMp1 = null, catchedMp2 = null;

		for( int i = 0; i < size; i++ ) {
			mp1 = get(i);
			mp2 = mc.get(i);

			if( !mp1.string.equals(mp2.string) ) return false;
			if( mp1.infoEnc != mp2.infoEnc ) {
				if( catchedMp1 != null ) return false;
				catchedMp1 = mp1;
				catchedMp2 = mp2;
			}
		}

		if( catchedMp1 == null ) return true;

		catchedMp1.infoEnc |= catchedMp2.infoEnc;

		return true;
	}


	/**
	 * <pre>
	 * 어절간 근접 확률만을 이용하여 최적 조합을 찾는다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 19
	 * @param mcPrev
	 */
	public void setBestPrevMC(MCandidate mcPrev)
	{

		// 첫 분석 후보 설정
		if( mcPrev == null ) {
			prevBestMC = null;
			diclenOfBestMC = getDicLenWithCand();
			spacingLnprOfBestMC = this.lnprOfSpacing;
			taggingLnprOfBestMC = PDDictionary.getLnprPosGMorpInter(POSTag.BOS, this.firstMorp.string, this.firstMorp.getTagNum()) + this.lnprOfTagging;
			return;
		}

		// 띄어쓰기 없이 결합 가능하고, 띄어쓰기 불가능한 경우에는 띄어쓰기 없이 확률 계산
		//boolean apndbl = mcPrev.isApndbl(this) && !mcPrev.isApndblWithSpace(this);
		boolean apndbl = false;

		// 종결 어미 다음의 마침 기호는 띄어쓰기 없이 처리
		if( mcPrev.lastMorp.isTagOf(POSTag.EF) && this.firstMorp.isTagOf(POSTag.SF) ) apndbl = true;

		// 사전어 길이 계산
		int newBestDicLen = mcPrev.diclenOfBestMC + mcPrev.getDicLenWithCand();

		// 띄어쓰기 확률 계산
		float newBestSpacingLnpr = mcPrev.spacingLnprOfBestMC + this.lnprOfSpacing;
		newBestSpacingLnpr += SpacingPDDictionary.getProb(mcPrev.getLastSyllable(), this.getFirstSyllable(), !apndbl);

		// 태깅 확률 계산
		float newBestTaggingLnpr = mcPrev.taggingLnprOfBestMC + this.lnprOfTagging;
		if( apndbl ) {
//			long prevTag = mcPrev.lastMorp.getTagNum();
//			if( mcPrev.lastMorp.isTagOf(POSTag.SS) && mcPrev.prevBestMC != null ) {
//				prevTag = mcPrev.prevBestMC.lastMorp.getTagNum();
//			}
			newBestTaggingLnpr += PDDictionary.getLnprPosGMorpIntra(mcPrev.lastMorp.getTagNum(), this.firstMorp.string, this.firstMorp.getTagNum());
		} else {
			newBestTaggingLnpr += PDDictionary.getLnprPosGMorpInter(mcPrev.lastMorp.getTagNum(), this.firstMorp.string, this.firstMorp.getTagNum());
		}
//		// TODO, 20110422
//		if( !mcPrev.lastMorp.isTag(POSTag.XP) ) {
//			newBestTaggingLnpr -= PDDictionary.getLnprPos(mcPrev.lastMorp.getTagNum());
//		}
		
		if( prevBestMC == null ) {
			prevBestMC = mcPrev;
			diclenOfBestMC = newBestDicLen;
			spacingLnprOfBestMC = newBestSpacingLnpr;
			taggingLnprOfBestMC = newBestTaggingLnpr;
		} else {
			// 사전어 길이 확인
			if( newBestDicLen > diclenOfBestMC ) {
				prevBestMC = mcPrev;
				diclenOfBestMC = newBestDicLen;
				spacingLnprOfBestMC = newBestSpacingLnpr;
				taggingLnprOfBestMC = newBestTaggingLnpr;
			}
			// 조합 확률 확인
			else if( newBestDicLen == diclenOfBestMC 
					&& newBestSpacingLnpr + newBestTaggingLnpr > spacingLnprOfBestMC + taggingLnprOfBestMC ) 
			{
				prevBestMC = mcPrev;
				diclenOfBestMC = newBestDicLen;
				spacingLnprOfBestMC = newBestSpacingLnpr;
				taggingLnprOfBestMC = newBestTaggingLnpr;
			}
		}
	}


	public void calcLnprOfSpacing()
	{
		this.lnprOfSpacing = SpacingPDDictionary.getProb(this.getExp());
	}


	/**
	 * <pre>
	 * 현재 분석 결과에 대한 형태소 인접 확률 계산
	 * </pre>
	 * @author	therocks
	 * @since	2009. 10. 16
	 */
	public void calcLnprOfTagging()
	{
		// 확률값 초기화
		lnprOfTagging = 0;

		boolean isApndbl = true;
		Morpheme prevMp = null;
		Morpheme mp = null;
		for( int i = 0, size = size(); i < size; i++ ) {
			mp = get(i);
			
			// 띄어쓰기할 때의 확률값으로 처리
			if( mp instanceof MorphemeSpace ) {
				isApndbl = false;
				continue;
			}
			
			float lnprPosGExp = PDDictionary.getLnprPosGExp(mp.string, mp.getTagNum());

			// 형태소 인접 확률 
			if( prevMp != null ) {
				// (어절내)
				if( isApndbl ) {
					// 한 -> 하 + ㄴ 과 같이 음절이 분리되어 처리되어야 하는 경우 P(M|E)가 낮게 나오므로 이를 위한 처리
					// 이를 직접 음절열에 대한 형태소 분석 확률 사전으로 구축하여 검색 (음절이 변형되는 경우에만 적용됨) 
					// if( mp.getString().equals("ㄴ") || mp.getString().equals("ㅁ") || mp.getString().equals("ㄹ") || mp.getString().equals("는") ) {
					float tempLnpr = PDDictionary.getLnprMorpsGExp(prevMp, mp);
					if( tempLnpr <= 0 ) {
						lnprPosGExp = tempLnpr - PDDictionary.getLnprPosGExp(prevMp.getString(), prevMp.getTagNum());
					} else {
						lnprOfTagging += PDDictionary.getLnprPosGMorpIntra(prevMp.getTagNum(), mp.string, mp.getTagNum());
					}
					// } else {
					//	lnprOfTagging += PDDictionary.getLnprPosGMorpIntra(prevMp.getTagNum(), mp.string, mp.getTagNum());
					// }
				}
				// (어절간)
				else {
					lnprOfTagging += PDDictionary.getLnprPosGMorpInter(prevMp.getTagNum(), mp.string, mp.getTagNum());
					isApndbl = true;
				}
				// TODO, 20110422
//				if( !prevMp.isTag(POSTag.XP) ) {
//					lnprOfTagging -= PDDictionary.getLnprPos(prevMp.getTagNum());
//				}
			}
			lnprOfTagging += lnprPosGExp;

			prevMp = mp;
		}
	}


	/**
	 * <pre>
	 * 첫번째 형태소가 주어진 품사 중 하나인지를 확인하여 반환한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 21
	 * @param tagEnc
	 * @return
	 */
	public boolean isFirstTagOf(long tagEnc)
	{
		return firstMorp.isTagOf(tagEnc);
	}


	/**
	 * <pre>
	 * 한글 이외것들의 조합인지 확인한다.
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 26
	 * @return
	 */
	public boolean isNotHangul()
	{
		return !lastMorp.isCharSetOf(CharSetType.HANGUL);
	}
	
	
	/**
	 * @return the realDicLen
	 */
	public byte getRealDicLen()
	{
		return realDicLen;
	}


	/**
	 * @param realDicLen the realDicLen to set
	 */
	public void setRealDicLen(byte realDicLen)
	{
		this.realDicLen = realDicLen;
	}


	/**
	 * @return the candDicLen
	 */
	public byte getCandDicLen()
	{
		return candDicLen;
	}


	/**
	 * @param candDicLen the candDicLen to set
	 */
	public void setCandDicLen(byte candDicLen)
	{
		this.candDicLen = candDicLen;
	}


	/**
	 * @param lnprOfSpacing the lnprOfSpacing to set
	 */
	public void setLnprOfSpacing(float lnprOfSpacing)
	{
		this.lnprOfSpacing = lnprOfSpacing;
	}
	

	public float getBestLnpr()
	{
		return spacingLnprOfBestMC + taggingLnprOfBestMC;
	}

}