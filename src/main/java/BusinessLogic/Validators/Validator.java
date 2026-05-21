package BusinessLogic.Validators;

/**
 * Generic interface for implementing validation logic on various model objects.
 * * @param T the type of object to be validated
 */
public interface Validator<T> {

    /**
     * Validates the given object according to specific business rules.
     *
     * @param t the object to validate
     * @throws IllegalArgumentException if the object does not meet the validation criteria
     */
    public void validate(T t);
}