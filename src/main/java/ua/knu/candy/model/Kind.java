package ua.knu.candy.model;

// Тип цукерки: карамель, ірис, шоколадна.
// Відповідає переліку KindType у XSD (атрибут kind елемента Type).
public enum Kind implements XmlValued {
    CARAMEL("caramel"),
    TOFFEE("toffee"),
    CHOCOLATE("chocolate");

    private final String xmlValue;

    Kind(String xmlValue) {
        this.xmlValue = xmlValue;
    }

    @Override
    public String xmlValue() {
        return xmlValue;
    }
}
