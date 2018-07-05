package adsender.com.adsender;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText editText_ip, editText_data;
    private OutputStream outputStream = null;
    private Socket socket = null;
    private String ip;
    private String data;
    private EditText editText_port;
    private int port;
    private InputStream inputStream;
    private EditText editText_extra;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_ip = (EditText) findViewById(R.id.host);
        editText_port = (EditText) findViewById(R.id.port);
        editText_data = (EditText) findViewById(R.id.msg);
        editText_extra = (EditText) findViewById(R.id.extra);

        editText_ip.setText(getSharedPreferences("ddd", Context.MODE_PRIVATE).getString("ip", ""));
        editText_port.setText(getSharedPreferences("ddd", Context.MODE_PRIVATE).getString("port", ""));
        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("ddd", Context.MODE_PRIVATE)
                        .edit()
                        .putString("ip", editText_ip.getText().toString())
                        .putString("port", editText_port.getText().toString())
                        .apply();
                send(v);
            }
        });
    }


    public void connect(View view) {

        ip = editText_ip.getText().toString();
        if (ip.isEmpty()) {
            Toast.makeText(MainActivity.this, "please input Server IP", Toast.LENGTH_SHORT).show();
        }
        port = Integer.parseInt(editText_port.getText().toString());

        new Thread() {
            @Override
            public void run() {

                try {
                    socket = new Socket(ip, port);
                    outputStream = socket.getOutputStream();
                    inputStream = socket.getInputStream();

                    data = editText_data.getText().toString();
                    String extra = editText_extra.getText().toString();

                    JSONObject ob = new JSONObject();
                    try {
                        ob.put("command", data);

                        if (extra.startsWith("{")) {
                            ob.put("extra", new JSONObject(extra));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    data = ob.toString();

                    outputStream.write(data.getBytes());
                    outputStream.flush();

                    final byte[] bytes = new byte[1024];
                    inputStream.read(bytes);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), new String(bytes).trim(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                        outputStream.close();
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }


    public void send(View view) {
        connect(view);

    }

    /*当客户端界面返回时，关闭相应的socket资源*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*关闭相应的资源*/
        try {
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}