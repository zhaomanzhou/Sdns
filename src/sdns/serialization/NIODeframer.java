package sdns.serialization;

public class NIODeframer
{
    public byte[] getMessage(byte[] buffer)
            throws java.lang.NullPointerException
    {
        if(buffer.length <= 2)
            return null;

        byte[] header = new byte[2];
        System.arraycopy(buffer, 0, header, 0,2);
        int length = 0;
        length =  (header[1] & 0xff);
        length = length | (header[0] << 8);
        if(buffer.length < 2+length)
            return null;
        byte[] res = new byte[length];
        System.arraycopy(buffer, 2,res, 0, length);

        byte[] newBuffer = new byte[buffer.length - length - 2];
        System.arraycopy(buffer, 2+length, newBuffer, 0, newBuffer.length);
        buffer = newBuffer;
        return res;
    }
}
