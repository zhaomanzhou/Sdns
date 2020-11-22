package sdns.serialization;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 5
 *  Class: CSI 4321
 *
 *************************************************/

import sdns.serialization.Message;
import sdns.serialization.ValidationException;

import java.util.Objects;

public class Query extends Message
{
    /**
     * Constructs SDNS query using given values
     *
     * @param id query id
     * @param query query domain name
     * @throws ValidationException
     *  if parse or validation problem
     */
    public Query(int id, String query) throws ValidationException
    {
        checkDomainValid(query);
        setID(id);
        this.query = query.trim().toLowerCase();
        this.originQuery = query;
        this.qr = 0;
        this.rCode = 0;
    }

    /**
     * return a String representation
     * @return string representation
     */
    public String toString()
    {
        return String.format("Query: id=%d query=%s", id, query);
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
        if (!(o instanceof Query)) return false;
        if(!o.toString().equals(toString())) return false;
        return super.equals(o);
    }

    /**
     *  override hasCode method.
     *
     * @return integer has code vlaue of the object.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(id, query);
    }

}
