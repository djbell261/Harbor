ALTER TABLE verification_reports
  ADD COLUMN reviewed_at TIMESTAMPTZ,
  ADD COLUMN review_notes TEXT,
  ADD COLUMN review_decision VARCHAR(40),
  ADD COLUMN reviewed_by VARCHAR(120);

ALTER TABLE verification_reports
  ADD CONSTRAINT verification_review_decision_check
  CHECK (review_decision IS NULL OR review_decision IN ('accepted', 'rejected'));

CREATE INDEX IF NOT EXISTS idx_verification_reports_reviewed_at
  ON verification_reports (reviewed_at DESC);
