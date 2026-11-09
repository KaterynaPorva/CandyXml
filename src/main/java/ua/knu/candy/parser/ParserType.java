package ua.knu.candy.parser;

import java.util.Locale;

// Три парсери, які вимагає завдання. Заодно це фабрика: createParser() створює потрібний.
public enum ParserType {
    SAX,
    DOM,
    STAX;

    public CandyParser createParser() {
        return switch (this) {
            case SAX -> new SaxCandyParser();
            case DOM -> new DomCandyParser();
            case STAX -> new StaxCandyParser();
        };
    }

    // "sax" / "Dom" / "STAX" з командного рядка -> константа
    public static ParserType fromString(String value) {
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown parser type: '" + value + "'", e);
        }
    }
}
