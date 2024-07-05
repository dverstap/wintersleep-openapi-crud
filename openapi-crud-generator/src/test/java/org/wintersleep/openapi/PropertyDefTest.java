package org.wintersleep.openapi;

import org.junit.jupiter.api.Test;
import org.wintersleep.openapi.crud.model.EntityModelType;
import org.wintersleep.openapi.crud.model.PropertyDef;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyDefTest {

    @Test
    void of() {
        assertThat(PropertyDef.of("pname", "lfcrud int"))
                .isEqualTo(new PropertyDef("pname", false, EntityModelType.allOf(), "int", null));
        assertThat(PropertyDef.of("pname?", "lfcrud string:email"))
                .isEqualTo(new PropertyDef("pname", true, EntityModelType.allOf(), "string", "email"));
    }

    // TODO error cases

}