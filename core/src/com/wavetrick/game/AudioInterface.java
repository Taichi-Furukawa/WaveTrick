package com.wavetrick.game;
import java.io.*;
import java.awt.*;
import java.net.URL;
import javax.sound.sampled.*;

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
            linear = new AudioFormat(sample_length,16,1,true,false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class,linear);
            TargetDataLine targetdataline = (TargetDataLine)AudioSystem.getLine(info);
            targetdataline.open(linear);
            //収録開始
            targetdataline.start();
            AudioInputStream linearStream = new AudioInputStream(targetdataline);

            linearStream.read(voiceData, 0, voiceData.length);
            targetdataline.start();
            targetdataline.close();
            /*
            File file = new File("rawdata.txt");
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            for (int i=0;i<voiceData.length;i++){
                pw.println(voiceData[i]);
            }
            pw.close();
            */
            /*
            File audioFIle = new File("voice.au");
            ByteArrayInputStream baiStream = new ByteArrayInputStream(voiceData);
            AudioInputStream aiStream = new AudioInputStream(baiStream,linear,voiceData.length);
            AudioSystem.write(aiStream,AudioFileFormat.Type.AU,audioFIle);
            aiStream.close();
            baiStream.close();
            System.out.print("File dekita");
            */

        }catch (Exception e){
            e.printStackTrace();
        }
        return voiceData;
    }

}

 // 消しゴム貸して *_*