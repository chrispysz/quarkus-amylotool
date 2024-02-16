ALTER SEQUENCE qa_token_seq RESTART WITH 50;

INSERT INTO qa_token(id, name, value, validUntil)
VALUES (nextval('qa_token_seq'), 'test_token_new', '45338a22-b747-4470-b6a4-36adcacf00c4', '2051-06-13T17:09:42.411');

INSERT INTO qa_token(id, name, value, validUntil)
VALUES (nextval('qa_token_seq'), 'test_token_old', '93082a72-2244-4bca-93e0-e9ae49a9822f', '2023-06-13T17:09:42.411');