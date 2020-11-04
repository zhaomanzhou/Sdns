package sdns.serialization.test;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 4
 *  Class: CSI 4321
 *
 *************************************************/

import org.junit.Assert;
import org.junit.Test;
import sdns.serialization.Framer;
import sdns.serialization.Query;
import sdns.serialization.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FramerTest
{
    /**
     * test encode method
     * @throws ValidationException
     *      if validation failed
     */
    @Test
    public void testEncode1() throws ValidationException
    {
        String msg = "hello";
        byte[] bytes = Framer.frameMesg(msg.getBytes());
        byte[] res = {0, 5,  104, 101, 108, 108, 111 };
        Assert.assertArrayEquals(res, bytes);
    }

    /**
     * test encode method
     * @throws ValidationException
     *      if validation failed
     */
    @Test
    public void testEncode2() throws ValidationException
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 65535; i++)
        {
            builder.append("a");
        }
        String msg = builder.toString();
        byte[] bytes = Framer.frameMesg(msg.getBytes());
        Assert.assertEquals(bytes[0], -1);
        Assert.assertEquals(bytes[1], -1);
    }

    /**
     * test encode method
     * @throws ValidationException
     *      if validation failed
     */
    @Test(expected = ValidationException.class)
    public void testEncode3() throws ValidationException
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 65536; i++)
        {
            builder.append("a");
        }
        String msg = builder.toString();
        byte[] bytes = Framer.frameMesg(msg.getBytes());

    }

    /**
     * test decode method
     * @throws ValidationException
     *      if validation failed
     * @throws IOException
     *  if failed or interrupted I/O operations
     */
    @Test
    public void testDecode() throws ValidationException, IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(Framer.frameMesg("hello".getBytes()));
        outputStream.write(Framer.frameMesg("world".getBytes()));

        byte[] bytes = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        byte[] bytes1 = Framer.nextMsg(inputStream);
        byte[] bytes2 = Framer.nextMsg(inputStream);
        Assert.assertArrayEquals(bytes1, "hello".getBytes());
        Assert.assertArrayEquals(bytes2, "world".getBytes());
    }


    @Test
    public void genQuery() throws ValidationException, IOException
    {
        Query query = new Query(10, "google.com.");
        byte[] bytes = Framer.frameMesg(query.encode());
        StringBuilder rawDataBuilder = new StringBuilder();
        for(byte b: bytes)
        {
            String x = Integer.toHexString(b & 0x00ff);
            if(x.length() == 1)
            {
                rawDataBuilder.append("0");
            }
            rawDataBuilder.append(x);
        }
        rawDataBuilder.append("0a");
        System.out.println(rawDataBuilder.toString());
    }
}
