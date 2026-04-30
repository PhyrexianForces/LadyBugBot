package codebase.telemetry_viewer.websocket.packets;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import codebase.actions.Action;
//import codebase.actions.ActionNode;
import codebase.actions.ActionParameter;
import codebase.actions.SequentialAction;
import codebase.actions.SimultaneousAction;
import codebase.geometry.FieldPosition;

public class TelemetryUpdatePacket extends TelemetryPacket {
    @SerializedName("telemetryDataName")
    public String telemetryDataName;

    @SerializedName("telemetryDataType")
    public TelemetryDataType telemetryDataType;

    @SerializedName("telemetryDataValue")
    public Object telemetryDataValue;

    public enum TelemetryDataType {
        @SerializedName("integer")
        INTEGER(Integer.class),

        @SerializedName("double")
        DOUBLE(Double.class),

        @SerializedName("string")
        STRING(String.class),

        @SerializedName("fieldPosition")
        FIELD_POSITION(TelemetryFieldPosition.class),

        @SerializedName("actionQueue")
        ACTION_QUEUE(TelemetryActionQueue.class);

        public final Class<?> metricTypeClass;

        TelemetryDataType(Class<?> metricTypeClass) {
            this.metricTypeClass = metricTypeClass;
        }
    }

    public static class TelemetryFieldPosition {
        @SerializedName("x")
        public double x;
        @SerializedName("y")
        public double y;

        @SerializedName("direction")
        public double direction;

        public TelemetryFieldPosition(FieldPosition fieldPosition) {
            this.x = fieldPosition.x;
            this.y = fieldPosition.y;
            this.direction = fieldPosition.direction;
        }
    }

    public static class TelemetryActionQueue {
        @SerializedName("actionQueue")
        public List<TelemetryAction> actionQueue;

        public TelemetryActionQueue(SequentialAction sequentialAction) {
            actionQueue = getSubActionsAsTelemetryActions(sequentialAction);
        }

        private List<TelemetryAction> getSubActionsAsTelemetryActions(Action action) {
            List<TelemetryAction> telemetryActions = new ArrayList<>();

            for (Action subAction : getSubActions(action)) {
                try {
                    telemetryActions.add(getTelemetryActionFromAction(subAction));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            return telemetryActions;
        }

        private List<Action> getSubActions(Action action) {
            if (action instanceof SimultaneousAction) {
                return ((SimultaneousAction) action).getActions();
            } else if (action instanceof  SequentialAction) {
                List<Action> actions = new ArrayList<>();

               // for(ActionNode node = ((SequentialAction) action).getCurrentActionNode(); node != null; node = node.next) {
              //      actions.add(node.action);
             //   }

                return actions;
            }

            return new ArrayList<>();
        }

        private TelemetryAction getTelemetryActionFromAction(Action action) throws IllegalAccessException {
            TelemetryAction telemetryAction = new TelemetryAction();

            List<String> actionParameters = new ArrayList<>();

            telemetryAction.actionName = action.getClass().getSimpleName();
            for (Field field : action.getClass().getFields()) {
                if (field.isAnnotationPresent(ActionParameter.class)) {
                    String parameterValue = Objects.requireNonNull(field.get(action.getClass())).toString();
                    actionParameters.add(parameterValue.isEmpty() ? "(null)" : parameterValue);
                }
            }

            telemetryAction.actionParameters = "(" + String.join(", ", actionParameters) + ")";

            telemetryAction.subActions = getSubActionsAsTelemetryActions(action);

            return telemetryAction;
        }
    }

    public static class TelemetryAction {
        @SerializedName("actionName")
        public String actionName;

        @SerializedName("actionParameters")
        public String actionParameters;

        @SerializedName("subActions")
        public List<TelemetryAction> subActions;
    }
}
