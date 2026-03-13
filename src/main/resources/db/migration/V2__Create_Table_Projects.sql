CREATE TABLE projects (
    id UUID PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    description TEXT,
    creator_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_project_creator FOREIGN KEY (creator_id) REFERENCES users(id)
);