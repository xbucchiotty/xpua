package actor;

import actor.message.Done;
import actor.message.StartListener;
import akka.actor.UntypedActor;

public class ProgressListenerActor extends UntypedActor {

    private Integer successCount;
    private Integer errorCount;
    private Integer objective;
    private Long startTime;

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof StartListener) {
            successCount = 0;
            errorCount = 0;
            objective = ((StartListener) message).objective;
            startTime = System.currentTimeMillis();
            printStatus("Starting...");
        } else {
            if (message instanceof Done) {
                successCount++;
                printStatus(((Done) message).message);
            } else {
                if (message instanceof Throwable) {
                    unhandled(((Throwable) message).getMessage());
                } else {
                    unhandled(message);
                }
            }
        }


    }

    @Override
    public void unhandled(Object message) {
        System.err.println(String.format("Error %s", message));
        errorCount++;
        printStatus(message.toString());
    }

    private void printStatus(String message) {
        if (totalCount() % scale() == 0 || totalCount().equals(objective)) {
            System.out.println(String.format("Progression: %3.0f%% %5d/%-5d in %5d(ms) Success: %5d, Error:%5d.\t[%-40s]",
                    (double)totalCount() / objective * 100d,
                    totalCount(),
                    objective,
                    System.currentTimeMillis() - startTime,
                    successCount,
                    errorCount,
                    message));
        }
    }

    private Integer totalCount() {
        return successCount + errorCount;
    }

    private Integer scale() {
        return (objective / 20) > 0 ? (objective / 20) : 1;
    }
}
