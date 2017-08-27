package reader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * Created by Jakub on 2016-11-13.
 */
public class FileManager {

    public static byte[] readFile(String sourcePath) throws IOException {
        Path path = Paths.get(sourcePath);
        return Files.readAllBytes(path);
    }

    public static byte[] partArray(byte[] array, int start, int end) {
        int size =  end - start + 1;
        byte[] part = new byte[size];
        System.arraycopy(array, start, part, 0, size);
        return part;
    }

    public static int getUnsignedByte(byte[] array, int index){
        byte currentByte = FileManager.partArray(array, index, index)[0];
        int anUnsignedByte = (int) currentByte & 0xff;
        return anUnsignedByte;
    }

    public static String toBinary(int obj){
        return String.format("%8s", Integer.toBinaryString(obj & 0xFF)).replace(' ', '0');
    }

    public static String decompressObject(byte[] array) throws UnsupportedEncodingException, DataFormatException {
        // Decompress the bytes
        Inflater decompresser = new Inflater();
        decompresser.setInput(array);
        byte[] result = new byte[10000000];
        int resultLength = decompresser.inflate(result);
        decompresser.end();
        return new String(result, 0, resultLength, "UTF-8");
    }

    public static int toInt(byte[] array){
        return java.nio.ByteBuffer.wrap(array).getInt();
    }

}
