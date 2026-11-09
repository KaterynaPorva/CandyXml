package ua.knu.candy.model;

/** Kind of chocolate used in a chocolate candy. Mirrors {@code ChocolateKindType} in the XSD. */
public enum ChocolateKind implements XmlValued {
    DARK("dark"),
    MILK("milk"),
    WHITE("white");

    private final String xmlValue;

    ChocolateKind(String xmlValue) {
        this.xmlValue = xmlValue;
    }

    @Override
    public String xmlValue() {
        return xmlValue;
    }
}
