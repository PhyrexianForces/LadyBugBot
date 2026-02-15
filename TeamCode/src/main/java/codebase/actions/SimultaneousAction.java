package codebase.actions;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class SimultaneousAction implements Action {

    private final ArrayList<Action> actions;

    public SimultaneousAction(Action first, Action... rest) {
        this.actions = new ArrayList<>();

        this.actions.add(first);

        for (Action action : rest) {
            this.add(action, false);
        }
    }

    public SimultaneousAction() {
        this.actions = new ArrayList<>();
    }

    @Override
    public void init() {
        for (Action action : actions) {
            action.init();
        }
    }

    @Override
    public boolean isComplete() {
        for (Action action : actions) {
            if (!action.isComplete()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void loop() {
        ArrayList<Action> toDelete = new ArrayList<>();

        for (Action action : actions) {
            if (!action.isComplete()) {
                action.loop();
            } else {
                toDelete.add(action);
            }
        }

        for (Action action : toDelete) {
            actions.remove(action);
        }
    }

    public void removeActionsOfType(Class<? extends Action> type) {
        ArrayList<Action> toDelete = new ArrayList<>();

        for (Action action : actions) {
            if (action.getClass().equals(type)) {
                toDelete.add(action);
            }
        }

        for (Action action : toDelete) {
            actions.remove(action);
        }
    }

    public void add(@NonNull Action action, boolean init, boolean removeOld) {
        if (removeOld) {
            removeActionsOfType(action.getClass());
        }

        actions.add(action);

        if (init) {
            action.init();
        }
    }

    public void add(@NonNull Action action, boolean init) {
        this.add(action, init, false);
    }

    public ArrayList<Action> getActions() {
        return actions;
    }
}