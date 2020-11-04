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
import sdns.serialization.Message;
import sdns.serialization.Query;
import sdns.serialization.ValidationException;

import java.io.IOException;
import java.util.Arrays;

public class QueryTest
{

    /**
     * test with null name
     *
     * @throws ValidationException
     *          if validation fail
     */
    @Test(expected = ValidationException.class)
    public void testNullName() throws ValidationException
    {
        Query query = new Query(10, null);
    }

    /**
     * test contructor with new object
     *
     * @throws ValidationException
     *      if validation fail
     */
    @Test
    public void testConstructor() throws ValidationException
    {
        Query query = new Query(10, "ns.name.");
    }

    /**
     * test if it equals to the string repesetation
     *
     * @throws ValidationException
     *      if validation fail
     */
    @Test
    public void testToString() throws ValidationException, IOException
    {
        Query query = new Query(0xe810, "www.baidu.com.");
        byte[] encode = query.encode();
        Integer[] datas = new Integer[encode.length];
        for (int i = 0; i < encode.length; i++)
        {
            int tmp = encode[i];
            if(encode[i] < 0)
            {

                tmp =  (encode[i] & 0x7f) | 0x80;
            }
           datas[i] = tmp;

        }
        System.out.println(Arrays.toString(query.encode()));
        for(Integer i: datas)
        {
            String s = Integer.toHexString(i);
            if(s.length() == 1)
            {
                s = "0" + s;
            }
            System.out.print(s + " ");
        }
        System.out.println();
        System.out.println("e8 10 01 00 00 01 00 00 00 00 00 00 03 77 77 77 05 62 61 69 64 75 03 63 6f 6d 00 00 01 00 01");


    }
    /**
     * test decode method
     * @throws Throwable
     * if any error or exceptions
     */
    @Test
    public void testDecode() throws Throwable
    {
        byte[] buf = {1, 65, -127, 0, 0, 1, 0, 1, 0, 1, 0, 3, 1, 121, 1, 113, 0, 0, -1, 0, 1, 4, 121, 117, 114, 112, 3, 99, 111, 109, 0, 0, 1, 0, 1, 0, 0, 0, 34, 0, 4, 1, 2, 3, 4, 4, 121, 117, 114, 112, 3, 99, 111, 109, 0, 0, 2, 0, 1, 0, 0, 0, 34, 0, 13, 2, 110, 115, 4, 121, 117, 114, 112, 3, 99, 111, 109, 0, 4, 121, 117, 114, 112, 3, 99, 111, 109, 0, 0, 1, 0, 1, 0, 0, 0, 34, 0, 4, 1, 2, 3, 4, 2, 98, 121, 3, 103, 111, 118, 0, 0, 2, 0, 1, 0, 0, 15, 9, 0, 9, 3, 98, 121, 101, 3, 109, 105, 108, 0, 2, 109, 121, 3, 111, 114, 103, 0, 0, 5, 0, 1, 0, 0, 33, -115, 0, 10, 4, 121, 111, 117, 114, 3, 111, 114, 103, 0};
        Message decode = Message.decode(buf);
    }



}
