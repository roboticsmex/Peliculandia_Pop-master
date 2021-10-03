package com.peliculandia.pop;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.facebook.login.Login;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.peliculandia.pop.database.DatabaseHelper;
import com.peliculandia.pop.kotlin.descargaversion;
import com.peliculandia.pop.network.RetrofitClient;
import com.peliculandia.pop.network.apis.ConfigurationApi;
import com.peliculandia.pop.network.model.config.ApkUpdateInfo;
import com.peliculandia.pop.network.model.config.Configuration;
import com.peliculandia.pop.utils.ApiResources;
import com.peliculandia.pop.utils.Constants;
import com.peliculandia.pop.utils.HelperUtils;
import com.peliculandia.pop.utils.PreferenceUtils;
import com.peliculandia.pop.utils.ToastMsg;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashScreen";
    private final int PERMISSION_REQUEST_CODE = 100;
    private int SPLASH_TIME = 1500;
    private Thread timer;
    private DatabaseHelper db;
    private boolean isRestricted = false;
    private boolean isUpdate = false;
    private boolean vpnStatus = false;
    private HelperUtils helperUtils;

    @Override
    protected void onStart() {
        super.onStart();

        vpnStatus = new HelperUtils(SplashScreenActivity.this).isVpnConnectionAvailable();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);
        db = new DatabaseHelper(SplashScreenActivity.this);
        helperUtils = new HelperUtils(SplashScreenActivity.this);
        vpnStatus = new HelperUtils(SplashScreenActivity.this).isVpnConnectionAvailable();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkStoragePermission()) {
                getConfigurationData();
            }
        } else {
            getConfigurationData();
        }

        timer = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_TIME);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    if (PreferenceUtils.isLoggedIn(SplashScreenActivity.this)) {
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                        Log.d("omarSanc", "1");
                    } else {

                        if (isLoginMandatory()) {
                            Intent intent = new Intent(SplashScreenActivity.this, Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                            Log.d("omarSanc", "2");
                        } else {
                            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                            Log.d("omarSanc", "3");
                        }
                    }

                }
            }
        };

    }

    public boolean isLoginMandatory() {
        return db.getConfigurationData().getAppConfig().getMandatoryLogin();
    }

    public void getConfigurationData() {
        if (!vpnStatus) {
            Retrofit retrofit = RetrofitClient.getRetrofitInstance();
            ConfigurationApi api = retrofit.create(ConfigurationApi.class);
            Call<Configuration> call = api.getConfigurationData(AppConfig.API_KEY);
            call.enqueue(new Callback<Configuration>() {
                @Override

                public void onResponse(Call<Configuration> call, Response<Configuration> response) {
                    if (response.code() == 200) {
                        Configuration configuration = response.body();
                        if (configuration != null) {
                            configuration.setId(1);

                            ApiResources.CURRENCY = configuration.getPaymentConfig().getCurrency();
                            ApiResources.PAYPAL_CLIENT_ID = configuration.getPaymentConfig().getPaypalClientId();
                            ApiResources.EXCHSNGE_RATE = configuration.getPaymentConfig().getExchangeRate();
                            ApiResources.RAZORPAY_EXCHANGE_RATE = configuration.getPaymentConfig().getRazorpayExchangeRate();
                            //save genre, country and tv category list to constants
                            Constants.genreList = configuration.getGenre();
                            Constants.countryList = configuration.getCountry();
                            Constants.tvCategoryList = configuration.getTvCategory();

                            db.deleteAllDownloadData();
                            //db.deleteAllAppConfig();
                            if (db.getConfigurationCount() != 1) {
                                db.deleteAllAppConfig();
                                //db.insertConfigurationData(configuration);
                                db.insertConfigurationData(configuration);
                            }
                            //db.updateConfigurationData(configuration, 1);
                            db.updateConfigurationData(configuration, 1);

                            //apk update check
                            if (isNeedUpdate(configuration.getApkUpdateInfo().getVersionCode())) {
                                showAppUpdateDialog(configuration.getApkUpdateInfo());
                                //    Toast.makeText(SplashScreenActivity.this, "nueva version", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (db.getConfigurationData() != null) {
                                timer.start();
                            } else {
                                showErrorDialog(getString(R.string.error_toast), getString(R.string.no_configuration_data_found));
                            }
                        } else {
                            showErrorDialog(getString(R.string.error_toast), getString(R.string.failed_to_communicate));
                        }
                    } else {
                        showErrorDialog(getString(R.string.error_toast), getString(R.string.failed_to_communicate));
                    }
                }

                @Override
                public void onFailure(Call<Configuration> call, Throwable t) {
                    Log.e("ConfigError", t.getLocalizedMessage());
                    showErrorDialog(getString(R.string.error_toast), getString(R.string.failed_to_communicate));
                }
            });
        } else {
            helperUtils.showWarningDialog(SplashScreenActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
        }
    }

    private void showAppUpdateDialog(final ApkUpdateInfo info) {

        String info_version = "Nueva version: "+info.getVersionName();

        String info_novedades = info.getWhatsNew();

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SplashScreenActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(SplashScreenActivity.this).inflate(
                R.layout.layout_warning_calificanos,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainerRR)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitleRT)).setText(info_version);
        ((TextView) view.findViewById(R.id.textMessageRT)).setText(info_novedades);
        ((Button) view.findViewById(R.id.buttonYesRR)).setText(getResources().getString(R.string.desdegoogle));
        ((Button) view.findViewById(R.id.buttonNoRR)).setText(getResources().getString(R.string.actualizadesdeaqui));
        ((ImageView) view.findViewById(R.id.imageIconRT)).setImageResource(R.drawable.logo);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonYesRR).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(gameOnlineMultii.this, "Mensaje enviado a todos los usuarios", Toast.LENGTH_SHORT).show();
                //   enviarnotiall();

                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.buttonNoRR).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashScreenActivity.this, descargaversion.class);

                startActivity(intent);
                finish();
                alertDialog.dismiss();

            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showAppUpdateDialogcopia(final ApkUpdateInfo info) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Nueva versión: " + info.getVersionName())
                .setMessage(info.getWhatsNew())
                .setPositiveButton("Actualizar ahora", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //update clicked

                        //  Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.getApkUrl()));
                        //  startActivity(browserIntent);
                        Intent intent = new Intent(SplashScreenActivity.this, descargaversion.class);
                        //    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        //    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("Luego", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //exit clicked
                        if (info.isSkipable()) {
                            if (db.getConfigurationData() != null) {
                                timer.start();
                            } else {
                                new ToastMsg(SplashScreenActivity.this).toastIconError(getString(R.string.error_toast));
                                finish();
                            }
                        } else {
                            System.exit(0);
                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }


    private void showErrorDialog(String title, String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                        finish();
                    }
                })
                .show();
    }

    private boolean isNeedUpdate(String versionCode) {
        return Integer.parseInt(versionCode) > BuildConfig.VERSION_CODE;
    }

    // ------------------ checking storage permission ------------
    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                Log.v(TAG, "Permission is granted");
                return true;

            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //resume tasks needing this permission
            getConfigurationData();
        }
    }

    public static void createKeyHash(Activity activity, String yourPackage) {
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(yourPackage, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(SplashScreenActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
        }
    }
}
