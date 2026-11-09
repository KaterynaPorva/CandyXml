package ua.knu.candy.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlTagTest {

    @ParameterizedTest
    @EnumSource(XmlTag.class)
    void everyTagIsFoundByItsXmlName(XmlTag tag) {
        assertSame(tag, XmlTag.fromName(tag.xmlName()));
    }

    @Test
    void containerElementsAreNotLeaves() {
        assertFalse(XmlTag.CANDIES.isLeaf());
        assertFalse(XmlTag.CANDY.isLeaf());
        assertFalse(XmlTag.TYPE.isLeaf());
        assertFalse(XmlTag.INGREDIENTS.isLeaf());
        assertFalse(XmlTag.VALUE.isLeaf());
    }

    @Test
    void textElementsAreLeaves() {
        assertTrue(XmlTag.NAME.isLeaf());
        assertTrue(XmlTag.ENERGY.isLeaf());
        assertTrue(XmlTag.CARBOHYDRATES.isLeaf());
        assertTrue(XmlTag.PRODUCTION.isLeaf());
    }

    @Test
    void unknownElementIsRejected() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> XmlTag.fromName("Color"));
        assertTrue(e.getMessage().contains("Color"));
    }

    @Test
    void namespaceMatchesSchema() {
        assertEquals("http://knu.ua/candy", XmlTag.NAMESPACE);
    }
}
