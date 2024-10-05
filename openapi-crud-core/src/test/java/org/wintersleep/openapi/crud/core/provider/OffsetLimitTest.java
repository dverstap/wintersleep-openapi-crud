package org.wintersleep.openapi.crud.core.provider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OffsetLimitTest {

    @Test
    void ofPage() {
        OffsetLimit offsetLimit = OffsetLimit.ofPage(5, 10);
        assertEquals(50, offsetLimit.offset());
        assertEquals(10, offsetLimit.limit());
    }

    @Test
    void ofStartEnd() {
        OffsetLimit offsetLimit = OffsetLimit.ofStartEnd(50L, 60L);
        assertEquals(50, offsetLimit.offset());
        assertEquals(10, offsetLimit.limit());
    }

}
