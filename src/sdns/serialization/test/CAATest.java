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
import sdns.serialization.CAA;
import sdns.serialization.ResourceRecord;
import sdns.serialization.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CAATest
{
    /**
     * test CAA encode method
     * @throws ValidationException
     * if validation fail
     * @throws IOException
     *  if failed or interrupted I/O operations
     */
    @Test
    public void testCAAEncode() throws ValidationException, IOException
    {
        CAA caa = new CAA("ns.name.", 100, "pki.google.");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        caa.encode(outputStream);
        byte[] bytes = outputStream.toByteArray();
        ResourceRecord decode = ResourceRecord.decode(new ByteArrayInputStream(bytes));
        Assert.assertEquals(decode, caa);
    }
}
