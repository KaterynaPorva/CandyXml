package ua.knu.candy.model;

/**
 * An enum constant that has a textual representation in the XML document.
 * Shared lookup logic lives here so that every enum does not repeat it.
 */
public interface XmlValued {

    /** @return the value exactly as it appears in XML (e.g. {@code "chocolate"}). */
    String xmlValue();

    /**
     * Finds the enum constant whose {@link #xmlValue()} equals {@code value}.
     *
     * @throws IllegalArgumentException when no constant matches
     */
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
