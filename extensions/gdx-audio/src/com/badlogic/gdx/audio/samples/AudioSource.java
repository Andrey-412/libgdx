package com.badlogic.gdx.audio.samples;

public interface AudioSource extends SampleSource<AudioSample> {
	/** @return The sample rate this source provides. */
	int getRate();
	/** @return The number of audio channels this source provides. */
	int getChannels();
	/** @return The expected size of each sample. Note that the actual sample size might be different (i.e. at the end). */
	int getSampleSize();
}
