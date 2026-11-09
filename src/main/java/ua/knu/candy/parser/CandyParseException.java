package ua.knu.candy.parser;

// Один власний виняток для всіх парсерів.
// SAX кидає SAXException, StAX - XMLStreamException, DOM - ще інші.
// Той, хто викликає парсер, не має знати про ці відмінності - він ловить лише CandyParseException.
public class CandyParseException extends Exception {

    public CandyParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
