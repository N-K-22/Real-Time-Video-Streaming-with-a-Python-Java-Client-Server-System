import CLIENT.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import java.lang.*;
import java.net.DatagramPacket;
import java.util.concurrent.Semaphore;
import org.opencv.core.Core;
//web port 8080, 0.0.0.0
//for threading purposes:
// https://codescoddler.medium.com/basics-of-shared-resources-in-multithreaded-environment-d5c79936226d
//https://medium.com/javarevisited/semaphore-in-java-6824fe663975
//https://aeontanvir.medium.com/java-multithreading-a-step-by-step-guide-for-concurrent-programming-3bf5dccbbfa1
//https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html#:~:text=A%20thread%20is%20a%20thread,to%20threads%20with%20lower%20priority.
//https://www.w3schools.com/java/java_threads.asp
public class Main extends Thread {
    private static Semaphore semaphore;
    public String address = "127.0.0.1";
    public int port = 12345;

    public boolean get_feedback_received(){
        return this.feedback_received;
    }

    private boolean feedback_received = true;
    private boolean video_finish = false;
    public boolean video_finished(){
        return video_finish;
    }
    public void setValue(boolean value){
        video_finish = value;
    }
    public void setFeedback(boolean value){
        feedback_received = value;
    }
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); //https://www.youtube.com/watch?v=TsUhEuySano
        //System.out.println(Core.VERSION);//Uncomment when you want to debug whether the OpenCV is working properly in IntelliJ
        semaphore = new Semaphore(1,true);
        Main feedback = new Main();
        Client_Implementation client = new Client_Implementation(); //update the file path to video
        double total = client.number_of_frames();
        System.out.println("Number of frames: " + total);
        System.out.println("Creating 2 threads");

        Runnable UDP_Transmission_task = ()->{ //UDP transmission

            System.out.println("Transmitting Frames over UDP");
            Socket_Transmission_Frames socket = new Socket_Transmission_Frames(feedback.address, feedback.port);
            Mat frame_to_send = new Mat();
            long frames_sent = 0;
            boolean status = true;
            while(frames_sent < total){
                try {
                    semaphore.acquire(); //acquire semaphore to allow for the synchronization where there is not a bombardment to the frames side for better consistent streaming rate
                    status = client.read_single_frame();
                    frame_to_send = client.get_frame();
                    client.view_frame(frame_to_send);
                    byte[] packet_buffer = new byte[frame_to_send.rows() * frame_to_send.cols()];
                    frame_to_send.get(0,0,packet_buffer); //https://answers.opencv.org/question/4761/mat-to-byte-array/
                    socket.send_frame(packet_buffer);
                    frames_sent++;
                    if(frames_sent >= total){
                        feedback.setValue(true);
                    }
                    System.out.println("Frame transmitted over UDP!");
                    semaphore.release();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Runnable task2 = ()->{ // Socket.IO message listening
            System.out.println("Listening over SocketIO");
            video_streaming stream = new video_streaming();
            stream.connect_socket("http://localhost:8080"); //make sure it is set up correctly
            long frames_received = 0;
            while(stream.is_connected()){ // while it is true
                try {
                    semaphore.acquire();
                    //System.out.println("SocketIO connected: " + stream.is_connected());
                    //System.out.println("SocketIO Listening occurs");
                    feedback.setFeedback(true);
                    if(feedback.video_finished()){stream.disconnect();}
                    semaphore.release();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        };
        //spawning threads
        Thread thread1 = new Thread(UDP_Transmission_task);
        Thread thread2 = new Thread(task2);
//starting threads
        thread1.start();
        thread2.start();
        //threads wait for each other to finish
       try{
            thread1.join(5000);
            thread2.join(5000);

       }catch (InterruptedException e){}
        System.out.println("Transmission Completed");
    }

}




