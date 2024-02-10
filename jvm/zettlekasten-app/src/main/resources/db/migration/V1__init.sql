CREATE TABLE note
(
    note_id UUID PRIMARY KEY,
    name TEXT,
    text TEXT
);

CREATE TABLE note_tag
(
    note_id UUID NOT NULL REFERENCES note(note_id),
    text TEXT
);
