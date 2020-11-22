package sdns.serialization;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 5
 *  Class: CSI 4321
 *
 *************************************************/


import sdns.serialization.ValidationException;

import java.io.*;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Objects;

public class ResourceRecord
{
    protected String name;
    protected String originName;
    protected int type;
    protected long ttl;
    protected int rdataLength;
    protected String rdata;
    public static boolean checkEof = true;

    /**
     *  Deserializes message from input source
     *
     * @param in deserialization input source
     * @return a specific ResourceRecord Aresulting from deserialization
     * @throws IOException
     *          if I/O problem
     * @throws ValidationException
     *          if parse or validation problem
     */
    public static ResourceRecord decode(InputStream in) throws IOException, ValidationException
    {

        if(in == null)
        {
            throw new NullPointerException("in");
        }
        //warp it into DataInputStream for read by bit
        DataInputStream dis = new DataInputStream(in);
        //the stringbuilder for build the domain
        StringBuilder nameBuilder = new StringBuilder();
        int len = readOneByte(dis);
        if(len == 0 || len >= 192){
            if(len >= 192){
                readOneByte(dis);
            }
            nameBuilder.append(".");
        }
        //read the domain
        while (len != 0 && len < 192){
            int i = readOneByte(dis);
            char c = (char)i;
            nameBuilder.append(c);
            len--;

            if(len == 0){
                nameBuilder.append('.');
                len = readOneByte(dis);
                if(len == 0){
                    break;
                }else if(len >= 192){
                    readOneByte(dis);
                    break;
                }
            }
        }

        //type
        int type = dis.readShort() & 0x0000ffff;
        //0x0001
        short i1 = dis.readShort();
        if(i1 != 1){
            throw new ValidationException("should be 0x001","");
        }
        //ttl
        int ttl = dis.readInt();

        //the length for rdata
        int dataLength = dis.readShort() & 0x0000ffff;

        //the string builder for building rdata
        StringBuilder dataBuilder = new StringBuilder();
        for (int i = 0; i < dataLength; i++)
        {
            int readed = readOneByte(dis);
            char c = (char)readed;
            dataBuilder.append(c);
        }

        if(checkEof){
            if(dis.available() > 0){
                throw new EOFException("eof");
            }
        }


        //type ; 2-> NS; 5->CName
        switch (type){
            case 1:{
                return new A(nameBuilder.toString(), ttl, convertDataToInet4address(dataBuilder.toString()));
            }
            case 2:{
                return new NS(nameBuilder.toString(), ttl, covertRdataToName(dataBuilder.toString()));
            }
            case 5:{
                return new CName(nameBuilder.toString(), ttl, covertRdataToName(dataBuilder.toString()));
            }
            case 6: {
                return convertRdataToSOA(nameBuilder.toString(), ttl, dataBuilder.toString());
            }
            case 15:{
                return new MX(nameBuilder.toString(), ttl, convertRdataToExchange(dataBuilder.toString()), convertRdataToPreference(dataBuilder.toString()) );
            }
            case 28:{
                return new AAAA(nameBuilder.toString(), ttl, convertDataToInet6address(dataBuilder.toString()));
            }
            case 257:{
                return new CAA(nameBuilder.toString(), ttl, convertDataToIssue(dataBuilder.toString()));
            }
            default:{
                Unknown unknown = new Unknown();
                unknown.setName(nameBuilder.toString());
                unknown.setTTL(ttl);
                unknown.type = type;
                unknown.setRdata(dataBuilder.toString());
                return unknown;
            }
        }

    }

