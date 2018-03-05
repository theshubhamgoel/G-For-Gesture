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

    View mTestView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void onClickAccessibility(View v) {
        if (!isSystemAlertPermissionGranted(Home.this)) {
            requestSystemAlertPermission(Home.this, 1);
            Toast.makeText(this, "Please give permission to continue", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent openSettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(openSettings);

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
