import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.csl.CSLItemDataBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Uni on 07.07.2017.
 */
public class StyleFormatter {

    private CitationStyle style;
    public StyleFormatter(CitationStyle style) {
        this.style = style;
    }

    public void changeEconBizResultsToFormat(EconBizReference econBizRef) {
        econBizRef.setAuthors(formatEconBizNames(econBizRef.getAuthors()));
        econBizRef.setYear(formatDate(econBizRef.getYear()));
        econBizRef.setEditors(formatEconBizNames(econBizRef.getEditors()));
        //also title, journal and booktitle because of the uppercase and lowercase writing
        econBizRef.setTitle(loadFormattedCSLTitle(econBizRef));
        econBizRef.setJournalOrBooktitle(loadFormattedCSLJournalBooktitle(econBizRef));
    }

    private String loadFormattedCSLJournalBooktitle(EconBizReference ref) {
        CSLItemData item = new CSLItemDataBuilder()
                .containerTitle(ref.getJournalOrBooktitle())
                .type(ref.getType())
                .build();

        String bibl = "";
        try {
            bibl = CSL.makeAdhocBibliography(style.getValue(), item).makeString();
            bibl = bibl.replaceAll("<((?!>).)*>", "");
            bibl = bibl.replace("(n.d.)", "");
            bibl = bibl.replace("\n", "");
            bibl = bibl.replace("\\&#38;", "&");
            if (ref.getJournalOrBooktitle().contains(".")) {
                //TODO finde unabhängige version für andere reference styles
                bibl = bibl.trim().substring(2, bibl.length()-4);
            } else {
                bibl = bibl.replace(".", "");
            }
            //bibl = bibl.replace("\u202f", " ");
            bibl = bibl.trim();
            System.out.println("Formatted Journal: " + bibl);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bibl;
    }

    private String loadFormattedCSLTitle(EconBizReference ref) {
        CSLItemData item = new CSLItemDataBuilder()
                .title(ref.getTitle())
                .type(ref.getType())
                .build();

        String bibl = "";
        try {
            bibl = CSL.makeAdhocBibliography(style.getValue(), item).makeString();
            bibl = bibl.replaceAll("<((?!>).)*>", "");
            bibl = bibl.replace("(n.d.)", "");
            bibl = bibl.replace("\n", "");
            bibl = bibl.replace("\\&#38;", "&");
            if (ref.getJournalOrBooktitle().contains(".")) {
                bibl = bibl.trim().substring(0, bibl.length()-5);
            } else {
                bibl = bibl.replace(".", "");
            }
            //bibl = bibl.replace("\u202f", " ");

            bibl = bibl.trim();
            System.out.println("Formatted Title: " + bibl);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bibl;
    }

    private String formatDate(String year) {
        String newYear = year;
        if (year.toCharArray().length > 4) {
            Pattern regex = Pattern.compile("\\d\\d\\d\\d");
            Matcher regexMatcher = regex.matcher(year);
            if (regexMatcher.find()) {
                newYear = regexMatcher.group();
            }
        }
        return newYear;
    }

    private ArrayList<String> formatEconBizNames(ArrayList persons) {
        ArrayList authorsEconShortFirstnames = new ArrayList<>();
        for (Object person : persons) {
            String firstname = "";
            String lastname = "";
            String[] nameParts;
            if (person.toString().contains(", ")) {
                nameParts = person.toString().split(", ");
                lastname = nameParts[0];
                firstname = nameParts[1];
            } else {
                nameParts = person.toString().split(" ");
                lastname = nameParts[nameParts.length - 1];
                firstname = nameParts[0].substring(0, 1);
                if (nameParts.length > 2) {
                    for (int i = 1; i <= nameParts.length - 2; i++) {
                        firstname += nameParts[i].substring(0, 1);
                    }
                }
            }
            //for the case that the author has more than one firstname
            authorsEconShortFirstnames.add(loadFormattetCSL(firstname, lastname));
        }
        return authorsEconShortFirstnames;
    }

    private String loadFormattetCSL(String firstname, String lastname) {
        CSLItemData item = new CSLItemDataBuilder()
                .author(firstname, lastname)
                .build();

        String bibl = "";
        try {
            bibl = CSL.makeAdhocBibliography(style.getValue(), item).makeString();
            bibl = bibl.replaceAll("<((?!>).)*>", "");
            bibl = bibl.replace("(n.d.)", "");
            bibl = bibl.replace(". .", ".");
            bibl = bibl.trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bibl;
    }
    /*private ArrayList shortenNames(ArrayList authorsEcon) {
        ArrayList authorsEconShortFirstnames = new ArrayList<>();
        for (Object author : authorsEcon) {
            String firstname = "";
            String lastname = "";
            String[] nameParts;
            if (author.toString().contains(", ")) {
                nameParts = author.toString().split(", ");
                lastname = nameParts[0];
                firstname = nameParts[1].replace(".", "");
                if (checkIfOnlyCapitals(firstname)) {
                    firstname = firstname.replace(" ", "");
                } else {
                    if (firstname.contains(" ")) {
                        String firstnames[] = firstname.split(" ");
                        String newFirstname = "";
                        for (int i = 0; i <= firstnames.length - 1; i++) {
                            newFirstname += firstnames[i].substring(0, 1);
                        }
                        firstname = newFirstname;
                    } else {
                        firstname = firstname.substring(0, 1);
                    }
                }
                if (nameParts.length > 2) {
                    for (int i = 2; i <= nameParts.length - 1; i++) {
                        firstname += nameParts[i].substring(0, 1);
                    }
                }
            } else {
                nameParts = author.toString().split(" ");
                lastname = nameParts[nameParts.length - 1];
                firstname = nameParts[0].substring(0, 1);
                if (nameParts.length > 2) {
                    for (int i = 1; i <= nameParts.length - 2; i++) {
                        firstname += nameParts[i].substring(0, 1);
                    }
                }
                if (checkIfOnlyCapitals(firstname)) {
                    //do nothing
                } else {
                    if (firstname.contains(" ")) {
                        String firstnames[] = firstname.split(" ");
                        String newFirstname = "";
                        for (int i = 0; i <= firstnames.length - 1; i++) {
                            newFirstname += firstnames[i].substring(0, 1);
                        }
                        firstname = newFirstname;
                    } else {
                        firstname = firstname.substring(0, 1);
                    }
                }
            }
            //for the case that the author has more than one firstname

            String newAuthor = lastname + ", " + firstname;
            System.out.println(newAuthor);
            authorsEconShortFirstnames.add(newAuthor);
        }
        return authorsEconShortFirstnames;
    }

    private boolean checkIfOnlyCapitals(String firstname) {
        for (Character character : firstname.toCharArray()) {
            if (!Character.isUpperCase(character)) {
                return false;
            }
        }
        return true;
    }

    private String formatDate(String year) {
        String newYear = year;
        if (year.toCharArray().length > 4) {
            newYear = year.substring(0, 4);
        }
        return newYear;
    }*/

    public void changeFreeCiteResultsToFormat(InputReference freeCiteRef) {
        freeCiteRef.setAuthors(formatNamesFreeCite(freeCiteRef.getAuthors()));
        freeCiteRef.setEditors(formatNamesFreeCite(freeCiteRef.getEditors()));
    }

    private ArrayList<String> formatNamesFreeCite(ArrayList authors) {
        ArrayList newAuthors = new ArrayList();
        for (Object author : authors) {
            // first author
            String firstname = "";
            String lastname;
            String[] nameParts = author.toString().split(" ");
            lastname = nameParts[nameParts.length - 1];
            if (nameParts.length >= 2) {
                firstname = nameParts[0];
                //for the case that the author has more than one firstname
                if (nameParts.length > 2) {
                    for (int i = 1; i <= nameParts.length - 2; i++) {
                        firstname += " " + nameParts[i];
                    }
                }
            }
            newAuthors.add(loadFormattetCSL(firstname, lastname));
        }
        return newAuthors;
    }

    /*
    private ArrayList formatNamesFreeCite(ArrayList authors) {
        ArrayList newAuthors = new ArrayList();
        for (Object author : authors) {
            // first author
            String firstname = "";
            String lastname;
            String[] nameParts = author.toString().split(" ");
            lastname = nameParts[nameParts.length - 1];
            if (nameParts.length >= 2) {
                firstname = nameParts[0];
                //for the case that the author has more than one firstname
                if (nameParts.length > 2) {
                    for (int i = 1; i <= nameParts.length - 2; i++) {
                        firstname += nameParts[i];
                    }
                }
            }
            String newAuthor;
            if (firstname != "") {
                newAuthor = lastname + ", " + firstname.replace(".", "");
            } else {
                newAuthor = lastname;
            }
            newAuthors.add(newAuthor);
        }
        return newAuthors;
    }*/

}

