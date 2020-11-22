package sdns.serialization;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 5
 *  Class: CSI 4321
 *
 *************************************************/


import sdns.serialization.ResourceRecord;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class Unknown extends ResourceRecord
{



    /**
     *  Always throws UnsupportedOperationException
     * @param out sdns.serialization sink
     */
    public void	encode(OutputStream out){
       throw new  UnsupportedOperationException("unsupported");
    }

    /**
     * returns a string representation
     *
     * @return a format of string
     */
    public String toString(){
        return String.format("Unknown: name=%s ttl=%d", name, ttl);
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
        if (!(o instanceof Unknown)) return false;
        Unknown unknown = (Unknown) o;
        return Objects.equals(unknown.name.toLowerCase(), this.name.toLowerCase()) && Objects.equals(unknown.ttl, this.ttl)
                && Objects.equals(unknown.type, this.type);
    }
    /**
     *  override hasCode method.
     *
     * @return integer has code vlaue of the object.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(name.toLowerCase(), ttl, type);
    }




}
