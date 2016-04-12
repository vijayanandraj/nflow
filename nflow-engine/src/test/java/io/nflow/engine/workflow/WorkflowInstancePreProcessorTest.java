package io.nflow.engine.workflow;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import io.nflow.engine.internal.executor.BaseNflowTest;
import io.nflow.engine.internal.workflow.WorkflowInstancePreProcessor;
import io.nflow.engine.service.DummyTestWorkflow;
import io.nflow.engine.service.WorkflowDefinitionService;
import io.nflow.engine.workflow.definition.WorkflowDefinition;
import io.nflow.engine.workflow.instance.WorkflowInstance;

public class WorkflowInstancePreProcessorTest extends BaseNflowTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  private WorkflowDefinitionService workflowDefinitionService;

  private WorkflowInstancePreProcessor preProcessor;

  @Before
  public void setup() {
    WorkflowDefinition<?> dummyWorkflow = new DummyTestWorkflow();
    doReturn(dummyWorkflow).when(workflowDefinitionService).getWorkflowDefinition("dummy");
    preProcessor = new WorkflowInstancePreProcessor(workflowDefinitionService);
  }

  @Test
  public void wrongStartStateCausesException() {
    thrown.expect(RuntimeException.class);
    thrown.expectMessage("Specified state [end] is not a start state.");
    WorkflowInstance i = constructWorkflowInstanceBuilder().setExternalId("123").setState("end").build();
    preProcessor.process(i);
  }

  @Test
  public void createsMissingExternalId() {
    WorkflowInstance i = constructWorkflowInstanceBuilder().build();
    WorkflowInstance processed = preProcessor.process(i);
    assertThat(processed.externalId, notNullValue());
  }

  @Test
  public void createsMissingState() {
    WorkflowInstance i = constructWorkflowInstanceBuilder().build();
    WorkflowInstance processed = preProcessor.process(i);
    assertThat(processed.state, is("CreateLoan"));
  }

  @Test
  public void setsStatusToCreatedWhenStatusIsNotSpecified() {
    WorkflowInstance i = constructWorkflowInstanceBuilder().setStatus(null).build();
    WorkflowInstance processed = preProcessor.process(i);
    assertThat(processed.status, is(WorkflowInstance.WorkflowInstanceStatus.created));
  }

  @Test
  public void unsupportedTypeThrowsException() {
    thrown.expect(RuntimeException.class);
    thrown.expectMessage("No workflow definition found for type [nonexistent]");
    WorkflowInstance i = constructWorkflowInstanceBuilder().setType("nonexistent").build();
    preProcessor.process(i);
  }
}
