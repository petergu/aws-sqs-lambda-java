package net.devfront.aws.sqslambda.consumer.component;

import dagger.Component;
import net.devfront.aws.sqslambda.consumer.module.EnvironmentModule;
import net.devfront.aws.sqslambda.consumer.module.ConsumerModule;
import net.devfront.aws.sqslambda.consumer.service.DequeueService;

import javax.inject.Singleton;

@Singleton
@Component(
    modules = {
        EnvironmentModule.class,
        ConsumerModule.class
    }
)
public interface ConsumerComponent {
    DequeueService getDequeueService();
}
