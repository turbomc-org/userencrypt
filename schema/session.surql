DEFINE TABLE IF NOT EXISTS session SCHEMAFULL TYPE NORMAL;
DEFINE FIELD IF NOT EXISTS ip         ON TABLE session TYPE string;
DEFINE FIELD IF NOT EXISTS user       ON TABLE session TYPE record<user>;
DEFINE FIELD IF NOT EXISTS created_at ON TABLE session TYPE datetime DEFAULT time::now();
DEFINE FIELD IF NOT EXISTS expires_at ON TABLE session TYPE datetime DEFAULT time::now() + 72h;
