CREATE TABLE IF NOT EXISTS `sequence` (
    `id` SMALLINT NOT NULL auto_increment,
    `sequence_name` varchar(60) NOT NULL,
    `current_offset` BIGINT(20) NOT NULL,
    UNIQUE (`sequence_name`)
);

