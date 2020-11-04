package sdns.app.udp.server;

import sdns.app.masterfile.MasterFile;
import sdns.app.masterfile.MasterFileFactory;
import sdns.serialization.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class Server
{
    private static Logger logger;

    private static DatagramSocket socket;
    private static MasterFile masterFile;

    public static void main(String[] args) throws IOException
    {
        initServer(args);
        while (true)
        {
            byte[] backbuf = new byte[10240];
            DatagramPacket packet = new DatagramPacket(backbuf, backbuf.length );
            socket.receive(packet);
            byte[] buf1 = new byte[packet.getLength()];
            System.arraycopy(backbuf, 0, buf1, 0, buf1.length);
            try
            {
                Message decode = Message.decode(buf1);
                if(decode instanceof Response)
                {
                    logger.severe("Unexpected message type: :" + decode.toString());
                    Response response = ((Response) decode);
                    response.setRCode(RCode.getRCode(5));

                    if(response.getNameServerList() != null)
                    {
                        response.getNameServerList().clear();
                    }
                    if(response.getAdditionalList() != null)
                    {
                        response.getAdditionalList().clear();
                    }
                    if(response.getAnswerList() != null)
                    {
                        response.getAnswerList().clear();
                    }

                    byte[] encode = response.encode();
                    DatagramPacket sendPacket = new DatagramPacket(encode, encode.length, packet.getSocketAddress());
                    try
                    {
                        socket.send(sendPacket);
                        logger.info(response.toString());
                    }catch (Exception e)
                    {
                        logger.severe("Communication problem: send failed");
                    }

                }else
                {
                    Query query = (Query) decode;
                    handleQuery(packet, query);
                }
            } catch (ValidationException e)
            {
                logger.severe("Unable to parse message: message");
            }
        }

    }


    private static void  handleQuery(DatagramPacket receivePacket, Query query) throws IOException, ValidationException
    {
        List<ResourceRecord> answers = new ArrayList<>();
        List<ResourceRecord> nameservers = new ArrayList<>();
        List<ResourceRecord> additionals = new ArrayList<>();
        Response response = new Response(query.getID(), query.getQuery(), RCode.getRCode(0));

        try
        {
            masterFile.search(query.getQuery(), answers, nameservers, additionals);
        } catch (ValidationException e)
        {
            logger.severe("Problem resolving:" + query);
            response.setRCode(RCode.getRCode(3));
        }catch (Exception e)
        {
            logger.severe("Domain name does not exist:" + query);
            response.setRCode(RCode.getRCode(2));

        }
        logger.info(query.toString());

        response.getAnswerList().addAll(answers);
        response.getNameServerList().addAll(nameservers);
        response.getAdditionalList().addAll(additionals);

        byte[] encode = response.encode();
        DatagramPacket packet = new DatagramPacket(encode, encode.length, receivePacket.getSocketAddress());
        try
        {
            socket.send(packet);
            logger.info(response.toString());
        }catch (Exception e)
        {
            logger.severe("Communication problem: send failed");
        }
    }


    private static void initServer(String[] args)
    {
        logger = Logger.getGlobal();
        logger.setUseParentHandlers(false);
        FileHandler handler = null;
        try
        {
            handler = new FileHandler("connections.log");
            handler.setFormatter(new SimpleFormatter());
        } catch (IOException e)
        {
            logger.severe("unable to start:" + "can't open connection file");
            System.exit(-1);
        }
        logger.addHandler(handler);
        if(args.length < 1){
            logger.severe("unable to start:" + "bad command line arg");
            System.exit(-1);
        }

        try
        {
            masterFile = MasterFileFactory.makeMasterFile();
        } catch (Exception e)
        {
            logger.severe("unable to start:" + "bad command line arg");
            System.exit(-1);

        }

        int port;
        try
        {
            port = Integer.parseInt(args[0]);
            socket = new DatagramSocket(port);
        }catch (NumberFormatException e)
        {
            logger.severe("unable to start:" + "bad command line arg");
            System.exit(-1);
        } catch (SocketException e)
        {
            logger.severe("unable to start:" + "udp server");
            System.exit(-1);
        }

    }
}