    private static SOA convertRdataToSOA(String name, int ttl, String rdata) throws IOException, ValidationException
    {

        int index = 0;
        char[] chars = rdata.toCharArray();
        StringBuilder mNameBuilder = new StringBuilder();


        int len = chars[index++];

        if(len == 0|| len > 192)
        {
            mNameBuilder.append(".");
        }
        while (!(len == 0 || len > 192))
        {
            int i = chars[index++];
            char c = (char)i;
            mNameBuilder.append(c);
            len--;
            if(len == 0){
                mNameBuilder.append('.');
                len = chars[index++];
                if(len == 0 || len >= 192){
                    break;
                }
            }
        }
        if(len > 192)
        {
            index++;
        }

        StringBuilder rnameBuilder = new StringBuilder();

        len = chars[index++];

        if(len == 0|| len > 192)
        {
            rnameBuilder.append(".");
        }
        while (!(len == 0 || len > 192))
        {
            int i = chars[index++];
            char c = (char)i;
            rnameBuilder.append(c);
            len--;
            if(len == 0){
                rnameBuilder.append('.');
                len = chars[index++];
                if(len == 0 || len >= 192){
                    break;
                }
            }
        }
        if(len > 192)
        {
            index++;
        }

        byte[] bytes = new byte[chars.length - index];
        for (int i = index; i < chars.length; i++)
        {
            bytes[i-index] = (byte) chars[i];
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes, 0, bytes.length);
        DataInputStream dataInputStream = new DataInputStream(bis);
        long base = 0x00000000ffffffffL;
        int serial = dataInputStream.readInt();
        int refresh = dataInputStream.readInt();
        int retry = dataInputStream.readInt();
        int expire = dataInputStream.readInt();
        int minimum = dataInputStream.readInt();

        SOA soa = new SOA(name, ttl, mNameBuilder.toString(),
                rnameBuilder.toString(), serial & base,
                refresh & base, retry & base, expire & base, minimum & base);

        return soa;
    }

    private static String convertDataToIssue(String data) throws ValidationException
    {
        char[] chars = data.toCharArray();
        if(chars.length < 7)
        {
            throw new ValidationException("rdata","rdata");
        }
        if(chars[0] != 0x0 || chars[1] != 0x5 || chars[2] != 'i'
            || chars[3] != 's' || chars[4] != 's'
            || chars[5] != 'u' || chars[6] != 'e')
        {
            throw new ValidationException("rdata", "rdata");
        }

        return data.substring(7 );
    }


    /**
     * Serializes RR to given sink
     *
     * @param out sdns.serialization sink
     * @throws IOException
     *          if I/O problem
     */
    public void encode(OutputStream out) throws IOException
    {
        if(out == null){
            throw new NullPointerException("out");
        }

        DataOutputStream dos = new DataOutputStream(out);

        //split the domain into labels
        String[] split = name.split("\\.");
        for(String label: split){
            //write each label
            dos.writeByte(label.length());
            for (int i = 0; i < label.length(); i++)
            {
                char c = label.charAt(i);
                dos.writeByte(c);
            }
        }

        //the end for the label
        dos.writeByte(0);
        dos.writeShort(type);
        dos.writeShort(0x0001);
        dos.writeInt((int) ttl);

        if(type == 2 || type == 5)
        {
            if(rdata.equals(".")){
                dos.writeShort(1);
            }else{
                dos.writeShort(rdata.length() + 1);
            }
            //write rdata
            split = rdata.split("\\.");
            for(String label: split){
                //write each label
                dos.writeByte(label.length());
                for (int i = 0; i < label.length(); i++)
                {
                    char c = label.charAt(i);
                    dos.writeByte(c);
                }
            }
            dos.writeByte(0);
        }else if(type == 15)
        {
            String[] split1 = rdata.split(":");
            int pre = Integer.parseInt(split1[0]);
            String exchangeData = split1[1];
            if(exchangeData.equals(".")){
                dos.writeShort(3);
            }else{
                dos.writeShort(exchangeData.length() + 3);
            }
            dos.writeShort(pre);

            //write rdata
            split = exchangeData.split("\\.");
            for(String label: split){
                //write each label
                dos.writeByte(label.length());
                for (int i = 0; i < label.length(); i++)
                {
                    char c = label.charAt(i);
                    dos.writeByte(c);
                }
            }
            dos.writeByte(0);
        }else if(type == 28)
        {
            byte[] bytes = rdata.getBytes();
            dos.writeShort(bytes.length);
            for (int i = 0; i < bytes.length; i++)
            {
                dos.write(bytes[i]);
            }

        }
        else if(type == 257)
        {
            dos.writeShort(2+rdata.length());
            dos.writeByte(0x0);
            dos.writeByte(0x5);
            for (int i = 0; i < rdata.length(); i++)
            {
                dos.writeByte(rdata.charAt(i));
            }
        }else if(type == 6)
        {
            SOA soa = (SOA) this;
            int length = 0;
            if(!soa.getRName().equals("."))
            {
                length += soa.getRName().length();
            }
            if(!soa.getMName().equals("."))
            {
                length += soa.getMName().length();
            }

            length += 2;
            length += 4*5;
            dos.writeShort(length);
            writeDomain(soa.getMName(), dos);
            writeDomain(soa.getRName(), dos);
            long serial = soa.getSerial();
            dos.writeInt((int)serial);
            long refresh = soa.getRefresh();
            dos.writeInt(((int)refresh));
            long retry = soa.getRetry();
            dos.writeInt(((int)retry));
            long expire = soa.getExpire();
            dos.writeInt((int)expire);
            long minium = soa.getMinium();
            dos.writeInt(((int)minium));

        }

        else
        {

            dos.writeShort(rdata.length());
            //write rdata
            for (int i = 0; i < rdata.length(); i++)
            {
                dos.writeByte(rdata.charAt(i));
            }



        }


        dos.close();

    }

