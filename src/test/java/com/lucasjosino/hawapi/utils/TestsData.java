package com.lucasjosino.hawapi.utils;

import com.lucasjosino.hawapi.models.*;
import com.lucasjosino.hawapi.models.user.UserAuthenticationModel;
import com.lucasjosino.hawapi.models.user.UserModel;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SuppressWarnings("SpellCheckingInspection")
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
        episode1.setHref("/api/v1/episodes/cf70f1e5-dadc-4dc9-9082-2b94aef34600");
        episode1.setTitle("Lorem");
        episode1.setDescription("Ipsum");
        episode1.setDuration(2400000);
        episode1.setEpisodeNum((byte) 1);
        episode1.setSeason("/api/v1/seasons/1");
        episodes.add(episode1);

        EpisodeModel episode2 = new EpisodeModel();
        episode2.setUuid(UUID.fromString("e6d7e898-7fb3-4224-8127-86376be9c000"));
        episode2.setHref("/api/v1/episodes/e6d7e898-7fb3-4224-8127-86376be9c000");
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
        episode.setHref("/api/v1/episodes/" + episode.getUuid());
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
        game1.setHref("/api/v1/games/cf70f1e5-dadc-4dc9-9082-2b94aef34600");
        game1.setName("Lorem");
        game1.setReleaseDate(LocalDate.now());
        game1.setUrl("https://strangerthings.com/games/1");
        game1.setTrailer("https://youtube.com/1");
        games.add(game1);

        GameModel game2 = new GameModel();
        game2.setUuid(UUID.fromString("e6d7e898-7fb3-4224-8127-86376be9c000"));
        game2.setHref("/api/v1/games/e6d7e898-7fb3-4224-8127-86376be9c000");
        game2.setName("Ipsum");
        game2.setReleaseDate(LocalDate.now());
        game2.setUrl("https://strangerthings.com/games/2");
        game2.setTrailer("https://youtube.com/2");
        games.add(game2);

        return games;
    }

    public static GameModel getNewGame() {
        GameModel game = new GameModel();
        game.setUuid(UUID.randomUUID());
        game.setHref("/api/v1/games/" + game.getUuid());
        game.setName("Lorem Ipsum");
        game.setReleaseDate(LocalDate.now());
        game.setUrl("https://strangerthings.com/games/3");
        game.setTrailer("https://youtube.com/3");
        return game;
    }

    // Locations

    public static LocationModel getSingleLocation() {
        return getLocations().get(0);
    }

    public static List<LocationModel> getLocations() {
        List<LocationModel> locations = new ArrayList<>();

        LocationModel location1 = new LocationModel();
        location1.setUuid(UUID.fromString("cf70f1e5-dadc-4dc9-9082-2b94aef34600"));
        location1.setHref("/api/v1/locations/cf70f1e5-dadc-4dc9-9082-2b94aef34600");
        location1.setName("Lorem");
        location1.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        locations.add(location1);

        LocationModel location2 = new LocationModel();
        location2.setUuid(UUID.fromString("e6d7e898-7fb3-4224-8127-86376be9c000"));
        location2.setHref("/api/v1/locations/e6d7e898-7fb3-4224-8127-86376be9c000");
        location2.setName("Ipsum");
        location2.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        locations.add(location2);

        return locations;
    }

    public static LocationModel getNewLocation() {
        LocationModel location = new LocationModel();
        location.setUuid(UUID.randomUUID());
        location.setHref("/api/v1/locations/" + location.getUuid());
        location.setName("Lorem Ipsum");
        location.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        return location;
    }

    // Seasons

    public static SeasonModel getSingleSeason() {
        return getSeasons().get(0);
    }

    public static List<SeasonModel> getSeasons() {
        List<SeasonModel> seasons = new ArrayList<>();

        SeasonModel season1 = new SeasonModel();
        season1.setUuid(UUID.fromString("cf70f1e5-dadc-4dc9-9082-2b94aef34600"));
        season1.setHref("/api/v1/seasons/cf70f1e5-dadc-4dc9-9082-2b94aef34600");
        season1.setTitle("Lorem");
        season1.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        season1.setDurationTotal(10000);
        season1.setSeasonNum((byte) 1);
        season1.setReleaseDate(LocalDate.now());
        seasons.add(season1);

        SeasonModel season2 = new SeasonModel();
        season2.setUuid(UUID.fromString("e6d7e898-7fb3-4224-8127-86376be9c000"));
        season2.setHref("/api/v1/seasons/e6d7e898-7fb3-4224-8127-86376be9c000");
        season2.setTitle("Ipsum");
        season2.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        season2.setDurationTotal(20000);
        season2.setSeasonNum((byte) 2);
        season2.setReleaseDate(LocalDate.now());
        seasons.add(season2);

        return seasons;
    }

    public static SeasonModel getNewSeason() {
        SeasonModel season = new SeasonModel();
        season.setUuid(UUID.randomUUID());
        season.setHref("/api/v1/seasons/" + season.getUuid());
        season.setTitle("Lorem Ipsum");
        season.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        season.setDurationTotal(30000);
        season.setSeasonNum((byte) 3);
        season.setReleaseDate(LocalDate.now());
        return season;
    }

    // Soundtracks

    public static SoundtrackModel getSingleSoundtrack() {
        return getSoundtracks().get(0);
    }

    public static List<SoundtrackModel> getSoundtracks() {
        List<SoundtrackModel> soundtracks = new ArrayList<>();

        SoundtrackModel soundtrack1 = new SoundtrackModel();
        soundtrack1.setUuid(UUID.fromString("cf70f1e5-dadc-4dc9-9082-2b94aef34600"));
        soundtrack1.setHref("/api/v1/soundtracks/cf70f1e5-dadc-4dc9-9082-2b94aef34600");
        soundtrack1.setName("Lorem");
        soundtrack1.setArtist("Lorem ipsum");
        soundtrack1.setReleaseDate(LocalDate.now());
        soundtrack1.setUrls(new String[]{"https://strangerthings.com/soundtracks/play/1"});
        soundtracks.add(soundtrack1);

        SoundtrackModel soundtrack2 = new SoundtrackModel();
        soundtrack2.setUuid(UUID.fromString("e6d7e898-7fb3-4224-8127-86376be9c000"));
        soundtrack2.setHref("/api/v1/soundtracks/e6d7e898-7fb3-4224-8127-86376be9c000");
        soundtrack2.setName("Ipsum");
        soundtrack2.setArtist("Lorem ipsum");
        soundtrack2.setReleaseDate(LocalDate.now());
        soundtrack2.setUrls(new String[]{"https://strangerthings.com/soundtracks/play/2"});
        soundtracks.add(soundtrack2);

        return soundtracks;
    }

    public static SoundtrackModel getNewSoundtrack() {
        SoundtrackModel soundtrack = new SoundtrackModel();
        soundtrack.setUuid(UUID.randomUUID());
        soundtrack.setHref("/api/v1/soundtracks/" + soundtrack.getUuid());
        soundtrack.setName("Lorem Ipsum");
        soundtrack.setArtist("Lorem ipsum");
        soundtrack.setReleaseDate(LocalDate.now());
        soundtrack.setUrls(new String[]{"https://strangerthings.com/soundtracks/play/3"});
        return soundtrack;
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