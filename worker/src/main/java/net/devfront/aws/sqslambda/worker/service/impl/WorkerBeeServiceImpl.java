package net.devfront.aws.sqslambda.worker.service.impl;

import net.devfront.aws.sqslambda.worker.model.Event;
import net.devfront.aws.sqslambda.worker.service.WorkerBeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerBeeServiceImpl implements WorkerBeeService {
    private static final Logger logger = LoggerFactory.getLogger(WorkerBeeServiceImpl.class);

    @Override
    public void run(Event event) {

        logger.info("Event handling start");

        logger.info("Event: {}", event.toString());

        for (int i = 0; i < 10000; i++) {
        }

        logger.info("Event handling end");
    }
}
