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

    private OffsetDateTime lastTrueAt;
    private OffsetDateTime lastFalseAt;

    public static BooleanTimestampPair of(boolean value) {
        BooleanTimestampPair result = new BooleanTimestampPair();
        if (value) {
            result.lastTrueAt = Now.offsetDateTime();
        }
        return result;
    }

    public boolean get() {
        if (lastTrueAt == null) {
            return false;
        }
        if (lastFalseAt == null) {
            return true;
        }
        return lastTrueAt.isAfter(lastFalseAt);
    }

    public void set(boolean verified) {
        if (verified) {
            lastTrueAt = Now.offsetDateTime();
        } else {
            lastFalseAt = Now.offsetDateTime();
        }
    }

    public static BooleanExpression filter(QBooleanTimestampPair timestampPair, Boolean value) {
        if (value == null) {
            return null;
        }
        BooleanExpression expr = Expressions.allOf(
                timestampPair.lastTrueAt.isNotNull(),
                Expressions.anyOf(
                        timestampPair.lastFalseAt.isNull(),
                        timestampPair.lastTrueAt.after(timestampPair.lastFalseAt)
                )
        );
        if (value) {
            return expr;
        } else {
            return expr.not();
        }
    }

}
