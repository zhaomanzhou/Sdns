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
import sdns.serialization.CName;
import sdns.serialization.ResourceRecord;
import sdns.serialization.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class CNameTest
{
    /**
     * test new CName object information
     * @throws ValidationException
     *      if validation fail
     */
    @Test
    public void testConstruct() throws ValidationException
    {
        CName cName = new CName("com.foo.", 10, "ns.name.");
        Assert.assertEquals(cName.getName(), "com.foo.");
        Assert.assertEquals(cName.getTTL(), 10);
        Assert.assertEquals(cName.getCanonicalName(), "ns.name.");
    }

    /**
     *  Test bad Token1 in new CName object
     * @throws ValidationException
     *          if validation fail
     */
    @Test
    public void testBadToken1() throws ValidationException, IOException
    {
        byte[] bytes = {0, 0, 5, 0, 1, 0, 0, 0, 0, 0, 1, 0};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ResourceRecord decode = ResourceRecord.decode(inputStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        decode.encode(outputStream);
        byte[] bytes1 = outputStream.toByteArray();
        System.out.println(Arrays.toString(bytes1));

    }
    /**
     *  Test bad Token2 in new CName object
     * @throws ValidationException
     *          if validation fail
     */
    @Test(expected = ValidationException.class)
    public void testBadToken2() throws ValidationException
    {
        CName cName = new CName("s�ring.", 10, "ns.name.");
    }
    /**
     *  Test bad Token3 in new CName object
     * @throws ValidationException
     *          if validation fail
     */
    @Test(expected = ValidationException.class)
    public void testBadToken3() throws ValidationException
    {
        CName cName = new CName("c,m.foo.", 10, "ns.name.");
    }

    /**
     * Test if the SetCanonicalName method is equal to the result from getCanonicalName
     * @throws ValidationException
     *          if validation fail
     */
    @Test
    public void testSetCanonicalName() throws ValidationException
    {
        CName cName = new CName("com.foo.", 10, "ns.name.");
        cName.setCanonicalName("pw.name.");
        Assert.assertEquals(cName.getCanonicalName(), "pw.name.");
    }

    /**
     * Test the String repesetations are equal
     * @throws ValidationException
     *          if validation fail
     */
    @Test
    public void testToString() throws ValidationException
    {
        CName cName = new CName("foo.com.", 500, "ns.name.");
        Assert.assertEquals(cName.toString(), "CName: name=foo.com. ttl=500 canonicalname=ns.name.");

    }
}
