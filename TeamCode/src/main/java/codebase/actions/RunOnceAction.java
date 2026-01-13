package codebase.actions;

public abstract class RunOnceAction implements Action {

    boolean hasRun = false;

    @Override
    public void loop() {
        if (!hasRun) {
            run();
            hasRun = true;
        }
    }

    @Override
    public boolean isComplete() {
        return hasRun;
    }

    public void run() {}
}
