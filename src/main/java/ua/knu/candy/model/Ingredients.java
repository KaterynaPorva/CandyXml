package ua.knu.candy.model;

import java.util.Optional;

/**
 * {@code <Ingredients>} element. All amounts are in milligrams per 100 g.
 *
 * @param chocolateType present only for chocolate candies, otherwise {@code null}
 */
public record Ingredients(int water, int sugar, int fructose, ChocolateKind chocolateType, int vanillin) {

    public Ingredients {
        requireNonNegative(water, "water");
        requireNonNegative(sugar, "sugar");
        requireNonNegative(fructose, "fructose");
        requireNonNegative(vanillin, "vanillin");
    }

    /** @return the chocolate kind, empty for non-chocolate candies. */
    public Optional<ChocolateKind> chocolate() {
        return Optional.ofNullable(chocolateType);
    }

    private static void requireNonNegative(int value, String field) {
        if (value < 0) {
            throw new IllegalArgumentException(field + " must be >= 0, got " + value);
        }
    }
}
