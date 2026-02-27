package ch.gryphus.chainvault.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Migration context.
 */
@Data
public class MigrationContext {
    private String docId;
    private String payloadHash;
    private String zipHash;
    private String pdfHash;
    private Map<String, String> pageHashes = new HashMap<>();

    /**
     * Add page hash.
     */
    public void addPageHash(String name, String pageHash) {
        pageHashes.put(name, pageHash);
    }
}
