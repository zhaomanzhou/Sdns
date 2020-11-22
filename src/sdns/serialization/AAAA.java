package sdns.serialization;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 5
 *  Class: CSI 4321
 *
 *************************************************/


import sdns.serialization.ResourceRecord;
import sdns.serialization.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet6Address;

public class AAAA extends ResourceRecord
{
    private Inet6Address address;

    /**
     * Constructs A RR using given values
     *
     * @param name RR name
     * @param ttl RR TTL
     * @param address IPv4 address
     * @throws ValidationException
     *  if validation fails
     */
    public AAAA(String name, int ttl, Inet6Address address) throws ValidationException
    {
        if(name == null || address == null){
            throw new ValidationException("null name or address", "name");
        }
        checkNameValid(name);
        checkTtlValid(ttl);
        this.name = name.trim().toLowerCase();
        this.originName = name;
        this.ttl = ttl;
        this.address = address;
        this.type = 28;

        byte[] address1 = address.getAddress();
        this.rdata = new String(address1);
    }

    /**
     * get address
     * @return address of A RR
     */
    public Inet6Address	getAddress()
    {
        return address;
    }

    /**
     * set address
     *
     * @param address new address
     * @return this RR with new address
     * @throws ValidationException
     *  if validation fails
     */
    public AAAA setAddress(Inet6Address address) throws ValidationException
    {
        if(address == null){
            throw new ValidationException("address", "address");
        }
        this.address = address;
        return this;
    }

    /**
     * return s a string representation
     *
     * @return a string representation
     */
    public String toString()
    {
        return String.format("A: name=%s ttl=%d address=%s", name, ttl, address.getHostAddress());
    }

}
