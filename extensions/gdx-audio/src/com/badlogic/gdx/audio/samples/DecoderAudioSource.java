package com.badlogic.gdx.audio.samples;

import com.badlogic.gdx.audio.io.Decoder;

public class DecoderAudioSource implements AudioSource {
	protected boolean initialized;
	protected int sampleSize;
	protected AudioSample currentSample;
	protected AudioSample nextSample;
	protected boolean peeked;
	public final Decoder decoder;

	public DecoderAudioSource (final Decoder decoder, final int sampleSize) {
		this.decoder = decoder;
		this.sampleSize = sampleSize;
	}
	
	@Override
	public void init () {
		currentSample = new AudioSample();
		currentSample.buffer = new float[sampleSize * getChannels()];
		nextSample = new AudioSample();
		nextSample.buffer = new float[sampleSize * getChannels()];
		initialized = true;
	}

	@Override
	public void deinit () {
		setPosition(0f);
		currentSample = nextSample = null;
		initialized = false;
	}

	@Override
	public boolean isInitialized () {
		return initialized;
	}
	
	@Override
	public int getSampleSize () {
		return sampleSize;
	}


	@Override
	public boolean isSeekable () {
		return decoder.canSeek();
	}

	@Override
	public float getPosition () {
		return decoder.getPosition();
	}

	@Override
	public boolean setPosition (float newPosition) {
		return decoder.setPosition(newPosition);
	}

	@Override
	public float getLength () {
		return decoder.getLength();
	}

	@Override
	public boolean hasNext () {
		if (!peeked) {
			nextSample.size = decoder.readSamples(nextSample.buffer, 0, sampleSize);
			peeked = true;
		}
		return nextSample.size >= sampleSize;
	}

	@Override
	public AudioSample next () {
		if (!peeked && !hasNext())
			return null;
		final AudioSample temp = currentSample;
		currentSample = nextSample;
		nextSample = temp;
		peeked = false;
		return currentSample;
	}

	@Override
	public void remove () {}

	@Override
	public void dispose () {
		decoder.dispose();
	}

	@Override
	public int getRate () {
		return decoder.getRate();
	}

	@Override
	public int getChannels () {
		return decoder.getChannels();
	}
}
