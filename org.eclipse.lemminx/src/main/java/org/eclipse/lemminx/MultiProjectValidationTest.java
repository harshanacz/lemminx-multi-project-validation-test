package org.eclipse.lemminx;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMParser;
import org.eclipse.lemminx.services.XMLLanguageService;
import org.eclipse.lemminx.extensions.contentmodel.model.ContentModelManager;
import org.eclipse.lemminx.extensions.contentmodel.settings.XMLFileAssociation;
import org.eclipse.lsp4j.Diagnostic;

public class MultiProjectValidationTest {

    // One XML engine instance
    private static XMLLanguageService xmlService = new XMLLanguageService();

    public static void main(String[] args) throws Exception {

        System.out.println("Starting real validation test...\n");

        setupXMLSettings();

        validate("../test-workspace/projectA/test.xml");
        validate("../test-workspace/projectB/test.xml");
    }

    private static void setupXMLSettings() {
        // Explictly initialize ContentModelPlugin for standalone testing
        org.eclipse.lemminx.extensions.contentmodel.ContentModelPlugin cmPlugin = new org.eclipse.lemminx.extensions.contentmodel.ContentModelPlugin();
        
        // XMLLanguageService itself is the XMLExtensionsRegistry
        cmPlugin.start(new org.eclipse.lsp4j.InitializeParams(), xmlService);
        
        // Fetch ContentModelManager to inject XML associations (similar to configuring XML catalogs via settings in MI LS)
        ContentModelManager contentModelManager = xmlService.getComponent(ContentModelManager.class);
        
        // Map Project A test.xml to schemaA.xsd
        XMLFileAssociation formatA = new XMLFileAssociation();
        formatA.setPattern("**/projectA/test.xml");
        formatA.setSystemId("../test-workspace/projectA/schemaA.xsd");
        
        // Map Project B test.xml to schemaB.xsd
        XMLFileAssociation formatB = new XMLFileAssociation();
        formatB.setPattern("**/projectB/test.xml");
        formatB.setSystemId("../test-workspace/projectB/schemaB.xsd");
        
        // Update the XML engine with these settings
        contentModelManager.setFileAssociations(new XMLFileAssociation[]{formatA, formatB});
    }

    static void validate(String path) throws Exception {

        System.out.println("Validating: " + path);

        // Read XML file
        String xml = new String(Files.readAllBytes(Paths.get(path)));
        System.out.println("XML Length: " + xml.length());
        System.out.println("Content: [" + xml + "]");

        // Create proper URI for pattern matching in LemMinX
        String uri = "file:///" + (path.contains("projectA") ? "projectA" : "projectB") + "/test.xml";

        // Parse XML using LemMinX parser
        DOMDocument document = DOMParser.getInstance().parse(uri, xml, null);

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