ALTER TABLE verification_reports DROP CONSTRAINT IF EXISTS verification_type_check;

ALTER TABLE verification_reports
  ADD CONSTRAINT verification_type_check CHECK (
    report_type IN (
      'food_unavailable',
      'shelter_full',
      'restroom_closed',
      'wifi_offline',
      'unsafe_location',
      'incorrect_hours',
      'inaccessible',
      'other',
      'closed',
      'wrong_hours',
      'wrong_address',
      'wrong_phone',
      'unsafe',
      'duplicate'
    )
  );

CREATE INDEX IF NOT EXISTS idx_verification_reports_resource_created
  ON verification_reports (resource_id, created_at DESC);