    /**
     * Get name of RR
     *
     * @return name
     */
    public String getName(){
        return this.originName;
    }

    /**
     * get TTl of RR
     *
     * @return TTL
     */
    public int	getTTL(){
        return (int) this.ttl;
    }
    /**
     * Return type value for specific RR
     *
     * @return type value
     */

    public int	getTypeValue(){
        return this.type;
    }

    /**
     * set name of RR
     *
     * @param name this RR with new name
     * @return this RR with new name
     * @throws ValidationException
     *          if new name invalid or null
     */
    public ResourceRecord setName(String name) throws ValidationException{
        checkNameValid(name);
        this.originName = name;
        this.name = name;
        return this;
    }

    /**
     * set TTL of RR
     *
     * @param ttl new TTl
     * @return this RR with new TTL
     * @throws ValidationException
     *          if new TTL invalid
     */
    public ResourceRecord setTTL(int ttl)throws ValidationException{
        checkTtlValid(ttl);
        this.ttl = ttl;
        return this;
    }

    /**
     * read one byte
     *
     * @param in input source
     * @return the value in a case of unsigned byte
     * @throws IOException
     *  if I/O problem
     */

    private static int readOneByte(DataInputStream in) throws IOException
    {
        byte[] bytes = new byte[1];
        int readed = in.read(bytes, 0, 1);
        if(readed <= 0){
            throw new EOFException("eof");
        }
        byte b = bytes[0];
        return b & 0x00ff;
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
        if (!(o instanceof ResourceRecord)) return false;
        ResourceRecord that = (ResourceRecord) o;
        return type == that.type &&
                ttl == that.ttl &&
                rdataLength == that.rdataLength &&
                Objects.equals(name, that.name) &&
                Objects.equals(rdata, that.rdata);
    }


