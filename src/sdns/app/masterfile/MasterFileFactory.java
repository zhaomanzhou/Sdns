package sdns.app.masterfile;

import sdns.serialization.*;

import java.net.*;
import java.util.List;
import java.util.NoSuchElementException;

public class MasterFileFactory
{
    static int id = 1;

    public static MasterFile makeMasterFile() throws Exception {
        return new MasterFile() {
            @Override
            public void search(String question, List<ResourceRecord> answers, List<ResourceRecord> nameservers,
                               List<ResourceRecord> additionals)
                    throws NoSuchElementException, NullPointerException, ValidationException
            {
                try
                {
                    DatagramSocket socket = new DatagramSocket();
                    Query query = new Query(id++, question);
                    InetAddress address = InetAddress.getByName(" 129.62.148.40");  //服务器地址
                    byte[] buf = query.encode();
                    DatagramPacket dataGramPacket = new DatagramPacket(buf, buf.length, address, 53);
                    socket.send(dataGramPacket);
                    byte[] backbuf = new byte[10240];
                    DatagramPacket backPacket = new DatagramPacket(backbuf, backbuf.length);
                    socket.receive(backPacket);
                    byte[] buf1 = new byte[backPacket.getLength()];
                    System.arraycopy(backbuf, 0, buf1, 0, buf1.length);
                    Response response = (Response) Message.decode(buf1);
                    answers.addAll(response.getAnswerList());
                    nameservers.addAll(response.getNameServerList());
                    additionals.addAll(response.getAdditionalList());
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }


        };
    }
}
