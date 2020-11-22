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
import sdns.serialization.NS;
import sdns.serialization.ValidationException;

import java.io.IOException;

public class NSTest
{

    /**
     * test new NS object information
     * @throws ValidationException
     *      if validation fail
     */
    @Test
    public void testConstructor() throws ValidationException
    {
        NS ns = new NS("foo.com.", 500, "ns.com.");
        Assert.assertEquals(ns.getName(), "foo.com.");
        Assert.assertEquals(ns.getTTL(), 500);
        Assert.assertEquals(ns.getNameServer(), "ns.com.");
    }
    /**
     *  Test bad Token1 in new CName object
     * @throws ValidationException
     *          if validation fail
     */
    @Test(expected = ValidationException.class)
    public void testBadToken1() throws ValidationException
    {
        NS ns = new NS("1foo.com.", 500, "ns.com.");
    }
    /**
     *  Test bad Token2 in new CName object
     * @throws ValidationException
     *          if validation fail
     */
    @Test(expected = ValidationException.class)
    public void testBadToken2() throws ValidationException
    {
        NS ns = new NS("foo2.com", 500, "ns.com");
    }
    /**
     *  Test bad Token3 in new CName object
     * @throws ValidationException
     *          if validation fail
     */
    @Test(expected = ValidationException.class)
    public void testBadToken3() throws ValidationException
    {
        NS ns = new NS("fo/o.com.", 500, "ns.com.");
    }

    /**
     * Test if the SetNameServer method is equal to the result from getNameServer
     * @throws ValidationException
     *          if validation fail
     */
    @Test
    public void testSetNameServer() throws ValidationException
    {
        NS ns = new NS("foo.com.", 500, "ns.com.");
        ns.setNameServer("pw.com.");
        Assert.assertEquals(ns.getNameServer(), "pw.com.");
    }

    /**
     * Test the String repesetations are equal
     * @throws ValidationException
     *          if validation fail
     */
    @Test
    public void testToString() throws ValidationException, IOException
    {
        NS ns = new NS("foo.com.", 500, "ns.com.");
        ns.encode(System.out);
        Assert.assertEquals(ns.toString(), "NS: name=foo.com. ttl=500 nameserver=ns.com.");

    }
}
