ALTER TABLE users
    ADD COLUMN email VARCHAR(255);
ALTER TABLE users
    ADD COLUMN password VARCHAR(255);

-- Apply NOT NULL constraints and UNIQUE constraint
ALTER TABLE users
    ALTER COLUMN name SET NOT NULL,
    ALTER COLUMN email SET NOT NULL,
    ALTER COLUMN password SET NOT NULL,
    ALTER COLUMN size_available SET NOT NULL,
    ADD CONSTRAINT uniquekey_user_email UNIQUE (email);