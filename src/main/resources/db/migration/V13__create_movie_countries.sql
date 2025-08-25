CREATE TABLE movie_countries (
    movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    country_id BIGINT NOT NULL REFERENCES countries(id) ON DELETE CASCADE,
    PRIMARY KEY (movie_id, country_id)
);