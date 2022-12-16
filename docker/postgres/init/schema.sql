--
-- Extensions
--
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--
-- Functions
--
CREATE OR REPLACE FUNCTION update_updated_at_field()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

--
-- Users
--
CREATE TABLE IF NOT EXISTS users (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid UUID       PRIMARY KEY UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    nickname        VARCHAR(50) NOT NULL,
    email           VARCHAR(50) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    role            VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TRIGGER handle_updated_at_field BEFORE
    UPDATE ON users
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_field();

--
-- Projects
--
CREATE TABLE IF NOT EXISTS projects (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid UUID       PRIMARY KEY UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    name            VARCHAR(50) NOT NULL,
    description     VARCHAR(255) NOT NULL,
    url TEXT        NOT NULL,
    git_url         TEXT NOT NULL,
    tags            VARCHAR(25) ARRAY,
    user_uuid       UUID NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_user_uuid
          FOREIGN KEY(user_uuid)
          REFERENCES users(uuid)
          ON DELETE CASCADE
);

CREATE TRIGGER handle_updated_at_field BEFORE
    UPDATE ON projects
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_field();

--
-- Characters
-- Gender ref: ISO/IEC 5218
--
CREATE TABLE IF NOT EXISTS characters (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid            UUID PRIMARY KEY UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    href            VARCHAR(100) NOT NULL,
    first_name      VARCHAR(50) NOT NULL,
    last_name       VARCHAR(50) NOT NULL,
    nicknames       VARCHAR ARRAY,
    birth_date      TIMESTAMP,
    death_date      TIMESTAMP,
    gender          SMALLINT NOT NULL DEFAULT 0,
    thumbnail       TEXT,
    actor           VARCHAR(100) NOT NULL,
    images          TEXT ARRAY,
    sources         TEXT ARRAY,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TRIGGER handle_updated_at_field BEFORE
    UPDATE ON characters
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_field();

--
-- Actors
--
CREATE TABLE IF NOT EXISTS actors (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid            UUID PRIMARY KEY UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    href            VARCHAR(100) NOT NULL,
    first_name      VARCHAR(50) NOT NULL,
    last_name       VARCHAR(50) NOT NULL,
    nicknames       VARCHAR ARRAY,
    birth_date      TIMESTAMP,
    death_date      TIMESTAMP,
    gender          SMALLINT NOT NULL DEFAULT 0,
    nationality     VARCHAR(50),
    seasons         VARCHAR ARRAY,
    awards          VARCHAR ARRAY,
    character       VARCHAR(255) NOT NULL,
    thumbnail       TEXT,
    images          TEXT ARRAY,
    sources         TEXT ARRAY,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TRIGGER handle_updated_at_field BEFORE
    UPDATE ON actors
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_field();

--
-- Socials
--
CREATE TABLE IF NOT EXISTS actors_socials (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    social VARCHAR(50) NOT NULL,
    handle VARCHAR(50) NOT NULL,
    url TEXT NOT NULL,
    actor_uuid UUID NOT NULL,
    CONSTRAINT fk_actor_uuid
          FOREIGN KEY(actor_uuid)
          REFERENCES actors(uuid)
          ON UPDATE CASCADE
          ON DELETE CASCADE
);

--
-- Episodes
--
CREATE TABLE IF NOT EXISTS episodes (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid            UUID PRIMARY KEY UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    href            VARCHAR(100) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    description     TEXT NOT NULL,
    duration        INTEGER NOT NULL,
    episode_num     SMALLINT NOT NULL,
    next_episode    VARCHAR(255),
    prev_episode    VARCHAR(255),
    season          VARCHAR(255) NOT NULL,
    thumbnail       TEXT,
    images          TEXT ARRAY,
    sources         TEXT ARRAY,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TRIGGER handle_updated_at_field BEFORE
    UPDATE ON episodes
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_field();

--
-- Seasons
--
CREATE TABLE IF NOT EXISTS seasons (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid            UUID PRIMARY KEY UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    href            VARCHAR(100) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    description     TEXT NOT NULL,
    duration_total  INTEGER NOT NULL,
    genres          VARCHAR(50) ARRAY,
    season_num      SMALLINT NOT NULL,
    release_date    TIMESTAMP NOT NULL,
    next_season     VARCHAR(255),
    prev_season     VARCHAR(255),
    episodes        VARCHAR ARRAY,
    soundtracks     VARCHAR(255) ARRAY,
    trailers        VARCHAR ARRAY,
    budget          INTEGER,
    thumbnail       TEXT,
    images          TEXT ARRAY,
    sources         TEXT ARRAY,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TRIGGER handle_updated_at_field BEFORE
    UPDATE ON seasons
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_field();

--
-- Locations
--
CREATE TABLE IF NOT EXISTS locations (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid            UUID PRIMARY KEY UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    href            VARCHAR(100) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    description     TEXT NOT NULL,
    thumbnail       TEXT,
    images          TEXT ARRAY,
    sources         TEXT ARRAY,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TRIGGER handle_updated_at_field BEFORE
    UPDATE ON locations
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_field();

--
-- Soundtracks
--
CREATE TABLE IF NOT EXISTS soundtracks (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid            UUID PRIMARY KEY UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    href            VARCHAR(100) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    duration        INTEGER NOT NULL,
    artist          VARCHAR(255) NOT NULL,
    album           VARCHAR (255),
    release_date    TIMESTAMP NOT NULL,
    urls            TEXT ARRAY NOT NULL,
    thumbnail       TEXT,
    sources         TEXT ARRAY,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TRIGGER handle_updated_at_field BEFORE
    UPDATE ON soundtracks
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_field();

--
-- Games
--
CREATE TABLE IF NOT EXISTS games (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid            UUID PRIMARY KEY UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    href            VARCHAR(100) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    platforms       VARCHAR(50) ARRAY,
    genres          VARCHAR(50) ARRAY,
    publishers      VARCHAR(50) ARRAY,
    developers      VARCHAR(50) ARRAY,
    release_date    TIMESTAMP NOT NULL,
    url             TEXT NOT NULL,
    trailer         TEXT NOT NULL,
    thumbnail       TEXT,
    sources         TEXT ARRAY,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TRIGGER handle_updated_at_field BEFORE
    UPDATE ON games
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_field();

