package codebase.actions;

import java.util.Arrays;
import java.util.stream.Stream;

public class SequentialAction implements Action {

    protected ActionNode currentActionNode;

    public SequentialAction(Action first, Action... rest) {
        this.currentActionNode = new ActionNode(Stream.concat(Stream.of(first), Arrays.stream(rest)).toArray(Action[]::new));
    }

    @Override
    public void init() {
        currentActionNode.action.init();
    }

    @Override
    public boolean isComplete() {
        return this.currentActionNode == null;
    }

    @Override
    public void loop() {
        if (this.currentActionNode == null) {
            return;
        }

        if (this.currentActionNode.action.isComplete()) {
            this.currentActionNode = this.currentActionNode.next;

            if (this.currentActionNode != null) {
                this.currentActionNode.action.init();
            }
        }

        if (this.currentActionNode == null) {
            return;
        }

        this.currentActionNode.action.loop();
    }

    public Action getRunningAction() {
        return currentActionNode.action;
    }
}

class ActionNode {
    public Action action;
    public ActionNode next;

    public ActionNode(Action[] actions) {
        this.action = actions[0];

        if (actions.length == 1) {
            this.next = null;
            return;
        }

        this.next = new ActionNode(Arrays.copyOfRange(actions, 1, actions.length));
    }
}