CREATE TABLE participant (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    is_confirmed BOOLEAN NOT NULL,
    trip_id UUID NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES trips(id)
);