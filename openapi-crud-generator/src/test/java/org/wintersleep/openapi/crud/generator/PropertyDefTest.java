package org.wintersleep.openapi.crud.generator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyDefTest {

    @Test
    void of() {
        assertThat(PropertyDef.of("pname", "lsfcru int"))
                .isEqualTo(new PropertyDef("pname", false, PropertyModelType.allOf(), "int", null));
        assertThat(PropertyDef.of("pname?", "lsfcru string:email"))
                .isEqualTo(new PropertyDef("pname", true, PropertyModelType.allOf(), "string", "email"));
    }

    // TODO error cases

}