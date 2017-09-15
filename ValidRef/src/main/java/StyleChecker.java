import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.csl.CSLType;
import de.undercouch.citeproc.output.Bibliography;
import name.fraser.neil.plaintext.diff_match_patch;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Uni on 17.07.2017.
 */
public class StyleChecker {

    /*private boolean withoutStyleError = true;

    public boolean isWithoutStyleError() {
        return withoutStyleError;
    }

    public void setWithoutStyleError(boolean withoutStyleError) {
        this.withoutStyleError = withoutStyleError;
    }
*/
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

    private String personDelemiter = "&";
    private String partsDelemiter = ".";

    private CitationStyle style;

    public StyleChecker(CitationStyle style) {
        this.style = style;
    }

    public JSONObject checkStyleNew(InputReference ref, CSLType type) {
     //   withoutStyleError = true;
        JSONObject referenceJSON = new JSONObject();

        JSONObject json = new JSONObject();
        //TODO just original string is still used
        fillEntities(ref);

        String toCompare = helper(ref, type).trim()
                //.replace('–', '\u002D')
                .replace("’", "'");

        if (toCompare != null && !toCompare.equals("")) {
            if (originalString.equals(toCompare)) {
                referenceJSON.put("styleCorrect", true);
                referenceJSON.put("possibleErrors", "");
            } else {
                printDifferences(toCompare, originalString);
                referenceJSON.put("styleCorrect", false);
           //     withoutStyleError = false;
                //TODO make check what error it is, might be more then one!!!, so maybe list of errors?
                referenceJSON.put("possibleErrors", chooseErrors(toCompare, originalString));
            }
            referenceJSON.put("createdString", toCompare);
        }
        return referenceJSON;
    }

    private void printDifferences(String toCompare, String originalString) {
        diff_match_patch difference = new diff_match_patch();
        LinkedList<diff_match_patch.Diff> deltas = difference.diff_main(toCompare, originalString);
        System.out.println("DIFFERENCES: ");

        for (int i = 0; i < deltas.size(); i++) {
            if (!deltas.get(i).operation.toString().equals("EQUAL")) {
                System.out.println(i + ": " + deltas.get(i).text);
            }
        }
        System.out.println("input user: " + originalString);
        System.out.println("created string: " + toCompare);

    }

    private List chooseErrors(String toCompare, String originalString) {
        diff_match_patch difference = new diff_match_patch();
        ArrayList<String> errorList = new ArrayList<>();
        LinkedList<diff_match_patch.Diff> deltas = difference.diff_main(toCompare, originalString);
        boolean errorFound = false;
        for (int i = 0; i < deltas.size(); i++) {
            if (!deltas.get(i).operation.toString().equals("EQUAL")) {
                //delimiting
                //TODO check if this is alwayys true when there is any sign in the string missing...that would be wrong, it should just be true when there are just delimiters
                Pattern p = Pattern.compile("\\p{Punct}");
                Matcher m = p.matcher(deltas.get(i).text);
                if (m.find()) {
                    System.out.println("TEST: " + m.group(0));
                    if (deltas.get(i).text.length() > 8) {
                        errorList = addErrorTypeIfNotExists(errorList, ErrorType.UNKNOWN.getValue());
                    } else {
                        errorList = addErrorTypeIfNotExists(errorList, ErrorType.WRONG_DELIMITING.getValue());
                    }
                    errorFound = true;
                }
                //order
                //remember order error might be also a wrong elements error
                if (deltas.get(i).operation.toString().equals("INSERT")) {
                    String insertText = deltas.get(i).text;
                    for (int j = 0; j < deltas.size(); j++) {
                        if (deltas.get(j).operation.toString().equals("DELETE")) {
                            String deleteText = deltas.get(j).text;
                            if (insertText.equals(deleteText)) {
                                errorList = addErrorTypeIfNotExists(errorList, ErrorType.ORDER.getValue());
                                errorFound = true;
                            }
                        }
                    }
                }
                if (!errorFound) {
                    //TODO add list of shortcuts, to check for other error categories
                    //shortcuts
                    errorList = addErrorTypeIfNotExists(errorList, ErrorType.WRONG_SHORTCUTTING.getValue());
                }
            }
        }
        return errorList;
    }

    private ArrayList<String> addErrorTypeIfNotExists(ArrayList<String> errorList, String value) {
        if (!errorList.contains(value)) {
            errorList.add(value);
        }
        return errorList;
    }


