CREATE TABLE employees.contracts
(id INT NOT NULL AUTO_INCREMENT,
uuid VARCHAR(40) NOT NULL,
hours DOUBLE NOT NULL,
salary DOUBLE NOT NULL,
status VARCHAR(25) NOT NULL,
validdate DATE NOT NULL,
created DATETIME NOT NULL,
PRIMARY KEY (id)) DEFAULT CHARSET=utf8;