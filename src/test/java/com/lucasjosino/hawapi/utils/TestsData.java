package com.lucasjosino.hawapi.utils;

import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.models.CharacterModel;
import com.lucasjosino.hawapi.models.EpisodeModel;
import com.lucasjosino.hawapi.models.GameModel;
import com.lucasjosino.hawapi.models.user.UserAuthenticationModel;
import com.lucasjosino.hawapi.models.user.UserModel;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public final class TestsData {

    // Actors

    public static ActorModel getSingleActor() {
        return getActors().get(0);
    }

    public static List<ActorModel> getActors() {
        List<ActorModel> actors = new ArrayList<>();

        ActorModel actor1 = new ActorModel();
        actor1.setUuid(UUID.fromString("cf70f1e5-dadc-4dc9-9082-2b94aef34600"));
        actor1.setHref("/api/v1/actors/cf70f1e5-dadc-4dc9-9082-2b94aef34600");
        actor1.setFirstName("John");
        actor1.setLastName("Doe");
        actor1.setGender((byte) 1);
        actor1.setCharacter("/api/v1/characters/1");
        actors.add(actor1);

        ActorModel actor2 = new ActorModel();
        actor2.setUuid(UUID.fromString("e6d7e898-7fb3-4224-8127-86376be9c000"));
        actor2.setHref("/api/v1/actors/e6d7e898-7fb3-4224-8127-86376be9c000");
        actor2.setFirstName("Ana");
        actor2.setLastName("Doe");
        actor2.setGender((byte) 2);
        actor2.setCharacter("/api/v1/characters/2");
        actors.add(actor2);

        return actors;
    }

    public static ActorModel getNewActor() {
        ActorModel actor = new ActorModel();
        actor.setUuid(UUID.randomUUID());
        actor.setHref("/api/v1/actors/" + actor.getUuid());
        actor.setFirstName("John");
        actor.setLastName("Mock");
        actor.setGender((byte) 1);
        actor.setCharacter("/api/v1/characters/3");
        return actor;
    }

    // Characters

    public static CharacterModel getSingleCharacter() {
        return getCharacters().get(0);
    }

    public static List<CharacterModel> getCharacters() {
        List<CharacterModel> characters = new ArrayList<>();

        CharacterModel character1 = new CharacterModel();
        character1.setUuid(UUID.fromString("cf70f1e5-dadc-4dc9-9082-2b94aef34600"));
        character1.setHref("/api/v1/characters/cf70f1e5-dadc-4dc9-9082-2b94aef34600");
        character1.setFirstName("John");
        character1.setLastName("Doe");
        character1.setGender((byte) 1);
        character1.setActor("/api/v1/actors/1");
        characters.add(character1);

        CharacterModel character2 = new CharacterModel();
        character2.setUuid(UUID.fromString("e6d7e898-7fb3-4224-8127-86376be9c000"));
        character2.setHref("/api/v1/characters/e6d7e898-7fb3-4224-8127-86376be9c000");
        character2.setFirstName("Ana");
        character2.setLastName("Doe");
        character2.setGender((byte) 2);
        character2.setActor("/api/v1/actors/2");
        characters.add(character2);

        return characters;
    }

    public static CharacterModel getNewCharacter() {
        CharacterModel character = new CharacterModel();
        character.setUuid(UUID.randomUUID());
        character.setHref("/api/v1/characters/" + character.getUuid());
        character.setFirstName("John");
        character.setLastName("Mock");
        character.setGender((byte) 1);
        character.setActor("/api/v1/actors/3");
        return character;
    }

    // Episodes

    public static EpisodeModel getSingleEpisode() {
        return getEpisodes().get(0);
    }

    public static List<EpisodeModel> getEpisodes() {
        List<EpisodeModel> episodes = new ArrayList<>();

        EpisodeModel episode1 = new EpisodeModel();
        episode1.setUuid(UUID.fromString("cf70f1e5-dadc-4dc9-9082-2b94aef34600"));
        episode1.setHref("/api/v1/actors/cf70f1e5-dadc-4dc9-9082-2b94aef34600");
        episode1.setTitle("Lorem");
        episode1.setDescription("Ipsum");
        episode1.setDuration(2400000);
        episode1.setEpisodeNum((byte) 1);
        episode1.setSeason("/api/v1/seasons/1");
        episodes.add(episode1);

        EpisodeModel episode2 = new EpisodeModel();
        episode2.setUuid(UUID.fromString("e6d7e898-7fb3-4224-8127-86376be9c000"));
        episode2.setHref("/api/v1/actors/e6d7e898-7fb3-4224-8127-86376be9c000");
        episode2.setTitle("Ipsum");
        episode2.setDescription("Lorem");
        episode2.setDuration(2580000);
        episode2.setEpisodeNum((byte) 2);
        episode2.setSeason("/api/v1/seasons/2");
        episodes.add(episode2);

        return episodes;
    }

    public static EpisodeModel getNewEpisode() {
        EpisodeModel episode = new EpisodeModel();
        episode.setUuid(UUID.randomUUID());
        episode.setHref("/api/v1/actors/" + episode.getUuid());
        episode.setTitle("Lorem Ipsum");
        episode.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        episode.setDuration(2580000);
        episode.setEpisodeNum((byte) 3);
        episode.setSeason("/api/v1/seasons/3");
        return episode;
    }

    // Games

    public static GameModel getSingleGame() {
        return getGames().get(0);
    }

    public static List<GameModel> getGames() {
        List<GameModel> games = new ArrayList<>();

        GameModel game1 = new GameModel();
        game1.setUuid(UUID.fromString("cf70f1e5-dadc-4dc9-9082-2b94aef34600"));
        game1.setHref("/api/v1/actors/cf70f1e5-dadc-4dc9-9082-2b94aef34600");
        game1.setName("Lorem");
        game1.setReleaseDate(LocalDate.now());
        game1.setUrl("https://strangerthingsgames.com/2");
        game1.setTrailer("https://youtube.com/1");
        games.add(game1);

        GameModel game2 = new GameModel();
        game2.setUuid(UUID.fromString("e6d7e898-7fb3-4224-8127-86376be9c000"));
        game2.setHref("/api/v1/actors/e6d7e898-7fb3-4224-8127-86376be9c000");
        game2.setName("Ipsum");
        game2.setReleaseDate(LocalDate.now());
        game2.setUrl("https://strangerthingsgames.com/2");
        game2.setTrailer("https://youtube.com/1");
        games.add(game2);

        return games;
    }

    public static GameModel getNewGame() {
        GameModel game = new GameModel();
        game.setUuid(UUID.randomUUID());
        game.setHref("/api/v1/actors/" + game.getUuid());
        game.setName("Lorem Ipsum");
        game.setReleaseDate(LocalDate.now());
        game.setUrl("https://strangerthingsgames.com/2");
        game.setTrailer("https://youtube.com/1");
        return game;
    }

    // Users/Auth

    public static UserModel getNewUser() {
        UserModel user = new UserModel();
        user.setUuid(UUID.randomUUID());
        user.setNickname("loremipsum");
        user.setEmail("lorem@loremipsum.com");
        user.setPassword("MYSUPERSECREATPASSWORD");
        user.setRole("DEV");
        return user;
    }

    public static UserAuthenticationModel getNewUserAuth() {
        UserAuthenticationModel userAuth = new UserAuthenticationModel();
        userAuth.setNickname(getNewUser().getNickname());
        userAuth.setEmail(getNewUser().getEmail());
        userAuth.setPassword(getNewUser().getPassword());
        return userAuth;
    }

    public static RequestPostProcessor getAdminAuth() {
        return user("admin").roles("ADMIN");
    }

    public static RequestPostProcessor getDevAuth() {
        return user("dev").roles("DEV");
    }
}