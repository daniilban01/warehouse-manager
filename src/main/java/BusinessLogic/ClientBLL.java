package BusinessLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import BusinessLogic.Validators.EmailValidator;
import BusinessLogic.Validators.ClientAgeValidator;
import BusinessLogic.Validators.Validator;
import DataAccess.ClientDAO;
import Model.Client;

/**
 * Business Logic Layer class for managing Client operations, including validation and database access.
 */
public class ClientBLL {

    private List<Validator<Client>> validators;
    private ClientDAO clientDAO;

    /**
     * Initializes the client business logic layer with its validators and data access object.
     */
    public ClientBLL() {
        validators = new ArrayList<Validator<Client>>();
        validators.add((Validator<Client>) new EmailValidator());
        validators.add((Validator<Client>) new ClientAgeValidator());

        clientDAO = new ClientDAO();
    }

    /**
     * Finds a client by their unique identifier.
     *
     * @param id the identifier of the client
     * @return the found client object
     * @throws NoSuchElementException if no client with the specified id exists
     */
    public Client findClientById(int id) {
        Client client = clientDAO.findById(id);
        if (client == null) {
            throw new NoSuchElementException("The client with id =" + id + " was not found!");
        }
        return client;
    }

    /**
     * Retrieves all clients from the database.
     *
     * @return a list of all clients
     */
    public List<Client> findAllClients() {
        return clientDAO.findAll();
    }

    /**
     * Validates and inserts a new client into the database.
     *
     * @param client the client object to be inserted
     */
    public void insertClient(Client client) {
        for (Validator<Client> v : validators) {
            v.validate(client);
        }
        clientDAO.insert(client);
    }

    /**
     * Validates and updates an existing client's details.
     *
     * @param client the client object to be updated
     */
    public void updateClient(Client client) {
        for (Validator<Client> v : validators) {
            v.validate(client);
        }
        clientDAO.update(client);
    }

    /**
     * Deletes a client by their unique identifier if they exist.
     *
     * @param id the identifier of the client to delete
     * @throws NoSuchElementException if the client with the specified id does not exist
     */
    public void deleteClient(int id) throws Exception {
        Client client = clientDAO.findById(id);
        if (client == null) {
            throw new NoSuchElementException("The client with id =" + id + " was not found and cannot be deleted!");
        }
        clientDAO.delete(id);
    }
}