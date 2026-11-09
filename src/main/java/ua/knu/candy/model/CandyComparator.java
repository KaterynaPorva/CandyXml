package ua.knu.candy.model;

import java.util.Comparator;
import java.util.Locale;

/**
 * Ready-made {@link Comparator}s for sorting candies.
 * Implemented as an enum so that a sort order can be selected by name from the command line.
 */
public enum CandyComparator implements Comparator<Candy> {

    /** Alphabetically by name, case-insensitive. */
    BY_NAME(Comparator.comparing(Candy::name, String.CASE_INSENSITIVE_ORDER)),

    /** By energy value, lowest first. */
    BY_ENERGY(Comparator.comparingInt(Candy::energy)),

    /** By manufacturer, then by name. */
    BY_PRODUCTION(Comparator.comparing(Candy::production).thenComparing(BY_NAME)),

    /** By sugar amount, highest first. */
    BY_SUGAR_DESC(Comparator.comparingInt((Candy c) -> c.ingredients().sugar()).reversed());

    private final Comparator<Candy> delegate;

    CandyComparator(Comparator<Candy> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int compare(Candy left, Candy right) {
        return delegate.compare(left, right);
    }

    /**
     * Parses a command-line value such as {@code name}, {@code energy}, {@code by_energy}.
     *
     * @throws IllegalArgumentException for an unknown value
     */
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
