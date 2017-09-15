import de.undercouch.citeproc.csl.CSLType;
import org.apache.commons.io.IOUtils;
import org.jbibtex.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.jbibtex.BibTeXEntry.*;


/**
 * Created by Uni on 14.12.2016.
 */

//Schefold, B. (2020). Contributions to the
// History of Economic Thought. London : Routledge.

public class EconbizHandler {

    public EconbizHandler() {
    }

    public EconBizReference searchEconbiz(InputReference ref) {
        if (ref.getTitle() != null) {
            try {
                if (testURL()) {
                    EconBizReference econRef = null;
                    //3 informations: 1 title, 2 author, 3 date
                    for (int i = 3; i > 0; i--) {
                        econRef = findEconBizReference(ref, i);
                        //stop searching if something was found
                        if (econRef != null) {
                            break;
                        }
                    }
                    if (econRef == null && ref.getJournalOrBooktitle().equals("") && ref.getTitle().contains(".")) {
                        //check again and make title smaller, if there was no journal or booktitle, because maybe freecite made the mistake
                        String title = ref.getTitle();
                        String titleSplit[] = title.split("\\.");
                        String newTitle = titleSplit[0];
                        InputReference newReference = ref;
                        newReference.setTitle(newTitle);
                        System.out.println("TRY NEW TITLE; SPLIT AT DOT!");
                        econRef = searchEconbiz(newReference);
                        if (econRef == null && titleSplit.length > 1) {
                            String newTitle2 = titleSplit[1];
                            InputReference newReference2 = ref;
                            newReference2.setTitle(newTitle2);
                            System.out.println("TRY NEW TITLE; SPLIT AT DOT! PART TWO!");
                            econRef = searchEconbiz(newReference2);
                        }
                    }
                    return econRef;
                } else {
                    System.out.println("Server of Econbiz not reachable! Please check your connection!");
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROR! Server of Econbiz not reachable! Please check your connection!");
                return null;
            }
        } else {
            System.out.println("Sorry, FreeCite could not find the title and compare it to a Reference!");
            return null;
        }
    }

    private EconBizReference findEconBizReference(InputReference ref, int numberOfInformation) {
        EconBizReference econRef = new EconBizReference();
        String title = ref.getTitle();
        String title_replaced = title.replace(" ", "+");
        title_replaced = title_replaced.replace("\"", "");
        String encodedTitle = "";
        String date = ref.getYear();
        try {
            encodedTitle = URLEncoder.encode(title_replaced, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //TODO what if this happens???
        }
        String encodedDate = "";
        try {
            encodedDate = URLEncoder.encode(date, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
        String encodedAuthor = "";
        String url = "https://api.econbiz.de/v1/search?q=title%3A%22" + encodedTitle + "%22";

        //when numberOfInformation is more then 1, the author should be added
        if (!ref.getAuthors().isEmpty() && ref.getAuthors() != null && numberOfInformation > 1) {
            String first_author = String.valueOf(ref.getAuthors().get(0));
            String nameParts[] = first_author.split(" ");
            String lastname = nameParts[nameParts.length - 1];
            try {
                encodedAuthor = URLEncoder.encode(lastname, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                encodedAuthor = lastname;
            }
            url = url + "+AND+" + "person%3A%22" + encodedAuthor + "%22";
        }
        //when numberOfInformation is more then 2, the date should be added
        if (date != "" && date != null && numberOfInformation > 2) {
            url = url + "+AND+" + "date%3A%22" + encodedDate + "%22";
        }
        //This can not be added, because it is just in the the isPartOf which must be fully added and contains unknown information
        //if (journal != "" && journal != null) {
        //    url = url + "+AND+" + "isPartOf%3A%22" + journal + "%22";
        //}
        //if (pages != "" && pages != null) {
        //    url = url + "+AND+" + "isPartOf%3A%22" + pages.replace("--", "-") + "%22";
        //}
        //if (volume != "" && volume != null) {
        //    url = url + "+AND+" + "isPartOf%3A%22" + volume + "%22";
        //}
        //String url = "https://api.econbiz.de/v1/search?q=title%3A%22" + title_replaced + "%22" + "+AND+" + "person%3A%22" + lastname + "%22" + "&spellcheck=on&echo=on";

        url = url + "&spellcheck=on&echo=on";
        URL urlReady = null;
        try {
            urlReady = new URL(url);
            System.out.println("URL EconBiz: " + urlReady);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String s = null;
        try {
            s = IOUtils.toString(URI.create(url), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(s);

            //check if something was found
            if (((JSONArray) ((JSONObject) json.get("hits")).get("hits")).size() > 0) {
                JSONObject value = (JSONObject) ((JSONArray) ((JSONObject) json.get("hits")).get("hits")).get(0);
                String titleEcon = (String) value.get("title");
                econRef.setTitle(titleEcon);

                String idEcon = (String) value.get("id");
                econRef.setID(idEcon);
                //TODO check for all if they are null
                String yearEcon = (String) ((JSONArray) value.get("date")).get(0);
                econRef.setYear(yearEcon);
                JSONArray publ = (JSONArray) value.get("publisher");
                //TODO check for all of them like this
                String publisherEcon = "";
                if (publ != null) {
                    publisherEcon = String.valueOf(publ.get(0));
                }
                econRef.setPublisher(publisherEcon);

                String typeEcon = (String) value.get("type");
                JSONArray typeEconGenre = (JSONArray) value.get("type_genre");
                //this are all available types in econBiz
                econRef.setType(chooseType(typeEcon, typeEconGenre));

                //TODO check what is better to use, maybe change to other way round person, or creator
                JSONArray authors = (JSONArray) value.get("person");
                if (authors == null) {
                    authors = (JSONArray) value.get("creator");
                }
                ArrayList authorsEcon = new ArrayList();
                if (authors != null) {
                    for (int m = 0; m < authors.size(); m++) {
                        authorsEcon.add((String) authors.get(m));
                    }
                }
                econRef.setAuthors(authorsEcon);
                loadMissingInformationFromBibTex(econRef, idEcon);
                System.out.println("TYPE:" + econRef.getType());
            } else if (((JSONArray) ((JSONObject) json.get("spellcheck")).get("suggestions")).size() > 0) {
                JSONObject value = (JSONObject) ((JSONArray) ((JSONObject) json.get("spellcheck")).get("suggestions")).get(0);
                String titleEcon = (String) value.get("collation");

                //String titleEconNew = titleEcon.substring(7, titleEcon.length() - 1);
                String parts[] = titleEcon.replace("+", " ").split(" AND ");
                InputReference newInputRef = null;
                try {
                    newInputRef = (InputReference) ref.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    System.out.println("NOTHING FOUND IN ECONBIZ FOR COMPARISON");
                    return null;
                }

                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].contains("title")) {
                        String newTitle = parts[i].substring(7, parts[i].length() - 1);
                        newInputRef.setTitle(newTitle);
                    } else if (parts[i].contains("person")) {
                        String newPerson = parts[i].substring(8, parts[i].length() - 1);
                        ArrayList newAuthors = new ArrayList();
                        newAuthors.add(newPerson);
                        newInputRef.setAuthors(newAuthors);
                    } else if (parts[i].contains("date")) {
                        String newDate = parts[i].substring(6, parts[i].length() - 1);
                        newInputRef.setYear(newDate);
                    }
                }
                //search again with new suggested title
                econRef = searchEconbiz(newInputRef);
            } else {
                System.out.println("NOTHING FOUND IN ECONBIZ FOR COMPARISON");
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //this minus is a so called em-dash, and different from the normal one
        // therefore it has to be replaced by the normal one for comparison
        if (econRef.getTitle().contains("–")) {
            String newTitle = econRef.getTitle().replace('–', '\u002D');
            econRef.setTitle(newTitle);
        }
        return econRef;
    }

    private void loadMissingInformationFromBibTex(EconBizReference ref, String idEcon) {
        String url = "https://www.econbiz.de/Record/" + idEcon + "/Export?style=BibTeX";
        JGet downloader = new JGet();
        String bibtex = downloader.downloadBibTex(url);
        bibtex = bibtex.replace("@misc", "@" + chooseTypeFromCSLToBibTeX(ref.getType()));
        System.out.println(bibtex);
        ref.setBibtex(bibtex);
        StringReader reader = new StringReader(bibtex);
        BibTeXDatabase database = new BibTeXDatabase();

        try {
            BibTeXParser bibtexParser = new BibTeXParser();
            database = bibtexParser.parse(reader);
        } catch (org.jbibtex.ParseException e) {
            e.printStackTrace();
        }

        Map<Key, BibTeXEntry> entryMap = database.getEntries();
        Collection<BibTeXEntry> entries = entryMap.values();
        for (org.jbibtex.BibTeXEntry entry : entries) {
            org.jbibtex.Value pages = entry.getField(KEY_PAGES);
            org.jbibtex.Value journal = entry.getField(KEY_JOURNAL);
            org.jbibtex.Value volume = entry.getField(KEY_VOLUME);
            org.jbibtex.Value booktitle = entry.getField(KEY_BOOKTITLE);
            org.jbibtex.Value chapter = entry.getField(KEY_CHAPTER);
            org.jbibtex.Value editor = entry.getField(KEY_EDITOR);
            org.jbibtex.Value DOI = entry.getField(KEY_DOI);
            org.jbibtex.Value institution = entry.getField(KEY_INSTITUTION);
            org.jbibtex.Value number = entry.getField(KEY_NUMBER);
            //TODO check if location is really address;
            org.jbibtex.Value location = entry.getField(KEY_ADDRESS);


            if (pages != null) {
                ref.setPages(pages.toUserString());
            }
            if (journal != null) {
                ref.setJournalOrBooktitle(journal.toUserString());
            } else if (booktitle != null) {
                ref.setJournalOrBooktitle(booktitle.toUserString());
            }
            if (volume != null) {
                ref.setVolume(volume.toUserString());
            }
            if (chapter != null) {
                ref.setChapter(chapter.toUserString());
            }
            if (editor != null) {
                ArrayList editorsEcon = new ArrayList();
                String[] editors = editor.toUserString().split(" and ");
                for (int m = 0; m < editors.length; m++) {
                    editorsEcon.add((String) editors[m]);
                }

                ref.setEditors(editorsEcon);
            }
            if (DOI != null) {
                ref.setDOI(DOI.toUserString());
            }
            if (institution != null) {
                ref.setInstitution(institution.toUserString());
            }
            if (number != null) {
                ref.setNumber(number.toUserString());
            }
            if (location != null) {
                ref.setLocation(location.toUserString());
            }
        }
    }

    //code duplication, evtl add neue klasse dafür
    private String chooseTypeFromCSLToBibTeX(CSLType type) {
        switch (type) {
            case ARTICLE:
                return "misc";
            case ARTICLE_JOURNAL:
                return "article";
            case CHAPTER:
                return "incollection";
            case BOOK:
                return "book";
            default:
                return "misc";
        }
    }

    private CSLType chooseType(String type, JSONArray typeGenre) {
        type = type.toLowerCase();
        switch (type) {
            case "book":
                return CSLType.BOOK;
            case "article":
                if (typeGenre != null && (typeGenre.contains("Aufsatz im Buch")
                        || typeGenre.contains("Article in collection")
                        || typeGenre.contains("Article in book"))) {
                    return CSLType.CHAPTER;
                }
                return CSLType.ARTICLE_JOURNAL;
            case "event":
                return CSLType.ARTICLE;
            case "journal":
                //todo check if there is a better type for this
                //this contains normally a title, author and a publisher, doi and year nothing else
                return CSLType.ARTICLE_JOURNAL;
            case "other":
                return CSLType.ARTICLE;
            default:
                return CSLType.ARTICLE;
        }
    }

    public boolean testURL() throws Exception {
        try {
            final URL url = new URL("http://www.econbiz.de");
            final URLConnection conn = url.openConnection();
            conn.connect();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }
}
