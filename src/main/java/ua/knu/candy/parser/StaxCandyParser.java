package ua.knu.candy.parser;

import ua.knu.candy.model.Candy;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.List;

/** StAX: pull parser - we ask the reader for the next event in a loop. */
public final class StaxCandyParser extends AbstractCandyParser {

    public StaxCandyParser() {
        super(ParserType.STAX);
    }

    @Override
    protected List<Candy> doParse(InputStream in) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        factory.setProperty(XMLInputFactory.IS_COALESCING, true);

        CandyEventHandler events = new CandyEventHandler();
        XMLStreamReader reader = factory.createXMLStreamReader(in);
        try {
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamConstants.START_ELEMENT -> events.startElement(
                            reader.getLocalName(),
                            name -> reader.getAttributeValue(null, name));
                    case XMLStreamConstants.CHARACTERS, XMLStreamConstants.CDATA ->
                            events.characters(reader.getText());
                    case XMLStreamConstants.END_ELEMENT -> events.endElement(reader.getLocalName());
                    default -> {
                        // comments, whitespace, processing instructions: ignore
                    }
                }
            }
        } finally {
            reader.close();
        }
        return events.result();
    }
}
