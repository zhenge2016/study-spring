DROP database test;
CREATE database test;
use test;

CREATE TABLE user_info (
  id INT auto_increment primary key,
  nick VARCHAR(20),
  age int,
  birthday date,
  tel VARCHAR(20),
  email VARCHAR(50)
);