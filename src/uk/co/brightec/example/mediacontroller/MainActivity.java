package uk.co.brightec.example.mediacontroller;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
    private VideoPlayerDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDialog = new VideoPlayerDialog(this, this, Uri.parse("http://erezmor.s3.amazonaws.com/tutorial_1.mp4"));
        mDialog.show();

        findViewById(R.id.content_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDialog.dismiss();
    }
}
