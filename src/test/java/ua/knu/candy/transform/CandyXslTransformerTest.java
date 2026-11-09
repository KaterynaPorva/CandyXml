package ua.knu.candy.transform;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ua.knu.candy.Resources;
import ua.knu.candy.TestData;
import ua.knu.candy.model.Kind;
import ua.knu.candy.parser.XmlTag;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Тести XSL-перетворення. Результат перетворення читаємо як DOM-дерево і перевіряємо структуру.
class CandyXslTransformerTest {

    private static Document grouped;

    // Перетворення виконуємо один раз, далі всі тести дивляться на результат
    @BeforeAll
    static void transformOnce() throws Exception {
        CandyXslTransformer transformer = new CandyXslTransformer(Resources.source(Resources.XSL));
        String xml = transformer.transformToString(Resources.source(Resources.XML));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        grouped = factory.newDocumentBuilder().parse(new org.xml.sax.InputSource(new StringReader(xml)));
    }

    // Дочірні елементи вузла (без текстових вузлів)
    private static List<Element> childElements(Node parent) {
        List<Element> result = new ArrayList<>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element element) {
                result.add(element);
            }
        }
        return result;
    }

    @Test
    void rootElementIsKept() {
        Element root = grouped.getDocumentElement();
        assertEquals(XmlTag.CANDIES.xmlName(), root.getLocalName());
        assertEquals(XmlTag.NAMESPACE, root.getNamespaceURI());
    }

    @Test
    void oneGroupPerKindSortedAlphabetically() {
        // під коренем мають бути рівно три групи: caramel, chocolate, toffee
        List<String> groupNames = childElements(grouped.getDocumentElement()).stream()
                .map(Element::getLocalName)
                .toList();

        assertEquals(List.of("caramel", "chocolate", "toffee"), groupNames);
        assertEquals(Kind.values().length, groupNames.size());
    }

    @Test
    void everyCandyLandsInTheGroupOfItsKind() {
        // у кожній групі кожна цукерка має Type/@kind, що збігається з назвою групи
        int total = 0;
        for (Element group : childElements(grouped.getDocumentElement())) {
            List<Element> candies = childElements(group);
            assertTrue(candies.size() >= 1, "empty group " + group.getLocalName());
            for (Element candy : candies) {
                assertEquals(XmlTag.CANDY.xmlName(), candy.getLocalName());
                Element type = childElements(candy).stream()
                        .filter(e -> e.getLocalName().equals(XmlTag.TYPE.xmlName()))
                        .findFirst().orElseThrow();
                assertEquals(group.getLocalName(), type.getAttribute(XmlTag.ATTR_KIND));
            }
            total += candies.size();
        }
        assertEquals(TestData.CANDY_COUNT, total);   // жодна цукерка не загубилась і не здублювалась
    }

    @Test
    void candiesAreCopiedUnchanged() {
        NodeList candies = grouped.getElementsByTagNameNS(XmlTag.NAMESPACE, XmlTag.CANDY.xmlName());
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < candies.getLength(); i++) {
            Element candy = (Element) candies.item(i);
            ids.add(candy.getAttribute(XmlTag.ATTR_ID));
            // у скопійованої цукерки залишились усі шість дочірніх елементів
            assertEquals(6, childElements(candy).size());
        }
        assertEquals(TestData.CANDY_COUNT, ids.size());
        assertTrue(ids.containsAll(TestData.ALL_IDS));
    }

    @Test
    void invalidStylesheetIsRejectedAtConstruction() {
        StreamSource broken = new StreamSource(new StringReader("<not-a-stylesheet/>"));
        assertThrows(TransformerConfigurationException.class, () -> new CandyXslTransformer(broken));
    }
}
