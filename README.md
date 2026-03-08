# LemMinX Multi-Project Validation Proof of Concept

This repository demonstrates how to validate XML documents from multiple projects using a single instance of the LemMinX (`XMLLanguageService`) engine.

## Problem with Current Approach
The current WSO2 MI Language Server uses **XML Catalogs** to pass XSD schemas to the XML engine. Catalogs map schemas based on the **XML Namespace** (e.g., `http://ws.apache.org/ns/synapse`). 

This creates a conflict in multi-root workspaces where different projects might use different MI Versions (e.g., 4.1.0 vs 4.4.0). Since all versions share the exact same namespace, the engine cannot differentiate which schema to apply if multiple are loaded.

## Solution: XML File Associations
Instead of relying on namespaces, this PoC uses **XML File Associations** via the `ContentModelManager`.
By mapping specific file path patterns to a specific schema path, we can apply different schemas to different projects simultaneously without conflict.

### Key Points Proved:
1. **Single Engine, Multiple Schemas:** We configured a single `XMLLanguageService` to simultaneously apply `schemaA.xsd` to `projectA` files and `schemaB.xsd` to `projectB` files based on glob patterns (e.g., `**/*projectA*test.xml`).
2. **Nested Schema Support:** We only mapped the root schema (`schemaA.xsd`). Any schemas included within it (using `<xs:include>`) are automatically resolved by the underlying parser relative to the root schema's directory. There is no need to map individual component schemas (`api.xsd`, `proxy.xsd`, etc.) individually.
3. **No Inline Schemas Needed:** The `test.xml` files do not have `xsi:noNamespaceSchemaLocation` injected into them. The schemas are fully applied externally via the engine settings.
