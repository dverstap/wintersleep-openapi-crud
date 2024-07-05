package org.wintersleep.openapi;

import io.swagger.v3.core.util.PrimitiveType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PrimitiveTypeTest {

    @Test
    void testNumber() {
        assertFromTypeAndFormatReturnsPrimitiveType("number", null, PrimitiveType.NUMBER);
        assertFromTypeAndFormatReturnsPrimitiveType("number", null, PrimitiveType.NUMBER);

    }

    @Test
    void testInteger() {
        assertFromTypeAndFormatReturnsPrimitiveType("integer", null, PrimitiveType.INT);
        assertFromTypeAndFormatReturnsPrimitiveType("integer", "int32", PrimitiveType.INT);
        assertFromTypeAndFormatReturnsPrimitiveType("integer", "int64", PrimitiveType.LONG);
        //assertFromTypeAndFormatReturnsPrimitiveType("integer", null, PrimitiveType.INTEGER); // BigInteger
    }

    private static void assertFromTypeAndFormatReturnsPrimitiveType(String type, String format, PrimitiveType result) {
        assertThat(PrimitiveType.fromTypeAndFormat(type, format))
                .isEqualTo(result);
    }

}
