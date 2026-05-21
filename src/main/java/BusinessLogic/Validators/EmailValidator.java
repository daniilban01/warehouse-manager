package BusinessLogic.Validators;

import Model.Client;

/**
 * Validator for checking if a client's email format is valid.
 */
public class EmailValidator implements Validator<Client> {

    /**
     * Validates that the client's email contains the '@' character between two text segments.
     *
     * @param t the client to validate
     * @throws IllegalArgumentException if the email format is invalid
     */
    @Override
    public void validate(Client t) {
        if (!t.getEmail().matches("^(.+)@(.+)$")) {
            throw new IllegalArgumentException("Invalid Email!");
        }
    }
}