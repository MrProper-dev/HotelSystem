package utils;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;

public class AssertionChecker<T> {
    
    private T actualValue;
    private String description;
    private List<Throwable> errors;
    private boolean throwOnError;
    
    private AssertionChecker(T actualValue, String description) {
        this.actualValue = actualValue;
        this.description = description;
        this.errors = new ArrayList<>();
        this.throwOnError = false;
    }
    
    public static <T> AssertionChecker<T> assertThat(T actual, String description) {
        return new AssertionChecker<>(actual, description);
    }
    
    public static <T> AssertionChecker<T> assertThat(T actual) {
        return new AssertionChecker<>(actual, "Проверяемое значение");
    }
    
    public AssertionChecker<T> isEqualTo(T expected) {
        if (actualValue == null && expected != null) {
            addError("Ожидалось: " + expected + ", но получено null");
        } else if (actualValue != null && !actualValue.equals(expected)) {
            addError("Ожидалось: " + expected + ", но получено: " + actualValue);
        } else if (actualValue == null && expected == null) {

        }
        return this;
    }
    
    public AssertionChecker<T> isNotEqualTo(T unexpected) {
        if (actualValue == null && unexpected == null) {
            addError("Значение не должно быть null");
        } else if (actualValue != null && actualValue.equals(unexpected)) {
            addError("Значение " + actualValue + " не должно равняться " + unexpected);
        }
        return this;
    }
    
    public AssertionChecker<T> isNull() {
        if (actualValue != null) {
            addError("Ожидалось null, но получено: " + actualValue);
        }
        return this;
    }
    
    public AssertionChecker<T> isNotNull() {
        if (actualValue == null) {
            addError("Ожидалось не-null значение, но получено null");
        }
        return this;
    }
    
    public AssertionChecker<T> isTrue() {
        if (!Boolean.TRUE.equals(actualValue)) {
            addError("Ожидалось true, но получено: " + actualValue);
        }
        return this;
    }
    
    public AssertionChecker<T> isFalse() {
        if (!Boolean.FALSE.equals(actualValue)) {
            addError("Ожидалось false, но получено: " + actualValue);
        }
        return this;
    }
    
    public AssertionChecker<T> satisfies(Predicate<T> condition, String conditionDescription) {
        if (!condition.test(actualValue)) {
            addError("Значение " + actualValue + " не удовлетворяет условию: " + conditionDescription);
        }
        return this;
    }

    public AssertionChecker<T> isGreaterThan(int expected) {
        if (actualValue instanceof Number) {
            double actual = ((Number) actualValue).doubleValue();
            if (actual <= expected) {
                addError("Ожидалось значение > " + expected + ", но получено: " + actual);
            }
        } else {
            addError("Значение не является числом: " + actualValue);
        }
        return this;
    }

    public AssertionChecker<T> isGreaterThanOrEqualTo(int expected) {
        if (actualValue instanceof Number) {
            double actual = ((Number) actualValue).doubleValue();
            if (actual < expected) {
                addError("Ожидалось значение >= " + expected + ", но получено: " + actual);
            }
        } else {
            addError("Значение не является числом: " + actualValue);
        }
        return this;
    }

    public AssertionChecker<T> isLessThan(int expected) {
        if (actualValue instanceof Number) {
            double actual = ((Number) actualValue).doubleValue();
            if (actual >= expected) {
                addError("Ожидалось значение < " + expected + ", но получено: " + actual);
            }
        } else {
            addError("Значение не является числом: " + actualValue);
        }
        return this;
    }
    
    public AssertionChecker<T> isInstanceOf(Class<?> expectedClass) {
        if (actualValue == null) {
            addError("Ожидался экземпляр " + expectedClass.getSimpleName() + ", но получено null");
        } else if (!expectedClass.isInstance(actualValue)) {
            addError("Ожидался экземпляр " + expectedClass.getSimpleName() + 
                    ", но получен " + actualValue.getClass().getSimpleName());
        }
        return this;
    }
    
    public AssertionChecker<String> asString() {
        if (!(actualValue instanceof String)) {
            throw new AssertionError("Значение не является строкой: " + actualValue);
        }
        return new AssertionChecker<>((String) actualValue, description);
    }
    
    public AssertionChecker<T> throwingOnError() {
        this.throwOnError = true;
        return this;
    }
    
    public void verify() {
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Проверка '").append(description).append("' не пройдена:\n");
            for (int i = 0; i < errors.size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(errors.get(i).getMessage()).append("\n");
            }
            throw new AssertionError(sb.toString());
        }
    }
    
    public int getErrorCount() {
        return errors.size();
    }
    
    public List<Throwable> getErrors() {
        return new ArrayList<>(errors);
    }
    
    private void addError(String message) {
        AssertionError error = new AssertionError(message);
        errors.add(error);
        if (throwOnError) {
            throw error;
        }
    }
    
    public static class ExceptionChecker {
        
        public static <T extends Throwable> T assertThrows(Class<T> expectedType, Runnable code) {
            try {
                code.run();
                throw new AssertionError("Ожидалось исключение " + expectedType.getSimpleName() + ", но код выполнился успешно");
            } catch (Throwable actual) {
                if (expectedType.isInstance(actual)) {
                    return expectedType.cast(actual);
                }
                throw new AssertionError("Ожидалось исключение " + expectedType.getSimpleName() + 
                        ", но получено: " + actual.getClass().getSimpleName(), actual);
            }
        }
        
        public static void assertDoesNotThrow(Runnable code) {
            try {
                code.run();
            } catch (Throwable t) {
                throw new AssertionError("Не ожидалось исключение, но получено: " + t.getClass().getSimpleName(), t);
            }
        }
        
        public static void assertThrowsWithMessage(Class<? extends Throwable> expectedType, 
                                                   String expectedMessage, 
                                                   Runnable code) {
            Throwable thrown = assertThrows(expectedType, code);
            if (!expectedMessage.equals(thrown.getMessage())) {
                throw new AssertionError("Ожидалось сообщение '" + expectedMessage + 
                        "', но получено '" + thrown.getMessage() + "'");
            }
        }
    }
}