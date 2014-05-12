package me.nrubin29.weblang.ide;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

class ActionPanel extends JPanel {

    public ActionPanel(String text) {
        JLabel label = new JLabel(text);
        add(label);

        setBorder(new LineBorder(Color.BLACK));
        setMaximumSize(new Dimension(640, 20 * text.split("<br/>").length));
    }
}