use serde::{Deserialize, Serialize};
use surrealdb::types::{Datetime, RecordId, SurrealValue};

#[derive(Serialize, Deserialize, SurrealValue, Clone)]
pub struct Session {
    pub id: Option<RecordId>,
    pub ip: String,
    pub user: RecordId,
    pub created_at: Option<Datetime>,
    pub expires_at: Option<Datetime>,
}
