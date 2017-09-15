import de.undercouch.citeproc.csl.CSLType;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;


/**
 * Created by Uni on 13.12.2016.
 * MAIN: APA, Harvard, Chicago
 */
public class ContentChecker {

    /*private boolean withoutContentError = true;

    public boolean isWithoutContentError() {
        return withoutContentError;
    }

    public void setWithoutContentError(boolean withoutContentError) {
        this.withoutContentError = withoutContentError;
    }

    int notFoundCounter = 0;
    int falseCounter = 0;
    int correctCounter = 0;

    int counterTitleRight = 0;
    int counterTitleFalse = 0;

    int counterAuthorsRight = 0;
    int counterAuthorsFalse = 0;

    int counterYearsRight = 0;
    int counterYearsFalse = 0;

    int counterPagesRight = 0;
    int counterPagesFalse = 0;

    int counterEditorsRight = 0;
    int counterEditorsFalse = 0;

    int counterVolumeRight = 0;
    int counterVolumeFalse = 0;

    int counterPublisherRight = 0;
    int counterPublisherFalse = 0;

    int counterJournalRight = 0;
    int counterJournalFalse = 0;

    int counterDOIRight = 0;
    int counterDOIFalse = 0;

    int counterInstitutionRight = 0;
    int counterInstitutionFalse = 0;

    int counterLocationRight = 0;
    int counterLocationFalse = 0;

    int counterNumberRight = 0;
    int counterNumberFalse = 0;

    int counterErrorUNKNOWN = 0;
    int counterErrorSPELLING = 0;
    int counterErrorWRONG_INFORMATION = 0;
    int counterErrorMISSING_INFORMATION = 0;
    int counterErrorADDITIONAL_INFORMATION = 0;
    int counterErrorNONE = 0;

    int counterErrorListORDER = 0;
    int counterErrorListMISSING_ELEMENT = 0;
    int counterErrorListTO_MANY_ELEMENTS = 0;
    int counterErrorListUNKNOWN = 0;
    int counterErrorListSPELLING = 0;
    int counterErrorListWRONG_ELEMENT = 0;

    public int getNotFoundCounter() {
        return notFoundCounter;
    }

    public void setNotFoundCounter(int notFoundCounter) {
        this.notFoundCounter = notFoundCounter;
    }

    public int getFalseCounter() {
        return falseCounter;
    }

    public int getCorrectCounter() {
        return correctCounter;
    }

    public int getCounterTitleRight() {
        return counterTitleRight;
    }

    public int getCounterTitleFalse() {
        return counterTitleFalse;
    }

    public int getCounterAuthorsRight() {
        return counterAuthorsRight;
    }

    public int getCounterAuthorsFalse() {
        return counterAuthorsFalse;
    }

    public int getCounterYearsRight() {
        return counterYearsRight;
    }

    public int getCounterYearsFalse() {
        return counterYearsFalse;
    }

    public int getCounterPagesRight() {
        return counterPagesRight;
    }

    public int getCounterPagesFalse() {
        return counterPagesFalse;
    }

    public int getCounterJournalRight() {
        return counterJournalRight;
    }

    public int getCounterJournalFalse() {
        return counterJournalFalse;
    }

    public int getCounterEditorsRight() {
        return counterEditorsRight;
    }

    public int getCounterEditorsFalse() {
        return counterEditorsFalse;
    }

    public int getCounterVolumeRight() {
        return counterVolumeRight;
    }

    public int getCounterVolumeFalse() {
        return counterVolumeFalse;
    }

    public int getCounterPublisherRight() {
        return counterPublisherRight;
    }

    public int getCounterPublisherFalse() {
        return counterPublisherFalse;
    }

    public int getCounterDOIRight() {
        return counterDOIRight;
    }

    public int getCounterDOIFalse() {
        return counterDOIFalse;
    }

    public int getCounterInstitutionRight() {
        return counterInstitutionRight;
    }

    public int getCounterInstitutionFalse() {
        return counterInstitutionFalse;
    }

    public int getCounterLocationRight() {
        return counterLocationRight;
    }

    public int getCounterLocationFalse() {
        return counterLocationFalse;
    }

    public int getCounterNumberRight() {
        return counterNumberRight;
    }

    public int getCounterNumberFalse() {
        return counterNumberFalse;
    }

    public int getCounterErrorUNKNOWN() {
        return counterErrorUNKNOWN;
    }

    public int getCounterErrorSPELLING() {
        return counterErrorSPELLING;
    }

    public int getCounterErrorWRONG_INFORMATION() {
        return counterErrorWRONG_INFORMATION;
    }

    public int getCounterErrorMISSING_INFORMATION() {
        return counterErrorMISSING_INFORMATION;
    }

    public int getCounterErrorNONE() {
        return counterErrorNONE;
    }

    public int getCounterErrorListORDER() {
        return counterErrorListORDER;
    }

    public int getCounterErrorListMISSING_ELEMENT() {
        return counterErrorListMISSING_ELEMENT;
    }

    public int getCounterErrorListTO_MANY_ELEMENTS() {
        return counterErrorListTO_MANY_ELEMENTS;
    }

    public int getCounterErrorListUNKNOWN() {
        return counterErrorListUNKNOWN;
    }

    public int getCounterErrorListSPELLING() {
        return counterErrorListSPELLING;
    }

    public int getCounterErrorListWRONG_ELEMENT() {
        return counterErrorListWRONG_ELEMENT;
    }

    public int getCounterErrorADDITIONAL_INFORMATION() {
        return counterErrorADDITIONAL_INFORMATION;
    }
*/
    public ContentChecker() {
    }

