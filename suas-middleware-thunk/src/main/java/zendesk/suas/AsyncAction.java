package zendesk.suas;

public interface AsyncAction {

    void execute(Dispatcher dispatcher, GetState getState);
}
