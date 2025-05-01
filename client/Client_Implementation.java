import java.io.IO;
import org.opencv.*;
import org.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;
import java.io.IOException;
import java.net.*;
import org.json.JSONObject;
import java.io.*;
//Apologies beforehand if the code looks very convoluted and messy, it has been a while since I have used Java

public class Client_Implementation{
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