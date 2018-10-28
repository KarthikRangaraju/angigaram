CREATE TABLE users (
    guid VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    time_zone VARCHAR(255) NOT NULL,
    photo_url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX user_guid_idx (guid)
);

CREATE TABLE badge_transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(255) NOT NULL,
    from_user_guid VARCHAR(255) NOT NULL,
    to_user_guid VARCHAR(255) NOT NULL,
    badge VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX from_user_guid_idx (from_user_guid),
    INDEX to_user_guid_idx (to_user_guid)
);