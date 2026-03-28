pub mod auth;
pub mod migration;
pub mod models;
pub mod modules;

use crate::models::config::Config;
use once_cell::sync::OnceCell;
use std::sync::LazyLock;
use surrealdb::Surreal;
use surrealdb::engine::any::Any;

mod runtime;

pub static DB: LazyLock<Surreal<Any>> = LazyLock::new(Surreal::init);
pub static RUNTIME: OnceCell<tokio::runtime::Runtime> = OnceCell::new();
pub static CONFIG: OnceCell<Config> = OnceCell::new();

uniffi::include_scaffolding!("userencrypt");

pub fn greet(name: String) -> String {
    format!("Hello from Rust, {}!", name)
}

pub fn add_numbers(a: i32, b: i32) -> i32 {
    a + b
}
