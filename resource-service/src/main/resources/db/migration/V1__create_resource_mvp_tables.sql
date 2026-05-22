CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE resource_categories (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  code VARCHAR(80) NOT NULL UNIQUE,
  name VARCHAR(120) NOT NULL,
  description TEXT,
  icon_name VARCHAR(80),
  sort_order INTEGER NOT NULL DEFAULT 0,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE resources (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  category_id UUID NOT NULL REFERENCES resource_categories(id),
  name VARCHAR(220) NOT NULL,
  description TEXT,
  address_line1 VARCHAR(220),
  address_line2 VARCHAR(220),
  city VARCHAR(120),
  region VARCHAR(120),
  postal_code VARCHAR(30),
  country_code VARCHAR(2) NOT NULL DEFAULT 'US',
  latitude NUMERIC(9,6),
  longitude NUMERIC(9,6),
  phone VARCHAR(50),
  website_url TEXT,
  eligibility_notes TEXT,
  intake_notes TEXT,
  accessibility_notes TEXT,
  data_source VARCHAR(120),
  source_url TEXT,
  last_verified_at TIMESTAMPTZ,
  confidence_score NUMERIC(4,3) NOT NULL DEFAULT 0.500,
  visibility VARCHAR(40) NOT NULL DEFAULT 'public',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at TIMESTAMPTZ,
  CONSTRAINT resources_confidence_range CHECK (confidence_score >= 0 AND confidence_score <= 1),
  CONSTRAINT resources_visibility_check CHECK (visibility IN ('public', 'hidden', 'pending_review'))
);

CREATE TABLE resource_hours (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  resource_id UUID NOT NULL REFERENCES resources(id) ON DELETE CASCADE,
  day_of_week SMALLINT NOT NULL,
  opens_at TIME,
  closes_at TIME,
  is_closed BOOLEAN NOT NULL DEFAULT FALSE,
  notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT resource_hours_day_check CHECK (day_of_week BETWEEN 0 AND 6),
  CONSTRAINT resource_hours_time_check CHECK (is_closed OR (opens_at IS NOT NULL AND closes_at IS NOT NULL))
);

CREATE TABLE resource_status (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  resource_id UUID NOT NULL REFERENCES resources(id) ON DELETE CASCADE,
  status VARCHAR(40) NOT NULL,
  reason TEXT,
  effective_from TIMESTAMPTZ NOT NULL DEFAULT now(),
  effective_until TIMESTAMPTZ,
  reported_by_type VARCHAR(40) NOT NULL DEFAULT 'system',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT resource_status_check CHECK (status IN ('open', 'closed', 'limited', 'unknown', 'temporarily_closed'))
);

CREATE TABLE verification_reports (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  resource_id UUID NOT NULL REFERENCES resources(id) ON DELETE CASCADE,
  report_type VARCHAR(60) NOT NULL,
  status VARCHAR(40) NOT NULL DEFAULT 'pending',
  reporter_kind VARCHAR(40) NOT NULL DEFAULT 'anonymous',
  description TEXT,
  suggested_value JSONB,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT verification_status_check CHECK (status IN ('pending', 'accepted', 'rejected', 'needs_more_info')),
  CONSTRAINT verification_type_check CHECK (report_type IN ('closed', 'wrong_hours', 'wrong_address', 'wrong_phone', 'unsafe', 'duplicate', 'other'))
);

CREATE INDEX idx_resource_categories_active_sort ON resource_categories (is_active, sort_order);
CREATE INDEX idx_resources_category ON resources (category_id);
CREATE INDEX idx_resources_location ON resources (latitude, longitude);
CREATE INDEX idx_resources_postal_code ON resources (postal_code);
CREATE INDEX idx_resources_city_region ON resources (city, region);
CREATE INDEX idx_resources_public ON resources (visibility) WHERE deleted_at IS NULL;
CREATE INDEX idx_resource_hours_resource_day ON resource_hours (resource_id, day_of_week);
CREATE INDEX idx_resource_status_resource_active ON resource_status (resource_id, effective_from DESC) WHERE effective_until IS NULL;
CREATE INDEX idx_verification_reports_resource ON verification_reports (resource_id);
CREATE INDEX idx_verification_reports_status_created ON verification_reports (status, created_at);

INSERT INTO resource_categories (code, name, description, icon_name, sort_order) VALUES
  ('food', 'Food', 'Food pantries, meals, and grocery assistance.', 'utensils', 10),
  ('shelter', 'Shelter', 'Emergency shelters and temporary housing support.', 'home', 20),
  ('clinic', 'Clinics', 'Free and low-cost medical clinics.', 'heart-pulse', 30),
  ('warming_cooling', 'Warming and Cooling Centers', 'Seasonal weather safety centers.', 'thermometer', 40),
  ('restroom', 'Public Restrooms', 'Public restroom access.', 'accessibility', 50),
  ('library', 'Libraries', 'Libraries with indoor space, Wi-Fi, and services.', 'book-open', 60),
  ('charging_wifi', 'Charging and Wi-Fi', 'Places to charge devices or access internet.', 'wifi', 70),
  ('transportation', 'Transportation', 'Transit help and transportation support.', 'bus', 80),
  ('mutual_aid', 'Mutual Aid', 'Community-led support organizations.', 'hand-heart', 90);
