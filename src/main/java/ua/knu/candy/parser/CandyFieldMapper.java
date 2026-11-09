package ua.knu.candy.parser;

import ua.knu.candy.model.CandyBuilder;
import ua.knu.candy.model.ChocolateKind;
import ua.knu.candy.model.Kind;
import ua.knu.candy.model.XmlValued;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Maps XML attributes and element text onto {@link CandyBuilder} fields.
 * This is the only place that knows which tag feeds which field, so SAX, DOM and StAX
 * parsers all share one mapping instead of three copies.
 */
final class CandyFieldMapper {

    private CandyFieldMapper() {
    }

    /**
     * Applies the attributes of an element that has just been opened.
     *
     * @param attribute returns an attribute value by name, or {@code null} when absent
     */
    static void applyAttributes(CandyBuilder builder, XmlTag tag, Function<String, String> attribute) {
        switch (tag) {
            case CANDY -> builder.id(required(attribute, XmlTag.ATTR_ID, tag));
            case TYPE -> {
                builder.kind(XmlValued.fromXml(Kind.class, required(attribute, XmlTag.ATTR_KIND, tag)));
                String filled = attribute.apply(XmlTag.ATTR_FILLED);
                builder.filled(filled != null && Boolean.parseBoolean(filled.trim()));
            }
            default -> {
                // other elements have no attributes
            }
        }
    }

    /** Applies the trimmed text content of a leaf element. */
    static void applyText(CandyBuilder builder, XmlTag tag, String text) {
        switch (tag) {
            case NAME -> builder.name(text);
            case ENERGY -> builder.energy(Integer.parseInt(text));
            case WATER -> builder.water(Integer.parseInt(text));
            case SUGAR -> builder.sugar(Integer.parseInt(text));
            case FRUCTOSE -> builder.fructose(Integer.parseInt(text));
            case CHOCOLATE_TYPE -> builder.chocolateType(XmlValued.fromXml(ChocolateKind.class, text));
            case VANILLIN -> builder.vanillin(Integer.parseInt(text));
            case PROTEINS -> builder.proteins(new BigDecimal(text));
            case FATS -> builder.fats(new BigDecimal(text));
            case CARBOHYDRATES -> builder.carbohydrates(new BigDecimal(text));
            case PRODUCTION -> builder.production(text);
            default -> throw new IllegalArgumentException(
                    "Element <" + tag.xmlName() + "> does not carry a text value");
        }
    }

    private static String required(Function<String, String> attribute, String name, XmlTag tag) {
        String value = attribute.apply(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Element <" + tag.xmlName() + "> is missing required attribute '" + name + "'");
        }
        return value.trim();
    }
}
