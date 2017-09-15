/**
 * Created by Uni on 03.07.2017.
 */
public enum ErrorType {
    //TODO add all of them
    UNKNOWN("unknown"), LEXICAL("lexical"), WRONG_INFORMATION("wrong information"), ADDITIONAL_INFORMATION("additional information"), MISSING_INFORMATION("missing information"), ORDER("order"), WRONG_DELIMITING("wrong delimiting"), WRONG_SHORTCUTTING("wrong shortcutting"), WRONG_SORTING("wrong_sorting"), NONE("none");

    private String value;

    ErrorType(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

}
