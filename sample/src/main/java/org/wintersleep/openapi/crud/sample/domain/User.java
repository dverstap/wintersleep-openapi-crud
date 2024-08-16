package org.wintersleep.openapi.crud.sample.domain;

import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.domain.Persistable;
import org.springframework.util.StringUtils;

import javax.persistence.*;

import static org.springframework.util.StringUtils.hasText;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(
        name = "principal",
        indexes = {
                @Index(name = "email", unique = true, columnList = "email")
        }
)
public class User implements Persistable<Long> {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @NaturalId
    @Column(nullable = false)
    private String email;

    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String displayName;

    @Override
    public boolean isNew() {
        return id == null;
    }

    /// Call this after setting email and first/last name!
    public void setDisplayName(String displayName) {
        if (hasText(displayName)) {
            this.displayName = displayName;
        } else {
            if (hasText(this.firstName) && hasText(this.lastName)) {
                this.displayName = firstName + " " + lastName;
            } else if (hasText(this.firstName)) {
                this.displayName = firstName;
            } else if (hasText(this.lastName)) {
                this.displayName = lastName;
            } else {
                this.displayName = email;
            }
        }
    }
}
