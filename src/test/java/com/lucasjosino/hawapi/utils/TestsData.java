package com.lucasjosino.hawapi.utils;

import com.lucasjosino.hawapi.models.ActorModel;
import com.lucasjosino.hawapi.models.user.UserAuthenticationModel;
import com.lucasjosino.hawapi.models.user.UserModel;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public final class TestsData {

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