package reader;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

    public void read(){
        readFirstLevelEntries();
    }

    public void readMetaData() throws IOException {
        String pack = readMetaFirst();
        String version = readMetaVersion();
        logger.debug("index pack file first 4 bytes: " + pack);
        logger.debug("index pack file version: " + version);

    }

    public Map<Integer, Integer> readFirstLevelEntries() {
        byte[] firstLevelArray = FileManager.partArray(fileByte, 8, 1024 + 8);
        Map<Integer, Integer> firstLevel = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            firstLevel.put(i, ByteBuffer.wrap(FileManager.partArray(firstLevelArray, i*4, i*4+3)).getInt());
        }
        for (Integer integer : firstLevel.keySet()) {
            logger.debug(integer.toString() + ": " + firstLevel.get(integer));
        }
        return firstLevel;
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
