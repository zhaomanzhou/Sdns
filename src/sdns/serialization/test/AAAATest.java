package sdns.serialization.test;

import org.junit.Test;
import sdns.serialization.AAAA;
import sdns.serialization.ResourceRecord;
import sdns.serialization.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class AAAATest
{

    @Test
    public void testRdata() throws IOException, ValidationException
    {
        AAAA aaaa = new AAAA("google.com.", 10 , (Inet6Address) InetAddress.getByName("1::1"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        aaaa.encode(bos);
        ResourceRecord decode = ResourceRecord.decode(new ByteArrayInputStream(bos.toByteArray()));

    }
}
