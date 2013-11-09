package com.badlogic.gdx.audio.samples.processors;

import com.badlogic.gdx.audio.analysis.AudioTools;
import com.badlogic.gdx.audio.analysis.FFT;
import com.badlogic.gdx.audio.samples.AudioSample;
import com.badlogic.gdx.audio.samples.AudioSource;
import com.badlogic.gdx.audio.samples.BaseAudioProcessor;

public class FFTAudioProcessor extends BaseAudioProcessor {
	protected FFT fft;
	protected int requiredSize;
	protected int channels;
	
	public FFTAudioProcessor () {
		super();
	}

	public FFTAudioProcessor (AudioSource source) {
		super(source);
	}

	@Override
	public void init () {
		super.init();
		fft = new FFT(requiredSize=getSampleSize(), getRate());
		channels = getChannels();
		fft.logAverages(11, 1);
	}
	
	@Override
	public void deinit () {
		super.deinit();
		fft = null;
	}
	
	@Override
	protected AudioSample process (AudioSample sample) {
		if (sample.size >= requiredSize) {
			fft.forward(sample.buffer, sample.offset);
		}
		return sample;
	}
	
	public FFT getFFT() { return fft; }
}
