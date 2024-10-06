package org.wintersleep.openapi.crud.sample.domain;

import lombok.*;
import org.springframework.data.domain.Persistable;
import org.wintersleep.openapi.crud.core.domain.BooleanTimestampPair;

import javax.persistence.*;

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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lastSetAt", column = @Column(name = "last_activated_at")),
            @AttributeOverride(name = "lastUnSetAt", column = @Column(name = "last_de_activated_at"))
    })
    private BooleanTimestampPair activatedTimestampPair;


    @Override
    public boolean isNew() {
        return id == null;
    }

    public boolean isActive() {
        return activatedTimestampPair != null && activatedTimestampPair.get();
    }

    public void setActive(boolean active) {
        if (activatedTimestampPair == null) {
            activatedTimestampPair = new BooleanTimestampPair();
        }
        activatedTimestampPair.set(active);
    }

    public BooleanTimestampPair getActivatedTimestampPair() {
        if (activatedTimestampPair == null) {
            activatedTimestampPair = new BooleanTimestampPair();
        }
        return activatedTimestampPair;
    }
}
