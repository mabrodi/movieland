CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    name_russian VARCHAR(255) NOT NULL,
    name_native VARCHAR(255),
    year_of_release INT NOT NULL,
    description TEXT NOT NULL,
    rating NUMERIC(10,2),
    price NUMERIC(10,2),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);