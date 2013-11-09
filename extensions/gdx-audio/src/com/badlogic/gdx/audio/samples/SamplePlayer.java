package com.badlogic.gdx.audio.samples;

public interface SamplePlayer<T, U extends SampleSource<T>> extends SampleProcessor<T, U> {
	public final static int STOPPED			= 0;
	public final static int STARTING		 	= 1;
	public final static int PLAYING 			= 2;
	public final static int PAUSED 			= 3;
	
	boolean step();
	void play();
	void loop();
	void loop(int count);
	void pause();
	void stop();
	int getStatus();
}
