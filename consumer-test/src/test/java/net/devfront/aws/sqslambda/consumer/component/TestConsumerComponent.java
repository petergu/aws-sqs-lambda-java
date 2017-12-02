package net.devfront.aws.sqslambda.consumer.component;

import dagger.Component;
import net.devfront.aws.sqslambda.consumer.module.ConsumerModule;
import net.devfront.aws.sqslambda.consumer.module.TestEnvironmentModuleModule;

import javax.inject.Singleton;

@Singleton
@Component(
    modules = {
        TestEnvironmentModuleModule.class,
        ConsumerModule.class
    }
)
public interface TestConsumerComponent extends ConsumerComponent {
}
