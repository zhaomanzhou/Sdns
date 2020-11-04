package sdns.app.udp.client;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 4
 *  Class: CSI 4321
 *
 *************************************************/

import sdns.serialization.Message;
import sdns.serialization.Query;
import sdns.serialization.Response;
import sdns.serialization.ValidationException;

import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;


public class Client
{
    private static int id = 1;
    private static Map<Integer, Query> expectList = Collections.synchronizedMap(new HashMap<>());
    private static DatagramSocket socket;
    private static String[] serverArgs;
    private static ExecutorService es;
    private static boolean retryed = false;

    /**
     * main function that will run the parameters in command-line
     * @param args parameters enter by user
     */
    public static void main(String[] args)
    {
        if(args.length < 2){
            System.exit(-1);
        }

        serverArgs = args;
        try
        {
            Integer port = Integer.parseInt(args[1]);
            socket = new DatagramSocket();
            socket.setSoTimeout(3000);
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        for (int i = 2; i < args.length; i++)
        {
            Query query = convertToQuery(args[i]);
            if(query != null)
            {
                expectList.put(query.getID(), query);
            }
        }

        es = Executors.newFixedThreadPool(10);

        for(Query query: expectList.values())
        {
            es.submit(new QueryThread(query));
        }

        es.shutdown();




    }

    /**
     * convert to Query
     * @param arg arguments
     * @return query object
     */
    private static Query convertToQuery(String arg)
    {
        Query query = null;
        try
        {
            query = new Query(id++, arg);
        } catch (ValidationException e)
        {
            System.out.printf("Malformed question: %s\n", arg);
        }
        return query;
    }

    /**
     * query thread
     */
    static class QueryThread implements Runnable{

        private Query query;
        private boolean retryed = false;

        public QueryThread(Query query)
        {

            this.query = query;
        }

        @Override
        public void run()
        {


            try
            {
                InetAddress address = InetAddress.getByName(serverArgs[0]);  //服务器地址
                Integer port = Integer.parseInt(serverArgs[1]);
                byte[] buf = query.encode();
                DatagramPacket dataGramPacket = new DatagramPacket(buf, buf.length, address, port);
                socket.send(dataGramPacket);
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            receivedPackage();
        }

        public void receivedPackage()
        {
            byte[] backbuf = new byte[10240];
            DatagramPacket backPacket = new DatagramPacket(backbuf, backbuf.length);
            try
            {
                socket.receive(backPacket);  //接收返回数据
            } catch (SocketTimeoutException e)
            {
                if (!retryed)
                {
                    retryed = !retryed;
                    receivedPackage();
                } else
                {
                    System.err.println("No response:" + query.getQuery());
                    return;
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }


            byte[] buf1 = new byte[backPacket.getLength()];
            System.arraycopy(backbuf, 0, buf1, 0, buf1.length);
            try
            {
                Response response = (Response) Message.decode(buf1);
                Query query = expectList.get(response.getID());
                if (query == null)
                {
                    System.err.println("No such ID:" + response.toString());
                    receivedPackage();
                    return;
                }
                if (!query.getQuery().equals(response.getQuery()))
                {
                    System.err.println("Non-matching query:" + response);
                    receivedPackage();
                    return;
                }

                if (response.getRCode().getRCodeValue() != 0)
                {
                    System.err.println("Non-zero ecode:" + response);
                    return;
                }

                System.out.println(response);
                expectList.remove(response.getID());
                if (expectList.size() == 0)
                {
                    es.shutdown();
                }


            } catch (ValidationException e)
            {
                if (e.getBadToken().equals("header"))
                {
                    System.err.println("Unexpected header content");
                } else if (e.getBadToken().equals("too more"))
                {
                    System.err.println("Packet too long");
                } else if (e.getBadToken().equals("rcode"))
                {
                    System.err.println("Unexpected query:");
                } else
                {
                    System.err.println(e.getMessage());
                }


            } catch (IOException e)
            {
                if (e instanceof EOFException)
                {
                    System.err.println("Packet too short");
                }else
                {
                    System.err.println(e.getMessage());
                }

            }
        }


    }

}


