import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Uni on 13.12.2016.
 * <p>
 * This handler parses text format citations using the online API of FreeCite -
 * Open Source Citation Parser http://freecite.library.brown.edu/
 */
public class FreeCiteHandler {

    public FreeCiteHandler() {
    }

    public boolean testURL() throws Exception {
        try {
            final URL url = new URL("http://freecite.library.brown.edu/");
            System.out.println("Checking URL: http://freecite.library.brown.edu/");
            final URLConnection conn = url.openConnection();
            conn.connect();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
        //try {
        //    InetAddress.getByName("http://freecite.library.brown.edu/").isReachable(3000); //Replace with your name
        //return true;
        //} catch (Exception e) {
        //    return false;
        //}
    }

    public List<InputReference> loadAllFreeCite(List<String> references) {
        try {
            if (testURL()) {
                String url = "http://freecite.library.brown.edu/citations/create";
                List<InputReference> searchEconbizList = new ArrayList<>();
                try {
                    URL obj = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

                    conn.setRequestProperty("Accept", "text/xml");
                    conn.setDoOutput(true);

                    conn.setRequestMethod("POST");

                    String data = "citation[]=" + URLEncoder.encode(references.get(0), "UTF-8");
                    for (int i = 1; i < references.size(); i++) {
                        data = data + "&citation[]=" + references.get(i);
                    }
                    System.out.println("FREECITE DATA: " + data);
                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    out.write(data);
                    out.flush();

                    XMLInputFactory factory = XMLInputFactory.newInstance();
                    try {
                        XMLStreamReader parser = factory.createXMLStreamReader(conn.getInputStream());
                        while (parser.hasNext()) {
                            if ((parser.getEventType() == XMLStreamConstants.START_ELEMENT)
                                    && (parser.getLocalName().equals("citation"))) {
                                parser.nextTag();
                                InputReference ref = new InputReference();

                                StringBuilder noteSB = new StringBuilder();

                                while (!(parser.getEventType() == XMLStreamConstants.END_ELEMENT
                                        && parser.getLocalName().equals("citation"))) {

                                    if (parser.getEventType() == XMLStreamConstants.START_ELEMENT) {
                                        String ln = parser.getLocalName();
                                        if (ln.equals("authors")) {
                                            StringBuilder sb = new StringBuilder();
                                            parser.nextTag();

                                            while (parser.getEventType() == XMLStreamConstants.START_ELEMENT) {
                                                // author is directly nested below authors
                                                assert (parser.getLocalName()
                                                        .equals("author"));

                                                String author = parser.getElementText();
                                                if (sb.length() == 0) {
                                                    sb.append(author);
                                                } else {
                                                    sb.append(" and ");
                                                    sb.append(author);
                                                }
                                                assert (parser.getEventType() == XMLStreamConstants.END_ELEMENT);
                                                assert (parser.getLocalName().equals("author"));
                                                parser.nextTag();
                                                // current tag is either begin:author or
                                                // end:authors
                                            }
                                            ArrayList<String> authorsNew = new ArrayList<>();
                                            String authors = sb.toString();
                                            String[] a = authors.split(" and ");
                                            for (String author : a) {
                                                authorsNew.add(author);
                                            }
                                            ref.setAuthors(authorsNew);

                                        } else if (ln.equals("journal")) {
                                            ref.setJournalOrBooktitle(parser.getElementText());
                                        } else if (ln.equals("tech")) {
                                            // the content of the "tech" field seems to contain the number of the technical report
                                            ref.setTechReportNumber(parser.getElementText());
                                        } else if (ln.equals("doi")) {
                                            ref.setDOI(parser.getElementText());
                                        } else if (ln.equals("editor")) {
                                            ref.setEditors(listifyEditors(parser.getElementText()));
                                        } else if (ln.equals("institution")) {
                                            ref.setInstitution(parser.getElementText());
                                        } else if (ln.equals("location")) {
                                            ref.setLocation(parser.getElementText());
                                        } else if (ln.equals("number")) {
                                            ref.setNumber(parser.getElementText());
                                        } else if (ln.equals("note")) {
                                            ref.setNote(parser.getElementText());
                                        } else if (ln.equals("pages")) {
                                            ref.setPages(parser.getElementText());
                                        } else if (ln.equals("volume")) {
                                            ref.setVolume(parser.getElementText());
                                        } else if (ln.equals("publisher")) {
                                            ref.setPublisher(parser.getElementText());
                                        } else if (ln.equals("title")) {
                                            ref.setTitle(parser.getElementText());
                                            if (ref.getTitle().contains("-")) {
                                                //some title contained a different 'minus', so we reset it to the standard one
                                                String newTitle = ref.getTitle().replace('-', '\u002D');
                                                ref.setTitle(newTitle);
                                            }
                                        } else if (ln.equals("raw_string")) {
                                            ref.setOriginalString(parser.getElementText());
                                        } else if (ln.equals("year")) {
                                            ref.setYear(parser.getElementText());
                                        } else if (ln.equals("booktitle")) {
                                            String booktitle = parser.getElementText();
                                            if (booktitle.startsWith("In ")) {
                                                // special treatment for parsing of
                                                // "In proceedings of..." references
                                                booktitle = booktitle.substring(3);
                                            }
                                            ref.setJournalOrBooktitle(booktitle);
                                        }
                                        // all other tags are ignored
                                    }
                                    parser.next();
                                }
                                searchEconbizList.add(ref);
                            }
                            parser.next();
                        }
                        parser.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    out.close();
                    //reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return searchEconbizList;
            } else {
                System.out.print("Server of FreeCite not reachable! Please check your connection!");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("Server of FreeCite not reachable! Please check your connection!");
            return null;
        }
    }

    private void checkIfNoteBelongsToSomeOtherPart(InputReference ref) {
        String note = ref.getNote();
        String title = ref.getTitle();
        String journal = ref.getJournalOrBooktitle();
        String original = ref.getOriginalString();
        if (!note.equals("")) {
            if (!title.equals("") && original.contains(title + " " + note)) {
                ref.setTitle(title + " " + note);
                ref.setNote("");
            } else if (!title.equals("") && original.contains(title + note)) {
                ref.setTitle(title + note);
                ref.setNote("");
            }

            if (!journal.equals("") && original.contains(journal + " " + note)) {
                ref.setJournalOrBooktitle(journal + " " + note);
                ref.setNote("");
            } else if (!journal.equals("") && original.contains(journal + note)) {
                ref.setJournalOrBooktitle(journal + note);
                ref.setNote("");
            }
        }
    }

    private ArrayList<String> listifyEditors(String editorsString) {
        ArrayList editors = new ArrayList();
        editorsString = editorsString.replace(", &", ",");
        editorsString = editorsString.replace(" & ", ", ");
        String editorsEach[] = editorsString.split(", ");
        for (String ed : editorsEach) {
            if ((ed.contains("Eds") || ed.contains("Ed") || ed.contains("Hrsg")) && ed.split(" ").length == 1) {
                //nix
            } else {
                editors.add(ed);
            }
        }
        return editors;
    }


    public List<InputReference> loadFreeCite(String reference) {
        try {
            if (testURL()) {
                String url = "http://freecite.library.brown.edu/citations/create";
                List<InputReference> searchEconbizList = new ArrayList<>();
                try {
                    URL obj = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

                    conn.setRequestProperty("Accept", "text/xml");
                    conn.setDoOutput(true);

                    conn.setRequestMethod("POST");

                    String data = "citation[]=" + URLEncoder.encode(reference, "UTF-8");

                    System.out.println("FREECITE DATA: " + data);
                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    out.write(data);
                    out.flush();

                    XMLInputFactory factory = XMLInputFactory.newInstance();
                    try {
                        XMLStreamReader parser = factory.createXMLStreamReader(conn.getInputStream());
                        while (parser.hasNext()) {
                            if ((parser.getEventType() == XMLStreamConstants.START_ELEMENT)
                                    && (parser.getLocalName().equals("citation"))) {
                                parser.nextTag();
                                InputReference ref = new InputReference();

                                StringBuilder noteSB = new StringBuilder();

                                while (!(parser.getEventType() == XMLStreamConstants.END_ELEMENT
                                        && parser.getLocalName().equals("citation"))) {

                                    if (parser.getEventType() == XMLStreamConstants.START_ELEMENT) {
                                        String ln = parser.getLocalName();
                                        if (ln.equals("authors")) {
                                            StringBuilder sb = new StringBuilder();
                                            parser.nextTag();

                                            while (parser.getEventType() == XMLStreamConstants.START_ELEMENT) {
                                                // author is directly nested below authors
                                                assert (parser.getLocalName()
                                                        .equals("author"));

                                                String author = parser.getElementText();
                                                if (sb.length() == 0) {
                                                    sb.append(author);
                                                } else {
                                                    sb.append(" and ");
                                                    sb.append(author);
                                                }
                                                assert (parser.getEventType() == XMLStreamConstants.END_ELEMENT);
                                                assert (parser.getLocalName().equals("author"));
                                                parser.nextTag();
                                                // current tag is either begin:author or
                                                // end:authors
                                            }
                                            ArrayList<String> authorsNew = new ArrayList<>();
                                            String authors = sb.toString();
                                            String[] a = authors.split(" and ");
                                            for (String author : a) {
                                                authorsNew.add(author);
                                            }
                                            ref.setAuthors(authorsNew);

                                        } else if (ln.equals("journal")) {
                                            ref.setJournalOrBooktitle(parser.getElementText());
                                        } else if (ln.equals("tech")) {
                                            // the content of the "tech" field seems to contain the number of the technical report
                                            ref.setTechReportNumber(parser.getElementText());
                                        } else if (ln.equals("doi")) {
                                            ref.setDOI(parser.getElementText());
                                        } else if (ln.equals("editor")) {
                                            ref.setEditors(listifyEditors(parser.getElementText()));
                                        } else if (ln.equals("institution")) {
                                            ref.setInstitution(parser.getElementText());
                                        } else if (ln.equals("location")) {
                                            ref.setLocation(parser.getElementText());
                                        } else if (ln.equals("number")) {
                                            ref.setNumber(parser.getElementText());
                                        } else if (ln.equals("note")) {
                                            ref.setNote(parser.getElementText());
                                        } else if (ln.equals("pages")) {
                                            ref.setPages(parser.getElementText());
                                        } else if (ln.equals("volume")) {
                                            ref.setVolume(parser.getElementText());
                                        } else if (ln.equals("publisher")) {
                                            ref.setPublisher(parser.getElementText());
                                        } else if (ln.equals("title")) {
                                            ref.setTitle(parser.getElementText());
                                            if (ref.getTitle().contains("-")) {
                                                //some title contained a different 'minus', so we reset it to the standard one
                                                String newTitle = ref.getTitle();//.replace('-', '\u002D');
                                                ref.setTitle(newTitle);
                                            }
                                        } else if (ln.equals("raw_string")) {
                                            ref.setOriginalString(parser.getElementText());
                                        } else if (ln.equals("year")) {
                                            ref.setYear(parser.getElementText());
                                        } else if (ln.equals("booktitle")) {
                                            String booktitle = parser.getElementText();
                                            if (booktitle.startsWith("In ")) {
                                                // special treatment for parsing of
                                                // "In proceedings of..." references
                                                booktitle = booktitle.substring(3);
                                            }
                                            ref.setJournalOrBooktitle(booktitle);
                                        }
                                        // all other tags are ignored
                                    }
                                    parser.next();
                                }
                                //checkIfNoteBelongsToSomeOtherPart(ref);
                                //checkIfTitleContainsAuthor(ref);
                                //checkTitleJournalSplitting(ref);
                                //searchForEditors(ref);
                                //improveFreeCiteSplitting(ref);
                                //checkIfPublisherIsWrong(ref);
                                //searchForDOI(ref);
                                //removeUnderscoreFromPersons(ref);
                                //isTitleEmpty(ref);
                                searchEconbizList.add(ref);
                            }
                            parser.next();
                        }
                        parser.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    out.close();
                    //reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return searchEconbizList;
            } else {
                System.out.print("Server of FreeCite not reachable! Please check your connection!");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("Server of FreeCite not reachable! Please check your connection!");
            return null;
        }
    }

    private void isTitleEmpty(InputReference ref) {
        if ((ref.getTitle() == null || ref.getTitle().equals("") && !ref.getJournalOrBooktitle().equals("")) ) {
            ref.setTitle(ref.getJournalOrBooktitle());
            ref.setJournalOrBooktitle("");
        }
    }

    private void removeUnderscoreFromPersons(InputReference ref) {
        ArrayList authors = ref.getAuthors();
        ArrayList newAuthors = new ArrayList();
        for (Object author : authors) {
            author.toString().replace("_", "");
            newAuthors.add(author);
        }
        ref.setAuthors(newAuthors);
    }

    private void searchForDOI(InputReference ref) {
        Pattern regex = Pattern.compile("https:\\/\\/doi.org\\/.*");
        Matcher regexMatcher = regex.matcher(ref.getOriginalString());
        if (regexMatcher.find()) {
            String doi = regexMatcher.group().trim().replace("https://doi.org/", "");
            ref.setDOI(doi);
        }
    }

    //If the title ends with "and [Example]", Example will be treated as an additional author
    private void checkIfTitleContainsAuthor(InputReference ref) {
        ArrayList<String> authors = ref.getAuthors();
        boolean found = false;
        String authorToRemove = "";
        if (ref.getOriginalString().contains("and")) {
            for (String author : authors) {
                if (ref.getOriginalString().contains(" and " + author) && author.split(", ").length == 1) {
                    authorToRemove = author;
                    found = true;
                }
            }
        }
        if (found) {
            authors.remove(authorToRemove);
            ref.setAuthors(authors);
            ref.setTitle(ref.getTitle() + " and " + authorToRemove);
        }

    }

    private void checkIfPublisherIsWrong(InputReference ref) {
        String title = ref.getTitle() + " " + ref.getPublisher();
        if (ref.getOriginalString().contains(title + ".")) {
            ref.setTitle(title);
            ref.setPublisher("");
        }
    }

    private void checkTitleJournalSplitting(InputReference ref) {
        String title = ref.getTitle();
        String journal = ref.getJournalOrBooktitle();
        String newTitle = "";
        String otherPart = "";
        //TODO improve this by excluding as many shortcuts as possible
        if (title.contains(".")) {
            newTitle = title.substring(0, title.indexOf("."));
            ref.setTitle(newTitle);
            otherPart = title.substring(title.indexOf(".")).substring(1).trim();
            findWherePartBelongsTo(ref, otherPart);
        } else if (!title.equals("") && !journal.equals("")) {
            String combined1 = title + " " + journal;
            String combined2 = title + journal;
            if (ref.getOriginalString().contains(combined1)) {
                if (journal.contains(".")) {
                    String newTitlePart = journal.substring(0, journal.indexOf("."));
                    String newJournalPart = journal.substring(journal.indexOf(".")).substring(1).trim();

                    ref.setTitle((title + " " + newTitlePart).replace("  ", " "));
                    ref.setJournalOrBooktitle(newJournalPart);
                } else {
                    ref.setTitle(combined1);
                    ref.setJournalOrBooktitle("");
                }
            } else if (ref.getOriginalString().contains(combined2)) {
                if (journal.contains(".")) {
                    String newTitlePart = journal.substring(0, title.indexOf("."));
                    String newJournalPart = journal.substring(title.indexOf(".")).substring(1).trim();

                    ref.setTitle((title + " " + newTitlePart).replace("  ", " "));
                    ref.setJournalOrBooktitle(newJournalPart);
                } else {
                    ref.setTitle(combined2);
                    ref.setJournalOrBooktitle("");
                }
            }
        }
    }

    private void findWherePartBelongsTo(InputReference ref, String otherPart) {
        String journal = ref.getJournalOrBooktitle();
        String original = ref.getOriginalString();
        String publisher = ref.getPublisher();
        String location = ref.getLocation();
        String institution = ref.getInstitution();

        if (!journal.equals("") && original.contains(otherPart + journal)) {
            ref.setJournalOrBooktitle(closeOpenBrackets(otherPart + journal));
        } else if (!journal.equals("") && original.contains(closeOpenBrackets(otherPart + " " + journal))) {
            ref.setJournalOrBooktitle(otherPart + " " + journal);
        }

        if (!publisher.equals("") && original.contains(otherPart + publisher)) {
            ref.setPublisher(closeOpenBrackets(otherPart + publisher));
        } else if (!publisher.equals("") && original.contains(otherPart + " " + publisher)) {
            ref.setPublisher(closeOpenBrackets(otherPart + " " + publisher));
        }

        if (!location.equals("") && original.contains(otherPart + location)) {
            ref.setLocation(closeOpenBrackets(otherPart + location));
        } else if (!location.equals("") && original.contains(otherPart + " " + location)) {
            ref.setLocation(closeOpenBrackets(otherPart + " " + location));
        }

        if (!institution.equals("") && original.contains(otherPart + institution)) {
            ref.setInstitution(closeOpenBrackets(otherPart + institution));
        } else if (!institution.equals("") && original.contains(otherPart + " " + institution)) {
            ref.setInstitution(closeOpenBrackets(otherPart + " " + institution));
        }
    }

    private void searchForEditors(InputReference ref) {
        String original = ref.getOriginalString();
        String editors = "";
        Pattern regex = Pattern.compile("\\([^\\(\\)]+Ed\\.+\\)");
        Matcher regexMatcher = regex.matcher(original);
        if (regexMatcher.find()) {
            editors = regexMatcher.group();
            editors = editors.replace("(", "");
            editors = editors.replace(")", "");
            ref.setEditors(listifyEditors(editors));
        }

        Pattern regex2 = Pattern.compile("\\([^\\(\\)]+Eds\\.+\\)");
        Matcher regexMatcher2 = regex2.matcher(original);
        if (regexMatcher2.find()) {
            editors = regexMatcher2.group();
            editors = editors.replace("(", "");
            editors = editors.replace(")", "");
            ref.setEditors(listifyEditors(editors));
        }
        //if (Pattern.matches("\\(.+Eds.\\)", original) || Pattern.matches("\\(.+Ed.\\)", original)) {
        //    String editors = original.
        //}

        //check if any other field contained the editors wrongly
        if (ref.getEditors().size() == 1) {
            String editor = ref.getEditors().get(0) + ", Ed";
            if (ref.getPublisher().equals(editor)) {
                ref.setPublisher("");
            }
        } else if (ref.getEditors().size() > 1) {
            String editor = "";
            for (String ed : ref.getEditors()) {
                editor = editor + ", " + ed;
            }
            editor = editor + ", Eds";
            editor = editor.trim();
            if (ref.getPublisher().equals(editor)) {
                ref.setPublisher("");
            }
        }
    }

    //TODO do this for all properties
    private void improveFreeCiteSplitting(InputReference ref) {
        String title = ref.getTitle();
        String journalBook = ref.getJournalOrBooktitle();
        title = closeOpenBrackets(title);
        journalBook = closeOpenBrackets(journalBook);
        if (ref.getOriginalString().contains(title)) {
            ref.setTitle(title);
        }
        if (ref.getOriginalString().contains(journalBook)) {
            ref.setJournalOrBooktitle(journalBook);
        }
    }

    private String closeOpenBrackets(String string) {
        if ((string.contains("(") && !string.contains(")"))) {
            string += ")";
        } else if (string.contains("[") && !string.contains("]")) {
            string += "]";
        } else if (string.contains("{") && !string.contains("}")) {
            string += "}";
        } else if (string.contains("\"")) {
            long occurences = string.chars().filter(num -> num == '\"').count();
            if (occurences % 2 == 1) {
                string += "\"";
            }
        } else if (string.contains("\'")) {
            long occurences = string.chars().filter(num -> num == '\'').count();
            if (occurences % 2 == 1) {
                string += "\'";
            }
        }
        return string;
    }
}
