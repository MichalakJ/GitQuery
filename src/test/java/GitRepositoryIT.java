import org.junit.Test;
import query.QueryProcessor;
import query.condition.FilterParserException;
import query.exception.QueryProcessorException;
import reader.GitRepository;

import java.io.IOException;
import java.util.zip.DataFormatException;

/**
 * Created by Jakub on 2016-11-13.
 */
public class GitRepositoryIT {
    private final static String repositoryLocation = "D:\\projects\\loadBalancerKataMto";
    //D:\projects\loadBalancerKataMto

    @Test
    public void test1() throws IOException, DataFormatException {
        GitRepository gitRepository = new GitRepository(repositoryLocation);
        gitRepository.readRepository();
        System.out.println("stop");
    }

    @Test
    public void test2() throws IOException, DataFormatException, FilterParserException, QueryProcessorException {
        GitRepository gitRepository = new GitRepository(repositoryLocation);
        QueryProcessor queryProcessor = new QueryProcessor(gitRepository);
        System.out.println(queryProcessor.query("SELECT commit WHERE author startsWith M"));
    }
}
