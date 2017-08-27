package reader;

import model.GitObject;
import model.Repository;
import org.apache.log4j.Logger;
import constants.ObjectType;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * Created by Jakub on 2016-11-13.
 */
public class GitRepository {
    private String repositoryDirectory;
    private final static String packFileLocation = ".git\\objects\\pack";

    private Repository repository = new Repository();
    private PackIndex packIndex;
    private Pack pack;


    static final Logger logger = Logger.getLogger(GitRepository.class);

    public GitRepository(String repositoryDirectory) {
        this.repositoryDirectory = repositoryDirectory;
        pack = new Pack(getPackFile());
        packIndex = new PackIndex(getPackIndexFile());
    }



    public void readRepository() throws IOException, DataFormatException {

        packIndex.init();
        pack.init();

        Map<Integer, Integer> firstLevel = packIndex.readFirstLevelEntries();
        int numberOfObjects = firstLevel.get(255);
        Map<Integer, String> secondLevel = packIndex.readSecondLevelEntries(numberOfObjects);
        Map<Integer, Integer> fourthLevel = packIndex.readFourthLevelEntries(numberOfObjects);
        //Map<Integer, Integer> offSetMap = packIndex.read();
        //readMainRepository(offSetMap);
        Map<Integer, ObjectType> objectTypes = getObjectTypes(fourthLevel);
        repository.setObjects(createRepositoryData(secondLevel, fourthLevel, objectTypes));

        logger.debug(repository.getObjects().toString());
        for (Integer integer : fourthLevel.keySet()) {
            logger.debug(new String(pack.readObject(fourthLevel.get(integer)), Charset.forName("UTF-8")));
        }

    }

    private Map<Integer, GitObject> createRepositoryData(Map<Integer, String> secondLevel, Map<Integer, Integer> fourthLevel, Map<Integer, ObjectType> objectTypes) {
        Map<Integer, GitObject> repositoryData = new HashMap<>();
        for (Integer index : secondLevel.keySet()) {
            repositoryData.put(index, new GitObject(objectTypes.get(index), secondLevel.get(index),fourthLevel.get(index)));
        }
        return repositoryData;
    }

    private Map<Integer,ObjectType> getObjectTypes(Map<Integer, Integer> fourthLevel) {
        Map<Integer, ObjectType> types = new HashMap<>();
        for (Integer key : fourthLevel.keySet()) {
            types.put(key, pack.getObjectType(fourthLevel.get(key)));
        }
        return types;
    }

    private void readMainRepository(Map<Integer, Integer> offSetMap) throws UnsupportedEncodingException, DataFormatException {
        List repositoryData = new ArrayList<String>();
        for (Integer key : offSetMap.keySet()) {
            logger.debug(key);
            repositoryData.add(pack.readObject(offSetMap.get(key)));

        }
    }





    public String getRepositoryDirectory() {
        return repositoryDirectory;
    }

    public void setRepositoryDirectory(String repositoryDirectory) {
        this.repositoryDirectory = repositoryDirectory;
    }

    private String getPackIndexFile() {
        String sourceLocation = repositoryDirectory + File.separator + packFileLocation;
        File packDirectory = new File(sourceLocation);
        for (String file : packDirectory.list()) {
            if(file.endsWith(".idx")){
                logger.debug("detected index file: " + file);
                return sourceLocation + File.separator + file;
            }
        }
        return null;
    }

    private String getPackFile() {
        String sourceLocation = repositoryDirectory + File.separator + packFileLocation;
        File packDirectory = new File(sourceLocation);
        for (String file : packDirectory.list()) {
            if(file.endsWith(".pack")){
                logger.debug("detected pack file: " + file);
                return sourceLocation + File.separator + file;
            }
        }
        return null;
    }
}
