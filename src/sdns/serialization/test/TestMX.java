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
import sdns.serialization.MX;
import sdns.serialization.ResourceRecord;
import sdns.serialization.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestMX
{
    /**
     * test constructor method
     * @throws ValidationException
     *      if validation fail
     */
    @Test
    public void testConstructor() throws ValidationException
    {
        MX mx = new MX("foo.com.", 500 ,"ns.com.", 3);
        Assert.assertEquals(mx.getName(), "foo.com.");
        Assert.assertEquals(mx.getTTL(), 500);
        Assert.assertEquals(mx.getExchange(), "ns.com.");
        Assert.assertEquals(mx.getPreference(), 3);
    }
    /**
     *  Test bad Token1 in new CName object
     * @throws ValidationException
     *          if validation fail
     */
    @Test(expected = ValidationException.class)
    public void testBadToken1() throws ValidationException
    {
        MX mx = new MX("1foo.com.", 500 ,"ns.com.", 3);

    }
    /**
     *  Test bad Token2 in new CName object
     * @throws ValidationException
     *          if validation fail
     */
    @Test(expected = ValidationException.class)
    public void testBadToken2() throws ValidationException
    {
        MX mx = new MX("foo.com.", 500 ,"1ns.com.", 3);

    }
    /**
     *  Test bad Token3 in new CName object
     * @throws ValidationException
     *          if validation fail
     */
    @Test(expected = ValidationException.class)
    public void testBadToken3() throws ValidationException
    {
        MX mx = new MX("foo.c/om.", 500 ,"ns.com.", 3);

    }

    /**
     * test set exchange method
     * @throws ValidationException
     *       if validation fail
     */
    @Test
    public void testSetExchange() throws ValidationException
    {
        MX mx = new MX("foo.com.", 500 ,"ns.com.", 3);

        mx.setExchange("pw.com.");
        Assert.assertEquals(mx.getExchange(), "pw.com.");
    }

    /**
     * test toString method
     * @throws ValidationException
     *       if validation fail
     */
    @Test
    public void testToString() throws ValidationException
    {
        MX mx = new MX("foo.com.", 500 ,"ns.com.", 3);

        Assert.assertEquals(mx.toString(), "MX: name=foo.com. ttl=500 exchange=ns.com. preference=3");
    }
    /**
     * test encode decode method
     * @throws ValidationException
     *       if validation fail
     * @throws IOException
     *      if I/O operation fails
     */
    @Test
    public void testEncodeDecode() throws ValidationException, IOException
    {
        MX mx = new MX("foo.com.", 500 ,"ns.com.", 3);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mx.encode(outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ResourceRecord resourceRecord = ResourceRecord.decode(inputStream);
        Assert.assertEquals(mx.toString(), resourceRecord.toString());
        Assert.assertEquals(resourceRecord.getTypeValue(), 15);


        Assert.assertEquals(mx.toString(), "MX: name=foo.com. ttl=500 exchange=ns.com. preference=3");
    }
}
