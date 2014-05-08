package me.nrubin29.weblang.ide;

import me.nrubin29.weblang.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;

public class IDE extends JFrame {

    private final Menu menu;
    private Console console;
    private JToolBar consolePane;

    private JTextPane text;
    private File currentFile;

    public IDE() {
        super("WebLang IDE");

        add(menu = new Menu(this));

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setSize(640, 480);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setup() {
        remove(menu);

        add(text = new JTextPane());

        console = new Console();

        JScrollPane consoleScroll = new JScrollPane(console);
        consoleScroll.setBorder(null);
        consoleScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        consolePane = new JToolBar();
        consolePane.setFloatable(false);
        consolePane.setVisible(false);
        consolePane.add(consoleScroll);

        add(consolePane);

        JMenuBar menuBar = new JMenuBar();

        final JMenu
                file = new JMenu("File"),
                settings = new JMenu("Settings"),
                orientation = new JMenu("Console Orientation"),
                help = new JMenu("Help");

        final JMenuItem
                saveFile = new JMenuItem("Save"),
                run = new JMenuItem("Run"),
                closeConsole = new JMenuItem("Close Console"),
                horizontal = new JRadioButtonMenuItem("Horizontal"),
                vertical = new JRadioButtonMenuItem("Vertical"),
                gitHub = new JMenuItem("GitHub Wiki");

        menuBar.add(file);
        menuBar.add(settings);
        menuBar.add(help);

        file.add(saveFile);
        file.add(run);

        settings.add(closeConsole);
        settings.add(orientation);

        ButtonGroup orientationGroup = new ButtonGroup();
        orientationGroup.add(horizontal);
        orientationGroup.add(vertical);

        vertical.setSelected(true);

        orientation.add(vertical);
        orientation.add(horizontal);

        help.add(gitHub);

        setJMenuBar(menuBar);

        int meta = System.getProperty("os.name").startsWith("Mac") ? KeyEvent.META_DOWN_MASK : KeyEvent.CTRL_DOWN_MASK;

        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, meta));
        saveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile));

                    String[] lines = text.getText().split("\n");

                    for (int i = 0; i < lines.length; i++) {
                        writer.write(lines[i]);
                        if (i + 1 != lines.length) writer.newLine();
                    }

                    writer.close();
                } catch (Exception ex) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), new Utils.IDEException("Could not save file."));
                }
            }
        });

        run.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, meta));
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!consolePane.isVisible()) consolePane.setVisible(true);
                console.run(text.getText());
            }
        });

        closeConsole.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, meta + KeyEvent.SHIFT_DOWN_MASK));
        closeConsole.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consolePane.setVisible(false);
            }
        });

        vertical.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean visible = consolePane.isVisible();
                if (visible) consolePane.setVisible(false);
                setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
                if (visible) consolePane.setVisible(true);
            }
        });

        horizontal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean visible = consolePane.isVisible();
                if (visible) consolePane.setVisible(false);
                setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
                if (visible) consolePane.setVisible(true);
            }
        });

        gitHub.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, meta + KeyEvent.SHIFT_DOWN_MASK));
        gitHub.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://www.github.com/nrubin29/WebLang/wiki"));
                } catch (Exception ex) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), new Utils.IDEException("Could not open page."));
                }
            }
        });

        validate();
        repaint();
    }

    void doOpen() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);

        if (chooser.showOpenDialog(IDE.this) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            setup();
            text.setText(Utils.readFile(currentFile, "\n"));
        }
    }

    void doNewProject() {
        JFileChooser chooser = new JFileChooser("Choose Save Directory and Name");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showSaveDialog(IDE.this) == JFileChooser.APPROVE_OPTION) {
            File toUse = chooser.getSelectedFile();
            if (!toUse.getName().endsWith(".weblang")) {
                toUse = new File(toUse.getParentFile(), toUse.getName() + ".weblang");
            }

            try {
                if (!toUse.createNewFile()) {
                    throw new Utils.IDEException("Could not create new file!");
                }

                currentFile = toUse;
                setup();
                text.setText(Utils.readFile(toUse, "\n"));
            } catch (Exception ex) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), new Utils.IDEException("Could not create new project."));
            }
        }
    }

    public Console getConsole() {
        return console;
    }
}