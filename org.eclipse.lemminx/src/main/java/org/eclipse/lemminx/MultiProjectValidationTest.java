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
        
        System.out.println("\n--- Testing nested subfolder file ---");
        validate("../test-workspace/projectA/api/apitest.xml");

        System.out.println("\n============== DYNAMIC CONTENT UPDATE (Adding a Connector) ==============");
        System.out.println("Simulating user adding a new connector (e.g. Salesforce). Updating childA.xsd content...\n");
        
        // 1. Write new content to the schema file (simulate adding salesforceConnector tag to MI 4.3.0 schema)
        String newChildSchema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
                "  <xs:element name=\"childComponentA\" type=\"xs:string\"/>\n" +
                "  <xs:element name=\"salesforceConnector\" type=\"xs:string\"/>\n" + 
                "</xs:schema>";
        Files.write(Paths.get("../test-workspace/projectA/childA.xsd"), newChildSchema.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        // 2. Write new XML file that uses this new tag
        String newTestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<projectA>\n" +
                "    <childComponentA>API test is working!</childComponentA>\n" +
                "    <salesforceConnector>Connected via Salesforce!</salesforceConnector>\n" +
                "</projectA>";
        Files.write(Paths.get("../test-workspace/projectA/api/apitest.xml"), newTestXml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        
        // 3. Clear cache / Re-apply associations so LemMinX rebuilds its grammar memory
        ContentModelManager contentModelManager = xmlService.getComponent(ContentModelManager.class);
        XMLFileAssociation formatA = new XMLFileAssociation();
        formatA.setPattern("**/projectA/**/*.xml");
        formatA.setSystemId(Paths.get("../test-workspace/projectA/schemaA.xsd").toAbsolutePath().toUri().toString());
        
        XMLFileAssociation formatB = new XMLFileAssociation();
        formatB.setPattern("**/projectB/**/*.xml");
        formatB.setSystemId(Paths.get("../test-workspace/projectB/schemaB.xsd").toAbsolutePath().toUri().toString());
        
        contentModelManager.setFileAssociations(new XMLFileAssociation[]{formatA, formatB});
        
        // 4. Validate again! It should be completely valid (no errors about salesforceConnector)
        System.out.println("Validating after content update...");
        validate("../test-workspace/projectA/api/apitest.xml");
    }

    private static void setupXMLSettings() {
        org.eclipse.lemminx.extensions.contentmodel.ContentModelPlugin cmPlugin = new org.eclipse.lemminx.extensions.contentmodel.ContentModelPlugin();
        cmPlugin.start(new org.eclipse.lsp4j.InitializeParams(), xmlService);
        
        ContentModelManager contentModelManager = xmlService.getComponent(ContentModelManager.class);
        
        XMLFileAssociation formatA = new XMLFileAssociation();
        formatA.setPattern("**/projectA/**/*.xml");
        formatA.setSystemId(Paths.get("../test-workspace/projectA/schemaA.xsd").toAbsolutePath().toUri().toString());
        
        XMLFileAssociation formatB = new XMLFileAssociation();
        formatB.setPattern("**/projectB/**/*.xml");
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