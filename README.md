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

## How to Run the Test

Navigate to the `org.eclipse.lemminx` directory and run the following Maven command:
```bash
cd org.eclipse.lemminx
mvn clean compile exec:java -Dexec.mainClass="org.eclipse.lemminx.MultiProjectValidationTest" -q
```
---
my note

I successfully completed the standalone PoC for multi-root schema validation. Here’s a quick summary of how it works:

Instead of using XML Catalogs (which maps via namespace and causes conflicts when multiple MI versions are open), I used XMLFileAssociation inside 

ContentModelManager
. This maps different schemas strictly based on the file path pattern (e.g., **/projectA/**/*.xml -> MI 4.1.0 schema).

I used a single 

XMLLanguageService
 instance to validate files from two different projects simultaneously. Both files mapped correctly to their isolated schemas based purely on the path pattern without any inline schema definitions inside the XMLs.

Also, it handles nested schemas perfectly. I only passed the root schema path (
synapse_config.xsd
 equivalent), and the underlying parser automatically resolved all <xs:include> schemas relative to the root file. This proves we can robustly support multi-root validation in the MI Language Server using this architecture.

---

### Understanding the Difference: XML Catalogs vs XML File Associations

**1. XML Catalogs (The Old Way)**
* **How it maps:** By **Namespace**.
* **How it works:** It tells the engine, "If you see an XML file with the namespace `http://ws.apache.org/ns/synapse`, use *this* specific schema file."
* **The Problem (Conflict):** In a Multi-Root workspace, `Project A` (MI 4.1.0) and `Project B` (MI 4.4.0) **both** use the exact same namespace (`.../ns/synapse`). Because the namespace is identical, the engine cannot figure out which project needs the 4.1.0 schema and which needs the 4.4.0 schema.

**2. XML File Associations (The New Way in this PoC)**
* **How it maps:** By **File Path Location**.
* **How it works:** It tells the engine, "If a file is opened from the `Project A` folder path, use the 4.1.0 schema. If a file is opened from the `Project B` folder path, use the 4.4.0 schema."
* **The Solution:** We completely ignore the namespace. Since the files are in different folders on the hard drive, we can easily direct the XML engine to validate them against different schemas simultaneously without any conflicts.