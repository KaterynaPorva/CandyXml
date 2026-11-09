package ua.knu.candy.model;

import java.util.Objects;

// Головний клас моделі - одна цукерка, тобто один елемент <Candy> з XML.
// Поля повторюють структуру XML:
//   id          - атрибут id (у XSD це тип ID, має бути унікальним)
//   name        - <Name>
//   energy      - <Energy>, ккал
//   type        - <Type kind filled>
//   ingredients - <Ingredients>
//   value       - <Value>
//   production  - <Production>, виробник
// Об'єкт незмінний, тому збирається через CandyBuilder.
public record Candy(
        String id,
        String name,
        int energy,
        CandyType type,
        Ingredients ingredients,
        NutritionValue value,
        String production) {

    // Перевірки, щоб не можна було створити "поламану" цукерку
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
