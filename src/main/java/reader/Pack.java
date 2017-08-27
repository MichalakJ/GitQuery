package reader;

import constants.ObjectType;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;

public class Pack {

    static final Logger logger = Logger.getLogger(Pack.class);

    private byte[] fileByte;
    private String packFileName;
    public Pack(String packFileName){
        this.packFileName = packFileName;
    }

    public void init() throws IOException {
        fileByte = FileManager.readFile(packFileName);
        readMetaData();
    }

    public void readMetaData() throws IOException {
        String pack = readMetaPACK();
        String version = readMetaVersion();
        int objectNumber = readNumberOfObjects();
        logger.debug("pack file first 4 bytes: " + pack);
        logger.debug("pack file version: " + version);
        logger.debug("pack file objects number: " + objectNumber);
    }

    public ObjectType getObjectType(int index){
        logger.debug("reading object from pack file");
        int currentByte = FileManager.getUnsignedByte(fileByte, index);
        String currentByteStr = FileManager.toBinary(currentByte);
        String typeBinary = currentByteStr.substring(1, 4);
        StringBuilder size = new StringBuilder(currentByteStr.substring(4, 8));

        while(currentByte>128){
            index++;
            currentByte = FileManager.getUnsignedByte(fileByte, index);
            currentByteStr = FileManager.toBinary(currentByte);
            size.append(currentByteStr.substring(1, 8));
        }
        return ObjectType.getName(Integer.parseInt(typeBinary, 2));
    }

    public String readObject(int index) throws UnsupportedEncodingException, DataFormatException {
        logger.debug("reading object from pack file");
        int currentByte = FileManager.getUnsignedByte(fileByte, index);
        String currentByteStr = FileManager.toBinary(currentByte);
        String typeBinary = currentByteStr.substring(1, 4);
        StringBuilder size = new StringBuilder(currentByteStr.substring(4, 8));

        while(currentByte>128){
            index++;
            currentByte = FileManager.getUnsignedByte(fileByte, index);
            currentByteStr = FileManager.toBinary(currentByte);
            size.append(currentByteStr.substring(1, 8));
        }
        ObjectType type = ObjectType.getName(Integer.parseInt(typeBinary, 2));
        logger.debug("object type: " + type);
        logger.debug("object size: " + size);
        index++;
        String object = "stub";
        if(type == ObjectType.COMMIT || type == ObjectType.TREE || type == ObjectType.BLOB){
            try {
                object = FileManager.decompressObject(FileManager.partArray(fileByte, index, fileByte.length - 1));
            }catch (DataFormatException ex){
                logger.warn(ex);
            }
        }

        logger.debug("decompressed object: " + object);
        return object;
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
}
