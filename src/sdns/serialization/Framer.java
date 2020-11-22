package sdns.serialization;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 5
 *  Class: CSI 4321
 *
 *************************************************/

import java.io.IOException;
import java.io.InputStream;

public class Framer
{
    /**
     * Frame given message
     * @param message bytes to frame
     * @return framed byte[]
     * @throws ValidationException
     *      if message too long
     */
    public static byte[] frameMesg(byte[] message) throws ValidationException
    {
        if(message.length > 65535)
        {
            throw new ValidationException("too long", "message");
        }
        int length = message.length;
        byte[] header = new byte[2];
        header[0] = (byte) (length >> 8);
        header[1] = (byte) (length & 0x00ff);
        byte[] res = new byte[length + 2];
        System.arraycopy(header,0, res, 0, 2);
        System.arraycopy(message,0,res, 2,length);
        return res;
    }

    /**
     * Get next message
     * @param in byte input source
     * @return message
     * @throws IOException
     *      if I/O problem
     */
    public static byte[] nextMsg(InputStream in) throws IOException
    {
        byte[] header = new byte[2];
        in.read(header, 0, 2);
        int length = 0;
        length =  (header[1] & 0xff);
        length = length | (header[0] << 8);
        byte[] res = new byte[length];
        in.read(res, 0, res.length);
        return res;
    }

}
