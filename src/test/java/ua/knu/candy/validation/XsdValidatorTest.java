package ua.knu.candy.validation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import ua.knu.candy.Resources;
import ua.knu.candy.TestData;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Тести валідації за XSD: правильний документ проходить, кожен вид помилки - ловиться
class XsdValidatorTest {

    private static XsdValidator validator;

    // Схему компілюємо один раз на весь клас
    @BeforeAll
    static void compileSchema() throws SAXException {
        validator = new XsdValidator(Resources.source(Resources.XSD));
    }

    private static ValidationResult validate(String resource) throws IOException {
        return validator.validate(Resources.source(resource));
    }

    // Усі помилки одним рядком - зручно для повідомлення, якщо тест упаде
    private static String joined(ValidationResult result) {
        return String.join("\n", result.errors());
    }

    @Test
    void bundledDocumentIsValid() throws IOException {
        ValidationResult result = validate(Resources.XML);
        assertTrue(result.isValid(), joined(result));
        assertEquals(0, result.errors().size());
    }

    @Test
    void energyAboveUpperBoundIsRejected() throws IOException {
        // граничне значення: maxInclusive 900
        ValidationResult result = validate(TestData.INVALID_ENERGY);
        assertFalse(result.isValid());
        assertTrue(joined(result).contains("950"), joined(result));
    }

    @Test
    void duplicateIdIsRejected() throws IOException {
        // тип ID: два однакові id в одному документі
        ValidationResult result = validate(TestData.INVALID_DUPLICATE_ID);
        assertFalse(result.isValid());
        assertTrue(joined(result).contains("c1"), joined(result));
    }

    @Test
    void allErrorsAreCollectedNotJustTheFirst() throws IOException {
        // у файлі дві помилки: немає атрибута kind і "ruby" не входить у перелік ChocolateKindType
        ValidationResult result = validate(TestData.INVALID_MISSING_KIND);
        assertFalse(result.isValid());
        assertTrue(result.errors().size() >= 2, joined(result));
        assertTrue(joined(result).contains("kind"), joined(result));
        assertTrue(joined(result).contains("ruby"), joined(result));
    }

    @Test
    void unknownElementIsRejected() throws IOException {
        ValidationResult result = validate(TestData.UNKNOWN_ELEMENT);
        assertFalse(result.isValid());
        assertTrue(joined(result).contains("Color"), joined(result));
    }

    @Test
    void missingElementIsRejected() throws IOException {
        ValidationResult result = validate(TestData.MISSING_FIELD);
        assertFalse(result.isValid());
        assertTrue(joined(result).contains("Production"), joined(result));
    }

    @Test
    void malformedDocumentIsInvalidNotAnException() throws IOException {
        // зламаний XML - не виняток, а результат з однією помилкою і номером рядка
        ValidationResult result = validate(TestData.MALFORMED);
        assertFalse(result.isValid());
        assertEquals(1, result.errors().size());
        assertTrue(result.errors().get(0).startsWith("line "), joined(result));
    }

    @Test
    void invalidSchemaIsRejectedAtConstruction() {
        StreamSource broken = new StreamSource(new StringReader("<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'><oops/></xs:schema>"));
        assertThrows(SAXException.class, () -> new XsdValidator(broken));
    }

    @Test
    void validationResultIsImmutable() {
        ValidationResult result = new ValidationResult(new java.util.ArrayList<>(java.util.List.of("e")));
        assertThrows(UnsupportedOperationException.class, () -> result.errors().add("x"));
    }
}
