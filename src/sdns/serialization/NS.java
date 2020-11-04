package sdns.serialization;

import sdns.serialization.ResourceRecord;
import sdns.serialization.ValidationException;

import java.util.Objects;

/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 4
 *  Class: CSI 4321
 *
 *************************************************/

public class NS extends ResourceRecord
{
    private String nameServer;

    /**
     * Constructs NS using given values
     *
     * @param name ResourceRecord name
     * @param ttl ResourceRecord ttl
     * @param nameServer name server
     * @throws ValidationException
     *          if validation fails, including null name or nameServer
     */
    public NS(String name, int ttl, String nameServer) throws ValidationException{
        //check the string parameter in case of null pointer
        if(nameServer == null || nameServer.trim().equals("")){
            throw new ValidationException("null name or nameServer", "");
        }
        checkNameValid(name);
        checkNameValid(nameServer);
        checkTtlValid(ttl);
        this.name = name.trim().toLowerCase();
        this.originName = name;
        this.ttl = ttl;
        this.type = 2;
        this.nameServer = nameServer;
        this.rdata = nameServer.trim().toLowerCase();

    }

    /**
     * Get name server
     *
     * @return name
     */
    public String getNameServer(){
        return nameServer;
    }

    /**
     * Set name server
     *
     * @param nameServer new name server
     * @return this NS with new name server
     * @throws ValidationException
     *          if invalid name server, including null
     */
    public NS setNameServer(String nameServer) throws ValidationException{
        checkNameValid(nameServer);
        this.nameServer = nameServer;
        return this;
    }

    /**
     *  Returns a String representation
     *
     * @return a format of string representation
     */
    public String	toString(){
        return String.format("NS: name=%s ttl=%d nameserver=%s", name, ttl, nameServer);
    }

    /**
     *  override equals method, check equal
     * @param o object
     * @return true if equal, flase if not equal
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof NS)) return false;
        if (!super.equals(o)) return false;
        NS ns = (NS) o;
        return Objects.equals(nameServer.trim().toLowerCase(), ns.nameServer.trim().toLowerCase());
    }

    /**
     *  override hasCode method.
     *
     * @return integer has code vlaue of the object.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), nameServer.trim().toLowerCase());
    }
}
