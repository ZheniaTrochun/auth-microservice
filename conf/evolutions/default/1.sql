# --- !Ups
CREATE TABLE "usersCreds"("id" SERIAL PRIMARY KEY ,"name" varchar(200), "hashedPassword" VARCHAR(200));
INSERT INTO "usersCreds" values (1,'Vikas', '123');
INSERT INTO "usersCreds" values (2,'Bhavya','123');
INSERT INTO "usersCreds" values (3,'Ayush', '123');
INSERT INTO "usersCreds" values (4,'Satendra', '123');
INSERT INTO "usersCreds" values (5,'Sushil', '123');

# --- !Downs
DROP TABLE "usersCreds";