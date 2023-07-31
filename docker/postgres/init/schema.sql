--
-- Functions
--

-- Trigger to update column 'updated_at' every time some column is updated.
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
    BEGIN
        NEW.updated_at = now();
        RETURN NEW;
    END;
$$ language plpgsql;

-- Trigger to update column 'updated_at' every time some column from 'translations' table is updated.
CREATE OR REPLACE FUNCTION handle_child_update()
RETURNS TRIGGER AS $$
    BEGIN
      EXECUTE 'UPDATE ' || TG_ARGV[0]::text || ' SET updated_at = now() WHERE uuid = $1.' || TG_ARGV[1]::text
      	USING NEW;
      RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

--
-- Tables
--

-- Users
CREATE TABLE IF NOT EXISTS users (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid UUID       PRIMARY KEY UNIQUE NOT NULL,
    first_name      VARCHAR(30) NOT NULL,
    last_name       VARCHAR(30) NOT NULL,
    username        VARCHAR(30) NOT NULL,
    email           VARCHAR(50) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    role            VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- Overviews
CREATE TABLE IF NOT EXISTS overviews (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid            UUID PRIMARY KEY UNIQUE NOT NULL,
    href            VARCHAR(100) NOT NULL,
    thumbnail       TEXT,
    creators        VARCHAR(30) ARRAY,
    sources         TEXT ARRAY,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- Overviews translations
CREATE TABLE IF NOT EXISTS overviews_translations (
    id              INTEGER PRIMARY KEY UNIQUE GENERATED ALWAYS AS IDENTITY,
    overview_uuid   UUID NOT NULL,
    language        VARCHAR(5) NOT NULL DEFAULT 'en-US',
    title           VARCHAR(255) NOT NULL,
    description     TEXT NOT NULL,
    CONSTRAINT fk_overview_uuid
          FOREIGN KEY(overview_uuid)
          REFERENCES overviews(uuid)
          ON UPDATE CASCADE
          ON DELETE CASCADE
);

-- Characters
CREATE TABLE IF NOT EXISTS characters (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid            UUID PRIMARY KEY UNIQUE NOT NULL,
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

-- Actors
CREATE TABLE IF NOT EXISTS actors (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid            UUID PRIMARY KEY UNIQUE NOT NULL,
    href            VARCHAR(100) NOT NULL,
    first_name      VARCHAR(50) NOT NULL,
    last_name       VARCHAR(50) NOT NULL,
    nicknames       VARCHAR ARRAY,
    nationality     VARCHAR(50),
    birth_date      TIMESTAMP,
    death_date      TIMESTAMP,
    gender          SMALLINT NOT NULL DEFAULT 0,
    seasons         VARCHAR ARRAY,
    awards          VARCHAR ARRAY,
    character       VARCHAR(255) NOT NULL,
    thumbnail       TEXT,
    images          TEXT ARRAY,
    sources         TEXT ARRAY,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- Socials
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

-- Episodes
CREATE TABLE IF NOT EXISTS episodes (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid            UUID PRIMARY KEY UNIQUE NOT NULL,
    href            VARCHAR(100) NOT NULL,
    languages       VARCHAR(5) ARRAY NOT NULL,
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

-- Episodes translations
CREATE TABLE IF NOT EXISTS episodes_translations (
    id              INTEGER PRIMARY KEY UNIQUE GENERATED ALWAYS AS IDENTITY,
    episode_uuid    UUID NOT NULL,
    language        VARCHAR(5) NOT NULL DEFAULT 'en-US',
    title           VARCHAR(255) NOT NULL,
    description     TEXT NOT NULL,
    CONSTRAINT fk_episode_uuid
          FOREIGN KEY(episode_uuid)
          REFERENCES episodes(uuid)
          ON UPDATE CASCADE
          ON DELETE CASCADE
);

-- Seasons
CREATE TABLE IF NOT EXISTS seasons (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid            UUID PRIMARY KEY UNIQUE NOT NULL,
    href            VARCHAR(100) NOT NULL,
    languages       VARCHAR(5) ARRAY NOT NULL,
    duration_total  INTEGER NOT NULL,
    season_num      SMALLINT NOT NULL,
    release_date    TIMESTAMP NOT NULL,
    next_season     VARCHAR(255),
    prev_season     VARCHAR(255),
    episodes        VARCHAR ARRAY,
    soundtracks     VARCHAR(255) ARRAY,
    budget          INTEGER,
    thumbnail       TEXT,
    images          TEXT ARRAY,
    sources         TEXT ARRAY,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- Seasons translations
CREATE TABLE IF NOT EXISTS seasons_translations (
    id              INTEGER PRIMARY KEY UNIQUE GENERATED ALWAYS AS IDENTITY,
    season_uuid     UUID NOT NULL,
    language        VARCHAR(5) NOT NULL DEFAULT 'en-US',
    title           VARCHAR(255) NOT NULL,
    description     TEXT NOT NULL,
    genres          VARCHAR(50) ARRAY,
    trailers        VARCHAR ARRAY,
    CONSTRAINT fk_season_uuid
          FOREIGN KEY(season_uuid)
          REFERENCES seasons(uuid)
          ON UPDATE CASCADE
          ON DELETE CASCADE
);

-- Locations
CREATE TABLE IF NOT EXISTS locations (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid            UUID PRIMARY KEY UNIQUE NOT NULL,
    href            VARCHAR(100) NOT NULL,
    languages       VARCHAR(5) ARRAY NOT NULL,
    thumbnail       TEXT,
    images          TEXT ARRAY,
    sources         TEXT ARRAY,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- Locations translations
CREATE TABLE IF NOT EXISTS locations_translations (
    id              INTEGER PRIMARY KEY UNIQUE GENERATED ALWAYS AS IDENTITY,
    location_uuid   UUID NOT NULL,
    language        VARCHAR(5) NOT NULL DEFAULT 'en-US',
    name            VARCHAR(255) NOT NULL,
    description     TEXT NOT NULL,
    CONSTRAINT fk_location_uuid
          FOREIGN KEY(location_uuid)
          REFERENCES locations(uuid)
          ON UPDATE CASCADE
          ON DELETE CASCADE
);

-- Soundtracks
CREATE TABLE IF NOT EXISTS soundtracks (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid            UUID PRIMARY KEY UNIQUE NOT NULL,
    href            VARCHAR(100) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    artist          VARCHAR(255) NOT NULL,
    album           VARCHAR(255),
    urls            TEXT ARRAY NOT NULL,
    duration        INTEGER NOT NULL,
    release_date    TIMESTAMP NOT NULL,
    thumbnail       TEXT,
    sources         TEXT ARRAY,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- Games
CREATE TABLE IF NOT EXISTS games (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    uuid            UUID PRIMARY KEY UNIQUE NOT NULL,
    href            VARCHAR(100) NOT NULL,
    languages       VARCHAR(5) ARRAY NOT NULL,
    release_date    TIMESTAMP NOT NULL,
    website         TEXT NOT NULL,
    stores          TEXT ARRAY,
    playtime        INTEGER,
    age_rating      VARCHAR(20),
    modes           VARCHAR(20) ARRAY,
    publishers      VARCHAR(50) ARRAY,
    developers      VARCHAR(50) ARRAY,
    platforms       VARCHAR(50) ARRAY,
    genres          VARCHAR(50) ARRAY,
    tags            VARCHAR(50) ARRAY,
    thumbnail       TEXT,
    images          TEXT ARRAY,
    sources         TEXT ARRAY,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- Games translations
CREATE TABLE IF NOT EXISTS games_translations (
    id              INTEGER PRIMARY KEY UNIQUE GENERATED ALWAYS AS IDENTITY,
    game_uuid       UUID NOT NULL,
    language        VARCHAR(5) NOT NULL DEFAULT 'en-US',
    name            VARCHAR(255) NOT NULL,
    description     TEXT NOT NULL,
    trailer         TEXT NOT NULL,
    CONSTRAINT fk_game_uuid
          FOREIGN KEY(game_uuid)
          REFERENCES games(uuid)
          ON UPDATE CASCADE
          ON DELETE CASCADE
);

--
-- Triggers
--

-- # Set triggers to update column 'updated_at' every time some column is updated.

-- Overviews
CREATE TRIGGER handle_overviews_updated_at_column BEFORE
    UPDATE ON overviews
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();

-- Users
CREATE TRIGGER handle_users_updated_at_column BEFORE
    UPDATE ON users
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();

-- Characters
CREATE TRIGGER handle_characters_updated_at_column BEFORE
    UPDATE ON characters
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();

-- Actors
CREATE TRIGGER handle_actors_updated_at_column BEFORE
    UPDATE ON actors
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();

-- Episodes
CREATE TRIGGER handle_episodes_updated_at_column BEFORE
    UPDATE ON episodes
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();

-- Seasons
CREATE TRIGGER handle_seasons_updated_at_column BEFORE
    UPDATE ON seasons
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();

-- Locations
CREATE TRIGGER handle_locations_updated_at_column BEFORE
    UPDATE ON locations
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();

-- Soundtracks
CREATE TRIGGER handle_soundtracks_updated_at_column BEFORE
    UPDATE ON soundtracks
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();

-- Games
CREATE TRIGGER handle_games_updated_at_column BEFORE
    UPDATE ON games
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();

-- # Set triggers to update column 'updated_at' every time some column from '[*].translations' is updated.

-- Overviews
CREATE TRIGGER handle_overviews_child_update AFTER UPDATE ON overviews_translations
  FOR EACH ROW EXECUTE PROCEDURE handle_child_update ('overviews', 'overview_uuid');

-- Actors (Socials)
CREATE TRIGGER handle_actors_child_update AFTER UPDATE ON actors_socials
  FOR EACH ROW EXECUTE PROCEDURE handle_child_update ('actors', 'actor_uuid');

-- Episodes
CREATE TRIGGER handle_episodes_child_update AFTER UPDATE ON episodes_translations
  FOR EACH ROW EXECUTE PROCEDURE handle_child_update ('episodes', 'episode_uuid');

-- Seasons
CREATE TRIGGER handle_seasons_child_update AFTER UPDATE ON seasons_translations
  FOR EACH ROW EXECUTE PROCEDURE handle_child_update ('seasons', 'season_uuid');

-- Locations
CREATE TRIGGER handle_locations_child_update AFTER UPDATE ON locations_translations
  FOR EACH ROW EXECUTE PROCEDURE handle_child_update ('locations', 'location_uuid');

-- Games
CREATE TRIGGER handle_games_child_update AFTER UPDATE ON games_translations
  FOR EACH ROW EXECUTE PROCEDURE handle_child_update ('games', 'game_uuid');