package com.badlogic.gdx.audio.samples;

import com.badlogic.gdx.Gdx;

public abstract class BaseSamplePlayer<T, U extends SampleSource<T>> extends BaseSampleProcessor<T, U> implements SamplePlayer<T, U> {
	protected int status;
	protected boolean started;
	protected PlayThread thread;
	protected boolean startPaused;
		
	public BaseSamplePlayer () {
		super();
	}

	public BaseSamplePlayer (U source) {
		super(source);
	}
	
	protected void onStart() {
		Gdx.app.log("BaseSamplePlayer", "onStart");
	}
	protected void onStop() {
		Gdx.app.log("BaseSamplePlayer", "onStop");
	}
	protected void onPause() {
		Gdx.app.log("BaseSamplePlayer", "onPause");
	}
	protected void onResume() {
		Gdx.app.log("BaseSamplePlayer", "onResume");
	}
	protected boolean onEnd(int loopsRemaing) {
		Gdx.app.log("BaseSamplePlayer", "onEnd");
		return loopsRemaing > 0; 
	}

	@Override
	public boolean step () {
		return next() != null && hasNext();
	}

	@Override
	public void play () {
		loop(1);
	}

	@Override
	public void loop () {
		loop(-1);
	}

	@Override
	public void loop (int count) {
		if (!started)
			start(false);
		else
			setStatus(PLAYING);
	}

	@Override
	public void pause () {
		if (!started)
			start(true);
		else
			setStatus(PAUSED);
	}

	@Override
	public void stop () {
		stop(false);
	}
	
	public void stop(boolean wait) {
		if (wait && thread != null && thread.isAlive()) {
			try {
				setStatus(STOPPED);
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		setStatus(STOPPED);
	}

	@Override
	public synchronized int getStatus () {
		return status;
	}
	
	protected synchronized void setStatus(int status) {
		this.status = status; 
	}
	
	@Override
	public void dispose () {
		stop(true);
		super.dispose();
	}
	
	protected void start(boolean paused) {
		if (started || getStatus() == STARTING)
			return;
		if (thread == null)
			thread = new PlayThread();
		status = STARTING;
		startPaused = paused;
		thread.start();
	}
	
	protected class PlayThread extends Thread {
		private boolean paused;
		
		@Override
		public void run () {
			started = true;
			paused = false;
		
			try {
				onStart();
				
				if (!isInitialized())
					init();
				
				setStatus(startPaused ? PAUSED : PLAYING);
				
				int status;
				while((status = getStatus()) != STOPPED) {
					if (status == PAUSED) {
						if (!paused) {
							paused = true;
							onPause();
						}
						try {
							sleep(100);
						} catch (InterruptedException e) {
						}
						status = getStatus();
					}
					
					if (status == PLAYING) {
						if (paused) {
							paused = false;
							onResume();
						}
						
						if (!step()) {
							if (onEnd(0) && isSeekable())
								setPosition(0f);
							else
								setStatus(STOPPED);
						}
					}
				}
			} finally {
				deinit();
				onStop();
				thread = null;
				started = false;
			}
		}
	}
}
