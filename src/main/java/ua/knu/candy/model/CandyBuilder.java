package ua.knu.candy.model;

import java.math.BigDecimal;

/**
 * Mutable builder for {@link Candy}. XML parsers fill it field by field as they
 * encounter elements, then call {@link #build()} once the closing {@code </Candy>} is reached.
 */
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

    /**
     * @return the assembled candy
     * @throws IllegalStateException if a required field was never set
     */
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
