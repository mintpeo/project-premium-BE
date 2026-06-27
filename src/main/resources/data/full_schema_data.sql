-- ============================================================
-- FULL DATABASE SCRIPT: tmdt
-- Tables + Seed Data (from premium DB export)
-- Compatible with JPA entities (Hibernate ddl-auto=update)
-- ============================================================

CREATE DATABASE IF NOT EXISTS `tmdt` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `tmdt`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- DROP ALL EXISTING TABLES (clean slate)
-- ============================================================
DROP TABLE IF EXISTS `seller_earnings`;
DROP TABLE IF EXISTS `seller_balances`;
DROP TABLE IF EXISTS `refund_requests`;
DROP TABLE IF EXISTS `product_keys`;
DROP TABLE IF EXISTS `coupons`;
DROP TABLE IF EXISTS `comments`;
DROP TABLE IF EXISTS `reviews`;
DROP TABLE IF EXISTS `product_types_user`;
DROP TABLE IF EXISTS `product_duration`;
DROP TABLE IF EXISTS `product_categories`;
DROP TABLE IF EXISTS `order_items`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `cart_items`;
DROP TABLE IF EXISTS `carts`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `types_user`;
DROP TABLE IF EXISTS `duration`;
DROP TABLE IF EXISTS `categories`;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 1. CATEGORIES
-- ============================================================
CREATE TABLE `categories` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `name`       VARCHAR(255) DEFAULT NULL,
  `icon`       VARCHAR(255) DEFAULT NULL,
  `is_active`  BIT(1)       DEFAULT b'1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `categories` (`id`, `name`, `icon`, `is_active`) VALUES
(1, 'Video',     'fa-solid fa-video text-red-500',        b'1'),
(3, 'Google',    'fa-brands fa-google',                   b'1'),
(4, 'Microsoft', 'fa-brands fa-windows text-[#00A4EF]',   b'1'),
(5, 'Spotify',   'fa-brands fa-spotify text-[#1DB954]',   b'1'),
(6, 'Canva',     'fa-solid fa-palette text-[#00C4CC]',    b'1'),
(7, 'AI',        'fa-solid fa-brain text-blue-500',        b'1'),
(8, 'Bảo mật',   'fa-solid fa-shield-halved text-green-500', b'1'),
(9, 'Games',     'fa-solid fa-gamepad text-green-500',     b'1');

