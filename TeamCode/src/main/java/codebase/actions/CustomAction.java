package codebase.actions;

public class CustomAction implements Action {

    private Runnable run;
    private boolean hasRun = false;

    public CustomAction(Runnable run) {
        this.run = run;
    }

    @Override
    public void init() {}

    @Override
    public boolean isComplete() {
        return hasRun;
    }

    @Override
    public void loop() {
        if (!isComplete()) {
            run.run();
            hasRun = true;
        }
    }
}
