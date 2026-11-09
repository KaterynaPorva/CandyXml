package ua.knu.candy.transform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

// XSL-перетворення (пункт 5 завдання).
// Сам алгоритм групування описаний у файлі group-by-kind.xsl, а цей клас лише його запускає:
//   1. TransformerFactory компілює .xsl у Templates (один раз, у конструкторі).
//   2. transform(xml, out) застосовує його до документа і пише результат.
public final class CandyXslTransformer {

    private static final Logger log = LoggerFactory.getLogger(CandyXslTransformer.class);

    private final Templates templates;

    public CandyXslTransformer(Source xsl) throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        this.templates = factory.newTemplates(xsl);
        log.debug("XSL stylesheet compiled");
    }

    // Результат може бути файл, рядок, потік - будь-який Result
    public void transform(Source xml, Result out) throws TransformerException {
        Transformer transformer = templates.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(xml, out);
        log.info("XSL transformation finished");
    }

    // Зручний варіант для тестів: результат як рядок
    public String transformToString(Source xml) throws TransformerException {
        StringWriter writer = new StringWriter();
        transform(xml, new StreamResult(writer));
        return writer.toString();
    }
}
