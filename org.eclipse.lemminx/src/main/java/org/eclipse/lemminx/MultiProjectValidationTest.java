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

        //additional test - dynamic schema swap - no need now.

        // System.out.println("\n============== DYNAMIC SCHEMA SWAP ==============");
        // System.out.println("Changing Project A to use schemaB.xsd instead of schemaA.xsd dynamically...\n");
        
        // ContentModelManager contentModelManager = xmlService.getComponent(ContentModelManager.class);
        
        // XMLFileAssociation formatA = new XMLFileAssociation();
        // formatA.setPattern("**/projectA/test.xml");
        // // Using schema B for Project A now!
        // formatA.setSystemId("../test-workspace/projectB/schemaB.xsd");
        
        // XMLFileAssociation formatB = new XMLFileAssociation();
        // formatB.setPattern("**/projectB/test.xml");
        // formatB.setSystemId("../test-workspace/projectB/schemaB.xsd");
        
        // // Push the new configuration down to LemMinX instantly
        // contentModelManager.setFileAssociations(new XMLFileAssociation[]{formatA, formatB});
        
        // // Re-validate same file, we should now see an error because schemaB doesn't know about <projectA>
        // validate("../test-workspace/projectA/test.xml");
        
    }

    private static void setupXMLSettings() {
        org.eclipse.lemminx.extensions.contentmodel.ContentModelPlugin cmPlugin = new org.eclipse.lemminx.extensions.contentmodel.ContentModelPlugin();
        cmPlugin.start(new org.eclipse.lsp4j.InitializeParams(), xmlService);
        
        ContentModelManager contentModelManager = xmlService.getComponent(ContentModelManager.class);
        
        XMLFileAssociation formatA = new XMLFileAssociation();
        formatA.setPattern("**/*projectA*test.xml");
        formatA.setSystemId(Paths.get("../test-workspace/projectA/schemaA.xsd").toAbsolutePath().toUri().toString());
        
        XMLFileAssociation formatB = new XMLFileAssociation();
        formatB.setPattern("**/*projectB*test.xml");
        formatB.setSystemId(Paths.get("../test-workspace/projectB/schemaB.xsd").toAbsolutePath().toUri().toString());
        
        contentModelManager.setFileAssociations(new XMLFileAssociation[]{formatA, formatB});
    }

    static void validate(String path) throws Exception {

        System.out.println("Validating: " + path);
        
        String xml = new String(Files.readAllBytes(Paths.get(path)), java.nio.charset.StandardCharsets.UTF_8);
        xml = xml.trim(); // strip leading whitespaces that ruin prolog

        String uri = Paths.get(path).toAbsolutePath().toUri().toString();

        DOMDocument document = DOMParser.getInstance().parse(uri, xml, null);

        List<Diagnostic> diagnostics = xmlService.doDiagnostics(
                document,
                null,
                null,
                () -> {}
        );

        System.out.println("Diagnostics: " + diagnostics);
        System.out.println("-----------------------------\n");
    }
}