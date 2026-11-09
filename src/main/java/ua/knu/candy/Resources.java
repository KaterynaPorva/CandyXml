package ua.knu.candy;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

/** Locations of the bundled XML, XSD and XSL files, and a helper to open them from the classpath. */
public final class Resources {

    public static final String XML = "xml/candies.xml";
    public static final String XSD = "xsd/candies.xsd";
    public static final String XSL = "xsl/group-by-kind.xsl";

    private Resources() {
    }

    /**
     * @throws IllegalArgumentException when the resource does not exist on the classpath
     */
    public static InputStream open(String path) {
        InputStream in = Resources.class.getClassLoader().getResourceAsStream(path);
        if (in == null) {
            throw new IllegalArgumentException("Classpath resource not found: " + path);
        }
        return in;
    }

    /** Opens a resource as a JAXP {@link Source}. */
    public static Source source(String path) {
        return new StreamSource(open(path));
    }
}
