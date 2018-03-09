package goel.gforgesture;

/**
 * Created by shubhgoe on 04/03/18.
 */


import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.Toast;

public class OverlayShowingService extends AccessibilityService implements OnTouchListener, OnClickListener {

    private View topLeftView;

    private Button overlayedButton;
    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;
    private WindowManager wm;

    NotificationManager notificationManager;
    Notification notification;

    SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        preferences = this.getSharedPreferences("goel.gforgesture.preffile", Context.MODE_PRIVATE);


        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        overlayedButton = new Button(this);
        overlayedButton.setText("");

        overlayedButton.setOnTouchListener(this);
        overlayedButton.setBackgroundColor(0xFF2D2C2C);
        overlayedButton.setOnClickListener(this);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.RIGHT;
        params.x = 0;
        params.y = 0;
        params.width = getScreenWidth() / 45;
        params.height = getScreenHeight() / 4;
        wm.addView(overlayedButton, params);

        topLeftView = new View(this);
        WindowManager.LayoutParams topLeftParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        topLeftParams.gravity = Gravity.RIGHT;
        topLeftParams.x = 0;
        topLeftParams.y = 0;
        topLeftParams.width = 0;
        topLeftParams.height = 0;
        wm.addView(topLeftView, topLeftParams);

        showNotification("Gesture Service is running");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(1234);
        if (overlayedButton != null) {
            wm.removeView(overlayedButton);
            wm.removeView(topLeftView);
            overlayedButton = null;
            topLeftView = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getRawX();
            float y = event.getRawY();

            moving = false;

            int[] location = new int[2];
            overlayedButton.getLocationOnScreen(location);

            originalXPos = location[0];
            originalYPos = location[1];

            offsetX = originalXPos - x;
            offsetY = originalYPos - y;

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int[] topLeftLocationOnScreen = new int[2];
            topLeftView.getLocationOnScreen(topLeftLocationOnScreen);

            System.out.println("topLeftY=" + topLeftLocationOnScreen[1]);
            System.out.println("originalY=" + originalYPos);

            float x = event.getRawX();
            float y = event.getRawY();

            WindowManager.LayoutParams params = (LayoutParams) overlayedButton.getLayoutParams();

            int newX = (int) (offsetX + x);
            int newY = (int) (offsetY + y);

            if (Math.abs(newX - originalXPos) < 1 && Math.abs(newY - originalYPos) < 1 && !moving) {
                return false;
            }

            params.x = newX - (topLeftLocationOnScreen[0]);
            params.y = newY - (topLeftLocationOnScreen[1]);

            wm.updateViewLayout(overlayedButton, params);
            moving = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (moving) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        //Toast.makeText(this, getScreenHeight() + " " + getScreenWidth(), Toast.LENGTH_SHORT).show();
        if (preferences.getBoolean("s1", true)) {
            performGlobalAction(GLOBAL_ACTION_BACK);
        } else if (preferences.getBoolean("s2", false)) {
            performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS);
        } else if (preferences.getBoolean("s3", false)) {
            performGlobalAction(GLOBAL_ACTION_RECENTS);
        }


    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        //Toast.makeText(this, "Awesome", Toast.LENGTH_SHORT).show();

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

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
