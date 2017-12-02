package net.devfront.aws.sqslambda.worker;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import net.devfront.aws.sqslambda.worker.component.DaggerWorkerComponent;
import net.devfront.aws.sqslambda.worker.component.WorkerComponent;
import net.devfront.aws.sqslambda.worker.model.Event;

public class Main implements RequestHandler<Event, Object> {

    private final WorkerComponent workerComponent;

    public Main() {
        workerComponent = DaggerWorkerComponent.builder().build();
    }

    @Override
    public Object handleRequest(Event event, Context context) {
        workerComponent.getWorkerBeeService().run(event);
        return null;
    }
}
