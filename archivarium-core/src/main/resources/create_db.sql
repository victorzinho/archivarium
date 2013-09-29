DROP TABLE IF EXISTS scores;
DROP TABLE IF EXISTS score_instruments;

CREATE TABLE scores (id INTEGER AUTO_INCREMENT, title VARCHAR(200), composer VARCHAR(200), description VARCHAR(200), edition VARCHAR(200), arrangement VARCHAR(200), origin VARCHAR(200), url VARCHAR(200), format VARCHAR(200), location VARCHAR(200), genre VARCHAR(200), lyrics VARCHAR(200), language VARCHAR(200), PRIMARY KEY (id));
CREATE TABLE score_instruments (id INTEGER AUTO_INCREMENT, instrument VARCHAR(200), score_id INTEGER, PRIMARY KEY (id), FOREIGN KEY (score_id) REFERENCES scores(id));