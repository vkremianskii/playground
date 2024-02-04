package net.kremianskii.zettlekasten;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ApplicationFixture {
    static Application application;
    static AtomicBoolean cancelled;
    static Thread thread;

    @BeforeAll
    static void setup() {
        application = new Application();
        cancelled = new AtomicBoolean(false);
        thread = new Thread(() -> {
            try {
                application.run(cancelled::get);
            } catch (final IOException ignored) {
            }
        });
        thread.start();
    }

    @AfterAll
    static void tearDown() throws InterruptedException {
        cancelled.set(true);
        thread.join();
    }
}
