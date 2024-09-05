package backend;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import frontend.ShortcutFrame;

//todo add more jna and jnh keycodes in keycode converter class
//todo setting a shortcut that already exists should remove the shortcut from its previous location and update it in the new location
//fixme when manipulating maximized window there is a white line above the window --- microsoft windows bug
//fixme shortcuts are saved before submit button is pressed --- 134th line in shortcutframe
//fixme subtract the taskbar size from the screen dimensions to improve window arrangement accuracy

public class Main {

    public static void main(String[] args) {

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException err) {
            err.printStackTrace();
            System.exit(1);
        }

        // registers WindowManager class to listen for global key events, regardless of which application is currently in focus
        GlobalScreen.addNativeKeyListener(new WindowManager());

        new ShortcutFrame();
    }
}
