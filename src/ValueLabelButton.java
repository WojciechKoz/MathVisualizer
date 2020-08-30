
/**
 * LabelButton with pair of title and value
 * title is a constant string to identify this button and to be drawn in button
 * to explain what the value is. Value can be any String (usually number convert to be a string) and it can be updated
 * This class inherit most method from LabelButton (doesn't support hovering either)
 */
class ValueLabelButton extends LabelButton {
    private final String title;
    private String value;

    ValueLabelButton(int x, int y, int width, int height, String title, String value, int fontSize) {
        super(x, y, width, height, title+": "+value, fontSize);
        this.title = title;
        this.value = value;
    }

    /**
     * Updates values of {@code value} and {@code label} variables
     * @param newValue - it's a new value of {@code value} variable
     */
    void updateValue(String newValue) {
        value = newValue;
        label = title+": "+value;
    }

    public String getTitle() { return title; }
}