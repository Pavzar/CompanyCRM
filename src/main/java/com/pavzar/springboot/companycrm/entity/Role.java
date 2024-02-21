package com.pavzar.springboot.companycrm.entity;

import jakarta.persistence.*;

import lombok.*;

import java.util.Collection;

@Setter
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@EqualsAndHashCode
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NonNull
    @Column(name = "name")
    private String name;

    @Override
    public String toString() {
        return name;
    }

    @NonNull
    @ManyToMany(mappedBy = "userRoles")
    Collection<User> users;

}