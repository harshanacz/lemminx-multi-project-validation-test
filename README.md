XML Language Server (LemMinX) Fork - Experiment to verify whether a single LemMinX XMLLanguageService instance can validate XML documents from multiple project roots using different schemas.
===========================
[![Maven](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.eclipse.org%2Fcontent%2Frepositories%2Flemminx-releases%2Forg%2Feclipse%2Flemminx%2Forg.eclipse.lemminx%2Fmaven-metadata.xml&style=for-the-badge&logo=apachemaven&logoColor=white&color=informational)](https://repo.eclipse.org/content/repositories/lemminx-releases/org/eclipse/lemminx/org.eclipse.lemminx/)
[![Eclipse Site](https://img.shields.io/badge/Eclipse%20Site-lemminx-informational?logo=eclipse&style=for-the-badge)](https://download.eclipse.org/lemminx/releases/)
[![Build Status](https://img.shields.io/jenkins/tests?jobUrl=https%3A%2F%2Fci.eclipse.org%2Flemminx%2Fjob%2Flemminx%2Fjob%2Fmain%2F&style=for-the-badge&logo=jenkins&logoColor=white)](https://ci.eclipse.org/lemminx/job/lemminx/job/main/)
[![CodeQL Status](https://img.shields.io/github/actions/workflow/status/eclipse/lemminx/codeql-analysis.yml?style=for-the-badge&label=codeql&logo=githubactions&logoColor=white)](https://github.com/eclipse/lemminx/actions/workflows/codeql-analysis.yml?query=branch%3Amain)
[![LICENSE](https://img.shields.io/github/license/eclipse/lemminx?style=for-the-badge&color=informational)](https://github.com/eclipse/lemminx/blob/main/LICENSE)

**LemMinX** is a XML language specific implementation of the [Language Server Protocol](https://github.com/Microsoft/language-server-protocol)
and can be used with any editor that supports the protocol, to offer good support for the **XML Language**. The server is based on:

 * [Eclipse LSP4J](https://github.com/eclipse/lsp4j), the Java binding for the Language Server Protocol.
 * Xerces to manage XML Schema validation, completion and hover
