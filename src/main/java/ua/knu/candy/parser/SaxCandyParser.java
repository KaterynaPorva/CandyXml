package ua.knu.candy.parser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import ua.knu.candy.model.Candy;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.List;

// SAX (Simple API for XML) - "push"-парсер.
// Читає документ потоково зверху вниз і САМ викликає наші методи-обробники
// (startElement, characters, endElement) для кожної події. Документ у пам'ять
// цілком не завантажується, тому підходить для великих файлів.
// Мінус: не можна повернутися назад, стан треба тримати самим (це робить CandyEventHandler).
public final class SaxCandyParser extends AbstractCandyParser {

    public SaxCandyParser() {
        super(ParserType.SAX);
    }

    @Override
    protected List<Candy> doParse(InputStream in) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);   // щоб localName був без префікса простору імен
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        Handler handler = new Handler();
        factory.newSAXParser().parse(in, handler);   // тут парсер сам викликає handler
        return handler.events.result();
    }

    // Обробник SAX: лише пересилає події у спільний CandyEventHandler
    private static final class Handler extends DefaultHandler {

        private final CandyEventHandler events = new CandyEventHandler();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            events.startElement(localName, attributes::getValue);
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            events.characters(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            events.endElement(localName);
        }
    }
}
