BEGIN TRANSACTION;

DROP TABLE IF EXISTS tenmo_user, account, user_transactions;

DROP SEQUENCE IF EXISTS seq_user_id, seq_account_id, seq_user_transactions_id;

-- Sequence to start user_id values at 1001 instead of 1
CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

-- Sequence to start account_id values at 2001 instead of 1
-- Note: Use similar sequences with unique starting values for additional tables
CREATE SEQUENCE seq_account_id
  INCREMENT BY 1
  START WITH 2001
  NO MAXVALUE;

CREATE TABLE account (
	account_id int NOT NULL DEFAULT nextval('seq_account_id'),
	user_id int NOT NULL,
	balance decimal(13, 2) NOT NULL,
	CONSTRAINT PK_account PRIMARY KEY (account_id),
	CONSTRAINT FK_account_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id)
);

CREATE SEQUENCE seq_user_transactions_id
  INCREMENT BY 1
  START WITH 3001
  NO MAXVALUE;

-- CREATE TABLE transaction (
--     transaction_id int NOT NULL DEFAULT nextval('seq_transaction_id'),
--     sender_id int NOT NULL,
--     receiver_id int NOT NULL,
--     money_amount decimal(13, 2) NOT NULL,
--     CONSTRAINT PK_transaction PRIMARY KEY (transaction_id)
	
--     CONSTRAINT FK_transaction_tenmo_user FOREIGN KEY (sender_id) REFERENCES tenmo_user (user_id),
--     CONSTRAINT FK_transaction_tenmo_user FOREIGN KEY (receiver_id) REFERENCES tenmo_user (user_id)

CREATE TABLE user_transactions (
	transaction_id int NOT NULL DEFAULT nextval('seq_user_transactions_id'),
	sender_id int NOT NULL, 
	receiver_id int NOT NULL, 
	money_amount decimal(13, 2) NOT NULL,
	CONSTRAINT PK_user_transactions PRIMARY KEY (transaction_id),
	CONSTRAINT FK_transaction_sender_account FOREIGN KEY (sender_id) REFERENCES tenmo_user (user_id),
	CONSTRAINT FK_transaction_receiver_account FOREIGN KEY (receiver_id) REFERENCES tenmo_user (user_id)
);



COMMIT;
