package ua.knu.candy;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

// Шляхи до XML, XSD, XSL усередині jar (тека src/main/resources) і допоміжні методи, щоб їх відкрити.
public final class Resources {

    public static final String XML = "xml/candies.xml";
    public static final String XSD = "xsd/candies.xsd";
    public static final String XSL = "xsl/group-by-kind.xsl";

    private Resources() {
    }

    // Відкриває ресурс як потік. Якщо файлу немає - одразу зрозуміла помилка, а не NullPointerException десь далі.
    public static InputStream open(String path) {
        InputStream in = Resources.class.getClassLoader().getResourceAsStream(path);
        if (in == null) {
            throw new IllegalArgumentException("Classpath resource not found: " + path);
        }
        return in;
    }

    // Те саме, але у вигляді Source - так приймають дані валідатор і трансформер
    public static Source source(String path) {
        return new StreamSource(open(path));
    }
}
