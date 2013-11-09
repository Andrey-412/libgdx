package com.badlogic.gdx.audio.transform;

import com.badlogic.gdx.audio.samples.AudioSample;
import com.badlogic.gdx.audio.samples.AudioSource;
import com.badlogic.gdx.audio.samples.BaseAudioProcessor;

public class SoundTouchProcessor extends BaseAudioProcessor {
	protected SoundTouch soundTouch;

	public SoundTouchProcessor () {
		soundTouch = new SoundTouch();
	}
	
	public SoundTouchProcessor (AudioSource source) {
		super(source);
		soundTouch = new SoundTouch();
	}

	@Override
	protected AudioSample process (AudioSample sample) {
		//soundTouch.putSamples(sample.buffer, sample.offset, sample.size);
		//sample.size = soundTouch.receiveSamples(sample.buffer, sample.offset, sample.size);
		return sample;
	}

	@Override
	public void init () {
		super.init();
		soundTouch.setChannels(source.getChannels());
		soundTouch.setSampleRate(source.getRate());
		soundTouch.setPitch(1f);
		soundTouch.setRate(1f);
		soundTouch.setTempo(1f);
	}
	
	@Override
	public void deinit () {
		super.deinit();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		soundTouch.dispose();
		soundTouch = null;
	}
}
