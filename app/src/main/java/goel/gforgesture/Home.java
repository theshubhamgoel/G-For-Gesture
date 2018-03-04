package goel.gforgesture;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    NotificationManager notificationManager;
    Notification notification;
    View mTestView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotification("Gesture service is not running");
    }

    public void onClickAccessibility(View v) {

    }

    public void startService(View v) {
        if (!isSystemAlertPermissionGranted(Home.this)) {
            requestSystemAlertPermission(Home.this, 1);
            Toast.makeText(this, "Please give permission to continue", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Running", Toast.LENGTH_SHORT).show();
        Intent svc = new Intent(this, OverlayShowingService.class);
        startService(svc);
        showNotification("Gesture service is running");

        TextView serviceStatusText = (TextView) findViewById(R.id.serviceStatusText);
        serviceStatusText.setText("Service is running");
    }

    public static void requestSystemAlertPermission(Activity context, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        final String packageName = context == null ? context.getPackageName() : context.getPackageName();
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName));
        if (context != null)
            context.startActivityForResult(intent, requestCode);
        else
            context.startActivityForResult(intent, requestCode);
    }

    @TargetApi(23)
    public static boolean isSystemAlertPermissionGranted(Context context) {
        final boolean result = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || Settings.canDrawOverlays(context);
        return result;
    }

    private void showNotification(String text) {
        // TODO Auto-generated method stub
        notification = new Notification.Builder(this)
                .setContentTitle(text)
                .setSmallIcon(R.drawable.gesture)
                .setOngoing(true)
                .build();
        notificationManager.notify(1234, notification);

    }

    public void showView() {
        WindowManager windowManager = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.FIRST_SUB_WINDOW);
        layoutParams.width = 25;
        layoutParams.height = 450;
        layoutParams.gravity = Gravity.RIGHT;

        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.token = getWindow().getDecorView().getRootView().getWindowToken();

        //Feel free to inflate here
        mTestView = new View(this);
        mTestView.setBackgroundColor(Color.BLACK);

        //Must wire up back button, otherwise it's not sent to our activity
        mTestView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    onBackPressed();
                }
                return true;
            }
        });

        final ViewGroup parent=(ViewGroup)mTestView.getParent();
        if(parent!=null)
            parent.removeView(mTestView);

        windowManager.addView(mTestView, layoutParams);
    }

    @Override
    protected void onDestroy() {
        if (mTestView != null) {
            WindowManager windowManager = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
            if (mTestView.isShown()) {
                windowManager.removeViewImmediate(mTestView);
            }
        }
        super.onDestroy();
    }
}
