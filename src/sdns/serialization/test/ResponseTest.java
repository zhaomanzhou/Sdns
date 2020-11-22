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
import sdns.serialization.*;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

public class ResponseTest
{

    /**
     * test with null query
     *
     * @throws ValidationException
     *          if validation fail
     */
    @Test(expected = ValidationException.class)
    public void testNullQuery() throws ValidationException
    {
        Response response = new Response(10, null, RCode.getRCode(0));
    }

    /**
     * test contructor that creates new object
     *
     * @throws ValidationException
     *          if validation fail
     */
    @Test
    public void testConstructor() throws ValidationException
    {
        Response response = new Response(10, "ns.com.", RCode.getRCode(0));
    }

    /**
     * test the get response that will get the same result with set presonse
     *
     * @throws ValidationException
     *          if validation fail
     */
    @Test
    public void testGetSetResponseCode() throws ValidationException
    {
        Response response = new Response(10, "ns.com.",RCode.getRCode(0) );
        response.setRCode(RCode.getRCode(3));

        Assert.assertEquals(response.getRCode(), RCode.getRCode(2));
    }

    /**
     *test the answer list with add answer method on new obejct
     *
     * @throws ValidationException
     *          if validation fail
     */
    @Test
    public void testAnswerList() throws ValidationException
    {
        Response response = new Response(10, "ns.com.", RCode.getRCode(0));
        Assert.assertEquals(response.getAnswerList().size(), 0);
        NS ns = new NS("foo.com.", 500, "ns.com.");
        response.addAnswer(ns);
        Assert.assertArrayEquals(response.getAnswerList().toArray(new ResourceRecord[0]), new ResourceRecord[]{ns});
    }

    /**
     *test the addition list with add additional method on new obejct
     *
     * @throws ValidationException
     *          if validation fail
     */
    @Test
    public void testAdditionList() throws ValidationException
    {
        Response response = new Response(10, "ns.com.", RCode.getRCode(0));
        Assert.assertEquals(response.getAnswerList().size(), 0);
        NS ns = new NS("foo.com.", 500, "ns.com.");
        response.addAdditional(ns);
        Assert.assertArrayEquals(response.getAdditionalList().toArray(new ResourceRecord[0]), new ResourceRecord[]{ns});
    }

    /**
     * test the name server list iwth add name server method on new object
     * @throws ValidationException
     *          if validation fail
     */
    @Test
    public void testNameServerList() throws ValidationException
    {
        Response response = new Response(10, "ns.com.", RCode.getRCode(0));
        Assert.assertEquals(response.getAnswerList().size(), 0);
        NS ns = new NS("foo.com.", 500, "ns.com.");
        response.addNameServer(ns);
        Assert.assertArrayEquals(response.getNameServerList().toArray(new ResourceRecord[0]), new ResourceRecord[]{ns});
    }

    /**
     * test the EncodeDecode method in Response class
     *
     * @throws ValidationException
     *          if validation fail
     * @throws IOException
     *          if I/O problem
     */
    @Test
    public void testEncodeDecode() throws Throwable
    {
        Response response = new Response(10, "ns.com.", RCode.getRCode(0));
        response.setRCode(RCode.getRCode(0));
        NS ns = new NS("foo.com.", 500, "ns.com.");
        response.addNameServer(ns);
        response.addNameServer(ns);
        A a = new A("ns.name.", 10, (Inet4Address) InetAddress.getByName("127.0.0.1"));
        response.addAdditional(a);
        response.addAdditional(ns);
        response.addAdditional(ns);
        response.addAnswer(ns);
        response.addAnswer(a);
        byte[] encode = response.encode();
        Message message = Message.decode(encode);
        Assert.assertEquals(message, response);
    }

    /**
     * test the EncodeDecode with A class method in Response class
     *
     * @throws ValidationException
     *          if validation fail
     * @throws IOException
     *          if I/O problem
     */
    @Test
    public void testEncodeDecodeA() throws Throwable
    {
        Response response = new Response(10, "ns.com.", RCode.getRCode(0));
        response.setRCode(RCode.getRCode(0));
        A a = new A("ns.name.", 10, (Inet4Address) InetAddress.getByName("127.0.0.1"));
        response.addAdditional(a);
        byte[] encode = response.encode();
        Message message = Message.decode(encode);
        Assert.assertEquals(message, response);
    }
}
