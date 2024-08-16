package org.wintersleep.openapi.crud.model;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

public enum AccessAudit {
    CREATE,
    UPDATE,
    DELETE,
    ;

    public final char letter;

    AccessAudit() {
        letter = Character.toLowerCase(name().charAt(0));
    }

    public static AccessAudit of(int ch) {
        for (AccessAudit access : AccessAudit.values()) {
            if (access.letter == ch) {
                return access;
            }
        }
        throw new IllegalArgumentException(format("Invalid character for AccessAudit: %s (%s)", (char) ch, ch));
    }

    public static Set<AccessAudit> parse(String str) {
        return str.chars()
                .mapToObj(AccessAudit::of)
                .collect(Collectors.toCollection(AccessAudit::noneOf));
    }

    public static EnumSet<AccessAudit> allOf() {
        return EnumSet.allOf(AccessAudit.class);
    }

    public static EnumSet<AccessAudit> noneOf() {
        return EnumSet.noneOf(AccessAudit.class);
    }

}
