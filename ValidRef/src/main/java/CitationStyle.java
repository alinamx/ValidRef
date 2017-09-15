/**
 * Created by Uni on 03.07.2017.
 */
public enum CitationStyle {
    HARVARD("harvard-cite-them-right"), VANCOUVER("vancouver"), MLA("modern-language-association"), APA("apa"), CHICAGO("chicago-author-date");

    private String value;

    CitationStyle(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
