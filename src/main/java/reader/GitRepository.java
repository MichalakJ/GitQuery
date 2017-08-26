package reader;

import org.apache.log4j.Logger;

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
    private final static String packFileLocation = ".git\\objects\\pack";
    private String packFileDir;
    private String idxFileDir;
    private byte[] fileByte;

    static final Logger logger = Logger.getLogger(GitRepository.class);

    public GitRepository(String repositoryDirectory) {
        this.repositoryDirectory = repositoryDirectory;
        packFileDir = getPackFile();
        idxFileDir = getPackIndexFile();
    }



    public void readRepository() throws IOException, DataFormatException {
        fileByte = FileManager.readFile(packFileDir);
        PackIndex packIndex = new PackIndex(idxFileDir);
        packIndex.init();
        packIndex.read();
        readMetaData();
        int index = readObject(12);
        readObject(index);
    }

    public void readMetaData() throws IOException {
        String pack = readMetaPACK();
        String version = readMetaVersion();
        int objectNumber = readNumberOfObjects();
        logger.debug("pack file first 4 bytes: " + pack);
        logger.debug("pack file version: " + version);
        logger.debug("pack file objects number: " + objectNumber);
    }

    public int readObject(int index) throws UnsupportedEncodingException, DataFormatException {
        logger.debug("reading object from pack file");
        int currentByte = FileManager.getUnsignedByte(fileByte, index);
        String currentByteStr = FileManager.toBinary(currentByte);
        String type = currentByteStr.substring(1, 4);
        String size = currentByteStr.substring(4, 8);


        while(currentByte>128){
            index++;
            currentByte = FileManager.getUnsignedByte(fileByte, index);
            currentByteStr = FileManager.toBinary(currentByte);
            size = size + currentByteStr.substring(1,8);
        }
        logger.debug("object type: " + type);
        logger.debug("object size: " + size);
        index++;
        String object = FileManager.decompressObject(FileManager.partArray(fileByte, index, 6000));
        logger.debug("decompressed object: " + object);
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
