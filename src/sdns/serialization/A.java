package sdns.serialization;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 4
 *  Class: CSI 4321
 *
 *************************************************/

import sdns.serialization.ResourceRecord;
import sdns.serialization.ValidationException;

import java.net.Inet4Address;

public class A extends ResourceRecord
{
    private Inet4Address address;

    /**
     * Constructs A RR using given values
     *
     * @param name RR name
     * @param ttl RR TTL
     * @param address IPv4 address
     * @throws ValidationException
     *  if validation fails
     */
    public A(String name, int ttl, Inet4Address address) throws ValidationException
    {
        if(name == null || address == null){
            throw new ValidationException("null name or address", "");
        }
        checkNameValid(name);
        checkTtlValid(ttl);
        this.name = name.trim().toLowerCase();
        this.originName = name;
        this.ttl = ttl;
        this.address = address;
        this.type = 1;
        this.rdata = "";

        String[] split = address.getHostAddress().split("\\.");
        for (String s: split)
        {
            rdata += ((char)Integer.parseInt(s));
        }
    }

    /**
     * get address
     * @return address of A RR
     */
    public Inet4Address	getAddress()
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
    public A setAddress(Inet4Address address) throws ValidationException
    {
        if(address == null){
            throw new ValidationException("null", "");
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
