package ua.knu.candy.parser;

import java.util.Locale;

/** The three parsing technologies required by the task, and a factory for each. */
public enum ParserType {
    SAX,
    DOM,
    STAX;

    /** @return a fresh parser of this type. */
    public CandyParser createParser() {
        return switch (this) {
            case SAX -> new SaxCandyParser();
            case DOM -> new DomCandyParser();
            case STAX -> new StaxCandyParser();
        };
    }

    /**
     * Parses a command-line value such as {@code sax}, {@code Dom}, {@code STAX}.
     *
     * @throws IllegalArgumentException for an unknown value
     */
    public static ParserType fromString(String value) {
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown parser type: '" + value + "'", e);
        }
    }
}
