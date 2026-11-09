package ua.knu.candy.parser;

import ua.knu.candy.model.Candy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Reads a Candy XML document into a list of {@link Candy} objects. */
public interface CandyParser {

    /** @return which technology this parser is built on. */
    ParserType type();

    /**
     * Parses the document from a stream. The stream is not closed.
     *
     * @throws CandyParseException when the XML is malformed or does not match the model
     */
    List<Candy> parse(InputStream in) throws CandyParseException;

    /** Convenience overload for files. */
    default List<Candy> parse(Path file) throws CandyParseException {
        try (InputStream in = Files.newInputStream(file)) {
            return parse(in);
        } catch (IOException e) {
            throw new CandyParseException("Cannot read " + file, e);
        }
    }
}
