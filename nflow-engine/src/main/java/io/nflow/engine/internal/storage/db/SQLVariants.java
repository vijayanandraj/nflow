package io.nflow.engine.internal.storage.db;

import io.nflow.engine.workflow.instance.WorkflowInstance.WorkflowInstanceStatus;

public interface SQLVariants {
  String currentTimePlusSeconds(int seconds);

  boolean hasUpdateReturning();

  String workflowStatus(WorkflowInstanceStatus status);

  String workflowStatus();

  String actionType();

  boolean hasUpdateableCTE();

  String nextActivationUpdate();

  String castToText();

  String limit(String query, String limit);

  int longTextType();

  boolean useBatchUpdate();
}
