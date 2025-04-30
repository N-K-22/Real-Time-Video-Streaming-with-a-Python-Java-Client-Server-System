import org.opencv.*;
import org.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;
import java.io.IOException;
import java.net.*;
//Apologies beforehand if the code looks very convoluted and messy, it has been a while since I have used Java

class Client_Implementation{
    private ArrayList frame_storage;
    private String path_video = "";
    private VideoCapture video_capture;
    private  Mat video_frame; //https://opencv-java-tutorials.readthedocs.io/en/latest/03-first-javafx-application-with-opencv.html
    public Client_Implementation(){
        this.video_capture = new VideoCapture(path_video);
        if (!video_capture.isOpened()){
            System.out.println("Video file could not be loaded in");
            return;
        }
        this.video_frame = new Mat();
        is_finished = true;
    }

    private boolean is_finished;

    public boolean read_single_frame(){ //reading in the video frames one by one
        this.is_finished = this.video_capture.read(this.video_frame);
        if (is_finished){
            frame_storage.add(video_frame);
        }
        return is_finished;
    } 
    private void view_frame(Mat frame){
        HighGui.imshow("Frames to send over UDP",frame);
    }
    private void close_frames(){
        HighGui.destroyAllWindows();
    }
    public boolean frames_processed(){
        this.video_capture.release();
        return true;
    }

}
public class Socket_Transmission_Frames extends Client_Implementation{
    private boolean connected;
    private DatagramSocket sock;
    private InetAddress server_address;
    private int port;

    public Socket_Transmission_Frames(String address. int port){
        try{
            this.sock = new DatagramSocket();
            this.server_address = InetAddress.getbyName(address);
            this.port = port;
        }
        catch(IOException e){
            System.out.println("Failed to connect client to server! Exception Message:"+ e.getMessage);
            connected = false;
        }
    }
    public DatagramPacket packet_creation(byte[] frame){
        DatagramPacket packet = new DatagramPack(frame, frame.length, this.server_address, this.port);
     //https://stackoverflow.com/questions/6137140/return-a-byte-array-from-a-java-method
     // https://docs.oracle.com/javase/8/docs/api/java/net/DatagramPacket.html
        return packet;
    }

    public boolean send_frame(byte[] frame){
        try{
            Datagram packet = packet_creation(frame);
            this.sock.send(packet);
        }catch (IOException e){
            System.err.println("Failed to send UDP packet");
        }
    }

    public boolean is_connect(){
        if (this.sock != null && !sock.isClosed()) connected = true;
        return true;
    }

    public void close_socket(){
        if (this.sock != null  || !(this.sock.isClosed())){
            this.sock.close();
        }
    }
}

public class video_streaming implements Socket_Transmission_Frames {


}