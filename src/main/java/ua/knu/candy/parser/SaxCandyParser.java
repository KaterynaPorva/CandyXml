package ua.knu.candy.parser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import ua.knu.candy.model.Candy;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.List;

/** SAX: push parser - the parser calls our handler for every event. */
public final class SaxCandyParser extends AbstractCandyParser {

    public SaxCandyParser() {
        super(ParserType.SAX);
    }

    @Override
    protected List<Candy> doParse(InputStream in) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        Handler handler = new Handler();
        factory.newSAXParser().parse(in, handler);
        return handler.events.result();
    }

    /** Adapts SAX callbacks to the shared {@link CandyEventHandler}. */
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
