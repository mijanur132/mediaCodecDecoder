package com.example.artiste.samplemediacodec;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Vector;

import static android.os.SystemClock.sleep;
import static java.lang.Boolean.TRUE;

public class SampleMediaCodec {
    private Context context;
    private Surface surface;

    public void play(Context c, Surface s) {
        Log.d("SampleMediaCodec", " beforePlay...");
        context = c;
        surface = s;
        new Thread(new Runnable() {
            public void run() {
                try {
                    //
                    long btime=0;
                    long atime=0;
                    System.out.println("downlaod..........................................>>>>");
                    String fPath="";
                    for (int a=0; a<1; a++)
                    {
                        for (int selectx = 1; selectx < 2; selectx++)
                        {
                            // vectorTime.add(0);
                            btime = System.currentTimeMillis();
                            System.out.println("Selectx..........................................>>>>" + selectx);
                            int chunknN = 10;
                            if (selectx < 6) {
                                chunknN = 10;
                            } else {
                                chunknN = 15;
                            }

                            //chunknN = 2;//for 1s exp, onno time e commented out
                            for (int chunkN = 1; chunkN < chunknN; chunkN++)
                            {
                                int m = 0;
                                if (selectx < 6) {
                                    m = 24;
                                } else {
                                    m = 2;
                                }
                                // m=50; //for 1s exp
                                for (int i = 1; i < m; i++) {
                                    //fPath = "/storage/emulated/0/divingT/30_diving_1min.avi_" + chunkN + "_" + i + ".avi";
                                    fPath = "/storage/emulated/0/rollerT/30_roller_1min.avi_" + chunkN + "_" + i + ".avi";
                                    //fPath = "/storage/emulated/0/1sChunk/30_30_roller.avi.avi"+"_" + i + "_0"+ ".avi.avi";
                                    if (selectx == 1) {
                                        fPath = "/storage/emulated/0/elephant/30_elephant.webm_" + chunkN + "_" + i + ".avi.avi";
                                    }
                                    if (selectx == 2) {
                                        fPath = "/storage/emulated/0/nyT/30_ny.mkv_" + chunkN + "_" + i + ".avi.avi";
                                    }
                                    if (selectx == 3) {
                                        fPath = "/storage/emulated/0/paris/30_paris.mkv_" + chunkN + "_" + i + ".avi.avi";
                                    }
                                    if (selectx == 4) {
                                        fPath = "/storage/emulated/0/rhinoT/45_30_rhino_1min.avi_" + chunkN + "_" + i + ".avi";
                                    }
                                    if (selectx == 5) {
                                        fPath = "/storage/emulated/0/rollerT/30_roller_1min.avi_" + chunkN + "_" + i + ".avi";
                                    }

                                    if (selectx == 6) {
                                        fPath = "/storage/emulated/0/diving/30_diving_original.mkv0_" + (chunkN + 10) + "_0_0" + ".avi";
                                    }
                                    if (selectx == 7) {
                                        fPath = "/storage/emulated/0/elephant/30_elephant.webm6_" + chunkN + "_10_20" + ".avi.avi";
                                    }
                                    if (selectx == 8) {
                                        fPath = "/storage/emulated/0/ny/30_ny.mkv6_" + chunkN + "_10_20" + ".avi";
                                    }
                                    if (selectx == 9) {
                                        fPath = "/storage/emulated/0/paris/30_paris.mkv6_" + chunkN + "_10_20" + ".avi.avi";
                                    }
                                    if (selectx == 10) {
                                        fPath = "/storage/emulated/0/rhino/30_rhino.webm0_" + chunkN + "_0_0" + ".avi";
                                    }
                                    if (selectx == 11) {
                                        //fPath = "/storage/emulated/0/roller/30_roller.mkv0_" + chunkN + "_0_0" + ".avi";
                                        fPath = "/storage/emulated/0/30_rhino.avi.avi";
                                    }

                                    System.out.println("path: " + fPath);
                                    playTask(fPath);
                                }
                            }
                        }
                    }
                    //
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void playTask(String fPath) throws IOException {
        long counterTime;
        long deltaTime;
        int frameCount;
        Vector<Long> timeVec=new Vector<>();

        /*
        Flow of video playback
        1.  MediaExtractor set source video resource (R.raw.xxx)
        2.  MediaExtractor get video type (In MediaFormat) and select first video track ("video/")
        3.  MediaCodec creates decoder with video type (MediaFormat.KEY_MINE)
        4.  Configure MediaCodec as "decoder" and start()
        5.  Looping if not End-Of-Stream
        6.     Request (De-queue) input buffer from MediaCodec by dequeueInputBuffer()
        7.     Read video data source (SampleData) by MediaExtractor.readSampleData() to input buffer
        8.     if has valid video data,send input buffer to MediaCodec for decode
        9.     otherwise. set BUFFER_FLAG_END_OF_STREAM to MediaCodec, and set eos
        10.    Request (De-queue) output buffer from MediaCodec by dequeueOutputBuffer()
        11.    If video frame is valid in output buffer, render it on surface by releaseOutputBuffer()
        12. End of loop
        13. Release MediaCodec, MediaExtractor
        */

        AssetFileDescriptor afd;
        //afd = context.getResources().openRawResourceFd(R.raw.clipcanvas_14348_h264_640x360);
        MediaExtractor mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(fPath);
        int numTracks = mediaExtractor.getTrackCount();
        String mine_type = null;
        MediaFormat format = null;
        for (int i = 0; i < numTracks; ++i) {
            format = mediaExtractor.getTrackFormat(i);
            mine_type = format.getString(MediaFormat.KEY_MIME);
            if (mine_type.startsWith("video/")) {
                // Must select the track we are going to get data by readSampleData()
                mediaExtractor.selectTrack(i);
                // Set required key for MediaCodec in decoder mode
                // Check http://developer.android.com/reference/android/media/MediaFormat.html
                format.setInteger(MediaFormat.KEY_CAPTURE_RATE, 24);
                format.setInteger(MediaFormat.KEY_PUSH_BLANK_BUFFERS_ON_STOP, 1);
                break;
            }
        }

        // TODO: Check if valid track has been selected by selectTrack()

        MediaCodec decoder = MediaCodec.createDecoderByType(mine_type);
        decoder.configure(format, surface, null, 0 /* 0:decoder 1:encoder */);
        decoder.start();

        // Count FPS
        counterTime = System.currentTimeMillis();
        frameCount = 0;

        int timeoutUs = 1000000; // 1 second timeout
        boolean eos = false;
        long playStartTime = System.currentTimeMillis();
        long frameDisplayTime = playStartTime;
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        for (;(!eos);) {
            int inputBufferIndex = decoder.dequeueInputBuffer(timeoutUs);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferIndex);
                int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);
                if (sampleSize > 0) {
                   // frameDisplayTime = (mediaExtractor.getSampleTime() >> 10) + playStartTime;
                    // Video data is valid,send input buffer to MediaCodec for decode
                    decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, mediaExtractor.getSampleTime(), 0);
                    mediaExtractor.advance();
                } else {
                    // End-Of-Stream (EOS)
                    decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    eos = true;
                }
            }

            int outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, timeoutUs);
            if (outputBufferIndex >= 0) {

                // Frame rate control
                //while(frameDisplayTime > System.currentTimeMillis()) {
                    //sleep(10);
               // }

                // outputBuffer is ready to be processed or rendered.
                decoder.releaseOutputBuffer(outputBufferIndex, true /*true:render to surface*/);

                // Count FPS
                if (frameCount==0)
                {
                    deltaTime = System.currentTimeMillis() - counterTime;
                    Log.v("sampleMediaCodec", deltaTime+ " frameCount:"+frameCount);
                    timeVec.add(deltaTime);
                    counterTime = System.currentTimeMillis();
                    frameCount = 0;
                }
                frameCount++;

                deltaTime = System.currentTimeMillis() - counterTime;
                if (deltaTime > 10000 || frameCount>9) {
                    //Log.v("SampleMediaCodec", (((float)frameCount / (float)deltaTime) * 1000) + " fps");
                    Log.v("sampleMediaCodec", deltaTime+ " frameCount:"+frameCount);
                    timeVec.add(deltaTime);
                    counterTime = System.currentTimeMillis();
                    frameCount = 1;
                }
            }
        }

        Iterator it= timeVec.iterator();

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            try {
                FileOutputStream fOut=
                        new FileOutputStream(
                                new File(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_DOWNLOADS), "PrllTileRolGPU.txt"), TRUE
                        );


                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append("new user:"+"\n");
                while(it.hasNext())
                    myOutWriter.append(it.next().toString()+"\n");

                myOutWriter.close();
                fOut.close();
                Log.v("MyApp","File has been written");
                System.out.println("File Has Been Written...................................................................");
                //System.exit(1);
            } catch(Exception ex) {
                ex.printStackTrace();
                Log.v("MyApp","File didn't write");
                System.out.println("File Has Not Been Written...................................................................");
            }
        }


        decoder.stop();
        decoder.release();
        mediaExtractor.release();
    }
}
