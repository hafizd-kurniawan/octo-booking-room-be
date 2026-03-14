CREATE TABLE IF NOT EXISTS customer (
  customer_id VARCHAR(255) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  phone VARCHAR(20) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  is_admin CHAR(1) NOT NULL DEFAULT 'N'
);

CREATE TABLE IF NOT EXISTS room_type (
  type_id VARCHAR(255) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  capacity INT NOT NULL
);

CREATE TABLE IF NOT EXISTS room (
  room_id VARCHAR(255) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  floor INT NOT NULL,
  type_id VARCHAR(255) NOT NULL,
  FOREIGN KEY (type_id) REFERENCES room_type(type_id)
);

CREATE TABLE IF NOT EXISTS booking (
  booking_id VARCHAR(255) PRIMARY KEY,
  status VARCHAR(255) NOT NULL DEFAULT 'BOOKED',
  date DATE NOT NULL,
  customer_id VARCHAR(255) NOT NULL,
  room_id VARCHAR(255) NOT NULL,
  FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
  FOREIGN KEY (room_id) REFERENCES room(room_id)
);

CREATE TABLE IF NOT EXISTS booking_slot (
  slot_id VARCHAR(255) PRIMARY KEY,
  start_hour DATETIME NOT NULL,
  end_hour DATETIME NOT NULL,
  booking_id VARCHAR(255) NOT NULL,
  FOREIGN KEY (booking_id) REFERENCES booking(booking_id)
);

CREATE TABLE token_blacklist (
    id VARCHAR(36) PRIMARY KEY,
    token TEXT,
    expired_at TIMESTAMP
);
