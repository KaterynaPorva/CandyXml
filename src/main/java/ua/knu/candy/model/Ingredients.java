package ua.knu.candy.model;

import java.util.Optional;

// Елемент <Ingredients>. Усі кількості в міліграмах на 100 г.
// chocolateType є лише у шоколадних цукерок, для інших він null.
public record Ingredients(int water, int sugar, int fructose, ChocolateKind chocolateType, int vanillin) {

    public Ingredients {
        requireNonNegative(water, "water");
        requireNonNegative(sugar, "sugar");
        requireNonNegative(fructose, "fructose");
        requireNonNegative(vanillin, "vanillin");
    }

    // Зручний доступ до виду шоколаду без перевірки на null
    public Optional<ChocolateKind> chocolate() {
        return Optional.ofNullable(chocolateType);
    }

    private static void requireNonNegative(int value, String field) {
        if (value < 0) {
            throw new IllegalArgumentException(field + " must be >= 0, got " + value);
        }
    }
}
