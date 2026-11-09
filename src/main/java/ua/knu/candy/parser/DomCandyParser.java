package ua.knu.candy.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ua.knu.candy.model.Candy;
import ua.knu.candy.model.CandyBuilder;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

// DOM (Document Object Model).
// Увесь документ спочатку завантажується в пам'ять як дерево об'єктів (Document -> Element -> ...),
// а потім ми це дерево обходимо. Найпростіший у використанні: можна ходити по дереву
// в будь-який бік. Мінус: для великих файлів потребує багато пам'яті.
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

        // 2. Знайти всі елементи <Candy>
        NodeList candyNodes = document.getDocumentElement()
                .getElementsByTagNameNS(XmlTag.NAMESPACE, XmlTag.CANDY.xmlName());

        // 3. Кожен <Candy> перетворити на об'єкт
        List<Candy> candies = new ArrayList<>(candyNodes.getLength());
        for (int i = 0; i < candyNodes.getLength(); i++) {
            CandyBuilder builder = new CandyBuilder();
            fill(builder, (Element) candyNodes.item(i));
            candies.add(builder.build());
        }
        return candies;
    }

    // Рекурсивно обходить елемент: атрибути -> у builder; якщо це листок - текст у builder;
    // інакше йдемо по дочірніх елементах.
    private static void fill(CandyBuilder builder, Element element) {
        XmlTag tag = XmlTag.fromName(element.getLocalName());
        CandyFieldMapper.applyAttributes(builder, tag,
                name -> element.hasAttribute(name) ? element.getAttribute(name) : null);

        if (tag.isLeaf()) {
            CandyFieldMapper.applyText(builder, tag, element.getTextContent().trim());
            return;
        }
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            // getChildNodes повертає і текстові вузли (пробіли між тегами) - беремо лише елементи
            if (children.item(i) instanceof Element child) {
                fill(builder, child);
            }
        }
    }
}
