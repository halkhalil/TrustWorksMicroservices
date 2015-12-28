DROP TABLE IF EXISTS usermanager.user;
DROP TABLE IF EXISTS usermanager.role;

CREATE TABLE IF NOT EXISTS `user` (
  `uuid` varchar(40) NOT NULL,
  `active` bit(1) NOT NULL,
  `created` datetime DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `role` (
  `uuid` varchar(40) NOT NULL,
  `rolename` varchar(255) DEFAULT NULL,
  `useruuid` varchar(40) NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;