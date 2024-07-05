package org.wintersleep.crud.domain;

import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

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

}
