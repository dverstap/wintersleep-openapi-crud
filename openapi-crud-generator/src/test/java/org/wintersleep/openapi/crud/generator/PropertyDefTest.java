package org.wintersleep.openapi.crud.generator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PropertyDefTest {

    @Test
    void ofEntity() {
        assertThat(PropertyDef.ofEntity("pname", "sfcru int"))
                .isEqualTo(new PropertyDef("pname", false, false, PropertyModelType.allOf(),
                        "int", null, null, null));
        assertThat(PropertyDef.ofEntity("pname?", "sfcru string:email"))
                .isEqualTo(new PropertyDef("pname", true, false, PropertyModelType.allOf(),
                        "string", "email", null, null));
        assertThat(PropertyDef.ofEntity("pname[]", "sfcru string:email"))
                .isEqualTo(new PropertyDef("pname", false, true, PropertyModelType.allOf(),
                        "string", "email", null, null));
    }

    @Test
    void errorTooManyWords() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PropertyDef.ofEntity("pname", "too many words");
        });
        assertEquals("Expected two space-separated words instead of 'too many words' for property 'pname'", exception.getMessage());
    }

    @Test
    void errorInvalidTypeFormat() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PropertyDef.ofEntity("pname", "cru my-type:my-format:unexpected");
        });
        assertEquals("Expected 'type' or 'type:format' in the second word of 'cru my-type:my-format:unexpected' for property 'pname'", exception.getMessage());
    }

    @Test
    void errorInvalidPropertyCodes() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PropertyDef.ofEntity("pname", "sfxcryu integer");
        });
        assertEquals("Invalid PropertyModelType codes 'xy' in 'sfxcryu integer' for property 'pname'", exception.getMessage());
    }

}