    /**
     *  override hasCode method.
     *
     * @return integer has code vlaue of the object.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(name, type, ttl, rdataLength, rdata);
    }

    /**
     * check for domain name
     * @param name domain name
     * @throws ValidationException
     *          if domain name is invalid
     */
    protected void checkNameValid(String name) throws ValidationException
    {
        if(name == null || name.trim().equals("")){
            throw new ValidationException("invalid domain name", name);
        }

        if(name.startsWith(" ") || name.endsWith(" ")){
            throw new ValidationException("invalid domain name", name);
        }
        if(!name.endsWith(".") || name.contains("..")){
            throw new ValidationException("invalid domain name", name);
        }
        if(name.length() > 255){
            throw new ValidationException("invalid domain name", name);
        }
        String[] split = name.split("\\.");


        for(String label: split){
            if(label.length() > 63){
                throw new ValidationException("invalid domain name", name);
            }
            for (int i = 0; i < label.length(); i++)
            {
                char c = label.charAt(i);
                //label , digits or hyphen is valid
                if(!(Character.isDigit(c) || c == '-' || c == '_' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')){
                    throw new ValidationException("invalid domain name", name);
                }

                //each label must start with a letter
                if(i == 0)
                {
                    if(!Character.isLetter(c))
                        throw new ValidationException("invalid domain name", name);
                }
                //each label must end with a letter
                else if(i == label.length() - 1)
                {
                    if(!(Character.isLetter(c) || Character.isDigit(c)))
                        throw new ValidationException("invalid domain name", name);
                }
            }
        }

    }

    /**
     * check for TTL validation
     * @param ttl ResourceRecord ttl
     * @throws ValidationException
     *          if the TTL is invalid
     */
    protected void checkTtlValid(int ttl) throws ValidationException
    {
        if(ttl < 0){
            throw new ValidationException("invalid ttl", ttl + "");
        }

    }


    protected void writeDomain(String domain, DataOutputStream dos) throws IOException
    {
        String[] split = domain.split("\\.");
        for(String label: split){
            //write each label
            dos.writeByte(label.length());
            for (int i = 0; i < label.length(); i++)
            {
                char c = label.charAt(i);
                dos.writeByte(c);
            }
        }
        dos.writeByte(0);
    }

    /**
     * convert Rdata to domain name
     *
     * @param rdata the specifical rdata
     * @return return the domain name with string type
     * @throws EOFException
     *   if reach to end of file
     */
    protected static String covertRdataToName(String rdata) throws EOFException
    {
        if(rdata.trim().length() == 0){
            return ".";
        }
        int index = 0;
        char[] chars = rdata.toCharArray();
        StringBuilder nameBuilder = new StringBuilder();


        int len = chars[index++];
        if(len > 192){
            return ".";
        }
        //read the domain
        while (len != 0){
            int i = chars[index++];
            char c = (char)i;
            nameBuilder.append(c);
            len--;

            if(len == 0){
                nameBuilder.append('.');
                len = chars[index++];
                if(len == 0){
                    break;
                }else if(len >= 192){
                    break;
                }
            }
        }
        return nameBuilder.toString();
    }

    private static String convertRdataToExchange(String toString) throws IOException
    {

//        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(toString.getBytes());
//        DataInputStream dataInputStream1 = new DataInputStream(inputStream1);
//
//        dataInputStream1.readShort();
//        int available = inputStream1.available();


        String rdata = toString.substring(2);
        if(rdata.trim().length() == 0){
            return ".";
        }
        int index = 0;
        char[] chars = rdata.toCharArray();
        StringBuilder nameBuilder = new StringBuilder();


        int len = chars[index++];
        if(len > 192){
            return ".";
        }
        //read the domain
        while (len != 0){
            int i = chars[index++];
            char c = (char)i;
            nameBuilder.append(c);
            len--;

            if(len == 0){
                nameBuilder.append('.');
                len = chars[index++];
                if(len == 0){
                    break;
                }else if(len >= 192){
                    break;
                }
            }
        }
        return nameBuilder.toString();
    }

    private static int convertRdataToPreference(String toString) throws IOException
    {
        byte[] datas = new byte[4];
        datas[0] = 0;
        datas[1] = 0;
        datas[2] = (byte) toString.toCharArray()[0];
        datas[3] =(byte) toString.toCharArray()[1];
        ByteArrayInputStream inputStream = new ByteArrayInputStream(datas);
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int i = dataInputStream.readInt();
        return i;
    }

    protected static Inet4Address convertDataToInet4address(String rdata) throws UnknownHostException
    {
        StringBuilder address = new StringBuilder();
        boolean first = true;
        for(char c: rdata.toCharArray())
        {
            if(!first){
                address.append(".");
            }else {
                first = false;
            }
            address.append((int)c);
        }
        return (Inet4Address) Inet4Address.getByName(address.toString());
    }

    protected static Inet6Address convertDataToInet6address(String rdata) throws UnknownHostException
    {
        byte[] addr = new byte[rdata.length()];
        int i = 0;
        for(char c: rdata.toCharArray())
        {
            addr[i++] = (byte)c;
        }
//        throw new IllegalArgumentException(rdata.getBytes().length + "");
        return (Inet6Address) Inet6Address.getByAddress(addr);

    }

    /**
     * get the Rdata
     * @return return Rdata with String type
     */
    public String getRdata()
    {
        return rdata;
    }

    /**
     * set Rdata
     * @param rdata the specifical rdata
     */
    public void setRdata(String rdata)
    {
        this.rdata = rdata;
    }



}
