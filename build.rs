use std::fs;
use std::path::Path;

fn main() {
    let schema_dir = Path::new("schema");
    let mut combined = String::new();

    for entry in fs::read_dir(schema_dir).unwrap() {
        let path = entry.unwrap().path();

        if path.extension().unwrap_or_default() == "surql" {
            let content = fs::read_to_string(&path).unwrap();

            combined.push_str(&format!(
                "\n-- {}\n{}\n",
                path.file_name().unwrap().to_string_lossy(),
                content
            ));
        }
    }

    let output = format!("pub const SCHEMA: &str = r#\"{}\"#;", combined);

    fs::write("src/migration.rs", output).unwrap();

    println!("cargo:rerun-if-changed=schema/");

    uniffi::generate_scaffolding("src/userencrypt.udl").unwrap();
}
