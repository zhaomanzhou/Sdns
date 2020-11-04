package sdns.serialization;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 4
 *  Class: CSI 4321
 *
 *************************************************/

public class ValidationException extends Exception
{

    private static final long serialVersionUID = 878299526242050283L;
    private String badToken;

    /**
     * Equivalent to ValidationException(message, null, badToken)
     *
     * @param message exception message
     * @param badToken string causing exception (null if no such string)
     */
    public ValidationException(String message, String badToken){
        super(message);
        this.badToken = badToken;
    }

    /**
     * Constructs validation exception
     *
     * @param message exception message
     * @param cause exception cause
     * @param badToken string causing exception(null if no such string)
     */
    public ValidationException(String message, Throwable cause, String badToken){
        super(message, cause);
        this.badToken = badToken;
    }

    /**
     * returns bad token
     *
     * @return bad token
     */
    public String getBadToken(){
        return badToken;
    }
}
