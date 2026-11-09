package ua.knu.candy.parser;

import ua.knu.candy.model.Candy;
import ua.knu.candy.model.CandyBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Parser-agnostic state machine for event-driven parsing.
 * Both SAX ({@link SaxCandyParser}) and StAX ({@link StaxCandyParser}) produce the same three
 * kinds of events - element start, text, element end - so the logic of turning them into
 * {@link Candy} objects is written once here.
 */
final class CandyEventHandler {

    private final List<Candy> candies = new ArrayList<>();
    private final StringBuilder text = new StringBuilder();
    private CandyBuilder current;

    /**
     * @param localName element name without namespace prefix
     * @param attribute returns an attribute value by name, or {@code null} when absent
     */
    void startElement(String localName, Function<String, String> attribute) {
        XmlTag tag = XmlTag.fromName(localName);
        text.setLength(0);
        if (tag == XmlTag.CANDY) {
            current = new CandyBuilder();
        }
        if (current != null) {
            CandyFieldMapper.applyAttributes(current, tag, attribute);
        }
    }

    void characters(char[] chars, int start, int length) {
        text.append(chars, start, length);
    }

    void characters(String chars) {
        text.append(chars);
    }

    void endElement(String localName) {
        XmlTag tag = XmlTag.fromName(localName);
        if (tag == XmlTag.CANDY) {
            candies.add(current.build());
            current = null;
        } else if (tag.isLeaf() && current != null) {
            CandyFieldMapper.applyText(current, tag, text.toString().trim());
        }
        text.setLength(0);
    }

    List<Candy> result() {
        return Collections.unmodifiableList(candies);
    }
}
