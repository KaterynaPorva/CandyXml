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

/**
 * Validates XML documents against one compiled XSD schema.
 * All schema violations are collected (not only the first one) and returned as a {@link ValidationResult}.
 */
public final class XsdValidator {

    private static final Logger log = LoggerFactory.getLogger(XsdValidator.class);

    private final Schema schema;

    /**
     * @param xsd the schema document
     * @throws SAXException when the schema itself is invalid
     */
    public XsdValidator(Source xsd) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        this.schema = factory.newSchema(xsd);
        log.debug("XSD schema compiled");
    }

    /**
     * @param xml the document to check
     * @throws IOException when the document cannot be read
     */
    public ValidationResult validate(Source xml) throws IOException {
        ErrorCollector errors = new ErrorCollector();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(errors);
        try {
            validator.validate(xml);
        } catch (SAXException e) {
            // fatal (well-formedness) error - the handler has already recorded it,
            // unless the parser bypassed the handler
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

    /** Records every error instead of stopping at the first. */
    private static final class ErrorCollector implements ErrorHandler {

        private final List<String> messages = new ArrayList<>();

        @Override
        public void warning(SAXParseException e) {
            log.warn("XSD warning at {}: {}", location(e), e.getMessage());
        }

        @Override
        public void error(SAXParseException e) {
            messages.add(format(e));
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            messages.add(format(e));
            throw e; // per ErrorHandler contract, parsing cannot continue
        }

        private static String format(SAXParseException e) {
            return location(e) + ": " + e.getMessage();
        }

        private static String location(SAXParseException e) {
            return "line " + e.getLineNumber() + ", column " + e.getColumnNumber();
        }
    }
}
