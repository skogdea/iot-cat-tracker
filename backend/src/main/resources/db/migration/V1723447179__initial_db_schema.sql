CREATE SCHEMA IF NOT EXISTS iot_cat_tracker_schema;

CREATE TABLE IF NOT EXISTS iot_cat_tracker_schema.cat_document_model (
    cat_id UUID PRIMARY KEY,
    cat_name VARCHAR(50) NOT NULL,
    cat_gender SMALLINT NOT NULL,
    cat_age INTEGER NOT NULL,
    cat_weight DOUBLE PRECISION NOT NULL,
    cat_breed SMALLINT NOT NULL,
    cat_color_pattern SMALLINT NOT NULL,
    cat_personality SMALLINT,
    best_friend VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS iot_cat_tracker_schema.favorite_food_model (
    id SERIAL PRIMARY KEY,
    cat_id UUID,
    favorite_food VARCHAR(50),
    FOREIGN KEY(cat_id) REFERENCES iot_cat_tracker_schema.cat_document_model (cat_id)
);

CREATE TABLE IF NOT EXISTS iot_cat_tracker_schema.favorite_toy_model (
    id SERIAL PRIMARY KEY,
    cat_id UUID,
    favorite_toy VARCHAR(50),
    FOREIGN KEY(cat_id) REFERENCES iot_cat_tracker_schema.cat_document_model (cat_id)
);
