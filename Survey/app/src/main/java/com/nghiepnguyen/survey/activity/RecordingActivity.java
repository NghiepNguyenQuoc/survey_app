package com.nghiepnguyen.survey.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.lassana.recorder.AudioRecorder;
import com.github.lassana.recorder.AudioRecorderBuilder;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.application.MainApplication;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 08670_000 on 29/12/2015.
 */
public class RecordingActivity extends BaseActivity {
    private static final String TAG = "RecordingActivity";
    private static final String FILE_NAME = "recording.ax";
    private static final String HOST_IP = "112.213.89.21";
    private static final String USER_NAME = "6saovn";
    private static final String PASSWORD = "NKVF2764etxq";
    private static final int PORT = 21;
    private static final int REQUEST_CODE_PERMISSIONS = 0x1;
    Button play, pause, record, upload;
    private AudioRecorder mAudioRecorder;
    private String outputFile = null;
    private Uri mAudioRecordUri;
    private String mActiveRecordFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);


        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FILE_NAME;
        final MainApplication application = MainApplication.getInstance();
        mAudioRecorder = application.getRecorder();
        if (mAudioRecorder == null
                || mAudioRecorder.getStatus() == AudioRecorder.Status.STATUS_UNKNOWN) {
            mAudioRecorder = AudioRecorderBuilder.with(application)
                    .fileName(outputFile)
                    .config(AudioRecorder.MediaRecorderConfig.DEFAULT)
                    .loggable()
                    .build();
            application.setRecorder(mAudioRecorder);
        }

        upload = (Button) findViewById(R.id.button4);
        play = (Button) findViewById(R.id.button3);
        pause = (Button) findViewById(R.id.button2);
        record = (Button) findViewById(R.id.button);
        invalidateViews();
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                tryStart();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                pause();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {
                play();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = ProgressDialog.show(RecordingActivity.this, "", "Uploading...", true, false);
                new Thread(new Runnable() {
                    public void run() {
                        FTPClient ftpClient = new FTPClient();
                        try {
                            //http://www.net2ftp.com/index.php
                            ftpClient.connect(HOST_IP, PORT);
                            ftpClient.login(USER_NAME, PASSWORD);
                            ftpClient.enterLocalPassiveMode();
                            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                            File secondLocalFile = new File(outputFile);
                            InputStream inputStream = new FileInputStream(secondLocalFile);

                            System.out.println("Start uploading second file");
                            OutputStream outputStream = ftpClient.storeFileStream(FILE_NAME);
                            byte[] bytesIn = new byte[4096];
                            int read = 0;

                            while ((read = inputStream.read(bytesIn)) != -1) {
                                outputStream.write(bytesIn, 0, read);
                            }
                            inputStream.close();
                            outputStream.close();

                            boolean completed = ftpClient.completePendingCommand();
                            if (completed) {
                                Log.d(TAG, "Upload success");
                                handler.sendEmptyMessage(2);
                            } else {
                                Log.d(TAG, "Upload failed");
                                handler.sendEmptyMessage(-1);
                            }
                        } catch (IOException ex) {
                            System.out.println("Error: " + ex.getMessage());
                            ex.printStackTrace();
                        } finally {
                            try {
                                if (ftpClient.isConnected()) {
                                    ftpClient.logout();
                                    ftpClient.disconnect();
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mAudioRecorder.isRecording()) {
            mAudioRecorder.cancel();
            setResult(Activity.RESULT_CANCELED);
        }
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void tryStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int checkAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            final int checkStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkAudio != PackageManager.PERMISSION_GRANTED || checkStorage != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    showMessage(getString(R.string.message_no_permissions));
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showMessage(getString(R.string.message_no_permissions));
                } else {
                    requestPermissions(new String[]{
                                    Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_PERMISSIONS);
                }
            } else {
                start();
            }
        } else {
            start();
        }
    }


    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                boolean userAllowed = true;
                for (final int result : grantResults) {
                    userAllowed &= result == PackageManager.PERMISSION_GRANTED;
                }
                if (userAllowed) {
                    start();
                } else {
                    /*
                     * Cannot show dialog from here
                     * https://code.google.com/p/android-developer-preview/issues/detail?id=2823
                     */
                    showMessage(getString(R.string.message_no_permissions));
                }
                break;
            default:
                break;
        }
    }

    private void showMessage(String message) {
        invalidateViews();
        final View root = findViewById(R.id.activity_recording_main_view);
        if (root != null) {
            final Snackbar snackbar = Snackbar.make(root, message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
    }

    private void start() {
        mAudioRecorder.start(new AudioRecorder.OnStartListener() {
            @Override
            public void onStarted() {
                invalidateViews();
            }

            @Override
            public void onException(Exception e) {
                setResult(Activity.RESULT_CANCELED);
                invalidateViews();
                showMessage(getString(R.string.message_error_audio_recorder, e));
            }
        });
    }

    private void pause() {
        mAudioRecorder.pause(new AudioRecorder.OnPauseListener() {
            @Override
            public void onPaused(String activeRecordFileName) {
                mActiveRecordFileName = activeRecordFileName;
                setResult(Activity.RESULT_OK,
                        new Intent().setData(saveCurrentRecordToMediaDB(mActiveRecordFileName)));
                invalidateViews();
            }

            @Override
            public void onException(Exception e) {
                setResult(Activity.RESULT_CANCELED);
                invalidateViews();
                showMessage(getString(R.string.message_error_audio_recorder, e));
            }
        });
    }

    private void play() {
        File file = new File(mActiveRecordFileName);
        if (file.exists()) {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "audio/*");
            startActivity(intent);
        }
    }

    public Uri saveCurrentRecordToMediaDB(final String fileName) {
        if (mAudioRecordUri != null) return mAudioRecordUri;

        final Activity activity = this;
        final Resources res = activity.getResources();
        final ContentValues cv = new ContentValues();
        final File file = new File(fileName);
        final long current = System.currentTimeMillis();
        final long modDate = file.lastModified();
        final Date date = new Date(current);
        final String dateTemplate = res.getString(R.string.audio_db_title_format);
        final SimpleDateFormat formatter = new SimpleDateFormat(dateTemplate, Locale.getDefault());
        final String title = formatter.format(date);
        final long sampleLengthMillis = 1;
        // Lets label the recorded audio file as NON-MUSIC so that the file
        // won't be displayed automatically, except for in the playlist.
        cv.put(MediaStore.Audio.Media.IS_MUSIC, "0");

        cv.put(MediaStore.Audio.Media.TITLE, title);
        cv.put(MediaStore.Audio.Media.DATA, file.getAbsolutePath());
        cv.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        cv.put(MediaStore.Audio.Media.DATE_MODIFIED, (int) (modDate / 1000));
        cv.put(MediaStore.Audio.Media.DURATION, sampleLengthMillis);
        cv.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");
        cv.put(MediaStore.Audio.Media.ARTIST, res.getString(R.string.audio_db_artist_name));
        cv.put(MediaStore.Audio.Media.ALBUM, res.getString(R.string.audio_db_album_name));

        Log.d(TAG, "Inserting audio record: " + cv.toString());

        final ContentResolver resolver = activity.getContentResolver();
        final Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Log.d(TAG, "ContentURI: " + base);

        mAudioRecordUri = resolver.insert(base, cv);
        if (mAudioRecordUri == null) {
            return null;
        }
        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mAudioRecordUri));
        return mAudioRecordUri;
    }

    private void invalidateViews() {
        switch (mAudioRecorder.getStatus()) {
            case STATUS_UNKNOWN:
                record.setEnabled(false);
                pause.setEnabled(false);
                play.setEnabled(false);
                break;
            case STATUS_READY_TO_RECORD:
                record.setEnabled(true);
                pause.setEnabled(false);
                play.setEnabled(false);
                break;
            case STATUS_RECORDING:
                record.setEnabled(false);
                pause.setEnabled(true);
                play.setEnabled(false);
                break;
            case STATUS_RECORD_PAUSED:
                record.setEnabled(true);
                pause.setEnabled(false);
                play.setEnabled(true);
                break;
            default:
                break;
        }
    }

    private ProgressDialog pd;
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (msg.what == 0) {
                //getFTPFileList();
            } else if (msg.what == 1) {
                //showCustomDialog(fileList);
            } else if (msg.what == 2) {
                Toast.makeText(RecordingActivity.this, "Uploaded Successfully!",
                        Toast.LENGTH_LONG).show();
            } else if (msg.what == 3) {
                Toast.makeText(RecordingActivity.this, "Disconnected Successfully!",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(RecordingActivity.this, "Unable to Perform Action!",
                        Toast.LENGTH_LONG).show();
            }

        }

    };
}
