package be.ugent.zeus.hydra.models.library;

import be.ugent.zeus.hydra.models.ModelTest;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import static be.ugent.zeus.hydra.testing.Utils.defaultVerifier;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
public class LibraryTest extends ModelTest<Library> {

    public LibraryTest() {
        super(Library.class);
    }

    @Test
    public void hasTelephone() throws Exception {
        Library empty = new Library();
        assertFalse(empty.hasTelephone());
        Library full = generate(Library.class);
        assertTrue(full.hasTelephone());
    }

    @Test
    public void equalsAndHash() {
        defaultVerifier(Library.class)
                .withOnlyTheseFields("code")
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}