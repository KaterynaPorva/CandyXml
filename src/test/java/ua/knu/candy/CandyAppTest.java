package ua.knu.candy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ua.knu.candy.model.Candy;
import ua.knu.candy.model.CandyComparator;
import ua.knu.candy.parser.ParserType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Тест усього сценарію CandyApp.run() від початку до кінця (валідація -> парсинг -> сортування -> XSL)
class CandyAppTest {

    @ParameterizedTest
    @EnumSource(ParserType.class)
    void pipelineRunsEndToEndWithEveryParser(ParserType type, @TempDir Path out) throws Exception {
        List<Candy> result = new CandyApp().run(type, CandyComparator.BY_ENERGY, out);

        // 6 цукерок, відсортовані за калорійністю
        assertEquals(TestData.CANDY_COUNT, result.size());
        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i - 1).energy() <= result.get(i).energy(), "not sorted by energy");
        }

        // файл з результатом XSL створений і містить три групи
        Path grouped = out.resolve(CandyApp.GROUPED_FILE_NAME);
        assertTrue(Files.exists(grouped));
        String xml = Files.readString(grouped);
        assertTrue(xml.contains("<caramel"));
        assertTrue(xml.contains("<chocolate"));
        assertTrue(xml.contains("<toffee"));
    }

    @Test
    void outputDirectoryIsCreatedWhenMissing(@TempDir Path tmp) throws Exception {
        Path nested = tmp.resolve("a").resolve("b");
        new CandyApp().run(ParserType.DOM, CandyComparator.BY_NAME, nested);
        assertTrue(Files.isRegularFile(nested.resolve(CandyApp.GROUPED_FILE_NAME)));
    }

    @Test
    void resourcesHelperFailsFastOnMissingResource() {
        assertThrows(IllegalArgumentException.class, () -> Resources.open("xml/does-not-exist.xml"));
    }
}
