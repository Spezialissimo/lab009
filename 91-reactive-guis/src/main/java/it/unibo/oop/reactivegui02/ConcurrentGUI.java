package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stopButton = new JButton("stop");
    private final JButton upButton = new JButton("up");
    private final JButton downButton = new JButton("down");
    /**
     * Builds a new CGUI.
     */
    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final JPanel panel = new JPanel();

        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        panel.add(display);
        panel.add(upButton);
        panel.add(downButton);
        panel.add(stopButton);
        
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();
        stopButton.addActionListener((e) -> agent.stopCounting());
        downButton.addActionListener((e) -> agent.downCounting());
        upButton.addActionListener((e) -> agent.upCounting());
    }

    /*
     * The counter agent is implemented as a nested class. This makes it
     * invisible outside and encapsulated.
     */
    private class Agent implements Runnable {
    
        private volatile boolean stop;
        private volatile boolean countUpwards = true;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {                                        
                    if (countUpwards) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    /*
                     * This is just a stack trace print, in a real program there
                     * should be some logging and decent error reporting
                     */
                    ex.printStackTrace();
                }
            }
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
            ConcurrentGUI.this.stopButton.setEnabled(false);
            ConcurrentGUI.this.upButton.setEnabled(false);
            ConcurrentGUI.this.downButton.setEnabled(false);
        }

        public void upCounting() {
            this.countUpwards = true;
        }
        
        public void downCounting() {
            this.countUpwards = false;
        }
    }
}
