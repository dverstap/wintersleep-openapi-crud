package org.wintersleep.openapi.crud.model;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

public enum PropertyModelType {
    SORT,
    FILTER,
    LIST, // TODO delete this field, because: https://marmelab.com/react-admin/DataProviderWriting.html#getlist-and-getone-shared-cache
    CREATE,
    READ,
    UPDATE,
    //DELETE,
    ;

    public final char letter;

    PropertyModelType() {
        letter = Character.toLowerCase(name().charAt(0));
    }

    public static PropertyModelType of(int ch) {
        for (PropertyModelType access : PropertyModelType.values()) {
            if (access.letter == ch) {
                return access;
            }
        }
        throw new IllegalArgumentException(format("Invalid character for PropertyModelType: %s (%s)", (char) ch, ch));
    }

    public static Set<PropertyModelType> parse(String str) {
        return str.chars()
                .mapToObj(PropertyModelType::of)
                .collect(Collectors.toCollection(PropertyModelType::noneOf));
    }

    public static EnumSet<PropertyModelType> allOf() {
        return EnumSet.allOf(PropertyModelType.class);
    }

    public static EnumSet<PropertyModelType> noneOf() {
        return EnumSet.noneOf(PropertyModelType.class);
    }

}
