package net.karlitos.supersimplekeyboard;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputContentInfo;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static android.provider.UserDictionary.AUTHORITY;

/**
 * Created by Karel Macha 2015-2016
 * Package: net.karlitos.supersimplekeyboard
 * <p/>
 * DISABLE HARDWARE KEYBOARD IN EMULATOR FOR PROPER WORK OF THE KEYBOARD
 * <p/>
 * Based on previous work of Andreas Madner: https://github.com/AndreasMadner/SMSsenior_Keyboard
 * Source:  http://code.tutsplus.com/tutorials/create-a-custom-keyboard-on-android--cms-22615
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class KeyboardService extends InputMethodService implements KeyboardView.OnKeyboardActionListener, SharedPreferences.OnSharedPreferenceChangeListener
{
    private KeyboardView keyboardView;
    private Keyboard defaultKeyboard;
    private Keyboard charKeyboard;
    private boolean isCaps = true; //Start the keyboard in Caps-layout
    private boolean isCharKeyboard = false; //Start the keyboard with letter-layout
    InputMethodManager imm;

    ActionBroadcastReceiver mReciever = new ActionBroadcastReceiver();

    Toolbar mToolbar;
    CandidateImage mCandidateImage;

    CustomTabHelper mCustomTabHelper;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateInputView()
    {
        CustomTabHelper.getInstance().setContext(this.getApplicationContext());
        mCustomTabHelper = CustomTabHelper.getInstance();
        mCustomTabHelper.bindCustomTabsService();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.microsoft.emmx.images");
        registerReceiver(mReciever,intentFilter);

        setCandidatesViewShown(true);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // SharedPreferences prefs = getSharedPreferences("ime_preferences", MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // register preference change listener
        prefs.registerOnSharedPreferenceChangeListener(this);

        // Set the initial layout according to the initial layout selection
        switch(prefs.getString("layouts", "qwert")) { // Initialize keyboard with abcde or qwert layout
            case "abcde":
                defaultKeyboard = new Keyboard(this, R.xml.alphabet_letter_keyboard);
                break;
            case "qwert":
                defaultKeyboard = new Keyboard(this, R.xml.typewriter_letter_keyboard);
                break;
        }

        charKeyboard = new Keyboard(this, R.xml.specialcharacter_keyboard);
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboardView.setKeyboard(defaultKeyboard);
        keyboardView.setOnKeyboardActionListener(this);
        keyboardView.setPreviewEnabled(false); //Disable the preview when key is long pressed
        defaultKeyboard.setShifted(true); // Start the keyboard in Caps-layout
        keyboardView.invalidateAllKeys(); // Requests a redraw of the entire keyboard
        return keyboardView;
    }

    @Override
    public void onKey(int primaryKeyCode, int[] ints)
    {
        setCandidatesViewShown(true);
        InputConnection inputConnection = getCurrentInputConnection(); //Retrieve the currently active InputConnection that is bound to the input method
        switch (primaryKeyCode)
        {
            case 1001: //Enter key pressed
                String keyWords = mToolbar.getText();
                if(keyWords==null || keyWords.equals("")) {
                    mCustomTabHelper.sendCustomTabActionSession(CustomTabHelper.ACTION_REOPEN,null);
                }else {
                    mCustomTabHelper.openCustomTabWithRemoteViewSession("https://www.bing.com/search?q=" + mToolbar.getText());
                }
                break;
            case Keyboard.KEYCODE_DONE: //Enter key pressed
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;

            case Keyboard.KEYCODE_DELETE: // BackSpace key pressed
                if(mToolbar.searchBoxFocused()){
                    mToolbar.deleteLast();
                    return;
                }
                inputConnection.deleteSurroundingText(1, 0); //Delete 1 character of text before the current cursor position and 0 after the cursor position
                break;

            case Keyboard.KEYCODE_SHIFT: // Shit key pressed
                if (isCaps)
                {
                    defaultKeyboard.setShifted(false); // If keyboard is already in Caps-layout then return to lower key-layout
                    keyboardView.invalidateAllKeys(); // Requests a redraw of the entire keyboard
                    isCaps = false;
                }
                else
                {
                    isCaps = true;
                    defaultKeyboard.setShifted(true); // if keyboard is in lower key-layout then switch it to Caps-layout
                    keyboardView.invalidateAllKeys(); // Requests a redraw of the entire keyboard
                }
                break;

            default:
                char keyCode = (char) primaryKeyCode;

                if (primaryKeyCode == -99999) //keyCode for switching between letter keyboard layout and character-layout
                {
                    if (!isCharKeyboard) //if keyboard is in letter-layout then switch it to character-layout
                    {
                        isCharKeyboard = true;
                        keyboardView.setKeyboard(charKeyboard);
                    }
                    else //if keyboard is already in character-layout then switch back to letter-layout
                    {
                        isCharKeyboard = false;
                        keyboardView.setKeyboard(defaultKeyboard);
                    }
                }
                else if (primaryKeyCode == -99998) {
                    // Start voice input
                    // check if the  Google voice input exist first
                    String voiceExists = voiceExists(imm);
                    if (voiceExists != null) {
                        final IBinder token = getWindow().getWindow().getAttributes().token;
                        imm.setInputMethod(token,voiceExists);
    }
                } else if (primaryKeyCode == -99997) {
                    // close the soft keyboard
                    requestHideSelf(0);
                } else {
                    if (Character.isLetter(keyCode) && isCaps) //check if the keyCode is a valid letter and check if the keyboard is in Caps-layout
                    {
                        keyCode = Character.toUpperCase(keyCode); // set keyValue to UpperCase
                    }
                    if(mToolbar.searchBoxFocused()){
                        mToolbar.appendText(String.valueOf(keyCode));
                        return;
                    }
                    inputConnection.commitText(String.valueOf(keyCode), 1); //Commit value from keyCode to the text box and set the cursor 1 position to the right

                    if (isCaps) //switch back the keyboard to the lowercase letter-layout
                    {
                        defaultKeyboard.setShifted(false);
                        keyboardView.invalidateAllKeys();
                        isCaps = false;
                    }
                }

        }
    }

    // method checking if the Google voice input is installed and returning its Id
    private String voiceExists(InputMethodManager imeManager) {
        List<InputMethodInfo> list = imeManager.getInputMethodList();
        for (InputMethodInfo el : list) {
            // return the id of the Google voice input input method
            // in this case "com.google.android.googlequicksearchbox"
            String id = el.getId();
            if (id.contains("com.google.android.voicesearch")) {
                return id;
            }
        }
        return null;
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {

    }

    // handle updates to preferences
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals("layouts")) {
            switch(prefs.getString("layouts", "qwert")) { // Initialize keyboard with abcde or qwert layout
            case "abcde":
                defaultKeyboard = new Keyboard(this, R.xml.alphabet_letter_keyboard);
                break;
            case "qwert":
                defaultKeyboard = new Keyboard(this, R.xml.typewriter_letter_keyboard);
                break;
        }
        keyboardView.setKeyboard(defaultKeyboard);
        setInputView(this.onCreateInputView());
        }
    }

    @Override
    public View onCreateCandidatesView() {
        mToolbar=(Toolbar) View.inflate(getApplicationContext(),R.layout.toolbar, null);

        return mToolbar;

    }

    public void showImage(Bitmap bmp, final String path){
        mCandidateImage=(CandidateImage) View.inflate(getApplicationContext(),R.layout.candidate_image, null);
        mCandidateImage.setImage(bmp);
        final String commitPath = saveImage(bmp);
        mCandidateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolBar();
                InputConnection inputConnection = getCurrentInputConnection();
                inputConnection.commitText(commitPath, 1);
            }
        });
        setCandidatesView(mCandidateImage);
    }

    public void showToolBar(){
        setCandidatesView(mToolbar);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Uri uri = (Uri)(intent.getParcelableExtra("imageUri"));
        if(uri!=null){
            try {
                InputStream image = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(image);
                showImage(bitmap,uri.toString());
            }catch (Exception e){
                Log.d("123",e.toString());
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public static String saveImage(Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getPath();
    }

    //region  Not implemented abstract methods
    @Override
    public void onPress(int i)
    {

    }

    @Override
    public void onRelease(int i)
    {

    }

    @Override
    public void onText(CharSequence charSequence)
    {

    }

    @Override
    public void swipeLeft()
    {

    }

    @Override
    public void swipeRight()
    {

    }

    @Override
    public void swipeDown()
    {

    }

    @Override
    public void swipeUp()
    {

    }
    //endregion
}
