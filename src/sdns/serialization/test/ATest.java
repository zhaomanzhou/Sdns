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
import sdns.serialization.A;
import sdns.serialization.ResourceRecord;
import sdns.serialization.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class ATest
{

    /**
     * test Object information with null name
     *
     * @throws ValidationException
     * if validation fail
     * @throws UnknownHostException
     * if IP address of a unknown host is detected
     */
    @Test(expected = ValidationException.class)
    public void testNullNameConstructor() throws ValidationException, UnknownHostException
    {
        new A("null",10, (Inet4Address) InetAddress.getByName("127.0.0.1"));
    }

    /**
     * test with address name
     *
     * @throws ValidationException
     * if validation fail
     * @throws UnknownHostException
     * if IP address of a unknown host is detected
     */
    @Test(expected = ValidationException.class)
    public void testInnet4AddressNameConstructor() throws ValidationException, UnknownHostException
    {
        new A("abc.com.",10,null);
    }

    /**
     * test new A object information
     *
     * @throws ValidationException
     * if validation fail
     * @throws UnknownHostException
     * if IP address of a unknown host is detected
     */
    @Test
    public void testConstructor() throws ValidationException, UnknownHostException
    {
        new A("abc.com.",10, (Inet4Address) InetAddress.getByName("127.0.0.1"));
    }

    /**
     * test if get address return the right address
     *
     * @throws ValidationException
     * if validation fail
     * @throws UnknownHostException
     * if IP address of a unknown host is detected
     */
    @Test
    public void testGetAddress() throws ValidationException, UnknownHostException
    {
        A a = new A("abc.com.", 10, (Inet4Address) InetAddress.getByName("127.0.0.1"));
        Assert.assertEquals(a.getAddress(), (Inet4Address) InetAddress.getByName("127.0.0.1"));
    }

    /**
     * test set address function
     *
     * @throws ValidationException
     * if validation fail
     * @throws UnknownHostException
     * if IP address of a unknown host is detected
     */
    @Test
    public void testSetAddress() throws ValidationException, UnknownHostException
    {
        A a = new A("abc.com.", 10, (Inet4Address) InetAddress.getByName("127.0.0.1"));
        a.setAddress( (Inet4Address) InetAddress.getByName("127.0.0.2"));
        Assert.assertEquals(a.getAddress(), (Inet4Address) InetAddress.getByName("127.0.0.2"));
    }

    /**
     * Test the String repesetations are equal
     *
     * @throws ValidationException
     * if validation fail
     * @throws UnknownHostException
     * if IP address of a unknown host is detected
     */
    @Test
    public void testToString() throws ValidationException, UnknownHostException
    {
        A a = new A("abc.com.", 10, (Inet4Address) InetAddress.getByName("127.0.0.1"));
        Assert.assertEquals(a.toString(), "A: name=abc.com. ttl=10 address=127.0.0.1");
    }

    /**
     *  test decode method with data
     *
     * @throws IOException
     *  if failed or interrupted I/O operations
     * @throws ValidationException
     * if validation fail
     */
    @Test
    public void testDecode() throws IOException, ValidationException
    {
        byte[] buf = {1, 102, 1, 99, 0, 0, 1, 0, 1, 0, 0, 1, 65, 0, 4, 1, 2, 3, 4};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
        ResourceRecord.decode(inputStream);
    }

    /**
     * test encode with data
     *
     * @throws IOException
     * if failed or interrupted I/O operations
     * @throws ValidationException
     * if validation fail
     */
    @Test
    public void testEnode() throws IOException, ValidationException
    {
        byte[] buf = {0, 0, 1, 0, 1, 127, -1, -1, -1, 0, 4, -128, -128, -128, -128};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
        ResourceRecord decode = ResourceRecord.decode(inputStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        decode.encode(outputStream);
        System.out.println(Arrays.toString(outputStream.toByteArray()));
    }


}
