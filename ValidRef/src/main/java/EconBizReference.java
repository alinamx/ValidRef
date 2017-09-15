import de.undercouch.citeproc.csl.CSLType;

import java.util.ArrayList;

/**
 * Created by Uni on 13.12.2016.
 */
public class EconBizReference {

    //TODO idea: make one reference object not two different classes
    private String ID = "";
    private String title = "";
    private String pages = "";
    private String journalOrBooktitle = "";
    private String year = "";
    private ArrayList<String> authors = new ArrayList<>();
    private CSLType type = null;
    private String bibtex = "";
    private String volume = "";
    private String publisher = "";
    private ArrayList<String> editors = new ArrayList<>();
    private String chapter = "";
    private String DOI = "";
    private String institution = "";
    private String number = "";
    private String location = "";
    //Institution Location Number Tech Note

    public EconBizReference() {
    }

    public ArrayList getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public CSLType getType() {
        return type;
    }

    public void setType(CSLType type) {
        this.type = type;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getBibtex() {
        return bibtex;
    }

    public void setBibtex(String bibtex) {
        this.bibtex = bibtex;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getVolume() {
        return volume;
    }

    public String getJournalOrBooktitle() {
        return journalOrBooktitle;
    }

    public void setJournalOrBooktitle(String journalOrBooktitle) {
        this.journalOrBooktitle = journalOrBooktitle;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public ArrayList<String> getEditors() {
        return editors;
    }

    public void setEditors(ArrayList<String> editors) {
        this.editors = editors;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getDOI() {
        return DOI;
    }

    public void setDOI(String DOI) {
        this.DOI = DOI;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
