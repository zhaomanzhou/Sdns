package sdns.serialization;

import sdns.serialization.ResourceRecord;
import sdns.serialization.ValidationException;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 4
 *  Class: CSI 4321
 *
 *************************************************/


public class CName extends ResourceRecord
{
    private String canonicalName;
    private String originCanonicalName;

    /**
     * Constructs CName using given values
     *
     * @param name ResourceRecord name
     * @param ttl ResourceRecord TTL
     * @param canonicalName Canonical anme
     * @throws ValidationException
     *          if validation fails, including null name or canonical name
     */
    public CName(String name, int ttl, String canonicalName) throws ValidationException
    {
       //check the string parameter in case of null pointer
        if(canonicalName == null || canonicalName.trim().equals("")){
            throw new ValidationException("null name or canonicalName","");
        }

        checkNameValid(name);
        checkNameValid(canonicalName);
        checkTtlValid(ttl);
        this.name = name.trim().toLowerCase();
        this.originName = name;
        this.originCanonicalName = canonicalName;
        this.ttl = ttl;
        this.type = 5;
        this.canonicalName = canonicalName.trim().toLowerCase();
        this.rdata = this.canonicalName;

    }

    /**
     * Get canonical name
     *
     * @return name
     */
    public String getCanonicalName(){
        return originCanonicalName;
    }

    /**
     *
     * @param canonicalName new canonical name
     * @return this RRwith new canonical name
     * @throws ValidationException
     *          if invalid canonical name, including null
     */
    public CName setCanonicalName(String canonicalName) throws ValidationException{
        checkNameValid(canonicalName);
        this.canonicalName = canonicalName;
        this.originCanonicalName = canonicalName;
        return this;
    }

    /**
     *  Returns a String representation
     *
     * @return a format of string representation
     */
    public String toString(){
        return String.format("CName: name=%s ttl=%d canonicalname=%s", name, ttl, canonicalName);
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
        if (!(o instanceof CName)) return false;
        if (!super.equals(o)) return false;
        CName cName = (CName) o;
        return Objects.equals(canonicalName, cName.canonicalName);
    }

    /**
     *  override hasCode method.
     *
     * @return integer has code vlaue of the object.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), canonicalName);
    }



}
