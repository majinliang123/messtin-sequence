CREATE TABLE IF NOT EXISTS `sequence` (
    `id` SMALLINT NOT NULL auto_increment,
    `sequence_name` varchar(60) NOT NULL,
    `current_offset` BIGINT(20) NOT NULL,
    `modify_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE (`sequence_name`)
);

