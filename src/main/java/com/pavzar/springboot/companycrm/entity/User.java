package com.pavzar.springboot.companycrm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Collection;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "users")
public class User implements Comparable<User>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "username")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    @Column(name = "first_name")
    private String firstName;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    @Column(name = "last_name")
    private String lastName;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    @Pattern(regexp="^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    @Column(name = "email")
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> userRoles;

    public String allUserRoles() {
        return userRoles.stream().map(role -> role.getName().substring(5)).collect(Collectors.joining(", "));
    }

    public User(String userName, String password, boolean enabled) {
        this.userName = userName;
        this.password = password;
        this.enabled = enabled;
    }

    public User(String userName, String password, boolean enabled,
                Collection<Role> userRoles) {
        this.userName = userName;
        this.password = password;
        this.enabled = enabled;
        this.userRoles = userRoles;
    }

    @Override
    public int compareTo(User otherUser) {
        return this.getLastName().compareTo(otherUser.getLastName());
    }
}