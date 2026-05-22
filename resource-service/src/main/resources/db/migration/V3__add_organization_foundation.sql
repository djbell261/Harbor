CREATE TABLE IF NOT EXISTS organizations (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(200) NOT NULL,
  description TEXT,
  website_url TEXT,
  phone VARCHAR(50),
  email VARCHAR(255),
  trusted_status VARCHAR(40) NOT NULL DEFAULT 'unverified',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT organizations_trusted_status_check CHECK (trusted_status IN ('unverified', 'verified', 'suspended'))
);

CREATE INDEX IF NOT EXISTS idx_organizations_name ON organizations (name);
CREATE INDEX IF NOT EXISTS idx_organizations_trusted_status ON organizations (trusted_status);

ALTER TABLE resources
  ADD COLUMN IF NOT EXISTS organization_id UUID REFERENCES organizations(id);

CREATE INDEX IF NOT EXISTS idx_resources_organization ON resources (organization_id);

WITH seed_organizations (
  id,
  name,
  description,
  website_url,
  phone,
  email,
  trusted_status
) AS (
  VALUES
    (
      'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa1'::uuid,
      'Lutheran Community Services Delaware',
      'Nonprofit organization providing food pantry and emergency assistance services in Delaware.',
      'https://lcsde.org/',
      '(302) 654-8886',
      NULL,
      'verified'
    ),
    (
      'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa2'::uuid,
      'Sunday Breakfast Mission',
      'Wilmington mission providing emergency shelter, meals, and support services.',
      'https://sundaybreakfastmission.org/',
      '(877) 306-4663',
      NULL,
      'verified'
    ),
    (
      'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa3'::uuid,
      'Westside Family Healthcare',
      'Federally qualified health center serving Delaware communities.',
      'https://www.westsidehealth.org/',
      '(302) 655-5822',
      NULL,
      'verified'
    ),
    (
      'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa4'::uuid,
      'Wilmington Public Library',
      'Public library serving Wilmington residents and visitors.',
      'https://wilmington.lib.de.us/',
      '(302) 571-7400',
      NULL,
      'verified'
    ),
    (
      'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa5'::uuid,
      'City of Wilmington Parks and Recreation',
      'City department operating community centers and recreation facilities.',
      'https://www.wilmingtonde.gov/',
      '(302) 576-3810',
      NULL,
      'verified'
    ),
    (
      'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa6'::uuid,
      'DART First State',
      'Delaware public transit agency providing fixed-route, paratransit, and customer assistance services.',
      'https://www.dartfirststate.com/',
      '(800) 652-3278',
      NULL,
      'verified'
    )
)
INSERT INTO organizations (
  id,
  name,
  description,
  website_url,
  phone,
  email,
  trusted_status
)
SELECT
  id,
  name,
  description,
  website_url,
  phone,
  email,
  trusted_status
FROM seed_organizations
WHERE current_setting('harbor.seed_data.enabled', true) = 'true'
ON CONFLICT (id) DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  website_url = EXCLUDED.website_url,
  phone = EXCLUDED.phone,
  email = EXCLUDED.email,
  trusted_status = EXCLUDED.trusted_status,
  updated_at = now();

UPDATE resources SET organization_id = 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa1'
WHERE current_setting('harbor.seed_data.enabled', true) = 'true'
  AND id = '11111111-1111-4111-8111-111111111111';

UPDATE resources SET organization_id = 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa2'
WHERE current_setting('harbor.seed_data.enabled', true) = 'true'
  AND id = '22222222-2222-4222-8222-222222222222';

UPDATE resources SET organization_id = 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa3'
WHERE current_setting('harbor.seed_data.enabled', true) = 'true'
  AND id = '33333333-3333-4333-8333-333333333333';

UPDATE resources SET organization_id = 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa4'
WHERE current_setting('harbor.seed_data.enabled', true) = 'true'
  AND id IN (
  '44444444-4444-4444-8444-444444444444',
  '66666666-6666-4666-8666-666666666666',
  '77777777-7777-4777-8777-777777777777'
);

UPDATE resources SET organization_id = 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa5'
WHERE current_setting('harbor.seed_data.enabled', true) = 'true'
  AND id = '55555555-5555-4555-8555-555555555555';

UPDATE resources SET organization_id = 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa6'
WHERE current_setting('harbor.seed_data.enabled', true) = 'true'
  AND id = '88888888-8888-4888-8888-888888888888';
