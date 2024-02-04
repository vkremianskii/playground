package net.kremianskii.zettlekasten;

import com.sun.net.httpserver.HttpServer;
import net.kremianskii.common.rest.http.HttpContextConfigurer;
import net.kremianskii.zettlekasten.data.FilesArchiveRepository;
import net.kremianskii.zettlekasten.resource.ArchiveResource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.function.BooleanSupplier;

import static java.nio.file.Files.createTempDirectory;

public final class Application {
    private static final Duration HTTP_SERVER_STOP_DELAY = Duration.ofSeconds(1);

    public static void main(String[] args) throws IOException {
        final var application = new Application();
        final var zettlekastenDirectory = createTempDirectory("zettlekasten");
        application.run(zettlekastenDirectory, () -> false);
    }

    void run(final Path zettlekastenDirectory,
             final BooleanSupplier cancelled) throws IOException {
        final var address = new InetSocketAddress("localhost", 8080);
        final var resources = List.of(new ArchiveResource(new FilesArchiveRepository(zettlekastenDirectory)));
        final var server = startHttpServer(address, resources);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop((int) HTTP_SERVER_STOP_DELAY.toSeconds());
        }));
        while (!cancelled.getAsBoolean()) {
            Thread.yield();
        }
    }

    private HttpServer startHttpServer(final InetSocketAddress address,
                                       final List<? extends HttpContextConfigurer> resources) throws IOException {
        final var server = HttpServer.create(address, 0);
        resources.forEach(resource -> resource.configureContexts(server));
        server.start();
        return server;
    }
}
