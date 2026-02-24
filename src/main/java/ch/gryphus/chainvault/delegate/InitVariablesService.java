package ch.gryphus.chainvault.delegate;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InitVariablesService implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String docId = (String) execution.getVariable("docId");
        log.info("Initialize variables started for docId:{}", docId);

        // init vars if needed

        log.info("Initialize variables completed for docId:{}", docId);
    }
}
