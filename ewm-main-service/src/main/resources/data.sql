-- USERS
INSERT INTO users (id, email, name)
VALUES
  (1, 'user1@mail.com', 'User One'),
  (2, 'admin@mail.com', 'Admin')
ON CONFLICT (id) DO NOTHING;

-- CATEGORIES
INSERT INTO categories (id, name)
VALUES
  (1, 'IT')
ON CONFLICT (id) DO NOTHING;

-- LOCATIONS
INSERT INTO locations (id, lat, lon)
VALUES
  (1, 55.75, 37.61)
ON CONFLICT (id) DO NOTHING;

-- EVENTS
INSERT INTO events (
    id, annotation, confirmed_requests, created_on,
    description, event_date, paid, participant_limit,
    published_on, request_moderation, state,
    title, views, category_id, initiator_id, location_id
)
VALUES
(
    1,
    'Annotation',
    0,
    now(),
    'Event description',
    now() + interval '1 day',
    false,
    10,
    now(),
    true,
    'PUBLISHED',
    'Test Event',
    0,
    1,
    1,
    1
)
ON CONFLICT (id) DO NOTHING;

-- COMMENTS
INSERT INTO comments (
    id, created_on, text, updated_on, author_id, event_id
)
VALUES
(
    1,
    now(),
    'Первый комментарий',
    now(),
    1,
    1
)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('users', 'id'),
              (SELECT COALESCE(MAX(id), 0) FROM users),
              true);

SELECT setval(pg_get_serial_sequence('categories', 'id'),
              (SELECT COALESCE(MAX(id), 0) FROM categories),
              true);

SELECT setval(pg_get_serial_sequence('locations', 'id'),
              (SELECT COALESCE(MAX(id), 0) FROM locations),
              true);

SELECT setval(pg_get_serial_sequence('events', 'id'),
              (SELECT COALESCE(MAX(id), 0) FROM events),
              true);

SELECT setval(pg_get_serial_sequence('comments', 'id'),
              (SELECT COALESCE(MAX(id), 0) FROM comments),
              true);