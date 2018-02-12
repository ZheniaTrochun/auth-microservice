# --- !Ups
CREATE TABLE "usersCreds"("id" SERIAL PRIMARY KEY ,"name" varchar(30) UNIQUE, "hashedPassword" VARCHAR(200));
# --- !Downs
DROP TABLE "usersCreds";