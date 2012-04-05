package project.client.network;

import java.net.DatagramPacket;

/*
 * This class is a helper class for Receiver. Reciever makes the 
 * fragment of each UDP packet.
 * 
 * This class maintains state information of fragment
 * 
 * This is comparable object on the basis of its fragment number, 
 * to facilitate sorting the packets according to the fragment 
 * Number in a ArrayList 
 */
class DataFragment implements Comparable<DataFragment> {
    public int seqNo;
    byte[] buffer;
    
    /*
     * Allocate new buffer space for storing data of Datagram Packet,
     * Also assign seqNo
     */
    public DataFragment(int seq, DatagramPacket pkt) {
        seqNo = seq;
        buffer = new byte[pkt.getData().length];
        System.arraycopy(pkt.getData(), 0, buffer, 0, buffer.length);
    }
    
    public int compareTo(DataFragment another) {
        // TODO Auto-generated method stub
        int seqNo2 = another.seqNo;
        if(seqNo > seqNo2)
            return 1;
        else if (seqNo < seqNo2)
            return -1;
        else
            return 0;
    }

    /*
     * Removes the header data and returns the byte array of fragment data
     */
    public byte[] getBytes() {
        
        int size = 0;
        for(int i=12; i<16; i++) {
            size <<= 8;
            size |= (int)buffer[i] & 0xff;
        }
        //System.out.println("Fragment size: " + size);
        byte[] retval = new byte[size];
        System.arraycopy(buffer, 16, retval, 0, size);
        return retval;
    }
}