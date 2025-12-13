package com.project.expense_tracker.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.List;

@Entity
@Table (name = "roles")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Roles implements GrantedAuthority {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String roleName;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    @ToString.Exclude
    private List<User> usersSet;

    public String getRoleNameWithoutPrefix() {
        return this.roleName;
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + this.roleName;
    }
}
