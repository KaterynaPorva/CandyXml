package ua.knu.candy.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ua.knu.candy.model.Candy;
import ua.knu.candy.model.CandyBuilder;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/** DOM: the whole document is loaded into a tree, then the tree is walked. */
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

        Document document = factory.newDocumentBuilder().parse(in);
        NodeList candyNodes = document.getDocumentElement()
                .getElementsByTagNameNS(XmlTag.NAMESPACE, XmlTag.CANDY.xmlName());

        List<Candy> candies = new ArrayList<>(candyNodes.getLength());
        for (int i = 0; i < candyNodes.getLength(); i++) {
            CandyBuilder builder = new CandyBuilder();
            fill(builder, (Element) candyNodes.item(i));
            candies.add(builder.build());
        }
        return candies;
    }

    /** Recursively copies an element (attributes, text, children) into the builder. */
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
            if (children.item(i) instanceof Element child) {
                fill(builder, child);
            }
        }
    }
}
