package ua.knu.candy.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import ua.knu.candy.model.Candy;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.List;

// DOM (Document Object Model).
// Увесь документ спочатку завантажується в пам'ять як дерево об'єктів (Document -> Element -> ...),
// а потім ми це дерево обходимо. Найпростіший у використанні: можна ходити по дереву
// в будь-який бік. Мінус: для великих файлів потребує багато пам'яті.
//
// Цукерки збирає той самий CandyEventHandler, що й у SAX та StAX: під час обходу дерева
// ми самі повідомляємо йому події "елемент відкрився", "текст", "елемент закрився".
public final class DomCandyParser extends AbstractCandyParser {

    public DomCandyParser() {
        super(ParserType.DOM);
    }

    @Override
    protected List<Candy> doParse(InputStream in) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        // 1. Побудувати дерево
        Document document = factory.newDocumentBuilder().parse(in);

        // 2. Обійти дерево від кореня, передаючи події у спільний обробник
        CandyEventHandler events = new CandyEventHandler();
        walk(document.getDocumentElement(), events);

        // 3. Забрати готовий список цукерок
        return events.result();
    }

    // Рекурсивний обхід дерева. Для кожного елемента:
    //   зайшли в елемент      -> startElement (разом з доступом до його атрибутів)
    //   дочірній текст        -> characters
    //   дочірній елемент      -> заходимо в нього так само (рекурсія)
    //   вийшли з елемента     -> endElement
    private static void walk(Element element, CandyEventHandler events) {
        String name = element.getLocalName();
        events.startElement(name,
                attribute -> element.hasAttribute(attribute) ? element.getAttribute(attribute) : null);

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element childElement) {
                walk(childElement, events);
            } else if (child instanceof Text text) {
                events.characters(text.getData());
            }
        }

        events.endElement(name);
    }
}
