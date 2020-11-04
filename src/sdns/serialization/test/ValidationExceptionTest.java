package sdns.serialization.test;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 4
 *  Class: CSI 4321
 *
 *************************************************/

import org.junit.Assert;
import org.junit.Test;
import sdns.serialization.ValidationException;

public class ValidationExceptionTest
{
    /**
     * test the validationException with bad token can
     * throw same results as getMessage and getBadToken method
     */
    @Test
    public void testValidationExceptionBadToken(){
        ValidationException exception = new ValidationException("exception", "bad");
        Assert.assertEquals(exception.getMessage(), "exception");
        Assert.assertEquals(exception.getBadToken(), "bad");
    }

    /**
     * Test the validationException with bad token and cause can
     * thorw the same result as getMessage, getCause, and getBadToekn
     */
    @Test
    public void testValidationExceptionBadTokenAndCause(){
        Exception cause = new Exception();
        ValidationException exception = new ValidationException("exception", cause, "bad");
        Assert.assertEquals(exception.getMessage(), "exception");
        Assert.assertEquals(exception.getCause(), cause);
        Assert.assertEquals(exception.getBadToken(), "bad");
    }

    /**
     * test new object of ValidationException with Exception and BadToken
     *
     * @throws ValidationException
     *          if validation fail
     */
    @Test(expected = ValidationException.class)
    public void testThrow() throws ValidationException
    {
        ValidationException exception = new ValidationException("exception", "bad");
        throw exception;
    }

    /**
     * test new object of ValidationException with exception, cause, and badToken
     *
     * @throws ValidationException
     *          if validation fail
     */
    @Test(expected = ValidationException.class)
    public void testThrow2() throws ValidationException
    {
        Exception cause = new Exception();
        ValidationException exception = new ValidationException("exception", cause, "bad");
        throw exception;
    }


}
