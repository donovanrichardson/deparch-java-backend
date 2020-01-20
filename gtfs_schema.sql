# ************************************************************
# Sequel Pro SQL dump
# Version 5446
#
# https://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 8.0.12)
# Database: gtfs
# Generation Time: 2020-01-04 23:18:55 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table agency
# ------------------------------------------------------------

DROP TABLE IF EXISTS `agency`;

CREATE TABLE `agency` (
  `key` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `agency_id` varchar(255) DEFAULT NULL,
  `agency_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `agency_url` text NOT NULL,
  `agency_timezone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `feed_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '',
  PRIMARY KEY (`key`),
  KEY `feed_version` (`feed_version`),
  KEY `agency_id` (`agency_id`),
  CONSTRAINT `agency_ibfk_1` FOREIGN KEY (`feed_version`) REFERENCES `feed_version` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DELIMITER ;;
/*!50003 SET SESSION SQL_MODE="ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION" */;;
/*!50003 CREATE */ /*!50017 DEFINER=`root`@`localhost` */ /*!50003 TRIGGER `agency_version_id_trigger` BEFORE INSERT ON `agency` FOR EACH ROW begin
declare feed_id varchar(255);

select id into feed_id from feed_version where inserted = (select max(inserted) from feed_version);

set new.feed_version = feed_id;
end */;;
DELIMITER ;
/*!50003 SET SESSION SQL_MODE=@OLD_SQL_MODE */;


# Dump of table feed
# ------------------------------------------------------------

DROP TABLE IF EXISTS `feed`;

CREATE TABLE `feed` (
  `id` varchar(255) NOT NULL DEFAULT '',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'gtfs',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `location` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# Dump of table feed_version
# ------------------------------------------------------------

DROP TABLE IF EXISTS `feed_version`;

CREATE TABLE `feed_version` (
  `id` varchar(255) NOT NULL DEFAULT '',
  `feed` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `timestamp` bigint(20) unsigned NOT NULL,
  `size` bigint(20) unsigned NOT NULL,
  `url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `start` char(8) DEFAULT NULL,
  `finish` char(8) DEFAULT NULL,
  `inserted` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `feed` (`feed`),
  CONSTRAINT `feed_version_ibfk_1` FOREIGN KEY (`feed`) REFERENCES `feed` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# Dump of table frequency
# ------------------------------------------------------------

DROP TABLE IF EXISTS `frequency`;

CREATE TABLE `frequency` (
  `trip_id` varchar(128) NOT NULL DEFAULT '',
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `headway_secs` int(10) unsigned NOT NULL,
  `exact_times` tinyint(4) DEFAULT '0',
  `feed_version` varchar(255) NOT NULL,
  PRIMARY KEY (`start_time`,`trip_id`,`feed_version`),
  KEY `frequency_feed_version_fk` (`feed_version`),
  KEY `trip_id` (`trip_id`),
  CONSTRAINT `frequency_feed_version_fk` FOREIGN KEY (`feed_version`) REFERENCES `feed_version` (`id`) ON DELETE CASCADE,
  CONSTRAINT `frequency_ibfk_1` FOREIGN KEY (`trip_id`) REFERENCES `trip` (`trip_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DELIMITER ;;
/*!50003 SET SESSION SQL_MODE="ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION" */;;
/*!50003 CREATE */ /*!50017 DEFINER=`root`@`localhost` */ /*!50003 TRIGGER `frequency_version_id_trigger` BEFORE INSERT ON `frequency` FOR EACH ROW begin
declare feed_id varchar(255);

select id into feed_id from feed_version where inserted = (select max(inserted) from feed_version);

set new.feed_version = feed_id;
end */;;
DELIMITER ;
/*!50003 SET SESSION SQL_MODE=@OLD_SQL_MODE */;


# Dump of table route
# ------------------------------------------------------------

DROP TABLE IF EXISTS `route`;

CREATE TABLE `route` (
  `route_id` varchar(128) NOT NULL DEFAULT '',
  `agency_id` varchar(255) DEFAULT NULL,
  `default_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT 'route_short_name',
  `route_long_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '',
  `route_desc` text,
  `route_type` tinyint(4) NOT NULL,
  `route_url` text,
  `route_color` char(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'FFFFFF',
  `route_text_color` char(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '000000',
  `route_sort_order` int(10) unsigned DEFAULT NULL,
  `feed_version` varchar(255) NOT NULL,
  PRIMARY KEY (`route_id`,`feed_version`),
  KEY `agency_id` (`agency_id`),
  KEY `route_feed_version_fk` (`feed_version`),
  KEY `route_id` (`route_id`),
  CONSTRAINT `route_feed_version_fk` FOREIGN KEY (`feed_version`) REFERENCES `feed_version` (`id`) ON DELETE CASCADE,
  CONSTRAINT `route_ibfk_1` FOREIGN KEY (`agency_id`) REFERENCES `agency` (`agency_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DELIMITER ;;
/*!50003 SET SESSION SQL_MODE="ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION" */;;
/*!50003 CREATE */ /*!50017 DEFINER=`root`@`localhost` */ /*!50003 TRIGGER `route_version_id_trigger` BEFORE INSERT ON `route` FOR EACH ROW begin
declare feed_id varchar(255);

select id into feed_id from feed_version where inserted = (select max(inserted) from feed_version);

set new.feed_version = feed_id;
end */;;
DELIMITER ;
/*!50003 SET SESSION SQL_MODE=@OLD_SQL_MODE */;


# Dump of table service
# ------------------------------------------------------------

DROP TABLE IF EXISTS `service`;

CREATE TABLE `service` (
  `service_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'calendar.txt',
  `monday` tinyint(1) NOT NULL DEFAULT '0',
  `tuesday` tinyint(1) NOT NULL DEFAULT '0',
  `wednesday` tinyint(1) NOT NULL DEFAULT '0',
  `thursday` tinyint(1) NOT NULL DEFAULT '0',
  `friday` tinyint(1) NOT NULL DEFAULT '0',
  `saturday` tinyint(1) NOT NULL DEFAULT '0',
  `sunday` tinyint(1) NOT NULL DEFAULT '0',
  `start_date` char(8) DEFAULT NULL,
  `end_date` char(8) DEFAULT NULL,
  `feed_version` varchar(255) NOT NULL,
  PRIMARY KEY (`service_id`,`feed_version`),
  KEY `service_feed_version_fk` (`feed_version`),
  KEY `service_id` (`service_id`),
  CONSTRAINT `service_feed_version_fk` FOREIGN KEY (`feed_version`) REFERENCES `feed_version` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DELIMITER ;;
/*!50003 SET SESSION SQL_MODE="ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION" */;;
/*!50003 CREATE */ /*!50017 DEFINER=`root`@`localhost` */ /*!50003 TRIGGER `service_version_id_trigger` BEFORE INSERT ON `service` FOR EACH ROW begin
declare feed_id varchar(255);

select id into feed_id from feed_version where inserted = (select max(inserted) from feed_version);

set new.feed_version = feed_id;
end */;;
DELIMITER ;
/*!50003 SET SESSION SQL_MODE=@OLD_SQL_MODE */;


# Dump of table service_exception
# ------------------------------------------------------------

DROP TABLE IF EXISTS `service_exception`;

CREATE TABLE `service_exception` (
  `service_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'calendar_dates.txt',
  `date` char(8) NOT NULL DEFAULT '',
  `exception_type` tinyint(1) unsigned NOT NULL DEFAULT '1',
  `feed_version` varchar(255) NOT NULL,
  PRIMARY KEY (`service_id`,`date`,`feed_version`),
  KEY `service_exception_feed_version_fk` (`feed_version`),
  CONSTRAINT `service_exception_feed_version_fk` FOREIGN KEY (`feed_version`) REFERENCES `feed_version` (`id`) ON DELETE CASCADE,
  CONSTRAINT `service_exception_ibfk_1` FOREIGN KEY (`service_id`) REFERENCES `service` (`service_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DELIMITER ;;
/*!50003 SET SESSION SQL_MODE="ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION" */;;
/*!50003 CREATE */ /*!50017 DEFINER=`root`@`localhost` */ /*!50003 TRIGGER `service_exception_version_id_trigger` BEFORE INSERT ON `service_exception` FOR EACH ROW begin
declare feed_id varchar(255);

select id into feed_id from feed_version where inserted = (select max(inserted) from feed_version);

set new.feed_version = feed_id;
end */;;
DELIMITER ;
/*!50003 SET SESSION SQL_MODE=@OLD_SQL_MODE */;


# Dump of table shape
# ------------------------------------------------------------

DROP TABLE IF EXISTS `shape`;

CREATE TABLE `shape` (
  `shape_id` varchar(128) NOT NULL DEFAULT '',
  `shape_pt_lat` double NOT NULL,
  `shape_pt_lon` double NOT NULL,
  `shape_pt_sequence` int(11) unsigned NOT NULL,
  `shape_dist_traveled` double unsigned DEFAULT NULL,
  `feed_version` varchar(255) NOT NULL,
  PRIMARY KEY (`shape_id`,`feed_version`,`shape_pt_sequence`),
  KEY `shape_feed_version_fk` (`feed_version`),
  KEY `shape_id` (`shape_id`),
  CONSTRAINT `shape_feed_version_fk` FOREIGN KEY (`feed_version`) REFERENCES `feed_version` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DELIMITER ;;
/*!50003 SET SESSION SQL_MODE="ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION" */;;
/*!50003 CREATE */ /*!50017 DEFINER=`root`@`localhost` */ /*!50003 TRIGGER `shape_version_id_trigger` BEFORE INSERT ON `shape` FOR EACH ROW begin
declare feed_id varchar(255);

select id into feed_id from feed_version where inserted = (select max(inserted) from feed_version);

set new.feed_version = feed_id;
end */;;
DELIMITER ;
/*!50003 SET SESSION SQL_MODE=@OLD_SQL_MODE */;


# Dump of table stop
# ------------------------------------------------------------

DROP TABLE IF EXISTS `stop`;

CREATE TABLE `stop` (
  `stop_id` varchar(128) NOT NULL DEFAULT '',
  `stop_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `stop_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `stop_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `stop_lat` double DEFAULT NULL,
  `stop_lon` double DEFAULT NULL,
  `stop_url` text,
  `location_type` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `parent_station` varchar(128) DEFAULT NULL,
  `wheelchair_boarding` tinyint(11) DEFAULT NULL,
  `feed_version` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`stop_id`),
  KEY `parent_station` (`parent_station`),
  KEY `stop_feed_version_fk` (`feed_version`),
  CONSTRAINT `stop_feed_version_fk` FOREIGN KEY (`feed_version`) REFERENCES `feed_version` (`id`) ON DELETE CASCADE,
  CONSTRAINT `stop_ibfk_1` FOREIGN KEY (`parent_station`) REFERENCES `stop` (`stop_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DELIMITER ;;
/*!50003 SET SESSION SQL_MODE="ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION" */;;
/*!50003 CREATE */ /*!50017 DEFINER=`root`@`localhost` */ /*!50003 TRIGGER `stop_version_id_trigger` BEFORE INSERT ON `stop` FOR EACH ROW begin
declare feed_id varchar(255);

select id into feed_id from feed_version where inserted = (select max(inserted) from feed_version);

set new.feed_version = feed_id;
end */;;
DELIMITER ;
/*!50003 SET SESSION SQL_MODE=@OLD_SQL_MODE */;


# Dump of table stop_time
# ------------------------------------------------------------

DROP TABLE IF EXISTS `stop_time`;

CREATE TABLE `stop_time` (
  `trip_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `arrival_time` time DEFAULT NULL,
  `departure_time` time DEFAULT NULL,
  `stop_id` varchar(128) NOT NULL DEFAULT '',
  `stop_sequence` int(10) unsigned NOT NULL,
  `stop_headsign` varchar(64) DEFAULT NULL,
  `pickup_type` tinyint(4) unsigned DEFAULT '0',
  `drop_off_type` tinyint(3) unsigned DEFAULT '0',
  `shape_dist_traveled` double unsigned DEFAULT NULL,
  `timepoint` tinyint(4) DEFAULT '1',
  `feed_version` varchar(255) NOT NULL,
  PRIMARY KEY (`trip_id`,`stop_sequence`,`feed_version`),
  KEY `stop_time_feed_version_fk` (`feed_version`),
  KEY `stop_id` (`stop_id`),
  CONSTRAINT `stop_time_feed_version_fk` FOREIGN KEY (`feed_version`) REFERENCES `feed_version` (`id`) ON DELETE CASCADE,
  CONSTRAINT `stop_time_ibfk_1` FOREIGN KEY (`stop_id`) REFERENCES `stop` (`stop_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DELIMITER ;;
/*!50003 SET SESSION SQL_MODE="ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION" */;;
/*!50003 CREATE */ /*!50017 DEFINER=`root`@`localhost` */ /*!50003 TRIGGER `stop_time_version_id_trigger` BEFORE INSERT ON `stop_time` FOR EACH ROW begin
declare feed_id varchar(255);

select id into feed_id from feed_version where inserted = (select max(inserted) from feed_version);

set new.feed_version = feed_id;
end */;;
DELIMITER ;
/*!50003 SET SESSION SQL_MODE=@OLD_SQL_MODE */;


# Dump of table trip
# ------------------------------------------------------------

DROP TABLE IF EXISTS `trip`;

CREATE TABLE `trip` (
  `route_id` varchar(128) NOT NULL DEFAULT '',
  `service_id` varchar(128) NOT NULL DEFAULT '',
  `trip_id` varchar(128) NOT NULL DEFAULT '',
  `trip_headsign` varchar(64) DEFAULT NULL,
  `trip_short_name` varchar(128) DEFAULT '',
  `direction_id` tinyint(11) DEFAULT NULL,
  `block_id` varchar(128) DEFAULT NULL,
  `shape_id` varchar(128) DEFAULT NULL,
  `wheelchair_accessible` tinyint(4) DEFAULT '0',
  `bikes_allowed` tinyint(4) DEFAULT '0',
  `feed_version` varchar(255) NOT NULL,
  PRIMARY KEY (`trip_id`,`feed_version`),
  KEY `service_id` (`service_id`),
  KEY `shape_id` (`shape_id`),
  KEY `trip_feed_version_fk` (`feed_version`),
  KEY `trip_id` (`trip_id`),
  KEY `route_id_2` (`route_id`),
  CONSTRAINT `trip_feed_version_fk` FOREIGN KEY (`feed_version`) REFERENCES `feed_version` (`id`) ON DELETE CASCADE,
  CONSTRAINT `trip_ibfk_1` FOREIGN KEY (`route_id`) REFERENCES `route` (`route_id`) ON DELETE CASCADE,
  CONSTRAINT `trip_ibfk_2` FOREIGN KEY (`service_id`) REFERENCES `service` (`service_id`) ON DELETE CASCADE,
  CONSTRAINT `trip_ibfk_3` FOREIGN KEY (`shape_id`) REFERENCES `shape` (`shape_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DELIMITER ;;
/*!50003 SET SESSION SQL_MODE="ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION" */;;
/*!50003 CREATE */ /*!50017 DEFINER=`root`@`localhost` */ /*!50003 TRIGGER `trip_version_id_trigger` BEFORE INSERT ON `trip` FOR EACH ROW begin
declare feed_id varchar(255);

select id into feed_id from feed_version where inserted = (select max(inserted) from feed_version);

set new.feed_version = feed_id;
end */;;
DELIMITER ;
/*!50003 SET SESSION SQL_MODE=@OLD_SQL_MODE */;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
