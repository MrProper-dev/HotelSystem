package dtos;

import java.time.LocalDateTime;

public class ClientDto {

    private Integer id;
    private String email;
    private String password;
    private String phone;
    private String name;
    private Boolean blocked;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    
    public ClientDto() {
    }

    public ClientDto(Integer id) {
        this.id = id;
    }

    public ClientDto(Integer id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public ClientDto(Integer id, String email, String phone, String name) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.name = name;
    }

    public ClientDto(Integer id, String email, String phone, String name, Boolean blocked) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.blocked = blocked;
    }

    public ClientDto(Integer id, String email, String password, String phone, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
    }

    public ClientDto(String email, String password, String phone, String name) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
    }

    public ClientDto(Integer id, String email, String password, String phone, String name, Boolean blocked,
            LocalDateTime createdAt, LocalDateTime lastActivity) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.blocked = blocked;
        this.createdAt = createdAt;
        this.lastActivity = lastActivity;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
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
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Boolean getBlocked() {
        return blocked;
    }
    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
    
}
