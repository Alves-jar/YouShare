CREATE TABLE project_members (
    user_id    UUID NOT NULL,
    project_id UUID NOT NULL,
    role       VARCHAR(20) NOT NULL,
    joined_at  TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, project_id),
    CONSTRAINT fk_member_user    FOREIGN KEY (user_id)    REFERENCES users(id),
    CONSTRAINT fk_member_project FOREIGN KEY (project_id) REFERENCES projects(id)
);