package reader;

import model.Commit;
import model.GitObject;
import model.ObjectData;
import model.Repository;
import model.factory.ObjectFactory;
import org.apache.log4j.Logger;
import constants.ObjectType;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;
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



    public Repository readRepository() throws IOException, DataFormatException {

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
            byte[] object = reconstructObject(fourthLevel.get(integer), objectTypes.get(integer));

            logger.debug(new String(object, Charset.forName("UTF-8")));
        }
        return repository;


    }

    private byte[] reconstructObject(Integer offset, ObjectType objectType) throws UnsupportedEncodingException, DataFormatException {
        ObjectData objectData = pack.readObject(offset);
        if(objectType == ObjectType.OFS_DELTA){
            return "not-implemented".getBytes();
        }
        else if(objectType == ObjectType.REF_DELTA){
            return "not-implemented".getBytes();
        }else{
            return objectData.getData();
        }
    }

    private Map<Integer, GitObject> createRepositoryData(Map<Integer, String> secondLevel, Map<Integer, Integer> fourthLevel, Map<Integer, ObjectType> objectTypes) {
        Map<Integer, GitObject> repositoryData = new HashMap<>();
        for (Integer index : secondLevel.keySet()) {
            GitObject obj = ObjectFactory.createObject(objectTypes.get(index), secondLevel.get(index),fourthLevel.get(index));
            if(obj.getType() == ObjectType.COMMIT){
                addCommitData((Commit)obj, pack.readObject(fourthLevel.get(index)).getData());
            }
            repositoryData.put(index, obj);
        }
        return repositoryData;
    }

    private void addCommitData(Commit obj, byte[] data) {
        try {
            String stringData = new String(data, "UTF-8");
            String[] parts = stringData.split("\n");
            if(parts.length>5) {
                obj.setTree(parts[0]);
                obj.setParent(parts[1]);
                obj.setComment(parts[5]);
                String[] authorData = parts[2].split("<");
                if(authorData.length<2){
                    logger.info("cannot parse commit author data");
                }else{
                    obj.setAuthor(authorData[0].substring(7));
                    String rest[] = authorData[1].split(" ");
                    obj.setEmail(rest[0].substring(0, rest[0].length() - 1));
                    obj.setDate(new Date(Long.parseLong(rest[1]) * 1000));
                }


            }else{
                logger.info("invalid commit format");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        logger.info(obj.toString());
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
