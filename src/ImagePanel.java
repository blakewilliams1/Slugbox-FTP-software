import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private BufferedImage unscaledImage;
    private Image scaledImage;
    public ImagePanel(int width, int height) {
       try {                
    	   InputStream stream = getClass().getResourceAsStream("/images/ucsc.jpg");
          unscaledImage = ImageIO.read(stream);
          scaledImage = unscaledImage.getScaledInstance(width, height, ALLBITS);
       } catch (IOException ex) {
    	   ex.printStackTrace();
       }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(scaledImage, 0, 0, null);      
    }

}