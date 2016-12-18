package reader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by Jakub on 2016-12-18.
 */
public class PackIndex {
    private String indexPackFile;
    private byte[] fileByte;
    public PackIndex(String indexPackFile) {
        this.indexPackFile = indexPackFile;
    }

    public void init() throws IOException {
        fileByte = FileManager.readFile(indexPackFile);
        readMetaData();
    }

    public void readMetaData() throws IOException {
        String pack = readMetaFirst();
        String version = readMetaVersion();
        System.out.println(pack);
        System.out.println(version);
    }

    private String readMetaVersion() {
        byte[] versionByte = FileManager.partArray(fileByte, 4, 7);

        int byte1 = versionByte[0];
        int byte2 = versionByte[1];
        int byte3 = versionByte[2];
        int byte4 = versionByte[3];
        return ""+byte1+"."+byte2+"."+byte3+"."+byte4;
        //return ""+byte4;
    }

    private String readMetaFirst() throws IOException {
        byte[] packNameByte = FileManager.partArray(fileByte, 0, 3);
        return new String(packNameByte, StandardCharsets.UTF_8);
    }

}
