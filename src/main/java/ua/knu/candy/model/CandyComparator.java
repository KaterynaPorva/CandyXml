package ua.knu.candy.model;

import java.util.Comparator;
import java.util.Locale;

// Варіанти сортування цукерок (пункт завдання про інтерфейс Comparator).
// Зроблено як enum, який сам реалізує Comparator<Candy>:
//   - кожна константа - окремий готовий компаратор,
//   - за назвою константи його можна вибрати з командного рядка (fromString).
public enum CandyComparator implements Comparator<Candy> {

    // За назвою, без урахування регістру
    BY_NAME(Comparator.comparing(Candy::name, String.CASE_INSENSITIVE_ORDER)),

    // За калорійністю, від меншої до більшої
    BY_ENERGY(Comparator.comparingInt(Candy::energy)),

    // За виробником, а всередині одного виробника - за назвою
    BY_PRODUCTION(Comparator.comparing(Candy::production).thenComparing(BY_NAME)),

    // За кількістю цукру, від більшої до меншої
    BY_SUGAR_DESC(Comparator.comparingInt((Candy c) -> c.ingredients().sugar()).reversed());

    // Справжній компаратор, якому делегуємо порівняння
    private final Comparator<Candy> delegate;

    CandyComparator(Comparator<Candy> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int compare(Candy left, Candy right) {
        return delegate.compare(left, right);
    }

    // "name" -> BY_NAME, "energy" -> BY_ENERGY, "by_sugar_desc" -> BY_SUGAR_DESC
    public static CandyComparator fromString(String value) {
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (!normalized.startsWith("BY_")) {
            normalized = "BY_" + normalized;
        }
        try {
            return valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown sort order: '" + value + "'", e);
        }
    }
}
