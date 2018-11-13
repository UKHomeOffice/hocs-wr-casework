DROP TABLE IF EXISTS audit_user_data;

CREATE TABLE IF NOT EXISTS audit_user_data
(
  id                BIGSERIAL PRIMARY KEY,
  uuid              UUID,
  case_uuid         UUID,
  created_by_user   TEXT,
  created_timestamp timestamp,
  updated_by_user   TEXT,
  updated_timestamp timestamp

);

CREATE INDEX idx_audit_user_data_uuid
  ON audit_user_data (uuid);
CREATE INDEX idx_audit_user_data_case_id
  ON audit_user_data (case_uuid);

ALTER table audit
  Add COLUMN user_id Int;

ALTER table audit
  Add CONSTRAINT fk_user_data_id FOREIGN KEY (user_id) REFERENCES audit_user_data (id);


