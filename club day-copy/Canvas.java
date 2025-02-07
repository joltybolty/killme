import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.net.*;
import java.awt.Image.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;

public class Canvas extends JPanel implements Runnable, KeyListener
{
    private int width, height;
    private Thread thread;
    private String name;
    private boolean imageGenerated;
    private String url;
    private URL image;
    private BufferedImage aiImage;
    
    public Canvas(int w, int h, JFrame frame)
    {
        width = w;
        height = h;
        name = "Christine";
        url = "";
        imageGenerated = false;
        frame.addKeyListener(this);
        thread = new Thread(this);
        thread.start();
    }
    
    public void run()
    {
        while(true)
        {
            update();
            try
            {
                thread.sleep(17);
            }
            catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }
            repaint();
        }
    }
    
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        if (!imageGenerated)
        {
            drawBackground(g2);
            g2.setFont(new Font("BOLD", 200, 200));
            g2.drawString(name, (width / 2) - (width / 3) + 20, (height / 2) - (height / 75));
        }
        else if (url.length() > 0)
        {
            try
            {
                image = new URL(url);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            try
            {
                aiImage = ImageIO.read(image);
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
            g2.drawImage(aiImage, 0, 0, width, height, null);
        }
    }
    
    private void update()
    {
        
    }
    
    private void drawBackground(Graphics2D g2)
    {
        Rectangle2D.Double r = new Rectangle2D.Double(-10, -10, width + 20, height + 20);
        Rectangle2D.Double tBox = new Rectangle2D.Double((width / 2) - (width / 3), (height / 2) - (height / 6), width / 1.5, height / 5);
        g2.setColor(Color.WHITE);
        g2.fill(r);
        g2.setColor(Color.BLACK);
        g2.draw(tBox);
    }
    
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
        {
            if (name.length() > 0)
            {
                name = name.substring(0, name.length() - 1);
            }
        }
        else if (e.getKeyCode() >= KeyEvent.VK_A && e.getKeyCode() <= KeyEvent.VK_Z)
        {
            char letter = (char)e.getKeyCode();
            name += letter;
        }
        else if (e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            url = generateImage();
        }
    }
    
    public String generateImage()
    {
        String imageURL = "";
        String prompt = "generate a background for the name " + name;
        try
        {
            imageURL = callOpenAIAndReturnImage(prompt);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        return imageURL;
    }
    
    
    private String callOpenAIAndReturnImage(String prompt) throws IOException
    {
        String apiKey = "";
        String openAI_URL = "https://api.openai.com/v1/images/generations";
        Map<String, Object> payload = new HashMap<>();
        payload.put("prompt", prompt);
        payload.put("n", 1);
        payload.put("size", "1024x1024");
        String jsonPayload = new com.google.gson.Gson().toJson(payload);
        
    }
    
    public void keyReleased(KeyEvent e)
    {
    }
    public void keyTyped(KeyEvent e)
    {
    }
}