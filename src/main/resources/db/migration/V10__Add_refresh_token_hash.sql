-- Add a column to store a hash of the refresh token and an index on it
-- We store only the hash in DB to avoid persisting raw tokens at rest.

SET @col_exists = (
  SELECT COUNT(1)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'users'
    AND column_name = 'refresh_token_hash'
);

SET @sql = IF(@col_exists = 0,
  'ALTER TABLE users ADD COLUMN refresh_token_hash VARCHAR(191) NULL;',
  'SELECT 1;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_exists = (
  SELECT COUNT(1)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'users'
    AND index_name = 'idx_users_refresh_token_hash'
);

SET @sql = IF(@idx_exists = 0,
  'ALTER TABLE users ADD INDEX idx_users_refresh_token_hash (refresh_token_hash);',
  'SELECT 1;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Note: This migration adds a new column; it does not delete the existing plaintext column
-- to avoid breaking existing deployments. Backfill and deletion should be handled
-- in a separate controlled migration/maintenance window if desired.
