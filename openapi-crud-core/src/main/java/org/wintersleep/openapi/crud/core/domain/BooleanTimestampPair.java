package org.wintersleep.openapi.crud.core.domain;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.Data;
import org.wintersleep.openapi.crud.core.provider.OrderDirection;
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

    public void set(boolean value) {
        if (value == this.get()) {
            // avoid updating the timestamps all the time:
            return;
        }
        if (value) {
            lastSetAt = Now.offsetDateTime();
        } else {
            lastUnSetAt = Now.offsetDateTime();
        }
    }

    public static BooleanExpression filter(QBooleanTimestampPair timestampPair, Boolean value) {
        if (value == null) {
            return null;
        }
        if (value) {
            return trueExpr(timestampPair);
        } else {
            return trueExpr(timestampPair).not();
        }
    }


    public static Expression<? extends Comparable<?>> order(QBooleanTimestampPair timestampPair, OrderDirection direction) {
//        return switch (direction) {
//            case ASC -> Expressions.cases()
//                    .when(trueExpr(timestampPair))
//                    .then(Expressions.constant(1))
//                    .otherwise(0);
//            case DESC -> Expressions.cases()
//                    .when(trueExpr(timestampPair))
//                    .then(Expressions.constant(0))
//                    .otherwise(1);
//        };
        return Expressions.cases()
                .when(trueExpr(timestampPair))
                .then(Expressions.constant(1))
                .otherwise(0);
    }

    public static BooleanExpression trueExpr(QBooleanTimestampPair timestampPair) {
        return Expressions.allOf(
                timestampPair.lastSetAt.isNotNull(),
                Expressions.anyOf(
                        timestampPair.lastUnSetAt.isNull(),
                        timestampPair.lastSetAt.after(timestampPair.lastUnSetAt)
                )
        );
    }

}
