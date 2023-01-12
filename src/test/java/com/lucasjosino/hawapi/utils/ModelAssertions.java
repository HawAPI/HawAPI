package com.lucasjosino.hawapi.utils;

import com.lucasjosino.hawapi.models.*;
import com.lucasjosino.hawapi.models.user.UserModel;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelAssertions {

    // Actors

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

    // Characters

    public static void assertCharacterEquals(CharacterModel expected, CharacterModel result) {
        assertEquals(expected.getUuid(), result.getUuid());
        assertEquals(expected.getHref(), result.getHref());
        assertEquals(expected.getFirstName(), result.getFirstName());
        assertEquals(expected.getLastName(), result.getLastName());
        assertEquals(expected.getGender(), result.getGender());
        assertEquals(expected.getActor(), result.getActor());
    }

    public static void assertCharacterEquals(CharacterModel expected, ResponseEntity<CharacterModel> result) {
        assertThat(result.getBody()).isNotNull();
        assertEquals(expected.getUuid(), result.getBody().getUuid());
        assertEquals(expected.getHref(), result.getBody().getHref());
        assertEquals(expected.getFirstName(), result.getBody().getFirstName());
        assertEquals(expected.getLastName(), result.getBody().getLastName());
        assertEquals(expected.getGender(), result.getBody().getGender());
        assertEquals(expected.getActor(), result.getBody().getActor());
    }

    // Episodes

    public static void assertEpisodeEquals(EpisodeModel expected, EpisodeModel result) {
        assertEquals(expected.getUuid(), result.getUuid());
        assertEquals(expected.getHref(), result.getHref());
        assertEquals(expected.getTitle(), result.getTitle());
        assertEquals(expected.getDescription(), result.getDescription());
        assertEquals(expected.getDuration(), result.getDuration());
        assertEquals(expected.getSeason(), result.getSeason());
    }

    public static void assertEpisodeEquals(EpisodeModel expected, ResponseEntity<EpisodeModel> result) {
        assertThat(result.getBody()).isNotNull();
        assertEquals(expected.getUuid(), result.getBody().getUuid());
        assertEquals(expected.getHref(), result.getBody().getHref());
        assertEquals(expected.getTitle(), result.getBody().getTitle());
        assertEquals(expected.getDescription(), result.getBody().getDescription());
        assertEquals(expected.getDuration(), result.getBody().getDuration());
        assertEquals(expected.getSeason(), result.getBody().getSeason());
    }

    // Games

    public static void assertGameEquals(GameModel expected, GameModel result) {
        assertEquals(expected.getUuid(), result.getUuid());
        assertEquals(expected.getHref(), result.getHref());
        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getReleaseDate().toString(), result.getReleaseDate().toString());
        assertEquals(expected.getUrl(), result.getUrl());
        assertEquals(expected.getTrailer(), result.getTrailer());
    }

    public static void assertGameEquals(GameModel expected, ResponseEntity<GameModel> result) {
        assertThat(result.getBody()).isNotNull();
        assertEquals(expected.getUuid(), result.getBody().getUuid());
        assertEquals(expected.getHref(), result.getBody().getHref());
        assertEquals(expected.getName(), result.getBody().getName());
        assertEquals(expected.getReleaseDate().toString(), result.getBody().getReleaseDate().toString());
        assertEquals(expected.getUrl(), result.getBody().getUrl());
        assertEquals(expected.getTrailer(), result.getBody().getTrailer());
    }

    // Locations

    public static void assertLocationEquals(LocationModel expected, LocationModel result) {
        assertEquals(expected.getUuid(), result.getUuid());
        assertEquals(expected.getHref(), result.getHref());
        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getDescription(), result.getDescription());
    }

    public static void assertLocationEquals(LocationModel expected, ResponseEntity<LocationModel> result) {
        assertThat(result.getBody()).isNotNull();
        assertEquals(expected.getUuid(), result.getBody().getUuid());
        assertEquals(expected.getHref(), result.getBody().getHref());
        assertEquals(expected.getName(), result.getBody().getName());
        assertEquals(expected.getDescription(), result.getBody().getDescription());
    }

    // User/Auth

    public static void assertAuthEquals(UserModel expected, ResponseEntity<UserModel> result) {
        assertEquals(expected.getNickname(), Objects.requireNonNull(result.getBody()).getNickname());
        assertEquals(expected.getEmail(), Objects.requireNonNull(result.getBody()).getEmail());
        assertEquals(expected.getRole(), Objects.requireNonNull(result.getBody()).getRole());
    }
}
