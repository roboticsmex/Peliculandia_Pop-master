package com.peliculandia.pop.activities;

import static com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Rational;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.mediarouter.app.MediaRouteButton;

import com.agrawalsuneet.dotsloader.loaders.TrailingCircularDotsLoader;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.DefaultDatabaseProvider;
import com.google.android.exoplayer2.ext.cast.CastPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.common.images.WebImage;
import com.google.android.material.snackbar.Snackbar;
import com.peliculandia.pop.DetailsActivity;
import com.peliculandia.pop.MainActivity;
import com.peliculandia.pop.R;
import com.peliculandia.pop.data.ExoPlayerCache;
import com.peliculandia.pop.database.DatabaseHelper;
import com.peliculandia.pop.network.model.config.AdsConfig;
import com.peliculandia.pop.services.PinchListener;
import com.peliculandia.pop.util.Conses;
import com.peliculandia.pop.util.Utils;
import com.peliculandia.pop.util.Widget;
import com.peliculandia.pop.utils.Constants;
import com.peliculandia.pop.utils.ads.BannerAds;
import com.peliculandia.pop.utils.ads.NativeAds;
import com.peliculandia.pop.utils.ads.PopUpAds;
import com.peliculandia.pop.widget.CustomOnScaleGestureListener;
import com.peliculandia.pop.widget.PlayerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class VideoPlayer extends AppCompatActivity implements CastPlayer.SessionAvailabilityListener{
    private Context mContext;

    /// Player vars
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private ActionBar ac;
    private static boolean floating = false;
    private TextView tv;
    private TrailingCircularDotsLoader mLoading;
    private Toolbar toolbar;
    private Boolean isPreparing = false;
    private ImageView mExoRewind, mExoForward,mExoLock,mPicturetoPICTURE;
    private LinearLayout mExoControls1, mExoControls2, mExoControls3;
    private boolean stream = true, locked = false;
    private ScaleGestureDetector scaleGestureDetector;

    // Ads vars
   //  private AdView mAdView;
  //   private InterstitialAd mInterstitialAd;

    /// Source data
    private boolean isPremium = false;
    private String url, title, referer,obtengotitulo,obtengoimagenportada;

    /// PIP mode
    private BroadcastReceiver mReceiver;
    private static final int REQUEST_CODE = 101;

   //google cast
   public MediaRouteButton mediaRouteButton;

   private CastContext castContext;
    private CastPlayer castPlayer;

    private boolean castSession;

    MediaQueueItem item;

    private InterstitialAd mInterstitialAd;

    private final String TAG = "junior";

    private AdView mAdView;

    String almacenaFuente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);


        //google cast inicia
        castContext = CastContext.getSharedInstance(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

                loadanuncio();
            }
        });

        mAdView = findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());
        mAdView.setVisibility(View.VISIBLE);

        //   adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        Handler handle2 = new Handler();
        Runnable r2 = new Runnable() {
            public void run() {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(VideoPlayer.this);
                    Log.d("TAG", "sI CARGUE ANUNCIO.");
                } else {
                    Log.d("TAG", "NO CARGUE ANUNCIO.");
                }

            }
        };
        handle2.postDelayed(r2, 4000);

        mediaRouteButton = findViewById(R.id.media_route_button);
        // dibuja boton
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), mediaRouteButton);


        castPlayer = new CastPlayer(castContext);

        castPlayer.setSessionAvailabilityListener(this);

        castSession = getIntent().getBooleanExtra("castSession", false);

    //    StartAppSDK.init(this,"206153457",false);
       // StartAppAd.showAd(this);

        if (Utils.isEmulator())
            finishAndRemoveTask();
        // Set context
        mContext = VideoPlayer.this;

        Utils.initializeAds(mContext);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        zoomVideo();

        retrieveExtras();



        Log.d("iztacalco","sientre1");
        Log.d("iztacalco",url);
        Log.d("iztacalco","referer es : "+referer);
        Log.d("iztacalco","el titulo es "+title);
        Log.d("iztacalco",obtengoimagenportada);
        Log.d("iztacalco",obtengotitulo);
        Log.d("iztacalco","el contenido stream es: "+stream);

        almacenaFuente = referer.split("/")[2];
        Log.d("panda", almacenaFuente);

        if (almacenaFuente.equals("mixdrop.to")) {

            mediaRouteButton.setEnabled(false);
            Toast.makeText(VideoPlayer.this, "No disponible Chromecast con este video", Toast.LENGTH_SHORT).show();

        } else if(almacenaFuente.equals("jekjukfkgt-mjxhlvtpkg.b-cdn.net")) {

            mediaRouteButton.setEnabled(false);
            Toast.makeText(VideoPlayer.this, "No disponible Chromecast con este video", Toast.LENGTH_SHORT).show();
        }else if(almacenaFuente.equals("JekjUKFkGT-MJxHLVtpkG.b-cdn.net")) {

            mediaRouteButton.setEnabled(false);
            Toast.makeText(VideoPlayer.this, "No disponible Chromecast con este video", Toast.LENGTH_SHORT).show();
        }else if(almacenaFuente.equals("objectstorage.us-phoenix-1.oraclecloud.com")) {

            mediaRouteButton.setEnabled(false);
            Toast.makeText(VideoPlayer.this, "No disponible Chromecast con este video", Toast.LENGTH_SHORT).show();
        } else
        {
            // si esta disponible la pantalla enciende el boton
            if (castContext.getCastState() != CastState.NO_DEVICES_AVAILABLE)
            {
                mediaRouteButton.setVisibility(View.VISIBLE);
            }
            mediaRouteButton.setEnabled(true);
        }

 //       mInterstitialAd = new InterstitialAd(mContext);
 //       mInterstitialAd.setAdUnitId(getString(R.string.idInterestial));

        // Link xml elements


        toolbar = findViewById(R.id.toolbar);
        mLoading = findViewById(R.id.loadIndicator);
        playerView = findViewById(R.id.playerView);
        mExoForward = findViewById(R.id.exo_ffwd);
        mExoRewind = findViewById(R.id.exo_rew);
        mExoLock = findViewById(R.id.exo_lock);
        mPicturetoPICTURE =  findViewById(R.id.botonpicturetopicture);
        mExoControls1 = findViewById(R.id.exo_controls1);
        mExoControls2 = findViewById(R.id.dura_els);
        mExoControls3 = findViewById(R.id.prog_els);
        scaleGestureDetector = new ScaleGestureDetector(mContext,
                new CustomOnScaleGestureListener(new PinchListener() {
                    @Override
                    public void onZoomOut() {
                        if (!locked && playerView != null) {
                            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                            showSnack(getString(R.string.zoom_out));
                        }
                    }

                    @Override
                    public void onZoomIn() {
                        if (!locked && playerView != null) {
                            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                            showSnack(getString(R.string.zoom_in));
                        }
                    }
                }));

        // Set toolbar
        setSupportActionBar(toolbar);
        startPlaying(url);


        //anuncios

        //
    }

    private void showSnack(String msg){
        CoordinatorLayout coordinatorLayout=(CoordinatorLayout)findViewById(R.id.cordinator);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(Color.parseColor("#65000000"));

        CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        view.setLayoutParams(params);
        snackbar.show();
    }

    private void startPlaying(String finalURL){
        url = finalURL;
        openPlayer();
    }

    private void retrieveExtras(){
        try {
            url = getIntent().getStringExtra("url");
            obtengoimagenportada = getIntent().getStringExtra("urlImagenPortada");
            obtengotitulo = getIntent().getStringExtra("tituloMovie");
            referer = getIntent().getStringExtra("referer");
            title = getIntent().getStringExtra("title");
            stream = getIntent().getBooleanExtra("stream", false);

            if (url == null || title == null){
                referer = "";
                url = getIntent().getDataString();
                stream = !(url.startsWith("content:") || url.startsWith("file:"));
                title = "video_" + Calendar.getInstance().getTimeInMillis();

            }
        }catch (Exception er){
            Toast.makeText(mContext, getString(R.string.error_ocurr), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //// METHODS
    // Init player
    private void openPlayer() {
        // Set increment forward/rewind
        playerView.setFastForwardIncrementMs(10000);
        playerView.setRewindIncrementMs(10000);

        // Lock button listener

        mExoLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locked = !locked;
                mExoLock.setImageDrawable(ContextCompat.getDrawable(mContext,
                        locked ? R.drawable.ic_lock_closed_outline : R.drawable.ic_lock_closed_outline));
                VideoPlayer.this.lockPlayerControls(locked);
            }
        });

        mPicturetoPICTURE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enterPIPMode();

            }
        });



        setActionBar();
        try{
            changeBarSize();
        }catch (Exception tr){}
        init(false);
    }

    /**
     * Set toolbar
     */
    private void setActionBar(){
        ac = getSupportActionBar();
        if (ac != null) {
            ac.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
            ac.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Change toolbar size
     */
    private void changeBarSize(){
        tv = new TextView(mContext);

        // Create a LayoutParams for TextView
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        tv.setLayoutParams(lp);
        tv.setText(getString(R.string.m_load));
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
        tv.setMaxLines(1);
        tv.setEllipsize(TextUtils.TruncateAt.END);

        ac.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ac.setCustomView(tv);
        ac.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Lock/unlock ads
     * @param lock
     */
    public void toggleBanner(boolean lock){
 //       mAdView.setVisibility(lock ? View.INVISIBLE : (!isPremium ? View.VISIBLE : View.INVISIBLE));
    }

    /**
     * Lock unlock screen controls
     * @param lock
     */
    private void lockPlayerControls(boolean lock){
        toggleBanner(lock);
        mExoControls1.setVisibility(lock ? View.INVISIBLE : View.VISIBLE);
        mExoControls2.setVisibility(lock ? View.INVISIBLE : View.VISIBLE);
        mExoControls3.setVisibility(lock ? View.INVISIBLE : View.VISIBLE);
        showActionBar(!lock);
    }

    /**
     * Show hide toolbar
     * @param show
     */
    private void showActionBar(boolean show){
        if (ac != null)
            if (show){
                if (!ac.isShowing()){
                    ac.show();
                }
            }else{
                if (ac.isShowing()){
                    ac.hide();
                }
            }
    }

    /**
     * Zoom video to fit
     */
    private void zoomVideo(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Window window =  this.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getAttributes().layoutInDisplayCutoutMode =  WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    /**
     * Show progress bar
     * @param show
     */
    private void showLoading(boolean show){
        if(mLoading != null){
            if (show){
                if (mLoading.getVisibility() != View.VISIBLE){
                    mLoading.setVisibility(View.VISIBLE);
                }
            }else{
                mLoading.setVisibility(View.GONE);
            }
        }
    }

    /// PLAYER METHODS
    private void init(Boolean all) {
        showLoading(true);
        DefaultLoadControl.Builder bl = new DefaultLoadControl.Builder();
        bl.setBufferDurationsMs(3500,
                150000,
                2500,
                3000);

        SimpleExoPlayer.Builder builder = new SimpleExoPlayer.Builder(
                mContext,
                new DefaultRenderersFactory(mContext)
        ).setLoadControl(bl.createDefaultLoadControl());

        player = builder.build();
        player.setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        playerView.setPlayer(player);
        playerView.setGestureDetector(scaleGestureDetector);
        playerView.setKeepScreenOn(true);
        playerView.requestFocus();
        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if (visibility == View.VISIBLE) {
                    if (!locked)
                        VideoPlayer.this.showActionBar(true);
                } else {
                    VideoPlayer.this.showActionBar(false);
                }
            }
        });

        MediaSource mediaSource;

        if(stream){
            Map<String, String> headers = new HashMap<>();
            headers.put("Referer", referer);

            try{
                DefaultHttpDataSourceFactory dataIntance = new DefaultHttpDataSourceFactory(
                        Util.getUserAgent(mContext, this.getPackageName()),
                        Conses.EXO_CON_TIME,
                        Conses.EXO_CON_TIME,
                        true
                );

                DatabaseProvider dp =
                        new DefaultDatabaseProvider(
                                new SQLiteOpenHelper(
                                        mContext,
                                        "ExoPlayer",
                                        null,
                                        1) {
                                    @Override
                                    public void onCreate(SQLiteDatabase db) {

                                    }

                                    @Override
                                    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

                                    }
                                });
                dataIntance.getDefaultRequestProperties().set(headers);
                CacheDataSourceFactory dataSourceFactory = new CacheDataSourceFactory(
                        ExoPlayerCache.getInstance(mContext), dataIntance, CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

                switch (Util.inferContentType(Uri.parse(url))){
                    case C.TYPE_HLS:
                        mediaSource = new HlsMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                    case C.TYPE_SS:
                        mediaSource = new SsMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                    case C.TYPE_DASH:
                        mediaSource = new DashMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                    default:
                        mediaSource = new ProgressiveMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                }
            }catch (Exception err){
                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(
                        Util.getUserAgent(mContext, this.getPackageName()),
                        Conses.EXO_CON_TIME,
                        Conses.EXO_CON_TIME,
                        true
                );

                dataSourceFactory.getDefaultRequestProperties().set(headers);

                switch (Util.inferContentType(Uri.parse(url))){
                    case C.TYPE_HLS:
                        mediaSource = new HlsMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                    case C.TYPE_SS:
                        mediaSource = new SsMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                    case C.TYPE_DASH:
                        mediaSource = new DashMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                    default:
                        mediaSource = new ProgressiveMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                }
            }
        }else{
            DefaultDataSourceFactory dataSourceFactory =
                    new DefaultDataSourceFactory(this,
                            Util.getUserAgent(this, this.getPackageName()));

            mediaSource = new ProgressiveMediaSource
                    .Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(url));
        }

        isPreparing = true;
        player.prepare(mediaSource);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        showLoading(true);
                        break;
                    case Player.STATE_ENDED:
                        showLoading(false);
                        break;
                    case Player.STATE_IDLE:
                        showLoading(true);
                        break;
                    case Player.STATE_READY:
                        showLoading(false);
                        if (isPreparing){
                            showLoading(false);
                            isPreparing = false;
                         //   showInterstitial();

                        }
                        break;
                    default:
                        showLoading(false);
                        break;
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }
        });
        player.setPlayWhenReady(true);
        MediaSessionCompat mediaSession = new MediaSessionCompat(mContext, getPackageName());
        MediaSessionConnector mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setPlayer(player);
        mediaSession.setActive(true);
        tv.setText("Peliculandia Pop : "+obtengotitulo);
        if (!playerView.getUseController())
            playerView.setUseController(true);
    }

    private void releasePlayer(Boolean finish) {
        if (player != null) {
            if (finish) {
                mLoading = null;
                player.release();
                playerView.setPlayer(null);
                finish();
            } else pausePlayer();
        }else{
            finish();
        }
    }

    @SuppressLint("NewApi")
    private void enterPIPMode() {
        if (Widget.canPIP(mContext)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Rational aspectRatio = new Rational(playerView.getWidth(), playerView.getHeight());
                PictureInPictureParams.Builder params = new PictureInPictureParams.Builder();
                params.setAspectRatio(aspectRatio).build();
                this.enterPictureInPictureMode(params.build());
            } else {
                this.enterPictureInPictureMode();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void createPipAction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final ArrayList<RemoteAction> actions = new ArrayList<>();

            Intent actionIntent =
                    new Intent("com.peliculandia.pop.PLAY_PAUSE");

            final PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    mContext,
                    REQUEST_CODE, actionIntent, 0);
            Icon icon = Icon.createWithResource(mContext,
                    player != null && player.getPlayWhenReady() ? R.drawable.ic_pause_outline : R.drawable.ic_play_outline );
            icon.setTint(ContextCompat.getColor(mContext, R.color.white));

            RemoteAction remoteAction = new RemoteAction(icon, "Player",
                    "Play", pendingIntent);

            actions.add(remoteAction);
            PictureInPictureParams params =
                    new PictureInPictureParams.Builder()
                            .setActions(actions)
                            .build();

            setPictureInPictureParams(params);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if ((keyCode == KeyEvent.KEYCODE_BACK ||
                keyCode == KeyEvent.KEYCODE_MENU ||
                keyCode == KeyEvent.KEYCODE_HOME) && locked) {
            return false;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onPictureInPictureModeChanged (boolean isInPictureInPictureMode,
                                               Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if (isInPictureInPictureMode) {
            startPlayer();
            playerView.setUseController(false);
            floating = true;
            showActionBar(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                IntentFilter filter = new IntentFilter();
                filter.addAction("com.peliculandia.pop.PLAY_PAUSE");
                mReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context,
                                          Intent intent) {
                        if (player != null){
                            boolean state = !player.getPlayWhenReady();
                            player.setPlayWhenReady(state);
                            createPipAction();
                        }
                    }
                };
                registerReceiver(mReceiver, filter);
                createPipAction();
            }
        } else {
            playerView.setUseController(true);
            floating = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (mReceiver != null) {
                    unregisterReceiver(mReceiver);
                }
            }
        }
    }


    private void pausePlayer(){
        if (player != null){
            try{
                if (player.getPlaybackState() == Player.STATE_READY
                        && player.getPlayWhenReady()){
                    player.setPlayWhenReady(false);
                }
            }catch (Exception e){}
        }
    }

    private void startPlayer(){
        if (player != null){
            try{
                if (player.getPlaybackState() == Player.STATE_READY
                        && !player.getPlayWhenReady()){
                    player.setPlayWhenReady(true);
                }
            }catch (Exception u){}
        }
    }

    @Override
    public void onPause() {
        showActionBar(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                isInPictureInPictureMode() && floating) {
            releasePlayer(false);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                isInPictureInPictureMode()){
        }else{
            pausePlayer();
        }

        super.onPause();
    }


    @Override
    public void onStop() {
        if (floating)
            releasePlayer(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                isInPictureInPictureMode() && floating) {
            releasePlayer(false);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                isInPictureInPictureMode()){

        }else{
            pausePlayer();
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        releasePlayer(true);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        startPlayer();
    }

    @Override
    public void onBackPressed() {
        releasePlayer(true);
        super.onBackPressed();
    }

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        try {
            enterPIPMode();
        }catch (Exception e){}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

       @Override
    public void onCastSessionAvailable() {
        inicia_cast();

      //  Toast.makeText(mContext, "Disfruta tu pelicula " , Toast.LENGTH_SHORT).show();
       //    Toast.makeText(mContext, "inicia:"+url , Toast.LENGTH_SHORT).show();
       //    Toast.makeText(mContext, "Titulo:"+obtengotitulo , Toast.LENGTH_SHORT).show();
    }

    private void inicia_cast() {
        //carga todos los datos para transmitir la pelicula
        Log.d("url", url);
        Log.d("url", obtengotitulo);
        Log.d("url", obtengoimagenportada);

      // video de prueba -  String videoUrl = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/720/Big_Buck_Bunny_720_10s_1MB.mp4";
        castSession = true;
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, obtengotitulo);
        movieMetadata.putString(MediaMetadata.KEY_ALBUM_ARTIST, "Test Artist");
        movieMetadata.addImage(new WebImage(Uri.parse(obtengoimagenportada)));
        MediaInfo mediaInfo = new MediaInfo.Builder(url)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(MimeTypes.VIDEO_UNKNOWN)
                .setMetadata(movieMetadata).build();

        item = new MediaQueueItem.Builder(mediaInfo).build();
        castPlayer.loadItem(item,4000);

        /*
        //array of media sources
        MediaQueueItem[] mediaItems = {new MediaQueueItem.Builder(mediaInfo).build()};
    //    CastPlayer castPlayer = new CastPlayer(castContext);
        //reproduce pelicula
        castPlayer.loadItems(mediaItems, 0, 4000, Player.REPEAT_MODE_OFF);


         */




        // deshabilita controles en celular
      //     player.setPlayWhenReady(false);
        //   playerView.setUseController(false);
    }

