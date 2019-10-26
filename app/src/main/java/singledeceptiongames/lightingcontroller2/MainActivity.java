package singledeceptiongames.lightingcontroller2;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerPopup;
import top.defaults.colorpicker.ColorPickerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ColorPickerView colorPickerView = findViewById(R.id.colorPicker);

        // Respond to color changes
        colorPickerView.subscribe(new ColorObserver() {
            @Override
            public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
                byte[] rgb = new byte[4];
                rgb[0] = (byte) 'R';
                rgb[1] = (byte)Color.red(color);
                rgb[2] = (byte)Color.green(color);
                rgb[3] = (byte)Color.blue(color);
                new SendNetworkMessage().execute(rgb);
            }
        });

        // Respond to brightness changes
        SeekBar brightnessBar = findViewById(R.id.seekBar4);
        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                byte[] bright = new byte[2];
                bright[0] = (byte) 'B';
                bright[1] = (byte) progress;
                new SendNetworkMessage().execute(bright);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void adjustBrightness(View view) {
    }

    public void fab1Click(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void fab2Click(final View view) {
        new ColorPickerPopup.Builder(getApplicationContext())
                .initialColor(Color.RED) // Set initial color
                .enableBrightness(true) // Enable brightness slider or not
                .enableAlpha(false) // Enable alpha slider or not
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(false)
                .onlyUpdateOnTouchEventUp(false)
                .build()
                .show(view, new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        view.setBackgroundColor(color);
                        byte[] rgb = new byte[4];
                        rgb[0] = (byte) 'R';
                        rgb[1] = (byte)Color.red(color);
                        rgb[2] = (byte)Color.green(color);
                        rgb[3] = (byte)Color.blue(color);
                        new SendNetworkMessage().execute(rgb);
                    }

                    //@Override
                    public void onColor(int color, boolean fromUser) {
                        byte[] rgb = new byte[4];
                        rgb[0] = (byte) 'R';
                        rgb[1] = (byte)Color.red(color);
                        rgb[2] = (byte)Color.green(color);
                        rgb[3] = (byte)Color.blue(color);
                        new SendNetworkMessage().execute(rgb);
                    }
                });
    }


    private class SendNetworkMessage extends AsyncTask<byte[],String,String> {
        @Override
        protected String doInBackground(byte[]... params) {
            if (params.length > 0) {
                //String message = params[0];
                byte[] message = params[0];

                // Connect to server
                Socket socket;
                try {
                    socket = new Socket("192.168.1.12", 28924);
                } catch (IOException e) {
                    return "Connection exception";
                }

                // Get in/out streams
                InputStream inputStream;
                InputStreamReader inputStreamReader;
                BufferedReader bufferedReader;
                OutputStream outputStream;
                try {
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();

                    inputStreamReader = new InputStreamReader(inputStream);
                    bufferedReader = new BufferedReader(inputStreamReader);
                } catch (IOException e) {
                    return "Input/Output stream exception";
                }

                // Send the message
                try {
                    outputStream.write(message);
                    outputStream.write('\n');
                    outputStream.flush();
                } catch (IOException e) {
                    return "Writing exception";
                }


                try {
                    return bufferedReader.readLine();
                } catch (IOException e) {
                    return "Exception reading reply";
                }
            }

            return "";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
