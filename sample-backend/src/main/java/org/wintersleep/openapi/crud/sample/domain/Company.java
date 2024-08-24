package org.wintersleep.openapi.crud.sample.domain;

import lombok.*;
import org.hibernate.annotations.NaturalId;
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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lastSetAt", column = @Column(name = "last_verified_at")),
            @AttributeOverride(name = "lastUnSetAt", column = @Column(name = "last_un_verified_at"))
    })
    private BooleanTimestampPair verifiedTimestampPair;

    @Override
    public boolean isNew() {
        return id == null;
    }

    public BooleanTimestampPair getVerifiedTimestampPair() {
        if (verifiedTimestampPair == null) {
            verifiedTimestampPair = new BooleanTimestampPair();
        }
        return verifiedTimestampPair;
    }

    public boolean isVerified() {
        return verifiedTimestampPair != null && verifiedTimestampPair.get();
    }

    public void setVerified(boolean verified) {
        if (verifiedTimestampPair == null) {
            verifiedTimestampPair = new BooleanTimestampPair();
        }
        verifiedTimestampPair.set(verified);
    }

}
