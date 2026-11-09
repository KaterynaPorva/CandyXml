package ua.knu.candy.model;

import java.util.Objects;

/**
 * One {@code <Candy>} element of the document. Immutable; build it with {@link CandyBuilder}.
 *
 * @param id         unique identifier, the {@code id} attribute (XSD type ID)
 * @param name       candy name
 * @param energy     energy value in kcal per 100 g
 * @param type       kind of candy and filling flag
 * @param ingredients ingredient amounts
 * @param value      nutrition value
 * @param production manufacturer
 */
public record Candy(
        String id,
        String name,
        int energy,
        CandyType type,
        Ingredients ingredients,
        NutritionValue value,
        String production) {

    public Candy {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(ingredients, "ingredients");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(production, "production");
        if (energy < 0) {
            throw new IllegalArgumentException("energy must be >= 0, got " + energy);
        }
    }
}
