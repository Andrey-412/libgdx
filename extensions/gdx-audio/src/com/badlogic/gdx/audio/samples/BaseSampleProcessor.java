package com.badlogic.gdx.audio.samples;

public abstract class BaseSampleProcessor<T, U extends SampleSource<T>> implements SampleProcessor<T, U> {
	protected U source;
	protected boolean initialized;
	protected boolean enabled = true;
	
	protected abstract T process(T sample);
	
	public BaseSampleProcessor() { }
	
	public BaseSampleProcessor(final U source) {
		setSource(source);
	}
	
	@Override
	public boolean isSeekable () {
		return source == null ? false : source.isSeekable();
	}

	@Override
	public float getPosition () {
		return source == null ? 0f : source.getPosition();
	}

	@Override
	public boolean setPosition (float newPosition) {
		return source == null ? false : source.setPosition(newPosition);
	}

	@Override
	public float getLength () {
		return source == null ? 0f : source.getLength();
	}

	@Override
	public boolean hasNext () {
		return source == null ? false : source.hasNext();
	}

	@Override
	public T next () {
		if (source == null)
			return null;
		if (!enabled)
			return source.next();
		return process(source.next());
	}

	@Override
	public void remove () {
		if (source != null)
			source.remove();
	}

	@Override
	public void setSource (final U source) {
		this.source = source;
	}

	@Override
	public U getSource () {
		return source;
	}

	@Override
	public void init () {
		if (source != null)
			source.init();
		initialized = true;
	}

	@Override
	public void deinit () {
		if (source != null)
			source.deinit();
		initialized = false;
	}

	@Override
	public boolean isInitialized () {
		return initialized && (source == null ? false : source.isInitialized()); 
	}
	
	@Override
	public void enable () {
		enabled = true;
	}

	@Override
	public void disable () {
		enabled = false;
	}

	@Override
	public boolean isEnabled () {
		return enabled;
	}

	@Override
	public void dispose () {
		deinit();
		if (source != null)
			source.dispose();
	}
}
