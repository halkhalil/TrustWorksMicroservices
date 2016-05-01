package dk.trustworks.personalassistant.search.indexers;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFIndexer implements FileIndexer {
	public IndexItem index(File file) throws IOException {  
	     PDDocument doc = PDDocument.load(file);  
	     String content = new PDFTextStripper().getText(doc);
	     doc.close();  
	     return new IndexItem((long)file.getName().hashCode(), file.getName(), content, file.getAbsolutePath());
	   }  
}
