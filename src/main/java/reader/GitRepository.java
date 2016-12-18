package reader;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;

/**
 * Created by Jakub on 2016-11-13.
 */
public class GitRepository {
    private String repositoryDirectory;
    private String workDirectory;
    private final static String packFileLocation = ".git\\objects\\pack";
    private String packFileDir;
    private byte[] fileByte;

    public GitRepository(String repositoryDirectory, String workDirectory) {
        this.repositoryDirectory = repositoryDirectory;
        this.workDirectory = workDirectory;
        packFileDir = repositoryDirectory + File.separator + packFileLocation + File.separator + "pack-42bb6e044b658d60c1e4d494196dc1445b7623a6.pack";
    }

    public void readRepository() throws IOException, DataFormatException {
        fileByte = FileManager.readFile(packFileDir);
        PackIndex packIndex = new PackIndex(repositoryDirectory + File.separator + packFileLocation + File.separator + "pack-42bb6e044b658d60c1e4d494196dc1445b7623a6.idx");
        packIndex.init();
        readMetaData();
        int index = readObject(12);
        readObject(index);
    }

    public void readMetaData() throws IOException {
        String pack = readMetaPACK();
        String version = readMetaVersion();
        int objectNumber = readNumberOfObjects();
        System.out.println(pack);
        System.out.println(version);
        System.out.println(objectNumber);
    }

    public int readObject(int index) throws UnsupportedEncodingException, DataFormatException {

        int currentByte = FileManager.getUnsignedByte(fileByte, index);
        String currentByteStr = FileManager.toBinary(currentByte);
        String type = currentByteStr.substring(1, 4);
        String size = currentByteStr.substring(4, 8);


        while(currentByte>128){
            index++;
            currentByte = FileManager.getUnsignedByte(fileByte, index);
            currentByteStr = FileManager.toBinary(currentByte);
            size = size + currentByteStr.substring(1,7);
        }
        System.out.println(type);
        System.out.println(size);
        index++;
        String s = FileManager.decompressObject(FileManager.partArray(fileByte, index, 6000));
        System.out.println(s);
        return index + 267;
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

    private String readMetaPACK() throws IOException {
        byte[] packNameByte = FileManager.partArray(fileByte, 0, 3);
        return new String(packNameByte, StandardCharsets.UTF_8);
    }

    private int readNumberOfObjects(){
        byte[] objectsByte = FileManager.partArray(fileByte, 8, 11);
        return java.nio.ByteBuffer.wrap(objectsByte).getInt();
    }



    public String getRepositoryDirectory() {
        return repositoryDirectory;
    }

    public void setRepositoryDirectory(String repositoryDirectory) {
        this.repositoryDirectory = repositoryDirectory;
    }

    public String getWorkDirectory() {
        return workDirectory;
    }

    public void setWorkDirectory(String workDirectory) {
        this.workDirectory = workDirectory;
    }
}
