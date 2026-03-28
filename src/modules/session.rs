use crate::{DB, models::session::Session};
use anyhow::{Context, Result};
use surrealdb::types::RecordId;

impl Session {
    pub fn new(ip: String, user: RecordId) -> Self {
        Self {
            id: None,
            ip,
            user,
            created_at: None,
            expires_at: None,
        }
    }

    pub async fn insert(&mut self) -> Result<()> {
        let result: Option<Self> = DB.create("session").content(self.clone()).await?;

        *self = result.context("Failed to insert session.")?;

        Ok(())
    }

    pub async fn find_sessions(user: RecordId) -> Result<Vec<Self>> {
        let mut result = DB
            .query("SELECT * FROM session WHERE user = $user")
            .bind(("user", user))
            .await?;

        let result: Vec<Self> = result.take(0)?;

        Ok(result)
    }

    pub async fn is_active(ip: String, user: RecordId) -> Result<bool> {
        let mut result = DB
            .query(
                r#"
            SELECT count() > 0 AS is_active
            FROM session
            WHERE ip = $ip
              AND user = $user
              AND expires_at > time::now()
            GROUP ALL;
            "#,
            )
            .bind(("ip", ip))
            .bind(("user", user))
            .await?;

        let result: Option<bool> = result.take((0, "is_active"))?;

        Ok(result.context("Failed to check if session is active.")?)
    }
}
