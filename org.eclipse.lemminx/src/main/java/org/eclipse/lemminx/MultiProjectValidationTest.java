package org.eclipse.lemminx;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMParser;
import org.eclipse.lemminx.services.XMLLanguageService;
import org.eclipse.lsp4j.Diagnostic;

public class MultiProjectValidationTest {

    // One XML engine instance
    private static XMLLanguageService xmlService = new XMLLanguageService();

    public static void main(String[] args) throws Exception {

        System.out.println("Starting real validation test...\n");

        validate("../test-workspace/projectA/test.xml");
        validate("../test-workspace/projectB/test.xml");
    }

    static void validate(String path) throws Exception {

        System.out.println("Validating: " + path);

        // Read XML file
        String xml = new String(Files.readAllBytes(Paths.get(path)));

        // Parse XML using LemMinX parser
        DOMDocument document = DOMParser.getInstance().parse(path, xml, null);

        // Run validation using XMLLanguageService
        List<Diagnostic> diagnostics = xmlService.doDiagnostics(
                document,
                null,
                null,
                () -> {} // CancelChecker (empty implementation)
        );

        // Print results
        System.out.println("Diagnostics: " + diagnostics);
        System.out.println("-----------------------------\n");
    }
}