use serde::{Deserialize, Serialize};
use surrealdb::types::{Datetime, RecordId, SurrealValue};

#[derive(Serialize, Deserialize, SurrealValue, Clone)]
pub struct User {
    pub id: Option<RecordId>,
    pub username: String,
    pub password: String,
    pub created_at: Option<Datetime>,
    pub updated_at: Option<Datetime>,
}
