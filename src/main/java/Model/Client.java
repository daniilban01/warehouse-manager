package Model;

/**
 * Model class representing a Client entity in the system.
 */
public class Client {
    private int id;
    private String name;
    private String address;
    private String email;
    private int age;

    /**
     * Default constructor required for reflection and object instantiation.
     */
    public Client(){}

    /**
     * Constructs a new Client without an ID (useful for insert operations).
     *
     * @param name    the name of the client
     * @param address the address of the client
     * @param email   the email address of the client
     * @param age     the age of the client
     */
    public Client( String name, String address, String email, int age) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.age = age;
    }

    /**
     * Constructs a new Client with all fields initialized.
     *
     * @param id      the unique identifier of the client
     * @param name    the name of the client
     * @param address the address of the client
     * @param email   the email address of the client
     * @param age     the age of the client
     */
    public Client(int id, String name, String address, String email, int age) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
        this.age = age;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student [id=" + id + ", name=" + name + ", address=" + address + ", email=" + email + ", age=" + age + "]";
    }
}