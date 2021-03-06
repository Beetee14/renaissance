package com.stefanaleksic.renaissance;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
//import javafx.scene.input.MouseButton;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by Stefan on 1/3/2015.
 * This code is taken from the TarsosDSP example code.
 * To check this awesome library out yourself, look at https://github.com/JorenSix/TarsosDSP
 */
public class Main implements PitchDetectionHandler {

    private AudioDispatcher dispatcher;
    private Mixer currentMixer;
    private PitchProcessor.PitchEstimationAlgorithm algo;

    public static void main(String[] args) {
        boolean SHOULD_LOOP = true;

        Main main = new Main();
        main.algo = PitchProcessor.PitchEstimationAlgorithm.AMDF;
        //This tests if the loop finished by finding a microphone or not.
        int index = -1;
        try {
            index = main.getMicrophone(0);
        } catch (MicrophoneNotFoundException mnfe) {
            mnfe.printStackTrace();
        }

        Mixer currentMicrophoneMixer = main.getNewMixer(index);


        while (SHOULD_LOOP) {

            try {

                main.setNewMixer(currentMicrophoneMixer);

                //Because there was no exception caught, the code should not loop again.
                SHOULD_LOOP = false;

            } catch (LineUnavailableException e) {

                //The microphone is being used right now so try another one.
                currentMicrophoneMixer = main.getNewMixer(index);

                //Now that there is a new microphone, try again and see if this one works.
                SHOULD_LOOP = true;
            } catch (UnsupportedAudioFileException e) {

                //The mixer didn't have valid data, therefore, should try a different mixer.
                currentMicrophoneMixer = main.getNewMixer(index);

                //Now that there is a new microphone, try again and see if this one works.
                SHOULD_LOOP = true;
            }
        }
    }


    /**
     *
     * @param index
     * The index at which this mixer is found.
     * @return
     * The Mixer at the index.
     */
    private Mixer getNewMixer(int index) {
        Mixer mixer = AudioSystem.getMixer(AudioSystem.getMixerInfo()[index]);
        return mixer;
    }


    /**
     * @param offset Starting point of search.
     * @return Index of first microphone mixer found with respect to offset.
     */
    private int getMicrophone(int offset) throws MicrophoneNotFoundException {
        //Mixer's Info object provides information on what each mixer is.
        //All the elements in the array are in respect to different mixers at the same index.
        Mixer.Info[] allInfos = AudioSystem.getMixerInfo();

        for (int i = offset; i < allInfos.length; i++) {
            Mixer.Info currentInfo = allInfos[i];
            if (currentInfo.getName().contains("Microphone")) {
                return i;
            }
        }

        //If code reaches the end of the for loop, that means that no microphone was found.
        throw new MicrophoneNotFoundException();
    }

    /**
     * This was one of the methods taken from the example code from the library in use.
     *
     * @param mixer The mixer a one wants to use.
     * @throws LineUnavailableException
     * @throws UnsupportedAudioFileException
     */
    private void setNewMixer(Mixer mixer) throws LineUnavailableException,
            UnsupportedAudioFileException {

        if (dispatcher != null) {
            dispatcher.stop();
        }
        currentMixer = mixer;

        //A common sample rate is 44100 and a common bufferSize is 1024.
        //That's why they are used here
        float sampleRate = 44100;
        int bufferSize = 1024;
        int overlap = 0;
        int sampleInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;


        AudioFormat format = new AudioFormat(sampleRate, sampleInBits, channels, signed,
                bigEndian);

        DataLine.Info dataLineInfo = new DataLine.Info(
                TargetDataLine.class, format);
        TargetDataLine line = (TargetDataLine) mixer.getLine(dataLineInfo);

        int numberOfSamples = bufferSize;
        line.open(format, numberOfSamples);
        line.start();

        AudioInputStream stream = new AudioInputStream(line);

        JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);

        dispatcher = new AudioDispatcher(audioStream, bufferSize,
                overlap);

        dispatcher.addAudioProcessor(new PitchProcessor(algo, sampleRate, bufferSize, this));

