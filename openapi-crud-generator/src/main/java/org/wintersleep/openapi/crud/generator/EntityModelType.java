package org.wintersleep.openapi.crud.generator;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum EntityModelType {
    FILTER(PropertyModelType.FILTER),
    LIST(PropertyModelType.LIST), // TODO delete this field, because: https://marmelab.com/react-admin/DataProviderWriting.html#getlist-and-getone-shared-cache
    CREATE(PropertyModelType.CREATE),
    READ(PropertyModelType.READ),
    UPDATE(PropertyModelType.UPDATE),
    //DELETE,
    ;

    private final PropertyModelType propertyModelType;
    public final char letter;

    EntityModelType(PropertyModelType propertyModelType) {
        this.propertyModelType = propertyModelType;
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

    public PropertyModelType getPropertyModelType() {
        return propertyModelType;
    }

    public char getLetter() {
        return letter;
    }

}
