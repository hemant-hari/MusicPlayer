package sample;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.*;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.*;


public class Controller {
    private Player player;
    private Visualiser vis;

    private Boolean seekerDown;

    public ToggleButton pbutton;
    public Button skip;
    public Button prev;
    public Button close;
    public Button minimise;
    public ImageView plypsimage;
    public Slider slider;
    State st = State.paused;

    @FXML
    public void initialize() throws IOException, LineUnavailableException,
            UnsupportedAudioFileException {
        player = new Player();
        player.openFile("/Users/heman/Downloads/lovedramatic.mp3");
        vis = player.getVisualiser();
        setupSeeker();
    }

    private void setupSeeker() {
        DoubleProperty svalue = slider.valueProperty();
        slider.setMin(0.0);
        slider.setMax(player.getStopTime().toSeconds());
        slider.setMajorTickUnit(0.5);
        slider.setSnapToTicks(true);

        player.getStopTimeProperty().addListener(
                new ChangeListener<Duration>() {
                    @Override
                    public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                        System.out.println(newValue.toSeconds());
                        slider.setMax(newValue.toSeconds());
                    }
                }
        );

        svalue.addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue <? extends Number >
                                                observable, Number oldValue, Number newValue)
                    {
                        if ((double)oldValue - (double)newValue > 0.2){ player.seek((double) newValue);}
                    }
                });

        player.getTimeProperty().addListener(
                new ChangeListener<Duration>() {
                    @Override
                    public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue)
                    {
                        if (seekerDown) {slider.setValue(newValue.toSeconds());}
                    }
                }
        );
    }

    private void printVisualiser(){
        float[] visData = vis.getVisualiserValues();
        for (int i=0; i<visData.length; i++){
            System.out.print(visData[i]);
        }
        System.out.println("");
        System.out.println(player.getStopTime().toSeconds());
    }

    public void playPressed(){
        if (st != State.playing){
            player.play();
            st = State.playing;
        }
        else{
            printVisualiser();
            player.pause();
            st = State.paused;
        }
        System.out.println(st);
        ToggleImage();
    }

    public void stopPressed(){
        if (st == State.playing) { playPressed(); }
        st = State.stopped;
        player.stop();
        System.out.println(st);
    }

    public void prevPressed(){
        player.previous();
    }

    public void sliderPressed(){
        System.out.println("sliderdown");
        seekerDown=true;
    }

    public void sliderReleased(){
        System.out.println("sliderup");
        seekerDown=false;
    }

    public void ToggleImage(){
        try{
            if (st == State.playing){
                Image pauseimg = new Image(getClass().getResource("icons/pause.png").toURI().toString());
                plypsimage.setImage(pauseimg);
                System.out.println("changing img");
            }
            else{
                Image playimg = new Image(getClass().getResource("icons/play-button.png").toURI().toString());
                plypsimage.setImage(playimg);
            }}
        catch(URISyntaxException e){
                throw new Error(e);
            }
    }

    public void skipPressed(){
        player.skipTrack();
    }

    public void closeWindow(){
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();
    }

    public void minimiseWindow(){
        Stage stage = (Stage) minimise.getScene().getWindow();
        stage.setIconified(true);
    }
}
