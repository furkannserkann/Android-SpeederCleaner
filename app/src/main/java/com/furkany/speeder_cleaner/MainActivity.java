package com.furkany.speeder_cleaner;

import android.app.ActivityManager;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.furkany.speeder_cleaner.custom.Boost.interfaces.CleanListener;
import com.furkany.speeder_cleaner.custom.Boost.interfaces.ScanListener;
import com.furkany.speeder_cleaner.custom.Boost.utils.ProcessInfo;
import com.furkany.speeder_cleaner.custom.RAMBooster;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private long MemorySize, StorageSize;

    private IconRoundCornerProgressBar progressBarStorage, progressBarMemory;
    private TextView textViewNameStorage, textViewNameMemory, textViewYuzdeStorage, textViewYuzdeMemory,
            textViewUsingStorage, textViewUsingMemory;

    private TextView textViewAppname, textViewPrivatepolicy;
    private james.buttons.Button buttonClean;


    private RAMBooster booster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadTool();
        usingInfo();
    }

    private void loadTool() {
        Typeface typeface1 = Typeface.createFromAsset(getAssets(),"muli.ttf");
        Typeface typeface2 = Typeface.createFromAsset(getAssets(),"major.ttf");

        progressBarStorage = (IconRoundCornerProgressBar) findViewById(R.id.progressStorage);
        progressBarMemory = (IconRoundCornerProgressBar) findViewById(R.id.progressMemory);


        textViewNameStorage = (TextView) findViewById(R.id.textViewStorageName);
        textViewNameMemory = (TextView) findViewById(R.id.textViewMemoryName);
        textViewYuzdeStorage = (TextView) findViewById(R.id.textViewStorageYuzde);
        textViewYuzdeMemory = (TextView) findViewById(R.id.textViewMemoryYuzde);
        textViewUsingStorage = (TextView) findViewById(R.id.textViewUsingStorage);
        textViewUsingMemory = (TextView) findViewById(R.id.textViewUsingMemory);
        textViewAppname = (TextView) findViewById(R.id.textViewAppname);
        textViewPrivatepolicy = (TextView) findViewById(R.id.textViewPrivatepolicy);


        buttonClean = (james.buttons.Button) findViewById(R.id.clean);

        textViewNameStorage.setTypeface(typeface1);
        textViewNameMemory.setTypeface(typeface1);
        textViewYuzdeStorage.setTypeface(typeface1);
        textViewYuzdeMemory.setTypeface(typeface1);
        textViewUsingStorage.setTypeface(typeface1);
        textViewUsingMemory.setTypeface(typeface1);
        textViewAppname.setTypeface(typeface1);
        textViewPrivatepolicy.setTypeface(typeface2);

        buttonClean.setTypeface(typeface1);


        SpannableString content = new SpannableString(textViewPrivatepolicy.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textViewPrivatepolicy.setText(content);


        clickButtons();
    }

    private void clickButtons() {
        buttonClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (booster!=null)
                    booster=null;
                booster = new RAMBooster(MainActivity.this);
                booster.setDebug(true);
                booster.setScanListener(new ScanListener() {
                    @Override
                    public void onStarted() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Starting Clear!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Log.e("ScanListener...", "Starting Clear!");
                    }

                    @Override
                    public void onFinished(long availableRam, long totalRam, List<ProcessInfo> appsToClean) {
                        try {
                            List<String> apps = new ArrayList<String>();
                            for (ProcessInfo info : appsToClean) {
                                apps.add(info.getProcessName());
                            }

                            booster.startClean();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                booster.setCleanListener(new CleanListener() {
                    @Override
                    public void onStarted() { }

                    @Override
                    public void onFinished(long availableRam, long totalRam) {
                        try {
                            booster = null;

                            usingInfo();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Finishing Clear!", Toast.LENGTH_SHORT).show();
                                }
                            });

                            Log.e("ScanListener...", "Finishing Clear!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                booster.startScan(true);
            }
        });
    }

    private void usingInfo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MemorySize = getMemory() * 1024;
                long usingMem = getUsingMemory();
                long yuzdeMemory = (usingMem * 100) / MemorySize;

                progressBarMemory.setMax(MemorySize);
                progressBarMemory.setProgress(usingMem);
                textViewYuzdeMemory.setText(String.valueOf(yuzdeMemory));
                textViewUsingMemory.setText(String.valueOf(unitConversion(usingMem) + " / " + unitConversion(MemorySize)));

                StorageSize = getStorage();
                long usingSto = getUsedStorage();
                long yuzdeSto = (usingSto * 100) / StorageSize;

                progressBarStorage.setMax(StorageSize);
                progressBarStorage.setProgress(usingSto);
                textViewYuzdeStorage.setText(String.valueOf(yuzdeSto));
                textViewUsingStorage.setText(String.valueOf(unitConversion(usingSto) + " / " + unitConversion(StorageSize)));
            }
        });
    }

//    private long TotRam = 1, UsRam = 1, TotMem = 1, UsMem = 1, FreeMem = 1;

    private String unitConversion(long total) {
        String load = null;

        try {
            DecimalFormat twoDecimalForm = new DecimalFormat("#.##");

            double kb = total / 1024.0;
            double mb = total / 1048576.0;
            double gb = total / 1073741824.0;
            double tb = total / 1099511627779.0;

            if (tb > 1)
                load = twoDecimalForm.format(tb).concat(" TB");
            else if (gb > 1)
                load = twoDecimalForm.format(gb).concat(" GB");
            else if (mb > 1)
                load = twoDecimalForm.format(mb).concat(" MB");
            else if (kb > 1)
                load = twoDecimalForm.format(kb).concat(" KB");
            else
                load = twoDecimalForm.format(total).concat(" B");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return load;
    }

    private Long getMemory() {
        String load = null;
        long total = 0L;

        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine().replaceAll("\\D+", "");
            total = Integer.parseInt(load);
        } catch (IOException ex) {
            ex.printStackTrace();
            total = 0L;
        }

        return total;
    }

    private Long getUsingMemory() {
        long total = 0L;

        try {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(mi);
            total = mi.availMem;
        } catch (Exception e) {
            e.printStackTrace();
            total = 0L;
        }

        return total;
    }

    private Long getStorage() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesTotal = stat.getTotalBytes();


        return bytesTotal;
    }

    private Long getUsedStorage() {
        long freeSize = 0L;
        long totalSize = 0L;
        long usedSize = 0L;

        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            freeSize = stat.getFreeBytes();
            totalSize = stat.getTotalBytes();
            usedSize = totalSize - freeSize;

        } catch (Exception e) {
            e.printStackTrace();
            usedSize = 0L;
        }

        return usedSize;
    }
}
