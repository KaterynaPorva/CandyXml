package ua.knu.candy.model;

import java.math.BigDecimal;
import java.util.Objects;

// Елемент <Value>: харчова цінність у грамах на 100 г.
// BigDecimal, а не double, щоб 5.20 зберігалося точно, як у XML.
public record NutritionValue(BigDecimal proteins, BigDecimal fats, BigDecimal carbohydrates) {

    public NutritionValue {
        Objects.requireNonNull(proteins, "proteins");
        Objects.requireNonNull(fats, "fats");
        Objects.requireNonNull(carbohydrates, "carbohydrates");
    }
}
