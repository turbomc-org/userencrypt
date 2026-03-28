use crate::{DB, models::user::User};
use anyhow::Context;
use anyhow::Result;
use argon2::{
    Argon2,
    password_hash::{PasswordHash, PasswordHasher, PasswordVerifier, SaltString, rand_core::OsRng},
};
use surrealdb::types::RecordId;

impl User {
    pub fn new(username: &str, password: &str) -> Result<Self> {
        Ok(Self {
            id: None,
            username: username.to_string(),
            password: Self::generate_password(password)?,
            created_at: None,
            updated_at: None,
        })
    }

    pub fn id(&self) -> Result<RecordId> {
        Ok(self.id.clone().context("User ID is not set")?)
    }

    pub async fn insert(&mut self) -> Result<()> {
        let result: Self = DB
            .create("user")
            .content(self.clone())
            .await?
            .context("Failed to insert user")?;
        *self = result;
        Ok(())
    }

    pub async fn find_by_username(username: String) -> Result<Option<Self>> {
        let mut result = DB
            .query("SELECT * FROM user WHERE username = $username LIMIT 1")
            .bind(("username", username))
            .await?;
        Ok(result.take(0)?)
    }

    pub async fn find(id: RecordId) -> Result<Option<Self>> {
        Ok(DB.select(id).await?)
    }

    pub async fn update_password(&mut self, password: &str) -> Result<()> {
        let hashed = Self::generate_password(password)?;
        let mut result = DB
            .query("UPDATE user SET password = $password WHERE id = $id")
            .bind(("password", hashed))
            .bind(("id", self.id.clone()))
            .await?;
        let result: Option<Self> = result.take(0)?;
        *self = result.context("Failed to update password")?;
        Ok(())
    }

    pub fn validate_password(&self, password: &str) -> Result<bool> {
        let parsed_hash = PasswordHash::new(&self.password)?;
        Ok(Argon2::default()
            .verify_password(password.as_bytes(), &parsed_hash)
            .is_ok())
    }

    pub fn generate_password(password: &str) -> Result<String> {
        let salt = SaltString::generate(&mut OsRng);
        Ok(Argon2::default()
            .hash_password(password.as_bytes(), &salt)?
            .to_string())
    }
}
