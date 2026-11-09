package ua.knu.candy;

import ua.knu.candy.model.Candy;
import ua.knu.candy.model.CandyType;
import ua.knu.candy.model.ChocolateKind;
import ua.knu.candy.model.Ingredients;
import ua.knu.candy.model.Kind;
import ua.knu.candy.model.NutritionValue;

import java.math.BigDecimal;
import java.util.List;

// Спільні дані для всіх тестів: шляхи до "зламаних" XML-файлів і очікувані об'єкти,
// зібрані вручну з тих самих значень, що в candies.xml.
public final class TestData {

    // Тестові документи з src/test/resources/xml
    public static final String INVALID_ENERGY = "xml/invalid-energy.xml";           // Energy = 950, більше за максимум 900
    public static final String INVALID_DUPLICATE_ID = "xml/invalid-duplicate-id.xml"; // два елементи з id="c1"
    public static final String INVALID_MISSING_KIND = "xml/invalid-missing-kind.xml"; // <Type> без атрибута kind
    public static final String UNKNOWN_ELEMENT = "xml/unknown-element.xml";         // зайвий елемент <Color>
    public static final String MISSING_FIELD = "xml/missing-field.xml";             // немає <Production>
    public static final String MALFORMED = "xml/malformed.xml";                     // незакритий тег

    // Скільки цукерок у candies.xml
    public static final int CANDY_COUNT = 6;

    // Перша цукерка з candies.xml - шоколадна з начинкою, має ChocolateType
    public static final Candy LACOSTA = new Candy(
            "c1",
            "Lacosta",
            520,
            new CandyType(Kind.CHOCOLATE, true),
            new Ingredients(1500, 42000, 3000, ChocolateKind.DARK, 120),
            new NutritionValue(new BigDecimal("5.20"), new BigDecimal("28.10"), new BigDecimal("60.50")),
            "Roshen");

    // Друга цукерка - карамель: без ChocolateType і без атрибута filled (має стати false)
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

    // Маленька цукерка для тестів сортування: задаємо лише те, що впливає на порядок
    public static Candy candy(String id, String name, int energy, int sugar, String production) {
        return new Candy(
                id, name, energy,
                new CandyType(Kind.TOFFEE, false),
                new Ingredients(0, sugar, 0, null, 0),
                new NutritionValue(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                production);
    }
}