        new Thread(dispatcher, "Audio dispatching").start();
    }

    //TODO: Create a way for users to play musical notes and map them to keys.

    private int count = 0;
    private ArrayList<Double> pitches = new ArrayList<Double>();

    private boolean isBetween(double value, double one, double two){
        return one < value && value < two;
    }

    //TODO: Get this to write directly to the keyboard buffer so that it works with other native programs that aren't text boxes.
    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        float pitch = 0;
        if ((pitch = pitchDetectionResult.getPitch()) != -1) {


            pitches.add((double) pitch);
            System.out.println(pitch + " hz");
            count++;
            //TODO: Get the robot to do things based on musical notes.
            try {
                Robot robot = new Robot();
                int mouseX = (int)MouseInfo.getPointerInfo().getLocation().getX();
                int mouseY =  (int)MouseInfo.getPointerInfo().getLocation().getY();


                //On my flute, this is B flat
                if(true == false/*isBetween(pitch,450,500)*/){

//                    robot.mouseMove(mouseX + 10,mouseY);
//                   robot.keyPress(KeyEvent.VK_RIGHT);
//                    robot.keyRelease(KeyEvent.VK_RIGHT);
                }
                //On my flute, this is A
                else if(isBetween(pitch,410,450)){

                    robot.mouseMove(mouseX + 10, mouseY);
//                    robot.keyPress(KeyEvent.VK_DOWN);
//                    robot.keyRelease(KeyEvent.VK_DOWN);
//                	robot.keyPress(KeyEvent.VK_A);
//                	robot.keyRelease(KeyEvent.VK_A);
                }
//                else if(isBetween(pitch,350,410)){
//                    robot.mouseMove(mouseX - 10, mouseY);
//                    robot.keyPress(KeyEvent.VK_LEFT);
//                    robot.keyRelease(KeyEvent.VK_LEFT);
//                }
//                else if(isBetween(pitch,510,540)){
//                    robot.mouseMove(mouseX, mouseY - 10);
//                    robot.keyPress(KeyEvent.VK_A);
//                    robot.keyRelease(KeyEvent.VK_A);
//                }
                // Violin B
                else if(isBetween(pitch,450,500)){
                	robot.keyPress(KeyEvent.VK_B);
                	robot.keyRelease(KeyEvent.VK_B);
                }
                // Violin C
                else if(isBetween(pitch,500,550)){
                	robot.keyPress(KeyEvent.VK_C);
                	robot.keyRelease(KeyEvent.VK_C);
                }
                // Violin D
                else if(isBetween(pitch,550,600)){
                	robot.keyPress(KeyEvent.VK_D);
                	robot.keyRelease(KeyEvent.VK_D);
                }
                // Violin open E
                else if(isBetween(pitch,650,700)){
//                	robot.keyPress(KeyEvent.VK_E);
//                	robot.keyRelease(KeyEvent.VK_E);
                	robot.mouseMove(mouseX, mouseY - 10);
                }
                // Violin F
                else if(isBetween(pitch,700,750)){
                	robot.keyPress(KeyEvent.VK_F);
                	robot.keyRelease(KeyEvent.VK_F);
                }
                // Violin G
                else if(isBetween(pitch,750,800)){
                	robot.keyPress(KeyEvent.VK_G);
                	robot.keyRelease(KeyEvent.VK_G);
                }
                // Violin A on E string
                else if(isBetween(pitch,550,900)){
                	robot.keyPress(KeyEvent.VK_H);
                	robot.keyRelease(KeyEvent.VK_H);
                }
                // Violin open G
                // The default reading seems to be ~81 so I
                // had to shorten this interval
                else if(isBetween(pitch,90,100)){
//                	robot.keyPress(KeyEvent.VK_B);
//                	robot.keyRelease(KeyEvent.VK_B);
                	robot.mouseMove(mouseX - 10, mouseY);
                }
                // Violin open D
                else if(isBetween(pitch,290,300)){
//                	robot.keyPress(KeyEvent.VK_B);
//                	robot.keyRelease(KeyEvent.VK_B);
                	robot.mouseMove(mouseX , mouseY + 10);
                }
                // Violin C on G string
                else if(isBetween(pitch,260,265)){
//                	robot.keyPress(KeyEvent.VK_B);
//                	robot.keyRelease(KeyEvent.VK_B);
                	robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                	robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                }

            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }








}
