package backend;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static backend.ShortcutManager.loadShortcuts;
import static backend.ShortcutManager.savedShortcuts;

public class WindowManager implements NativeKeyListener {

    private static final User32 USER32 = User32.INSTANCE;
    private static final Set<Integer> pressedKeys = new HashSet<>();
    public static boolean applicationIsPaused;

    /**
     * Constructor which loads saved shortcuts
     */
    public WindowManager() {
        loadShortcuts();
    }

    /**
     * Retrieves the height of the monitor in pixels
     */
    static int getMonitorHeight() {

        WinDef.HWND currentlyFocusedWindow = USER32.GetForegroundWindow(); // retrieves the handle of the currently focused window

        if (currentlyFocusedWindow != null) {

            WinDef.RECT windowRect = new WinDef.RECT(); // stores the dimension of the currently focused window
            USER32.GetWindowRect(currentlyFocusedWindow, windowRect); // fill the RECT object with the position and size of the focused window

            WinUser.MONITORINFO monitorInfo = new WinUser.MONITORINFO(); // create an object to store information about the monitor
            // get the monitor that contains the focused window and fill the monitorInfo object with details
            USER32.GetMonitorInfo(USER32.MonitorFromRect(windowRect, WinUser.MONITOR_DEFAULTTONEAREST), monitorInfo);

            // calculate and return the height of the monitor
            return monitorInfo.rcMonitor.bottom - monitorInfo.rcMonitor.top;
        }
        return 0; // return 0 if no window is focused
    }

    /**
     * Retrieves the height of the monitor in pixels
     */
    static int getMonitorWidth() {

        WinDef.HWND currentlyFocusedWindow = USER32.GetForegroundWindow(); // retrieves the handle of the currently focused window

        if (currentlyFocusedWindow != null) {

            WinDef.RECT windowRect = new WinDef.RECT(); // stores the dimension of the currently focused window
            USER32.GetWindowRect(currentlyFocusedWindow, windowRect); // fill the RECT object with the position and size of the focused window

            WinUser.MONITORINFO monitorInfo = new WinUser.MONITORINFO(); // create an object to store information about the monitor
            // get the monitor that contains the focused window and fill the monitorInfo object with details
            USER32.GetMonitorInfo(USER32.MonitorFromRect(windowRect, WinUser.MONITOR_DEFAULTTONEAREST), monitorInfo);

            // calculate and return the height of the monitor
            return monitorInfo.rcMonitor.right - monitorInfo.rcMonitor.left;
        }
        return 0; // return 0 if no window is focused
    }

    /**
     * Handles key press events by converting the JNH key codes to a JNA key codes
     * and triggering the corresponding window movement based on saved shortcuts
     */
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {

        // if the application is paused, exit the current method without executing further logic.
        if (applicationIsPaused) {
            return;
        }

        int jnhKeyCode = e.getKeyCode();
        int jnaKeyCode = KeyCodeConverter.jnativehookToJNA.getOrDefault(jnhKeyCode, -1); // get the JNA key code using converter

        // add the key code to the pressed keys set
        pressedKeys.add(jnaKeyCode);


        // checks if pressed keys matches any saved shortcuts
        for (Map.Entry<String, List<Integer>> entry : savedShortcuts.entrySet()) {

            String position = entry.getKey();
            List<Integer> keyCodes = entry.getValue();

            // if all pressed keys match the key codes for a saved shortcut, move the window
            if (pressedKeys.containsAll(keyCodes)) {
                executeShortcutAction(position);
                return;
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {

        int jnhKeyCode = e.getKeyCode();
        int jnaKeyCode = KeyCodeConverter.jnativehookToJNA.getOrDefault(jnhKeyCode, -1);

        pressedKeys.remove(jnaKeyCode);
    }

    /**
     * Move the currently focused window to the specific position
     */
    private void executeShortcutAction(String position) {
        System.out.println("Executing action for position: " + position);

        switch (position) {
            case "top_left":
                moveWindowToPosition(0, 0, true, getMonitorHeight() / 2);
                break;
            case "top_right":
                moveWindowToPosition(getMonitorWidth() / 2, 0, true, getMonitorHeight() / 2);
                break;
            case "bottom_left":
                moveWindowToPosition(0, getMonitorHeight() / 2, true, getMonitorHeight() / 2);
                break;
            case "bottom_right":
                moveWindowToPosition(getMonitorWidth() / 2, getMonitorHeight() / 2, true, getMonitorHeight() / 2);
                break;
            case "top":
                moveWindowToPosition(0, 0, false, getMonitorHeight() / 3);
                break;
            case "middle":
                moveWindowToPosition(0, getMonitorHeight() / 3, false, getMonitorHeight() / 3);
                break;
            case "bottom":
                moveWindowToPosition(0, getMonitorHeight() * 2 / 3, false, getMonitorHeight() / 3);
                break;
        }
    }

    /**
     * Moves the currently focused window to a new position on the screen and resizes it based on the provided parameters.
     * It calculates the new position of the window relative to the top left corner of the monitor
     * containing the focused window. It also determines the new dimensions of the window, considering whether
     * the window is resizable and the type of screen (horizontal or vertical).
     */
    private void moveWindowToPosition(int windowNewXPosition, int windowNewYPosition, boolean horizontal, int height) {

        WinDef.HWND currentlyFocusedWindow = USER32.GetForegroundWindow(); // retrieves the handle of the currently focused window

        if (currentlyFocusedWindow != null) {

            WinDef.RECT windowRect = new WinDef.RECT(); // stores the dimension of the currently focused window
            USER32.GetWindowRect(currentlyFocusedWindow, windowRect); // fill the RECT object with the position and size of the focused window

            WinUser.MONITORINFO monitorInfo = new WinUser.MONITORINFO(); // create an object to store information about the focused monitor
            // get the monitor that contains the focused window and fill the monitorInfo object with details
            USER32.GetMonitorInfo(USER32.MonitorFromRect(windowRect, WinUser.MONITOR_DEFAULTTONEAREST), monitorInfo);

            // check if window is resizable
            int style = USER32.GetWindowLong(currentlyFocusedWindow, WinUser.GWL_STYLE);
            boolean isResizable = (style & WinUser.WS_SIZEBOX) != 0;

            // determine new position of the focused window relative to the monitors top left corner
            int xNewPosition = monitorInfo.rcMonitor.left + windowNewXPosition;
            int yNewPosition = monitorInfo.rcMonitor.top + windowNewYPosition;

            // calculate the width of the monitor
            int monitorWidth = monitorInfo.rcMonitor.right - monitorInfo.rcMonitor.left;

            // determine new dimensions of the focused window based on resizability and position type
            int newWidth = isResizable ? (horizontal ? monitorWidth / 2 : monitorWidth) : (windowRect.right - windowRect.left);
            int newHeight = isResizable ? height : (windowRect.bottom - windowRect.top);

            USER32.SetWindowPos(
                    currentlyFocusedWindow,
                    null,
                    xNewPosition,
                    yNewPosition,
                    newWidth,
                    newHeight,
                    User32.SWP_NOACTIVATE
            );
        }
    }

    /**
     * Toggles the paused state of the application, invoked by one of the Popup menu options
     */
    public static void setPaused(boolean paused) {
        applicationIsPaused = paused;
    }

}


