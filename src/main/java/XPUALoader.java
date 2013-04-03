import actor.ProgressListenerActor;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class XPUALoader {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("LoadingSystem");

        system.actorOf(new Props(ProgressListenerActor.class), "progressListener");
    }

}
