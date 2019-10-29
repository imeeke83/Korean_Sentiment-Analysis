package org.snu.ids.ha.sp;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.snu.ids.ha.constants.POSTag;
import org.snu.ids.ha.ma.Eojeol;
import org.snu.ids.ha.ma.Sentence;


public class Parser
{
	private List<ParseGrammar>	grammarList	= null;

	private static Parser		parser		= null;


	public static Parser getInstance()
	{
		if( parser == null ) parser = new Parser();
		return parser;
	}


	public Parser()
	{
		initGrammars();
	}


	protected void initGrammars()
	{
		grammarList = new ArrayList<ParseGrammar>();

		// add specific grammars
		grammarList.add(new ParseGrammar("이유", "기", POSTag.ETN, "때문", POSTag.NNB, 1, 1));
		
		// less specific grammars
		grammarList.add(new ParseGrammar("술부", POSTag.NNG | POSTag.XSN | POSTag.ETN, "없", POSTag.VA | POSTag.VX, 1, 1));
		
		// add general grammars
		grammarList.add(new ParseGrammar("동일", POSTag.N | POSTag.XSN, POSTag.NP, 1, 2));
		grammarList.add(new ParseGrammar("명사구", POSTag.N | POSTag.XSN, POSTag.N | POSTag.XPN, 1, 2));
		grammarList.add(new ParseGrammar("부사어", POSTag.JKM, POSTag.VP | POSTag.XPV, 1, 2));
		grammarList.add(new ParseGrammar("부사어", POSTag.JKM, POSTag.VP | POSTag.XPV, 10, 10));
		grammarList.add(new ParseGrammar("수식", POSTag.MD | POSTag.ETD | POSTag.JKG, POSTag.N | POSTag.XPN, 1, 2));
		grammarList.add(new ParseGrammar("수식", POSTag.MD | POSTag.ETD | POSTag.JKG, POSTag.N | POSTag.XPN, 3, 10));
		grammarList.add(new ParseGrammar("수식", POSTag.MAG, POSTag.VP | POSTag.MAG | POSTag.MD, 1, 2));
		grammarList.add(new ParseGrammar("수식", POSTag.MAG, POSTag.VP | POSTag.MAG | POSTag.MD, 10, 10));
		grammarList.add(new ParseGrammar("보조 연결", POSTag.ECS, POSTag.VP, 1, 2));
		grammarList.add(new ParseGrammar("의존 연결", POSTag.ECD, POSTag.VP, 10, 2));
		grammarList.add(new ParseGrammar("대등 연결", POSTag.ECE, POSTag.VP, 10, 2));
		grammarList.add(new ParseGrammar("체언 연결", POSTag.JC, POSTag.N | POSTag.XPN, 1, 2));
		grammarList.add(new ParseGrammar("주어", POSTag.JKS, POSTag.VP, 10, 2));
		grammarList.add(new ParseGrammar("주어", POSTag.N | POSTag.XSN | POSTag.JKS | POSTag.JX, POSTag.VCP, 10, 10));
		grammarList.add(new ParseGrammar("보어", POSTag.JKC | POSTag.JX, POSTag.VCN, 3, 10));
		grammarList.add(new ParseGrammar("목적어", POSTag.JKO, POSTag.VP, 10, 2));
		grammarList.add(new ParseGrammar("(주어,목적)대상", POSTag.N | POSTag.XSN | POSTag.JX | POSTag.ETN, POSTag.VP | POSTag.XPV, 10, 100));
		grammarList.add(new ParseGrammar("인용", POSTag.JKQ, POSTag.VV, 1, 2));

		Collections.sort(grammarList, new Comparator<ParseGrammar>()
		{

			@Override
			public int compare(ParseGrammar arg0, ParseGrammar arg1)
			{
				return arg0.priority - arg1.priority;
			}

		});
	}


	public ParseTree parse(Sentence sentence)
	{
		List<ParseTreeNode> nodeList = new ArrayList<ParseTreeNode>();
		for( Iterator<Eojeol> itr = sentence.iterator(); itr.hasNext(); ) {
			nodeList.add(new ParseTreeNode(itr.next()));
		}

		for( int i = 0; i < nodeList.size() - 1; i++ ) {
			ParseTreeNode ptnPrev = nodeList.get(i);

			for( int j = i + 1; j < nodeList.size(); j++ ) {
				ParseTreeNode ptnNext = nodeList.get(j);
				ParseTreeEdge arc = dominate(ptnPrev, ptnNext, j - i);
				if( arc != null ) {
					ptnNext.addChildEdge(arc);
					ptnPrev.setParentNode(ptnNext);
					break;
				}
			}
		}

		ParseTree tree = new ParseTree();
		tree.setSentenec(sentence.getSentence());
		for( Iterator<ParseTreeNode> itr = nodeList.iterator(); itr.hasNext(); ) {
			ParseTreeNode ptn = itr.next();
			if( ptn.getParentNode() == null ) tree.setRoot(ptn);
		}
		tree.setId();
		tree.setAllList();
		return tree;
	}


	public ParseTreeEdge dominate(ParseTreeNode ptnPrev, ParseTreeNode ptnNext, int distance)
	{
		ParseTreeEdge arc = null;
		for( int i = 0, size = grammarList.size(); i < size; i++ ) {
			if( (arc = grammarList.get(i).dominate(ptnPrev, ptnNext, distance)) != null ) {
				return arc;
			}
		}
		return null;
	}
}
