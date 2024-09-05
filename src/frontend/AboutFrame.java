package frontend;

import javax.swing.*;
import java.awt.*;

public class AboutFrame extends JFrame {

    public AboutFrame() {

        this.setTitle("About");

        Image icon = Toolkit.getDefaultToolkit().getImage("appicon.png");
        this.setIconImage(icon);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(650, 245);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        aboutDetails();
        this.setVisible(true);
    }

    /**
     * Sets up and displays the content for the "About" dialog
     */
    public void aboutDetails() {

        String text = "<font face='Arial' style='font-size: 12pt;'> About Windows Manager " + "<br><br>" +
                "Windows Manager is a desktop utility that lets you manage and rearrange windows with your own customizable keyboard shortcuts." + "<br><br>" +
                "The application supports shortcuts that can be used on both horizontal and vertical screens. However, some shortcuts are recommended for specific setups: " + "<br><br>" +
                "&nbsp; - On <strong>horizontal</strong> screens it is recommended to use shortcuts for moving windows to the <strong>top left</strong>, <strong>top right</strong>, <strong>bottom left</strong> or <strong>bottom right</strong> corners" + "<br>" +
                "&nbsp; - On <strong>vertical</strong> screens it is recommended to use shortcuts for moving windows to the <strong>top</strong>, <strong>middle</strong> or <strong>bottom</strong> of the screen" + "<br><br>" +
                "For any concerns or suggestions, email petarmilunov@gmail.com </font>";

        // initialize text area
        JTextPane textArea = new JTextPane();
        textArea.setContentType("text/html");
        textArea.setText(text);
        textArea.setEditable(false);

        // add text area to the scroll pane, in case in the future there is additional text added
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 245));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        this.add(scrollPane);
    }

}
