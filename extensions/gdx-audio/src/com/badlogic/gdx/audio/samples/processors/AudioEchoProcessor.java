package com.badlogic.gdx.audio.samples.processors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.CircularBuffer;
import com.badlogic.gdx.audio.CircularFloatBuffer;
import com.badlogic.gdx.audio.samples.AudioSample;
import com.badlogic.gdx.audio.samples.AudioSource;
import com.badlogic.gdx.audio.samples.BaseAudioProcessor;
import com.badlogic.gdx.math.MathUtils;

public class AudioEchoProcessor extends BaseAudioProcessor {
	protected CircularFloatBuffer buffer;
	protected float echo[];
	protected int channels;
	protected int dist;
	// The maximum echo delay in seconds
	protected float maxDelay = 5f;
	// The echo delay in seconds
	protected float delay = 0.5f;
	// The amplitude of the echo [0,1]
	protected float amplitude = 0.2f;
	
	public AudioEchoProcessor () {
		super();
	}

	public AudioEchoProcessor (AudioSource source) {
		super(source);
	}
	
	public void setMaxEchoDelay(final float delay) {
		maxDelay = delay;
		setEchoDelay(this.delay);
	}
	
	public float getMaxEchoDelay() {
		return delay;
	}
	
	public void setEchoDelay(final float delay) {
		this.delay = (delay > maxDelay) ? maxDelay : delay;
		final int size = getSampleSize() * channels;
		dist = (int)((float)getRate() * maxDelay) * channels;
		if (dist < size)
			dist = size;
	}
	
	public float getEchoDelay() {
		return delay;
	}
	
	public void setEchoAmplitude(final float amplitude) {
		this.amplitude = amplitude;
	}
	
	public float getEchoAmplitude() {
		return amplitude;
	}

	@Override
	protected AudioSample process (final AudioSample sample) {
		if (sample.size * channels == echo.length) {
			int avail = buffer.getAvailable();
			if (delay < 0.01 || amplitude < 0.01f || avail < dist) 
				buffer.write(sample.buffer, sample.offset, sample.size * channels);
			else {
				if (avail > dist)
					avail -= buffer.skip(avail-dist);
				if (avail == dist) {
					final float da = 1f - amplitude;
					buffer.read(echo, 0, echo.length);
					for (int i = 0; i < echo.length; i++) {
						final float s1 = da * sample.buffer[i];
						final float s2 = amplitude * echo[i]; 
						float dest = MathUtils.clamp(s1 + s2, -1f, 1f);
						sample.buffer[i] = dest;
					}
				}
				buffer.write(sample.buffer, sample.offset, sample.size * channels);
			}
		}
		return sample;
	}

	@Override
	public void init () {
		super.init();
		channels = getChannels();
		final int size = getSampleSize() * channels;
		int maxDist = (int)((float)getRate() * maxDelay) * channels;
		if (maxDist < size)
			maxDist = size;
		if (buffer == null || buffer.size != maxDist)
		buffer = new CircularFloatBuffer(maxDist);
		echo = new float[size];
	}
}
