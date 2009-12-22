-- MySQL dump 10.10
--
-- Host: localhost    Database: pos
-- ------------------------------------------------------
-- Server version	5.0.26-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP DATABASE IF EXISTS `pos`;
CREATE DATABASE `pos`;
USE `pos`;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
CREATE TABLE `customers` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `balance` decimal(10,2) NOT NULL default '0.00',
  `firstname` varchar(32) NOT NULL default '',
  `lastname` varchar(32) NOT NULL default '',
  `comp` tinyint(3) unsigned NOT NULL default '0',
  `renewamount` decimal(10,2) NOT NULL default '0.00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `sku` varchar(32) NOT NULL default '',
  `quantity` int(10) unsigned NOT NULL default '0',
  `name` varchar(128) NOT NULL default '',
  `price` decimal(10,2) NOT NULL default '0.00',
  `tax` decimal(3,2) NOT NULL default '0.00',
  `rentable` tinyint(3) unsigned NOT NULL default '0',
  `unlimited` tinyint(3) unsigned NOT NULL default '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `username` varchar(32) NOT NULL default '',
  `password_hash` varbinary(41) NOT NULL default '',
  `level` tinyint(4) NOT NULL default '0',
  `firstname` varchar(32) NOT NULL default '',
  `lastname` varchar(32) NOT NULL default '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin',MD5('pos'),3,'pos','admin');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notes`
--

DROP TABLE IF EXISTS `notes`;
CREATE TABLE `notes` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `customer_id` int(10) unsigned NOT NULL default '0',
  `note` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`id`),
  KEY `notes_customerid_fkey` (`customer_id`),
  CONSTRAINT `notes_customerid_fkey` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
CREATE TABLE `transactions` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `transaction_time` datetime NOT NULL default '0000-00-00 00:00:00',
  `code` char(1) NOT NULL default '',
  `user_id` int(10) unsigned NOT NULL,
  `customer_id` int(10) unsigned,
  `subtotal` decimal(10,2) NOT NULL default '0.00',
  `tax` decimal(3,2) NOT NULL default '0.00',
  PRIMARY KEY (`id`),
  KEY `transactions_cashierid_fkey` (`user_id`),
  CONSTRAINT `transactions_cashierid_fkey` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  KEY `transactions_customerid_fkey` (`customer_id`),
  CONSTRAINT `transactions_customerid_fkey` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `transaction_items`
--

DROP TABLE IF EXISTS `transaction_items`;
CREATE TABLE `transaction_items` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `transaction_id` int(10) unsigned NOT NULL,
  `sku` varchar(32) NOT NULL,
  `quantity` int(10) unsigned NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `transaction_items_transaction_id_fkey` (`transaction_id`),
  CONSTRAINT `transaction_items_transaction_id_fkey` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