    protected JSONObject checkReference(InputReference ref, EconBizReference econRef) {
        //withoutContentError = true;
        JSONObject innerJson = new JSONObject();

        CSLType type = econRef.getType();

        if (type == CSLType.ARTICLE_JOURNAL) {
            innerJson = checkItems(ref, econRef, true, true, true, true, true, false, true, true, false, false, false, true);
        } else if (type == CSLType.CHAPTER) {
            innerJson = checkItems(ref, econRef, true, true, true, true, true, true, true, true, true, false, true, true);
        } else if (type == CSLType.BOOK) {
            innerJson = checkItems(ref, econRef, true, true, true, false, false, true, true, true, true, false, true, true);
        } else { // if type is CSLType.ARTICLE
            innerJson = checkItems(ref, econRef, true, true, true, true, true, true, true, true, true, true, true, true);
        }

        return innerJson;
    }

    private JSONObject checkItems(InputReference ref, EconBizReference econRef, boolean title, boolean author, boolean year,
                                  boolean journalBooktitle, boolean pages, boolean editors, boolean volume, boolean number,
                                  boolean publisher, boolean institution, boolean location, boolean DOI) {
        JSONObject innerJson = new JSONObject();
        if (title) {
            innerJson.put("title", checkTitle(ref, econRef));
        } else {
            JSONObject details = new JSONObject();
            if (ref.getTitle() != null && !ref.getTitle().equals("") && (econRef.getTitle() == null || econRef.getTitle().equals(""))) {
                setJSONDetails(details, ref.getTitle(), false, "", ErrorType.ADDITIONAL_INFORMATION, "");
            } else {
                setJSONDetails(details, "", true, "", ErrorType.NONE, "Not checked");
            }
            innerJson.put("title", details);
        }

        if (author) {
            innerJson.put("authors", checkAuthors(ref.getAuthors(), econRef.getAuthors()));
        } else {
            JSONObject details = new JSONObject();
            if (ref.getAuthors() != null && ref.getAuthors().size() > 0 && (econRef.getAuthors() == null || econRef.getAuthors().size() == 0)) {
                setJSONDetailsList(details, ref.getAuthors().toString(), false, "", ErrorType.ADDITIONAL_INFORMATION, "");
            } else {
                setJSONDetails(details, "", true, "", ErrorType.NONE, "Not checked");
            }
            innerJson.put("authors", details);        
        }

        if (year) {
            innerJson.put("year", checkYear(ref, econRef));
        } else {
            JSONObject details = new JSONObject();
            if (ref.getYear() != null && !ref.getYear().equals("") && (econRef.getYear() == null || econRef.getYear().equals(""))) {
                setJSONDetails(details, ref.getYear(), false, "", ErrorType.ADDITIONAL_INFORMATION, "");
            } else {
                setJSONDetails(details, "", true, "", ErrorType.NONE, "Not checked");
            }
            innerJson.put("year", details);
        }

        if (journalBooktitle) {
            innerJson.put("journalBooktitle", checkJournalOrBooktitle(ref, econRef));
        } else {
            JSONObject details = new JSONObject();
            if (ref.getJournalOrBooktitle() != null && !ref.getJournalOrBooktitle().equals("") && (econRef.getJournalOrBooktitle() == null || econRef.getJournalOrBooktitle().equals(""))) {
                setJSONDetails(details, ref.getJournalOrBooktitle(), false, "", ErrorType.ADDITIONAL_INFORMATION, "");
            } else {
                setJSONDetails(details, "", true, "", ErrorType.NONE, "Not checked");
            }
            innerJson.put("journalBooktitle", details);
        }

        if (pages) {
            innerJson.put("pages", checkPages(ref.getPages(), econRef.getPages()));
        } else {
            JSONObject details = new JSONObject();
            if (ref.getPages() != null && !ref.getPages().equals("") && (econRef.getPages() == null || econRef.getPages().equals(""))) {
                setJSONDetails(details, ref.getPages(), false, "", ErrorType.ADDITIONAL_INFORMATION, "");
            } else {
                setJSONDetails(details, "", true, "", ErrorType.NONE, "Not checked");
            }
            innerJson.put("pages", details);
        }

        if (editors) {
            innerJson.put("editors", checkEditors(ref.getEditors(), econRef.getEditors()));
        } else {
            JSONObject details = new JSONObject();
            if (ref.getEditors() != null && ref.getEditors().size() > 0 && (econRef.getEditors() == null || econRef.getEditors().size() == 0)) {
                setJSONDetailsList(details, ref.getEditors().toString(), false, "", ErrorType.ADDITIONAL_INFORMATION, "");
            } else {
                setJSONDetails(details, "", true, "", ErrorType.NONE, "Not checked");
            }
            innerJson.put("editors", details);
        }

        if (volume) {
            innerJson.put("volume", checkVolume(ref.getVolume(), econRef.getVolume()));
        } else {
            JSONObject details = new JSONObject();
            if (ref.getVolume() != null && !ref.getVolume().equals("") && (econRef.getVolume() == null || econRef.getVolume().equals(""))) {
                setJSONDetails(details, ref.getVolume(), false, "", ErrorType.ADDITIONAL_INFORMATION, "");
            } else {
                setJSONDetails(details, "", true, "", ErrorType.NONE, "Not checked");
            }
            innerJson.put("volume", details);
        }

        if (number) {
            innerJson.put("number", checkNumber(ref.getNumber(), econRef.getNumber()));
        } else {
            JSONObject details = new JSONObject();
            if (ref.getNumber() != null && !ref.getNumber().equals("") && (econRef.getNumber() == null || econRef.getNumber().equals(""))) {
                setJSONDetails(details, ref.getNumber(), false, "", ErrorType.ADDITIONAL_INFORMATION, "");
            } else {
                setJSONDetails(details, "", true, "", ErrorType.NONE, "Not checked");
            }
            innerJson.put("number", details);
        }

        if (publisher) {
            innerJson.put("publisher", checkPublisher(ref.getPublisher(), econRef.getPublisher()));
        } else {
            JSONObject details = new JSONObject();
            if (ref.getPublisher() != null && !ref.getPublisher().equals("") && (econRef.getPublisher() == null || econRef.getPublisher().equals(""))) {
                setJSONDetails(details, ref.getPublisher(), false, "", ErrorType.ADDITIONAL_INFORMATION, "");
            } else {
                setJSONDetails(details, "", true, "", ErrorType.NONE, "Not checked");
            }
            innerJson.put("publisher", details);
        }

        if (institution) {
            innerJson.put("institution", checkInstitution(ref.getInstitution(), econRef.getInstitution()));
        } else {
            JSONObject details = new JSONObject();
            if (ref.getInstitution() != null && !ref.getInstitution().equals("") && (econRef.getInstitution() == null || econRef.getInstitution().equals(""))) {
                setJSONDetails(details, ref.getInstitution(), false, "", ErrorType.ADDITIONAL_INFORMATION, "");
            } else {
                setJSONDetails(details, "", true, "", ErrorType.NONE, "Not checked");
            }
            innerJson.put("institution", details);
        }

        if (location) {
            innerJson.put("location", checkLocation(ref.getLocation(), econRef.getLocation()));
        } else {
            JSONObject details = new JSONObject();
            if (ref.getLocation() != null && !ref.getLocation().equals("") && (econRef.getLocation() == null || econRef.getLocation().equals(""))) {
                setJSONDetails(details, ref.getLocation(), false, "", ErrorType.ADDITIONAL_INFORMATION, "");
            } else {
                setJSONDetails(details, "", true, "", ErrorType.NONE, "Not checked");
            }
            innerJson.put("location", details);
        }

        if (DOI) {
            innerJson.put("DOI", checkDOI(ref.getDOI(), econRef.getDOI()));
        } else {
            JSONObject details = new JSONObject();
            if (ref.getDOI() != null && !ref.getDOI().equals("") && (econRef.getDOI() == null || econRef.getDOI().equals(""))) {
                setJSONDetails(details, ref.getDOI(), false, "", ErrorType.ADDITIONAL_INFORMATION, "");
            } else {
                setJSONDetails(details, "", true, "", ErrorType.NONE, "Not checked");
            }
            innerJson.put("DOI", details);
        }
        return innerJson;
    }

