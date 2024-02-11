CREATE TABLE category
(
    category_id UUID NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    parent_id UUID
);

CREATE TABLE note
(
    note_id UUID NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    text TEXT NOT NULL,
    category_id UUID REFERENCES category(category_id)
);

CREATE TABLE note_tag
(
    note_id UUID NOT NULL REFERENCES note(note_id),
    text TEXT NOT NULL
);
