package ua.knu.candy.parser;

import ua.knu.candy.model.CandyBuilder;
import ua.knu.candy.model.ChocolateKind;
import ua.knu.candy.model.Kind;
import ua.knu.candy.model.XmlValued;

import java.math.BigDecimal;
import java.util.function.Function;

// Єдине місце, де XML-тег перетворюється на поле CandyBuilder.
// Наприклад: тег ENERGY з текстом "520" -> builder.energy(520).
// Цим класом користуються всі три парсери (SAX, DOM, StAX), тому правило
// "який тег куди кладемо" написане один раз, а не тричі.
final class CandyFieldMapper {

    private CandyFieldMapper() {
    }

    // Обробка атрибутів відкритого елемента.
    // attribute - функція "назва атрибута -> значення" (або null, якщо атрибута немає).
    // Атрибути є лише у двох елементів: <Candy id="..."> і <Type kind="..." filled="...">.
    static void applyAttributes(CandyBuilder builder, XmlTag tag, Function<String, String> attribute) {
        switch (tag) {
            case CANDY -> builder.id(required(attribute, XmlTag.ATTR_ID, tag));
            case TYPE -> {
                builder.kind(XmlValued.fromXml(Kind.class, required(attribute, XmlTag.ATTR_KIND, tag)));
                // filled необов'язковий: якщо його немає - false (як default у XSD)
                String filled = attribute.apply(XmlTag.ATTR_FILLED);
                builder.filled(filled != null && Boolean.parseBoolean(filled.trim()));
            }
            default -> {
                // інші елементи атрибутів не мають
            }
        }
    }

    // Обробка тексту елемента-листка: перетворюємо рядок на число/enum і кладемо в builder
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

    // Обов'язковий атрибут: якщо його немає або він порожній - помилка
    private static String required(Function<String, String> attribute, String name, XmlTag tag) {
        String value = attribute.apply(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Element <" + tag.xmlName() + "> is missing required attribute '" + name + "'");
        }
        return value.trim();
    }
}
