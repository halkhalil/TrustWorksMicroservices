package dk.trustworks.personalassistant.search.indexers;


import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileIndexApplication {
    // location where the index will be stored.
    private static final String INDEX_DIR = "/Users/hans/index";
    private static final int DEFAULT_RESULT_SIZE = 100;
    
    private static final String[] supportedFiletypes = {"doc","docx","xls","xlsx","ppt","pptx","pdf"};

    public static void main(String[] args) throws NumberFormatException, Exception {

        MSDocumentIndexer msDocumentIndexer = new MSDocumentIndexer();
        PDFIndexer pdfIndexer = new PDFIndexer();
        
        Indexer indexer = new Indexer(INDEX_DIR);

        File root = new File("/Users/hans/Dropbox (TrustWorks ApS)/Shared");
        
        
        traverseDirectory(msDocumentIndexer, pdfIndexer, indexer, root);
        
        // close the index to enable them index
        indexer.close();

        // creating the Searcher to the same index location as the Indexer
        Searcher searcher = new Searcher(INDEX_DIR);
        List<IndexItem> result = searcher.findByContent("Temperaturmling", DEFAULT_RESULT_SIZE);
        print(result);

        searcher.close();
    }

	public static void traverseDirectory(MSDocumentIndexer msDocumentIndexer,
			PDFIndexer pdfIndexer, Indexer indexer, File root) throws IOException {
		File[] files = root.listFiles();
        for (File file : files) {
			if(file.isFile() && file.length()>5000) {
				for (String filetype : supportedFiletypes) {
					if(file.getName().toLowerCase().endsWith(filetype)) {
				        try {
				        	if(file.getName().toLowerCase().endsWith("pdf")) indexer.index(pdfIndexer.index(file));
				        	else indexer.index(msDocumentIndexer.index(file));
						} catch (Exception e) {
							System.out.println("Error indexing "+file.getAbsolutePath());
						}
					}
				}
			} else if(file.isDirectory()) {
				traverseDirectory(msDocumentIndexer, pdfIndexer, indexer, file);
			}
		}
	}

     /**
      * print the results.
      */
    private static void print(List<IndexItem> result) {
        System.out.println("Result Size: " + result.size());

        for (IndexItem item : result) {
            System.out.println(item);
        }
    }
}
