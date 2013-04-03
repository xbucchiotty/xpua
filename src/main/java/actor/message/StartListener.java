package actor.message;

import actor.Message;

public class StartListener implements Message{

    public final Integer objective;

    public StartListener(Integer objective) {
        this.objective = objective;
    }
}
