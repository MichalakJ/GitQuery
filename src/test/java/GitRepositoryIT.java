import org.junit.Test;
import reader.GitRepository;

import java.io.IOException;
import java.util.zip.DataFormatException;

/**
 * Created by Jakub on 2016-11-13.
 */
public class GitRepositoryIT {
    private final static String repositoryLocation = "C:\\Users\\Jakub\\Documents\\gitTest\\ServerTrello";
    private final static String workArea = "C:\\Users\\Jakub\\Documents\\workArea";

    @Test
    public void test1() throws IOException, DataFormatException {
        GitRepository gitRepository = new GitRepository(repositoryLocation, workArea);
        gitRepository.readRepository();
    }
}
