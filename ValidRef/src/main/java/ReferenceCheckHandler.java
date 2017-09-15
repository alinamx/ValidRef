import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.bibtex.BibTeXConverter;
import de.undercouch.citeproc.bibtex.BibTeXItemDataProvider;
import de.undercouch.citeproc.csl.CSLType;
import de.undercouch.citeproc.output.Bibliography;
import org.jbibtex.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Uni on 18.07.2017.
 */
public class ReferenceCheckHandler {

    protected JSONArray checkAll(String style, String input) {
        //this is where all references are saved
        JSONArray baseJson = new JSONArray();
        CitationStyle citationStyle = chooseCitationStyleFromString(style);

        Scanner scan = new Scanner(input);

        StringBuilder sb = new StringBuilder();

        ContentChecker contentChecker = new ContentChecker();
        FreeCiteHandler freeCiteHandler = new FreeCiteHandler();
        StyleChecker styleChecker = new StyleChecker(citationStyle);

        //read one reference after another
        while (scan.hasNextLine()) {
            String reference = scan.nextLine();
            sb.append(reference);
            reference = purgeInputString(reference);

            List<InputReference> resultFreeCite = freeCiteHandler.loadFreeCite(reference);
            if (resultFreeCite != null && !resultFreeCite.isEmpty()) {
                EconbizHandler econbizHandler = new EconbizHandler();
                //for every reference search now in econBiz for results
                for (InputReference ref : resultFreeCite) {
                    JSONObject json = new JSONObject();
                    EconBizReference econRef = econbizHandler.searchEconbiz(ref);

                    json.put("raw_input", ref.getOriginalString());
                    if (econRef != null) {
                        String econRefString = getExpectedStringOfEconBizBibTeX(econRef, citationStyle);

                        json.put("correct_reference", econRefString.replaceAll("<((?!>).)*>", "").trim());
                        //this will change the inputs to a special style based on the citation style, that they can be compared
                        //this way any new citation style can be added by adding its own ruleset for it
                        formatReferences(citationStyle, ref, econRef);

                        //contentcheck
                        JSONObject refcheckJson = contentChecker.checkReference(ref, econRef);
                        json.put("content_check", refcheckJson);

                        //style check - this check is not related to the results of econbiz and can be performed even if there was nothing found
                        if (styleChecker != null) {
                            JSONObject styleCheck = styleChecker.checkStyleNew(ref, econRef.getType());

                            json.put("style_check", styleCheck);
                        }
                    } else {
                        System.out.println("Error! Couldnt find a result in econbiz!");
                        json.put("content_check", new JSONObject());
                        json.put("style_check", new JSONObject());
                        json.put("correct_reference", new JSONObject());
                    }
                    baseJson.add(json);
                }
            }
        }
        System.out.println("CORRECT REFERENCES:");
        return baseJson;

    }

