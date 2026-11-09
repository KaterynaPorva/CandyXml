package ua.knu.candy.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Тести переліків Kind і ChocolateKind та спільного пошуку XmlValued.fromXml
class XmlValuedTest {

    // @EnumSource запускає тест окремо для кожної константи переліку
    @ParameterizedTest
    @EnumSource(Kind.class)
    void kindRoundTripsThroughXmlValue(Kind kind) {
        // константа -> XML-значення -> та сама константа
        assertSame(kind, XmlValued.fromXml(Kind.class, kind.xmlValue()));
    }

    @ParameterizedTest
    @EnumSource(ChocolateKind.class)
    void chocolateKindRoundTripsThroughXmlValue(ChocolateKind kind) {
        assertSame(kind, XmlValued.fromXml(ChocolateKind.class, kind.xmlValue()));
    }

    @Test
    void xmlValuesAreLowercaseAsInSchema() {
        assertEquals("chocolate", Kind.CHOCOLATE.xmlValue());
        assertEquals("dark", ChocolateKind.DARK.xmlValue());
    }

    @Test
    void unknownValueIsRejectedWithHelpfulMessage() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> XmlValued.fromXml(Kind.class, "marmalade"));
        assertTrue(e.getMessage().contains("marmalade"));
        assertTrue(e.getMessage().contains("Kind"));
    }

    @Test
    void lookupIsCaseSensitiveLikeXsdEnumeration() {
        // у XSD перелік чутливий до регістру, тому "Caramel" - не те саме, що "caramel"
        assertThrows(IllegalArgumentException.class, () -> XmlValued.fromXml(Kind.class, "Caramel"));
    }
}
