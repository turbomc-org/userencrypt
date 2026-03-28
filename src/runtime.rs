use once_cell::sync::Lazy;
use std::sync::Arc;
use tokio::runtime::Runtime;

pub static TOKIO_RUNTIME: Lazy<Arc<Runtime>> =
    Lazy::new(|| Arc::new(Runtime::new().expect("Failed to create Tokio runtime")));

/// Helper to spawn async tasks on the global runtime
pub fn spawn<F>(future: F) -> tokio::task::JoinHandle<F::Output>
where
    F: std::future::Future + Send + 'static,
    F::Output: Send + 'static,
{
    TOKIO_RUNTIME.spawn(future)
}

/// Helper to block on an async operation (use sparingly from sync contexts)
pub fn block_on<F>(future: F) -> F::Output
where
    F: std::future::Future,
{
    TOKIO_RUNTIME.block_on(future)
}
