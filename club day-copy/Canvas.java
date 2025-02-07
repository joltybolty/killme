import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import com.google.gson.*;

import java.util.stream.Collectors;

public class Canvas extends JPanel implements Runnable, KeyListener {
    private int width, height;
    private Thread thread;
    private String name;
    private boolean imageGenerated;
    private BufferedImage aiImage;
    
    public Canvas(int w, int h, JFrame frame) {
        width = w;
        height = h;
        name = "Chris";
        imageGenerated = false;
        this.addKeyListener(this);
        setFocusable(true); // Ensure panel can receive focus
        // requestFocusInWindow();
        setPreferredSize(new Dimension(width, height));
        thread = new Thread(this);
        thread.start();
    }
    
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow(); // Now the panel is displayable
    }
    
    public void run() {
        while (true) {
            update();
            try {
                Thread.sleep(17);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            repaint();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Clear the background
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        
        if (!imageGenerated) {
            drawBackground(g2);
            g2.setFont(new Font("SansSerif", Font.BOLD, 100)); // Fixed font settings
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(name)) / 2;
            int y = (getHeight() / 2) + fm.getAscent() / 2;
            g2.drawString(name, x, y);
        } else if (aiImage != null) {
            g2.drawImage(aiImage, 0, 0, width, height, null);
        }
    }
    
    private void update() { }
    
    private void drawBackground(Graphics2D g2) {
        int width = getWidth();
        int height = getHeight();
        g2.setColor(Color.WHITE);
        g2.fillRect(-10, -10, width + 20, height + 20);
        g2.setColor(Color.BLACK);
        g2.drawRect((width / 6), (height / 3), 2 * width / 3, height / 3);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (!name.isEmpty()) {
                name = name.substring(0, name.length() - 1);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            new Thread(() -> {
                String imageUrl = generateImage();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    try {
                        BufferedImage img = ImageIO.read(new URL(imageUrl));
                        SwingUtilities.invokeLater(() -> {
                            aiImage = img;
                            imageGenerated = true;
                            repaint();
                        });
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        } else {
            char c = e.getKeyChar();
            if (Character.isLetter(c)) {
                name += c;
            }
        }
    }
    
    private String generateImage() {
        String prompt = "Generate an image with a vibe that matches the name \"" + name + "\", while also including the name \"" + name + "\" as text.";
        try {
            return callOpenAIAndReturnImage(prompt);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return "";
        }
    }
    
    private String callOpenAIAndReturnImage(String prompt) throws IOException {
        String apiKey = "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";
        String openAI_URL = "https://api.openai.com/v1/images/generations";
        
        HttpURLConnection conn = (HttpURLConnection) new URL(openAI_URL).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("prompt", prompt);
        payload.put("n", 1);
        payload.put("size", "1024x1024");
        
        String json = new Gson().toJson(payload);
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                JsonObject response = JsonParser.parseReader(br).getAsJsonObject();
                JsonArray data = response.getAsJsonArray("data");
                if (data.size() > 0) {
                    return data.get(0).getAsJsonObject().get("url").getAsString();
                }
            }
        } else {
            
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                String errorMessage = errorReader.lines().collect(Collectors.joining("\n"));
                System.err.println("Error from OpenAI API (" + responseCode + "): " + errorMessage);
            }
        }
        return "";
    }
    
    @Override public void keyReleased(KeyEvent e) { }
    @Override public void keyTyped(KeyEvent e) { }
}