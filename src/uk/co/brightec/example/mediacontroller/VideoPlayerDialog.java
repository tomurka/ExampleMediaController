package uk.co.brightec.example.mediacontroller;

import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

public class VideoPlayerDialog extends Dialog implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
        VideoControllerView.MediaPlayerControl, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private SurfaceView mVideoSurface;
    private MediaPlayer mMediaPlayer;
    private VideoControllerView mVideoControllerView;
    private int mCurrBuffer;
    private Uri mMeduaUri;

    private Activity mHost;

    public VideoPlayerDialog(Context context, Activity host, Uri mediaUri) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        mHost = host;
        mMeduaUri = mediaUri;

        setContentView(R.layout.video_player_dialog);

        mVideoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = mVideoSurface.getHolder();
        videoHolder.addCallback(this);

        mMediaPlayer = new MediaPlayer();
        mVideoControllerView = new VideoControllerView(getContext());
    }

    @Override
    public void show() {
        super.show();
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(getContext(), mMeduaUri);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
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
        mVideoControllerView.show();
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mMediaPlayer.reset();
    }

    /**
     * SurfaceHolder.Callback
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
        mMediaPlayer.prepareAsync();
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
        mVideoControllerView.setMediaPlayer(this);
        mVideoControllerView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        handleAspectRatio();
        mMediaPlayer.start();
        findViewById(R.id.progress).setVisibility(View.GONE);
    }
    // End MediaPlayer.OnPreparedListener

    /**
     * MediaPlayer.OnErrorListener
     */
    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        Log.e(getContext().getPackageName(), String.format("Error(%s%s)", what, extra));

        if (extra == MediaPlayer.MEDIA_ERROR_IO) {
            Toast.makeText(getContext(), "Network issue", Toast.LENGTH_SHORT).show();
            mMediaPlayer.reset();
        }

        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            mMediaPlayer.reset();
        } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            mMediaPlayer.reset();
        }
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnCompletionListener(this);

        return true;
    }
    //

    /**
     * Handle aspect ratio
     */
    private void handleAspectRatio() {
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = mHost.getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = mHost.getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        android.view.ViewGroup.LayoutParams lp = mVideoSurface.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        mVideoSurface.setLayoutParams(lp);
    }
    //

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
        Toast.makeText(getContext(), "Playback finished!", Toast.LENGTH_LONG).show();
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
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public void seekTo(int i) {
        mMediaPlayer.seekTo(i);
    }

    @Override
    public void start() {
        mMediaPlayer.start();
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
