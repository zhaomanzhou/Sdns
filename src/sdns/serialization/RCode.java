package sdns.serialization;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 5
 *  Class: CSI 4321
 *
 *************************************************/

import sdns.serialization.ValidationException;

public enum RCode
{
    /**
     * NOERROR Indicates no error
     * FORMATERROR Indicates that the name server was unable to interpret the query
     * SERVERFAILURE Indicates that the name server was unable to process this query due to a problem with the name server
     * NAMEERROR Indicates that the domain name referenced in the query does not exist
     * NOTIMPLEMENTED Indicates that the name server does not support the requested kind of query
     * REFUSED Indicates that the name server refuses to perform the specified operation
     */
    NOERROR(0), FORMATERROR(1),SERVERFAILURE(2),NAMEERROR(3),NOTIMPLEMENTED(4),REFUSED(5);
    private final int rcode;

    /**
     * constructor of Rcode
     * @param rcode Rcode
     */
    private RCode(int rcode)
    {
        this.rcode = rcode;
    }
    /**
     * Get the rcode associated with the given rcode value
     * @param rcodeValue rcode value
     * @return RCode associated with given value
     * @throws ValidationException
     *      if rcode value is out of range
     */
    public static RCode	getRCode(int rcodeValue) throws ValidationException
    {
        switch (rcodeValue){
            case 0: return NOERROR;
            case 1: return FORMATERROR;
            case 2: return SERVERFAILURE;
            case 3: return NAMEERROR;
            case 4: return NOTIMPLEMENTED;
            case 5: return REFUSED;
        }
        throw new ValidationException("Out of range", "rcode");
    }
    /**
     * Get the rcode message
     * @return the message associate with the rcode
     */
    public String getRCodeMessage()
    {
        switch (rcode){
            case 0: return "No error condition";
            case 1: return "The name server was unable to interpret the query";
            case 2: return "The name server was unable to process this query due to a problem with the name server";
            case 3: return "The domain name referenced in the query does not exist";
            case 4: return "The name server does not support the requested kind of query";
            case 5: return "The name server refuses to perform the specified operation";
        }
        return null;
    }
    /**
     * Get the rcode value
     * @return the value associate with the rcode
     */
    public int	getRCodeValue()
    {

        return rcode;
    }

//    public static RCode	valueOf(String name)
//    {
//        switch (name){
//            case "NOERROR": return NOERROR;
//            case "FORMATERROR": return FORMATERROR;
//            case "SERVERFAILURE": return SERVERFAILURE;
//            case "NAMEERROR": return NAMEERROR;
//            case "NOTIMPLEMENTED": return NOTIMPLEMENTED;
//            case "REFUSED": return REFUSED;
//        }
//        throw new IllegalArgumentException("No matched");
//
//    }



}
