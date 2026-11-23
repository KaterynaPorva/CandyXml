package ua.knu.candy.parser;

import ua.knu.candy.model.Candy;
import ua.knu.candy.model.CandyBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

// Спільний обробник для всіх трьох парсерів (SAX, StAX і DOM).
// Кожен парсер повідомляє йому три види подій: "елемент відкрився", "текст", "елемент закрився".
// Різниця лише в тому, ЗВІДКИ події: у SAX їх надсилає сам парсер, у StAX ми беремо їх
// у циклі, у DOM генеруємо самі під час обходу дерева. А ЩО робити з подією - однаково,
// тому написано тут один раз.
//
// Як працює:
//   <Candy id="c1">  -> створюємо новий CandyBuilder, записуємо id
//   <Name>Lacosta    -> накопичуємо текст у StringBuilder
//   </Name>          -> текст готовий, кладемо його в builder через CandyFieldMapper
//   </Candy>         -> builder.build(), додаємо цукерку в список
final class CandyEventHandler {

    private final List<Candy> candies = new ArrayList<>();
    private final StringBuilder text = new StringBuilder();
    private CandyBuilder current;   // цукерка, яку зараз читаємо (null між цукерками)

    void startElement(String localName, Function<String, String> attribute) {
        XmlTag tag = XmlTag.fromName(localName);
        text.setLength(0);  // очищаємо накопичений текст
        if (tag == XmlTag.CANDY) {
            current = new CandyBuilder();
        }
        if (current != null) {
            CandyFieldMapper.applyAttributes(current, tag, attribute);
        }
    }

    // Текст може прийти частинами, тому саме накопичуємо, а не присвоюємо
    void characters(char[] chars, int start, int length) {
        text.append(chars, start, length);
    }

    void characters(String chars) {
        text.append(chars);
    }

    void endElement(String localName) {
        XmlTag tag = XmlTag.fromName(localName);
        if (tag == XmlTag.CANDY) {
            candies.add(current.build());
            current = null;
        } else if (tag.isLeaf() && current != null) {
            CandyFieldMapper.applyText(current, tag, text.toString().trim());
        }
        text.setLength(0);
    }

    List<Candy> result() {
        return Collections.unmodifiableList(candies);
    }
}
