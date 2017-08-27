package reader;

import constants.ObjectType;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;

import static org.apache.commons.codec.binary.Hex.encodeHexString;
import static reader.FileManager.*;

public class Pack {

    static final Logger logger = Logger.getLogger(Pack.class);

    private byte[] fileByte;
    private String packFileName;
    public Pack(String packFileName){
        this.packFileName = packFileName;
    }

    public void init() throws IOException {
        fileByte = readFile(packFileName);
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
        int currentByte = getUnsignedByte(fileByte, index);
        String currentByteStr = toBinary(currentByte);
        String typeBinary = currentByteStr.substring(1, 4);
        StringBuilder size = new StringBuilder(currentByteStr.substring(4, 8));

        while(currentByte>128){
            index++;
            currentByte = getUnsignedByte(fileByte, index);
            currentByteStr = toBinary(currentByte);
            size.append(currentByteStr.substring(1, 8));
        }
        return ObjectType.getName(Integer.parseInt(typeBinary, 2));
    }

    public byte[] readObject(int index) throws UnsupportedEncodingException, DataFormatException {
        logger.debug("reading object from pack file");
        int currentByte = getUnsignedByte(fileByte, index);
        String currentByteStr = toBinary(currentByte);
        String typeBinary = currentByteStr.substring(1, 4);
        StringBuilder size = new StringBuilder(currentByteStr.substring(4, 8));

        while(currentByte>128){
            index++;
            currentByte = getUnsignedByte(fileByte, index);
            currentByteStr = toBinary(currentByte);
            size.append(currentByteStr.substring(1, 8));
        }
        ObjectType type = ObjectType.getName(Integer.parseInt(typeBinary, 2));
        logger.debug("object type: " + type);
        logger.debug("object size: " + size);
        index++;
        byte[] object = null;
        if(type == ObjectType.COMMIT || type == ObjectType.TREE || type == ObjectType.BLOB){
            object = unpackObject(index, Integer.parseInt(size.toString(), 2));
        }else if(type == ObjectType.REF_DELTA){
            object = getRefDeltaObj(index, Integer.parseInt(size.toString(), 2));
        }
        if(object == null){
            object = "Stub".getBytes();
        }
        logger.debug("decompressed object: " + new String(object, Charset.defaultCharset()));
        return object;
    }

    private byte[] getRefDeltaObj(int index, int size){
        String baseObject = encodeHexString(partArray(fileByte, index, index + 19));
        logger.debug("base object of dif: " + baseObject);
        byte[] dif = null;
        try {
            dif = decompressObject(partArray(fileByte, index+20, fileByte.length-1), size);
        } catch (Exception e){
            logger.warn(e);
        }
        return dif;
    }

    private byte[] unpackObject(int index, int size) throws UnsupportedEncodingException {
        byte[] object = null;
        try {
            object = decompressObject(partArray(fileByte, index, fileByte.length - 1), size);
        }catch (DataFormatException ex){
            logger.warn(ex);
        }
        return object;
    }


    private String readMetaVersion() {
        byte[] versionByte = partArray(fileByte, 4, 7);

        int byte1 = versionByte[0];
        int byte2 = versionByte[1];
        int byte3 = versionByte[2];
        int byte4 = versionByte[3];
        return ""+byte1+"."+byte2+"."+byte3+"."+byte4;
        //return ""+byte4;
    }

    private String readMetaPACK() throws IOException {
        byte[] packNameByte = partArray(fileByte, 0, 3);
        return new String(packNameByte, StandardCharsets.UTF_8);
    }

    private int readNumberOfObjects(){
        byte[] objectsByte = partArray(fileByte, 8, 11);
        return java.nio.ByteBuffer.wrap(objectsByte).getInt();
    }
}
