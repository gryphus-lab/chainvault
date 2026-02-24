package ch.gryphus.chainvault.delegate;

import ch.gryphus.chainvault.domain.MigrationContext;
import ch.gryphus.chainvault.utils.HashUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.Map;

@Slf4j
public class SignDocumentDelegate implements JavaDelegate {
    @SneakyThrows
    @Override
    public void execute(DelegateExecution execution) {
        String docId = (String) execution.getVariable("docId");
        var ctx = new MigrationContext(docId);
        log.info("SignDocumentDelegate started for docId: {}", docId);

        byte[] payloadBytes = new ObjectMapper().writeValueAsBytes(execution.getVariable("payload"));
        String payloadHash = HashUtils.sha256(payloadBytes);
        ctx.setPayloadHash(payloadHash);

        execution.setVariables(Map.of(
                "ctx", new ObjectMapper().writeValueAsString(ctx),
                "payloadBytes", payloadBytes)
        );
        execution.removeVariable("payload");

        log.info("SignDocumentDelegate completed for docId: {}, payloadHash:{}", docId, payloadHash);
    }
}
