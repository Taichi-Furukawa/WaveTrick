package com.wavetrick.game;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioSystem;

/**
 * Created by furukawa on 15/09/25.
 */
public class AudioInterface {
    private static byte[] voiceData;
    private static AudioFormat linear;
    private static int sample_length = 1024;

    public static byte[] recoding(){
        voiceData = new byte[sample_length*2];
        try{
            linear = new AudioFormat(sample_length,16,2,true,false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class,linear);
            TargetDataLine targetdataline = (TargetDataLine)AudioSystem.getLine(info);
            targetdataline.open(linear);
            //収録開始
            targetdataline.start();
            AudioInputStream linearStream = new AudioInputStream(targetdataline);
            linearStream.read(voiceData, 0, voiceData.length);
            targetdataline.stop();
            targetdataline.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return voiceData;
    }
    public static void dispose(){


    }

}