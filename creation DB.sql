CREATE TABLE buildings (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT,
	floors INT NOT NULL
);
CREATE TABLE rooms (
    id SERIAL PRIMARY KEY,
    building_id INT REFERENCES buildings(id) ON DELETE CASCADE,
    number INT NOT NULL,
    floor INT NOT NULL,
    sleeping_places INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
	picture VARCHAR(255),
    description TEXT NOT NULL
);
CREATE TABLE superusers (
	id SERIAL PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    last_log_in TIMESTAMP
);
CREATE TABLE administrators (
    id SERIAL PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
	full_name VARCHAR(255) NOT NULL,
	phone VARCHAR(50) NOT NULL,
	creation_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_log_in TIMESTAMP
);
CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
	password VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    name VARCHAR(255)NOT NULL,
    is_blocked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_log_in TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    client_id INT REFERENCES clients(id) ON DELETE CASCADE,
    room_id INT REFERENCES rooms(id) ON DELETE CASCADE,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL
);
CREATE TABLE guests (
    id SERIAL PRIMARY KEY,
    booking_id INT REFERENCES bookings(id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    series_and_number VARCHAR(20) NOT NULL
);