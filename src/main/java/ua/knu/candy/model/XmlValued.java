package ua.knu.candy.model;

// Спільний інтерфейс для переліків (enum), які записуються в XML текстом.
// Наприклад, Kind.CHOCOLATE в XML виглядає як "chocolate".
// Метод fromXml один для всіх переліків, щоб не писати однаковий пошук у кожному enum.
public interface XmlValued {

    // Як значення записано в XML
    String xmlValue();

    // Шукає константу переліку за її XML-значенням.
    // Якщо такого значення немає - кидає виняток з підказкою, що саме не знайшли.
    static <E extends Enum<E> & XmlValued> E fromXml(Class<E> type, String value) {
        for (E constant : type.getEnumConstants()) {
            if (constant.xmlValue().equals(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException(
                "Unknown " + type.getSimpleName() + " value: '" + value + "'");
    }
}
