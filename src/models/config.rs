use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize)]
pub struct Config {
    pub db_path: String,
    pub session_expiry_minutes: u64,
    pub max_attempts: u64,
    pub rate_limit_window_seconds: u64,
}
