package dtos;

import java.time.LocalDateTime;

public class SuperDto {

    private Integer id;
    private String login;
    private String password;
    private LocalDateTime lastLogIn;
    
    public SuperDto(Integer id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public LocalDateTime getLastLogIn() {
        return lastLogIn;
    }
    public void setLastLogIn(LocalDateTime lastLogIn) {
        this.lastLogIn = lastLogIn;
    }    
}
