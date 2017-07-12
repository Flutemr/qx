SET SESSION FOREIGN_KEY_CHECKS=0;

/* Drop Tables */

DROP TABLE IF EXISTS stats_device_browser;
DROP TABLE IF EXISTS dimension_browser;
DROP TABLE IF EXISTS stats_device_location;
DROP TABLE IF EXISTS stats_hourly;
DROP TABLE IF EXISTS stats_user;
DROP TABLE IF EXISTS dimension_date;
DROP TABLE IF EXISTS dimension_kpi;
DROP TABLE IF EXISTS dimension_location;
DROP TABLE IF EXISTS dimension_platform;




/* Create Tables */

CREATE TABLE dimension_browser
(
	id int(11) NOT NULL AUTO_INCREMENT,
	browser_name varchar(45),
	browser_version varchar(45),
	PRIMARY KEY (id)
);


CREATE TABLE dimension_date
(
	id int(11) NOT NULL AUTO_INCREMENT,
	year int(11),
	season int(11),
	month int(11),
	week int(11),
	day int(11),
	calendar date,
	type enum('year','season','month','week','day'),
	PRIMARY KEY (id)
);


CREATE TABLE dimension_kpi
(
	id int(11) NOT NULL AUTO_INCREMENT,
	kpi_name varchar(30) NOT NULL,
	PRIMARY KEY (id)
);


CREATE TABLE dimension_location
(
	id int(11) NOT NULL AUTO_INCREMENT,
	country varchar(45),
	province varchar(45),
	city varchar(45),
	PRIMARY KEY (id)
);


CREATE TABLE dimension_platform
(
	id int(11) NOT NULL AUTO_INCREMENT,
	platform_name varchar(45) NOT NULL,
	PRIMARY KEY (id)
);


CREATE TABLE stats_device_browser
(
	active_users int(11),
	new_install_users int(11),
	total_install_users int(11),
	sessions int(11),
	sessions_length int(11),
	total_members int(11),
	new_members int(11),
	active_members int(11),
	created date,
	platform_dimension_id int(11) NOT NULL,
	date_dimension_id int(11) NOT NULL,
	browser_dimension_id int(11) NOT NULL,
	UNIQUE (platform_dimension_id, date_dimension_id, browser_dimension_id)
);


CREATE TABLE stats_device_location
(
	date_dimension_id int(11) NOT NULL,
	platform_dimension_id int(11) NOT NULL,
	location_dimension_id int(11) NOT NULL,
	active_users int(11),
	sessions int(11),
	bounce_sessions int(11),
	created date,
	UNIQUE (date_dimension_id, platform_dimension_id, location_dimension_id)
);


CREATE TABLE stats_hourly
(
	platform_dimension_id int(11) NOT NULL,
	kpi_dimension_id int(11) NOT NULL,
	date_dimension_id int(11) NOT NULL,
	hour_00 int(11),
	hour_01 int(11),
	hour_02 int(11),
	hour_03 int(11),
	hour_04 int(11),
	hour_05 int(11),
	hour_06 int(11),
	hour_07 int(11),
	hour_08 int(11),
	hour_09 int(11),
	hour_10 int(11),
	hour_12 int(11),
	hour_13 int(11),
	hour_14 int(11),
	hour_15 int(11),
	hour_16 int(11),
	hour_17 int(11),
	hour_18 int(11),
	hour_19 int(11),
	hour_20 int(11),
	hour_21 int(11),
	hour_22 int(11),
	hour_23 int(11),
	UNIQUE (platform_dimension_id, kpi_dimension_id, date_dimension_id)
);


CREATE TABLE stats_user
(
	platform_dimension_id int(11) NOT NULL,
	date_dimension_id int(11) NOT NULL,
	active_users int(11),
	new_install_users int(11),
	total_install_users int(11),
	sessions int(11),
	sessions_length int(11),
	total_members int(11),
	new_members int(11),
	active_members int(11),
	created date,
	UNIQUE (platform_dimension_id, date_dimension_id)
);



/* Create Foreign Keys */

ALTER TABLE stats_device_browser
	ADD FOREIGN KEY (browser_dimension_id)
	REFERENCES dimension_browser (id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE stats_device_browser
	ADD FOREIGN KEY (date_dimension_id)
	REFERENCES dimension_date (id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE stats_device_location
	ADD FOREIGN KEY (date_dimension_id)
	REFERENCES dimension_date (id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE stats_hourly
	ADD FOREIGN KEY (date_dimension_id)
	REFERENCES dimension_date (id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE stats_user
	ADD FOREIGN KEY (date_dimension_id)
	REFERENCES dimension_date (id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE stats_hourly
	ADD FOREIGN KEY (kpi_dimension_id)
	REFERENCES dimension_kpi (id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE stats_device_location
	ADD FOREIGN KEY (location_dimension_id)
	REFERENCES dimension_location (id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE stats_device_browser
	ADD FOREIGN KEY (platform_dimension_id)
	REFERENCES dimension_platform (id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE stats_device_location
	ADD FOREIGN KEY (platform_dimension_id)
	REFERENCES dimension_platform (id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE stats_hourly
	ADD FOREIGN KEY (platform_dimension_id)
	REFERENCES dimension_platform (id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE stats_user
	ADD FOREIGN KEY (platform_dimension_id)
	REFERENCES dimension_platform (id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;



