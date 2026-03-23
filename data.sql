-- =========================================
-- USERS
-- =========================================
CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    photo TEXT NULL,
    bio TEXT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL
    );

-- =========================================
-- POSTS
-- =========================================
CREATE TABLE IF NOT EXISTS posts (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    image TEXT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL,

    CONSTRAINT fk_post_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    );

-- =========================================
-- COMMENTS
-- =========================================
CREATE TABLE IF NOT EXISTS comments (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID NOT NULL,
    user_id UUID NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_comment_post
    FOREIGN KEY (post_id)
    REFERENCES posts(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_comment_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    updated_at TIMESTAMP NULL
    );

-- =========================================
-- LIKES
-- =========================================
CREATE TABLE IF NOT EXISTS likes (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID NOT NULL,
    user_id UUID NOT NULL,

    CONSTRAINT fk_like_post
    FOREIGN KEY (post_id)
    REFERENCES posts(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_like_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    );

-- =========================================
-- REFRESH TOKENS
-- =========================================
CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    refresh_token TEXT NOT NULL,
    auth_token TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_refresh_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    );