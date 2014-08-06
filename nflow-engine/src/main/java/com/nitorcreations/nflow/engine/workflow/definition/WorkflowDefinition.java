package com.nitorcreations.nflow.engine.workflow.definition;

import java.util.EnumSet;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public abstract class WorkflowDefinition<S extends Enum<S> & WorkflowState> extends AbstractWorkflowDefinition<S> {

  private final Set<S> allStates;

  @SuppressFBWarnings(value = "BC_UNCONFIRMED_CAST", justification = "findbugs does not understand that S extends both WorkflowState end Enum")
  protected WorkflowDefinition(String type, S initialState, S errorState) {
    this(type, initialState, errorState, new WorkflowSettings(null));
  }

  @SuppressWarnings("unchecked")
  protected WorkflowDefinition(String type, S initialState, S errorState, WorkflowSettings settings) {
    super(type, initialState, errorState, settings);
    this.allStates = EnumSet.allOf((Class<S>) initialState.getClass());
    requireEnumValuesHaveMatchingMethod();
  }

  @SuppressFBWarnings(value="BC_UNCONFIRMED_CAST_OF_RETURN_VALUE", justification = "findbugs does not understand that S extends both WorkflowState end Enum")
  private void requireEnumValuesHaveMatchingMethod() {
    for (S state : allStates) {
      requireStateMethodExists(state);
    }
  }

  @Override
  public Set<S> getStates() {
    return allStates;
  }

  public WorkflowState getState(String state) {
    for (WorkflowState s : allStates) {
      if (state.equals(s.getName())) {
        return s;
      }
    }
    throw new IllegalStateException("No state '" + state + "' in workflow definiton " + getType());
  }
}