    private Object checkDOI(String doiInput, String doiEcon) {
        JSONObject doiJSON = new JSONObject();
        JSONObject doiJSONdetails = new JSONObject();
        if (!doiInput.equals(doiEcon)) {
            setJSONDetails(doiJSONdetails, doiInput, false, doiEcon, chooseErrorTypeNumbers(doiInput, doiEcon), "");
        } else {
            setJSONDetails(doiJSONdetails, doiInput, true, "", ErrorType.NONE, "");
        }
        doiJSON.put("doi", doiJSONdetails);
        return doiJSONdetails;
    }

    private JSONObject checkPublisher(String inputPublisher, String econPublisher) {
        JSONObject publisherJSON = new JSONObject();
        JSONObject publisherJSONdetails = new JSONObject();
        if (!inputPublisher.equals(econPublisher)) {
            setJSONDetails(publisherJSONdetails, inputPublisher, false, econPublisher, chooseErrorTypeStrings(inputPublisher, econPublisher), "");
        } else {
            setJSONDetails(publisherJSONdetails, inputPublisher, true, "", ErrorType.NONE, "");
        }
        publisherJSON.put("publisher", publisherJSONdetails);
        return publisherJSONdetails;
    }

    private JSONObject checkLocation(String inputLocation, String econLocation) {
        JSONObject locationJSON = new JSONObject();
        JSONObject locationJSONdetails = new JSONObject();
        if (!inputLocation.equals(econLocation)) {
            setJSONDetails(locationJSONdetails, inputLocation, false, econLocation, chooseErrorTypeStrings(inputLocation, econLocation), "");
        } else {
            setJSONDetails(locationJSONdetails, inputLocation, true, "", ErrorType.NONE, "");
        }
        locationJSON.put("location", locationJSONdetails);
        return locationJSONdetails;
    }

