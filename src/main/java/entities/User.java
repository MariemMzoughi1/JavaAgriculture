package entities;

public class User {

    private int id;

    private String email;

    private String password;

    private String role;


    private String reset_token;

    public User(int id, String email, String password, String role, String reset_token) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.reset_token = reset_token;
    }

    public User(String email, String password, String role, String reset_token) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.reset_token = reset_token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getReset_token() {
        return reset_token;
    }

    public void setReset_token(String reset_token) {
        this.reset_token = reset_token;
    }
}
