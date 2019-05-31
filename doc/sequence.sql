CREATE DATABASE IF NOT EXISTS sequence DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
CREATE TABLE IF NOT EXISTS `sequence` (
    `id` SMALLINT NOT NULL auto_increment,
    `sequence_name` varchar(60) NOT NULL,
    `current_offset` BIGINT(20) UNSIGNED NOT NULL DEFAULT '0',
    `modify_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(3)
);

