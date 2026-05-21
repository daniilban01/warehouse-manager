package BusinessLogic.Validators;

import Model.Client;

/**
 * Validator for checking if a client's age is within legal boundaries.
 */
public class ClientAgeValidator implements Validator<Client> {

    /** Minimum allowed age. */
    private static final int MIN_AGE = 18;

    /** Maximum allowed age. */
    private static final int MAX_AGE = 100;

    /**
     * Validates that the client's age is between 18 and 100.
     *
     * @param t the client to validate
     * @throws IllegalArgumentException if the age limit is breached
     */
    @Override
    public void validate(Client t) {
        if (t.getAge() < MIN_AGE || t.getAge() > MAX_AGE) {
            throw new IllegalArgumentException("The Student Age limit is not respected!");
        }
    }
}