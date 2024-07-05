package org.wintersleep.crud.domain;

import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.time.Instant;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(
        name = "employee",
        indexes = {
                @Index(name = "employee_user_company", unique = true, columnList = "user_id,company_id")
        }
)
public class Employee implements Persistable<Long> {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Company company;

    private OffsetDateTime lastActivatedAt;

    private OffsetDateTime lastDeActivatedAt;

    @Override
    public boolean isNew() {
        return id == null;
    }

    public boolean isActive() {
        if (lastActivatedAt == null) {
            return false;
        }
        if (lastDeActivatedAt == null) {
            return true;
        }
        return lastActivatedAt.isAfter(lastDeActivatedAt);
    }
}
