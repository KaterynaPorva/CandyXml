package ua.knu.candy.parser;

import ua.knu.candy.model.Candy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Інтерфейс парсера: "дай потік з XML - отримай список цукерок".
// Три реалізації: SaxCandyParser, DomCandyParser, StaxCandyParser.
// Завдяки інтерфейсу решта програми не знає, який саме парсер працює.
public interface CandyParser {

    ParserType type();

    // Основний метод. Потік не закриває - це справа того, хто його відкрив.
    List<Candy> parse(InputStream in) throws CandyParseException;

    // Зручний варіант для файлу: відкриває потік, парсить, закриває
    default List<Candy> parse(Path file) throws CandyParseException {
        try (InputStream in = Files.newInputStream(file)) {
            return parse(in);
        } catch (IOException e) {
            throw new CandyParseException("Cannot read " + file, e);
        }
    }
}
