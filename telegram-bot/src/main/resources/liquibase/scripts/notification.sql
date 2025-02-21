-- liquibase formatted sql

-- changeset tgbotadmin:1
CREATE TABLE notification_task (
    "id" int8 NULL,
    "date" DATE NULL,
    "time" TIME NULL,
    "chat_id" int8 NULL,
    "text" VARCHAR NULL
)