package org.wintersleep.openapi.crud.core.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class Now {

    public static LocalDate localDate() {
        return LocalDate.now();
    }

    public static LocalDateTime localDateTime() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public static OffsetDateTime offsetDateTime() {
        return OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public static Instant instant() {
        return Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }

}
