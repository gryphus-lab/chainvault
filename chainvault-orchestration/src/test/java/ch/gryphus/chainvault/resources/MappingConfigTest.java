/*
 * Copyright (c) 2026. Gryphus Lab
 */
package ch.gryphus.chainvault.resources;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * Validates the structure and correctness of {@code src/main/resources/mapping-config.yml}.
 *
 * <p>This PR reformatted inline comments (trailing spaces removed) and fixed the missing newline
 * at end-of-file. These tests verify the semantic content remains correct.
 */
class MappingConfigTest {

    private static Map<String, Object> config;

    @BeforeAll
    @SuppressWarnings("unchecked")
    static void loadConfig() throws Exception {
        try (InputStream is =
                MappingConfigTest.class
                        .getClassLoader()
                        .getResourceAsStream("mapping-config.yml")) {
            assertThat(is).as("mapping-config.yml must exist on the main classpath").isNotNull();
            Yaml yaml = new Yaml();
            config = yaml.load(is);
        }
    }

    // -----------------------------------------------------------------------
    // Top-level structure tests
    // -----------------------------------------------------------------------

    @Test
    void mappingConfig_ShouldHaveMappingsKey() {
        assertThat(config).containsKey("mappings");
    }

    @Test
    void mappingConfig_ShouldHaveEnrichmentKey() {
        assertThat(config).containsKey("enrichment");
    }

    // -----------------------------------------------------------------------
    // mappings[] tests
    // -----------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    void mappings_ShouldContainExactlyTwoEntries() {
        List<Map<String, Object>> mappings = (List<Map<String, Object>>) config.get("mappings");
        assertThat(mappings).hasSize(2);
    }

    @Test
    @SuppressWarnings("unchecked")
    void mappings_FirstEntryShouldMapTitleToTitle() {
        List<Map<String, Object>> mappings = (List<Map<String, Object>>) config.get("mappings");
        Map<String, Object> first = mappings.get(0);
        assertThat(first.get("source")).isEqualTo("title");
        assertThat(first.get("target")).isEqualTo("title");
    }

    @Test
    @SuppressWarnings("unchecked")
    void mappings_FirstEntryShouldHaveDcElementsNamespace() {
        List<Map<String, Object>> mappings = (List<Map<String, Object>>) config.get("mappings");
        Map<String, Object> first = mappings.get(0);
        assertThat(first.get("namespace")).isEqualTo("http://purl.org/dc/elements/1.1/");
    }

    @Test
    @SuppressWarnings("unchecked")
    void mappings_SecondEntryShouldMapCreationDateToCreated() {
        List<Map<String, Object>> mappings = (List<Map<String, Object>>) config.get("mappings");
        Map<String, Object> second = mappings.get(1);
        assertThat(second.get("source")).isEqualTo("creationDate");
        assertThat(second.get("target")).isEqualTo("created");
    }

    @Test
    @SuppressWarnings("unchecked")
    void mappings_SecondEntryShouldHaveDcTermsNamespace() {
        List<Map<String, Object>> mappings = (List<Map<String, Object>>) config.get("mappings");
        Map<String, Object> second = mappings.get(1);
        assertThat(second.get("namespace")).isEqualTo("http://purl.org/dc/terms/");
    }

    @Test
    @SuppressWarnings("unchecked")
    void mappings_SecondEntryShouldHaveToIso8601Transform() {
        // PR changed: `transform: "toIso8601"  # comment` → `transform: "toIso8601" # comment`
        // Semantic value must still be "toIso8601"
        List<Map<String, Object>> mappings = (List<Map<String, Object>>) config.get("mappings");
        Map<String, Object> second = mappings.get(1);
        assertThat(second.get("transform")).isEqualTo("toIso8601");
    }

    @Test
    @SuppressWarnings("unchecked")
    void mappings_FirstEntryShouldNotHaveTransform() {
        List<Map<String, Object>> mappings = (List<Map<String, Object>>) config.get("mappings");
        Map<String, Object> first = mappings.get(0);
        assertThat(first).doesNotContainKey("transform");
    }

    // -----------------------------------------------------------------------
    // enrichment.migration tests
    // -----------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    void enrichment_ShouldHaveMigrationSection() {
        Map<String, Object> enrichment = (Map<String, Object>) config.get("enrichment");
        assertThat(enrichment).containsKey("migration");
    }

    @Test
    @SuppressWarnings("unchecked")
    void enrichment_MigrationEventTypeShouldBeMigration() {
        Map<String, Object> enrichment = (Map<String, Object>) config.get("enrichment");
        Map<String, Object> migration = (Map<String, Object>) enrichment.get("migration");
        assertThat(migration.get("eventType")).isEqualTo("Migration");
    }

    @Test
    @SuppressWarnings("unchecked")
    void enrichment_MigrationToolShouldBeSwissArchiveMigrator() {
        Map<String, Object> enrichment = (Map<String, Object>) config.get("enrichment");
        Map<String, Object> migration = (Map<String, Object>) enrichment.get("migration");
        assertThat(migration.get("tool")).isEqualTo("SwissArchiveMigrator v1.0");
    }

    @Test
    @SuppressWarnings("unchecked")
    void enrichment_MigrationEventDateTimeShouldBeNowPlaceholder() {
        // PR changed: `eventDateTime: "${now}"  # placeholder` → `eventDateTime: "${now}" # placeholder`
        // Semantic value remains the placeholder string
        Map<String, Object> enrichment = (Map<String, Object>) config.get("enrichment");
        Map<String, Object> migration = (Map<String, Object>) enrichment.get("migration");
        assertThat(migration.get("eventDateTime")).isEqualTo("${now}");
    }

    // -----------------------------------------------------------------------
    // enrichment.integrity tests
    // -----------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    void enrichment_ShouldHaveIntegritySection() {
        Map<String, Object> enrichment = (Map<String, Object>) config.get("enrichment");
        assertThat(enrichment).containsKey("integrity");
    }

    @Test
    @SuppressWarnings("unchecked")
    void enrichment_IntegrityAlgorithmShouldBeSha256() {
        // PR fixed missing newline at EOF; value must remain "SHA-256"
        Map<String, Object> enrichment = (Map<String, Object>) config.get("enrichment");
        Map<String, Object> integrity = (Map<String, Object>) enrichment.get("integrity");
        assertThat(integrity.get("algorithm")).isEqualTo("SHA-256");
    }

    // -----------------------------------------------------------------------
    // File format / end-of-file tests
    // -----------------------------------------------------------------------

    @Test
    void mappingConfigFile_ShouldNotBeEmpty() throws Exception {
        try (InputStream is =
                MappingConfigTest.class
                        .getClassLoader()
                        .getResourceAsStream("mapping-config.yml")) {
            assertThat(is).isNotNull();
            byte[] bytes = is.readAllBytes();
            assertThat(bytes).isNotEmpty();
            String content = new String(bytes, StandardCharsets.UTF_8);
            assertThat(content.strip()).isNotEmpty();
        }
    }

    @Test
    void mappingConfigFile_ShouldEndWithNewline() throws Exception {
        // PR added a trailing newline to mapping-config.yml
        try (InputStream is =
                MappingConfigTest.class
                        .getClassLoader()
                        .getResourceAsStream("mapping-config.yml")) {
            assertThat(is).isNotNull();
            byte[] bytes = is.readAllBytes();
            String content = new String(bytes, StandardCharsets.UTF_8);
            assertThat(content).endsWith("\n");
        }
    }
}