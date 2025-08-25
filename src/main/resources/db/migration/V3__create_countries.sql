CREATE TABLE countries (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

INSERT INTO countries (name) VALUES
('США'),
('Франция'),
('Великобритания'),
('Италия'),
('Германия'),
('Япония'),
('Испания')