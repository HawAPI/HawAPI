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
-- Socials
--
CREATE TABLE IF NOT EXISTS socials (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(50) NOT NULL,
    url TEXT NOT NULL,
    actors_id INTEGER NOT NULL,
    CONSTRAINT fk_actors
          FOREIGN KEY(actors_id)
          REFERENCES actors(id)
          ON UPDATE CASCADE
          ON DELETE CASCADE;
)

--
-- Characters
-- Gender ref: ISO/IEC 5218
--
CREATE TABLE IF NOT EXISTS characters (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uuid UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    href VARCHAR(100) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    nicknames VARCHAR ARRAY,
    birth_date TIMESTAMP,
    death_date TIMESTAMP,
    gender SMALLINT NOT NULL DEFAULT 0;
    thumbnail TEXT,
    actor VARCHAR(100) NOT NULL,
    images VARCHAR ARRAY,
    sources VARCHAR ARRAY,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TRIGGER handle_updated_at_field BEFORE
    UPDATE ON characters
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_field();

--
-- Actors
--
CREATE TABLE IF NOT EXISTS actors (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uuid UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    href VARCHAR(100) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    nicknames VARCHAR ARRAY,
    birth_date TIMESTAMP,
    death_date TIMESTAMP,
    gender SMALLINT NOT NULL DEFAULT 0;
    nationality VARCHAR(50),
    episodes VARCHAR ARRAY,
    seasons VARCHAR ARRAY,
    awards VARCHAR ARRAY,
    socials_id INTEGER FOREIGN KEY,
    character VARCHAR(255) NOT NULL,
    thumbnail TEXT,
    images VARCHAR ARRAY,
    sources VARCHAR ARRAY,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TRIGGER handle_updated_at_field BEFORE
    UPDATE ON characters
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_field();

--
-- Episodes
--
CREATE TABLE IF NOT EXISTS episodes (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uuid UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    href VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    duration INTEGER NOT NULL,
    episode_num SMALLINT NOT NULL,
    next_episode VARCHAR(255),
    prev_episode VARCHAR(255);
    season VARCHAR(255) NOT NULL,
    trailers VARCHAR ARRAY,
    thumbnail TEXT,
    images VARCHAR ARRAY,
    sources VARCHAR ARRAY,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TRIGGER handle_updated_at_field BEFORE
    UPDATE ON episodes
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_field();

--
-- Seasons
--
CREATE TABLE IF NOT EXISTS seasons (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uuid UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    href VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    duration_total INTEGER NOT NULL,
    tags VARCHAR ARRAY,
    season_num SMALLINT NOT NULL,
    release_date TIMESTAMP,
    next_season VARCHAR(255),
    prev_season VARCHAR(255),
    episodes VARCHAR ARRAY,
    trailers VARCHAR ARRAY,
    thumbnail TEXT,
    images VARCHAR ARRAY,
    sources VARCHAR ARRAY,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TRIGGER handle_updated_at_field BEFORE
    UPDATE ON seasons
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_field();

--
-- Places
--
CREATE TABLE IF NOT EXISTS places (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uuid UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    href VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    thumbnail TEXT,
    images VARCHAR ARRAY,
    sources VARCHAR ARRAY,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TRIGGER handle_updated_at_field BEFORE
    UPDATE ON places
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_field();
