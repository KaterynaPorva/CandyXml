package ua.knu.candy.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.knu.candy.model.Candy;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

// Базовий клас для трьох парсерів (патерн "шаблонний метод").
// Спільне для всіх - логування і перетворення будь-якої помилки на CandyParseException -
// написане тут у parse(). Кожен парсер реалізує лише doParse() зі своєю технологією.
abstract class AbstractCandyParser implements CandyParser {

    // getClass() - щоб у лозі було видно, який саме парсер пише (SaxCandyParser, DomCandyParser...)
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
            // Кожна технологія кидає свій виняток (SAXException, XMLStreamException...),
            // а мапінг полів - IllegalArgument/IllegalState. Назовні віддаємо один тип.
            log.error("{} parser failed: {}", type, e.getMessage());
            throw new CandyParseException(type + " parser failed: " + e.getMessage(), e);
        }
    }

    // Те, що відрізняється між SAX, DOM і StAX
    protected abstract List<Candy> doParse(InputStream in) throws Exception;
}
