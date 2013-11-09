package com.badlogic.gdx.audio.samples;

public abstract class BaseAudioProcessor extends BaseSampleProcessor<AudioSample, AudioSource> implements AudioProcessor {
	public BaseAudioProcessor () {
		super();
	}

	public BaseAudioProcessor (final AudioSource source) {
		super(source);
	}

	@Override
	public int getRate () {
		return source == null ? 0 : source.getRate();
	}

	@Override
	public int getChannels () {
		return source == null ? 0 : source.getChannels();
	}

	@Override
	public int getSampleSize () {
		return source == null ? 0 : source.getSampleSize();
	}
}
