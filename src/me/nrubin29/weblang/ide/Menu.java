package me.nrubin29.weblang.ide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Menu extends Box {

    public Menu(final IDE ide) {
        super(BoxLayout.Y_AXIS);

        JLabel title = new JLabel("WebLang");
        JButton newFile = new JButton("New File"), openFile = new JButton("Open File");

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 64));

        newFile.setAlignmentX(Component.CENTER_ALIGNMENT);
        newFile.setBorderPainted(false);
        newFile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ide.doNewProject();
            }
        });

        openFile.setAlignmentX(Component.CENTER_ALIGNMENT);
        openFile.setBorderPainted(false);
        openFile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ide.doOpen();
            }
        });

        add(Box.createVerticalGlue());
        add(title);
        add(Box.createVerticalStrut(20));
        add(newFile);
        add(openFile);
        add(Box.createVerticalGlue());

        setOpaque(true);
        setBackground(Color.WHITE);
        setAlignmentX(JComponent.CENTER_ALIGNMENT);
        setAlignmentY(JComponent.CENTER_ALIGNMENT);
        setSize(640, 200);
        setVisible(true);
    }
}