package dk.trustworks.personalassistant.search.indexers;

import java.io.File;
import java.io.IOException;

import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

public class MSDocumentIndexer implements FileIndexer {

	public IndexItem index(File file) throws IOException, InvalidFormatException, OpenXML4JException, XmlException {

		String content = "";

		content = ExtractorFactory.createExtractor(file).getText();

		return new IndexItem((long) file.hashCode(), file.getName(), content, file.getAbsolutePath());
	}
}
