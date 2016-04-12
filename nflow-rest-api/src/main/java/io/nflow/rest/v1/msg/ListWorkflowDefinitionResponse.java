package io.nflow.rest.v1.msg;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Basic information of workflow definition")
@SuppressFBWarnings(value="URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", justification="jackson reads dto fields")
public class ListWorkflowDefinitionResponse implements Comparable<ListWorkflowDefinitionResponse> {

  @ApiModelProperty(value = "Type of the workflow definition", required=true)
  public String type;

  @ApiModelProperty(value = "Name of the workflow definition", required=true)
  public String name;

  @ApiModelProperty("Description of the workflow definition")
  public String description;

  @ApiModelProperty(value = "Default error state", required = true)
  public String onError;

  @ApiModelProperty(value = "Workflow definition states and transitions", required=true)
  public State[] states;

  @ApiModelProperty(value = "Workflow settings", required=true)
  public Settings settings;


  public static class Settings {

    @ApiModelProperty(value = "Global transition delays for the workflow", required=true)
    public TransitionDelays transitionDelaysInMilliseconds;

    @ApiModelProperty(value = "Maximum retries for a state before moving to failure", required=true)
    public int maxRetries;

  }

  public static class TransitionDelays {

    @ApiModelProperty(value = "Delay in immediate transition", required=true)
    public long immediate;

    @ApiModelProperty(value = "Short delay between transitions", required=true)
    public long waitShort;

    @ApiModelProperty(value = "First retry delay after failure", required=true)
    public long minErrorWait;

    @ApiModelProperty(value = "Maximum delay between failure retries", required=true)
    public long maxErrorWait;

  }

  @Override
  public int compareTo(ListWorkflowDefinitionResponse response) {
    return type.compareTo(response.type);
  }

}
