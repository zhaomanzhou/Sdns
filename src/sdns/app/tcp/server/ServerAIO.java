package sdns.app.tcp.server;

import sdns.app.masterfile.MasterFile;
import sdns.app.masterfile.MasterFileFactory;
import sdns.serialization.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ServerAIO
{
    private static Logger logger;

    private static AsynchronousServerSocketChannel server;
    private static MasterFile masterFile;
    private static Integer defaultTimeout = 20;

    /**
     * main method
     *
     * @param args arguments entered by user
     * @throws IOException if I/O fail
     */
    public static void main(String[] args) throws IOException
    {
        initServer(args);
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>()
        {
            @Override
            public void completed(AsynchronousSocketChannel socketChannel, Object attachment)
            {
                server.accept(null, this);
                ByteBuffer readBuffer = ByteBuffer.allocate(2550);
                socketChannel.read(readBuffer, defaultTimeout, TimeUnit.SECONDS, new byte[0], new SocketChannelHandle(socketChannel, readBuffer));
            }

            @Override
            public void failed(Throwable exc, Object attachment)
            {
                exc.printStackTrace();
            }
        });

        try
        {
            CountDownLatch latch = new CountDownLatch(1);
            latch.await();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }


    }

    static class SocketChannelHandle implements CompletionHandler<Integer, byte[]>
    {


        private AsynchronousSocketChannel socketChannel;
        private ByteBuffer byteBuffer;
        private NIODeframer nioDeframer;
        private boolean end;

        public SocketChannelHandle(AsynchronousSocketChannel socketChannel, ByteBuffer byteBuffer)
        {
            this.socketChannel = socketChannel;
            this.byteBuffer = byteBuffer;
            nioDeframer = new NIODeframer();
        }

        @Override
        public void completed(Integer result, byte[] buffer)
        {
            if (result == -1)
            {
                try
                {
                    if(this.socketChannel.isOpen())
                        this.socketChannel.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                return;
            }


            this.byteBuffer.flip();
            byte[] contexts = new byte[result];
            this.byteBuffer.get(contexts, 0, result);
            this.byteBuffer.clear();

            byte[] newBuffer = new byte[buffer.length + contexts.length];

            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            System.arraycopy(contexts, 0, newBuffer, buffer.length, contexts.length);
            buffer = newBuffer;
            byte[] message = nioDeframer.getMessage(buffer);
            if (message == null)
            {
                this.socketChannel.read(this.byteBuffer, defaultTimeout, TimeUnit.SECONDS, buffer, this);
            } else
            {
                handleMessage(message);
                int length = message.length;
                newBuffer = new byte[buffer.length - length - 2];
                System.arraycopy(buffer, 2 + length, newBuffer, 0, newBuffer.length);
                buffer = newBuffer;
                this.socketChannel.read(this.byteBuffer, defaultTimeout, TimeUnit.SECONDS, buffer, this);

            }


        }

        private void handleMessage(byte[] bytes)
        {
            try
            {
                Message decode = Message.decode(bytes);
                if (decode instanceof Response)
                {
                    logger.severe("Unexpected message type: :" + decode.toString());
                    Response response = ((Response) decode);
                    response.setRCode(RCode.getRCode(5));

                    if (response.getNameServerList() != null)
                    {
                        response.getNameServerList().clear();
                    }
                    if (response.getAdditionalList() != null)
                    {
                        response.getAdditionalList().clear();
                    }
                    if (response.getAnswerList() != null)
                    {
                        response.getAnswerList().clear();
                    }

                    try
                    {
                        try
                        {
                            byte[] encode = Framer.frameMesg(response.encode());
                            ByteBuffer writeBuffer = ByteBuffer.allocate(encode.length);
                            writeBuffer.put(encode);
                            writeBuffer.flip();
                            socketChannel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>()
                            {
                                @Override
                                public void completed(Integer result, ByteBuffer attachment)
                                {
                                    if (attachment.hasRemaining())
                                    {
                                        socketChannel.write(attachment, attachment, this);
                                    }
                                }
                                @Override
                                public void failed(Throwable exc, ByteBuffer attachment)
                                {
                                    try
                                    {
                                        socketChannel.close();
                                    } catch (IOException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            logger.info(response.toString());

                        } catch (Exception e)
                        {
                            socketChannel.close();
                            logger.severe("Communication problem: send failed");
                            end = true;
                        }
                        logger.info(response.toString());
                    } catch (Exception e)
                    {
                        logger.severe("Communication problem: send failed");
                        end = true;
                    }
                } else
                {
                    Query query = (Query) decode;
                    end = !handleQuery(query);
                }
            } catch (IOException e)
            {
                try
                {
                    socketChannel.close();
                } catch (IOException ioException)
                {
                    logger.severe(ioException.getMessage());
                } finally
                {
                    end = true;
                }
            } catch (ValidationException e)
            {
                logger.severe("Unable to parse message: message");
            }
        }


        /**
         * handleQuery
         *
         * @param query query
         * @return true if send sucess, false if send fail
         * @throws IOException         If I/O fail
         * @throws ValidationException If Validation fail
         */
        private boolean handleQuery(Query query) throws IOException, ValidationException
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
            } catch (Exception e)
            {
                logger.severe("Domain name does not exist:" + query);
                response.setRCode(RCode.getRCode(2));

            }
            logger.info(query.toString());

            response.getAnswerList().addAll(answers);
            response.getNameServerList().addAll(nameservers);
            response.getAdditionalList().addAll(additionals);

            byte[] encode = Framer.frameMesg(response.encode());

            try
            {
                ByteBuffer writeBuffer = ByteBuffer.allocate(encode.length);
                writeBuffer.put(encode);
                writeBuffer.flip();
                socketChannel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>()
                {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment)
                    {
                        if (attachment.hasRemaining())
                        {
                            socketChannel.write(attachment, attachment, this);
                        }
                    }
                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment)
                    {
                        try
                        {
                            socketChannel.close();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
                logger.info(response.toString());
            } catch (Exception e)
            {
                socketChannel.close();
                logger.severe("Communication problem: send failed");
                return false;
            }

            return true;
        }


        @Override
        public void failed(Throwable exc, byte[] buffer)
        {
            try
            {
                this.socketChannel.shutdownInput();
                this.socketChannel.shutdownOutput();
                this.socketChannel.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    /**
     * initial server
     *
     * @param args arguments entered by user
     */
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
        if (args.length < 1)
        {
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
            server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
        } catch (NumberFormatException e)
        {
            logger.severe("unable to start:" + "bad command line arg");
            System.exit(-1);
        } catch (IOException e)
        {
            logger.severe("unable to start:" + "udp server");
            System.exit(-1);
        }


    }


}
