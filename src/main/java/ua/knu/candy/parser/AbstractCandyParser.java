package ua.knu.candy.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.knu.candy.model.Candy;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * Template method: logging and exception translation are identical for every parser,
 * only {@link #doParse(InputStream)} differs.
 */
abstract class AbstractCandyParser implements CandyParser {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final ParserType type;

    protected AbstractCandyParser(ParserType type) {
        this.type = type;
    }

    @Override
    public final ParserType type() {
        return type;
    }

    @Override
    public final List<Candy> parse(InputStream in) throws CandyParseException {
        Objects.requireNonNull(in, "in");
        log.debug("{} parser: start", type);
        try {
            List<Candy> candies = doParse(in);
            log.info("{} parser: read {} candies", type, candies.size());
            return candies;
        } catch (Exception e) {
            // Each technology throws its own checked exception (SAXException, XMLStreamException, ...)
            // and field mapping throws IllegalArgument/IllegalState; unify them for the caller.
            log.error("{} parser failed: {}", type, e.getMessage());
            throw new CandyParseException(type + " parser failed: " + e.getMessage(), e);
        }
    }

    /** Technology-specific parsing. Any exception is wrapped into {@link CandyParseException}. */
    protected abstract List<Candy> doParse(InputStream in) throws Exception;
}
