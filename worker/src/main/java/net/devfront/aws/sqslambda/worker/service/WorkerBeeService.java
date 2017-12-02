package net.devfront.aws.sqslambda.worker.service;

import net.devfront.aws.sqslambda.worker.model.Event;

public interface WorkerBeeService {
    void run(Event event);
}
