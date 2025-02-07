import javax.swing.*;
import java.awt.*;

public class Tester
{
    public static void main(String[] args)
    {
        int width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        JFrame frame = new JFrame();
        Canvas c = new Canvas(width, height, frame);
        frame.add(c);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Name Image Generator");
        frame.setVisible(true);
    }
}