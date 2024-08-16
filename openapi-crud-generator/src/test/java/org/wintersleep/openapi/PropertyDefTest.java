package org.wintersleep.openapi;

import org.junit.jupiter.api.Test;
import org.wintersleep.openapi.crud.model.EntityModelType;
import org.wintersleep.openapi.crud.model.PropertyDef;
import org.wintersleep.openapi.crud.model.PropertyModelType;

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