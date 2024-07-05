package org.wintersleep.openapi.crud.model;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum EntityModelType {
    FILTER,
    LIST(),
    CREATE,
    READ,
    UPDATE,
    //DELETE,
    ;

    public final char letter;

    EntityModelType() {
        letter = Character.toLowerCase(name().charAt(0));
    }

    public static EntityModelType of(int ch) {
        for (EntityModelType access : EntityModelType.values()) {
            if (access.letter == ch) {
                return access;
            }
        }
        throw new IllegalArgumentException("Invalid character for PropertyAccess: " + ch);
    }

    public static Set<EntityModelType> parse(String str) {
        return str.chars()
                .mapToObj(EntityModelType::of)
                .collect(Collectors.toCollection(EntityModelType::noneOf));
    }

    public static EnumSet<EntityModelType> allOf() {
        return EnumSet.allOf(EntityModelType.class);
    }

    public static EnumSet<EntityModelType> noneOf() {
        return EnumSet.noneOf(EntityModelType.class);
    }

}
