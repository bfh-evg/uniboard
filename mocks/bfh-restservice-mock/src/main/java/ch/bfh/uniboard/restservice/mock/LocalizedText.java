package ch.bfh.uniboard.restservice.mock;


public class LocalizedText  {

    private static final long serialVersionUID = 1L;
    /**
     * Language code.
     */
    private LanguageCode languageCode;
    /**
     * Text in the corresponding language.
     */
    private String text;

    public LocalizedText() {
    }
   
    public LocalizedText(LanguageCode language, String text) {
        this.languageCode = language;
        this.text = text;
    }

    /**
     * Returns the language code.
     *
     * @return a language code
     */
    public LanguageCode getLanguageCode() {
        return languageCode;
    }

    /**
     * Returns the corresponding text.
     *
     * @return a text
     */
    public String getText() {
        return text;
    }

    public void setLanguageCode(LanguageCode languageCode) {
	this.languageCode = languageCode;
    }

    public void setText(String text) {
	this.text = text;
    }

    
}
