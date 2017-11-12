package com.fyp.mrisecondscreen;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.IOException;
import java.util.Random;

public class AudioRecorder {

    final MediaRecorder recorder = new MediaRecorder();
    final String relativePath;
    String AudioSavePathInDevice = null;

    private static Random random;
    private static String name = "sampleclip";

    /**
     * Creates a new audio recording at the given path (relative to root of SD card).
     */
    public AudioRecorder() {
        relativePath =
                CreateRandomAudioFileName(5) + "AudioRecording.m4a";

        AudioSavePathInDevice =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + relativePath;

        MediaRecorderReady();
    }

    public void MediaRecorderReady(){


        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioEncodingBitRate(16*44100);
        recorder.setAudioSamplingRate(44100);
        recorder.getMaxAmplitude();
        recorder.setOutputFile(AudioSavePathInDevice);
    }


    /**
     * Starts a new recording.
     */
    public void start() throws IOException {

        recorder.prepare();
        recorder.start();
    }

    /**
     * Stops a recording that has been previously started.
     */
    public void stop() throws IOException {
        recorder.stop();
        recorder.release();
    }

    //Generate random audio clip name
    public static String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        random = new Random();
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(name.
                    charAt(random.nextInt(name.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }

    public String getPath() {
        return this.AudioSavePathInDevice;
    }

}
