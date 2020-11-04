package sdns.serialization;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 4
 *  Class: CSI 4321
 *
 *************************************************/

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CAA extends ResourceRecord
{
    private String issuer;

    /**
     * Constructs CAA using given values
     * @param name RR name
     * @param ttl RR TTL
     * @param issuer issuer name
     * @throws ValidationException
     *       if validation fails (see specification), including null name or issuer name
     */
    public CAA(String name, int ttl, String issuer) throws ValidationException
    {
        checkNameValid(name);
        checkTtlValid(ttl);
        this.name = name.trim().toLowerCase();
        this.originName = name;
        this.ttl = ttl;
        this.type = 257;
        this.issuer = issuer;
        this.rdata = "issue" + issuer;

    }

    /**
     * Get issuer name
     * @return issuer name
     */
    public String getIssuer()
    {
        return this.issuer;
    }

    /**
     *Set issuer name
     * @param issuer
     * @return CAA object
     */
    public CAA setIssuer(String issuer)
    {
        this.issuer = issuer;
        return this;
    }

    /**
     * Returns a String representation
     * @return a String representation
     */
    public String toString()
    {
        return String .format("CAA: name=%s ttl=%d issuer=%s", name, ttl, issuer);
    }
}