    private JSONObject checkInstitution(String inputInstitution, String econInstitution) {
        JSONObject institutionJSON = new JSONObject();
        JSONObject institutionJSONdetails = new JSONObject();
        if (!inputInstitution.equals(econInstitution)) {
            setJSONDetails(institutionJSONdetails, inputInstitution, false, econInstitution, chooseErrorTypeStrings(inputInstitution, econInstitution), "");
        } else {
            setJSONDetails(institutionJSONdetails, inputInstitution, true, "", ErrorType.NONE, "");
        }
        institutionJSON.put("institution", institutionJSONdetails);
        return institutionJSONdetails;
    }

    private JSONObject checkNumber(String inputNumber, String econNumber) {
        JSONObject numberJSON = new JSONObject();
        JSONObject numberJSONdetails = new JSONObject();
        if (!inputNumber.equals(econNumber)) {
            setJSONDetails(numberJSONdetails, inputNumber, false, econNumber, chooseErrorTypeNumbers(inputNumber, econNumber), "");
        } else {
            setJSONDetails(numberJSONdetails, inputNumber, true, "", ErrorType.NONE, "");
        }
        numberJSON.put("number", numberJSONdetails);
        return numberJSONdetails;
    }


    private JSONObject checkVolume(String inputVolume, String econVolume) {
        JSONObject volumeJSON = new JSONObject();
        JSONObject volumeJSONdetails = new JSONObject();
        if (!inputVolume.equals(econVolume)) {
            setJSONDetails(volumeJSONdetails, inputVolume, false, econVolume, chooseErrorTypeNumbers(inputVolume, econVolume), "");
        } else {
            setJSONDetails(volumeJSONdetails, inputVolume, true, "", ErrorType.NONE, "");
        }
        volumeJSON.put("volume", volumeJSONdetails);
        return volumeJSONdetails;
    }

