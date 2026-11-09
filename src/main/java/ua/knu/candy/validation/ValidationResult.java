package ua.knu.candy.validation;

import java.util.List;

/**
 * Outcome of validating an XML document against the XSD.
 *
 * @param errors human-readable messages, empty when the document is valid
 */
public record ValidationResult(List<String> errors) {

    public ValidationResult {
        errors = List.copyOf(errors);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }
}
