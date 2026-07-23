INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США')
WHERE m.name_native='The Shawshank Redemption';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США')
WHERE m.name_native='The Green Mile';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США')
WHERE m.name_native='Forrest Gump';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США')
WHERE m.name_native='Schindler''s List';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('Франция')
WHERE m.name_native='Intouchables';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США', 'Великобритания')
WHERE m.name_native='Inception';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('Италия')
WHERE m.name_native='La vita è bella';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США', 'Германия')
WHERE m.name_native='Fight Club';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США')
WHERE m.name_native='Star Wars';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США')
WHERE m.name_native='Star Wars: Episode V - The Empire Strikes Back';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('Япония')
WHERE m.name_native='Sen to Chihiro no kamikakushi';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США')
WHERE m.name_native='Titanic';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США')
WHERE m.name_native='One Flew Over the Cuckoo''s Nest';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('Япония')
WHERE m.name_native='Hauru no ugoku shiro';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США', 'Великобритания')
WHERE m.name_native='Gladiator';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США', 'Великобритания')
WHERE m.name_native='Snatch';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США', 'Великобритания')
WHERE m.name_native='The Dark Knight';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США')
WHERE m.name_native='How to Train Your Dragon';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США')
WHERE m.name_native='The Silence of the Lambs';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США', 'Германия')
WHERE m.name_native='Gran Torino';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США', 'Италия', 'Испания', 'Германия', 'США')
WHERE m.name_native='Il buono, il brutto, il cattivo';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('Италия')
WHERE m.name_native='Il bisbetico domato';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('Италия')
WHERE m.name_native='Bluff storia di truffe e di imbroglioni';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США')
WHERE m.name_native='Django Unchained';

INSERT INTO movie_countries (movie_id, country_id)
SELECT m.id, c.id FROM movies m
JOIN countries c ON c.name IN ('США', 'Великобритания')
WHERE m.name_native='Dances with Wolves';