    private JSONObject checkEditors(ArrayList<String> editors, ArrayList<String> editorsEcon) {
        JSONObject editorsJSON = new JSONObject();
        JSONObject editorsJSONdetails = new JSONObject();

        ArrayList editorsInputList = editors;
        if ((editors).equals(editorsEcon)) {
            System.out.println("TOP! EDITORS CORRECT!");
            setJSONDetails(editorsJSONdetails, editors.toString(), true, "", ErrorType.NONE, "");
        } else {
            System.out.println("WRONG! EDITORS INCORRECT!");
            setJSONDetailsList(editorsJSONdetails, editors.toString(), false, editorsEcon.toString(), chooseErrorTypeList(editors, editorsEcon), "");
        }
        editorsJSON.put("editors", editorsJSONdetails);
        return editorsJSONdetails;
    }

    private JSONObject checkJournalOrBooktitle(InputReference ref, EconBizReference econRef) {
        String journalInput = ref.getJournalOrBooktitle();
        String journalEconBiz = econRef.getJournalOrBooktitle();
        JSONObject journalJSON = new JSONObject();
        JSONObject journalJSONdetails = new JSONObject();
        if (journalInput != null && journalInput.equals(journalEconBiz)) {
            System.out.println("TOP! Journal CORRECT!");
            setJSONDetails(journalJSONdetails, journalInput, true, "", ErrorType.NONE, "");
        } else if (journalInput != null && journalInput.contains(".")) {
            String parts[] = journalInput.split("\\.");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals(journalEconBiz)) {
                    System.out.println("TOP! Journal CORRECT! BUT WAS SPLIT BEFORE");
                    setJSONDetails(journalJSONdetails, journalInput, true, "", ErrorType.NONE, "Journal correct after splitting it at '.'");
                }
            }
        } else if (journalInput != null && journalInput.contains("(") && !journalInput.contains(")")) {
            journalInput = journalInput + ")";
            if (journalInput.equals(journalEconBiz)) {
                System.out.println("TOP! Journal CORRECT! Added ) at the end;");
                ref.setJournalOrBooktitle(journalInput);
                setJSONDetails(journalJSONdetails, journalInput, true, "", ErrorType.NONE, "Journal correct after adding ')' at the end, got probably cut from freeCite.");
            }
        } else {
            System.out.println("WRONG! Journal INCORRECT!");
            ErrorType errorType = chooseErrorTypeStrings(journalInput, journalEconBiz);
            setJSONDetails(journalJSONdetails, journalInput, false, journalEconBiz, errorType, "");
        }
        journalJSON.put("journal/booktitle", journalJSONdetails);
        return journalJSONdetails;
    }

    private ErrorType chooseErrorTypeStrings(String inputOriginal, String econRef) {
        String partsInputOriginal[] = inputOriginal.split(" ");
        String partsEconBiz[] = econRef.split(" ");
        //check if information is missing completely
        if ((inputOriginal.equals("") || inputOriginal == null) && (!econRef.equals("") && econRef != null)) {
            return ErrorType.MISSING_INFORMATION;
        }

        if (!inputOriginal.equals("") && inputOriginal != null && (econRef.equals("") || econRef == null)) {
            return ErrorType.ADDITIONAL_INFORMATION;
        }

        int dist = StringUtils.getLevenshteinDistance(inputOriginal, econRef);

        if (dist <= 2) {
            return ErrorType.LEXICAL;
        }

        if (partsInputOriginal.length != partsEconBiz.length) {
            if (dist <= (inputOriginal.toCharArray().length / 3)) {
                return ErrorType.LEXICAL;
            } else {
                return ErrorType.WRONG_INFORMATION;
            }
        }
        return ErrorType.UNKNOWN;
    }

    private ErrorType chooseErrorTypeList(ArrayList inputList, ArrayList econBizList) {
        boolean match = false;
        if (inputList.size() < econBizList.size()) {
            return ErrorType.MISSING_INFORMATION;
        } else if (inputList.size() > econBizList.size()) {
            return ErrorType.ADDITIONAL_INFORMATION;
        } else {
            ArrayList inputCheckList = (ArrayList) inputList.clone();
            ArrayList econCheckList = (ArrayList) econBizList.clone();
            int positionNotTheSame = 0;
            for (int i = 0; i < inputList.size(); i++) {
                for (int j = 0; j < econBizList.size(); j++) {
                    match = false;
                    if (inputList.get(i).equals(econBizList.get(j))) {
                        match = true;
                        if (i < j || i > j) {
                            positionNotTheSame++;
                        } else {
                            inputCheckList.remove(i);
                            econCheckList.remove(j);
                        }
                    }
                }
                if (match && positionNotTheSame >= 2) {
                    return ErrorType.ORDER;
                }
                //reaching here means, that
                if (!match && inputList.size() == econBizList.size()) {
                    for (int j = 0; j < econCheckList.size(); j++) {
                        int dist = StringUtils.getLevenshteinDistance((inputList.get(i).toString()), econCheckList.get(j).toString());

                        if (dist <= 2) {
                            return ErrorType.LEXICAL;
                        } else {
                            return ErrorType.WRONG_INFORMATION;
                        }
                    }
                }
            }
        }
        return ErrorType.UNKNOWN;
    }

    private JSONObject checkYear(InputReference inputReference, EconBizReference econBizReference) {
        JSONObject yearJSONdetails = new JSONObject();
        String yearInput = inputReference.getYear();
        String yearEconBiz = econBizReference.getYear();
        if (yearInput != null && yearInput.equals(yearEconBiz)) {
            System.out.println("TOP! YEAR CORRECT!");
            setJSONDetails(yearJSONdetails, yearInput, true, "", ErrorType.NONE, "");
        } else if (yearInput.equals("") && inputReference.getPages().replace("--", "-").equals(yearEconBiz)) {
            System.out.println("TOP! YEAR CORRECT! BUT FREECITE MARKED IT AS PAGES");
            inputReference.setYear(inputReference.getPages());
            inputReference.setPages("");
            setJSONDetails(yearJSONdetails, yearInput, true, "", ErrorType.NONE, "freeCite marked year as pages");
        } else if (inputReference.getOriginalString().contains(yearEconBiz)) {
            System.out.println("TOP! YEAR CORRECT! IT was in the original string, so we suggest, that it was correct");
            inputReference.setYear(econBizReference.getYear());
            setJSONDetails(yearJSONdetails, yearInput, true, "", ErrorType.NONE, "freeCite marked it wrong, but was in the original input string");
        } else {
            System.out.println("WRONG! YEAR INCORRECT!");
            setJSONDetails(yearJSONdetails, yearInput, false, yearEconBiz, chooseErrorTypeNumbers(yearInput, yearEconBiz), "");
        }
        return yearJSONdetails;
    }

    private ErrorType chooseErrorTypeNumbers(String yearInput, String yearEconBiz) {
        if ((yearInput.equals("") || yearInput == null) && (!yearEconBiz.equals("") && yearEconBiz != null)) {
            return ErrorType.MISSING_INFORMATION;
        }
        if (!yearInput.equals("") && yearInput != null && (yearEconBiz.equals("") || yearEconBiz == null)) {
            return ErrorType.ADDITIONAL_INFORMATION;
        }
        if (yearInput.toCharArray().length != yearEconBiz.toCharArray().length) {
            return ErrorType.LEXICAL;
        } else {
            int dist = StringUtils.getLevenshteinDistance(yearInput, yearEconBiz);
            if (dist <= 1) {
                return ErrorType.LEXICAL;
            } else {
                return ErrorType.WRONG_INFORMATION;
            }
        }
    }

    private JSONObject checkPages(String pagesInput, String pagesEcon) {
        JSONObject pagesJSON = new JSONObject();
        JSONObject pagesJSONdetails = new JSONObject();
        if (pagesInput != null && pagesInput.equals(pagesEcon)) {
            System.out.println("TOP! Pages CORRECT!");
            setJSONDetails(pagesJSONdetails, pagesInput, true, "", ErrorType.NONE, "");
        } else {
            System.out.println("WRONG! Pages INCORRECT!");
            setJSONDetails(pagesJSONdetails, pagesInput, false, pagesEcon, chooseErrorTypeNumbers(pagesInput, pagesEcon), "");
        }
        pagesJSON.put("pages", pagesJSONdetails);
        return pagesJSONdetails;
    }

    private JSONObject checkAuthors(ArrayList authors, ArrayList authorsEcon) {
        JSONObject authorsJSON = new JSONObject();
        JSONObject authorsJSONdetails = new JSONObject();

        ArrayList authorsInputList = authors;

        if ((authors).equals(authorsEcon)) {
            System.out.println("TOP! AUTHORS CORRECT!");
           // correctCounter++;
           // counterAuthorsRight++;
           // counterErrorNONE++;
            setJSONDetails(authorsJSONdetails, authors.toString(), true, "", ErrorType.NONE, "");
        } else {
            System.out.println("WRONG! AUTHORS INCORRECT!");
          //  falseCounter++;
          //  counterAuthorsFalse++;
          //  withoutContentError = false;
            setJSONDetailsList(authorsJSONdetails, authors.toString(), false, authorsEcon.toString(), chooseErrorTypeList(authors, authorsEcon), "");
        }
        authorsJSON.put("authors", authorsJSONdetails);
        //referenceJSON.add(authorsJSON);
        return authorsJSONdetails;
    }

    private void setJSONDetails(JSONObject JSONdetails, String origValue, boolean correct, String correctInfo, ErrorType errorType, String note) {
        JSONdetails.put("originalInputValue", origValue);
        JSONdetails.put("correct", correct);
        JSONdetails.put("correctInformation", correctInfo);
        JSONdetails.put("errorType", errorType.getValue());
        JSONdetails.put("note", note);
    }

    private void setJSONDetailsList(JSONObject JSONdetails, String origValue, boolean correct, String correctInfo, ErrorType errorType, String note) {
        JSONdetails.put("originalInputValue", origValue);
        JSONdetails.put("correct", correct);
        JSONdetails.put("correctInformation", correctInfo);
        JSONdetails.put("errorType", errorType.getValue());
        JSONdetails.put("note", note);
    }

    private JSONObject checkTitle(InputReference inputRef, EconBizReference econRef) {
        String titleInput = inputRef.getTitle();
        String titleEcon = econRef.getTitle();
        JSONObject titleJSON = new JSONObject();
        JSONObject titleJSONdetails = new JSONObject();
        if (titleInput.equals(titleEcon)) {
            System.out.println("TOP! TITLE CORRECT!");
           // correctCounter++;
           // counterTitleRight++;
           // counterErrorNONE++;
            setJSONDetails(titleJSONdetails, titleInput, true, "", ErrorType.NONE, "");
        } else if (titleInput.equals(econRef.getChapter() + ". In " + econRef.getJournalOrBooktitle()) || titleInput.equals(econRef.getChapter())) {
            System.out.println("TOP! TITLE CORRECT!");
           // correctCounter++;
           // counterTitleRight++;
           // counterErrorNONE++;
            setJSONDetails(titleJSONdetails, titleInput, true, "", ErrorType.NONE, "Title is actually a chapter of a book.");
            inputRef.setTitle(econRef.getChapter());
            if (titleInput.contains("In " + econRef.getJournalOrBooktitle())) {
                inputRef.setJournalOrBooktitle(econRef.getJournalOrBooktitle());
            }
        } else {
            if (specialTitleChecks(inputRef, econRef)) {
                //TODO check doppelter code
                System.out.println("TOP! TITLE CORRECT!");
              //  correctCounter++;
              //  counterTitleRight++;
              //  counterErrorNONE++;
                String note = "After special checking, like splitting the title, we decided that it is probably correct, but freecite might have made a mistake!";
                setJSONDetails(titleJSONdetails, titleInput, true, "", ErrorType.NONE, note);
            } else {
                System.out.println("WRONG! TITLE INCORRECT!");
              //  falseCounter++;
               // counterTitleFalse++;
               // withoutContentError = false;
                setJSONDetails(titleJSONdetails, titleInput, false, titleEcon, chooseErrorTypeStrings(titleInput, titleEcon), "");
            }
        }
        titleJSON.put("title", titleJSONdetails);
        return titleJSONdetails;
    }

    private static boolean specialTitleChecks(InputReference inputRef, EconBizReference econRef) {
        String titleInput = inputRef.getTitle();
        String titleEcon = econRef.getTitle();
        String newTitle = "";
        String newJournalOrBooktitle = "";
        if (titleInput.contains(".") || titleInput.contains(";")) {
            if (titleInput.contains(".") && (titleInput.contains(";"))) {
                int indexDot = titleInput.indexOf(".");
                int indexSemicolon = titleInput.indexOf(";");
                if (indexDot < indexSemicolon) {
                    newTitle = titleInput.substring(0, titleInput.indexOf('.'));
                    newJournalOrBooktitle = titleInput.substring(titleInput.indexOf('.'), titleInput.length() - 1);
                } else {
                    newTitle = titleInput.substring(0, titleInput.indexOf(';'));
                    newJournalOrBooktitle = titleInput.substring(titleInput.indexOf(';'), titleInput.length() - 1);
                }
            } else if (titleInput.contains(".")) {
                newTitle = titleInput.substring(0, titleInput.indexOf('.'));
                newJournalOrBooktitle = titleInput.substring(titleInput.indexOf('.'), titleInput.length() - 1);
                System.out.println("TITLE WAS SPLIT!!! - .");
            } else if (titleInput.contains(";")) {
                newTitle = titleInput.substring(0, titleInput.indexOf(';'));
                newJournalOrBooktitle = titleInput.substring(titleInput.indexOf(';'), titleInput.length() - 1);
                System.out.println("TITLE WAS SPLIT!!! - ;");
            }

            String newJBtitle = newJournalOrBooktitle + " " + inputRef.getJournalOrBooktitle();
            if (newJBtitle.equals(econRef.getJournalOrBooktitle())) {
                inputRef.setJournalOrBooktitle(newJBtitle);
            }
            inputRef.setTitle(newTitle);
            //check again
            if (inputRef.getTitle().equals(titleEcon)) {
                return true;
            } else {
                return false;
            }
        }

        //check rawString
        //TODO checken: macht evtl andere lösungen unnötig, bsp sondercheck drüber

        //TODO löschen, da bereits in freeCiteHandler gemacht
        if (titleInput.contains("(") && !titleInput.contains(")")) {
            if ((titleInput + ")").equals(titleEcon)) {
                inputRef.setTitle(titleInput + ")");
                return true;
            }
        } else if (titleInput.contains("[") && !titleInput.contains("]")) {
            if ((titleInput + "]").equals(titleEcon)) {
                inputRef.setTitle(titleInput + "]");
                return true;
            }
        } else if (titleInput.contains("\"")) {
            long occurences = titleInput.chars().filter(num -> num == '\"').count();
            if (occurences % 2 == 1) {
                String title = titleInput + "\"";
                if (title.equals(titleEcon)) {
                    inputRef.setTitle(title);
                    return true;
                }
            }
        } else if (titleInput.contains("\'")) {
            long occurences = titleInput.chars().filter(num -> num == '\'').count();
            if (occurences % 2 == 1) {
                String title = titleInput + "\'";
                if (title.equals(titleEcon)) {
                    inputRef.setTitle(title);
                    return true;
                }
            }
        }
        //Todo check if this is also right, when the original has one word more, than the econbiz title, then also check if the input title is smaller
        String originalString = inputRef.getOriginalString();
        if (originalString.contains(titleEcon)) {
            System.out.println("PART OF ORIGINAL STRING");
            return true;
        }
        return false;
    }
}
