package org.wintersleep.openapi.crud.model;

import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum EntityOperationType {
    LIST(EntityModelType.LIST, EntityModelType.FILTER),
    CREATE(EntityModelType.CREATE),
    READ(EntityModelType.READ),
    UPDATE(EntityModelType.UPDATE),
    DELETE(),
    ;

    private final char letter;
    private final Set<EntityModelType> modelTypes;

    EntityOperationType(EntityModelType... modelTypes) {
        letter = Character.toLowerCase(name().charAt(0));
        this.modelTypes = Sets.immutableEnumSet(List.of(modelTypes));
    }

    public static EntityOperationType of(char ch) {
        for (EntityOperationType access : EntityOperationType.values()) {
            if (access.letter == ch) {
                return access;
            }
        }
        throw new IllegalArgumentException("Invalid character for PropertyAccess: " + ch);
    }

    public static Set<EntityOperationType> parse(String str) {
        return str.chars()
                .mapToObj(ch -> of((char) ch))
                .collect(Collectors.toCollection(EntityOperationType::noneOf));
    }

    public static EnumSet<EntityOperationType> allOf() {
        return EnumSet.allOf(EntityOperationType.class);
    }

    public static EnumSet<EntityOperationType> noneOf() {
        return EnumSet.noneOf(EntityOperationType.class);
    }

}
