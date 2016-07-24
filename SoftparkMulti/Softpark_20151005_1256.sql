CREATE DATABASE  IF NOT EXISTS `Softpark` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `Softpark`;
-- MySQL dump 10.13  Distrib 5.6.24, for Win64 (x86_64)
--
-- Host: localhost    Database: Softpark
-- ------------------------------------------------------
-- Server version	5.6.26-log

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

--
-- Table structure for table `AccessTypes`
--

DROP TABLE IF EXISTS `AccessTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AccessTypes` (
  `Id` int(11) NOT NULL,
  `Name` varchar(50) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AccessTypes`
--

LOCK TABLES `AccessTypes` WRITE;
/*!40000 ALTER TABLE `AccessTypes` DISABLE KEYS */;
INSERT INTO `AccessTypes` VALUES (0,'Denied'),(1,'Granted'),(2,'RequiresAuthorization');
/*!40000 ALTER TABLE `AccessTypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `BlackList`
--

DROP TABLE IF EXISTS `BlackList`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `BlackList` (
  `BranchId` smallint(6) NOT NULL DEFAULT '101',
  `Cardid` bigint(20) NOT NULL,
  `DateIn` datetime NOT NULL,
  `SupervisorId` smallint(6) NOT NULL,
  `Reason` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`Cardid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `BlackList`
--

LOCK TABLES `BlackList` WRITE;
/*!40000 ALTER TABLE `BlackList` DISABLE KEYS */;
/*!40000 ALTER TABLE `BlackList` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CarBrands`
--

DROP TABLE IF EXISTS `CarBrands`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CarBrands` (
  `Id` smallint(6) NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CarBrands`
--

LOCK TABLES `CarBrands` WRITE;
/*!40000 ALTER TABLE `CarBrands` DISABLE KEYS */;
/*!40000 ALTER TABLE `CarBrands` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CarModels`
--

DROP TABLE IF EXISTS `CarModels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CarModels` (
  `Id` smallint(6) NOT NULL AUTO_INCREMENT,
  `CarBrandId` smallint(6) NOT NULL,
  `Name` varchar(50) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CarModels`
--

LOCK TABLES `CarModels` WRITE;
/*!40000 ALTER TABLE `CarModels` DISABLE KEYS */;
/*!40000 ALTER TABLE `CarModels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CardModification`
--

DROP TABLE IF EXISTS `CardModification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CardModification` (
  `LoginId` int(11) NOT NULL,
  `CardId` bigint(20) NOT NULL,
  `Field` varchar(30) NOT NULL,
  `OldValue` varchar(50) NOT NULL,
  `NewValue` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CardModification`
--

LOCK TABLES `CardModification` WRITE;
/*!40000 ALTER TABLE `CardModification` DISABLE KEYS */;
/*!40000 ALTER TABLE `CardModification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CardSubTypes`
--

DROP TABLE IF EXISTS `CardSubTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CardSubTypes` (
  `Id` tinyint(4) NOT NULL,
  `TypeId` tinyint(4) NOT NULL,
  `Name` varchar(50) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CardSubTypes`
--

LOCK TABLES `CardSubTypes` WRITE;
/*!40000 ALTER TABLE `CardSubTypes` DISABLE KEYS */;
/*!40000 ALTER TABLE `CardSubTypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CardTypes`
--

DROP TABLE IF EXISTS `CardTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CardTypes` (
  `Id` tinyint(4) NOT NULL,
  `Name` varchar(50) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CardTypes`
--

LOCK TABLES `CardTypes` WRITE;
/*!40000 ALTER TABLE `CardTypes` DISABLE KEYS */;
/*!40000 ALTER TABLE `CardTypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Cars`
--

DROP TABLE IF EXISTS `Cars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Cars` (
  `Plate` char(8) NOT NULL,
  `FirstNameOwner` varchar(50) NOT NULL,
  `LastNameOwner` varchar(50) NOT NULL,
  `Passport` varchar(10) NOT NULL,
  `ColorId` smallint(6) NOT NULL,
  `CarBrandId` smallint(6) NOT NULL,
  `CarModelId` smallint(6) NOT NULL,
  `Description` varchar(100) NOT NULL,
  PRIMARY KEY (`Plate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Cars`
--

LOCK TABLES `Cars` WRITE;
/*!40000 ALTER TABLE `Cars` DISABLE KEYS */;
/*!40000 ALTER TABLE `Cars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Cities`
--

DROP TABLE IF EXISTS `Cities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Cities` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `CountryId` tinyint(4) NOT NULL,
  `StateId` smallint(6) NOT NULL,
  `Name` varchar(50) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Cities`
--

LOCK TABLES `Cities` WRITE;
/*!40000 ALTER TABLE `Cities` DISABLE KEYS */;
/*!40000 ALTER TABLE `Cities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ClientsPerLocal`
--

DROP TABLE IF EXISTS `ClientsPerLocal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ClientsPerLocal` (
  `Local` varchar(20) NOT NULL,
  `Tenant` float DEFAULT NULL,
  `Owner` float DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ClientsPerLocal`
--

LOCK TABLES `ClientsPerLocal` WRITE;
/*!40000 ALTER TABLE `ClientsPerLocal` DISABLE KEYS */;
/*!40000 ALTER TABLE `ClientsPerLocal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Colors`
--

DROP TABLE IF EXISTS `Colors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Colors` (
  `Id` smallint(6) NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Colors`
--

LOCK TABLES `Colors` WRITE;
/*!40000 ALTER TABLE `Colors` DISABLE KEYS */;
/*!40000 ALTER TABLE `Colors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Companies`
--

DROP TABLE IF EXISTS `Companies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Companies` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  `Director` varchar(50) NOT NULL,
  `Passport` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Companies`
--

LOCK TABLES `Companies` WRITE;
/*!40000 ALTER TABLE `Companies` DISABLE KEYS */;
/*!40000 ALTER TABLE `Companies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CompanyAddresses`
--

DROP TABLE IF EXISTS `CompanyAddresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CompanyAddresses` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `CompanyId` int(11) NOT NULL,
  `Address` varchar(250) NOT NULL,
  `PostalCode` char(5) NOT NULL,
  `CountryId` tinyint(4) NOT NULL DEFAULT '1',
  `StateId` smallint(6) NOT NULL DEFAULT '0',
  `CityId` int(11) NOT NULL,
  `Phone` varchar(15) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CompanyAddresses`
--

LOCK TABLES `CompanyAddresses` WRITE;
/*!40000 ALTER TABLE `CompanyAddresses` DISABLE KEYS */;
/*!40000 ALTER TABLE `CompanyAddresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CompanyLocals`
--

DROP TABLE IF EXISTS `CompanyLocals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CompanyLocals` (
  `CompanyId` int(11) NOT NULL,
  `LocalId` smallint(6) NOT NULL,
  `DateCreated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`CompanyId`,`LocalId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CompanyLocals`
--

LOCK TABLES `CompanyLocals` WRITE;
/*!40000 ALTER TABLE `CompanyLocals` DISABLE KEYS */;
/*!40000 ALTER TABLE `CompanyLocals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Contracts`
--

DROP TABLE IF EXISTS `Contracts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Contracts` (
  `Id` smallint(6) NOT NULL AUTO_INCREMENT,
  `CustomerId` smallint(6) NOT NULL DEFAULT '0',
  `CardTypeId` tinyint(4) NOT NULL DEFAULT '0',
  `CardId` bigint(20) NOT NULL,
  `ContractDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ExpirationDate` datetime NOT NULL,
  `Status` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Contracts`
--

LOCK TABLES `Contracts` WRITE;
/*!40000 ALTER TABLE `Contracts` DISABLE KEYS */;
/*!40000 ALTER TABLE `Contracts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Countries`
--

DROP TABLE IF EXISTS `Countries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Countries` (
  `Id` tinyint(4) NOT NULL AUTO_INCREMENT,
  `Code` char(2) NOT NULL,
  `Name` varchar(50) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Countries`
--

LOCK TABLES `Countries` WRITE;
/*!40000 ALTER TABLE `Countries` DISABLE KEYS */;
/*!40000 ALTER TABLE `Countries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `GateLogs`
--

DROP TABLE IF EXISTS `GateLogs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GateLogs` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `StationId` smallint(6) NOT NULL DEFAULT '0',
  `LogDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Message` varchar(1000) NOT NULL DEFAULT '',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GateLogs`
--

LOCK TABLES `GateLogs` WRITE;
/*!40000 ALTER TABLE `GateLogs` DISABLE KEYS */;
/*!40000 ALTER TABLE `GateLogs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Levels`
--

DROP TABLE IF EXISTS `Levels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Levels` (
  `Id` tinyint(4) NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  `Description` varchar(100) NOT NULL,
  `ExitOption` tinyint(4) NOT NULL DEFAULT '0',
  `Minutes` smallint(6) NOT NULL DEFAULT '15',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Levels`
--

LOCK TABLES `Levels` WRITE;
/*!40000 ALTER TABLE `Levels` DISABLE KEYS */;
INSERT INTO `Levels` VALUES (1,'ALL','ALL',0,15);
/*!40000 ALTER TABLE `Levels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LoginLog`
--

DROP TABLE IF EXISTS `LoginLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `LoginLog` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `UserId` smallint(6) NOT NULL,
  `LoginDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LoginSuccessful` bit(1) NOT NULL DEFAULT b'0',
  `Ip` varchar(46) NOT NULL DEFAULT '',
  `MacAddress` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Id_UNIQUE` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LoginLog`
--

LOCK TABLES `LoginLog` WRITE;
/*!40000 ALTER TABLE `LoginLog` DISABLE KEYS */;
/*!40000 ALTER TABLE `LoginLog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PayTypes`
--

DROP TABLE IF EXISTS `PayTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PayTypes` (
  `Id` tinyint(4) NOT NULL AUTO_INCREMENT,
  `Description` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PayTypes`
--

LOCK TABLES `PayTypes` WRITE;
/*!40000 ALTER TABLE `PayTypes` DISABLE KEYS */;
INSERT INTO `PayTypes` VALUES (1,'Efectivo'),(2,'Tarjeta de Debito'),(3,'Tarjeta de Credito'),(4,'Cheque'),(5,'Cupon');
/*!40000 ALTER TABLE `PayTypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RateAmounts`
--

DROP TABLE IF EXISTS `RateAmounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RateAmounts` (
  `IdRate` int(11) NOT NULL,
  `IdType` tinyint(4) NOT NULL,
  `RangeFrom` tinyint(4) NOT NULL,
  `RangeTo` tinyint(4) NOT NULL,
  `Amount` double NOT NULL,
  `Offset` int(11) NOT NULL,
  `Fraction` double NOT NULL,
  PRIMARY KEY (`IdRate`,`IdType`,`RangeFrom`,`RangeTo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RateAmounts`
--

LOCK TABLES `RateAmounts` WRITE;
/*!40000 ALTER TABLE `RateAmounts` DISABLE KEYS */;
INSERT INTO `RateAmounts` VALUES (1,1,0,23,400,0,400),(2,1,0,23,150,0,150);
/*!40000 ALTER TABLE `RateAmounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RateLevels`
--

DROP TABLE IF EXISTS `RateLevels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RateLevels` (
  `IdRate` int(11) NOT NULL,
  `StartDate` datetime NOT NULL,
  `EndDate` datetime NOT NULL,
  `IdLevel` tinyint(4) NOT NULL,
  PRIMARY KEY (`IdRate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RateLevels`
--

LOCK TABLES `RateLevels` WRITE;
/*!40000 ALTER TABLE `RateLevels` DISABLE KEYS */;
INSERT INTO `RateLevels` VALUES (1,'2015-09-29 09:00:00','2016-03-29 09:00:00',1),(2,'2015-09-29 09:00:00','2016-03-29 09:00:00',1);
/*!40000 ALTER TABLE `RateLevels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RateTypes`
--

DROP TABLE IF EXISTS `RateTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RateTypes` (
  `Id` tinyint(4) NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  `Description` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RateTypes`
--

LOCK TABLES `RateTypes` WRITE;
/*!40000 ALTER TABLE `RateTypes` DISABLE KEYS */;
INSERT INTO `RateTypes` VALUES (1,'Fijo','Tarifa con tarifa fija'),(2,'Horas','Tarifa por horas'),(3,'Servicio','Tarifa de servicio');
/*!40000 ALTER TABLE `RateTypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Rates`
--

DROP TABLE IF EXISTS `Rates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Rates` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `TransactionTypeId` int(11) NOT NULL,
  `Name` varchar(20) NOT NULL,
  `Description` varchar(100) NOT NULL,
  `MaxAmount` double NOT NULL,
  `Accumulative` bit(1) NOT NULL,
  `Tax` double NOT NULL,
  `Template` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Rates`
--

LOCK TABLES `Rates` WRITE;
/*!40000 ALTER TABLE `Rates` DISABLE KEYS */;
INSERT INTO `Rates` VALUES (1,1,'Valet','Tarifa Valet',400,'\0',12,'\0'),(2,2,'Ticket Perdido','Ticket Perdido',150,'\0',12,'\0');
/*!40000 ALTER TABLE `Rates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Stations`
--

DROP TABLE IF EXISTS `Stations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Stations` (
  `Id` smallint(6) NOT NULL AUTO_INCREMENT,
  `TypeId` tinyint(4) NOT NULL,
  `LevelId` tinyint(4) NOT NULL DEFAULT '1',
  `Name` varchar(50) NOT NULL,
  `Description` varchar(100) NOT NULL,
  `LastTicket` int(11) NOT NULL DEFAULT '0',
  `Active` bit(1) NOT NULL,
  `Configuration` varchar(2000) NOT NULL DEFAULT '',
  `MacAddress` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Stations`
--

LOCK TABLES `Stations` WRITE;
/*!40000 ALTER TABLE `Stations` DISABLE KEYS */;
INSERT INTO `Stations` VALUES (1,4,1,'Valet','Caja Valet',0,'','','');
/*!40000 ALTER TABLE `Stations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `StationsType`
--

DROP TABLE IF EXISTS `StationsType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StationsType` (
  `Id` tinyint(4) NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `StationsType`
--

LOCK TABLES `StationsType` WRITE;
/*!40000 ALTER TABLE `StationsType` DISABLE KEYS */;
INSERT INTO `StationsType` VALUES (1,'Caja'),(2,'Entrada'),(3,'Salida'),(4,'Valet');
/*!40000 ALTER TABLE `StationsType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Summary`
--

DROP TABLE IF EXISTS `Summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Summary` (
  `Id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `StationId` smallint(6) NOT NULL,
  `UserId` smallint(6) NOT NULL,
  `SupervisorId` smallint(6) NOT NULL DEFAULT '0',
  `TotalAmount` double NOT NULL DEFAULT '0',
  `TaxAmount` double NOT NULL DEFAULT '0',
  `DateCreated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `DateClosing` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Status` bit(1) NOT NULL DEFAULT b'0',
  `FirstFiscalInvoice` varchar(8) NOT NULL DEFAULT '',
  `LastFiscalInvoice` varchar(8) NOT NULL DEFAULT '',
  `CashFlow` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Id_UNIQUE` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Summary`
--

LOCK TABLES `Summary` WRITE;
/*!40000 ALTER TABLE `Summary` DISABLE KEYS */;
/*!40000 ALTER TABLE `Summary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SummaryDetail`
--

DROP TABLE IF EXISTS `SummaryDetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SummaryDetail` (
  `SummaryId` int(11) NOT NULL,
  `TransactionTypeId` smallint(6) NOT NULL,
  `Quantity` smallint(6) NOT NULL DEFAULT '0',
  `TotalAmount` double NOT NULL DEFAULT '0',
  `TaxAmount` decimal(4,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`SummaryId`),
  KEY `TransactionTypeId` (`TransactionTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SummaryDetail`
--

LOCK TABLES `SummaryDetail` WRITE;
/*!40000 ALTER TABLE `SummaryDetail` DISABLE KEYS */;
/*!40000 ALTER TABLE `SummaryDetail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SummaryPayDetail`
--

DROP TABLE IF EXISTS `SummaryPayDetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SummaryPayDetail` (
  `SummaryId` int(11) NOT NULL,
  `PayTypeId` tinyint(4) NOT NULL,
  `Quantity` smallint(6) NOT NULL,
  `TotalAmount` double NOT NULL,
  PRIMARY KEY (`SummaryId`),
  KEY `PayTypeId` (`PayTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SummaryPayDetail`
--

LOCK TABLES `SummaryPayDetail` WRITE;
/*!40000 ALTER TABLE `SummaryPayDetail` DISABLE KEYS */;
/*!40000 ALTER TABLE `SummaryPayDetail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TransactionTypes`
--

DROP TABLE IF EXISTS `TransactionTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TransactionTypes` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(30) NOT NULL,
  `Description` varchar(50) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TransactionTypes`
--

LOCK TABLES `TransactionTypes` WRITE;
/*!40000 ALTER TABLE `TransactionTypes` DISABLE KEYS */;
INSERT INTO `TransactionTypes` VALUES (1,'Ticket Valet','Ticket de servicio de Valet Parking'),(2,'Ticket Perdido','Ticket perdido');
/*!40000 ALTER TABLE `TransactionTypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Transactions`
--

DROP TABLE IF EXISTS `Transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Transactions` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `StationId` smallint(6) NOT NULL,
  `CardId` bigint(20) NOT NULL DEFAULT '0',
  `TicketNumber` varchar(20) NOT NULL DEFAULT '0',
  `SummaryId` int(11) NOT NULL DEFAULT '0',
  `Picture` varchar(100) NOT NULL DEFAULT '',
  `TotalAmount` double NOT NULL,
  `TransactionDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Id_UNIQUE` (`Id`),
  KEY `SummaryId` (`SummaryId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Transactions`
--

LOCK TABLES `Transactions` WRITE;
/*!40000 ALTER TABLE `Transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `Transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TransactionsDetail`
--

DROP TABLE IF EXISTS `TransactionsDetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TransactionsDetail` (
  `TransactionId` int(11) NOT NULL,
  `TypeId` smallint(6) NOT NULL DEFAULT '0',
  `TotalAmount` double NOT NULL DEFAULT '0',
  `TaxAmount` decimal(4,2) NOT NULL DEFAULT '12.00',
  KEY `TransactionId` (`TransactionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TransactionsDetail`
--

LOCK TABLES `TransactionsDetail` WRITE;
/*!40000 ALTER TABLE `TransactionsDetail` DISABLE KEYS */;
/*!40000 ALTER TABLE `TransactionsDetail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TransactionsPay`
--

DROP TABLE IF EXISTS `TransactionsPay`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TransactionsPay` (
  `TransactionId` int(11) NOT NULL,
  `PayTypeId` tinyint(4) NOT NULL,
  `Amount` double NOT NULL,
  KEY `TransactionId` (`TransactionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TransactionsPay`
--

LOCK TABLES `TransactionsPay` WRITE;
/*!40000 ALTER TABLE `TransactionsPay` DISABLE KEYS */;
/*!40000 ALTER TABLE `TransactionsPay` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UserType`
--

DROP TABLE IF EXISTS `UserType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UserType` (
  `Id` tinyint(4) NOT NULL,
  `Name` varchar(50) NOT NULL,
  `Description` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Id_UNIQUE` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UserType`
--

LOCK TABLES `UserType` WRITE;
/*!40000 ALTER TABLE `UserType` DISABLE KEYS */;
INSERT INTO `UserType` VALUES (1,'Administrador','Administrador'),(2,'Supervisor','Supervisor'),(3,'Cajero','Cajero'),(4,'Tecnico','Tecnico');
/*!40000 ALTER TABLE `UserType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UserTypePermissions`
--

DROP TABLE IF EXISTS `UserTypePermissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UserTypePermissions` (
  `UserTypeId` tinyint(4) NOT NULL,
  `LogToWeb` tinyint(1) NOT NULL DEFAULT '0',
  `LogToProgram` tinyint(1) NOT NULL DEFAULT '0',
  `ViewUserTypes` tinyint(1) NOT NULL DEFAULT '0',
  `CreateUserTypes` tinyint(1) NOT NULL DEFAULT '0',
  `ViewUsers` tinyint(1) NOT NULL DEFAULT '0',
  `CreateUsers` tinyint(1) NOT NULL DEFAULT '0',
  `ViewStations` tinyint(1) NOT NULL DEFAULT '0',
  `CreateStations` tinyint(1) NOT NULL DEFAULT '0',
  `ViewLoginLog` tinyint(1) NOT NULL DEFAULT '0',
  `ViewSummary` tinyint(1) NOT NULL DEFAULT '0',
  `ViewTransactions` tinyint(1) NOT NULL DEFAULT '0',
  `ViewStats` tinyint(1) NOT NULL DEFAULT '0',
  `ViewVehicleTypes` tinyint(1) NOT NULL DEFAULT '0',
  `CreateVehicleTypes` tinyint(1) NOT NULL DEFAULT '0',
  `CanCheckOut` tinyint(1) NOT NULL DEFAULT '0',
  `CanPrintReportZ` tinyint(1) NOT NULL DEFAULT '0',
  `CanPrintReportX` tinyint(1) NOT NULL DEFAULT '0',
  UNIQUE KEY `UserTypeId_UNIQUE` (`UserTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UserTypePermissions`
--

LOCK TABLES `UserTypePermissions` WRITE;
/*!40000 ALTER TABLE `UserTypePermissions` DISABLE KEYS */;
INSERT INTO `UserTypePermissions` VALUES (1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1),(2,1,1,0,0,1,1,1,0,0,1,1,0,0,0,1,1,1),(3,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0);
/*!40000 ALTER TABLE `UserTypePermissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Users` (
  `Id` smallint(6) NOT NULL AUTO_INCREMENT,
  `UserTypeId` tinyint(4) NOT NULL,
  `Passport` varchar(10) NOT NULL DEFAULT '',
  `FirstName` varchar(50) NOT NULL,
  `LastName` varchar(50) NOT NULL,
  `Email` varchar(60) NOT NULL,
  `MobilePhone` varchar(50) NOT NULL,
  `Login` varchar(20) NOT NULL,
  `Password` varchar(50) NOT NULL,
  `CreatedDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Status` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Users`
--

LOCK TABLES `Users` WRITE;
/*!40000 ALTER TABLE `Users` DISABLE KEYS */;
INSERT INTO `Users` VALUES (1,1,'18058264','Jesus','Flores','admin@jesusflores.com.ve','04261540479','areyalp','1714941655ce6f0ea44d87171d6dfa6','2015-09-11 01:44:00','');
/*!40000 ALTER TABLE `Users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `VaultSummary`
--

DROP TABLE IF EXISTS `VaultSummary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `VaultSummary` (
  `SummaryId` int(11) NOT NULL,
  `SupervisorId` smallint(6) NOT NULL,
  `ZReport` int(11) NOT NULL DEFAULT '0',
  `Description` varchar(200) NOT NULL,
  KEY `SummaryId` (`SummaryId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `VaultSummary`
--

LOCK TABLES `VaultSummary` WRITE;
/*!40000 ALTER TABLE `VaultSummary` DISABLE KEYS */;
/*!40000 ALTER TABLE `VaultSummary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `VaultSummaryDetail`
--

DROP TABLE IF EXISTS `VaultSummaryDetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `VaultSummaryDetail` (
  `SummaryId` int(11) NOT NULL,
  `TransactionTypeId` smallint(6) NOT NULL,
  `Quantity` smallint(6) NOT NULL DEFAULT '0',
  `TotalAmount` double NOT NULL DEFAULT '0',
  KEY `SummaryId` (`SummaryId`),
  KEY `TransactionTypeId` (`TransactionTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `VaultSummaryDetail`
--

LOCK TABLES `VaultSummaryDetail` WRITE;
/*!40000 ALTER TABLE `VaultSummaryDetail` DISABLE KEYS */;
/*!40000 ALTER TABLE `VaultSummaryDetail` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-10-05 12:57:40
