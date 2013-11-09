package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.Player;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Player.PlayerListener;
import com.badlogic.gdx.audio.analysis.FFT;
import com.badlogic.gdx.audio.io.Decoder;
import com.badlogic.gdx.audio.io.Mpg123Decoder;
import com.badlogic.gdx.audio.io.VorbisDecoder;
import com.badlogic.gdx.audio.samples.AudioProcessor;
import com.badlogic.gdx.audio.samples.AudioSample;
import com.badlogic.gdx.audio.samples.AudioSource;
import com.badlogic.gdx.audio.samples.BasicAudioPlayer;
import com.badlogic.gdx.audio.samples.DecoderAudioSource;
import com.badlogic.gdx.audio.samples.processors.AudioEchoProcessor;
import com.badlogic.gdx.audio.samples.processors.FFTAudioProcessor;
import com.badlogic.gdx.audio.transform.SoundTouch;
import com.badlogic.gdx.audio.transform.SoundTouchProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.StringBuilder;

public class AudioFXTest extends GdxTest {

	@Override
	public boolean needsGL20 () {
		return false;
	}

	boolean positionSliding;
	BasicAudioPlayer player;
	FFTAudioProcessor fftProcessor;
	AudioEchoProcessor echo;
	Stage stage;
	Slider positionSlider;
	Slider volumeSlider;
	Slider echoDelaySlider;
	Slider echoVolumeSlider;
	Label timeLabel;
	StringBuilder sb = new StringBuilder();
	String totalTime = "00:00.00";
	ShapeRenderer shapeRenderer;

	private final static String filename = "iron.mp3";//"cloudconnected.ogg";//"engine-2.ogg"; //"remove_me.ogg";
	
	@Override
	public void create () {
		Gdx.files.internal("data/"+filename).copyTo(Gdx.files.external(filename));

		Decoder decoder;
		if (filename.endsWith(".mp3"))
			decoder = new Mpg123Decoder(Gdx.files.external(filename));
		else
			decoder = new VorbisDecoder(Gdx.files.external(filename));
		AudioSource source = new DecoderAudioSource(decoder, 1024);
		echo = new AudioEchoProcessor(source); 
		//AudioProcessor soundTouch = new SoundTouchProcessor(source);
		fftProcessor = new FFTAudioProcessor(echo);
		player = new BasicAudioPlayer(fftProcessor);
		
		echo.setMaxEchoDelay(3f);
		echo.setEchoAmplitude(0f);
		echo.setEchoDelay(0f);
		
		shapeRenderer = new ShapeRenderer();
		
		stage = new Stage(400, 240, true);
		Gdx.input.setInputProcessor(stage);
		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		
		Table root = new Table(skin);
		
		Button btn;
		Table table = new Table(skin);
		table.add(new Label("Main volume: ", skin));
		table.add(volumeSlider = new Slider(0f, 1f, 0.01f, false, skin));
		volumeSlider.setValue(1f);
		volumeSlider.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				player.setVolume(volumeSlider.getValue());
			}
		});
		table.row();
		table.add(new Label("Echo delay: ", skin));
		table.add(echoDelaySlider = new Slider(0f, 2.5f, 0.01f, false, skin));
		echoDelaySlider.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				echo.setEchoDelay(echoDelaySlider.getValue());
			}
		});
		table.row();
		table.add(new Label("Echo volume: ", skin));
		table.add(echoVolumeSlider = new Slider(0f, 0.5f, 0.01f, false, skin));
		echoVolumeSlider.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				echo.setEchoAmplitude(echoVolumeSlider.getValue());
			}
		});
		table.pack();
		root.add(table);
		root.row();
		
		table = new Table(skin);
		table.add(btn = new TextButton("Play", skin));
		btn.addListener(new ClickListener() {
			@Override public void clicked (InputEvent event, float x, float y) {
				doplay();
			}
		});
		table.add(btn = new TextButton("Stop", skin));
		btn.addListener(new ClickListener() {
			@Override public void clicked (InputEvent event, float x, float y) {
				dostop();
			}
		});
		table.add(btn = new TextButton("Pause", skin));
		btn.addListener(new ClickListener() {
			@Override public void clicked (InputEvent event, float x, float y) {
				dopause();
			}
		});
		table.add(positionSlider = new Slider(0f, 1f, 1f, false, skin));
		positionSlider.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (!positionSliding)
					player.setPosition(positionSlider.getValue());
			}
		});
		table.add(timeLabel = new Label("00:00.00/00:00.00", skin));
		table.pack();
		root.add(table);
		root.pack();
		stage.addActor(root);
	}

	@Override
	public void resize (int width, int height) {
		super.resize(width, height);
	}

	Color col1 = new Color(0f, 1f, 0f, 1f);
	Color col2 = new Color();
	@Override
	public void render () { 
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		float pos = player.getPosition();
		positionSliding = true;
		positionSlider.setValue(pos);
		positionSliding = false;
		timeLabel.setText(time2str(pos, sb).append('/').append(totalTime));
			
		FFT fft = fftProcessor.getFFT();
		if (fft != null) {
			int numAvg = fft.avgSize();
			if (numAvg > 0) {
				final float height = stage.getHeight();
				final float dwidth = stage.getWidth() / numAvg;
				shapeRenderer.setColor(0.4f, 0.4f, 0.4f, 1f);
				shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
				shapeRenderer.begin(ShapeType.Filled);
				float x2, x1 = 0;
				float y2, y1 = fft.getAvg(0) * height;
				for (int i = 1; i < numAvg; i++) {
					final float avg = fft.getAvg(i) * 0.01f; 
					x2 = x1 + dwidth;
					y2 = avg * height;
					col2.set(col1).lerp(Color.RED, avg);
					shapeRenderer.rect(x1, 0, x2-x1, y2, col1, col1, col2, col2);
					//shapeRenderer.line(x1, y1, x2, y2);
					x1 = x2;
					y1 = y2;
				}
				shapeRenderer.end();
			}
		}
		
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
	}

	@Override
	public void dispose () {
		player.dispose();
		
		Gdx.files.external(filename).delete();
	}
	
	public void doplay() {
		float len = player.getLength();
		positionSlider.setRange(0, len);
		totalTime = time2str(len, sb).toString();
		player.loop(3);
	}
	
	public void dostop() {
		player.stop();
	}
	
	public void dopause() {
		player.pause();
	}
	
	public StringBuilder time2str(final float time, StringBuilder sb) {
		final int secs = (int)time;
		final int min = secs / 60;
		final int sec = secs % 60;
		final int ms = (int)((time - secs) * 100f);
		
		sb.setLength(0);
		
		if (min < 10)
			sb.append('0');
		sb.append(min);
		sb.append(":");
		if (sec < 10)
			sb.append('0');
		sb.append(sec);
		sb.append(".");
		if (ms < 10)
			sb.append('0');
		sb.append(ms);
		return sb;
	}
}