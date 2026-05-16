package BusinessLogic.Validators;

import Model.Client;

public class EmailValidator implements Validator<Client> {
    @Override
    public void validate(Client t) {
        if (!t.getEmail().matches("^(.+)@(.+)$")) {
            throw new IllegalArgumentException("Invalid Email!");
        }
    }
}