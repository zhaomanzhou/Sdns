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
import sdns.serialization.Message;
import sdns.serialization.Query;
import sdns.serialization.ValidationException;

import java.io.IOException;
import java.util.Arrays;

public class MessageTest
{
    /**
     * test if decode a null message
     *
     * @throws ValidationException
     *          if validation fail
     * @throws IOException
     *          if I/O program
     */
    @Test(expected = NullPointerException.class)
    public void testDecodeOfNullMessage() throws Throwable
    {
        Message.decode(null);
    }

    /**
     *test the encode and decode function
     *
     * @throws ValidationException
     *          if validation fail
     * @throws IOException
     *          if I/O program
     */
    @Test
    public void testEncodeAndDecode() throws Throwable
    {
        Query query = new Query(123, "google.com.");
        byte[] encode = query.encode();

        byte[] bytes = Arrays.copyOfRange(encode, 12, encode.length);
        


        Message message1 = Message.decode(encode);

        Assert.assertEquals(query.toString(), message1.toString());
    }

    /**
     * test set ID method, if it set the expected value
     *
     * @throws ValidationException
     *          if validation fail
     */
    @Test
    public void testSetId() throws ValidationException
    {
        Message message = new Message();
        message.setID(16);
        Assert.assertEquals(message.getID(), 16);
    }

    /**
     * test Get ID that will get same value
     *
     * @throws ValidationException
     *          if validation fail
     */
    @Test
    public void testGetId() throws ValidationException
    {
        Message message = new Message();
        message.setID(16);
        Assert.assertEquals(message.getID(), 16);
    }

    /**
     * test if the setID with invalid
     *
     * @throws ValidationException
     *          if validation fail
     */
    @Test(expected = ValidationException.class)
    public void setInvalidId() throws ValidationException
    {
        Message message = new Message();
        message.setID(Integer.MAX_VALUE);
    }
    /**
     * test if the ID and query are equals
     *
     * @throws ValidationException
     *          if validation fail
     */
    @Test
    public void testSetGetQuery() throws ValidationException
    {
        Message message = new Message();
        message.setID(16);
        message.setQuery("ns.name.");
        Assert.assertEquals(message.getQuery(), "ns.name.");
    }

    /**
     * test set a invalid query.
     *
     * @throws ValidationException
     *          if validation fail
     */
    @Test(expected = ValidationException.class)
    public void testSetInvalidQuery() throws ValidationException
    {
        Message message = new Message();
        message.setQuery("ns.name...");
    }









}
