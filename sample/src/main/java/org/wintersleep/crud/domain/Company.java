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
        name = "company",
        indexes = {
                @Index(name = "vat_number", unique = true, columnList = "vatNumber")
        }
)
public class Company implements Persistable<Long> {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @NaturalId
    @Column(nullable = false)
    private String vatNumber;

    @Column(nullable = false)
    private String name;

    private String externalId;

    private String url;

    @Override
    public boolean isNew() {
        return id == null;
    }

}
