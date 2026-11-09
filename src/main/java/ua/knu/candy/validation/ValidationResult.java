package ua.knu.candy.validation;

import java.util.List;

// Результат валідації: список повідомлень про помилки.
// Порожній список = документ валідний.
public record ValidationResult(List<String> errors) {

    public ValidationResult {
        errors = List.copyOf(errors);   // незмінна копія, щоб ззовні список не змінили
    }

    public boolean isValid() {
        return errors.isEmpty();
    }
}
