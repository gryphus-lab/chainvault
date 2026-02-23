package ch.gryphus.chainvault.delegate;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

@Slf4j
public class HandleErrorDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        log.info("HandleErrorDelegate executed for {}", execution.getProcessInstanceId());
    }
}
