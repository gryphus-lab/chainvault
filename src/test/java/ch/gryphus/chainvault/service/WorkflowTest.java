package ch.gryphus.chainvault.service;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import io.camunda.process.test.api.CamundaAssert;
import io.camunda.process.test.api.CamundaProcessTestContext;
import io.camunda.process.test.api.CamundaSpringProcessTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@CamundaSpringProcessTest
class WorkflowTest {
    @Autowired
    private CamundaClient client;
    @Autowired
    private CamundaProcessTestContext processTestContext;

    @Test
    void test_HappyPathShouldCompleteProcessInstance() {
        // given: the processes are deployed
        client
                .newDeployResourceCommand()
                .addResourceFromClasspath("default_workflow.bpmn")
                .send()
                .join();

        // when
        final ProcessInstanceEvent processInstance = client
                .newCreateInstanceCommand()
                .bpmnProcessId("chainvault")
                .latestVersion()
                .send()
                .join();
        processTestContext.mockJobWorker("extract-and-hash").thenComplete();
        processTestContext.mockJobWorker("sign-document").thenComplete();
        processTestContext.mockJobWorker("prepare-files").thenComplete();
        processTestContext.mockJobWorker("merge-to-pdf").thenComplete();
        processTestContext.mockJobWorker("transform-metadata").thenComplete();
        processTestContext.mockJobWorker("upload-sftp").thenComplete();
        processTestContext.mockJobWorker("handle-error").thenComplete();

        // then
        CamundaAssert.assertThat(processInstance).isCompleted();
    }
}

