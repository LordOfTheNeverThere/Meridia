CREATE TABLE IF NOT EXISTS photos (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255),
    file_size INTEGER,
    file_type VARCHAR(255),
    date_of_creation TIMESTAMP WITH TIME ZONE,
    uploader_id BIGSERIAL
);

ALTER TABLE photos ADD CONSTRAINT uniquekey_file_name UNIQUE (file_name);