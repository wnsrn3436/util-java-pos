CREATE DATABASE IF NOT EXISTS pos DEFAULT CHARACTER SET utf8mb4;
USE pos;

CREATE TABLE item (
    itemNum INT NOT NULL AUTO_INCREMENT,
    itemName VARCHAR(50) NOT NULL,
    price INT NOT NULL,
    stock INT NOT NULL,
    PRIMARY KEY (itemNum)
);

CREATE TABLE member (
    memberNum INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL,
    tel VARCHAR(20) NOT NULL,
    nowPoint INT NOT NULL,
    accPoint INT NOT NULL,
    grade VARCHAR(10) NOT NULL,
    PRIMARY KEY (memberNum)
);

CREATE TABLE table_info (
    width INT NOT NULL,
    height INT NOT NULL,
    posX INT NOT NULL,
    posY INT NOT NULL
);

CREATE TABLE pay (
    payDate DATE NOT NULL,
    payMoney INT NOT NULL
);

CREATE VIEW sales AS
    SELECT DATE_FORMAT(payDate, '%Y-%m') AS `date`, SUM(payMoney) AS sales
    FROM pay
    GROUP BY DATE_FORMAT(payDate, '%Y-%m')
    ORDER BY `date`;
