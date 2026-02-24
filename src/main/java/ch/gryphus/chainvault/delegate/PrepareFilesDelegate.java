package ch.gryphus.chainvault.delegate;

import ch.gryphus.chainvault.domain.MigrationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

@Slf4j
public class PrepareFilesDelegate implements JavaDelegate {
    @SneakyThrows
    @Override
    public void execute(DelegateExecution execution) {
        String docId = (String) execution.getVariable("docId");
        var ctx = (String) execution.getVariable("ctx");

        log.info("PrepareFilesDelegate started for docId:{}", docId);


        log.info("PrepareFilesDelegate completed for docId:{}", docId);
    }
}
