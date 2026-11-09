package ua.knu.candy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import ua.knu.candy.model.Candy;
import ua.knu.candy.model.CandyComparator;
import ua.knu.candy.parser.CandyParseException;
import ua.knu.candy.parser.ParserType;
import ua.knu.candy.transform.CandyXslTransformer;
import ua.knu.candy.validation.ValidationResult;
import ua.knu.candy.validation.XsdValidator;

import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point. Validates the bundled XML against the XSD, parses it with the chosen parser,
 * sorts the candies with the chosen comparator and writes the XSL-grouped document.
 *
 * <pre>
 *   java -jar candy-xml.jar [sax|dom|stax] [name|energy|production|sugar_desc] [outputDir]
 * </pre>
 */
public final class CandyApp {

    private static final Logger log = LoggerFactory.getLogger(CandyApp.class);

    public static final String GROUPED_FILE_NAME = "candies-grouped.xml";

    public static void main(String[] args) {
        try {
            ParserType parserType = args.length > 0 ? ParserType.fromString(args[0]) : ParserType.SAX;
            CandyComparator order = args.length > 1 ? CandyComparator.fromString(args[1]) : CandyComparator.BY_NAME;
            Path outputDir = args.length > 2 ? Path.of(args[2]) : Path.of("output");

            new CandyApp().run(parserType, order, outputDir);
        } catch (IllegalArgumentException e) {
            log.error("Bad arguments: {}", e.getMessage());
            log.error("Usage: [sax|dom|stax] [name|energy|production|sugar_desc] [outputDir]");
            System.exit(2);
        } catch (Exception e) {
            log.error("Application failed: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Runs the full pipeline.
     *
     * @return the parsed candies, sorted with {@code order}
     * @throws IllegalStateException when the XML does not pass XSD validation
     */
    public List<Candy> run(ParserType parserType, CandyComparator order, Path outputDir)
            throws SAXException, IOException, CandyParseException, TransformerException {

        log.info("Parser: {}, sort order: {}, output: {}", parserType, order, outputDir.toAbsolutePath());

        // 1. Validate
        XsdValidator validator = new XsdValidator(Resources.source(Resources.XSD));
        ValidationResult validation = validator.validate(Resources.source(Resources.XML));
        if (!validation.isValid()) {
            validation.errors().forEach(error -> log.error("  {}", error));
            throw new IllegalStateException("XML document failed XSD validation");
        }

        // 2. Parse
        List<Candy> candies;
        try (InputStream in = Resources.open(Resources.XML)) {
            candies = parserType.createParser().parse(in);
        }

        // 3. Sort
        List<Candy> sorted = new ArrayList<>(candies);
        sorted.sort(order);
        log.info("Candies sorted {}:", order);
        sorted.forEach(candy -> log.info("  {} | {} | {} kcal | {} | {}",
                candy.id(), candy.name(), candy.energy(), candy.type().kind().xmlValue(), candy.production()));

        // 4. Transform
        Files.createDirectories(outputDir);
        Path grouped = outputDir.resolve(GROUPED_FILE_NAME);
        CandyXslTransformer transformer = new CandyXslTransformer(Resources.source(Resources.XSL));
        transformer.transform(Resources.source(Resources.XML), new StreamResult(grouped.toFile()));
        log.info("Grouped document written to {}", grouped.toAbsolutePath());

        return sorted;
    }
}
