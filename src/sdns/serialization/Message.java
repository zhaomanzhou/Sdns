package sdns.serialization;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 4
 *  Class: CSI 4321
 *
 *************************************************/

import sdns.serialization.ResourceRecord;
import sdns.serialization.Response;
import sdns.serialization.ValidationException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Message
{
    protected Integer id;
    protected Integer qr;
    protected Integer rCode = 0;
    protected String query;
    protected String originQuery;

    protected List<ResourceRecord> answerList = new ArrayList<>();
    protected List<ResourceRecord> additionalList = new ArrayList<>();
    protected List<ResourceRecord> nameServerList = new ArrayList<>();

    /**
     * Deserializes message from byte source
     *
     * @param message deserialization byte source
     * @return a specific message resulting from deserialization
     * @throws ValidationException
     *  if parse or validation problem
     * @throws IOException
     *          if I/O problem
     */
    public static Message decode(byte[] message) throws ValidationException, IOException
    {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(message);
        DataInputStream dis = new DataInputStream(inputStream);
        short i = dis.readShort();
        Integer idS = i & 0xffff;
        byte b = dis.readByte();
        for (int j = 0; j < 4; j++)
        {
            if((b >> 3+j & 1) == 1){
                throw new ValidationException("opCode", "header");
            }
        }
        Integer qrS = (b >> 7 & 1);
        b = dis.readByte();
//        for (int j = 0; j < 3; j++)
//        {
//            if((b >> 4+j & 1) == 1){
//                throw new ValidationException("Z", "header");
//            }
//        }
        Integer rCodeS = b  & 0xf;
        if(rCodeS != 0 && qrS == 0){
            throw new ValidationException("rcode", "header");
        }

        short i1 = dis.readShort();
        if(i1 != 1){
            throw new ValidationException("0x0011", "header");
        }
        int anCount = dis.readShort() & 0xffff;
        int nsCount = dis.readShort() & 0xffff;
        int arCount = dis.readShort() & 0xffff;

        //qname
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
            int ii = readOneByte(dis);
            char c = (char)ii;
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

        short i2 = dis.readShort();
        if(i2 != 0x00ff){
            throw new ValidationException("0x00ff", "0x00ff");
        }
        try
        {
            i2 = dis.readShort();
        } catch (IOException e)
        {
            throw new ValidationException("premature end of message","");
        }
        if(i2 != 0x0001){
            throw new ValidationException("0x0001", "0x0001");
        }


        if(qrS == 0){
            if(dis.available() > 0){
                throw new ValidationException("to more", "too more");
            }
            Query query = new Query(idS, nameBuilder.toString());
            return query;
        }else 
        {
            Response response = new Response(idS, nameBuilder.toString(), RCode.getRCode(rCodeS));
            ResourceRecord.checkEof = false;
            try
            {
                for (int j = 0; j < anCount; j++)
                {
                    ResourceRecord decode = ResourceRecord.decode(dis);
                    response.addAnswer(decode);
                }

                for (int j = 0; j < nsCount; j++)
                {
                    ResourceRecord decode = ResourceRecord.decode(dis);
                    response.addNameServer(decode);
                }

                for (int j = 0; j < arCount; j++)
                {
                    ResourceRecord decode = ResourceRecord.decode(dis);
                    response.addAdditional(decode);
                }
            } catch (Exception e)
            {
                throw new ValidationException(".", ".");
            }
            if(dis.available() > 0){
                throw new ValidationException("to more", "too more");
            }
            return response;
        }
        
    }

    /**
     * serialize message as byte array
     *
     * @return serialized message
     * @throws IOException
     *          if I/O problem
     */
    public byte[] encode() throws IOException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(os);
        //id
        dos.writeShort(id);
        //qr, opcode, aa, tc, rd
        int tmp = 0;
        tmp = (tmp | qr);
        //opCode
        tmp  = tmp << 4;
        //AA
        tmp = tmp << 1;
        //TC
        tmp = tmp << 1;
        tmp = tmp << 1;
        tmp |= 1;
        dos.writeByte(tmp);

        tmp = 0;
        //RA
        tmp = tmp << 1;
        //Z
        tmp = tmp << 3;
        //rcode
        for (int i = 3; i >= 0 ; i--)
        {
            tmp = tmp << 1;
            tmp = tmp | ((rCode >> i) & 1);
        }
        dos.writeByte(tmp);
        dos.writeShort(1);
        dos.writeShort(answerList.size());
        dos.writeShort(nameServerList.size());
        dos.writeShort(additionalList.size());

        //Question
        writeDomain(query, dos);
        dos.writeShort(0x00ff);
        dos.writeShort(0x0001);

        for (ResourceRecord record: answerList)
        {
            record.encode(dos);
        }
        for (ResourceRecord record: nameServerList)
        {
            record.encode(dos);
        }
        for (ResourceRecord record: additionalList)
        {
            record.encode(dos);
        }
        return os.toByteArray();
    }

    /**
     * get message ID
     *
     * @return message ID
     */
    public int	getID()
    {
        return this.id;
    }

    /**
     * get query (domain name) of message
     *
     * @return message query
     */
    public String getQuery()
    {
        return this.originQuery;
    }

    /**
     * set ID of message
     *
     * @param id new id of message
     * @return this message with new id
     * @throws ValidationException
     *  if parse or validation problem
     */
    public Message setID(int id) throws ValidationException
    {
        if(id < 0 || id > 65535){
            throw new ValidationException("id", "id");
        }
        this.id = id;
        return this;
    }

    /**
     * set message query (domain name)
     *
     * @param query in the form of a domain name
     * @return this message with new query
     * @throws ValidationException
     *  if parse or validation problem
     */
    public Message setQuery(String query) throws ValidationException
    {
        if(query == null ){
            throw new ValidationException("query", "query");
        }
        checkDomainValid(query);
        this.query = query.trim().toLowerCase();
        this.originQuery = query;
        return this;
    }

    /**
     * check the domain name
     *
     * @param name domain name
     * @throws ValidationException
     *  if parse or validation problem
     */
    protected void checkDomainValid(String name) throws ValidationException
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
                if(!(Character.isDigit(c) || c == '-' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')){
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
     * write domain data
     *
     * @param domain domain name
     * @param dos data output
     * @throws IOException
     *          if I/O problem
     */
    protected void writeDomain(String domain, DataOutputStream dos) throws IOException
    {
        //write rdata
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
     * read one byte from input stream
     *
     * @param in input stream
     * @return data
     * @throws IOException
     *          if I/O problem
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
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) &&
                Objects.equals(qr, message.qr) &&
                Objects.equals(query, message.query);
    }

    /**
     *  override hasCode method.
     *
     * @return integer has code vlaue of the object.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(id, qr, query);
    }
}
