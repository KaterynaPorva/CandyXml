package ua.knu.candy.parser;

/** Thrown when an XML document cannot be turned into a list of candies. */
public class CandyParseException extends Exception {

    public CandyParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
