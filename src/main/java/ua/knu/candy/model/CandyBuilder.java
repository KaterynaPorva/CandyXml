package ua.knu.candy.model;

import java.math.BigDecimal;

// Будівельник (патерн Builder) для Candy.
// Навіщо: Candy незмінний і створюється одразу з усіма полями, а парсер читає XML
// поступово - спочатку <Name>, потім <Energy> і т.д. Тому парсер складає поля сюди,
// а коли дійшов до </Candy>, викликає build() і отримує готовий об'єкт.
public final class CandyBuilder {

    private String id;
    private String name;
    private Integer energy;
    private Kind kind;
    private boolean filled;
    private Integer water;
    private Integer sugar;
    private Integer fructose;
    private ChocolateKind chocolateType;
    private Integer vanillin;
    private BigDecimal proteins;
    private BigDecimal fats;
    private BigDecimal carbohydrates;
    private String production;

    // Кожен сеттер повертає this, щоб можна було писати ланцюжком: builder.id("c1").name("...")

    public CandyBuilder id(String id) {
        this.id = id;
        return this;
    }

    public CandyBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CandyBuilder energy(int energy) {
        this.energy = energy;
        return this;
    }

    public CandyBuilder kind(Kind kind) {
        this.kind = kind;
        return this;
    }

    public CandyBuilder filled(boolean filled) {
        this.filled = filled;
        return this;
    }

    public CandyBuilder water(int water) {
        this.water = water;
        return this;
    }

    public CandyBuilder sugar(int sugar) {
        this.sugar = sugar;
        return this;
    }

    public CandyBuilder fructose(int fructose) {
        this.fructose = fructose;
        return this;
    }

    public CandyBuilder chocolateType(ChocolateKind chocolateType) {
        this.chocolateType = chocolateType;
        return this;
    }

    public CandyBuilder vanillin(int vanillin) {
        this.vanillin = vanillin;
        return this;
    }

    public CandyBuilder proteins(BigDecimal proteins) {
        this.proteins = proteins;
        return this;
    }

    public CandyBuilder fats(BigDecimal fats) {
        this.fats = fats;
        return this;
    }

    public CandyBuilder carbohydrates(BigDecimal carbohydrates) {
        this.carbohydrates = carbohydrates;
        return this;
    }

    public CandyBuilder production(String production) {
        this.production = production;
        return this;
    }

    // Збирає Candy. Якщо якогось обов'язкового поля в XML не було -
    // кидає IllegalStateException з назвою поля (chocolateType необов'язковий).
    public Candy build() {
        return new Candy(
                required(id, "id"),
                required(name, "Name"),
                required(energy, "Energy"),
                new CandyType(required(kind, "Type/@kind"), filled),
                new Ingredients(
                        required(water, "Water"),
                        required(sugar, "Sugar"),
                        required(fructose, "Fructose"),
                        chocolateType,
                        required(vanillin, "Vanillin")),
                new NutritionValue(
                        required(proteins, "Proteins"),
                        required(fats, "Fats"),
                        required(carbohydrates, "Carbohydrates")),
                required(production, "Production"));
    }

    private static <T> T required(T value, String field) {
        if (value == null) {
            throw new IllegalStateException("Candy is missing required field: " + field);
        }
        return value;
    }
}
