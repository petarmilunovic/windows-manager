package frontend;

import backend.WindowManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import static backend.ShortcutManager.*;
import static backend.ShortcutManager.savedShortcuts;

public class ShortcutFrame extends JFrame {

    public static String selectedShortcutPosition;
    private static JPanel shortcutPanel;
    private static JLabel lbTopLeftShortcut, lbTopRightShortcut, lbBottomLeftShortcut, lbBottomRightShortcut,
            lbTopShortcut, lbMiddleShortcut, lbBottomShortcut;
    private static JButton submitButton;
    private boolean isDuplicateWarningShown = false;
    private static TrayIcon trayIcon;

    /**
     * GUI
     */
    public ShortcutFrame() {

        this.setTitle("Shortcut configurator");

        Image icon = Toolkit.getDefaultToolkit().getImage("appicon.png");
        this.setIconImage(icon);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        this.setSize(new Dimension(457, 298));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                /*
                 * Resets the labels to display only the saved shortcuts when the window is reopened.
                 * This ensures that any unsaved shortcut changes made by the user are not displayed
                 * the next time the window is opened.
                 */
                createOrUpdateLabel("top_left", lbTopLeftShortcut, "Top left → ");
                createOrUpdateLabel("top_right", lbTopRightShortcut, "Top right → ");
                createOrUpdateLabel("bottom_left", lbBottomLeftShortcut, "Bottom left → ");
                createOrUpdateLabel("bottom_right", lbBottomRightShortcut, "Bottom right → ");
                createOrUpdateLabel("top", lbTopShortcut, "Top → ");
                createOrUpdateLabel("middle", lbMiddleShortcut, "Middle → ");
                createOrUpdateLabel("bottom", lbBottomShortcut, "Bottom → ");
            }
        });

        loadingTrayIcon(); // initializes and adds the application tray icon to the system tray

        // additional text to provide context of the window
        JLabel lbAddText = new JLabel("<html>" +
                "Configure shortcuts for your preferred monitor orientation. Some " + "<br>" +
                "shortcuts are recommended for specific orientations, but they are still fully " + "<br>" +
                "functional in any setup. " +
                "</html>");
        this.add(lbAddText);

        // label asking user to select monitor orientation
        JLabel lbMonitorOrientation = new JLabel("Select monitor orientation:");
        this.add(lbMonitorOrientation);


        Choice choiceMonitorPosition = new Choice();
        choiceMonitorPosition.setPreferredSize(new Dimension(115, 15));
        // options for monitor orientations
        choiceMonitorPosition.add("Horizontal");
        choiceMonitorPosition.add("Vertical");

        // listener which captures chosen option and loads new components
        choiceMonitorPosition.addItemListener(e -> {
            shortcutPanel.removeAll(); // deletes all current components from the panel

            switch ((String) e.getItem()) {
                case "Horizontal" -> horizontalComponents();
                case "Vertical" -> verticalComponents();
            }

            shortcutPanel.revalidate();
            shortcutPanel.repaint();
        });
        this.add(choiceMonitorPosition);

        // shortcut panel & its components
        shortcutPanel = new JPanel(new GridBagLayout());
        shortcutPanel.setSize(440, 230);
        this.add(shortcutPanel);

        // display horizontal components as soon as the shortcut configurator window is opened
        horizontalComponents();

        /*
         * Monitors key presses and releases, storing keys in a Set to ensure uniqueness and order.
         * If the shortcut is already in use, shows an error and resets the input.
         * Otherwise, saves the shortcut and updates the label.
         */
        this.addKeyListener(new KeyAdapter() {
            private final Set<Integer> pressedKeys = new LinkedHashSet<>();

            @Override
            public void keyPressed(KeyEvent e) {

                if (selectedShortcutPosition != null) {
                    pressedKeys.add(e.getKeyCode()); // add pressed key into the set
                    isDuplicateWarningShown = false; // reset flag when starting a new shortcut input
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

                if (selectedShortcutPosition != null) {

                    // converting set to list, so it can be used by other method //todo maybe update other methods to accept set?
                    List<Integer> shortcut = new ArrayList<>(pressedKeys);

                    if (isShortcutAlreadyUsed(shortcut)) {
                        // edge case where the error dialog is being triggered repeatedly due to multiple keyReleased events firing in quick succession
                        if (!isDuplicateWarningShown) {
                            JOptionPane.showMessageDialog(null, "This shortcut is already in use. Please choose a different one.",
                                    "Error", JOptionPane.ERROR_MESSAGE);

                            isDuplicateWarningShown = true;
                        }
                        // clear set and reset selected position
                        pressedKeys.clear();
                        selectedShortcutPosition = null;
                        requestFocusInWindow();
                        return;
                    }

                    savedShortcuts.put(selectedShortcutPosition, shortcut); // saves a shortcut
                    updateShortcutLabel(selectedShortcutPosition, shortcut); // updates label text

                    // clear set and reset selected position
                    pressedKeys.clear();
                    selectedShortcutPosition = null;
                    isDuplicateWarningShown = false;
                    requestFocusInWindow();
                }
            }
        });
    }

    /**
     * Initializes and adds components (labels, buttons) to the `shortcutPanel` for configuring shortcuts
     * on a horizontally oriented monitor.
     */
    public void horizontalComponents() {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;

        JLabel lbCurrentShortcuts = new JLabel("Current shortcuts (recommended for horizontal screens):");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // label spans across two columns
        gbc.insets = new Insets(0 ,0, 17, 0); // bottom inset for the new components
        shortcutPanel.add(lbCurrentShortcuts, gbc);

        // initialize labels and buttons
        lbTopLeftShortcut = createOrUpdateLabel("top_left", null, "Top left → ");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // reset span for new components
        gbc.insets = new Insets(0, 0, 0, 35); // reset bottom and add right inset for the new components
        shortcutPanel.add(lbTopLeftShortcut, gbc);

        JButton btnChangeTopLeftShortcut = new JButton("Change shortcut");
        gbc.gridx = 1;
        gbc.gridy = 1;
        shortcutPanel.add(btnChangeTopLeftShortcut, gbc);

        lbTopRightShortcut = createOrUpdateLabel("top_right", null, "Top right → ");
        gbc.gridx = 0;
        gbc.gridy = 2;
        shortcutPanel.add(lbTopRightShortcut, gbc);

        JButton btnChangeTopRightShortcut = new JButton("Change shortcut");
        gbc.gridx = 1;
        gbc.gridy = 2;
        shortcutPanel.add(btnChangeTopRightShortcut, gbc);

        lbBottomLeftShortcut = createOrUpdateLabel("bottom_left", null, "Bottom left → ");
        gbc.gridx = 0;
        gbc.gridy = 3;
        shortcutPanel.add(lbBottomLeftShortcut, gbc);

        JButton btnChangeBottomLeftShortcut = new JButton("Change shortcut");
        gbc.gridx = 1;
        gbc.gridy = 3;
        shortcutPanel.add(btnChangeBottomLeftShortcut, gbc);

        lbBottomRightShortcut = createOrUpdateLabel("bottom_right", null, "Bottom right → ");
        gbc.gridx = 0;
        gbc.gridy = 4;
        shortcutPanel.add(lbBottomRightShortcut, gbc);

        JButton btnChangeBottomRightShortcut = new JButton("Change shortcut");
        gbc.gridx = 1;
        gbc.gridy = 4;
        shortcutPanel.add(btnChangeBottomRightShortcut, gbc);

        submitButton = new JButton("Submit");
        gbc.gridx = 0;
        gbc.gridy = 5;
        shortcutPanel.add(submitButton, gbc);

        // add action listeners to buttons
        btnChangeTopLeftShortcut.addActionListener(e -> selectedShortcutPosition("top_left"));
        btnChangeTopRightShortcut.addActionListener(e -> selectedShortcutPosition("top_right"));
        btnChangeBottomLeftShortcut.addActionListener(e -> selectedShortcutPosition("bottom_left"));
        btnChangeBottomRightShortcut.addActionListener(e -> selectedShortcutPosition("bottom_right"));
        submitButton.addActionListener(e -> {
            saveShortcuts(); // persist the saved shortcuts to file
            loadShortcuts(); // reload saved shortcuts to update the UI if necessary
            this.dispose(); // close the configuration window
        });
    }

    /**
     * Initializes and adds components (labels, buttons) to the `shortcutPanel` for configuring shortcuts
     * on a vertically oriented monitor.
     */
    public void verticalComponents() {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;

        JLabel lbCurrentShortcuts = new JLabel("Current shortcuts (recommended for vertical screens):");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // label spans across two columns
        gbc.insets = new Insets(0 ,0, 17, 0); // bottom inset for the new components
        shortcutPanel.add(lbCurrentShortcuts, gbc);

        // initialize labels and buttons
        lbTopShortcut = createOrUpdateLabel("top", null, "Top → ");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // reset span for new components
        gbc.insets = new Insets(0, 0, 0, 35); // reset bottom and add right inset for the new components
        shortcutPanel.add(lbTopShortcut, gbc);

        JButton btnChangeTopShortcut = new JButton("Change shortcut");
        gbc.gridx = 1;
        gbc.gridy = 1;
        shortcutPanel.add(btnChangeTopShortcut, gbc);

        lbMiddleShortcut = createOrUpdateLabel("middle", null, "Middle → ");
        gbc.gridx = 0;
        gbc.gridy = 2;
        shortcutPanel.add(lbMiddleShortcut, gbc);

        JButton btnChangeMiddleShortcut = new JButton("Change shortcut");
        gbc.gridx = 1;
        gbc.gridy = 2;
        shortcutPanel.add(btnChangeMiddleShortcut, gbc);

        lbBottomShortcut = createOrUpdateLabel("bottom", null, "Bottom → ");
        gbc.gridx = 0;
        gbc.gridy = 3;
        shortcutPanel.add(lbBottomShortcut, gbc);

        JButton btnChangeBottomShortcut = new JButton("Change shortcut");
        gbc.gridx = 1;
        gbc.gridy = 3;
        shortcutPanel.add(btnChangeBottomShortcut, gbc);

        submitButton = new JButton("Submit");
        gbc.insets = new Insets(26, 0, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 4;
        shortcutPanel.add(submitButton, gbc);

        // add action listeners to buttons
        btnChangeTopShortcut.addActionListener(e -> selectedShortcutPosition("top"));
        btnChangeMiddleShortcut.addActionListener(e -> selectedShortcutPosition("middle"));
        btnChangeBottomShortcut.addActionListener(e -> selectedShortcutPosition("bottom"));
        submitButton.addActionListener(e -> {
            saveShortcuts(); // persist the saved shortcuts to file
            loadShortcuts(); // reload saved shortcuts to update the UI if necessary
            this.dispose(); // close the configuration window
        });

    }

    /**
     * Used by button action listeners to update the selectedShortcutPosition attribute
     * with the specified position. Updated attribute is then used later for
     * displaying and saving shortcuts specific to that position.
     */
    private void selectedShortcutPosition(String position) {
        selectedShortcutPosition = position;
        this.requestFocusInWindow();
    }

    /**
     * Updates the label text to display the current shortcut for the specified position in real-time.
     * This method is used during a key release event to show the shortcut that the user
     * has set for a particular position.
     */
    private void updateShortcutLabel(String position, List<Integer> keyCodes) {

        String chosenShortcut = keyCodes.stream()
                .map(KeyEvent::getKeyText)
                .collect(Collectors.joining(" + "));

        switch (position) {
            case "top_left":
                lbTopLeftShortcut.setText("Top left → " + chosenShortcut);
                break;
            case "top_right":
                lbTopRightShortcut.setText("Top right → " + chosenShortcut);
                break;
            case "bottom_left":
                lbBottomLeftShortcut.setText("Bottom left → " + chosenShortcut);
                break;
            case "bottom_right":
                lbBottomRightShortcut.setText("Bottom right → " + chosenShortcut);
                break;
            case "top":
                lbTopShortcut.setText("Top → " + chosenShortcut);
                break;
            case "middle":
                lbMiddleShortcut.setText("Middle → " + chosenShortcut);
                break;
            case "bottom":
                lbBottomShortcut.setText("Bottom → " + chosenShortcut);
                break;
        }
    }

    /**
     * Creates new JLabel or updates an existing one to display the current saved shortcut
     * associated with the specified position.
     * If the "label" parameter is null, a new JLabel is created, if non-null "label" parameter is
     * provided, it updates the text of the existing label to reflect the current saved shortcut.
     */
    private JLabel createOrUpdateLabel(String position, JLabel label, String prefix) {

        List<Integer> currentShortcutKeyCodes = savedShortcuts.getOrDefault(position, Collections.emptyList());

        String shortcut = currentShortcutKeyCodes.isEmpty() ?
                "Not set" : currentShortcutKeyCodes.stream()
                .map(KeyEvent::getKeyText)
                .collect(Collectors.joining(" + "));

        String labelText = prefix + shortcut;

        return label == null ? new JLabel(labelText) : label;
    }

    /**
     * Initializes and adds the application tray icon to the system tray
     */
    public void loadingTrayIcon() {

        PopupMenu popup = getPopupMenu();
        this.add(popup);

        // loads an image for the tray icon
        Image trayIconImage = Toolkit.getDefaultToolkit().getImage("appicon.png");

        // configures the tray icon
        trayIcon = new TrayIcon(trayIconImage, "Windows manager", popup);
        trayIcon.setImageAutoSize(true);

        // adds the tray icon to the system tray
        try {
            SystemTray tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
        } catch (AWTException err) {
            System.err.println("TrayIcon could not be added.");
        }
    }

    /**
     * Creates and configures a popup menu for the system tray icon
     */
    private PopupMenu getPopupMenu() {
        PopupMenu popup = new PopupMenu();

        // displays the home window and brings it to the front
        MenuItem openWindowsManager = new MenuItem("Show");
        openWindowsManager.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                this.setVisible(true);
                this.toFront();
            });
        });
        popup.add(openWindowsManager);

        // opens a dialog with information about the application
        MenuItem aboutWindowsManager = new MenuItem("About");
        aboutWindowsManager.addActionListener(e -> new AboutFrame());
        popup.add(aboutWindowsManager);

        // pauses the application
        CheckboxMenuItem pauseWindowsManager = new CheckboxMenuItem("Pause");
        pauseWindowsManager.addItemListener(e -> {
            boolean isSelected = pauseWindowsManager.getState();
            WindowManager.setPaused(isSelected);
        });
        popup.add(pauseWindowsManager);

        // removes the tray icon from the system tray and exits the application
        MenuItem exitWindowsManager = new MenuItem("Exit");
        exitWindowsManager.addActionListener(e -> {
            SystemTray.getSystemTray().remove(trayIcon);
            System.exit(0);
        });
        popup.addSeparator();
        popup.add(exitWindowsManager);
        return popup;
    }

}