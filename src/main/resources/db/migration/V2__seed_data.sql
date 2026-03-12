INSERT INTO customer (customer_id, name, phone, email, password, is_admin) VALUES 
('cst_12345', 'Hafiz Kurniawan', '08123456789', 'hafiz@example.com', 'password123', 'N');

INSERT INTO room_type (type_id, name, capacity) VALUES 
('rt_1', 'Meeting Room', 10);

INSERT INTO room (room_id, name, floor, type_id) VALUES 
('rm_1', 'Meeting Room A', 3, 'rt_1'),
('rm_2', 'Meeting Room B', 3, 'rt_1');

INSERT INTO booking (booking_id, status, date, customer_id, room_id) VALUES 
('book_1', 'PAID', '2026-03-25', 'cst_12345', 'rm_1'),
('book_2', 'PENDING', '2026-03-26', 'cst_12345', 'rm_2');

INSERT INTO booking_slot (slot_id, start_hour, end_hour, booking_id) VALUES 
('slot_1', '2026-03-25 09:00:00', '2026-03-25 10:00:00', 'book_1'),
('slot_2', '2026-03-25 13:00:00', '2026-03-25 15:00:00', 'book_1'),
('slot_3', '2026-03-26 10:00:00', '2026-03-26 12:00:00', 'book_2');
