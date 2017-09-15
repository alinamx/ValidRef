import java.io.File;
import java.io.IOException;

/**
 * Created by Uni on 07.01.2017.
 */
public class Main {

    static String path = "C:\\Users\\Uni\\Documents\\Masterarbeit\\CorpusFinal";

    public static void main(String[] args) throws IOException {

        downloadFiles();
        createFileNewIfNotExistsOrOverwrite(path + "\\finalTxtFiles", "\\bibliographyRef.txt");
        createReferences("\\bibliography.bib", path + "\\finalTxtFiles", "\\bibliographyRef.txt");

        //addMistakes();
    }

    private static void addMistakes() {
        MistakeAdder mistakeAdder = null;
        try {
            mistakeAdder = new MistakeAdder(path);

            mistakeAdder.changeInformationOrder(); //done
            mistakeAdder.changeMissingInformation();//done
            mistakeAdder.changeAdditionalInformation();
            mistakeAdder.changeWrongInformation();
            mistakeAdder.changePunctuation(); //later
            mistakeAdder.changeSpelling(); //done

            mistakeAdder.changeWrongPerson(); //done
            mistakeAdder.changeAdditionalPerson();
            mistakeAdder.changeMissingPerson();
            mistakeAdder.changeNamesOrder(); // done, changes last and firstnames
            mistakeAdder.changePersonOrder(); // changes the order of the persons
            mistakeAdder.changeCitationStyle(); //later

            createFileNewIfNotExistsOrOverwrite(path + "\\finalTxtFiles", "\\order_information.txt");
            createReferences("\\order_information.bib", path + "\\finalTxtFiles", "\\order_information.txt");
            createFileNewIfNotExistsOrOverwrite(path + "\\finalTxtFiles", "\\missing_information.txt");
            createReferences("\\missing_information.bib", path + "\\finalTxtFiles", "\\missing_information.txt");
            createFileNewIfNotExistsOrOverwrite(path + "\\finalTxtFiles", "\\additional_information.txt");
            createReferences("\\additional_information.bib", path + "\\finalTxtFiles", "\\additional_information.txt");
            createFileNewIfNotExistsOrOverwrite(path + "\\finalTxtFiles", "\\wrong_information.txt");
            createReferences("\\wrong_information.bib", path + "\\finalTxtFiles", "\\wrong_information.txt");
            createFileNewIfNotExistsOrOverwrite(path + "\\finalTxtFiles", "\\spelling.txt");
            createReferences("\\spelling.bib", path + "\\finalTxtFiles", "\\spelling.txt");

            createFileNewIfNotExistsOrOverwrite(path + "\\finalTxtFiles", "\\wrong_person.txt");
            createReferences("\\wrong_person.bib", path + "\\finalTxtFiles", "\\wrong_person.txt");

            createFileNewIfNotExistsOrOverwrite(path + "\\finalTxtFiles", "\\missing_person.txt");
            createReferences("\\missing_person.bib", path + "\\finalTxtFiles", "\\missing_person.txt");
            createFileNewIfNotExistsOrOverwrite(path + "\\finalTxtFiles", "\\additional_person.txt");
            createReferences("\\additional_person.bib", path + "\\finalTxtFiles", "\\additional_person.txt");
            createFileNewIfNotExistsOrOverwrite(path + "\\finalTxtFiles", "\\order_names.txt");
            createReferences("\\order_names.bib", path + "\\finalTxtFiles", "\\order_names.txt");
            createFileNewIfNotExistsOrOverwrite(path + "\\finalTxtFiles", "\\order_person.txt");
            createReferences("\\order_person.bib", path + "\\finalTxtFiles", "\\order_person.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createReferences(String fileName, String newPath, String newFileName) {
        ReferencesCreator creator = new ReferencesCreator();
        creator.createReferences(path, fileName, newPath, newFileName);
    }

    private static void downloadFiles() {
        //v1
        long number = 10002240342L;
        //v2
        //long number = 10010692247L;
        int i = 0;

        createFileNewIfNotExistsOrOverwrite(path, "\\bibliography.txt");
        createFileNewIfNotExistsOrOverwrite(path, "\\bibliography.bib");

        while (i <= 20) {
            try {
                i++;
                number = number + i;
                String url = "https://www.econbiz.de/Record/" + number + "/Export?style=BibTeX";
                //just add if new files should be downloaded
                JGet loader = new JGet();
                loader.downloadBibTex(url, number, path);
            } catch (Exception e) {
                i--;
            }

        }
    }

    private static void createFileNewIfNotExistsOrOverwrite(String path, String filename) {
        File fileOld = new File(path + filename);
        fileOld.delete();
        File fileTXT = new File(path + filename);
        try {
            fileTXT.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * Be aware of the following:
 * delete URL: optional
 * replace characters: \ -> \\, % -> \%, $ -> \$, & -> \&
 * delete crossrefs
 * type bestimmen und einsetzen zum compilieren
 */