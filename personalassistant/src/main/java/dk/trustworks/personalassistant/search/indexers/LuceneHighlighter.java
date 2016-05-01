package dk.trustworks.personalassistant.search.indexers;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.util.Version;

public class LuceneHighlighter {
	public static String highlight(String pText, String pQuery) throws Exception {
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		QueryParser parser = new QueryParser(Version.LUCENE_36, "", analyzer);
		Highlighter highlighter = new Highlighter(new QueryScorer(parser.parse(pQuery)));
		highlighter.setTextFragmenter(new SimpleFragmenter(100));

		String[] textFragments = highlighter.getBestFragments(analyzer, pQuery, pText, 3);
		
		String text = StringUtils.join(textFragments, "...");
		
		if (text != null) {
			return text;
		}
		return pText;
	}
}