/*
    private void inicia_cast() {
        //carga todos los datos para transmitir la pelicula
        Log.d("url", url);
        Log.d("url", obtengotitulo);
        Log.d("url", obtengoimagenportada);

        String videoUrl = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/720/Big_Buck_Bunny_720_10s_1MB.mp4";
        castSession = true;
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, obtengotitulo);
        movieMetadata.putString(MediaMetadata.KEY_ALBUM_ARTIST, "Test Artist");
        movieMetadata.addImage(new WebImage(Uri.parse(obtengoimagenportada)));
        MediaInfo mediaInfo = new MediaInfo.Builder(url)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(MimeTypes.VIDEO_UNKNOWN)
                .setMetadata(movieMetadata).build();

        //array of media sources
        final MediaQueueItem[] mediaItems = {new MediaQueueItem.Builder(mediaInfo).build()};


        final CastPlayer castPlayer = new CastPlayer(castContext);
        //detecta di hay una sesion iniciada
        castPlayer.setSessionAvailabilityListener(new CastPlayer.SessionAvailabilityListener() {
            @Override
            public void onCastSessionAvailable() {
                //carga los datos de la pelicula en el castplayer - reproduce
                castPlayer.loadItems(mediaItems, 0, 4000, Player.REPEAT_MODE_OFF);
                castPlayer.loadItems(mediaItems, 0, 4000, Player.REPEAT_MODE_OFF);
            }

            @Override
            public void onCastSessionUnavailable() {
            }
        });
        // deshabilita controles en celular
        //     player.setPlayWhenReady(false);
        //   playerView.setUseController(false);
    }
    */


    @Override
    public void onCastSessionUnavailable() {
        termina_cast();
    }

    private void termina_cast() {
        // make cast session false
        castSession = false;

        rebuildQueueItem(item);
        // invisible control ui of exoplayer
        //    player.setPlayWhenReady(true);
        //    playerView.setUseController(true);

        // invisible control ui of casting
        //    castControlView.setVisibility(GONE);
        //    chromeCastTv.setVisibility(GONE);
    }

    public static MediaQueueItem rebuildQueueItem(MediaQueueItem item) {
        return new MediaQueueItem.Builder(item).clearItemId().build();
    }

    private void loadanuncio() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-8545394288685224/1295075961", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                                //  finish();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

    }

    /*
    private void loadAd() {
        AdsConfig adsConfig = db.getConfigurationData().getAdsConfig();
        if (adsConfig.getAdsEnable().equals("1")) {

            if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.ADMOB)) {
                BannerAds.ShowAdmobBannerAds(this, adView1);
                PopUpAds.ShowAdmobInterstitialAds(this);

            } else if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.START_APP)) {
                BannerAds.showStartAppBanner(this, adView2);
                PopUpAds.showStartappInterstitialAds(VideoPlayer.this);

            } else if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.NETWORK_AUDIENCE)) {
              //  BannerAds.showFANBanner(this, adView3);
              //  PopUpAds.showFANInterstitialAds(VideoPlayer.this);
            }

        }

    }

     */

}