-- ============================================================
-- 2. DURATION
-- ============================================================
CREATE TABLE `duration` (
  `id`   BIGINT       NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `duration` (`id`, `name`) VALUES
(1, '1 tháng'),
(2, '3 tháng'),
(3, '6 tháng'),
(4, '1 ngày'),
(5, '3 ngày'),
(6, '7 ngày');

-- ============================================================
-- 3. TYPES_USER
-- ============================================================
CREATE TABLE `types_user` (
  `id`   BIGINT       NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `types_user` (`id`, `name`) VALUES
(1, 'Admin'),
(2, 'Premium'),
(3, 'Pro'),
(4, 'Ultra'),
(5, 'Member');

-- ============================================================
-- 4. USERS
-- ============================================================
CREATE TABLE `users` (
  `id`               BIGINT                            NOT NULL AUTO_INCREMENT,
  `email`            VARCHAR(255)                      NOT NULL,
  `password`         VARCHAR(255)                      NOT NULL,
  `full_name`        VARCHAR(255)                      DEFAULT NULL,
  `phone_number`     VARCHAR(255)                      DEFAULT NULL,
  `role`             ENUM('CUSTOMER','SELLER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
  `seller_verified`  BIT(1)                            NOT NULL DEFAULT b'0',
  `banned`           BIT(1)                            NOT NULL DEFAULT b'0',
  `created_at`       DATETIME(6)                       DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `users` (`id`, `email`, `password`, `full_name`, `phone_number`, `role`, `seller_verified`, `banned`, `created_at`) VALUES
(1, '22130127@st.hcmuaf.edu.vn', '$2a$10$SAeuuuWMy6vedpLkanbBvukL6IbOSezqoa4mHt4hDTExGO22HRhGK', 'Nguyễn Anh Khôi', '012345678', 'CUSTOMER', b'0', b'0', NULL);

-- ============================================================
-- 5. PRODUCTS
-- ============================================================
CREATE TABLE `products` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT,
  `img`            VARCHAR(255) DEFAULT NULL,
  `name`           VARCHAR(255) DEFAULT NULL,
  `price`          INT          DEFAULT NULL,
  `price_original` INT          DEFAULT NULL,
  `rating`         DOUBLE       DEFAULT NULL,
  `sold`           INT          DEFAULT NULL,
  `seller_id`      BIGINT       DEFAULT NULL,
  `approved`       BIT(1)       NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  KEY `FK_products_seller` (`seller_id`),
  CONSTRAINT `FK_products_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `products` (`id`, `img`, `name`, `price`, `price_original`, `rating`, `sold`, `seller_id`, `approved`) VALUES
-- Video
(1,  'https://gamikey.com/wp-content/uploads/2022/03/Netflix.jpg',                                 'Tài khoản Netflix Premium',                              99000,  899000,   5, 42217, NULL, b'1'),
(3,  'https://gamikey.com/wp-content/uploads/2022/03/Image-product-5-Youtube-510x510.png.webp',    'Nâng cấp Youtube Premium chính chủ',                      359000, 599000,   5, 9525,  NULL, b'1'),
(8,  'https://gamikey.com/wp-content/uploads/2022/12/4.jpg',                                       'Tài khoản Disney Plus 01 User',                           399000, 399000,   5, 2357,  NULL, b'1'),
(9,  'https://gamikey.com/wp-content/uploads/2023/09/FPT-Play.png',                                'Nâng cấp tài khoản FPT Play chính chủ',                   49000,  259000,   5, 3254,  NULL, b'1'),
(13, 'https://placehold.co/510x510/e74c3c/white?text=HBO+Go',                                      'Tài khoản HBO Go 1 tháng',                                99000,  299000,   5, 1890,  NULL, b'1'),
(14, 'https://placehold.co/510x510/e74c3c/white?text=Apple+TV%2B',                                 'Apple TV+ 1 tháng',                                        89000,  249000,   5, 1560,  NULL, b'1'),
(15, 'https://placehold.co/510x510/e74c3c/white?text=VieON',                                       'Tài khoản VieON Premium 1 tháng',                         49000,  199000,   5, 2100,  NULL, b'1'),
(16, 'https://placehold.co/510x510/e74c3c/white?text=iQIYI',                                       'Tài khoản iQIYI Premium 1 tháng',                         69000,  199000,   5, 1230,  NULL, b'1'),

-- Canva
(2,  'https://gamikey.com/wp-content/uploads/2022/04/Untitled-3-Canva-510x510.png.webp',           'Nâng cấp tài khoản Canva 12 tháng',                       49000,  1599000,  5, 10028, NULL, b'1'),
(28, 'https://placehold.co/510x510/8B5CF6/white?text=Canva+Pro',                                     'Canva Pro 1 tháng',                                       119000, 350000,   5, 3450,  NULL, b'1'),
(29, 'https://placehold.co/510x510/8B5CF6/white?text=Canva+Teams',                                   'Canva for Teams 1 tháng',                                 299000, 699000,   5, 780,   NULL, b'1'),

-- Google
(5,  'https://gamikey.com/wp-content/uploads/2026/04/Google-one-2TB-510x511.jpg',                  'Nâng cấp bộ nhớ Google One chính chủ 12 tháng',           249000, 799000,   5, 7701,  NULL, b'1'),
(17, 'https://placehold.co/510x510/4285f4/white?text=Google+Workspace',                             'Google Workspace Business Starter 1 tháng',                199000, 699000,   5, 890,   NULL, b'1'),
(18, 'https://placehold.co/510x510/4285f4/white?text=YouTube+Music',                                'YouTube Music Premium 1 tháng',                           59000,  119000,   5, 4520,  NULL, b'1'),
(19, 'https://placehold.co/510x510/4285f4/white?text=Google+Play',                                  'Google Play 200.000 VND Gift Card',                       190000, 200000,   5, 3200,  NULL, b'1'),

-- Microsoft
(20, 'https://placehold.co/510x510/00a4ef/white?text=M365+Personal',                                 'Microsoft 365 Personal 12 tháng',                         149000, 799000,   5, 2340,  NULL, b'1'),
(21, 'https://placehold.co/510x510/00a4ef/white?text=M365+Family',                                   'Microsoft 365 Family 12 tháng',                           249000, 999000,   5, 1670,  NULL, b'1'),
(22, 'https://placehold.co/510x510/00a4ef/white?text=Xbox+GP+Core',                                  'Xbox Game Pass Core 12 tháng',                            199000, 799000,   5, 1450,  NULL, b'1'),
(23, 'https://placehold.co/510x510/00a4ef/white?text=Xbox+GP+Ultimate',                              'Xbox Game Pass Ultimate 12 tháng',                        349000, 1499000,  5, 980,   NULL, b'1'),
(24, 'https://placehold.co/510x510/00a4ef/white?text=Windows+11+Pro',                                'Windows 11 Pro License',                                  599000, 2999000,  5, 2150,  NULL, b'1'),

-- Spotify
(25, 'https://placehold.co/510x510/1db954/white?text=Spotify+Individual',                            'Spotify Premium Individual 1 tháng',                      59000,  119000,   5, 15670, NULL, b'1'),
(26, 'https://placehold.co/510x510/1db954/white?text=Spotify+Duo',                                   'Spotify Premium Duo 1 tháng',                             79000,  159000,   5, 5670,  NULL, b'1'),
(27, 'https://placehold.co/510x510/1db954/white?text=Spotify+Family',                                'Spotify Premium Family 1 tháng',                          119000, 239000,   5, 4320,  NULL, b'1'),

-- AI
(4,  'https://gamikey.com/wp-content/uploads/2025/05/Google-AI-510x510.jpg',                       'Tài khoản Google AI - Trợ lý thông minh',                 449000, 13999000, 5, 4398,  NULL, b'1'),
(11, 'https://gamikey.com/wp-content/uploads/2024/09/Grammarly-1.jpg',                             'Grammarly Premium - Kiểm tra chính tả, ngữ pháp Tiếng Anh',349000, 499000,   5, 2150,  NULL, b'1'),
(12, 'https://gamikey.com/wp-content/uploads/2023/02/gpt.png',                                     'Tài khoản ChatGPT Plus 1 tháng riêng tư',                 580000, 449000,   5, 782,   NULL, b'1'),
(30, 'https://placehold.co/510x510/6366f1/white?text=Midjourney',                                    'Midjourney Basic 1 tháng',                                450000, 599000,   5, 2340,  NULL, b'1'),
(31, 'https://placehold.co/510x510/6366f1/white?text=Claude+Pro',                                    'Claude Pro 1 tháng',                                      600000, 699000,   5, 1120,  NULL, b'1'),
(32, 'https://placehold.co/510x510/6366f1/white?text=Copilot+Pro',                                   'Copilot Pro 1 tháng',                                     450000, 599000,   5, 1890,  NULL, b'1'),
(33, 'https://placehold.co/510x510/6366f1/white?text=Gemini+Advanced',                               'Gemini Advanced 1 tháng',                                 550000, 799000,   5, 1560,  NULL, b'1'),

-- Bảo mật
(7,  'https://gamikey.com/wp-content/uploads/2022/03/Image-product-6-NordVPN.png',                 'Mua NordVPN 12 tháng',                                    399000, 159000,   5, 8009,  NULL, b'1'),
(34, 'https://placehold.co/510x510/f97316/white?text=Kaspersky',                                     'Kaspersky Internet Security 1 năm',                       299000, 899000,   5, 2340,  NULL, b'1'),
(35, 'https://placehold.co/510x510/f97316/white?text=Bitdefender',                                   'Bitdefender Total Security 1 năm',                        399000, 999000,   5, 1870,  NULL, b'1'),
(36, 'https://placehold.co/510x510/f97316/white?text=ExpressVPN',                                    'ExpressVPN 12 tháng',                                     599000, 1999000,  5, 3210,  NULL, b'1'),

-- Others
(6,  'https://gamikey.com/wp-content/uploads/2024/02/Capcut.jpg',                                  'Tài khoản Capcut Pro | Thiết kế video dễ dàng',           79000,  350000,   5, 3901,  NULL, b'1'),
(10, 'https://gamikey.com/wp-content/uploads/2022/03/Duolingo.png',                                'Nâng cấp Duolingo Super - Ứng dụng học ngoại ngữ',        299000, 359000,   5, 2925,  NULL, b'1'),

-- Games
(37, 'https://placehold.co/510x510/ef4444/white?text=Steam+200K',                                    'Steam Wallet 200.000 VND',                                190000, 200000,   5, 8900,  NULL, b'1'),
(38, 'https://placehold.co/510x510/ef4444/white?text=Steam+500K',                                    'Steam Wallet 500.000 VND',                                480000, 500000,   5, 6700,  NULL, b'1'),
(39, 'https://placehold.co/510x510/ef4444/white?text=Roblox+1000',                                   'Roblox 1000 Robux',                                       99000,  119000,   5, 12300, NULL, b'1'),
(40, 'https://placehold.co/510x510/ef4444/white?text=Roblox+2500',                                   'Roblox 2500 Robux',                                       249000, 299000,   5, 7800,  NULL, b'1'),
(41, 'https://placehold.co/510x510/ef4444/white?text=PUBG+600+UC',                                   'PUBG Mobile 600 UC',                                      69000,  79000,    5, 15600, NULL, b'1'),
(42, 'https://placehold.co/510x510/ef4444/white?text=Valorant+1000',                                 'Valorant 1000 Points',                                    99000,  119000,   5, 11200, NULL, b'1'),
(43, 'https://placehold.co/510x510/ef4444/white?text=LMHT+1380+RP',                                  'LMHT 1380 Riot Points',                                   99000,  119000,   5, 9800,  NULL, b'1'),
(44, 'https://placehold.co/510x510/ef4444/white?text=Minecraft+Java',                                'Minecraft: Java Edition',                                 249000, 499000,   5, 14500, NULL, b'1'),
(45, 'https://placehold.co/510x510/ef4444/white?text=FreeFire+1000',                                 'Free Fire 1000 Kim Cương',                                69000,  89000,    5, 20300, NULL, b'1');

-- ============================================================
-- 6. PRODUCT_CATEGORIES
-- ============================================================
CREATE TABLE `product_categories` (
  `id`          BIGINT NOT NULL AUTO_INCREMENT,
  `product_id`  BIGINT DEFAULT NULL,
  `category_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_prod_cat_products`   (`product_id`),
  KEY `FK_prod_cat_categories` (`category_id`),
  CONSTRAINT `FK_prod_cat_products`   FOREIGN KEY (`product_id`)  REFERENCES `products`  (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_prod_cat_categories` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `product_categories` (`id`, `product_id`, `category_id`) VALUES
-- Video (category 1)
(1,  1,  1),
(8,  8,  1),
(9,  9,  1),
(10, 3,  1),
(13, 13, 1),
(14, 14, 1),
(15, 15, 1),
(16, 16, 1),

-- Google (category 3)
(5,  5,  3),
(17, 17, 3),
(18, 18, 3),
(19, 19, 3),

-- Microsoft (category 4)
(21, 20, 4),
(22, 21, 4),
(23, 22, 4),
(24, 23, 4),
(25, 24, 4),

-- Spotify (category 5)
(26, 25, 5),
(27, 26, 5),
(28, 27, 5),

-- Canva (category 6)
(29, 2,  6),
(30, 28, 6),
(31, 29, 6),

-- AI (category 7)
(4,  4,  7),
(11, 11, 7),
(12, 12, 7),
(32, 30, 7),
(33, 31, 7),
(34, 32, 7),
(35, 33, 7),

-- Bảo mật (category 8)
(7,  7,  8),
(36, 34, 8),
(37, 35, 8),
(38, 36, 8),

-- Games (category 9)
(39, 37, 9),
(40, 38, 9),
(41, 39, 9),
(42, 40, 9),
(43, 41, 9),
(44, 42, 9),
(45, 43, 9),
(46, 44, 9),
(47, 45, 9),

-- Others
(6,  6, 1);

-- ============================================================
-- 7. PRODUCT_DURATION
-- ============================================================
CREATE TABLE `product_duration` (
  `id`          BIGINT NOT NULL AUTO_INCREMENT,
  `product_id`  BIGINT DEFAULT NULL,
  `duration_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_prod_dur_products` (`product_id`),
  KEY `FK_prod_dur_duration` (`duration_id`),
  CONSTRAINT `FK_prod_dur_products` FOREIGN KEY (`product_id`)  REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_prod_dur_duration` FOREIGN KEY (`duration_id`) REFERENCES `duration` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `product_duration` (`id`, `product_id`, `duration_id`) VALUES
(1, 2,  2),
(2, 2,  3),
(3, 3,  1),
(4, 3,  3),
(5, 6,  1),
(6, 6,  2),
(7, 6,  3),
(8, 6,  6),
(9, 9,  1),
(10, 14, 1),
(11, 15, 1),
(12, 16, 1),
(13, 18, 1),
(14, 25, 1),
(15, 26, 1),
(16, 27, 1),
(17, 28, 1),
(18, 28, 2),
(19, 28, 3),
(20, 29, 1),
(21, 29, 2),
(22, 29, 3),
(23, 30, 1),
(24, 31, 1),
(25, 32, 1),
(26, 33, 1);

-- ============================================================
-- 8. PRODUCT_TYPES_USER
-- ============================================================
CREATE TABLE `product_types_user` (
  `id`           BIGINT NOT NULL AUTO_INCREMENT,
  `product_id`   BIGINT DEFAULT NULL,
  `type_user_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_prod_type_products`  (`product_id`),
  KEY `FK_prod_type_typesuser` (`type_user_id`),
  CONSTRAINT `FK_prod_type_products`  FOREIGN KEY (`product_id`)   REFERENCES `products`  (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_prod_type_typesuser` FOREIGN KEY (`type_user_id`) REFERENCES `types_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `product_types_user` (`id`, `product_id`, `type_user_id`) VALUES
(1, 1, 2),
(2, 2, 1),
(3, 2, 5),
(4, 4, 3),
(5, 4, 4);

-- ============================================================
-- 9. CARTS
-- ============================================================
CREATE TABLE `carts` (
  `id`      BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_carts_user` (`user_id`),
  CONSTRAINT `FK_carts_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `carts` (`id`, `user_id`) VALUES (1, 1);

-- ============================================================
-- 10. CART_ITEMS
-- ============================================================
CREATE TABLE `cart_items` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `cart_id`    BIGINT       DEFAULT NULL,
  `product_id` BIGINT       DEFAULT NULL,
  `quantity`   INT          DEFAULT NULL,
  `duration`   VARCHAR(255) DEFAULT NULL,
  `type_user`  VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_cartitems_carts`    (`cart_id`),
  KEY `FK_cartitems_products` (`product_id`),
  CONSTRAINT `FK_cartitems_carts`    FOREIGN KEY (`cart_id`)    REFERENCES `carts`    (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_cartitems_products` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================================
-- 11. ORDERS
-- ============================================================
CREATE TABLE `orders` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT,
  `user_id`         BIGINT       DEFAULT NULL,
  `full_name`       VARCHAR(255) DEFAULT NULL,
  `phone_number`    VARCHAR(255) DEFAULT NULL,
  `note`            VARCHAR(255) DEFAULT NULL,
  `order_date`      DATETIME(6)  DEFAULT NULL,
  `order_status`    VARCHAR(255) DEFAULT NULL,
  `payment_method`  VARCHAR(255) DEFAULT NULL,
  `payment_status`  VARCHAR(255) DEFAULT NULL,
  `total_price`     INT          DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_orders_users` (`user_id`),
  CONSTRAINT `FK_orders_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `orders` (`id`, `user_id`, `full_name`, `note`, `order_date`, `order_status`, `payment_method`, `payment_status`, `phone_number`, `total_price`) VALUES
(4,  1, '', '', '2026-05-19 18:44:11.000000', 'CANCELLED', 'bank', 'PENDING', '', 49000),
(5,  1, '', '', '2026-05-19 18:44:37.000000', 'SUCCESS',   'bank', 'PAID',    '', 2000),
(6,  1, '', '', '2026-05-19 18:45:37.000000', 'CANCELLED', 'bank', 'PENDING', '', 359000),
(7,  1, '', '', '2026-05-19 18:46:23.000000', 'CANCELLED', 'bank', 'PENDING', '', 49000),
(8,  1, '', '', '2026-05-19 18:51:17.000000', 'SUCCESS',   'bank', 'PAID',    '', 24900),
(9,  1, '', '', '2026-05-24 16:24:08.000000', 'CANCELLED', 'bank', 'PENDING', '', 4900),
(10, 1, '', '', '2026-05-24 17:56:44.000000', 'CANCELLED', 'bank', 'PENDING', '', 4900),
(11, 1, '', '', '2026-05-24 18:00:18.000000', 'CANCELLED', 'bank', 'PENDING', '', 20000),
(12, 1, '', '', '2026-05-24 18:01:51.000000', 'CANCELLED', 'bank', 'PENDING', '', 79000),
(177964609908791, 1, '', '', '2026-05-24 18:08:19.000000', 'CANCELLED', 'bank', 'PENDING', '', 4900),
(177964626624746, 1, '', '', '2026-05-24 18:11:06.000000', 'CANCELLED', 'bank', 'PENDING', '', 299000),
(177964654424802, 1, '', '', '2026-05-24 18:15:44.000000', 'CANCELLED', 'bank', 'PENDING', '', 79000),
(177964660425105, 1, '', '', '2026-05-24 18:16:44.000000', 'CANCELLED', 'bank', 'PENDING', '', 399000),
(177964664120372, 1, '', '', '2026-05-24 18:17:21.000000', 'CANCELLED', 'bank', 'PENDING', '', 399000),
(177964674118947, 1, '', '', '2026-05-24 18:19:01.000000', 'CANCELLED', 'bank', 'PENDING', '', 249000),
(177964724775111, 1, '', '', '2026-05-24 18:27:27.000000', 'SUCCESS',   'bank', 'PAID',    '', 20000),
(178000741236281, 1, '', '', '2026-05-28 22:30:12.000000', 'CANCELLED', 'bank', 'PENDING', '', 99000),
(178000745137146, 1, 'ádsad', 'ádsadas', '2026-05-28 22:30:51.000000', 'CANCELLED', 'bank', 'PENDING', 'đasa', 359000);

-- ============================================================
-- 12. ORDER_ITEMS
-- ============================================================
CREATE TABLE `order_items` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `order_id`   BIGINT       DEFAULT NULL,
  `product_id` BIGINT       DEFAULT NULL,
  `quantity`   INT          DEFAULT NULL,
  `price`      INT          DEFAULT NULL,
  `duration`   VARCHAR(255) DEFAULT NULL,
  `type_user`  VARCHAR(255) DEFAULT NULL,
  `key_code`   VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_orderitems_orders`   (`order_id`),
  KEY `FK_orderitems_products` (`product_id`),
  CONSTRAINT `FK_orderitems_orders`   FOREIGN KEY (`order_id`)   REFERENCES `orders`   (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_orderitems_products` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `order_items` (`id`, `order_id`, `product_id`, `quantity`, `price`, `duration`, `type_user`, `key_code`) VALUES
(4,  4,                  2,  1, 49000,  '3 tháng', 'Admin', NULL),
(5,  5,                  1,  1, 2000,   '',        'Premium', 'ACC-1-1779216320565'),
(6,  6,                  3,  1, 359000, '1 tháng', '', NULL),
(7,  7,                  2,  1, 49000,  '3 tháng', 'Admin', NULL),
(8,  8,                  2,  1, 4900,   '3 tháng', 'Admin', 'ACC-2-1779216755517'),
(9,  8,                  1,  1, 20000,  '',        '', 'ACC-1-1779216755517'),
(10, 9,                  2,  1, 4900,   '3 tháng', 'Admin', NULL),
(11, 10,                 2,  1, 4900,   '3 tháng', 'Admin', NULL),
(12, 11,                 1,  1, 20000,  '',        '', NULL),
(13, 12,                 6,  1, 79000,  '',        '', NULL),
(14, 177964609908791,    2,  1, 4900,   '',        '', NULL),
(15, 177964626624746,    10, 1, 299000, '',        '', NULL),
(16, 177964654424802,    6,  1, 79000,  '',        '', NULL),
(17, 177964660425105,    7,  1, 399000, '',        '', NULL),
(18, 177964664120372,    8,  1, 399000, '',        '', NULL),
(19, 177964674118947,    5,  1, 249000, '',        '', NULL),
(20, 177964724775111,    1,  1, 20000,  '',        'Premium', 'ACC-1-1779647284282'),
(21, 178000741236281,    1,  1, 99000,  '',        '', NULL),
(22, 178000745137146,    3,  1, 359000, '',        '', NULL);

-- ============================================================
-- 13. REVIEWS
-- ============================================================
CREATE TABLE `reviews` (
  `id`         BIGINT   NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT   NOT NULL,
  `user_id`    BIGINT   NOT NULL,
  `stars`      INT      NOT NULL,
  `content`    TEXT     NOT NULL,
  `created_at` DATETIME(6) DEFAULT NULL,
  `approved`   BIT(1)  NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  KEY `FK_reviews_users` (`user_id`),
  CONSTRAINT `FK_reviews_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `reviews` (`id`, `product_id`, `user_id`, `stars`, `content`, `created_at`, `approved`) VALUES
(1,  1,  1, 5, 'Tài khoản dùng siêu mượt, chủ shop hỗ trợ giao hàng nhanh trong vòng 5 phút, chất lượng 4K chuẩn đét.',            '2026-05-15 14:20:00.000000', b'1'),
(2,  1,  1, 5, 'Giá rẻ hơn mua trực tiếp rất nhiều, xem phim không bị giật lag hay văng profile gì luôn, uy tín ạ.',              '2026-05-16 09:35:00.000000', b'1'),
(3,  2,  1, 5, 'Mua gói Canva thiết kế bài thuyết trình nhóm trên trường bao đỉnh, nhấn link vào nhóm Premium cái rụp.',          '2026-05-17 11:10:00.000000', b'1'),
(4,  3,  1, 5, 'Xem YouTube không còn một cái quảng cáo nào, nghe nhạc tắt màn hình mượt mà, đáng tiền nha mọi người.',            '2026-05-17 16:45:00.000000', b'1'),
(5,  10, 1, 4, 'Học ngoại ngữ bằng Duolingo Super không bị giới hạn tim, không quảng cáo, học cuốn hẳn lên.',                       '2026-05-18 07:15:00.000000', b'1'),
(6,  11, 1, 5, 'Tài khoản giá rẻ',                                                                                                 NULL, b'1'),
(7,  3,  1, 5, 'sản phẩm tốt',                                                                                                      NULL, b'1'),
(8,  3,  1, 5, 'sản phẩm tốt',                                                                                                      NULL, b'1'),
(9,  1,  1, 5, 'sản phẩm tốt',                                                                                                      NULL, b'1'),
(10, 1,  1, 5, '123',                                                                                                                NULL, b'1');

-- ============================================================
-- 14. COMMENTS
-- ============================================================
CREATE TABLE `comments` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT       NOT NULL,
  `user_id`    BIGINT       NOT NULL,
  `content`    TEXT         NOT NULL,
  `parent_id`  BIGINT       DEFAULT NULL,
  `created_at` DATETIME(6)  DEFAULT CURRENT_TIMESTAMP(6),
  `approved`   BIT(1)       NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  KEY `FK_comments_users` (`user_id`),
  CONSTRAINT `FK_comments_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================================
-- 15. COUPONS
-- ============================================================
CREATE TABLE `coupons` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT,
  `code`            VARCHAR(255) NOT NULL,
  `discount_type`   VARCHAR(255) NOT NULL,
  `discount_value`  INT          NOT NULL,
  `min_order_value` INT          DEFAULT NULL,
  `max_uses`        INT          DEFAULT NULL,
  `used_count`      INT          NOT NULL DEFAULT 0,
  `expiry_date`     DATETIME(6)  DEFAULT NULL,
  `is_active`       BIT(1)       DEFAULT b'1',
  `created_at`      DATETIME(6)  DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_coupons_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================================
-- 16. PRODUCT_KEYS
-- ============================================================
CREATE TABLE `product_keys` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT,
  `product_id`   BIGINT       NOT NULL,
  `key_code`     VARCHAR(255) NOT NULL,
  `is_sold`      BIT(1)       NOT NULL DEFAULT b'0',
  `order_item_id` BIGINT      DEFAULT NULL,
  `created_at`   DATETIME(6)  DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  KEY `FK_prodkeys_product`   (`product_id`),
  KEY `FK_prodkeys_orderitem` (`order_item_id`),
  CONSTRAINT `FK_prodkeys_product`   FOREIGN KEY (`product_id`)    REFERENCES `products`   (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_prodkeys_orderitem` FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================================
-- 17. REFUND_REQUESTS
-- ============================================================
CREATE TABLE `refund_requests` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT,
  `order_id`     BIGINT       NOT NULL,
  `user_id`     BIGINT       NOT NULL,
  `reason`       TEXT         NOT NULL,
  `status`       VARCHAR(255) NOT NULL DEFAULT 'PENDING',
  `admin_note`   TEXT         DEFAULT NULL,
  `created_at`   DATETIME(6)  DEFAULT CURRENT_TIMESTAMP(6),
  `processed_at` DATETIME(6)  DEFAULT NULL,
  `processed_by` BIGINT       DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_refund_order`    (`order_id`),
  KEY `FK_refund_user`    (`user_id`),
  KEY `FK_refund_processed` (`processed_by`),
  CONSTRAINT `FK_refund_order`    FOREIGN KEY (`order_id`)     REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_refund_user`     FOREIGN KEY (`user_id`)     REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_refund_processed` FOREIGN KEY (`processed_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================================
-- 18. SELLER_BALANCES
-- ============================================================
CREATE TABLE `seller_balances` (
  `id`               BIGINT      NOT NULL AUTO_INCREMENT,
  `seller_id`        BIGINT      NOT NULL,
  `pending_amount`   BIGINT      NOT NULL DEFAULT 0,
  `available_amount` BIGINT      NOT NULL DEFAULT 0,
  `total_earned`     BIGINT      NOT NULL DEFAULT 0,
  `created_at`       DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at`       DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_seller_balances_seller` (`seller_id`),
  CONSTRAINT `FK_seller_balances_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================================
-- 19. SELLER_EARNINGS
-- ============================================================
CREATE TABLE `seller_earnings` (
  `id`          BIGINT      NOT NULL AUTO_INCREMENT,
  `seller_id`   BIGINT      NOT NULL,
  `order_id`    BIGINT      NOT NULL,
  `product_id`  BIGINT      NOT NULL,
  `amount`      BIGINT      NOT NULL,
  `status`      VARCHAR(255) NOT NULL DEFAULT 'PENDING',
  `created_at`  DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
  `reviewed_at` DATETIME(6) DEFAULT NULL,
  `reviewed_by` BIGINT      DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_earnings_seller`   (`seller_id`),
  KEY `FK_earnings_order`    (`order_id`),
  KEY `FK_earnings_product`  (`product_id`),
  KEY `FK_earnings_reviewer` (`reviewed_by`),
  CONSTRAINT `FK_earnings_seller`   FOREIGN KEY (`seller_id`)   REFERENCES `users`    (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_earnings_order`    FOREIGN KEY (`order_id`)    REFERENCES `orders`   (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_earnings_product`  FOREIGN KEY (`product_id`)  REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_earnings_reviewer` FOREIGN KEY (`reviewed_by`) REFERENCES `users`    (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- FIX AUTO_INCREMENT VALUES
-- ============================================================
ALTER TABLE `categories`          AUTO_INCREMENT = 12;
ALTER TABLE `duration`            AUTO_INCREMENT = 7;
ALTER TABLE `types_user`          AUTO_INCREMENT = 6;
ALTER TABLE `users`               AUTO_INCREMENT = 2;
ALTER TABLE `products`            AUTO_INCREMENT = 46;
ALTER TABLE `product_categories`  AUTO_INCREMENT = 48;
ALTER TABLE `product_duration`    AUTO_INCREMENT = 27;
ALTER TABLE `product_types_user`  AUTO_INCREMENT = 6;
ALTER TABLE `carts`               AUTO_INCREMENT = 2;
ALTER TABLE `cart_items`          AUTO_INCREMENT = 65;
ALTER TABLE `orders`              AUTO_INCREMENT = 178000745137147;
ALTER TABLE `order_items`         AUTO_INCREMENT = 23;
ALTER TABLE `reviews`             AUTO_INCREMENT = 11;
