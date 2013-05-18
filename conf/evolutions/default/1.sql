# --- !Ups
CREATE SEQUENCE user_id_seq;
CREATE TABLE users(
    id LONG NOT NULL DEFAULT nextval('user_id_seq')
    , twitter_id LONG NOT NULL
    , name  VARCHAR(255) NOT NULL
    , token VARCHAR(255) NOT NULL
    , secret VARCHAR(255) NOT NULL
    , screen_name VARCHAR(255)
    , created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    , PRIMARY KEY(id)
);

CREATE SEQUENCE tweet_id_seq;
CREATE TABLE tweets(
  id LONG  NOT NULL DEFAULT nextval('tweet_id_seq')
  , users_id LONG NOT NULL
  , content VARCHAR(140) NOT NULL
  , created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
  , PRIMARY KEY(id)
  , FOREIGN KEY(users_id) REFERENCES users(id) ON DELETE CASCADE
);

# --- !Downs
DROP TABLE users;
DROP SEQUENCE user_id_seq;

DROP TABLE tweets;
DROP SEQUENCE tweet_id_seq;
