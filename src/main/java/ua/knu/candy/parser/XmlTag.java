package ua.knu.candy.parser;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Every element name of the Candy XML document, plus its attribute names.
 * Single source of truth shared by the SAX, DOM and StAX parsers.
 */
public enum XmlTag {
    CANDIES("Candies", false),
    CANDY("Candy", false),
    NAME("Name", true),
    ENERGY("Energy", true),
    TYPE("Type", false),
    INGREDIENTS("Ingredients", false),
    WATER("Water", true),
    SUGAR("Sugar", true),
    FRUCTOSE("Fructose", true),
    CHOCOLATE_TYPE("ChocolateType", true),
    VANILLIN("Vanillin", true),
    VALUE("Value", false),
    PROTEINS("Proteins", true),
    FATS("Fats", true),
    CARBOHYDRATES("Carbohydrates", true),
    PRODUCTION("Production", true);

    public static final String NAMESPACE = "http://knu.ua/candy";

    public static final String ATTR_ID = "id";
    public static final String ATTR_KIND = "kind";
    public static final String ATTR_FILLED = "filled";

    private static final Map<String, XmlTag> BY_NAME = Stream.of(values())
            .collect(Collectors.toUnmodifiableMap(XmlTag::xmlName, Function.identity()));

    private final String xmlName;
    private final boolean leaf;

    XmlTag(String xmlName, boolean leaf) {
        this.xmlName = xmlName;
        this.leaf = leaf;
    }

    /** @return the element name as written in XML. */
    public String xmlName() {
        return xmlName;
    }

    /** @return {@code true} when the element carries a text value and no child elements. */
    public boolean isLeaf() {
        return leaf;
    }

    /**
     * @throws IllegalArgumentException for an element that is not part of the schema
     */
    public static XmlTag fromName(String localName) {
        XmlTag tag = BY_NAME.get(localName);
        if (tag == null) {
            throw new IllegalArgumentException("Unknown XML element: <" + localName + ">");
        }
        return tag;
    }
}
