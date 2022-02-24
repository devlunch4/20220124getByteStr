//@Data
public class ProcRow {

    public static String delim = "¶";

    public ProcRow(Integer seq, String type, String tagId, String text, String subTitle,
                   PROC_TYPE procType) {
        this.seq = seq; // sentence seq for sort
        this.type = type; // TELGM-TYP
        this.tagId = tagId; // TAG-NO
        this.text = text; // TELGM-CTNT
        this.subTitle = subTitle;
        this.procType = procType; // flag - replace/translate...
    }

    private Integer seq;
    private String type;
    private String tagId;
    private String text;
    private String subTitle;
    private PROC_TYPE procType;

    public String getTextWitTag() {
        return this.tagId + delim + this.text;
    }
}