package sdns.app.tcp.client;
/************************************************
 *
 *  Author: Peiyang Chang
 *  Assignment: Program 4
 *  Class: CSI 4321
 *
 *************************************************/

import sdns.serialization.*;

import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Client
{
    private static int id = 1;
    private static Map<Integer, Query> expectList = Collections.synchronizedMap(new HashMap<>());
    private static Socket socket;
    private static boolean retryed = false;

    /**
     * main method to run with arguments
     * @param args arguments entered by user
     */
    public static void main(String[] args)
    {
        if(args.length < 2){
            System.exit(-1);
        }

        try
        {
            Integer port = Integer.parseInt(args[1]);
            InetAddress address = InetAddress.getByName(args[0]);
            socket = new Socket(address, port);
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
                try
                {
                    byte[] bytes = Framer.frameMesg(query.encode());
                    socket.getOutputStream().write(bytes);
                } catch (ValidationException e)
                {
                    e.printStackTrace();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        while (true)
        {
            byte[] buf = new byte[0];
            try
            {
                buf = Framer.nextMsg(socket.getInputStream());
            }catch (SocketTimeoutException e)
            {
                {
                    for(Map.Entry<Integer, Query> entry: expectList.entrySet())
                    {
                        System.err.println("No response:" + entry.getValue().getQuery());

                    }
                    System.exit(-1);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.exit(-1);

            }



            try
            {
                Response response = (Response) Message.decode(buf);
                Query query = expectList.get(response.getID());
                if (query == null)
                {
                    System.err.println("No such ID:" + response.toString());
                    continue ;
                }
                if (!query.getQuery().equals(response.getQuery()))
                {
                    System.err.println("Non-matching query:" + response);
                    continue ;
                }

                if (response.getRCode().getRCodeValue() != 0)
                {
                    System.err.println("Non-zero ecode:" + response);
                    continue ;
                }

                System.out.println(response);
                expectList.remove(response.getID());
                if(expectList.size() == 0)
                    break ;



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
                    e.printStackTrace();
                    System.err.println("validation exception " + e.getMessage());
                }


            } catch (IOException e)
            {
                if (e instanceof EOFException)
                {
                    System.err.println("Packet too short");
                    e.printStackTrace();
                }else
                {
                    System.err.println(e.getMessage());
                }
                break;

            }

        }


        try
        {
            socket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }


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



}
