package com.badlogic.gdx.audio.samples;


public interface SampleProcessor<T, U extends SampleSource<T>> extends SampleSource<T> {
	/** Set the source for this processor */
	void setSource(U source);
	/** @return The source this processor uses to get its samples from */
	U getSource();
	/** Enables this specific processor, meaning it will process samples */
	void enable();
	/** Disable this specific processor, meaning it will simple pass thru the samples from its source */
	void disable();
	/** @return True if this processor is enabled, false otherwise */
	boolean isEnabled();
}
