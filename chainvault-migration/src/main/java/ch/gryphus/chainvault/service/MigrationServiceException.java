package ch.gryphus.chainvault.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

/**
 * The type Migration service exception.
 */
public class MigrationServiceException extends RuntimeException {
    /**
     * Instantiates a new Migration service exception.
     *
     * @param docId      the doc id
     * @param statusCode the status code
     * @param headers    the headers
     */
    public MigrationServiceException(String docId, HttpStatusCode statusCode, HttpHeaders headers) {
        super("Unable to find document with id: " + docId + " . See exception details: " + statusCode + " " + headers);
    }
}
