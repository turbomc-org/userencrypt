use anyhow::{Context, Result};

use crate::models::session::Session;
use crate::models::user::User;

pub async fn register(ip: String, username: String, password: String) -> Result<Session> {
    let mut user = User::new(&username, &password)?;
    user.insert().await?;

    let mut session = Session::new(ip, user.id()?);
    session.insert().await?;

    Ok(session)
}

pub async fn login(ip: String, username: String, password: String) -> Result<Session> {
    let user = User::find_by_username(username)
        .await?
        .context("User not found")?;

    let is_valid = user.validate_password(&password)?;

    if !is_valid {
        return Err(anyhow::anyhow!("Invalid password"));
    }

    let mut session = Session::new(ip, user.id()?);
    session.insert().await?;

    Ok(session)
}
