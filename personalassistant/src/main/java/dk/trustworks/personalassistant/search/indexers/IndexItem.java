package dk.trustworks.personalassistant.search.indexers;

public class IndexItem {

    private Long id;
    private String title;
    private String content;
    private String path;

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String PATH = "path";

    public IndexItem(Long id, String title, String content, String path) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getPath() { return path; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IndexItem{");
        sb.append("id=").append(id);
        sb.append(", title='").append(title).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append(", path='").append(path).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
