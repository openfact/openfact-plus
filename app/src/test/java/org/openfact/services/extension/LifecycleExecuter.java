package org.openfact.services.extension;

import java.lang.reflect.Method;

import org.jboss.arquillian.container.spi.event.container.AfterDeploy;
import org.jboss.arquillian.container.spi.event.container.AfterUnDeploy;
import org.jboss.arquillian.container.spi.event.container.BeforeDeploy;
import org.jboss.arquillian.container.spi.event.container.BeforeUnDeploy;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestClass;

public class LifecycleExecuter {

    public void executeBeforeDeploy(@Observes BeforeDeploy event, TestClass testClass) {
        execute(testClass.getMethods(org.openfact.services.extension.api.BeforeDeploy.class));
    }

    public void executeAfterDeploy(@Observes AfterDeploy event, TestClass testClass) {
        execute(testClass.getMethods(org.openfact.services.extension.api.AfterDeploy.class));
    }

    public void executeBeforeUnDeploy(@Observes BeforeUnDeploy event, TestClass testClass) {
        execute(testClass.getMethods(org.openfact.services.extension.api.BeforeUnDeploy.class));
    }

    public void executeAfterUnDeploy(@Observes AfterUnDeploy event, TestClass testClass) {
        execute(testClass.getMethods(org.openfact.services.extension.api.AfterUnDeploy.class));
    }

    private void execute(Method[] methods) {
        if (methods == null) {
            return;
        }
        for (Method method : methods) {
            try {
                method.invoke(null);
            } catch (Exception e) {
                throw new RuntimeException("Could not execute @BeforeDeploy method: " + method, e);
            }
        }
    }
}
