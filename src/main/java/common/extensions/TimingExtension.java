package common.extensions;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private final Map<String, Long> start = new ConcurrentHashMap<>();

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        String testName = extensionContext.getRequiredTestClass().getPackageName() + "." + extensionContext.getDisplayName();
        start.put(testName, System.currentTimeMillis());
        System.out.println(LocalDateTime.now() + ": Thread " + Thread.currentThread().getName() + ": Test started " + testName);

    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        String testName = extensionContext.getRequiredTestClass().getPackageName() + "." + extensionContext.getDisplayName();
        long testDuration = System.currentTimeMillis() - start.get(testName);
        System.out.println(LocalDateTime.now() + ": Thread " + Thread.currentThread().getName() + ": Test finished " + testName + ", test duration " + testDuration + " ms");
    }
}
