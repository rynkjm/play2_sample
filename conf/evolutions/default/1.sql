# --- !Ups
CREATE SEQUENCE tweet_id_seq;
CREATE TABLE tweet(
  id INTEGER NOT NULL DEFAULT nextval('tweet_id_seq')
  , content varchar(140) NOT NULL
  , created_at timestamp NOT NULL
);

# --- !Downs
DROP TABLE tweet;
DROP SEQUENCE tweet_id_seq;