    private String helper(InputReference ref, CSLType type) {
        CSL citeproc = null;
        try {
            MyItemProvider provider = new MyItemProvider();
            provider.setInput(ref);
            provider.setType(type);
            citeproc = new CSL(provider, style.getValue());

            citeproc.setOutputFormat("text");
            citeproc.registerCitationItems("ID-0");
            Bibliography bibl = citeproc.makeBibliography();
            //List<Citation> s1 = citeproc.makeCitation("ID-0");
            //System.out.println(s1.get(0).getText());
            //return s1.get(0).getText();
            for (String entry : bibl.getEntries()) {
                //This is necessary due to a not yet fixed problem with the citeproc-java, which will double all editors in the editors string
                //entry = checkOfDoubleEditors(entry);
                String pages = ref.getPages();
                String newPages = ref.getPages().replace("-", "\u2013");
                entry.replace(pages, newPages);
                System.out.println(entry);
                return entry;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        /*
        CSLItemData item = new CSLItemDataBuilder()
                .type(CSLType.WEBPAGE)
                .title("citeproc-java: A Citation Style Language (CSL) processor for Java")
                .author("Michel", "Krämer")
                .issued(2016, 11, 20)
                .URL("http://michel-kraemer.github.io/citeproc-java/")
                .accessed(2017, 8, 30)
                .build();

        try {
            String bibl = CSL.makeAdhocBibliography("apa", item).makeString();
            System.out.println(bibl);
            return bibl;
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //TODO evtl hier leerstring ""
        return null;
    }

    private void fillEntities(InputReference ref) {
        this.title = ref.getTitle();
        this.pages = ref.getPages();
        this.journalOrBooktitle = ref.getJournalOrBooktitle();
        this.year = ref.getYear();
        this.authors = ref.getAuthors();
        this.originalString = ref.getOriginalString();
        this.volume = ref.getVolume();
        this.publisher = ref.getPublisher();
        this.techReportNumber = ref.getTechReportNumber();
        this.DOI = ref.getDOI();
        this.institution = ref.getInstitution();
        this.location = ref.getLocation();
        this.number = ref.getNumber();
        this.note = ref.getNote();
        this.editors = ref.getEditors();

    }

    private String createBook() {
        //punkte zwischen allen abschnitten
        //abkürzungen volume, pages etc
        //Author, A. (Year of Publication). Title of work. Publisher City, State: Publisher.
        String book = "";

        if (authors != null && !authors.isEmpty()) {
            String authors = addAuthors();
            book += authors;
        } else {
            String editors = addEditorsAsAuthors();
            book += editors;
        }

        //there must be a year, if there was none in the input it has to be written as (n.d.)
        String year = addYear();
        book += " " + year;

        if (title != null && title != "") {
            String title = addTitle();
            book += partsDelemiter + " " + title;
        }
        if (journalOrBooktitle != null && journalOrBooktitle != "") {
            String journal = addJournalOrBooktitle();
            book += partsDelemiter + " " + journal;
        }
        if (editors != null && !editors.isEmpty()) {
            String edit = addEditors();
            book += " " + edit;
        }
        if (publisher != "" && publisher != null) {
            book += partsDelemiter + " " + addPublisher();
        }
        if (institution != "" && institution != null) {
            if (location != "" && location != null) {
                book += partsDelemiter + " " + location + ": " + institution;
            } else {
                book += partsDelemiter + " " + institution;
            }
        }
        book += ".";
        if (DOI != null && DOI != "") {
            book += partsDelemiter + " " + addDOI();
        }
        return book;
    }

    private String addEditorsAsAuthors() {
        String editorsString = "";
        for (int i = 0; i < editors.size(); i++) {
            String[] names = editors.get(i).split(", ");
            String editorReady = "";
            if (names != null) {
                String lastname = names[0];
                String firstnames = "";
                //if there is more then just a lastname, at least one firstname
                if (names.length > 1) {
                    //check each firstname
                    for (int j = 1; j < names.length; j++) {
                        String formattedFirstname = "";
                        //check every character of the firstnames, if it is capital we use it to shortcut it
                        for (int n = 0; n < names[j].length(); n++) {
                            if (Character.isUpperCase(names[j].charAt(n)) || names[j].toCharArray().length == 1) {
                                if (n != names[j].length() - 1) {
                                    formattedFirstname += names[j].charAt(n) + ". ";
                                } else {
                                    formattedFirstname += names[j].charAt(n) + ".";
                                }
                            }
                        }
                        firstnames += formattedFirstname;
                    }
                }
                editorReady = lastname + ", " + firstnames;
            }

            if (i != 0 && i == editors.size() - 1) {
                editorsString += personDelemiter + " " + editorReady;
                if (editors.size() == 1) {
                    editorsString += ", Ed.";
                } else {
                    editorsString += ", Eds.";
                }
            } else if (editors.size() == 1) {
                editorsString += editorReady;
            } else {
                editorsString += editorReady + ", ";
            }
        }
        return editorsString;
    }

    private String addPublisher() {
        if (location != "" && location != null) {
            return location + ": " + publisher;
        } else {
            return publisher;
        }
    }

    private String createArticle() {
        //punkte zwischen allen abschnitten
        //abkürzungen volume, pages etc
        String article = "";
        if (authors != null && !authors.isEmpty()) {
            String authors = addAuthors();
            article += authors;
        }

        //there must be a year, if there was none in the input it has to be written as (n.d.)
        String year = addYear();
        article += " " + year;

        if (title != null && title != "") {
            String title = addTitle();
            article += partsDelemiter + " " + title;
        }
        if (journalOrBooktitle != null && journalOrBooktitle != "") {
            String journal = addJournalOrBooktitle();
            article += partsDelemiter + " " + journal;
        }
        if (institution != "" && institution != null) {
            if (location != "" && location != null) {
                article += partsDelemiter + " " + location + ": " + institution;
            } else {
                article += partsDelemiter + " " + institution;
            }
        }
        article += ".";
        if (DOI != null && DOI != "") {
            //TODO check if after DOI is really a dot!
            article += partsDelemiter + " " + addDOI();
        }
        return article;
    }

    private String addDOI() {
        String doi = "DOI: " + DOI;
        return doi;
    }

    private String addJournalOrBooktitle() {
        String journal = journalOrBooktitle;
        if (volume != null && volume != "") {
            String vol = volume;
            if (number != null && number != "") {
                vol += "(" + number + ")";
            }
            journal += ", " + vol;
        }
        if (pages != null && pages != "") {
            journal += ", " + pages.replace("--", "-");
        }
        return journal;
    }

    private String addTitle() {
        return title;
    }

    private String addYear() {
        if (year.equals("") || year == null) {
            return "(n.d.)";
        }
        return "(" + year + ")";
    }

    private String addAuthors() {
        String authorsString = "";
        for (int i = 0; i < authors.size(); i++) {
            String[] names = authors.get(i).split(", ");
            String authorReady = "";
            if (names != null) {
                String lastname = names[0];
                String firstnames = "";
                //if there is more then just a lastname, at least one firstname
                if (names.length > 1) {
                    //check each firstname
                    for (int j = 1; j < names.length; j++) {
                        String formattedFirstname = "";
                        //check every character of the firstnames, if it is capital we use it to shortcut it
                        for (int n = 0; n < names[j].length(); n++) {
                            if (Character.isUpperCase(names[j].charAt(n)) || names[j].toCharArray().length == 1) {
                                if (n == names[j].length() - 1) {
                                    formattedFirstname += names[j].charAt(n) + ".";
                                } else {
                                    formattedFirstname += names[j].charAt(n) + ". ";
                                }
                            }
                        }
                        firstnames += formattedFirstname;
                    }
                }
                authorReady = lastname + ", " + firstnames;
            }

            if (i != 0 && i == authors.size() - 1) {
                authorsString += personDelemiter + " " + authorReady;
            } else if (authors.size() == 1) {
                authorsString += authorReady;
            } else {
                authorsString += authorReady + ", ";
            }
        }
        return authorsString;
    }

    private String addEditors() {
        String editorsString = "";
        for (int i = 0; i < editors.size(); i++) {
            String[] names = editors.get(i).split(", ");
            String editorReady = "";
            if (names != null) {
                String lastname = names[0];
                String firstnames = "";
                //if there is more then just a lastname, at least one firstname
                if (names.length > 1) {
                    //check each firstname
                    for (int j = 1; j < names.length; j++) {
                        String formattedFirstname = "";
                        //check every character of the firstnames, if it is capital we use it to shortcut it
                        for (int n = 0; n < names[j].length(); n++) {
                            if (Character.isUpperCase(names[j].charAt(n)) || names[j].toCharArray().length == 1) {
                                if (n != names[j].length() - 1) {
                                    formattedFirstname += names[j].charAt(n) + ". ";
                                } else {
                                    formattedFirstname += names[j].charAt(n) + ".";
                                }
                            }
                        }
                        firstnames += formattedFirstname;
                    }
                }
                editorReady = firstnames + " " + lastname;
            }

            if (i == editors.size() - 1) {
                if (editors.size() > 1) {
                    editorsString += " " + personDelemiter + " " + editorReady + ", Eds.";
                } else {
                    editorsString += editorReady + ", Ed.";
                }
            } else if (editorsString.equals("")) {
                editorsString += editorReady;
            } else {
                editorsString += ", " + editorReady;
            }
        }
        return "(" + editorsString + ")";
    }

    private String createEvent() {
        //punkte zwischen allen abschnitten
        //abkürzungen volume, pages etc
        String article = "";
        return article;
    }
}
