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
import sdns.serialization.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class TestResourceRecord
{
    /**
     * Test whether the CName object  has the same results as output1
     *
     * @throws ValidationException
     *      if validation fail
     * @throws IOException
     *      if I/O problem
     */
    @Test
    public void testEncodeDecodeCName1() throws ValidationException, IOException
    {
        CName cName = new CName("com.foo.", 10, "ns.name.");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        cName.encode(outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ResourceRecord resourceRecord = ResourceRecord.decode(inputStream);
        Assert.assertEquals(cName.toString(), resourceRecord.toString());
        Assert.assertEquals(resourceRecord.getTypeValue(), 5);

    }

    /**
     * Test whether the CName object  has the same results as output2
     *
     * @throws ValidationException
     *      if validation fail
     * @throws IOException
     *      if I/O problem
     */
    @Test
    public void testEncodeDecodeCName2() throws ValidationException, IOException
    {
        CName cName = new CName("com.fff.", 10000, "ns.name.");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        cName.encode(outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ResourceRecord resourceRecord = ResourceRecord.decode(inputStream);
        Assert.assertEquals(cName.toString(), resourceRecord.toString());
        Assert.assertEquals(resourceRecord.getName(), "com.fff.");
        Assert.assertEquals(resourceRecord.getTTL(), 10000);
        Assert.assertEquals(((CName)resourceRecord).getCanonicalName(), "ns.name.");
        Assert.assertEquals(resourceRecord.getTypeValue(), 5);
    }

    /**
     * Test whether the NS object has the same result as output1
     *
     * @throws ValidationException
     *      if validation fail
     * @throws IOException
     *      if I/O problem
     */
    @Test
    public void testEncodeDecodeNS() throws ValidationException, IOException
    {
        NS ns = new NS("com.foo.", 10, "ns.name.");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ns.encode(outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ResourceRecord resourceRecord = ResourceRecord.decode(inputStream);
        Assert.assertEquals(ns.toString(), resourceRecord.toString());
        Assert.assertEquals(resourceRecord.getTypeValue(), 2);

    }

    /**
     * Test whether the NS object has the same result as output2
     *
     * @throws ValidationException
     *      if validation fail
     * @throws IOException
     *      if I/O problem
     */
    @Test
    public void testEncodeDecodeNS2() throws ValidationException, IOException
    {
        NS ns = new NS("com.fff.", 10000, "ns.name.");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ns.encode(outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ResourceRecord resourceRecord = ResourceRecord.decode(inputStream);
        Assert.assertEquals(ns.toString(), resourceRecord.toString());
        Assert.assertEquals(resourceRecord.getName(), "com.fff.");
        Assert.assertEquals(resourceRecord.getTTL(), 10000);
        Assert.assertEquals(((NS)resourceRecord).getNameServer(), "ns.name.");
        Assert.assertEquals(resourceRecord.getTypeValue(), 2);
    }
    /**
     * test decode method from AAAA clas
     * @throws ValidationException
     *      if validation fail
     * @throws IOException
     *      if I/O problem
     */
    @Test
    public void testDecodeAAAA() throws ValidationException, IOException
    {
        byte[] bytes = {1, 102, 1, 99, 0, 0, 28, 0, 1, 0, 0, 1, 65, 0, 16, 1, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 1, 7, 1, 8};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ResourceRecord resourceRecord = ResourceRecord.decode(inputStream);
        AAAA resourceRecord1 = (AAAA) resourceRecord;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resourceRecord.encode(outputStream);
        System.out.println(Arrays.toString(outputStream.toByteArray()));
    }


}
