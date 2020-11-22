package sdns.serialization.test;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 5
 *  Class: CSI 4321
 *
 *************************************************/

import org.junit.Assert;
import org.junit.Test;
import sdns.serialization.ResourceRecord;
import sdns.serialization.SOA;
import sdns.serialization.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SOATest
{
    /**
     * test invaliad name
     * @throws ValidationException
     *      if valid fail
     */
    @Test(expected = ValidationException.class)
    public void testInvalidName() throws ValidationException
    {
        SOA soa = new SOA("ns.name", 100, "manem.ts.", "ps.w.",
                100, 2000, 300, 400, 100);
    }
    /**
     * test invaliad name
     * @throws ValidationException
     *      if valid fail
     */
    @Test(expected = ValidationException.class)
    public void testInvalidmName() throws ValidationException
    {
        SOA soa = new SOA("ns.name.", 100, "manem.ts", "ps.w.",
                100, 2000, 300, 400, 100);
    }
    /**
     * test encode method
     * @throws ValidationException
     *  if valida fail
     * @throws IOException
     *  if I/O fail
     */
    @Test
    public void testEncode() throws ValidationException, IOException
    {
        SOA soa = new SOA("ns.name.", 100, "manem.ts.", "ps.w.",
                100, 2000, 300, 400, 100);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        soa.encode(bos);

    }
    /**
     * test decode method
     * @throws ValidationException
     *  if valida fail
     * @throws IOException
     *  if I/O fail
     */
    @Test
    public void testDecode() throws ValidationException, IOException
    {
        SOA soa = new SOA("ns.name.", 100, "manem.ts.", "ps.w.",
                100, Integer.MAX_VALUE + 1L, 300, 400, 100);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        soa.encode(bos);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ResourceRecord decode = ResourceRecord.decode(bis);
        SOA soa2 = (SOA) decode;
        Assert.assertEquals(soa, soa2);
    }
    /**
     * test write Int method
     * @throws IOException
     * if I/O fail
     */
    @Test
    public void testWriteInt() throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(123);
        dos.close();
        byte[] bytes = bos.toByteArray();
        Assert.assertEquals(bytes.length, 4);
    }

}
