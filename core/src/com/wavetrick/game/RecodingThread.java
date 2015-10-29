package com.wavetrick.game;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

import static java.lang.Math.*;

/**
 * Created by furukawa on 15/09/25.
 */
public class RecodingThread extends Thread {
    public static AudioInterface mic_recorder;
    public static FFT4g fft;
    public RecodingThread() {
         mic_recorder = new AudioInterface();
    }
    public static int creation = 0;

    public void run() {
        byte[] audios;
        creation = 0;
        int max = -100;
        int index = 0;
        int cnt = 0;
     while (true){
         audios = mic_recorder.recoding();
         double[] doubledata = new double[audios.length];
         for (int i =0;i<audios.length;i++){
             doubledata[i] = (double)audios[i];
         }
         fft = new FFT4g(doubledata.length);
         fft.rdft(1, doubledata);
         Integer[] stageData = new Integer[doubledata.length];
         max = -100;
         index = 0;
         cnt = 0;
         for(int i=0;i<doubledata.length-2;i+=2){
             stageData[cnt] = (int)sqrt((pow(doubledata[i], 2) + pow(doubledata[i + 1], 2)));
             if(stageData[cnt]>max){
                 max = stageData[cnt];
                 index = cnt;
             }
             cnt++;
         }
         creation = index;
         //System.out.println("max"+max+"index"+index);

     }
    }
}
