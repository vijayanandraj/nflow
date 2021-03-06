package com.nitorcreations.nflow.engine.workflow.statistics;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", justification = "used by nflow-rest")
public class Statistics {

  public final QueueStatistics queuedStatistics;
  public final QueueStatistics executionStatistics;

  public Statistics(QueueStatistics queuedStatistics, QueueStatistics executionStatistics) {
    this.queuedStatistics = queuedStatistics;
    this.executionStatistics = executionStatistics;
  }

  @SuppressFBWarnings(value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", justification = "used by nflow-rest")
  public static class QueueStatistics {
    public final int count;
    public final Long maxAgeMillis;
    public final Long minAgeMillis;

    public QueueStatistics(int count, Long maxAgeMillis, Long minAgeMillis) {
      this.count = count;
      this.maxAgeMillis = maxAgeMillis;
      this.minAgeMillis = minAgeMillis;
    }
    @Override
    public String toString() {
      return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
  }

  @Override
  public String toString() {
    return reflectionToString(this, SHORT_PREFIX_STYLE);
  }
}
