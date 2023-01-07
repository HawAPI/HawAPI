package com.lucasjosino.hawapi.utils;

import com.lucasjosino.hawapi.models.ActorModel;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelAssertions {

    public static void assertActorEquals(ActorModel expected, ActorModel result) {
        assertEquals(expected.getUuid(), result.getUuid());
        assertEquals(expected.getHref(), result.getHref());
        assertEquals(expected.getFirstName(), result.getFirstName());
        assertEquals(expected.getLastName(), result.getLastName());
        assertEquals(expected.getGender(), result.getGender());
        assertEquals(expected.getCharacter(), result.getCharacter());
    }

    public static void assertActorEquals(ActorModel expected, ResponseEntity<ActorModel> result) {
        assertThat(result.getBody()).isNotNull();
        assertEquals(expected.getUuid(), result.getBody().getUuid());
        assertEquals(expected.getHref(), result.getBody().getHref());
        assertEquals(expected.getFirstName(), result.getBody().getFirstName());
        assertEquals(expected.getLastName(), result.getBody().getLastName());
        assertEquals(expected.getGender(), result.getBody().getGender());
        assertEquals(expected.getCharacter(), result.getBody().getCharacter());
    }
}
