-- phpMyAdmin SQL Dump
-- version 4.1.13
-- http://www.phpmyadmin.net
--
-- Host: 100.64.2.103
-- Generation Time: Oct 11, 2014 at 06:31 AM
-- Server version: 10.0.11-MariaDB-1~precise-log
-- PHP Version: 5.4.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `cf_20a714ef_27d7_42e8_a143_bdb9f4c4d4ec`
--

-- --------------------------------------------------------

--
-- Table structure for table `speed_controls`
--

CREATE TABLE IF NOT EXISTS `speed_controls` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp_sent` bigint(20) NOT NULL,
  `timestamp_received` bigint(20) DEFAULT NULL,
  `power` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `speed_controls`
--

INSERT INTO `speed_controls` (`id`, `timestamp_sent`, `timestamp_received`, `power`) VALUES
(5, 1413007882947, NULL, 100),
(6, 1413008536988, NULL, 100);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
