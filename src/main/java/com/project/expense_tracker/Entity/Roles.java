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

    @ManyToMany(mappedBy = "rolesList")
    @JsonIgnore
    @ToString.Exclude
    private List<User> usersSet;

    @Override
    public String getAuthority() {
        return this.roleName;
    }
}
