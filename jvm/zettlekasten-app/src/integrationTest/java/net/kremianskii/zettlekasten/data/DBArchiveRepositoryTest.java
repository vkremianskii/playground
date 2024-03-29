package net.kremianskii.zettlekasten.data;

import com.zaxxer.hikari.HikariDataSource;
import net.kremianskii.zettlekasten.api.CategoryName;
import net.kremianskii.zettlekasten.api.NoteName;
import net.kremianskii.zettlekasten.api.Tag;
import org.jooq.ConnectionProvider;
import org.jooq.impl.DataSourceConnectionProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static net.kremianskii.zettlekasten.ArchiveFixture.anArchive;
import static net.kremianskii.zettlekasten.CategoryFixture.aCategory;
import static net.kremianskii.zettlekasten.NoteFixture.aNote;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DBArchiveRepositoryTest {

    static ConnectionProvider connectionProvider;

    @BeforeAll
    static void setup() {
        var dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:h2:~/zettlekasten;MODE=PostgreSQL");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        connectionProvider = new DataSourceConnectionProvider(dataSource);
    }

    @Test
    void saves_archive_to_database() {
        // given
        var repository = new DBArchiveRepository(connectionProvider);
        var archive = anArchive(
            List.of(aNote(
                new NoteName("name"),
                "text",
                Set.of(new Tag("tag")),
                null)),
            List.of(aCategory(
                new CategoryName("name"),
                null)));

        // expect
        assertDoesNotThrow(() -> repository.save(archive));
    }

    @Test
    void finds_archive_in_database() {
        // given
        var repository = new DBArchiveRepository(connectionProvider);
        var rootCategory = aCategory(
            new CategoryName("root"),
            null);
        var childCategory = aCategory(
            new CategoryName("child"),
            rootCategory.id);
        var archive = anArchive(
            List.of(
                aNote(
                    new NoteName("name1"),
                    "text",
                    Set.of(new Tag("tag")),
                    null),
                aNote(
                    new NoteName("name2"),
                    "text",
                    Set.of(),
                    childCategory.id)),
            List.of(childCategory, rootCategory));
        repository.save(archive);

        // when
        var found = repository.find();

        // then
        assertEquals(Optional.of(archive), found);
    }
}
