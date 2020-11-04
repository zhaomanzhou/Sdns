package sdns.app.tcp.server;

import sdns.app.masterfile.MasterFile;
import sdns.app.masterfile.MasterFileFactory;
import sdns.serialization.*;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Server
{
    private static Logger logger;

    private static ServerSocket socket;
    private static MasterFile masterFile;
    private static ExecutorService es;
    private static Integer defaultTimeout = 1000*20;

    public static void main(String[] args) throws IOException
    {
        initServer(args);
        while (true)
        {
            Socket accept = socket.accept();
            es.submit(new SocketHandler(accept));
        }

    }





    private static void initServer(String[] args)
    {
        logger = Logger.getGlobal();
        logger.setUseParentHandlers(true);
        FileHandler handler = null;
        try
        {
            handler = new FileHandler("connections.log");
        } catch (IOException e)
        {
            logger.severe("unable to start:" + "can't open connection file");
            System.exit(-1);
        }
        logger.addHandler(handler);
        if(args.length < 2){
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
        int threadNum;
        try
        {
            port = Integer.parseInt(args[0]);
            threadNum = Integer.parseInt(args[1]);
            es = new ThreadPoolExecutor(threadNum, threadNum*10, 60, TimeUnit.SECONDS
            ,new LinkedBlockingQueue<>(100));
            socket = new ServerSocket(port);
        }catch (NumberFormatException e)
        {
            logger.severe("unable to start:" + "bad command line arg");
            System.exit(-1);
        } catch (IOException e)
        {
            logger.severe("unable to start:" + "udp server");
            System.exit(-1);
        }


    }


    static class SocketHandler implements Runnable{

        private Socket socket;


        public SocketHandler(Socket socket)
        {
            this.socket = socket;
            try
            {
                socket.setSoTimeout(defaultTimeout);
            } catch (SocketException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void run()
        {
            boolean end = false;
            while (!end)
            {
                try
                {
                    byte[] bytes = Framer.nextMsg(socket.getInputStream());
                    Message decode = Message.decode(bytes);
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
                        try
                        {
                            try
                            {
                                socket.getOutputStream().write(Framer.frameMesg(encode));
                                logger.info(response.toString());
                            }catch (Exception e)
                            {
                                socket.close();
                                logger.severe("Communication problem: send failed");
                                end = true;
                            }
                            logger.info(response.toString());
                        }catch (Exception e)
                        {
                            logger.severe("Communication problem: send failed");
                            end = true;
                        }
                    }else
                    {
                        Query query = (Query) decode;
                        end = !handleQuery(query);
                    }
                } catch (IOException e)
                {
                    try
                    {
                        socket.close();
                    } catch (IOException ioException)
                    {
                        logger.severe(ioException.getMessage());
                    }finally
                    {
                        end =true;
                    }
                } catch (ValidationException e)
                {
                    logger.severe("Unable to parse message: message");
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


        private boolean  handleQuery( Query query) throws IOException, ValidationException
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

            try
            {
                socket.getOutputStream().write(Framer.frameMesg(encode));
                logger.info(response.toString());
            }catch (Exception e)
            {
                socket.close();
                logger.severe("Communication problem: send failed");
                return false;
            }

            return true;
        }
    }


}


