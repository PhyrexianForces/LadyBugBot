package codebase.actions;

public class CustomAction extends RunOnceAction {

    private final Runnable runnable;

    public CustomAction(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        runnable.run();
    }

    @Override
    public void init() {}
}
