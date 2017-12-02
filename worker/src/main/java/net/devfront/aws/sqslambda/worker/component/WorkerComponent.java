package net.devfront.aws.sqslambda.worker.component;

import dagger.Component;
import net.devfront.aws.sqslambda.worker.module.WorkerModule;
import net.devfront.aws.sqslambda.worker.service.WorkerBeeService;

import javax.inject.Singleton;

@Singleton
@Component(
    modules = {
        WorkerModule.class
    }
)
public interface WorkerComponent {
    WorkerBeeService getWorkerBeeService();
}
