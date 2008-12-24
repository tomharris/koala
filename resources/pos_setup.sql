-- MySQL dump 10.9
--
-- Host: localhost    Database: pos
-- ------------------------------------------------------
-- Server version	4.1.14-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES latin1 */;
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
  `customerid` int(10) unsigned NOT NULL auto_increment,
  `balance` float NOT NULL default '0',
  `firstname` varchar(32) NOT NULL default '',
  `lastname` varchar(32) NOT NULL default '',
  `comp` tinyint(3) unsigned NOT NULL default '0',
  `renewamount` float NOT NULL default '0',
  UNIQUE KEY `customerid` (`customerid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory` (
  `sku` varchar(32) NOT NULL default '',
  `quantity` int(10) unsigned NOT NULL default '0',
  `name` varchar(128) NOT NULL default '',
  `price` float NOT NULL default '0',
  `tax` float NOT NULL default '0',
  `rentable` tinyint(3) unsigned NOT NULL default '0',
  `unlimited` tinyint(3) unsigned NOT NULL default '0',
  UNIQUE KEY `sku` (`sku`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `inventory`
--


/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
LOCK TABLES `inventory` WRITE;
INSERT INTO `inventory` VALUES ('1',0,'Small Drink',0.6,0,0,1),('2',23,'Cheetos',1,0,0,0),('3',43,'Milky Way',0.6,0,0,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;

--
-- Table structure for table `notes`
--

DROP TABLE IF EXISTS `notes`;
CREATE TABLE `notes` (
  `customerid` int(10) unsigned NOT NULL default '0',
  `note` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`customerid`),
  UNIQUE KEY `noteid` (`customerid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
CREATE TABLE `transactions` (
  `transnum` int(10) unsigned NOT NULL auto_increment,
  `transtime` datetime NOT NULL default '0000-00-00 00:00:00',
  `code` char(1) NOT NULL default '',
  `cashierid` int(10) unsigned NOT NULL default '0',
  `customerid` int(10) unsigned NOT NULL default '0',
  `subtotal` float NOT NULL default '0',
  `tax` float NOT NULL default '0',
  PRIMARY KEY  (`transnum`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `userlog`
--

DROP TABLE IF EXISTS `userlog`;
CREATE TABLE `userlog` (
  `userid` int(10) unsigned NOT NULL default 0,
  `action` varchar(128) NOT NULL default ''
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `userid` int(10) unsigned NOT NULL auto_increment,
  `username` varchar(32) NOT NULL default '',
  `password` varbinary(41) NOT NULL default '',
  `level` tinyint(4) NOT NULL default '0',
  `firstname` varchar(32) NOT NULL default '',
  `lastname` varchar(32) NOT NULL default '',
  UNIQUE KEY `userid` (`userid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--


/*!40000 ALTER TABLE `users` DISABLE KEYS */;
LOCK TABLES `users` WRITE;
INSERT INTO `users` VALUES (1,'manager',PASSWORD('pos'),2,'pos','manager'),(2,'admin',PASSWORD('pos'),3,'pos','admin'),(3,'cashier',PASSWORD('pos'),1,'pos','cashier');
UNLOCK TABLES;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
