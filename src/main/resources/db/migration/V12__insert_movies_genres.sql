INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('драма', 'криминал')
WHERE m.name_native='The Shawshank Redemption';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('фэнтези', 'драма', 'криминал', 'детектив')
WHERE m.name_native='The Green Mile';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('драма', 'мелодрама')
WHERE m.name_native='Forrest Gump';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('драма', 'биография')
WHERE m.name_native='Schindler''s List';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('драма', 'комедия', 'биография')
WHERE m.name_native='Intouchables';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('фантастика', 'боевик', 'триллер', 'драма', 'детектив')
WHERE m.name_native='Inception';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('драма', 'мелодрама', 'комедия')
WHERE m.name_native='La vita è bella';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('триллер', 'драма', 'криминал')
WHERE m.name_native='Fight Club';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('фантастика', 'фэнтези', 'боевик', 'приключения')
WHERE m.name_native='Star Wars';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('фантастика', 'фэнтези', 'боевик', 'приключения')
WHERE m.name_native='Star Wars: Episode V - The Empire Strikes Back';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('аниме', 'мультфильм', 'фэнтези', 'приключения', 'семейный')
WHERE m.name_native='Sen to Chihiro no kamikakushi';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('драма', 'мелодрама')
WHERE m.name_native='Titanic';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('драма')
WHERE m.name_native='One Flew Over the Cuckoo''s Nest';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('аниме', 'мультфильм', 'фэнтези', 'приключения')
WHERE m.name_native='Hauru no ugoku shiro';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('боевик', 'драма')
WHERE m.name_native='Gladiator';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('криминал', 'комедия')
WHERE m.name_native='Snatch';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('фантастика', 'боевик', 'триллер', 'криминал', 'драма')
WHERE m.name_native='The Dark Knight';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('мультфильм', 'фэнтези', 'комедия', 'приключения', 'семейный')
WHERE m.name_native='How to Train Your Dragon';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('триллер', 'криминал', 'детектив', 'драма')
WHERE m.name_native='The Silence of the Lambs';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('драма')
WHERE m.name_native='Gran Torino';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('вестерн')
WHERE m.name_native='Il buono, il brutto, il cattivo';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('комедия')
WHERE m.name_native='Il bisbetico domato';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('комедия', 'криминал')
WHERE m.name_native='Bluff storia di truffe e di imbroglioni';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('драма', 'вестерн', 'приключения', 'комедия')
WHERE m.name_native='Django Unchained';

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id FROM movies m
JOIN genres g ON g.name IN ('драма', 'приключения', 'вестерн')
WHERE m.name_native='Dances with Wolves';