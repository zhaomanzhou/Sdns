package sdns.serialization;

import java.util.Objects;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 5
 *  Class: CSI 4321
 *
 *************************************************/

public class SOA extends ResourceRecord
{
    private String mName;
    private String originMname;
    private String rName;
    private String originRname;
    private Long serial;
    private Long refresh;
    private Long retry;
    private Long expire;
    private Long minium;
    /**
     * Constructs SOA using given values
     * @param name RR name
     * @param ttl RR TTL
     * @param mName domain name of primary zone source
     * @param rName domain name of mailbox of responsible person
     * @param serial version number
     * @param refresh time before zone refresh
     * @param retry wait time before retry refresh
     * @param expire max time zone authoritative
     * @param minimum minimum TTL for any exported RR
     * @throws ValidationException
     *       if validation fails
     */
    public SOA(String name, int ttl, String mName, String rName, long serial, long refresh, long retry, long expire, long minimum) throws ValidationException
    {

        checkNameValid(name);
        checkNameValid(mName);
        checkNameValid(rName);
        checkTtlValid(ttl);
        checkTimeValid(serial, "serial");
        checkTimeValid(refresh, "refresh");
        checkTimeValid(retry, "retry");
        checkTimeValid(expire, "expire");
        checkTimeValid(minimum, "minimum");
        this.name = name.trim().toLowerCase();
        this.originName = name;
        this.mName = mName.trim().toLowerCase();
        this.originMname = mName;
        this.rName = rName.trim().toLowerCase();
        this.originRname = rName;
        this.serial = serial;
        this.refresh = refresh;
        this.retry = retry;
        this.expire = expire;
        this.minium = minimum;
        this.ttl = ttl;
        this.type = 6;
    }
    /**
     * check time validation
     * @param value time
     * @param message validation message
     * @throws ValidationException
     *      if validation fails
     */
    private void checkTimeValid(Long value, String message) throws ValidationException
    {
        if(value < 0){
            throw new ValidationException("invalid " + message, value + "");
        }
    }

    /**
     * Get mName
     * @return domain name
     */
    public String getMName()
    {
        return originMname;
    }
    /**
     * Set mName domain name
     * @param mName new mName domain name
     * @return this RR with new mName
     * @throws ValidationException
     *      if invalid mName, including null
     */
    public SOA setMName(String mName) throws ValidationException
    {
        checkNameValid(mName);
        this.originMname = mName;
        this.mName = mName.trim().toLowerCase();
        return this;
    }

    /**
     *  Get rName
     * @return rName domain name
     */
    public String getRName()
    {
        return originRname;
    }
    /**
     * Set rName domain name
     * @param rName new rName domain name
     * @return
     * @throws ValidationException
     */
    public SOA setRName(String rName) throws ValidationException
    {
        checkNameValid(rName);
        this.originRname = rName;
        this.rName = rName.trim().toLowerCase();
        return this;
    }
    /**
     * Get serial
     * @return serial value
     */
    public Long getSerial()
    {
        return serial;
    }
    /**
     * Set serial
     * @param serial new serial
     * @return this RR with new serial
     * @throws ValidationException
     *      if invalid serial
     */
    public SOA setSerial(Long serial) throws ValidationException
    {
        checkTimeValid(serial, "serial");
        this.serial = serial;
        return this;
    }
    /**
     * Get refresh
     * @return refresh value
     */
    public Long getRefresh()
    {
        return refresh;
    }
    /**
     * Set refresh
     * @param refresh new refresh
     * @return this RR with new refresh
     * @throws ValidationException
     *      if invalid refresh
     */
    public SOA setRefresh(Long refresh) throws ValidationException
    {
        checkTimeValid(refresh, "refresh");
        this.refresh = refresh;
        return this;
    }
    /**
     * Get retry
     * @return retry value
     */
    public Long getRetry()
    {
        return retry;
    }
    /**
     * Set retry
     * @param retry new retry
     * @return this RR with new retry
     * @throws ValidationException
     *      if invalid retry
     *
     */
    public SOA setRetry(Long retry) throws ValidationException
    {
        checkTimeValid(retry, "retry");
        this.retry = retry;
        return this;
    }
    /**
     *  Get expire
     * @return expire value
     */
    public Long getExpire()
    {
        return expire;
    }
    /**
     *  Set expire
     * @param expire new expire
     * @return this RR with new expire
     * @throws ValidationException
     *      if invalid expire
     */
    public SOA setExpire(Long expire) throws ValidationException
    {
        checkTimeValid(expire, "expire");
        this.expire = expire;
        return this;
    }
    /**
     * Get minimum
     * @return minimum value
     */
    public Long getMinium()
    {
        return minium;
    }
    /**
     * Set minimum
     * @param minium new minimum
     * @return this RR with new minimum
     * @throws ValidationException
     *      if invalid minimum
     */
    public SOA setMinium(Long minium) throws ValidationException
    {
        checkTimeValid(minium, "minium");
        this.minium = minium;
        return this;
    }
    /**
     * Returns a String representation
     * @return String representation
     */
    @Override
    public String toString()
    {
        return String.format("SOA: name=%s ttl=%d mname=%s rname=%s serial=%d refresh=%d retry=%d expire=%d minimum=%d",
                name, ttl, mName, rName, serial, refresh, retry, expire, minium);
    }
    /**
     * override equals method
     * @param o object
     * @return if equal return true, if not equal return false
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof SOA)) return false;
        if (!super.equals(o)) return false;
        SOA soa = (SOA) o;
        return Objects.equals(mName, soa.mName) &&
                Objects.equals(originMname, soa.originMname) &&
                Objects.equals(rName, soa.rName) &&
                Objects.equals(originRname, soa.originRname) &&
                Objects.equals(serial, soa.serial) &&
                Objects.equals(refresh, soa.refresh) &&
                Objects.equals(retry, soa.retry) &&
                Objects.equals(expire, soa.expire) &&
                Objects.equals(minium, soa.minium);
    }
    /**
     * facilitate hashing in hash tables
     * @returna integer value
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), mName, originMname, rName, originRname, serial, refresh, retry, expire, minium);
    }
}
