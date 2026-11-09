package ua.knu.candy.model;

import java.math.BigDecimal;
import java.util.Objects;

/** {@code <Value>} element: nutrition value in grams per 100 g. */
public record NutritionValue(BigDecimal proteins, BigDecimal fats, BigDecimal carbohydrates) {

    public NutritionValue {
        Objects.requireNonNull(proteins, "proteins");
        Objects.requireNonNull(fats, "fats");
        Objects.requireNonNull(carbohydrates, "carbohydrates");
    }
}
