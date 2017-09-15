import com.sun.deploy.util.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jbibtex.*;

import java.io.*;
import java.util.*;

/**
 * Created by Uni on 10.01.2017.
 */
public class MistakeAdder {
    // TODO wichtig ist es die IDs zu ändern, da sie nicht doppelt sein dürfen

    private Collection<BibTeXEntry> entries;
    private String path;

    public MistakeAdder(String path) throws IOException {
        this.path = path;
        FileReader fr = new FileReader(this.path + "\\bibliographyCorrect.txt");
        BufferedReader br = new BufferedReader(fr);

        org.jbibtex.BibTeXDatabase database = new org.jbibtex.BibTeXDatabase();
        try {
            BibTeXParser bibtexParser = new BibTeXParser();
            database = bibtexParser.parse(br);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Map<Key, BibTeXEntry> entryMap = database.getEntries();

        this.entries = entryMap.values();

        //code for checking the values
        /*for(org.jbibtex.BibTeXEntry entry : this.entries){

            org.jbibtex.Value author = entry.getField(BibTeXEntry.KEY_AUTHOR);
            org.jbibtex.Value title = entry.getField(org.jbibtex.BibTeXEntry.KEY_TITLE);
            if(title == null && author == null){
                continue;
            }
            // Do something with the title value
        }*/

        br.close();
    }

    //    Semantische Fehler:
    //    F1: Vor- und Nachname vertauscht – Semantikfehler
    //    Simon Stefan (1987). Using stochastic dominance to
    //    evaluate the performance of portfolios with options. Financial Analysts
    //    Journal, 43, 79–82.
    public void changeNamesOrder() throws IOException {

        org.jbibtex.BibTeXDatabase database = new org.jbibtex.BibTeXDatabase();
        Random rnd = new Random();

        Collection<BibTeXEntry> entriesChangeNamesOrder = cloneEntryCollection();

        for (org.jbibtex.BibTeXEntry entry : entriesChangeNamesOrder) {

            org.jbibtex.Value author = entry.getField(BibTeXEntry.KEY_AUTHOR);
            if (author == null) {
                System.out.println("Category F1: Author empty!");
                continue;
            }

            String[] authors = author.toUserString().split(" and ");
            int chooseAuthor = rnd.nextInt(authors.length);
            String chosenAuthor = authors[chooseAuthor];

            String[] names = chosenAuthor.split(", ");
            if (names.length >= 2) {
                String firstname = names[1];

                String lastname = names[0];

                String newLastname = firstname;
                String newFirstname = lastname;

                String newAuthorsValue = "";
                String switchedAuthor = newLastname + ", " + newFirstname;

                for (int i = 0; i < authors.length; i++) {
                    if (i == 0) {
                        if (i == chooseAuthor) {
                            newAuthorsValue = switchedAuthor;
                        } else {
                            newAuthorsValue = authors[i];
                        }
                    } else {
                        if (i == chooseAuthor) {
                            newAuthorsValue = newAuthorsValue + " and " + switchedAuthor;
                        } else {
                            newAuthorsValue = newAuthorsValue + " and " + authors[i];
                            newAuthorsValue = getRidOffSpaces(newAuthorsValue);
                        }
                    }
                }

                org.jbibtex.StringValue authorNew = new org.jbibtex.StringValue(newAuthorsValue, StringValue.Style.BRACED);
                entry.addField(BibTeXEntry.KEY_AUTHOR, authorNew);
                entry = addCategoryNote(entry, "F1");
                database.addObject(entry);
            } else {
                System.out.println("Error! Not enough Names to switch!");
            }
        }

        Writer writer = new FileWriter(path + "\\order_names.txt");
        Writer bibWriter = new FileWriter(path + "\\order_names.bib");
        org.jbibtex.BibTeXFormatter bibtexFormatter = new org.jbibtex.BibTeXFormatter();

        bibtexFormatter.format(database, writer);
        bibtexFormatter.format(database, bibWriter);
        System.out.println("F1 DONE");

    }

    private Collection<BibTeXEntry> cloneEntryCollection() {
        Collection<BibTeXEntry> clonedEntries = new ArrayList<BibTeXEntry>();

        for (org.jbibtex.BibTeXEntry entry : this.entries) {
            BibTeXEntry newEntry = new BibTeXEntry(entry.getType(), entry.getKey());
            newEntry.addAllFields(entry.getFields());
            clonedEntries.add(newEntry);
        }
        return clonedEntries;
    }


//    F2: Falscher Name – Semantikfehler
//    Wrong, N., Levy, H., & Yoder, J. (1987). Using stochastic dominance to
//    evaluate the performance of portfolios with options. Financial Analysts
//    Journal, 43, 79–82.

    public void changeWrongPerson() throws IOException {

        org.jbibtex.BibTeXDatabase database = new org.jbibtex.BibTeXDatabase();
        Collection<BibTeXEntry> entriesWrongNames = cloneEntryCollection();
        for (org.jbibtex.BibTeXEntry entry : entriesWrongNames) {

            org.jbibtex.Value author = entry.getField(BibTeXEntry.KEY_AUTHOR);
            if (author == null) {
                continue;
            }

            String[] names = author.toUserString().split(" and ");

            String newLastname = "Wrong";
            String newFirstname = "N.";

            if (names.length == 1) {
                newLastname += RandomStringUtils.randomAlphabetic(2);
                newFirstname = RandomStringUtils.randomAlphabetic(1);
            }
            String newAuthorsValue = "";
            String newAuthor = newLastname + ", " + newFirstname;
            Random rnd = new Random();
            int decideValue = rnd.nextInt(names.length);
            for (int i = 0; i < names.length; i++) {
                if (i != decideValue) {
                    if (newAuthorsValue.equals("")) {
                        newAuthorsValue = names[i];
                    } else {
                        newAuthorsValue = newAuthorsValue + " and " + names[i];
                    }
                } else {
                    if (newAuthorsValue.equals("")) {
                        newAuthorsValue = newAuthor;
                    } else {
                        newAuthorsValue = newAuthorsValue + " and " + newAuthor;
                    }
                }
            }

            org.jbibtex.StringValue authorNew = new org.jbibtex.StringValue(newAuthorsValue, StringValue.Style.BRACED);
            entry.addField(BibTeXEntry.KEY_AUTHOR, authorNew);
            entry = addCategoryNote(entry, "F2");
            database.addObject(entry);
        }

        Writer writer = new FileWriter(path + "\\wrong_person.txt");
        Writer bibWriter = new FileWriter(path + "\\wrong_person.bib");

        org.jbibtex.BibTeXFormatter bibtexFormatter = new org.jbibtex.BibTeXFormatter();

        bibtexFormatter.format(database, writer);
        bibtexFormatter.format(database, bibWriter);
        System.out.println("F2 DONE");

    }

    public void changeAdditionalPerson() throws IOException {

        org.jbibtex.BibTeXDatabase database = new org.jbibtex.BibTeXDatabase();
        Collection<BibTeXEntry> entriesWrongNames = cloneEntryCollection();
        for (org.jbibtex.BibTeXEntry entry : entriesWrongNames) {

            org.jbibtex.Value author = entry.getField(BibTeXEntry.KEY_AUTHOR);
            if (author == null) {
                continue;
            }

            String[] names = author.toUserString().split("and");

            String newLastname = "Additional";
            String newFirstname = "P.";

            String newAuthor = newLastname + ", " + newFirstname;
            Random rnd = new Random();
            int decideValue = rnd.nextInt(names.length);
            String newAuthorsValue;
            if (decideValue == 0) {
                newAuthorsValue = newAuthor + " and " + names[0]; 
            } else {
                newAuthorsValue = names[0];
            }
            for (int i = 1; i < names.length; i++) {
                if (decideValue == i) {
                    newAuthorsValue = newAuthorsValue + " and " + newAuthor;
                }
                newAuthorsValue = newAuthorsValue + " and " + names[i];

            }

            org.jbibtex.StringValue authorNew = new org.jbibtex.StringValue(newAuthorsValue, StringValue.Style.BRACED);
            entry.addField(BibTeXEntry.KEY_AUTHOR, authorNew);
            entry = addCategoryNote(entry, "F2");
            database.addObject(entry);
        }

        Writer writer = new FileWriter(path + "\\additional_person.txt");
        Writer bibWriter = new FileWriter(path + "\\additional_person.bib");

        org.jbibtex.BibTeXFormatter bibtexFormatter = new org.jbibtex.BibTeXFormatter();

        bibtexFormatter.format(database, writer);
        bibtexFormatter.format(database, bibWriter);
        System.out.println("F2 DONE");

    }


    public void changeMissingPerson() throws IOException {

        org.jbibtex.BibTeXDatabase database = new org.jbibtex.BibTeXDatabase();
        Collection<BibTeXEntry> entriesWrongNames = cloneEntryCollection();
        for (org.jbibtex.BibTeXEntry entry : entriesWrongNames) {

            org.jbibtex.Value author = entry.getField(BibTeXEntry.KEY_AUTHOR);
            if (author == null) {
                continue;
            }

            String[] names = author.toUserString().split("and");
            
            Random rnd = new Random();
            int decideValue = rnd.nextInt(names.length);
            String newAuthorsValue = "";
            for (int i = 0; i < names.length; i++) {
                if (decideValue != i) {
                    if (newAuthorsValue.equals("")) {
                        newAuthorsValue = names[i];
                    }
                    newAuthorsValue = newAuthorsValue + " and " + names[i];
                }

            }

            org.jbibtex.StringValue authorNew = new org.jbibtex.StringValue(newAuthorsValue, StringValue.Style.BRACED);
            entry.addField(BibTeXEntry.KEY_AUTHOR, authorNew);
            entry = addCategoryNote(entry, "F2");
            database.addObject(entry);
        }

        Writer writer = new FileWriter(path + "\\missing_person.txt");
        Writer bibWriter = new FileWriter(path + "\\missing_person.bib");

        org.jbibtex.BibTeXFormatter bibtexFormatter = new org.jbibtex.BibTeXFormatter();

        bibtexFormatter.format(database, writer);
        bibtexFormatter.format(database, bibWriter);
        System.out.println("F2 DONE");

    }

//    F3: Fehlende/falsche/zusätzliche Informationen
//    Brooks, R., Levy, H., & Yoder, J. (1987). Using stochastic dominance to
//    evaluate the performance of portfolios with options. Financial Analysts
//    Journal, 43. Fehlende Seitenzahlen

    public void changeWrongInformation() throws IOException {

        org.jbibtex.BibTeXDatabase database = new org.jbibtex.BibTeXDatabase();
        Random rnd = new Random();
        Collection<BibTeXEntry> entriesWrongInformation = cloneEntryCollection();
        //decide what to change

        for (org.jbibtex.BibTeXEntry entry : entriesWrongInformation) {

            //org.jbibtex.Value author = entry.getField(BibTeXEntry.KEY_AUTHOR);
            org.jbibtex.Value title = entry.getField(BibTeXEntry.KEY_TITLE);
            org.jbibtex.Value year = entry.getField(BibTeXEntry.KEY_YEAR);
            org.jbibtex.Value journal = entry.getField(BibTeXEntry.KEY_JOURNAL);
            org.jbibtex.Value booktitle = entry.getField(BibTeXEntry.KEY_BOOKTITLE);
            org.jbibtex.Value pages = entry.getField(BibTeXEntry.KEY_PAGES);
            org.jbibtex.Value volume = entry.getField(BibTeXEntry.KEY_VOLUME);
            org.jbibtex.Value number = entry.getField(BibTeXEntry.KEY_NUMBER);
            //org.jbibtex.Value editor = entry.getField(BibTeXEntry.KEY_EDITOR);

            boolean deleted = false;
            int decideValue = rnd.nextInt(6);
            int counter = 0;
            while (!deleted && counter < 6) {
                switch (decideValue) {
                    case 0:
                        if ((booktitle != null)) {
                            entry.addField(BibTeXEntry.KEY_BOOKTITLE, new StringValue("I am the wrong booktitle", StringValue.Style.BRACED));
                            deleted = true;
                            break;
                        } else if (journal != null) {
                            entry.addField(BibTeXEntry.KEY_JOURNAL, new StringValue("I am the wrong journal title", StringValue.Style.BRACED));
                            deleted = true;
                            break;
                        }
                    case 1:
                        if (title != null) {
                            entry.addField(BibTeXEntry.KEY_TITLE, new StringValue("I am the wrong Title", StringValue.Style.BRACED));
                            deleted = true;
                            break;
                        }
                    case 2:
                        if (year != null) {
                            entry.addField(BibTeXEntry.KEY_YEAR, new StringValue("56101", StringValue.Style.BRACED));
                            deleted = true;
                            break;
                        }
                    case 3:
                        if (number != null) {
                            entry.addField(BibTeXEntry.KEY_NUMBER, new StringValue("123456", StringValue.Style.BRACED));
                            deleted = true;
                            break;
                        }
                    case 4:
                        if (volume == null) {
                            entry.addField(BibTeXEntry.KEY_VOLUME, new StringValue("654321", StringValue.Style.BRACED));
                            deleted = true;
                            break;
                        }
                    case 5:
                        if (pages != null) {
                            entry.addField(BibTeXEntry.KEY_PAGES, new StringValue("999--555", StringValue.Style.BRACED));
                            deleted = true;
                            break;
                        }
                }
                entry = addCategoryNote(entry, "F3");
                if (deleted == true) {
                    database.addObject(entry);
                }
                counter++;
                decideValue = (decideValue + 1) % 6;
            }
        }

        Writer writer = new FileWriter(path + "\\wrong_information.txt");
        org.jbibtex.BibTeXFormatter bibtexFormatter = new org.jbibtex.BibTeXFormatter();
        bibtexFormatter.format(database, writer);
        Writer bibWriter = new FileWriter(path + "\\wrong_information.bib");
        bibtexFormatter.format(database, bibWriter);
        System.out.println("F3 DONE");
    }

    public void changeMissingInformation() throws IOException {

        org.jbibtex.BibTeXDatabase database = new org.jbibtex.BibTeXDatabase();
        Random rnd = new Random();
        Collection<BibTeXEntry> entriesMissingInformation = cloneEntryCollection();
        //decide what to change

        for (org.jbibtex.BibTeXEntry entry : entriesMissingInformation) {

            org.jbibtex.Value author = entry.getField(BibTeXEntry.KEY_AUTHOR);
            org.jbibtex.Value title = entry.getField(BibTeXEntry.KEY_TITLE);
            org.jbibtex.Value year = entry.getField(BibTeXEntry.KEY_YEAR);
            org.jbibtex.Value journal = entry.getField(BibTeXEntry.KEY_JOURNAL);
            org.jbibtex.Value booktitle = entry.getField(BibTeXEntry.KEY_BOOKTITLE);
            org.jbibtex.Value pages = entry.getField(BibTeXEntry.KEY_PAGES);
            org.jbibtex.Value volume = entry.getField(BibTeXEntry.KEY_VOLUME);
            org.jbibtex.Value number = entry.getField(BibTeXEntry.KEY_NUMBER);
            org.jbibtex.Value editor = entry.getField(BibTeXEntry.KEY_EDITOR);

            boolean deleted = false;
            int decideValue = rnd.nextInt(8);
            int counter = 0;
            while (!deleted && counter < 8) {
                switch (decideValue) {
                    case 0:
                        if ((booktitle != null)) {
                            entry.removeField(BibTeXEntry.KEY_BOOKTITLE);
                            //entry.addField(BibTeXEntry.KEY_BOOKTITLE, new StringValue(""));
                            deleted = true;
                            break;
                        } else if (journal != null) {
                            entry.removeField(BibTeXEntry.KEY_JOURNAL);
                            //entry.addField(BibTeXEntry.KEY_JOURNAL, null);
                            deleted = true;
                            break;
                        }
                    case 1:
                        if (title != null) {
                            entry.removeField(BibTeXEntry.KEY_TITLE);
                           // entry.addField(BibTeXEntry.KEY_TITLE, null);
                            deleted = true;
                            break;
                        }
                    case 2:
                        if (year != null) {
                            entry.removeField(BibTeXEntry.KEY_YEAR);
                            //entry.addField(BibTeXEntry.KEY_YEAR, null);
                            deleted = true;
                            break;
                        }
                    case 3:
                        if (number != null) {
                            entry.removeField(BibTeXEntry.KEY_NUMBER);
                            //entry.addField(BibTeXEntry.KEY_NUMBER, null);
                            deleted = true;
                            break;
                        }
                    case 4:
                        if (volume == null) {
                            entry.removeField(BibTeXEntry.KEY_VOLUME);
                            //entry.addField(BibTeXEntry.KEY_VOLUME, null);
                            deleted = true;
                            break;
                        }
                    case 5:
                        if (pages != null) {
                            entry.removeField(BibTeXEntry.KEY_PAGES);
                            //entry.addField(BibTeXEntry.KEY_PAGES, null);
                            deleted = true;
                            break;
                        }
                    case 6:
                        if (author != null) {
                            entry.removeField(BibTeXEntry.KEY_AUTHOR);
                            //entry.addField(BibTeXEntry.KEY_AUTHOR, null);
                            deleted = true;
                            break;
                        }
                    case 7:
                        if (editor != null) {
                            entry.removeField(BibTeXEntry.KEY_EDITOR);
                            //entry.addField(BibTeXEntry.KEY_EDITOR, null);
                            deleted = true;
                            break;
                        }
                }
                entry = addCategoryNote(entry, "F3");
                if (deleted == true) {
                    database.addObject(entry);
                } else {
                    decideValue = (decideValue + 1) % 8;
                }
                counter++;
            }
        }

        Writer writer = new FileWriter(path + "\\missing_information.txt");
        org.jbibtex.BibTeXFormatter bibtexFormatter = new org.jbibtex.BibTeXFormatter();
        bibtexFormatter.format(database, writer);
        Writer bibWriter = new FileWriter(path + "\\missing_information.bib");
        bibtexFormatter.format(database, bibWriter);
        System.out.println("F3 DONE");
    }

    public void changeAdditionalInformation() throws IOException {

        org.jbibtex.BibTeXDatabase database = new org.jbibtex.BibTeXDatabase();
        Random rnd = new Random();
        Collection<BibTeXEntry> entriesAdditionalInformation = cloneEntryCollection();
        //decide what to change
        for (org.jbibtex.BibTeXEntry entry : entriesAdditionalInformation) {

            //org.jbibtex.Value author = entry.getField(BibTeXEntry.KEY_AUTHOR);
            org.jbibtex.Value title = entry.getField(BibTeXEntry.KEY_TITLE);
            org.jbibtex.Value year = entry.getField(BibTeXEntry.KEY_YEAR);
            org.jbibtex.Value journal = entry.getField(BibTeXEntry.KEY_JOURNAL);
            org.jbibtex.Value booktitle = entry.getField(BibTeXEntry.KEY_BOOKTITLE);
            org.jbibtex.Value pages = entry.getField(BibTeXEntry.KEY_PAGES);
            org.jbibtex.Value volume = entry.getField(BibTeXEntry.KEY_VOLUME);
            org.jbibtex.Value number = entry.getField(BibTeXEntry.KEY_NUMBER);
            //org.jbibtex.Value editor = entry.getField(BibTeXEntry.KEY_EDITOR);


            //int whatToDo = rnd.nextInt(3);

            boolean added = false;
            int decideValue = rnd.nextInt(6);
            int counter = 0;
            while (!added && counter < 6) {
                switch (decideValue) {
                    case 0:
                        if ((booktitle == null && journal != null)) {
                            org.jbibtex.StringValue newBooktitle = new StringValue("I am the additional booktitle, which should not be here", StringValue.Style.BRACED);
                            entry.addField(BibTeXEntry.KEY_BOOKTITLE, newBooktitle);
                            added = true;
                            break;
                        } else if (booktitle != null && journal == null) {
                            org.jbibtex.StringValue newJournal = new StringValue("I am the additional journal, which should not be here", StringValue.Style.BRACED);
                            entry.addField(BibTeXEntry.KEY_JOURNAL, newJournal);
                            added = true;
                            break;
                        }
                    case 1:
                        if (title == null) {
                            org.jbibtex.StringValue newTitle = new StringValue("I am the additional title, which should not be here", StringValue.Style.BRACED);
                            entry.addField(BibTeXEntry.KEY_TITLE, newTitle);
                            added = true;
                            break;
                        }
                    case 2:
                        if (year == null) {
                            org.jbibtex.StringValue newYear = new StringValue("2222", StringValue.Style.BRACED);
                            entry.addField(BibTeXEntry.KEY_YEAR, newYear);
                            added = true;
                            break;
                        }
                    case 3:
                        if (volume != null && number == null) {
                            org.jbibtex.StringValue newNumber = new StringValue("22", StringValue.Style.BRACED);
                            entry.addField(BibTeXEntry.KEY_NUMBER, newNumber);
                            added = true;
                            break;
                        }
                    case 4:
                        if (volume == null) {
                            org.jbibtex.StringValue newVolume = new StringValue("22", StringValue.Style.BRACED);
                            entry.addField(BibTeXEntry.KEY_VOLUME, newVolume);
                            added = true;
                            break;
                        }
                    case 5:
                        if (pages == null) {
                            org.jbibtex.StringValue newPages = new StringValue("22--222", StringValue.Style.BRACED);
                            entry.addField(BibTeXEntry.KEY_PAGES, newPages);
                            added = true;
                            break;
                        }
                }
                entry = addCategoryNote(entry, "F3");
                if (added == true) {
                    database.addObject(entry);
                } else {
                    decideValue = (decideValue + 1) % 6;
                }
                counter++;

            }
        }

        Writer writer = new FileWriter(path + "\\additional_information.txt");
        org.jbibtex.BibTeXFormatter bibtexFormatter = new org.jbibtex.BibTeXFormatter();
        bibtexFormatter.format(database, writer);
        Writer bibWriter = new FileWriter(path + "\\additional_information.bib");
        bibtexFormatter.format(database, bibWriter);
        System.out.println("F3 DONE");
    }


    private String wrongTitle(Value oldTitle) {
        Random rnd = new Random();
        List<String> words = new ArrayList<String>(Arrays.asList(oldTitle.toUserString().split(" ")));
        int chooseWord = rnd.nextInt(words.size());
        //todo add others than just deleting the word
        words.remove(chooseWord);
        String newTitle = words.get(0);
        for (int i = 1; i < words.size(); i++) {
            newTitle = newTitle + " " + words.get(i);
        }
        newTitle = getRidOffSpaces(newTitle);
        return newTitle;
    }

    private String deleteAuthor(Value oldAuthor) {
        Random rnd = new Random();
        List<String> authors = new ArrayList<String>(Arrays.asList(oldAuthor.toUserString().split(" and ")));
        int chooseAuthor = rnd.nextInt(authors.size());
        //todo add others than just deleting the word
        authors.remove(chooseAuthor);
        if (authors.size() > 0) {
            String newAuthors = authors.get(0);
            for (int i = 1; i < authors.size(); i++) {
                newAuthors = newAuthors + " and " + authors.get(i);
            }
            newAuthors = getRidOffSpaces(newAuthors);
            return newAuthors;
        } else {
            return "";
        }
    }

    private String addAuthor(Value oldAuthor) {
        Random rnd = new Random();
        List<String> authors = new ArrayList<String>(Arrays.asList(oldAuthor.toUserString().split(" and ")));
        int chooseAuthor = rnd.nextInt(authors.size());
        //todo add others than just deleting the word
        authors.add(chooseAuthor, "Additional, A.");
        String newAuthors = authors.get(0);
        for (int i = 1; i < authors.size(); i++) {
            newAuthors = newAuthors + " and " + authors.get(i);
        }
        newAuthors = getRidOffSpaces(newAuthors);
        return newAuthors;
    }

    private String wrongAuthor(Value oldAuthor) {
        Random rnd = new Random();
        List<String> authors = new ArrayList<String>(Arrays.asList(oldAuthor.toUserString().split(" and ")));
        int chooseAuthor = rnd.nextInt(authors.size());
        //todo add others than just deleting the word
        authors.set(chooseAuthor, "Wrong, A.");
        String newAuthors = authors.get(0);
        for (int i = 1; i < authors.size(); i++) {
            newAuthors = newAuthors + " and " + authors.get(i);
        }
        newAuthors = getRidOffSpaces(newAuthors);
        return newAuthors;
    }

//    Brooks, R., Levy, H., & Yoder, J. (1995). Using stochastic dominance to
//    evaluate the performance of portfolios with options. Financial Analysts
//    Journal, 43, 79–82.
//    F4: Abkürzungen fehlen/falsch
//    Brooks, R., Levy, H., & Yoder, J. (1987). Using stochastic dominance to
//    evaluate the performance of portfolios with options. Financial Analysts
//    Jr., 43, 79–82.

    public void changeShortcuts() throws IOException {
        //TODO nachträglicher fehler
    }


//    Syntaktische Fehler:
//    Lexikalische Fehler:
//    F5: Rechtschreibfehler Groß-/Kleinschreibung, Fehlende/falsche/vertauschte/zusätzliche Buchstaben
//    Brooks, R., Levy, H., & Yoder, J. (1987). Ussing stohcastic dominance to
//    evaluate the performance of portfolios with options. Financial analysts
//    Journal, 43, 79–82.

    //includes also some leerzeichen fehler!!

    public void changeSpelling() throws IOException {
//TODO evtl noch andere sachen hinzufügen
        org.jbibtex.BibTeXDatabase database = new org.jbibtex.BibTeXDatabase();
        Random rnd = new Random();
        Collection<BibTeXEntry> entriesChangeSpelling = cloneEntryCollection();
        for (org.jbibtex.BibTeXEntry entry : entriesChangeSpelling) {

            int choosePart = rnd.nextInt(4);
            StringBuilder builder = new StringBuilder();
            switch (choosePart) {
                case 0: // title
                    org.jbibtex.Value title = entry.getField(org.jbibtex.BibTeXEntry.KEY_TITLE);
                    if (title == null) {
                        System.out.println("Title empty!");
                        continue;
                    }
                    builder = new StringBuilder(title.toUserString());
                    org.jbibtex.StringValue titleNew = new org.jbibtex.StringValue(doMistake(builder), StringValue.Style.BRACED);
                    entry.addField(BibTeXEntry.KEY_TITLE, titleNew);
                    break;
                case 1: //author
                    org.jbibtex.Value author = entry.getField(BibTeXEntry.KEY_AUTHOR);
                    if (author == null) {
                        System.out.println("Author empty!");
                        continue;
                    }

                    String[] authors = author.toUserString().split(" and ");
                    int chooseAuthor = rnd.nextInt(authors.length);
                    String chosenAuthor = authors[chooseAuthor];

                    String[] names = chosenAuthor.split(", ");
                    String newAuthorsValue = "";
                    if (names.length >= 2) {
                        String firstname = names[1];
                        String lastname = names[0];

                        int chooseWhichName = rnd.nextInt(2);
                        switch (chooseWhichName) {
                            case 0: //firstname
                                builder = new StringBuilder(firstname);
                                firstname = doMistake(builder);
                                break;
                            case 1: //lastname
                                builder = new StringBuilder(lastname);
                                lastname = doMistake(builder);
                                break;
                        }
                        String mistakeAuthor = lastname + ", " + firstname;
                        //System.out.println(mistakeAuthor);

                        //putting together the list of authors
                        for (int i = 0; i < authors.length; i++) {
                            //if it is the first author of the list
                            if (i == 0) {
                                if (i == chooseAuthor) {
                                    newAuthorsValue = mistakeAuthor;
                                } else {
                                    newAuthorsValue = authors[i];
                                }
                                //all other authors following
                            } else {
                                if (i == chooseAuthor) {
                                    newAuthorsValue = newAuthorsValue + " and " + mistakeAuthor;
                                } else {
                                    newAuthorsValue = newAuthorsValue + " and " + authors[i];
                                    newAuthorsValue = getRidOffSpaces(newAuthorsValue);
                                }
                            }
                        }
                        //for the case the author has no firstname
                    } else {
                        String lastname = names[0];
                        String mistakeAuthor = lastname;

                        //putting together the list of authors
                        for (int i = 0; i < authors.length; i++) {
                            //if it is the first author of the list
                            if (i == 0) {
                                if (i == chooseAuthor) {
                                    newAuthorsValue = mistakeAuthor;
                                } else {
                                    newAuthorsValue = authors[i];
                                }
                                //all other authors following
                            } else {
                                if (i == chooseAuthor) {
                                    newAuthorsValue = newAuthorsValue + " and " + mistakeAuthor;
                                } else {
                                    newAuthorsValue = newAuthorsValue + " and " + authors[i];
                                    newAuthorsValue = getRidOffSpaces(newAuthorsValue);
                                }
                            }
                        }
                    }

                    //System.out.println(newAuthorsValue);
                    org.jbibtex.StringValue authorNew = new org.jbibtex.StringValue(newAuthorsValue, StringValue.Style.BRACED);

                    entry.addField(BibTeXEntry.KEY_AUTHOR, authorNew);
                    break;
                case 2: //journal
                    org.jbibtex.Value journal = entry.getField(BibTeXEntry.KEY_JOURNAL);
                    if (journal == null) {
                        System.out.println("Journal empty!");
                        continue;
                    }
                    builder = new StringBuilder(journal.toUserString());
                    org.jbibtex.StringValue journalNew = new org.jbibtex.StringValue(doMistake(builder), StringValue.Style.BRACED);
                    entry.addField(BibTeXEntry.KEY_JOURNAL, journalNew);
                    break;
                case 3: //booktitle
                    org.jbibtex.Value booktitle = entry.getField(BibTeXEntry.KEY_BOOKTITLE);
                    if (booktitle == null) {
                        System.out.println("Title empty!");
                        continue;
                    }
                    org.jbibtex.StringValue booktitleNew = new org.jbibtex.StringValue(doMistake(builder), StringValue.Style.BRACED);
                    entry.addField(BibTeXEntry.KEY_BOOKTITLE, booktitleNew);
                    break;
                //case 4:
                //    break;

            }

            entry = addCategoryNote(entry, "F5");
            database.addObject(entry);
        }

        Writer writer = new FileWriter(path + "\\spelling.txt");

        org.jbibtex.BibTeXFormatter bibtexFormatter = new org.jbibtex.BibTeXFormatter();

        bibtexFormatter.format(database, writer);
        Writer bibWriter = new FileWriter(path + "\\spelling.bib");
        bibtexFormatter.format(database, bibWriter);
        System.out.println("F5 DONE");
    }

    private BibTeXEntry addCategoryNote(BibTeXEntry entry, String category) {
        org.jbibtex.Value note = new org.jbibtex.StringValue(category, StringValue.Style.BRACED);
        entry.addField(BibTeXEntry.KEY_NOTE, note);
        return entry;
    }

    /*private BibTeXEntry removeAbstracts(BibTeXEntry entry) {
        entry.removeField(BibTeXEntry.Ke);
        return entry;
    }*/

    private String getRidOffSpaces(String string) {
        String newString = string.trim().replaceAll(" +", " ");
        return newString;
    }


    private String doMistake(StringBuilder builder) {
        Random rnd = new Random();
        int chooseMistake = rnd.nextInt(3);
        String newString = "";

        if (builder.length() != 0) {
            switch (chooseMistake) {
                case 0: //fehlende buchstaben
                    builder.deleteCharAt(rnd.nextInt(builder.length()));
                    newString = builder.toString();
                    break;
                case 1: //vertauschte buchstaben
                    int maxPosition = builder.length();
                    if (maxPosition < 2) {
                        return "";
                    }
                    int swapinPosition = rnd.nextInt(builder.length());
                    if (swapinPosition == maxPosition -1 && maxPosition > 1 ) {//checking if the random number generated is less then the max chars to reduce it by 1
                        swapinPosition--;
                    }
                    char first = builder.charAt(swapinPosition);
                    char second = builder.charAt(swapinPosition + 1);

                    builder.setCharAt(swapinPosition, second);
                    builder.setCharAt(swapinPosition + 1, first);
                    newString = builder.toString();
                    break;
                case 2: //zusätzliche buchstaben
                    builder.insert(rnd.nextInt(builder.length()), getRandomChar());
                    newString = builder.toString();
                    break;
            }
        }
        return newString;
    }

    //    F6: Satzzeichenfehler fehlen/zusätzlich/falsch: ,;.:“- () etc. (punctuation)
//    Brooks, R., Levy, H., & Yoder, J. (1987).. Using stochastic dominance to
//    evaluate the performance of portfolios with options. Financial Analysts
//    Journal; 43, 79–82. → eher Syntakfehler????
    public void changePunctuation() throws IOException {
        //nachträglich, bzw kann auch im text geändert werden, ist dann aber ein rechtschreibfehler
    }

//    F7: Reihenfolge-Fehler der Informationen (Syntaxfehler)
//    bsp: Namen vertauscht
//    Levy, H., Brooks, R., & Yoder, J. (1987). Using stochastic dominance to
//    evaluate the performance of portfolios with options. Financial Analysts
//    Journal, 43, 79–82.

    public void changePersonOrder() throws IOException {
        Random rnd = new Random();
        org.jbibtex.BibTeXDatabase database = new org.jbibtex.BibTeXDatabase();
        Collection<BibTeXEntry> entriesChangeInformationOrder = cloneEntryCollection();
        loop:
        for (org.jbibtex.BibTeXEntry entry : entriesChangeInformationOrder) {

            org.jbibtex.Value author = entry.getField(BibTeXEntry.KEY_AUTHOR);
            if (author == null) {
                continue loop;
            }
            List<String> authors = new ArrayList<String>(Arrays.asList(author.toUserString().split(" and ")));

            //macht keinen sinn, wenn es nur einen autor gibt
            if (authors.size() > 1) {
                int chooseAuthor = rnd.nextInt(authors.size());
                String author1 = authors.get(chooseAuthor);
                authors.remove(author1);
                //choose new position
                int newPosition = chooseAuthor + 1;
                if (newPosition >= authors.size() + 1) {
                    newPosition = chooseAuthor - 1;
                }
                authors.add(newPosition, author1);
                String newAuthors = authors.get(0);
                for (int i = 1; i < authors.size(); i++) {
                    newAuthors = newAuthors + " and " + authors.get(i);
                }
                newAuthors = getRidOffSpaces(newAuthors);

                org.jbibtex.StringValue authorNew = new org.jbibtex.StringValue(newAuthors, StringValue.Style.BRACED);
                entry.addField(BibTeXEntry.KEY_AUTHOR, authorNew);

                entry = addCategoryNote(entry, "F7");
                database.addObject(entry);
            }

        }

        Writer writer = new FileWriter(path + "\\order_person.txt");

        org.jbibtex.BibTeXFormatter bibtexFormatter = new org.jbibtex.BibTeXFormatter();

        bibtexFormatter.format(database, writer);
        Writer bibWriter = new FileWriter(path + "\\order_person.bib");
        bibtexFormatter.format(database, bibWriter);
        System.out.println("F7 DONE");

    }


//    F8: Vor- und Nachname vertauscht - Syntaxfehler
//    R., Brooks,  Levy, H., & Yoder, J. (1987). Using stochastic dominance to
//    evaluate the performance of portfolios with options. Financial Analysts
//    Journal, 43, 79–82.

    //this is the same as F1!!!!!!!!!!!

    /*
    public void changeNamesOrderSyntax() throws IOException {
    }
    */


//    F9: Nicht im Stil des Zitierstils (& - and; Abkürzung oder keine, etc.):
//    Brooks, R., Levy, H., and Yoder, J. (1987). Using stochastic dominance to
//    evaluate the performance of portfolios with options. Financial Analysts
//    Journal, 43, 79–82.
//

    public void changeCitationStyle() throws IOException {
        //TODO nchaträglich
    }

    public char getRandomChar() {
        Random r = new Random();
        char randomChar = (char) (r.nextInt(26) + 'a');

        return randomChar;
    }

    public void changeInformationOrder() throws IOException {
        org.jbibtex.BibTeXDatabase database = new org.jbibtex.BibTeXDatabase();
        Random rnd = new Random();
        Collection<BibTeXEntry> entriesOrderInformation = cloneEntryCollection();
        //decide what to change

        for (org.jbibtex.BibTeXEntry entry : entriesOrderInformation) {

            org.jbibtex.Value author = entry.getField(BibTeXEntry.KEY_AUTHOR);
            org.jbibtex.Value title = entry.getField(BibTeXEntry.KEY_TITLE);
            org.jbibtex.Value year = entry.getField(BibTeXEntry.KEY_YEAR);
            org.jbibtex.Value journal = entry.getField(BibTeXEntry.KEY_JOURNAL);
            org.jbibtex.Value booktitle = entry.getField(BibTeXEntry.KEY_BOOKTITLE);
            org.jbibtex.Value volume = entry.getField(BibTeXEntry.KEY_VOLUME);
            org.jbibtex.Value number = entry.getField(BibTeXEntry.KEY_NUMBER);
            org.jbibtex.Value editor = entry.getField(BibTeXEntry.KEY_EDITOR);

            boolean swaped = false;
            int decideValue = rnd.nextInt(5);
            int counter = 0;
            while (swaped == false && counter < 5 ) {
                switch (decideValue) {
                    case 0:
                        //swap title and booktitle
                        if ((booktitle != null && title != null)) {
                            entry.addField(BibTeXEntry.KEY_BOOKTITLE, title);
                            entry.addField(BibTeXEntry.KEY_TITLE, booktitle);
                            swaped = true;
                            break;
                        } else if (journal != null && title != null) {
                            entry.addField(BibTeXEntry.KEY_JOURNAL, title);
                            entry.addField(BibTeXEntry.KEY_TITLE, journal);
                            swaped = true;
                            break;
                        }
                    case 1:
                        //swap authors and editors
                        if (author != null && editor != null) {
                            entry.addField(BibTeXEntry.KEY_AUTHOR, editor);
                            entry.addField(BibTeXEntry.KEY_EDITOR, author);
                            swaped = true;
                            break;
                        }
                    case 2:
                        //swap volume and number
                        if (year != null && volume != null) {
                            entry.addField(BibTeXEntry.KEY_VOLUME, number);
                            entry.addField(BibTeXEntry.KEY_NUMBER, volume);
                            swaped = true;
                            break;
                        }
                    case 3:
                        //swap number and year
                        if (number != null && year != null) {
                            entry.addField(BibTeXEntry.KEY_NUMBER, year);
                            entry.addField(BibTeXEntry.KEY_YEAR, number);
                            swaped = true;
                            break;
                        }
                    case 4:
                        //swap volume and year
                        if (volume != null && year != null) {
                            entry.addField(BibTeXEntry.KEY_VOLUME, year);
                            entry.addField(BibTeXEntry.KEY_YEAR, volume);
                            swaped = true;
                            break;
                        }
                }
                entry = addCategoryNote(entry, "F3");
                if (swaped == true) {
                    database.addObject(entry);
                } else {
                    decideValue = (decideValue + 1) % 5;
                }
                counter++;
            }
        }

        Writer writer = new FileWriter(path + "\\order_information.txt");
        org.jbibtex.BibTeXFormatter bibtexFormatter = new org.jbibtex.BibTeXFormatter();
        bibtexFormatter.format(database, writer);
        Writer bibWriter = new FileWriter(path + "\\order_information.bib");
        bibtexFormatter.format(database, bibWriter);
        System.out.println("F3 DONE");
    }

//    Layout-Fehler:
//    F10: fett/kursiv/unterstrichen
//    Brooks, R., Levy, H., & Yoder, J. (1987). Using stochastic dominance to
//    evaluate the performance of portfolios with options. Financial Analysts
//    Journal, 43, 79–82.
//    F11: Leerzeichen-/Einrückungs-/Enterfehler
//    Brooks, R., Levy, H., & Yoder, J. (1987). Using stochastic dominance to
//    evaluate the performance of portfolios with options.
//    Financial Analysts Journal, 43, 79–  82.
}
