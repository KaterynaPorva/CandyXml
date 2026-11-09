package ua.knu.candy.parser;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Усі назви елементів XML-документа в одному місці (плюс назви атрибутів).
// Навіщо: три парсери працюють з тими самими тегами. Якби кожен писав "Energy", "Sugar"
// рядками, одна одруківка зламала б лише один парсер і її було б важко знайти.
// leaf = true означає "листок": елемент містить текст і не має дочірніх елементів (<Name>Lacosta</Name>).
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

    // Простір імен документа (той самий, що targetNamespace у XSD)
    public static final String NAMESPACE = "http://knu.ua/candy";

    // Назви атрибутів
    public static final String ATTR_ID = "id";
    public static final String ATTR_KIND = "kind";
    public static final String ATTR_FILLED = "filled";

    // Словник "назва тегу -> константа", щоб fromName працював швидко
    private static final Map<String, XmlTag> BY_NAME = Stream.of(values())
            .collect(Collectors.toUnmodifiableMap(XmlTag::xmlName, Function.identity()));

    private final String xmlName;
    private final boolean leaf;

    XmlTag(String xmlName, boolean leaf) {
        this.xmlName = xmlName;
        this.leaf = leaf;
    }

    public String xmlName() {
        return xmlName;
    }

    public boolean isLeaf() {
        return leaf;
    }

    // "Energy" -> ENERGY. Невідомий тег = помилка, бо такого елемента немає у схемі.
    public static XmlTag fromName(String localName) {
        XmlTag tag = BY_NAME.get(localName);
        if (tag == null) {
            throw new IllegalArgumentException("Unknown XML element: <" + localName + ">");
        }
        return tag;
    }
}
