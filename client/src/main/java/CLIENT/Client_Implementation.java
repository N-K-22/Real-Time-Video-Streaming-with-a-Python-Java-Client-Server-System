package CLIENT;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.Videoio;

import java.util.ArrayList;
//Apologies beforehand if the code looks very convoluted and messy, it has been a while since I have used Java
public class Client_Implementation{
    private ArrayList frame_storage;
    private final String path_video = "src/resources/IMG_0146.MOV";
    private VideoCapture video_capture;
    private double numberOfFrames;
    private Mat video_frame; //https://opencv-java-tutorials.readthedocs.io/en/latest/03-first-javafx-application-with-opencv.html
    public Client_Implementation(){
        this.video_capture = new VideoCapture(path_video);
        if (!video_capture.isOpened()){
            System.out.println("Video file could not be loaded in");
            return;
        }
        this.video_frame = new Mat();
        is_finished = true;
        System.out.println("Trying to load video from: " + path_video);
        System.out.println("VideoCapture opened: " + video_capture.isOpened());
        numberOfFrames = video_capture.get(Videoio.CAP_PROP_FRAME_COUNT); //https://stackoverflow.com/questions/25359288/how-to-know-total-number-of-frame-in-a-file-with-cv2-in-python
        frame_storage = new ArrayList<>(); //fix null pointer error

    }

    private boolean is_finished;

    public boolean read_single_frame(){ //reading in the video frames one by one
        this.is_finished = this.video_capture.read(this.video_frame);
        if (is_finished){
            frame_storage.add(video_frame);
        }
        return is_finished;
    }
    public void view_frame(Mat frame){
        HighGui.imshow("Frames to send over UDP",frame);
    }
    public void close_frames(){
        HighGui.destroyAllWindows();
    }
    public boolean frames_processed(){
        this.video_capture.release();
        return true;
    }
    public Mat get_frame(){
        return this.video_frame;
    }
    public double number_of_frames(){
        return this.numberOfFrames;
    }
}