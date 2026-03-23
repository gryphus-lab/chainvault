/*
 * Copyright (c) 2026. Gryphus Lab
 */
package ch.gryphus.chainvault.arch;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AnalyzeClasses(
        packages = "ch.gryphus.chainvault",
        importOptions = ImportOption.DoNotIncludeTests.class)
class MigrationArchitectureTest {

    // TODO: Check and fix violations
    @ArchTest
    static void layered_architecture(JavaClasses classes) {
        var layeredArchitecture =
                layeredArchitecture()
                        .consideringAllDependencies()
                        .layer("Service")
                        .definedBy("..service..")
                        .layer("Domain")
                        .definedBy("..domain..")
                        .layer("Util")
                        .definedBy("..util..")
                        .layer("Config")
                        .definedBy("..config..")
                        .whereLayer("Service")
                        .mayNotBeAccessedByAnyLayer()
                        .whereLayer("Domain")
                        .mayOnlyBeAccessedByLayers("Service")
                        .whereLayer("Util")
                        .mayOnlyBeAccessedByLayers("Service", "Domain")
                        .whereLayer("Config")
                        .mayNotBeAccessedByAnyLayer();

        var result = layeredArchitecture.evaluate(classes);
        if (result.hasViolation()) {
            log.warn(String.valueOf(result.getFailureReport()));
        }
    }
}
