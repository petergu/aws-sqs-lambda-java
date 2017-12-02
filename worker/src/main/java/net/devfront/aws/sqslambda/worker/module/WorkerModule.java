package net.devfront.aws.sqslambda.worker.module;

import dagger.Module;
import dagger.Provides;
import net.devfront.aws.sqslambda.worker.service.WorkerBeeService;
import net.devfront.aws.sqslambda.worker.service.impl.WorkerBeeServiceImpl;

import javax.inject.Singleton;

@Module
public class WorkerModule {

    @Provides
    @Singleton
    WorkerBeeService provideWorkerBeeService() {
        return new WorkerBeeServiceImpl();
    }
}
