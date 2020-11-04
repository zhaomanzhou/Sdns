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

import java.util.List;
import java.util.Objects;

public class Response extends Message
{
    /**
     * constructs SDNS reponse using given values
     *
     * @param id query id
     * @param query query domain name
     * @param rcode
     * @throws ValidationException
     *     Validation problem
     */
    public Response(int id, String query, RCode rcode) throws ValidationException
    {
        setID(id);
        setQuery(query);
        this.qr = 1;
        if(rcode == null)
        {
            throw new ValidationException("null rcode", "rcode");
        }
        this.rCode = rcode.getRCodeValue();
    }

    /**
     * add new additional to additional list
     *
     * @param additional new additional to add to additonal list
     * @return this reponse with new additional
     * @throws ValidationException
     *      Validation problem
     */
    public Response addAdditional(ResourceRecord additional) throws ValidationException
    {
        checkNull(additional);
        this.getAdditionalList().remove(additional);

        this.additionalList.add(additional);
        return this;
    }

    /**
     * add new answer to answer list (duplicates ignored)
     *
     * @param answer new answer to add to answer list
     * @return this response with new answer
     * @throws ValidationException
     *      Validation problem
     */
    public Response addAnswer(ResourceRecord answer) throws ValidationException
    {
        checkNull(answer);
        this.answerList.remove(answer);
        this.answerList.add(answer);
        return this;
    }

    /**
     * add new name server to name server list (duplicates ignored)
     *
     * @param nameServer new name server to add name server list
     * @return this response with new name server
     * @throws ValidationException
     *      Validation problem
     */
    public Response addNameServer(ResourceRecord nameServer) throws ValidationException
    {
        checkNull(nameServer);
        this.nameServerList.remove(nameServer);
        this.nameServerList.add(nameServer);
        return this;
    }

    /**
     * get list of RR additonals
     *
     * @return list of RRs
     */
    public List<ResourceRecord> getAdditionalList()
    {
        return additionalList;
    }

    /**
     * get list of RR name servers
     *
     * @return list of RRs
     */
    public List<ResourceRecord>	getAnswerList()
    {
        return answerList;
    }
    /**
     * get list of RR name servers
     *
     * @return list of RRs
     */
    public List<ResourceRecord>	getNameServerList()
    {
        return nameServerList;
    }
    /**
     * get response code
     *
     * @return response code
     */
    public RCode	getRCode()
    {
        RCode rCode = null;
        try
        {
            rCode = RCode.getRCode(this.rCode);
        } catch (ValidationException e)
        {
            e.printStackTrace();
        }
        return rCode;
    }
    /**
     * set new response code
     *
     * @return this Response with update response code
     */
    public Response setRCode(RCode rcode1) throws ValidationException
    {
        if(rcode1 == null)
        {
            throw new ValidationException("null rcode", "rcode");
        }
        int rcode2 = rcode1.getRCodeValue();
        if(rcode2 < 0 || rcode2 > 5)
        {
            throw new ValidationException("rcode", "rcode");
        }
        this.rCode = rcode2;
        return this;
    }

    /**
     * check for null
     * @param o object
     * @throws ValidationException
     *      validation problem
     */
    protected void checkNull(Object o) throws ValidationException
    {
        if(o == null){
            throw new ValidationException("null", "null");
        }
    }
    /**
     * Return a String representation
     * @return  a String representation
     */
    public String	toString()
    {
        return String.format("Response: id=%d query=%s answers=%s nameservers=%s additionals=%s",
                id, query, stringfyList(answerList), stringfyList(nameServerList), stringfyList(additionalList));
    }

    /**
     * put string into list
     *
     * @param list a resource record list
     * @return return a string
     */
    public String stringfyList(List<ResourceRecord> list)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++)
        {
            sb.append(list.get(i).toString());
            if(i != list.size() - 1){
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
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
        if (!(o instanceof Response)) return false;
        Response response = (Response) o;
        return Objects.equals(id, response.id) &&
                Objects.equals(rCode, response.rCode) &&
                Objects.equals(query, response.query) &&
                listEquals(answerList, response.answerList) &&
                listEquals(additionalList, response.additionalList) &&
                listEquals(nameServerList, response.nameServerList);
    }
    /**
     *  override hasCode method.
     *
     * @return integer has code vlaue of the object.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(id, rCode, query, answerList, additionalList, nameServerList);
    }

    /**
     * check if two list are equal or not
     *
     * @param list1 a resource record list
     * @param list2 a resource record list
     * @return ture if equal false if not equal
     */
    private boolean listEquals(List<ResourceRecord> list1, List<ResourceRecord> list2)
    {
        if(list1.size() != list2.size()){
            return false;
        }
        for(ResourceRecord record: list1){
            if(!list2.contains(record)){
                return false;
            }
        }
        return true;
    }
}
