package goel.gforgesture;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Home extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    RadioButton s1, s2, s3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        s1 = (RadioButton) findViewById(R.id.swipeFromRightSwitch);
        s2 = (RadioButton) findViewById(R.id.swipeFromLeftSwitch);
        s3 = (RadioButton) findViewById(R.id.swipeFromBottom);

        sharedPref = this.getSharedPreferences("goel.gforgesture.preffile", Context.MODE_PRIVATE);
        s1.setChecked(sharedPref.getBoolean("s1", true));
        s2.setChecked(sharedPref.getBoolean("s2", false));
        s3.setChecked(sharedPref.getBoolean("s3", false));

        s1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                s1.setChecked(true);
                s2.setChecked(false);
                s3.setChecked(false);

                editor = sharedPref.edit();
                editor.putBoolean("s1", true);
                editor.putBoolean("s2", false);
                editor.putBoolean("s3", false);
                editor.commit();
            }
        });

        s2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                s1.setChecked(false);
                s2.setChecked(true);
                s3.setChecked(false);

                editor = sharedPref.edit();
                editor.putBoolean("s1", false);
                editor.putBoolean("s2", true);
                editor.putBoolean("s3", false);
                editor.commit();
            }
        });

        s3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                s1.setChecked(false);
                s2.setChecked(false);
                s3.setChecked(true);

                editor = sharedPref.edit();
                editor.putBoolean("s1", false);
                editor.putBoolean("s2", false);
                editor.putBoolean("s3", true);
                editor.commit();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //logInstalledAccessiblityServices(this);
        TextView serviceStatusText = (TextView) findViewById(R.id.serviceStatusText);
        if (isAccessibilityEnabled(this, "goel.gforgesture/.OverlayShowingService")) {
            serviceStatusText.setText("Service is running");
        } else {
            serviceStatusText.setText("Service is not running");
        }
    }

    public void onClickAccessibility(View v) {
        if (!isSystemAlertPermissionGranted(Home.this)) {
            requestSystemAlertPermission(Home.this, 1);
            Toast.makeText(this, "Please give permission to continue", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent openSettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(openSettings);
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


    public static boolean isAccessibilityEnabled(Context context, String id) {

        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices) {
            if (id.equals(service.getId())) {
                return true;
            }
        }

        return false;
    }

    public static void logInstalledAccessiblityServices(Context context) {

        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getInstalledAccessibilityServiceList();
        for (AccessibilityServiceInfo service : runningServices) {
            Log.i("hi", service.getId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
