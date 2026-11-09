package ua.knu.candy.model;

import java.util.Objects;

// Елемент <Type kind="..." filled="..."/>.
// kind - який це тип цукерки, filled - чи є начинка (актуально для шоколадних).
// record = незмінний клас, Java сама генерує конструктор, геттери, equals, hashCode, toString.
public record CandyType(Kind kind, boolean filled) {

    // Компактний конструктор: перевірка, що kind не null
    public CandyType {
        Objects.requireNonNull(kind, "kind");
    }
}
