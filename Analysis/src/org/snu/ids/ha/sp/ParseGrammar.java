/**
 * <pre>
 * </pre>
 * @author	Dongjoo
 * @since	2009. 10. 26
 */
package org.snu.ids.ha.sp;


import org.snu.ids.ha.constants.POSTag;


/**
 * <pre>
 * 
 * </pre>
 * @author 	Dongjoo
 * @since	2009. 10. 26
 */
public class ParseGrammar
{
	String	relation		= null;
	String	dependantMorp	= null;
	long	dependantTag	= 0l;
	String	dominantMorp	= null;
	long	dominantTag		= 0l;
	int		distance		= 1;
	int		priority		= 10;


	public ParseGrammar(String relation, long dependantTag, long dominantTag, int distance, int priority)
	{

		this(relation, null, dependantTag, null, dominantTag, distance, priority);
	}


	public ParseGrammar(String relation, long dependantTag, String dominantMorp, long dominantTag, int distance, int priority)
	{

		this(relation, null, dependantTag, dominantMorp, dominantTag, distance, priority);
	}


	public ParseGrammar(String relation, String dependantMorp, long dependantTag, String dominantMorp, long dominantTag, int distance, int priority)
	{
		super();
		this.relation = relation;
		this.dependantMorp = dependantMorp;
		this.dependantTag = dependantTag;
		this.dominantMorp = dominantMorp;
		this.dominantTag = dominantTag;
		this.distance = distance;
		this.priority = priority;
	}


	/**
	 * <pre>
	 * 
	 * </pre>
	 * @author	Dongjoo
	 * @since	2009. 10. 26
	 * @param prevNode
	 * @param nextNode
	 * @param distance
	 * @return
	 */
	public ParseTreeEdge dominate(ParseTreeNode prevNode, ParseTreeNode nextNode, int distance)
	{
		boolean matchedPrev = false, matchedNext = false, isInRange = false;
		
		// 서술격 조사에 의한 서술
		if( dominantTag == POSTag.VCP ) {
			if( prevNode.isLastTagOf(dependantTag) && nextNode.containsTagOf(dominantTag) && distance <= this.distance ) {
				return new ParseTreeEdge(relation, prevNode, nextNode, distance, priority);
			}
		} else {
			if( prevNode.isLastMorpOf(dependantMorp, dependantTag) && nextNode.isFirstMorpOf(dominantMorp, dominantTag) && distance <= this.distance ) //
			{
				return new ParseTreeEdge(relation, prevNode, nextNode, distance, priority);
			}
		}

		// 의존소 확인
		matchedPrev = prevNode.isLastMorpOf(dependantMorp, dependantTag);

		// 지배소 확인
		// POSTag.VP
		if( dominantTag == POSTag.VCP ) {
			matchedNext = nextNode.containsTagOf(dominantTag);
		} else if( (dominantTag & (POSTag.XSV | POSTag.XSA)) > 0 ) {
			matchedNext = nextNode.containsTagOf(POSTag.XSV | POSTag.XSA);
		} else  {
			matchedNext = nextNode.isFirstMorpOf(dominantMorp, dominantTag);
		}

		// 거리 확인
		isInRange = distance <= this.distance;

		if( matchedPrev && matchedNext && isInRange ) {
			return new ParseTreeEdge(relation, prevNode, nextNode, distance, priority);
		}

		return null;
	}
}