    private String getExpectedStringOfEconBizBibTeX(EconBizReference econRef, CitationStyle citationStyle) {
        try {
            InputStream currentValue = new ByteArrayInputStream(econRef.getBibtex().replace("misc", chooseType(econRef.getType())).getBytes(StandardCharsets.UTF_8.name()));
            BibTeXDatabase db = new BibTeXConverter().loadDatabase(currentValue);
            BibTeXItemDataProvider provider = new BibTeXItemDataProvider();
            provider.addDatabase(db);
            CSL citeproc = new CSL(provider, citationStyle.getValue());
            provider.registerCitationItems(citeproc);
            Bibliography bibl = citeproc.makeBibliography();
            for (String entry : bibl.getEntries()) {

                entry = entry.replace("&#38;", "&");
                entry = checkOfDoubleEditors(entry);
                System.out.println(" ENTRY: " + entry);
                //entry = entry.replace("\u202f", " ");
                return entry;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TokenMgrException | ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    private String checkOfDoubleEditors(String entry) {
        String newEntry = "";
        String toReset = "";
        String newEditors = "";
        Pattern pattern = Pattern.compile("\\(((?!\\)).)*Ed.\\)");
        Matcher matcher = pattern.matcher(entry);
        if (matcher.find()) {
            toReset = matcher.group();
            String[] editors = toReset
                    .replace(", Ed.", "")
                    .replace("(", "")
                    .replace(")", "")
                    .split(", ");
            if (editors.length > 1) {
                if (editors[0].equals(editors[1])) {
                    newEditors = newEditors + editors[0] + ", Ed.";
                }
            }
            return entry.replace(toReset, "(" + newEditors + ")");
        }
        Pattern pattern2 = Pattern.compile("\\(((?!\\)).)*Eds.\\)");
        Matcher matcher2 = pattern2.matcher(entry);
        if (matcher2.find()) {
            toReset = matcher2.group(0);
            String[] editors = toReset
                    .replace(", Eds.", "")
                    .replace("(", "")
                    .replace(")", "")
                    .replace(" & ", ", ")
                    .split(", ");
            if (editors.length > 2) {
                int stop = editors.length / 2;
                for (int i = 0; i < stop; i++) {
                    if (editors[i].equals(editors[stop + i])) {
                        if (i == 0) {
                            newEditors = editors[i];
                        } else if (i == stop - 1) {
                            newEditors = newEditors + " & " + editors[i];
                        } else {
                            newEditors = newEditors + ", " + editors[i];
                        }
                    }
                }
                newEditors += ", Eds.";
            }
            return entry.replace(toReset, "(" + newEditors + ")");
        }
        return entry;
    }

    private String chooseType(CSLType type) {
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


    private BibTeXEntry getFittingEntry(String title) {
        FileReader fr = null;
        try {
            fr = new FileReader("C:\\Users\\Uni\\Documents\\Masterarbeit\\CorpusFinal\\bibliography.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(fr);

        BibTeXDatabase database = new BibTeXDatabase();
        try {
            BibTeXParser bibtexParser = new BibTeXParser();
            database = bibtexParser.parse(br);
        } catch (org.jbibtex.ParseException e) {
            e.printStackTrace();
        }

        Map<Key, BibTeXEntry> entryMap = database.getEntries();

        Collection<BibTeXEntry> entries = entryMap.values();

        BibTeXEntry entryReturn = null;
        //code for checking the values
        for (org.jbibtex.BibTeXEntry entry : entries) {
            String titleEntry = entry.getField(BibTeXEntry.KEY_TITLE).toUserString()
                    .replace("{", "")
                    .replace("}", "")
                    .replace("\u202f", " ");
            title = title.replace("\u202f", " ");
            if ((titleEntry.equals(title))) {
                entryReturn = entry;
            }
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entryReturn;

    }

    private void formatReferences(CitationStyle citationStyle, InputReference ref, EconBizReference econRef) {
        StyleFormatter formatter = new StyleFormatter(citationStyle);
        formatter.changeEconBizResultsToFormat(econRef);
        formatter.changeFreeCiteResultsToFormat(ref);
    }

    private String purgeInputString(String reference) {
        reference = reference.replaceAll("\\-\\n", ""); //eliminieren von wort trennenden bindestrichen
        reference = reference.replaceAll("\\n", " ");
        //reference = reference.replaceAll("\\[(.*?)\\]", " ");
        //reference = reference.replace("\u202f", " ");
        return reference;
    }

    private CitationStyle chooseCitationStyleFromString(String style) {
        CitationStyle cStyle;
        switch (style) {
            case "apa":
                cStyle = CitationStyle.APA;
                break;
            case "vancouver":
                cStyle = CitationStyle.VANCOUVER;
                break;
            case "harvard":
                cStyle = CitationStyle.HARVARD;
                break;
            case "chicago":
                cStyle = CitationStyle.CHICAGO;
                break;
            case "mla":
                cStyle = CitationStyle.MLA;
                break;
            default:
                cStyle = CitationStyle.APA;
        }
        return cStyle;
    }
}
