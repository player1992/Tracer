package com.trace;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends Activity implements GestureDetector.OnGestureListener {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 10;
    private final String TAG = "MainActivity";
    GestureDetectorCompat detector;
    private Random random;
    private Dialog dialog;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss   ", Locale.CHINESE);

    private Stack<WindowBehavior> stack = new Stack<>();

    long timestamp = Long.MIN_VALUE;
    private boolean isKeyboardShow;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        random = new Random();
        Button btn = findViewById(R.id.btn);
        Button btn2 = findViewById(R.id.btn2);
        tv = findViewById(R.id.tv);
        ListView lv = findViewById(R.id.lv);
        detector = new GestureDetectorCompat(this, this);
        detector.setIsLongpressEnabled(false);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAccessibilityService();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char c = getRandomChar();
                tv.setText(String.valueOf(c));
            }
        });
        List<String> names = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.setLength(0);
            sb.append(getRandomChar()).append(getRandomChar());
            names.add(sb.toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_ed,R.id.tv, names);
        lv.setAdapter(adapter);


//        KeyboardStateObserver.getKeyboardStateObserver(this).
//                setKeyboardVisibilityListener(new KeyboardStateObserver.OnKeyboardVisibilityListener() {
//                    @Override
//                    public void onKeyboardShow() {
//                        EditText current = (EditText) getCurrentFocus();
//                        int x = (current.getLeft() + current.getRight()) / 2;
//                        int y = (current.getBottom());
//                        isKeyboardShow = true;
//                        FileUtils.wirteFile("ACTION_MD::TOUCH::("+x+","+y+",MonkeyDevice.DOWN_AND_UP)\n");
//                        FileUtils.wirteFile("ACTION_MR::SLEEP::("+1.0+")\n");
//                        current.addTextChangedListener(new TextWatcher() {
//                            @Override
//                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                            }
//
//                            @Override
//                            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                                CharSequence chars = s.subSequence(start, start + count);
//                                for (int i = 0; i < chars.length(); i++) {
//                                    CharSequence ch = chars.subSequence(i, i);
//                                    int code = KeyEvent.keyCodeFromString(ch.toString());
//                                    FileUtils.wirteFile("ACTION_MD::PRESS::('"+code+"',MonkeyDevice.DOWN_AND_UP)\n");
//                                }
//                            }
//
//                            @Override
//                            public void afterTextChanged(Editable s) {
//
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onKeyboardHide() {
//                        FileUtils.wirteFile("ACTION_MR::SLEEP::("+1.0+")\n");
//                        if (isKeyboardShow) {
//                            FileUtils.wirteFile("ACTION_MD::PRESS::('KEYCODE_BACK',MonkeyDevice.DOWN_AND_UP)\n");
//                        }
//                        isKeyboardShow = false;
//                    }
//                });
    }

    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        return super.dispatchKeyShortcutEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                isKeyboardShow = false;
                FileUtils.wirteFile("ACTION_MD::PRESS::('KEYCODE_BACK',MonkeyDevice.DOWN_AND_UP)\n");
                break;
            case KeyEvent.KEYCODE_ENTER:
                FileUtils.wirteFile("ACTION_MD::PRESS::('KEYCODE_ENTER',MonkeyDevice.DOWN_AND_UP)\n");
                break;
            case KeyEvent.KEYCODE_DEL:
                FileUtils.wirteFile("ACTION_MD::PRESS::('KEYCODE_DEL',MonkeyDevice.DOWN_AND_UP)\n");
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction()==MotionEvent.ACTION_DOWN){
            if (timestamp!=Long.MIN_VALUE) {
                float time = (System.currentTimeMillis()-timestamp)/1000.0f;
                FileUtils.wirteFile("ACTION_MR::SLEEP::("+time+")\n");
            }
            timestamp = System.currentTimeMillis();
        }
        detector.onTouchEvent(ev);
        if (!stack.isEmpty()&&ev.getAction()==MotionEvent.ACTION_UP){
            WindowBehavior pop = stack.pop();
            float time = (System.currentTimeMillis()-timestamp)/1000.0f;
            FileUtils.wirteFile("ACTION_MD::DRAG::(("+pop.x1+","+pop.y1+"),("+pop.x2+","+pop.y2+"),"+time+",10)\n");
            stack.clear();
            timestamp = System.currentTimeMillis();
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Log.d(TAG, "onDown:" + motionEvent.getX() + ":" + motionEvent.getY());
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        Log.d(TAG, "onShowPress:" + motionEvent.getX() + ":" + motionEvent.getY());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        FileUtils.wirteFile("ACTION_MD::TOUCH::("+(int)motionEvent.getX()+","+(int)motionEvent.getY()+",MonkeyDevice.DOWN_AND_UP)\n");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent ev, MotionEvent ev1, float v, float v1) {
        stack.push(new WindowBehavior(ev.getX(),ev.getY(),ev1.getX(),ev1.getY()));
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        Log.d(TAG, "onLongPress:" + motionEvent.getX() + ":" + motionEvent.getY());

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Log.d(TAG, "onFling:" + motionEvent.getX() + ":" + motionEvent.getY() + "\nonFling:" + motionEvent1.getX() + ":" + motionEvent1.getY() + "v:" + v + "v1:" + v1);
        return false;
    }

    private char getRandomChar() {
        String str = "";
        int hightPos; //
        int lowPos;

        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("错误");
        }

        return str.charAt(0);
    }

    private void getPermission() {
        PermissionUtils.needPermission(this, REQUEST_WRITE_EXTERNAL_STORAGE, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, new PermissionUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied() {

            }
        });
    }

    private void startAccessibilityService() {
        new AlertDialog.Builder(this)
                .setTitle("开启辅助功能")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("使用此项功能需要您开启辅助功能")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 隐式调用系统设置界面
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                    }
                }).create().show();
    }

}
