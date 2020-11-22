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

import java.util.Objects;

public class MX extends ResourceRecord
{

    private String exchange;
    private String originExchange;
    private int preference;

    /**
     * Constructs MX using given values
     * @param name RR name
     * @param ttl RR TTL
     * @param exchange Domain name of mail exchange
     * @param preference preference for mail exchange
     * @throws ValidationException
     *      if validation fails, including null name or exchange
     */
    public MX(String name, int ttl, String exchange, int preference) throws ValidationException
    {
        if(exchange == null || exchange.trim().equals("")){
            throw new ValidationException("null name or exchange", "");
        }
        checkNameValid(name);
        checkNameValid(exchange);
        checkTtlValid(ttl);
        this.name = name.trim().toLowerCase();
        this.originName = name;
        this.ttl = ttl;
        this.type = 15;
        this.exchange = exchange.trim().toLowerCase();
        this.originExchange = exchange;
        setPreference(preference);
        this.rdata = preference + ":" + exchange.trim().toLowerCase();
    }

    /**
     * Get exchange
     * @return exchange domain name
     */
    public String getExchange()
    {
        return originExchange;
    }

    /**
     * Get preference
     * @return preference
     */
    public int getPreference()
    {
        return preference;
    }

    /**
     * Set exchange domain name
     * @param exchange new exchange domain name
     * @return this RR with new exchange
     * @throws ValidationException
     *      if invalid exchange, including null
     */
    public MX setExchange(String exchange) throws ValidationException
    {
        checkNameValid(exchange);
        this.exchange = exchange.trim().toLowerCase();
        this.originExchange = exchange;
        return this;
    }

    /**
     * Set preference
     * @param preference exchange preference
     * @return this RR with new preference
     * @throws ValidationException
     *      if invalid preference
     */
    public MX setPreference(int preference) throws ValidationException
    {
        if(preference < 0 || preference > 65535)
            throw new ValidationException("invalid preference", "preference");
        this.preference = preference;
        return this;
    }

    /**
     * Returns a String representation
     * @return  a String representation
     */
    public String toString()
    {
        return String.format("MX: name=%s ttl=%d exchange=%s preference=%d", name, ttl, exchange, preference);
    }

    /**
     * override the equals method
     * @param o a new Object
     * @return return true if equal, return false if not equal
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof MX)) return false;
        if (!super.equals(o)) return false;
        MX mx = (MX) o;
        return preference == mx.preference &&
                Objects.equals(exchange, mx.exchange);
    }

    /**
     * override the hashCode method
     * @return a hash code value for this object
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), exchange, preference);
    }
}
