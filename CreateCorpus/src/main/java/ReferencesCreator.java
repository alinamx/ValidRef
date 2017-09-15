import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.undercouch.citeproc.bibtex.BibTeXConverter;
import de.undercouch.citeproc.output.Bibliography;
import org.jbibtex.BibTeXDatabase;
import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.bibtex.BibTeXItemDataProvider;
import org.jbibtex.ParseException;


public class ReferencesCreator {

    private String path;

    public void createReferences(String path, String file, String pathNew, String newFileName) {
        this.path = path;
        BibTeXDatabase db = null;
        try {
            db = new BibTeXConverter().loadDatabase(new FileInputStream(path + file));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        BibTeXItemDataProvider provider = new BibTeXItemDataProvider();
        provider.addDatabase(db);

        try {
            CSL citeproc = new CSL(provider, "apa");
            citeproc.setOutputFormat("text");
            provider.registerCitationItems(citeproc);
            Bibliography bibl = citeproc.makeBibliography();
            for (String entry : bibl.getEntries()) {
                //There is an Error with the editors, if its just one, the name will be written twice
                //Therefore we remove it our own
                //entry = removeWrongEditors(entry);
                entry = entry.replace("’", "'");
                //entry = entry.replace(",", ",");
                entry = checkOfDoubleEditors(entry);
                System.out.println(entry);
                Files.write(Paths.get(pathNew + newFileName), entry.getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String checkOfDoubleEditors(String entry) {
        String newEntry = "";
        String toReset = "";
        String newEditors = "";
        Pattern pattern = Pattern.compile("\\(((?!\\)).)*Ed.\\)");
        Matcher matcher = pattern.matcher(entry);
        if (matcher.find()) {
            toReset = matcher.group(0);
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
                        } else if (i == stop -1) {
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
}