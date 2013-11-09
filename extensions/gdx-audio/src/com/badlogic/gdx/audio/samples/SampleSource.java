package com.badlogic.gdx.audio.samples;

import java.util.Iterator;
import com.badlogic.gdx.utils.Disposable;

public interface SampleSource<T> extends Iterator<T>, Disposable {
	/** Initialize the source and opens all required resources */
	void init();
	/** De-initializes the source and closes all open resources */
	void deinit();
	/** @return true if the source is initialized */
	boolean isInitialized();
	/** @return true if this source is seekable and the {@link #setPosition(float)} method can be used. */
	boolean isSeekable();
	/** @return the current position in the source, where 0 <= position < {@link #getLength()} */ 
	float getPosition();
	/** @return true if succesful, false otherwise. */
	boolean setPosition(float newPosition);
	/** @return the length of the source, which is the maximum value for {{@link #getPosition()} and {@link #setPosition(float)} */
	float getLength();
}
