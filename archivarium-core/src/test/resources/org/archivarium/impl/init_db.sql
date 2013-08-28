DELETE FROM score_instruments;
DELETE FROM scores;
INSERT INTO scores (id, name, author, description, edition, url, format, location, genre) VALUES(0, 'Score 1', 'Author 1', 'Score description', 'Custom edition', 'media/scores/score.pdf', 'pdf', null, null);
INSERT INTO scores (id, name, author, description, edition, url, format, location, genre) VALUES(1, 'Score 2', 'Anonymous', null, '1992 edition', null, 'Paper', 'Desk drawer', null);
INSERT INTO score_instruments (score_id, instrument) VALUES (0, 'Violin')
INSERT INTO score_instruments (score_id, instrument) VALUES (0, 'Viola')
INSERT INTO score_instruments (score_id, instrument) VALUES (0, 'Cello')
INSERT INTO score_instruments (score_id, instrument) VALUES (0, 'Contrabass')
INSERT INTO score_instruments (score_id, instrument) VALUES (1, 'Piano')

