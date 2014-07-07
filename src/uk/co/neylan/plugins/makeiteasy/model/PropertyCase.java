package uk.co.neylan.plugins.makeiteasy.model;

public enum PropertyCase {
    SAME_AS_ORIGINAL("Same as original"),
    UPPERCASE("Uppercase");

    private String displayText;

    PropertyCase(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }

    public static PropertyCase fromDisplayText(String selectedItem) {
        for (PropertyCase propertyCase : PropertyCase.values()) {
            if (propertyCase.getDisplayText().equals(selectedItem)) {
                return propertyCase;
            }
        }
        throw new IllegalArgumentException("value not found");
    }
}
