package dtos;

import java.time.LocalDateTime;

public class AdminDto {

    private Integer id;
    private String login;
    private String password;
    private String fullName;
    private String phone;
    private LocalDateTime creationAt;
    private LocalDateTime last_log_in;

    public AdminDto(Integer id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public AdminDto(String login, String password, String fullName, String phone) {
        this.login = login;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
    }

    public AdminDto(Integer id, String login, String fullName, String phone) {
        this.id = id;
        this.login = login;
        this.fullName = fullName;
        this.phone = phone;
    }

    public AdminDto(Integer id, String login, String password, String fullName, String phone) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
    }

    public AdminDto(Integer id, String login, String password, String fullName, String phone, LocalDateTime creationAt,
            LocalDateTime last_log_in) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.creationAt = creationAt;
        this.last_log_in = last_log_in;
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
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public LocalDateTime getCreationAt() {
        return creationAt;
    }
    public void setCreationAt(LocalDateTime creationAt) {
        this.creationAt = creationAt;
    }
    public LocalDateTime getLast_log_in() {
        return last_log_in;
    }
    public void setLast_log_in(LocalDateTime last_log_in) {
        this.last_log_in = last_log_in;
    }

}
