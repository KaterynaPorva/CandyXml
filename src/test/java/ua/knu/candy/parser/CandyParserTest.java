package ua.knu.candy.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ua.knu.candy.Resources;
import ua.knu.candy.TestData;
import ua.knu.candy.model.Candy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Тести парсерів. Один клас для всіх трьох: @EnumSource(ParserType.class) запускає
// кожен тест по черзі для SAX, DOM і StAX. Так гарантуємо, що вони поводяться однаково.
class CandyParserTest {

    // Відкрити ресурс і розпарсити його обраним парсером
    private static List<Candy> parse(ParserType type, String resource) throws CandyParseException {
        try (InputStream in = Resources.open(resource)) {
            return type.createParser().parse(in);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @ParameterizedTest
    @EnumSource(ParserType.class)
    void parserReportsItsType(ParserType type) {
        assertEquals(type, type.createParser().type());
    }

    @ParameterizedTest
    @EnumSource(ParserType.class)
    void readsEveryCandyInDocumentOrder(ParserType type) throws CandyParseException {
        List<Candy> candies = parse(type, Resources.XML);

        assertEquals(TestData.CANDY_COUNT, candies.size());
        assertEquals(TestData.ALL_IDS, candies.stream().map(Candy::id).toList());
    }

    // Порівнюємо цілий об'єкт з очікуваним - перевіряються всі поля одразу (record має equals)
    @ParameterizedTest
    @EnumSource(ParserType.class)
    void readsAllFieldsOfChocolateCandy(ParserType type) throws CandyParseException {
        assertEquals(TestData.LACOSTA, parse(type, Resources.XML).get(0));
    }

    @ParameterizedTest
    @EnumSource(ParserType.class)
    void handlesOptionalElementAndDefaultAttribute(ParserType type) throws CandyParseException {
        // карамель: немає ChocolateType і немає filled
        assertEquals(TestData.BARBARIS, parse(type, Resources.XML).get(1));
    }

    @Test
    void allParsersProduceIdenticalResults() throws CandyParseException {
        List<Candy> sax = parse(ParserType.SAX, Resources.XML);
        List<Candy> dom = parse(ParserType.DOM, Resources.XML);
        List<Candy> stax = parse(ParserType.STAX, Resources.XML);

        assertEquals(sax, dom);
        assertEquals(sax, stax);
    }

    // Далі - негативні тести на "зламаних" документах

    @ParameterizedTest
    @EnumSource(ParserType.class)
    void malformedXmlIsReportedAsParseException(ParserType type) {
        CandyParseException e = assertThrows(CandyParseException.class, () -> parse(type, TestData.MALFORMED));
        assertTrue(e.getMessage().startsWith(type.toString()));
    }

    @ParameterizedTest
    @EnumSource(ParserType.class)
    void unknownElementIsRejected(ParserType type) {
        CandyParseException e = assertThrows(CandyParseException.class, () -> parse(type, TestData.UNKNOWN_ELEMENT));
        assertTrue(e.getMessage().contains("Color"), e.getMessage());
    }

    @ParameterizedTest
    @EnumSource(ParserType.class)
    void missingRequiredFieldIsRejected(ParserType type) {
        CandyParseException e = assertThrows(CandyParseException.class, () -> parse(type, TestData.MISSING_FIELD));
        assertTrue(e.getMessage().contains("Production"), e.getMessage());
    }

    @ParameterizedTest
    @EnumSource(ParserType.class)
    void missingRequiredAttributeIsRejected(ParserType type) {
        CandyParseException e = assertThrows(CandyParseException.class,
                () -> parse(type, TestData.INVALID_MISSING_KIND));
        assertTrue(e.getMessage().contains("kind"), e.getMessage());
    }

    // @TempDir - JUnit створює тимчасову теку і сам її видаляє після тесту
    @ParameterizedTest
    @EnumSource(ParserType.class)
    void parsesFromFilePath(ParserType type, @TempDir Path dir) throws Exception {
        Path file = dir.resolve("candies.xml");
        try (InputStream in = Resources.open(Resources.XML)) {
            Files.copy(in, file);
        }

        assertEquals(TestData.CANDY_COUNT, type.createParser().parse(file).size());
    }

    @ParameterizedTest
    @EnumSource(ParserType.class)
    void missingFileIsReportedAsParseException(ParserType type, @TempDir Path dir) {
        Path missing = dir.resolve("nope.xml");
        assertThrows(CandyParseException.class, () -> type.createParser().parse(missing));
    }

    @Test
    void parserTypeFromStringIsCaseInsensitive() {
        assertEquals(ParserType.SAX, ParserType.fromString("sax"));
        assertEquals(ParserType.DOM, ParserType.fromString(" Dom "));
        assertEquals(ParserType.STAX, ParserType.fromString("STAX"));
        assertThrows(IllegalArgumentException.class, () -> ParserType.fromString("jaxb"));
    }

    @Test
    void everyParserTypeHasAFactory() {
        assertEquals(3, Stream.of(ParserType.values()).map(ParserType::createParser).distinct().count());
    }
}
