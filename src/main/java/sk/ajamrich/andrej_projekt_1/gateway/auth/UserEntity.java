package sk.ajamrich.andrej_projekt_1.gateway.auth;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public class UserEntity {

    @Id
    private Long id;

    private String username;

    private String passwordHash;

    /** Role oddelene ciarkou, napr. "ROLE_USER,ROLE_ADMIN" */
    private String roles;

    public UserEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
}