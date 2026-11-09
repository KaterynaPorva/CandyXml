package ua.knu.candy.model;

import java.util.Objects;

/**
 * {@code <Type kind="..." filled="..."/>} element: the kind of candy and whether it has a filling.
 *
 * @param kind   caramel / toffee / chocolate
 * @param filled {@code true} when the candy has a filling (meaningful for chocolate)
 */
public record CandyType(Kind kind, boolean filled) {

    public CandyType {
        Objects.requireNonNull(kind, "kind");
    }
}
