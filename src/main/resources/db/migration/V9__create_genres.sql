CREATE TABLE genres (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

INSERT INTO genres (name) VALUES
('драма'),
('криминал'),
('фэнтези'),
('детектив'),
('мелодрама'),
('биография'),
('комедия'),
('фантастика'),
('боевик'),
('триллер'),
('приключения'),
('аниме'),
('мультфильм'),
('семейный'),
('вестерн');