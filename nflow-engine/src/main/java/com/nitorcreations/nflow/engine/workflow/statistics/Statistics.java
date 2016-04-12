package com.nitorcreations.nflow.engine.workflow.statistics;

import com.nitorcreations.nflow.engine.model.ModelObject;

public class Statistics extends ModelObject {

  public final QueueStatistics queuedStatistics;
  public final QueueStatistics executionStatistics;

  public Statistics(QueueStatistics queuedStatistics, QueueStatistics executionStatistics) {
    this.queuedStatistics = queuedStatistics;
    this.executionStatistics = executionStatistics;
  }

  public static class QueueStatistics extends ModelObject {
    public final int count;
    public final Long maxAgeMillis;
    public final Long minAgeMillis;

    public QueueStatistics(int count, Long maxAgeMillis, Long minAgeMillis) {
      this.count = count;
      this.maxAgeMillis = maxAgeMillis;
      this.minAgeMillis = minAgeMillis;
    }
  }
}
