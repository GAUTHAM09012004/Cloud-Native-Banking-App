CREATE TABLE IF NOT EXISTS `customers`
(
    `customer_id`   int AUTO_INCREMENT PRIMARY KEY,
    `name`          varchar(100) NOT NULL,
    `email`         varchar(100) NOT NULL,
    `mobile_number` varchar(20)  NOT NULL

);
CREATE TABLE IF NOT EXISTS `accounts`
(
    `customer_id`    int          NOT NULL,
    `account_number` int AUTO_INCREMENT PRIMARY KEY,
    `account_type`   varchar(100) NOT NULL,
    `branch_address` varchar(200) NOT NULL
);
