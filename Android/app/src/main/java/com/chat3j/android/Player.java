package com.chat3j.android;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;

public class Player {
    private int minBuffSize;
    AudioTrack track;

    public Player(int sampleSize) {
        this.minBuffSize = AudioTrack.getMinBufferSize(sampleSize,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        track = new AudioTrack.Builder().setAudioAttributes(new AudioAttributes.Builder().
                setUsage(AudioAttributes.USAGE_MEDIA).
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()).
                setAudioFormat(new AudioFormat.Builder().
                        setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(sampleSize).
                        setChannelMask(AudioFormat.CHANNEL_OUT_STEREO).build()).
                setBufferSizeInBytes(minBuffSize).build();

        start();
    }

    public void writeSamples(byte[] bytes, int i, int i1) {
        if (track.getPlayState() == AudioTrack.PLAYSTATE_PAUSED || track.getPlayState() == AudioTrack.PLAYSTATE_STOPPED)
            start();
        track.write(bytes, i, i1);
    }

    public void start() {
        track.play();
    }

    public void stop() {
        track.stop();
    }

    public void dispose() {
        track.release();
    }
}
