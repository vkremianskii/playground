CREATE TABLE note
(
    note_id SERIAL PRIMARY KEY,
    name TEXT,
    text TEXT
);

CREATE TABLE note_tag
(
    note_id INTEGER NOT NULL REFERENCES note(note_id),
    text TEXT
);
