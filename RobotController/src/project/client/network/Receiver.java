package project.client.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Receiver {
    DatagramSocket socket;
    DatagramPacket packet;
    int DGRAM_MAX_LENGTH = 4096;
    int seqNo = 0;
    int[] header;
    List<DataFragment> list;
    byte[] dgramBuffer;

    public Receiver(int port) throws SocketException {
        socket = new DatagramSocket(port);
        dgramBuffer = new byte[DGRAM_MAX_LENGTH];
        packet = new DatagramPacket(dgramBuffer, DGRAM_MAX_LENGTH);
        list = new ArrayList<DataFragment>();
        header = new int[4];
    }
    
    public void close() {
    	socket.close();
    }

    public byte[] recievePacket() {
        byte[] retdata;
        byte[] bytes;     
        byte[] packetData;
        int size = 0;
        DataFragment mfragment;
        list.clear();
        
        try {
            /*
             * System.out.println("Trying to receive packet");
             */
            
            //Trying to get all fragments of a packet
            while(true) {
                socket.receive(packet);
                packetData = packet.getData();
                
                //Copying header data
                for(int i=0; i<4; i++) {
                    header[i] = 0;
                    for(int j=0; j<4; j++) {
                        header[i] <<= 8;
                        header[i] = header[i] | ((int)packetData[i*4+j] & 0xff);
                    }
                }
                /*
                 * System.out.println("Fragment\t" + "pn:" + header[0] + "  nf:" + header[1] +  "  fno:" + header[2] + "  size:" + header[3]);
                 */
                
                if(seqNo != header[0]) {
                    list.clear();
                    size = 0;
                    seqNo = header[0];
                    //System.out.println("Sequence number reset to : " + seqNo);
                }
                
                /*
                 * System.out.println("\tAdding fragment Number:" + header[2]);
                 */
                
                mfragment = new DataFragment(header[2], packet);
                list.add(mfragment);
                size += header[3];
                
                if(list.size() == header[1]) {
                    break;
                }
            }
            
            /*
             * System.out.println("Recieved Packet : " + size);
             */
            
            //Sorting fragments of a packet on the basis of fragment number
            java.util.Collections.sort(list);

            //Initializaing byte array with size
            retdata = new byte[size];
            size = 0;

            //Copying the data into data array
            for(DataFragment pkt : list) {
                bytes = pkt.getBytes();
                System.arraycopy(bytes, 0, retdata, size, bytes.length);
                size += bytes.length;
            }
            
            //Increasing sequence number by one for sending next image
            seqNo++;
            seqNo %= 1024;
        } catch (IOException e) {
            retdata = null;
        }
        return retdata;
    }
}
