package reader;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;

/**
 * Created by Jakub on 2016-12-18.
 */
public class PackIndex {
    private String indexPackFile;
    private byte[] fileByte;
    static final Logger logger = Logger.getLogger(PackIndex.class);

    public PackIndex(String indexPackFile) {
        this.indexPackFile = indexPackFile;
    }

    public void init() throws IOException {
        fileByte = FileManager.readFile(indexPackFile);
        readMetaData();
    }

    public Map<Integer, Integer> read() {
        Map<Integer, Integer> firstLevelEntries = readFirstLevelEntries();
        Integer numberOjObjects = firstLevelEntries.get(255); //last entry contains number of all objects
        readSecondLevelEntries(numberOjObjects);
        readThirdLevelEntries(numberOjObjects);
        return readFourthLevelEntries(numberOjObjects);
    }

    private void readMetaData() throws IOException {
        String pack = readMetaFirst();
        String version = readMetaVersion();
        logger.debug("index pack file first 4 bytes: " + pack);
        logger.debug("index pack file version: " + version);

    }

    private Map<Integer, Integer> readFirstLevelEntries() {
        byte[] firstLevelArray = FileManager.partArray(fileByte, 8, 1023 + 8);
        Map<Integer, Integer> firstLevel = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            firstLevel.put(i, ByteBuffer.wrap(FileManager.partArray(firstLevelArray, i * 4, i * 4 + 3)).getInt());
        }
        logger.debug("first level entries");
        for (Integer integer : firstLevel.keySet()) {
            logger.debug(integer.toString() + ": " + firstLevel.get(integer));
        }
        return firstLevel;
    }

    private Map<Integer, String> readSecondLevelEntries(int numberOfObject) {
        int secondLevelStart = 1023 + 8 + 1; //this is where first level ends + 1
        int secondLevelEnd = secondLevelStart + numberOfObject * 20 - 1; //second level has 20 byte entries for each object
        byte[] secondLevelArray = FileManager.partArray(fileByte, secondLevelStart, secondLevelEnd);
        Map<Integer, String> secondLevel = new HashMap<>();
        logger.debug("second level entries");
        for (int i = 0; i < numberOfObject; i++) {
            String entry = Hex.encodeHexString(FileManager.partArray(secondLevelArray, i * 20, i * 20 + 19));
            logger.debug(i + ":" + entry);
            secondLevel.put(i, entry);
        }
        return secondLevel;
    }

    private Map<Integer, String> readThirdLevelEntries(int numerOfObjects) {
        int thirdLevelStart = 1023 + 8 + 20 * numerOfObjects + 1; //this is where second level ends +1
        int thirdLevelEnd = thirdLevelStart + 4 * numerOfObjects - 1;
        byte[] thirdLevelArray = FileManager.partArray(fileByte, thirdLevelStart, thirdLevelEnd);
        Map<Integer, String> thirdLevel = new HashMap<>();
        logger.debug("third level entries");
        for (int i = 0; i < numerOfObjects; i++) {
            String entry = Hex.encodeHexString(FileManager.partArray(thirdLevelArray, i * 4, i * 4 + 3));
            logger.debug(entry);
            thirdLevel.put(i, entry);
        }
        return thirdLevel;
    }

    private Map<Integer, Integer> readFourthLevelEntries(int numberOfObjects) {
        int fourthLevelStart = 8 + 1024 + 20 * numberOfObjects + 4 * numberOfObjects;
        int fourthLevelEnd = fourthLevelStart + numberOfObjects * 4;
        byte[] fourthLevelArray = FileManager.partArray(fileByte, fourthLevelStart, fourthLevelEnd);
        Map<Integer, Integer> fourthLevel = new HashMap<>();
        logger.debug("fourth level entries");
        for (int i = 0; i < numberOfObjects; i++) {
            int entry = FileManager.toInt(FileManager.partArray(fourthLevelArray, i * 4, i * 4 + 3));
            fourthLevel.put(i, entry);
            logger.debug(i + " : " + entry);
        }

        return fourthLevel;
    }


    private String readMetaVersion() {
        byte[] versionByte = FileManager.partArray(fileByte, 4, 7);

        int byte1 = versionByte[0];
        int byte2 = versionByte[1];
        int byte3 = versionByte[2];
        int byte4 = versionByte[3];
        return "" + byte1 + "." + byte2 + "." + byte3 + "." + byte4;
        //return ""+byte4;
    }

    private String readMetaFirst() throws IOException {
        byte[] packNameByte = FileManager.partArray(fileByte, 0, 3);
        return new String(packNameByte, StandardCharsets.UTF_8);
    }


}
