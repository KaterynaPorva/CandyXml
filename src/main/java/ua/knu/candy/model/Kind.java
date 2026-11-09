package ua.knu.candy.model;

/** Kind of candy. Mirrors {@code KindType} in the XSD. */
public enum Kind implements XmlValued {
    CARAMEL("caramel"),
    TOFFEE("toffee"),
    CHOCOLATE("chocolate");

    private final String xmlValue;

    Kind(String xmlValue) {
        this.xmlValue = xmlValue;
    }

    @Override
    public String xmlValue() {
        return xmlValue;
    }
}
