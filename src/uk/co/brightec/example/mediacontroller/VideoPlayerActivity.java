package uk.co.brightec.example.mediacontroller;

import java.io.IOException;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

public class VideoPlayerActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
        VideoControllerView.MediaPlayerControl, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private SurfaceView videoSurface;
    private MediaPlayer player;
    private VideoControllerView controller;
    private int mCurrBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        player = new MediaPlayer();
        controller = new VideoControllerView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(this, Uri.parse("http://erezmor.s3.amazonaws.com/tutorial_1.mp4"));
            player.setOnErrorListener(this);
            player.setOnPreparedListener(this);
            player.setOnBufferingUpdateListener(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        player.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.reset();
    }

    /**
     * SurfaceHolder.Callback
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        player.setDisplay(holder);
        player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    // End SurfaceHolder.Callback

    /**
     * MediaPlayer.OnPreparedListener
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        handleAspectRatio();
        player.start();
        findViewById(R.id.progress).setVisibility(View.GONE);
    }
    // End MediaPlayer.OnPreparedListener

    /**
     * MediaPlayer.OnErrorListener
     */
    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        Log.e(getPackageName(), String.format("Error(%s%s)", what, extra));

        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            player.reset();
        } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            player.reset();
        }
        player.setOnErrorListener(this);
        player.setOnPreparedListener(this);
        player.setOnBufferingUpdateListener(this);

        return true;
    }

    /**
     * Handle aspect ratio
     */
    private void handleAspectRatio() {
        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        android.view.ViewGroup.LayoutParams lp = videoSurface.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        videoSurface.setLayoutParams(lp);
    }

    /**
     * Buffering updates listening
     *
     * @param mediaPlayer
     * @param i
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        mCurrBuffer = i;
        Log.i("Buffer:", mCurrBuffer + "%");
    }

    /**
     * MediaPlayer.OnCompletionListener
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Toast.makeText(this, "Playback finished!", Toast.LENGTH_LONG).show();
    }

    /**
     * VideoMediaController.MediaPlayerControl
     *
     * @return
     */
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {

    }

    // End VideoMediaController.MediaPlayerControl

}
