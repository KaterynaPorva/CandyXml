package ua.knu.candy.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ua.knu.candy.TestData.candy;

class CandyComparatorTest {

    private static final Candy ZEBRA = candy("c1", "zebra", 300, 500, "Konti");
    private static final Candy APPLE = candy("c2", "Apple", 500, 900, "Roshen");
    private static final Candy MANGO = candy("c3", "mango", 400, 700, "Konti");

    private static List<Candy> sorted(CandyComparator comparator) {
        List<Candy> list = new ArrayList<>(List.of(ZEBRA, APPLE, MANGO));
        list.sort(comparator);
        return list;
    }

    @Test
    void byNameIsCaseInsensitive() {
        assertEquals(List.of(APPLE, MANGO, ZEBRA), sorted(CandyComparator.BY_NAME));
    }

    @Test
    void byEnergyIsAscending() {
        assertEquals(List.of(ZEBRA, MANGO, APPLE), sorted(CandyComparator.BY_ENERGY));
    }

    @Test
    void byProductionThenByName() {
        assertEquals(List.of(MANGO, ZEBRA, APPLE), sorted(CandyComparator.BY_PRODUCTION));
    }

    @Test
    void bySugarIsDescending() {
        assertEquals(List.of(APPLE, MANGO, ZEBRA), sorted(CandyComparator.BY_SUGAR_DESC));
    }

    @ParameterizedTest
    @EnumSource(CandyComparator.class)
    void comparatorIsConsistentWithItself(CandyComparator comparator) {
        assertEquals(0, comparator.compare(APPLE, APPLE));
        assertEquals(-Integer.signum(comparator.compare(APPLE, ZEBRA)),
                Integer.signum(comparator.compare(ZEBRA, APPLE)));
    }

    @Test
    void fromStringAcceptsShortAndFullNamesInAnyCase() {
        assertEquals(CandyComparator.BY_NAME, CandyComparator.fromString("name"));
        assertEquals(CandyComparator.BY_ENERGY, CandyComparator.fromString(" Energy "));
        assertEquals(CandyComparator.BY_SUGAR_DESC, CandyComparator.fromString("sugar_desc"));
        assertEquals(CandyComparator.BY_PRODUCTION, CandyComparator.fromString("BY_PRODUCTION"));
    }

    @Test
    void fromStringRejectsUnknownOrder() {
        assertThrows(IllegalArgumentException.class, () -> CandyComparator.fromString("color"));
    }
}
