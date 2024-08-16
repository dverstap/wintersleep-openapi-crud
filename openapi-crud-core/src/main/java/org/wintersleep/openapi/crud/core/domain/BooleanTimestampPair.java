package org.wintersleep.openapi.crud.core.domain;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.Data;
import org.wintersleep.openapi.crud.core.util.Now;

import javax.persistence.Embeddable;
import java.time.OffsetDateTime;

@Embeddable
@Data
public class BooleanTimestampPair {

    private OffsetDateTime lastSetAt;
    private OffsetDateTime lastUnSetAt;

    public static BooleanTimestampPair of(boolean value) {
        BooleanTimestampPair result = new BooleanTimestampPair();
        if (value) {
            result.lastSetAt = Now.offsetDateTime();
        }
        return result;
    }

    public boolean get() {
        if (lastSetAt == null) {
            return false;
        }
        if (lastUnSetAt == null) {
            return true;
        }
        return lastSetAt.isAfter(lastUnSetAt);
    }

    public void set(boolean verified) {
        if (verified) {
            lastSetAt = Now.offsetDateTime();
        } else {
            lastUnSetAt = Now.offsetDateTime();
        }
    }

    public static BooleanExpression filter(QBooleanTimestampPair timestampPair, Boolean value) {
        if (value == null) {
            return null;
        }
        BooleanExpression expr = Expressions.allOf(
                timestampPair.lastSetAt.isNotNull(),
                Expressions.anyOf(
                        timestampPair.lastUnSetAt.isNull(),
                        timestampPair.lastSetAt.after(timestampPair.lastUnSetAt)
                )
        );
        if (value) {
            return expr;
        } else {
            return expr.not();
        }
    }

}
