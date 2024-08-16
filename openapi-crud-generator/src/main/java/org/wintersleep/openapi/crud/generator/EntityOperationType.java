package org.wintersleep.openapi.crud.generator;

import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Getter
public enum EntityOperationType {
    LIST(true, EntityModelType.LIST, EntityModelType.FILTER),
    GET_MANY(true, EntityModelType.READ),
    CREATE(false, EntityModelType.CREATE),
    READ(false, EntityModelType.READ),
    UPDATE(false, EntityModelType.UPDATE),
    DELETE(false),
    ;

    private final char letter;
    private final boolean multiple;
    private final Set<EntityModelType> modelTypes;

    EntityOperationType(boolean multiple, EntityModelType... modelTypes) {
        this.multiple = multiple;
        letter = Character.toLowerCase(name().charAt(0));
        this.modelTypes = Sets.immutableEnumSet(List.of(modelTypes));
    }

    public static EntityOperationType of(int ch) {
        for (EntityOperationType access : EntityOperationType.values()) {
            if (access.letter == ch) {
                return access;
            }
        }
        throw new IllegalArgumentException(format("Invalid character for EntityOperationType: %s (%s)", (char) ch, ch));
    }

    public static Set<EntityOperationType> parse(String str) {
        return str.chars()
                .mapToObj(ch -> of(ch))
                .collect(Collectors.toCollection(EntityOperationType::noneOf));
    }

    public static EnumSet<EntityOperationType> allOf() {
        return EnumSet.allOf(EntityOperationType.class);
    }

    public static EnumSet<EntityOperationType> noneOf() {
        return EnumSet.noneOf(EntityOperationType.class);
    }

}
