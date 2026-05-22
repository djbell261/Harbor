CREATE INDEX IF NOT EXISTS idx_verification_reports_status
  ON verification_reports (status);

CREATE INDEX IF NOT EXISTS idx_verification_reports_resource_id
  ON verification_reports (resource_id);

CREATE INDEX IF NOT EXISTS idx_verification_reports_created_at
  ON verification_reports (created_at DESC);

CREATE INDEX IF NOT EXISTS idx_resources_last_verified_at
  ON resources (last_verified_at);

CREATE INDEX IF NOT EXISTS idx_resources_visibility
  ON resources (visibility);

CREATE INDEX IF NOT EXISTS idx_resources_category_id
  ON resources (category_id);
