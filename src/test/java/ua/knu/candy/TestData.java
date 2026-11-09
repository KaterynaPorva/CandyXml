package ua.knu.candy;

import ua.knu.candy.model.Candy;
import ua.knu.candy.model.CandyType;
import ua.knu.candy.model.ChocolateKind;
import ua.knu.candy.model.Ingredients;
import ua.knu.candy.model.Kind;
import ua.knu.candy.model.NutritionValue;

import java.math.BigDecimal;
import java.util.List;

/** Expected objects and test-resource paths shared by all test classes. */
public final class TestData {

    public static final String INVALID_ENERGY = "xml/invalid-energy.xml";
    public static final String INVALID_DUPLICATE_ID = "xml/invalid-duplicate-id.xml";
    public static final String INVALID_MISSING_KIND = "xml/invalid-missing-kind.xml";
    public static final String UNKNOWN_ELEMENT = "xml/unknown-element.xml";
    public static final String MISSING_FIELD = "xml/missing-field.xml";
    public static final String MALFORMED = "xml/malformed.xml";

    /** Number of candies in the bundled {@code candies.xml}. */
    public static final int CANDY_COUNT = 6;

    /** The first candy of {@code candies.xml}, built by hand. */
    public static final Candy LACOSTA = new Candy(
            "c1",
            "Lacosta",
            520,
            new CandyType(Kind.CHOCOLATE, true),
            new Ingredients(1500, 42000, 3000, ChocolateKind.DARK, 120),
            new NutritionValue(new BigDecimal("5.20"), new BigDecimal("28.10"), new BigDecimal("60.50")),
            "Roshen");

    /** The second candy of {@code candies.xml}: caramel, no chocolate type, no filled attribute. */
    public static final Candy BARBARIS = new Candy(
            "c2",
            "Barbaris",
            380,
            new CandyType(Kind.CARAMEL, false),
            new Ingredients(800, 95000, 1200, null, 0),
            new NutritionValue(new BigDecimal("0.00"), new BigDecimal("0.10"), new BigDecimal("96.00")),
            "Roshen");

    public static final List<String> ALL_IDS = List.of("c1", "c2", "c3", "c4", "c5", "c6");

    private TestData() {
    }

    /** A small candy for comparator tests. */
    public static Candy candy(String id, String name, int energy, int sugar, String production) {
        return new Candy(
                id, name, energy,
                new CandyType(Kind.TOFFEE, false),
                new Ingredients(0, sugar, 0, null, 0),
                new NutritionValue(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                production);
    }
}
