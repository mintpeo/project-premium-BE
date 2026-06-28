-- USERS (admin + seller for product references)
INSERT INTO users (email, password, full_name, phone_number, role, created_at, seller_verified, banned) VALUES
('admin@premium.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'Admin', '0900000001', 'ADMIN', NOW(), false, false),
('seller@premium.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'Seller', '0900000002', 'SELLER', NOW(), true, false);

-- CATEGORIES
INSERT INTO categories (name, icon, is_active) VALUES
('Netflix', 'netflix', true),
('Adobe', 'adobe', true),
('Google', 'google', true),
('Microsoft', 'microsoft', true),
('Spotify', 'spotify', true),
('Canva', 'canva', true),
('AI', 'ai', true),
('Bảo Mật', 'bao-mat', true),
('Games', 'games', true);

-- DURATIONS
INSERT INTO duration (name) VALUES
('1 tháng'), ('3 tháng'), ('6 tháng'), ('12 tháng');

-- TYPE USERS
INSERT INTO types_user (name) VALUES
('Cá nhân'), ('Gia đình'), ('Premium'), ('Ultra'), ('Standard'), ('Pro'), ('Business');

-- PRODUCTS
INSERT INTO products (img, rating, sold, name, price_original, price, seller_id, approved) VALUES
('/products/watch.png', 4.8, 1250, 'Netflix Premium - 4K Ultra HD', 299000, 169000, 2, true),
('/products/perfume.png', 4.7, 980, 'Spotify Premium - Cá nhân', 159000, 89000, 2, true),
('/products/bag.png', 4.9, 750, 'Microsoft Office 365 Personal', 1499000, 499000, 2, true),
('/products/overcoat.png', 4.8, 620, 'Adobe Creative Cloud All Apps', 2999000, 899000, 2, true),
('/products/google-one.png', 4.6, 430, 'Google One 2TB', 499000, 249000, 2, true),
('/products/capcut-pro.png', 4.7, 560, 'Canva Pro', 699000, 199000, 2, true),
('/products/super-duolingo.png', 4.9, 890, 'ChatGPT Plus', 599000, 299000, 2, true),
('/products/shoes.png', 4.5, 340, 'NordVPN - 2 năm', 2999000, 799000, 2, true),
('/products/dress.png', 4.8, 2100, 'Steam Wallet 50$', 1500000, 1190000, 2, true),
('/products/youtube-premium.png', 4.7, 670, 'YouTube Premium - Cá nhân', 179000, 99000, 2, true);

-- PRODUCT-CATEGORY
INSERT INTO product_categories (product_id, category_id) VALUES
(1, 1), (2, 5), (3, 4), (4, 2), (5, 3),
(6, 6), (7, 7), (8, 8), (9, 9), (10, 5);

-- PRODUCT-DURATION
INSERT INTO product_duration (product_id, duration_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4),
(2, 1), (2, 2), (2, 3), (2, 4),
(3, 1), (3, 2), (3, 3), (3, 4),
(4, 1), (4, 2), (4, 3), (4, 4),
(5, 1), (5, 2), (5, 3), (5, 4),
(6, 1), (6, 2), (6, 3), (6, 4),
(7, 1), (7, 2), (7, 3), (7, 4),
(8, 1), (8, 2), (8, 3), (8, 4),
(9, 1),
(10, 1), (10, 2), (10, 3), (10, 4);

-- PRODUCT-TYPE USER
INSERT INTO product_types_user (product_id, type_user_id) VALUES
(1, 3), (1, 4),
(2, 1), (2, 2),
(3, 1), (3, 6),
(4, 6), (4, 7),
(5, 1),
(6, 6), (6, 7),
(7, 3),
(8, 1),
(9, 1),
(10, 1), (10, 2);
