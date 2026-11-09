package ua.knu.candy.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Перевірка XML-документа на відповідність XSD-схемі (пункт 4 завдання).
// Як працює:
//   1. SchemaFactory читає файл .xsd і компілює його в об'єкт Schema (один раз, у конструкторі).
//   2. Для кожного документа Schema.newValidator().validate(xml).
//   3. Стандартна поведінка - кинути виняток на першій помилці. Ми підставляємо свій ErrorHandler,
//      який збирає ВСІ помилки в список, щоб побачити їх одразу.
public final class XsdValidator {

    private static final Logger log = LoggerFactory.getLogger(XsdValidator.class);

    private final Schema schema;

    public XsdValidator(Source xsd) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        this.schema = factory.newSchema(xsd);   // тут же впаде SAXException, якщо сама схема зламана
        log.debug("XSD schema compiled");
    }

    public ValidationResult validate(Source xml) throws IOException {
        ErrorCollector errors = new ErrorCollector();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(errors);
        try {
            validator.validate(xml);
        } catch (SAXException e) {
            // Сюди потрапляємо лише при фатальній помилці (XML навіть не well-formed).
            // Зазвичай ErrorCollector уже записав повідомлення; якщо ні - додаємо вручну.
            if (errors.messages.isEmpty()) {
                errors.messages.add(e.getMessage());
            }
        }

        ValidationResult result = new ValidationResult(errors.messages);
        if (result.isValid()) {
            log.info("XML document is valid against the XSD");
        } else {
            log.warn("XML document is NOT valid: {} error(s)", result.errors().size());
        }
        return result;
    }

    // Збирач помилок. Validator викликає ці методи замість того, щоб кидати виняток.
    private static final class ErrorCollector implements ErrorHandler {

        private final List<String> messages = new ArrayList<>();

        @Override
        public void warning(SAXParseException e) {
            log.warn("XSD warning at {}: {}", location(e), e.getMessage());
        }

        // Звичайна помилка схеми (вихід за межі, невідомий елемент...) - записали і йдемо далі
        @Override
        public void error(SAXParseException e) {
            messages.add(format(e));
        }

        // Фатальна (зламаний XML) - записали, але далі читати неможливо, тому кидаємо
        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            messages.add(format(e));
            throw e;
        }

        private static String format(SAXParseException e) {
            return location(e) + ": " + e.getMessage();
        }

        private static String location(SAXParseException e) {
            return "line " + e.getLineNumber() + ", column " + e.getColumnNumber();
        }
    }
}
