
USE subsolr;

DROP TABLE IF EXISTS employees;

CREATE TABLE employees (
    emp_no      INT             NOT NULL,
    birth_date  DATE            NOT NULL,
    first_name  VARCHAR(14)     NOT NULL,
    last_name   VARCHAR(16)     NOT NULL,
    gender      ENUM ('M','F')  NOT NULL,    
    hire_date   DATE            NOT NULL,
    PRIMARY KEY (emp_no)
);

INSERT INTO `employees` VALUES
(483791,'1955-09-23','Divine','Felder','F','1990-09-16'),
(483792,'1953-09-26','Irena','Unno','M','1985-06-07'),
(483793,'1963-04-18','Sachio','Binkley','F','1989-05-23'),
(483794,'1952-08-29','Hausi','Alvarado','M','1986-12-18'),
(483795,'1964-06-28','Weijing','Stranks','F','1994-03-13'),
(483796,'1957-08-31','Alejandro','Schwartzbauer','M','1986-04-30'),
(483797,'1954-10-19','Vasilii','Dengi','F','1989-09-03'),
(483798,'1954-11-23','Sudharsan','Attimonelli','M','1985-05-09'),
(483799,'1955-01-21','Khedija','Hammerschmidt','F','1995-05-26'),
(483800,'1957-03-07','Yishay','Ellozy','F','1988-07-18');
