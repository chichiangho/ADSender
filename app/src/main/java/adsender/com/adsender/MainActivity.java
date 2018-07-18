package adsender.com.adsender;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chichaingho.library_rx_okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12345);
        }
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
        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("ddd", Context.MODE_PRIVATE)
                        .edit()
                        .putString("ip", editText_ip.getText().toString())
                        .putString("port", editText_port.getText().toString())
                        .apply();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null && data.getData() != null) {
            Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(final ObservableEmitter<String> emitter) throws Exception {

                    String path = getPath(getBaseContext(), data.getData());
                    OkHttpClient.INSTANCE.upLoad("http:" + editText_ip.getText().toString() + ":" + editText_port.getText().toString() + "/upload", path, new OkHttpClient.ProgressListener() {
                        @Override
                        public void onProgress(int progress, Exception e) {
                            if (progress == -1)
                                emitter.onError(e);
                            else
                                emitter.onNext("" + progress);
                        }
                    });
                }
            }).observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        Toast toast = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);

                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(String s) {
                            toast.setText(s);
                            toast.show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
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
//        connect(view);
        httpSend();
    }

    private void httpSend() {

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