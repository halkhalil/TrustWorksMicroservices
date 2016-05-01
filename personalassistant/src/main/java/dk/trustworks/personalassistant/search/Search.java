package dk.trustworks.personalassistant.search;

import dk.trustworks.personalassistant.search.indexers.IndexItem;
import dk.trustworks.personalassistant.search.indexers.Searcher;

import java.util.List;

/**
 * Created by hans on 27/04/16.
 */
public class Search {
    private static final long serialVersionUID = 1L;
    private static final String INDEX_DIR = "/Users/hans/index";
    private static final int DEFAULT_RESULT_SIZE = 100;

    public static void main(String[] args) {
        try {
            Searcher searcher = new Searcher(INDEX_DIR);
            List<IndexItem> result;
            result = searcher.findByContent("Målarkitektur",
                    DEFAULT_RESULT_SIZE);
            for (IndexItem item : result) {
                //System.out.println("item.getContent() = " + item.getContent());
                //System.out.println("item.toString() = " + item.toString());
                System.out.println("item.getTitle() = " + item.getTitle());
                System.out.println("item.getPath() = " + item.getPath());
            }

            //request.setAttribute("result", result);
            searcher.close();
            //request.getRequestDispatcher("/result.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
