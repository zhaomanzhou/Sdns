package sdns.serialization;

import java.util.Objects;

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

    private void checkTimeValid(Long value, String message) throws ValidationException
    {
        if(value < 0){
            throw new ValidationException("invalid " + message, value + "");
        }
    }


    public String getMName()
    {
        return originMname;
    }

    public SOA setMName(String mName) throws ValidationException
    {
        checkNameValid(mName);
        this.originMname = mName;
        this.mName = mName.trim().toLowerCase();
        return this;
    }

    public String getRName()
    {
        return originRname;
    }

    public SOA setRName(String rName) throws ValidationException
    {
        checkNameValid(rName);
        this.originRname = rName;
        this.rName = rName.trim().toLowerCase();
        return this;
    }

    public Long getSerial()
    {
        return serial;
    }

    public SOA setSerial(Long serial) throws ValidationException
    {
        checkTimeValid(serial, "serial");
        this.serial = serial;
        return this;
    }

    public Long getRefresh()
    {
        return refresh;
    }

    public SOA setRefresh(Long refresh) throws ValidationException
    {
        checkTimeValid(refresh, "refresh");
        this.refresh = refresh;
        return this;
    }

    public Long getRetry()
    {
        return retry;
    }

    public SOA setRetry(Long retry) throws ValidationException
    {
        checkTimeValid(retry, "retry");
        this.retry = retry;
        return this;
    }

    public Long getExpire()
    {
        return expire;
    }

    public SOA setExpire(Long expire) throws ValidationException
    {
        checkTimeValid(expire, "expire");
        this.expire = expire;
        return this;
    }

    public Long getMinium()
    {
        return minium;
    }

    public SOA setMinium(Long minium) throws ValidationException
    {
        checkTimeValid(minium, "minium");
        this.minium = minium;
        return this;
    }

    @Override
    public String toString()
    {
        return String.format("SOA: name=%s ttl=%d mname=%s rname=%s serial=%d refresh=%d retry=%d expire=%d minimum=%d",
                name, ttl, mName, rName, serial, refresh, retry, expire, minium);
    }

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

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), mName, originMname, rName, originRname, serial, refresh, retry, expire, minium);
    }
}
