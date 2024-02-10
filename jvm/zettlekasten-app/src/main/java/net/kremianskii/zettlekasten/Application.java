package net.kremianskii.zettlekasten;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.javalin.Javalin;
import net.kremianskii.common.rest.Resource;
import net.kremianskii.zettlekasten.config.Config;
import net.kremianskii.zettlekasten.data.FilesArchiveRepository;
import net.kremianskii.zettlekasten.resource.ArchiveResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static net.kremianskii.common.Checks.checkEqual;
import static net.kremianskii.common.Checks.checkNonNull;
import static net.kremianskii.common.Checks.checkThat;

public final class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition stoppingCondition = lock.newCondition();
    private final Config config;

    private State state = State.INITIAL;
    private Javalin javalin;

    public Application(final Config config) {
        this.config = checkNonNull(config, "config");
    }

    void start() throws IOException {
        lock.lock();
        try {
            checkEqual(state, State.INITIAL, "Must be in Initial state");
            final var address = new InetSocketAddress(config.server().port());
            if (!Files.exists(config.zettlekasten().dir())) {
                Files.createDirectories(config.zettlekasten().dir());
            }
            javalin = startJavalin(address, resources());
            state = State.STARTED;
        } finally {
            lock.unlock();
        }
    }

    void requestStop() {
        lock.lock();
        try {
            checkEqual(state, State.STARTED, "Must be in Started state");
            state = State.STOPPING;
            stoppingCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    void awaitStopping() throws InterruptedException {
        lock.lock();
        try {
            checkEqual(state, State.STARTED, "Must be in Started state");
            stoppingCondition.await();
        } finally {
            lock.unlock();
        }
    }

    void stop() {
        lock.lock();
        try {
            final var validStates = Set.of(State.STARTED, State.STOPPING);
            checkThat(state, validStates::contains, "Must be in Started or Stopping state");
            javalin.stop();
            state = State.STOPPED;
        } finally {
            lock.unlock();
        }
    }

    private List<Resource> resources() {
        return List.of(
            new ArchiveResource(new FilesArchiveRepository(config.zettlekasten().dir())));
    }

    private Javalin startJavalin(final InetSocketAddress address,
                                 final List<? extends Resource> resources) {
        return Javalin.create(config -> {
                config.router.apiBuilder(() ->
                    resources.forEach(Resource::registerRoutes));
            })
            .start(address.getHostName(), address.getPort());
    }

    public static void main(String[] args) {
        try {
            final var config = parseConfig(URI.create("classpath:/config.yml"));
            final var application = new Application(config);
            application.start();
            Runtime.getRuntime().addShutdownHook(new Thread(application::requestStop));
            try {
                application.awaitStopping();
            } catch (InterruptedException ignored) {
            }
            application.stop();
        } catch (Exception exc) {
            LOGGER.error("Application failure", exc);
        }
    }

    static Config parseConfig(URI uri) throws IOException {
        if (!uri.getScheme().equals("classpath")) {
            throw new IllegalArgumentException("URI must use classpath scheme");
        }
        final var resource = Application.class.getResource(uri.getPath());
        if (resource == null) {
            throw new RuntimeException("Classpath resource not found: " + uri.getPath());
        }
        final var file = new File(resource.getPath());
        final var mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(file, Config.class);
    }

    private enum State {
        INITIAL,
        STARTED,
        STOPPING,
        STOPPED
    }
}
