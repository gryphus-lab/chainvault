/*
 * Copyright (c) 2026. Gryphus Lab
 */
package ch.gryphus.chainvault.arch;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

@AnalyzeClasses(
        packages = "ch.gryphus.chainvault",
        importOptions = ImportOption.DoNotIncludeTests.class)
class OrchestrationArchitectureTest {

    @ArchTest
    static void layered_architecture(JavaClasses classes) {

        var setup =
                layeredArchitecture()
                        .consideringAllDependencies()
                        .layer("Controller")
                        .definedBy("..controller..")
                        .layer("Workflow")
                        .definedBy("..workflow..")
                        .layer("Repository")
                        .definedBy("..repository..")
                        .layer("Model")
                        .definedBy("..model..")

                        // Entry point
                        .whereLayer("Controller")
                        .mayNotBeAccessedByAnyLayer()

                        // Flow
                        .whereLayer("Workflow")
                        .mayOnlyBeAccessedByLayers("Controller")
                        .whereLayer("Repository")
                        .mayOnlyBeAccessedByLayers("Workflow")

                        // Entities can be used by repo + workflow
                        .whereLayer("Model")
                        .mayOnlyBeAccessedByLayers("Workflow", "Repository");

        var result = setup.evaluate(classes);
        if (result.hasViolation()) {
            System.out.println(result.getFailureReport());
        }
    }
}
