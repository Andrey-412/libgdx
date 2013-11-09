package com.badlogic.gdx.audio.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;

public class BasicAudioPlayer extends BaseAudioPlayer {
	protected AudioDevice device;
	protected float volume = 1f;

	public BasicAudioPlayer () {
		super();
	}

	public BasicAudioPlayer (AudioSource source) {
		super(source);
	}
	
	@Override
	public boolean isInitialized () {
		return device != null && super.isInitialized();
	}
	
	@Override
	public void init () {
		super.init();
		if (device == null) {
			device = Gdx.audio.newAudioDevice(getRate(), getChannels() == 1 ? true : false);
			device.setVolume(volume);
		}
	}
	
	@Override
	public void deinit () {
		super.deinit();
		if (device != null) {
			device.dispose();
			device = null;
		}
	}
	
	@Override
	protected AudioSample process (AudioSample sample) {
		if (sample != null && device != null)
			device.writeSamples(sample.buffer, sample.offset, sample.size);
		else
			Gdx.app.log("BasicAudioPlayer", "No sample or device");
		return sample;
	}
	
	public float getVolume() {
		return volume;
	}
	
	public void setVolume(float volume) {
		this.volume = volume;
		if (device != null)
			device.setVolume(volume);
	}
}
