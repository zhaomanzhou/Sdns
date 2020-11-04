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
import sdns.serialization.Unknown;
import sdns.serialization.ValidationException;

import java.io.IOException;

public class TestUnknow
{
    /**
     * test the new UnKnown object has been created
     */
    @Test
    public void testConstructor(){
        Unknown unknown = new Unknown();
    }

    /**
     * Test ecncode method
     *
     * @throws IOException
     *          if I/O problem
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testEncode() throws IOException
    {
        Unknown unknown = new Unknown();
        unknown.encode(System.out);
    }

    /**
     * test the String representation format
     *
     * @throws ValidationException
     *          if validation fail
     */
    @Test
    public void testToString() throws ValidationException {
        Unknown unknown = new Unknown();
        unknown.setTTL(100);
        unknown.setName("foo.com.");
        unknown.toString();
        //Assert.assertEquals(unknown.toString(), "Unknown: name=foo.com. ttl=100");
    }


}
