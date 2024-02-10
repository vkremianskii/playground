package net.kremianskii.zettlekasten;

import io.javalin.Javalin;
import net.kremianskii.common.rest.Resource;
import net.kremianskii.zettlekasten.data.FilesArchiveRepository;
import net.kremianskii.zettlekasten.resource.ArchiveResource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.nio.file.Files.createTempDirectory;
import static net.kremianskii.common.Checks.checkEqual;
import static net.kremianskii.common.Checks.checkNonNull;
import static net.kremianskii.common.Checks.checkThat;

public final class Application {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition stoppingCondition = lock.newCondition();
    private final Path zettlekastenDirectory;

    private final InetSocketAddress address;

    private State state = State.INITIAL;
    private Javalin javalin;

    public Application(final Path zettlekastenDirectory,
                       final InetSocketAddress address) {
        this.zettlekastenDirectory = checkNonNull(zettlekastenDirectory, "zettlekastenDirectory");
        this.address = checkNonNull(address, "address");
    }

    void start() {
        lock.lock();
        try {
            checkEqual(state, State.INITIAL, "Must be in Initial state");
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
            new ArchiveResource(new FilesArchiveRepository(zettlekastenDirectory)));
    }

    private Javalin startJavalin(final InetSocketAddress address,
                                 final List<? extends Resource> resources) {
        return Javalin.create(config -> {
                config.router.apiBuilder(() ->
                    resources.forEach(Resource::registerRoutes));
            })
            .start(address.getHostName(), address.getPort());
    }

    public static void main(String[] args) throws IOException {
        final var zettlekastenDirectory = createTempDirectory("zettlekasten");
        final var address = new InetSocketAddress("localhost", 8080);
        final var application = new Application(zettlekastenDirectory, address);
        application.start();
        Runtime.getRuntime().addShutdownHook(new Thread(application::requestStop));
        try {
            application.awaitStopping();
        } catch (InterruptedException ignored) {
        }
        application.stop();
    }

    private enum State {
        INITIAL,
        STARTED,
        STOPPING,
        STOPPED
    }
}
