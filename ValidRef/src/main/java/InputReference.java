import java.util.ArrayList;

/**
 * Created by Uni on 13.12.2016.
 */
public class InputReference implements Cloneable {

    private String title = "";
    private String pages = "";
    private String journalOrBooktitle = "";
    private String year = "";
    private ArrayList<String> authors = new ArrayList<>();
    private String originalString = "";
    private String volume = "";
    private String publisher = "";
    private String techReportNumber = "";
    private String DOI = "";
    private String institution = "";
    private String location = "";
    private String number = "";
    private String note = "";
    private ArrayList<String> editors = new ArrayList<>();

    public InputReference() {
    }

    public InputReference(String title, String pages, String journalOrBooktitle, String year, ArrayList<String> authors,
                          String originalString, String volume, String publisher, String techReportNumber, String DOI,
                          String institution, String location, String number, String note, ArrayList<String> editors) {
        this.title = title;
        this.pages = pages;
        this.journalOrBooktitle = journalOrBooktitle;
        this.year = year;
        this.authors = authors;
        this.editors = editors;
        this.originalString = originalString;
        this.volume = volume;
        this.publisher = publisher;
        this.techReportNumber = techReportNumber;
        this.DOI = DOI;
        this.institution = institution;
        this.location = location;
        this.number = number;
        this.note = note;
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

    public void setOriginalString(String originalString) {
        this.originalString = originalString;
    }

    public String getOriginalString() {
        return originalString;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
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

    public String getTechReportNumber() {
        return techReportNumber;
    }

    public void setTechReportNumber(String techReportNumber) {
        this.techReportNumber = techReportNumber;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<String> getEditors() {
        return editors;
    }

    public void setEditors(ArrayList<String> editors) {
        this.editors = editors;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
