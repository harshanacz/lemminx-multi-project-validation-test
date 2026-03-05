package org.eclipse.lemminx;

import java.nio.file.Files;
import java.nio.file.Paths;

public class MultiProjectValidationTest {

    public static void main(String[] args) throws Exception {

        System.out.println("Starting validation test...\n");

        validate("../test-workspace/projectA/test.xml");
        validate("../test-workspace/projectB/test.xml");

    }

    static void validate(String path) throws Exception {

        System.out.println("Validating: " + path);

        String xml = new String(Files.readAllBytes(Paths.get(path)));

        System.out.println("XML Content:");
        System.out.println(xml);

        System.out.println("-----------------------------\n");
    }
}