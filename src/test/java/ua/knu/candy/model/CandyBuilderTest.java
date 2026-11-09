package ua.knu.candy.model;

import org.junit.jupiter.api.Test;
import ua.knu.candy.TestData;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CandyBuilderTest {

    private static CandyBuilder completeBuilder() {
        return new CandyBuilder()
                .id("c1")
                .name("Lacosta")
                .energy(520)
                .kind(Kind.CHOCOLATE)
                .filled(true)
                .water(1500)
                .sugar(42000)
                .fructose(3000)
                .chocolateType(ChocolateKind.DARK)
                .vanillin(120)
                .proteins(new BigDecimal("5.20"))
                .fats(new BigDecimal("28.10"))
                .carbohydrates(new BigDecimal("60.50"))
                .production("Roshen");
    }

    @Test
    void buildsCandyEqualToHandWrittenRecord() {
        assertEquals(TestData.LACOSTA, completeBuilder().build());
    }

    @Test
    void chocolateTypeIsOptional() {
        Candy candy = completeBuilder().chocolateType(null).build();
        assertTrue(candy.ingredients().chocolate().isEmpty());
    }

    @Test
    void filledDefaultsToFalse() {
        Candy candy = new CandyBuilder()
                .id("c9").name("x").energy(1).kind(Kind.CARAMEL)
                .water(0).sugar(0).fructose(0).vanillin(0)
                .proteins(BigDecimal.ZERO).fats(BigDecimal.ZERO).carbohydrates(BigDecimal.ZERO)
                .production("p")
                .build();
        assertEquals(new CandyType(Kind.CARAMEL, false), candy.type());
    }

    @Test
    void missingRequiredFieldIsReported() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> completeBuilder().production(null).build());
        assertTrue(e.getMessage().contains("Production"));
    }

    @Test
    void missingKindIsReported() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> completeBuilder().kind(null).build());
        assertTrue(e.getMessage().contains("kind"));
    }

    @Test
    void negativeAmountsAreRejectedByModel() {
        assertThrows(IllegalArgumentException.class, () -> completeBuilder().sugar(-1).build());
        assertThrows(IllegalArgumentException.class, () -> completeBuilder().energy(-5).build());
    }
}
