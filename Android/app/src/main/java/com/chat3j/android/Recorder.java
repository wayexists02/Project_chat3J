package com.chat3j.android;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

public class Recorder {
    private AudioRecord record;

    public Recorder(int sampleSize) {
        // 음성 대화 MediaRecorder.AudioSource.VOICE_COMMUNICATION
        // MIC로 쓰는 것에서 echo cancellation or automatic gain control 기능 제공
        // 기본 오디오 소스 MediaRecorder.AudioSource.DEFAULT
        record = new AudioRecord.Builder().
                setAudioSource(MediaRecorder.AudioSource.DEFAULT).
                setAudioFormat(new AudioFormat.Builder().
                        setEncoding(AudioFormat.ENCODING_PCM_16BIT).
                        setSampleRate(sampleSize).
                        setChannelMask(AudioFormat.CHANNEL_IN_STEREO).build()).
                setBufferSizeInBytes(AudioTrack.getMinBufferSize(sampleSize, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)).build();

        start();
    }

    public int read(byte[] bytes, int i, int i1) {
        return record.read(bytes, i, i1);
    }

    public void start() {
        record.startRecording();
    }

    public void stop() {
        record.stop();
    }

    public void dispose() {
        record.release();
    }
}
