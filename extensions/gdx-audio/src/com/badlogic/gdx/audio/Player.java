package com.badlogic.gdx.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.io.Decoder;
import com.badlogic.gdx.audio.samples.AudioSample;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** @author Xoppa */
public class Player {
	public interface PlayerListener {
		/** Occurs when the player starts, e.g. when play() or pause() is called for the first time or after stop(). */ 
		void onStart(boolean paused, int channels, int rate, int maxSampleSize);
		/** Occurs when the players stops, e.g. when the decoder reaches the end of file and doesn't loop */
		void onStop();
		/** Occurs when the player pauses */
		void onPause();
		/** Occurs when the player resumes from pause */
		void onResume();
		/** Occurs whenever the player needs to change the position without actual processing samples.
		 * @param position The new position (in seconds)
		 * @return True to allow the change in position or false to not allow the change in position */
		boolean onSeek(float position);
		/** Occurs whenever the end of the decoder is reached. Note that returning true doesn't guarantee the
		 * loop actual accurs. Also, onPlay isn't called on loop.
		 * @return True to loop if possible, false to stop */
		boolean onEnd(long loopsRemaining, long totalLoops);
		/** Occurs whenever the player loops (start immediately at the beginning after reaching the end) */
		void onLoop(long currentLoop);
		/** Get called before playing a sample, feel free to fully modify the sample as you like. 
		 * Set the size to zero to not further process the sample. */
		void onSample(AudioSample sample); 
	}
	
	/* public final static int STOPPED = 0;
	public final static int PAUSED = 1;
	public final static int PLAYING = 2;
	public final static int STARTING = 3;
	
	protected PlayThread thread;
	protected Decoder decoder;
	protected boolean manageDecoder;
	protected PlayerListener listener;
	protected boolean initialized;
	protected int sampleSize;
	
	public Player(final Decoder decoder, final boolean manageDecoder, final PlayerListener listener, final int sampleSize) {
		setDecoder(decoder, manageDecoder);
		setListener(listener);
		setSampleSize(sampleSize);
	}
	
	public void setDecoder(final Decoder decoder, final boolean managed) {
		if (initialized)
			stop(true);
		if (manageDecoder)
			this.decoder.dispose();
		this.decoder = decoder;
		manageDecoder = decoder == null ? false : managed;
	}
	
	public void setListener(final PlayerListener listener) {
		if (initialized)
			stop(true);
		this.listener = listener;
	}
	
	public void setSampleSize(final int size) {
		if (initialized)
			stop(true);
		this.sampleSize = size;		
	}
	
	protected boolean init() {
		if (thread != null)
			return true;
		if (decoder == null)
			return false;
		thread = new PlayThread(decoder, listener, sampleSize);
		return initialized = true;
	}
	
	public void dispose() {
		if (thread != null)
			stop(true);
		setDecoder(null, false);
	}
	
	public void stop() {
		stop(true);
	}
	
	protected void stop(boolean wait) {
		if (thread != null) {
			thread.stop(wait);
			thread = null;
			initialized = false;
		}
	}
	
	public void loop() {
		loop(-1);
	}
	
	public void loop(int count) {
		if (count != 0 && (initialized || init()))
			thread.loop(count);		
	}
	
	public void play() {
		if (initialized || init())
			thread.play();
	}
	
	public void pause() {
		if (initialized || init())
			thread.pause();
	}
	
	protected class PlayThread extends Thread {
		protected int status;
		private AudioDevice device;
		private final short buffer[];
		public final Decoder decoder;
		public final AudioSample sample = new AudioSample();
		public final int channels;
		public final int rate;
		public final int sampleSize;
		public final boolean seekable;
		public final PlayerListener listener;
		private boolean started;
		private boolean paused;
		private boolean startPaused;
		private long loopCount = 1;
		private long currentLoop;
		
		public PlayThread(final Decoder decoder, final PlayerListener listener) {
			this(decoder, listener, 1024);
		}
		
		public PlayThread(final Decoder decoder, final PlayerListener listener, final int sampleSize) {
			this.decoder = decoder;
			this.listener = listener;
			this.channels = decoder.getChannels();
			this.seekable = decoder.canSeek();
			this.rate = decoder.getRate();
			if (channels > 2)
				throw new GdxRuntimeException("Only mono and stereo is supported at this moment");
			this.sampleSize = sampleSize;
			this.buffer = new short[channels*sampleSize];
		}
		
		public void play() {
			if (!started)
				start(false);
			else
				status = PLAYING;
		}
		
		public void loop(int count) {
			if (!started) {
				loopCount = count;
				start();
			} else if (loopCount >= 0) {
				loopCount = count < 0 ? count : (count == 0 ? 0 : loopCount + count);
				play();
			}
		}
		
		public void pause() {
			if (!started)
				start(true);
			else
				status = PAUSED;
		}
		
		public void stop(boolean wait) {
			status = STOPPED;
			if (wait && isAlive()) {
				try {
					join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void start(boolean paused) {
			if (started || status == STARTING)
				return;
			status = STARTING;
			startPaused = paused;
			start();
		}
		
		@Override
		public void run () {
			started = true;
			paused = false;
			currentLoop = 1;
			status = startPaused ? PAUSED : PLAYING;
			
			this.device = Gdx.audio.newAudioDevice(rate, channels == 1 ? true : false);
			
			if (listener != null)
				listener.onStart(startPaused, channels, rate, sampleSize);
			
			try {
				boolean end = false;
				while (status != STOPPED) {
					
					if (status == PAUSED) {
						if (!paused) {
							paused = true;
							if (listener != null)
								listener.onPause();
						}
						
						try {
							sleep(100);
						} catch (InterruptedException e) { }
					}
					
					if (status == PLAYING) {
						if (paused) {
							paused = false;
							if (listener != null)
								listener.onResume();
						}
						
						sample.size = decoder.readSamples(buffer, 0, sampleSize);
						sample.offset = 0;
						sample.buffer = buffer;
						
						if (sample.size < sampleSize)
							end = true;
						if (sample.size > 0) {
							if (listener != null)
								listener.onSample(sample);
							
							if (sample.size > 0)
								device.writeSamples(sample.buffer, sample.offset, sample.size);
						}
						
						if (end) {
							end = false;
							final long remain = loopCount < 0 ? Long.MAX_VALUE : (loopCount <= currentLoop ? 0 : loopCount - currentLoop);
							if ((listener != null ? listener.onEnd(remain, loopCount) : (remain>0))
									&& seekable) {
								setPosition(0f);
								
								if (currentLoop >= Long.MAX_VALUE)
									currentLoop = 0;
								else
									currentLoop++;
								
								if (listener != null)
									listener.onLoop(currentLoop);
							} else
								status = STOPPED;
						}
					}
				}
			} finally {
				initialized = false;
				thread = null;
				setPosition(0f);
				device.dispose();
				started = false;
				if (listener!=null)
					listener.onStop();
			}
		}
		
		public void setPosition(float pos) {
			if (seekable && (listener == null || listener.onSeek(pos)))
				decoder.setPosition(pos);
		}
	} */
}
