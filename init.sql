-- create of MSISDN table
CREATE TABLE IF NOT EXISTS MSISDN (
  id SERIAL PRIMARY KEY,
  phone_number varchar(250) NOT NULL
);

-- create of PIN table
CREATE TABLE IF NOT EXISTS PIN (
  id SERIAL PRIMARY KEY,
  msisdn_id INT NOT NULL,
  pin_number varchar(250) NOT NULL,
  creation_date_time TIMESTAMP,
  validation_attempts INT,
  discarded BOOLEAN DEFAULT FALSE,
  discarded_date_time TIMESTAMP,
  CONSTRAINT fk_msisdn FOREIGN KEY(msisdn_id) REFERENCES MSISDN(id)
);