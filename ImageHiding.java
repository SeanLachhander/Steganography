/* Sean Lachhander
 * */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;


import java.io.*;

import javax.imageio.ImageIO;

import javax.swing.*;

public class ImageHiding extends JFrame implements ActionListener
{
 BufferedImage hostImage;
 //BufferedImage secretImage;

 JPanel controlPanel;
 JPanel imagePanel;

 
 JTextField encodeBitsText;
 JButton encodeBitsPlus;
 JButton encodeBitsMinus;
 
 JTextField noChars;

 JTextField nBitsText;
 JButton nBitsPlus;
 JButton nBitsMinus;

 ImageCanvas hostCanvas;
 ImageCanvas secretCanvas;

 Steganography s;

 public BufferedImage getHostImage()
 {
  BufferedImage img = null;

  try
  {
   img = ImageIO.read(new File("host_image.jpg"));
  }
  catch (IOException ioe) { ioe.printStackTrace(); }

  return img;
 }

 /*public BufferedImage getSecretImage()      // Commented out because do not need the secret image.
 {
  BufferedImage img = null;

  try
  {
   img = ImageIO.read(new File("secret_image.jpg"));
  }
  catch (IOException ioe) { ioe.printStackTrace(); }

  return img;
 } */
 
 
 public byte[] getTextBytes()    // Function I implemented into ImageHiding.java
 {
   byte[] bFile = null;   // null byte array
   try
   {
     File file = new File("text.txt"); // text file store in file
     // BufferedReader reader = new BufferedReader(new FileReader(file));
     FileInputStream FIS = null;    
     bFile = new byte[(int)file.length()];   // bFile byte length of txt file
     FIS = new FileInputStream(file); // in fileinputstream
     FIS.read(bFile); // read
     FIS.close();    // close file
   }
   
   catch (IOException ioe) 
   { 
     ioe.printStackTrace(); 
   }
   
   return bFile;
 }
 

 public int getBits()
 {
  return Integer.parseInt(encodeBitsText.getText());
 }
 
 
 
 public void actionPerformed(ActionEvent event)
 {
  Object source = event.getSource();

  if (source == encodeBitsPlus)
  {
   int bits = this.getBits() + 1;

   if (bits > 8) { bits = 8; }

   encodeBitsText.setText(Integer.toString(bits));
   
   int width = this.getHostImage().getWidth();
   int height = this.getHostImage().getHeight();
   
   
   noChars.setText(Integer.toString((bits*(3*width*height))/8));  // number of characters to string
   

   s = new Steganography(this.getHostImage());
   s.encode(this.getTextBytes(), bits);

   hostCanvas.setImage(s.getImage());
   hostCanvas.repaint();

 //  s = new Steganography(this.getSecretImage());
 //  s.getMaskedImage(bits);

 //  secretCanvas.setImage(s.getImage());
//   secretCanvas.repaint();
  }
  else if (source == encodeBitsMinus)
  {
    
    
   int width = this.getHostImage().getWidth();
   int height = this.getHostImage().getHeight();
   
   int bits = this.getBits() - 1;

   if (bits < 0) { bits = 0; }
   
   noChars.setText(Integer.toString((bits*(3*width*height))/8));

   encodeBitsText.setText(Integer.toString(bits));

   s = new Steganography(this.getHostImage());
   s.encode(this.getTextBytes(), bits);

   hostCanvas.setImage(s.getImage());
   hostCanvas.repaint();

 //  s = new Steganography(this.getSecretImage());
 //  s.getMaskedImage(bits);

 //  secretCanvas.setImage(s.getImage());
 //  secretCanvas.repaint();
  }
 }

 public ImageHiding()
 {
  GridBagLayout layout = new GridBagLayout();
  GridBagConstraints gbc = new GridBagConstraints();
  this.setTitle("Image Hiding Demo");

  Container container = this.getContentPane();

  this.setLayout(layout);

  this.add(new JLabel("Bits to encode into host image:"));

  encodeBitsText = new JTextField("0", 5);
  encodeBitsText.setEditable(false);
  
  noChars = new JTextField("0", 5);
  noChars.setEditable(false);
  

  gbc.weightx = -1.0;
  layout.setConstraints(encodeBitsText, gbc);
  this.add(encodeBitsText);
   
  gbc.weightx = 1.0;
  layout.setConstraints(noChars, gbc);
  this.add(noChars);

  encodeBitsPlus = new JButton("+");
  encodeBitsPlus.addActionListener(this);

  encodeBitsMinus = new JButton("-");
  encodeBitsMinus.addActionListener(this);

  gbc.weightx = 1.0;
  layout.setConstraints(encodeBitsPlus, gbc);
  this.add(encodeBitsPlus);

  gbc.gridwidth = GridBagConstraints.REMAINDER;
  layout.setConstraints(encodeBitsMinus, gbc);
  this.add(encodeBitsMinus);

  GridBagLayout imageGridbag = new GridBagLayout();
  GridBagConstraints imageGBC = new GridBagConstraints();

  imagePanel = new JPanel();
  imagePanel.setLayout(imageGridbag);

  JLabel hostImageLabel = new JLabel("Host image:");
  JLabel secretImageLabel = new JLabel("No. of characters: ");

  imagePanel.add(hostImageLabel);

  imageGBC.gridwidth = GridBagConstraints.REMAINDER;
  imageGridbag.setConstraints(secretImageLabel, imageGBC);
  imagePanel.add(secretImageLabel);

  hostCanvas = new ImageCanvas(this.getHostImage());  
  //secretCanvas = new ImageCanvas(this.getSecretImage());

  imagePanel.add(hostCanvas);
//  imagePanel.add(secretCanvas);

  gbc.gridwidth = GridBagConstraints.REMAINDER;
  layout.setConstraints(imagePanel, gbc);
  this.add(imagePanel);

  Steganography host = new Steganography(this.getHostImage());
  host.encode(this.getTextBytes(), this.getBits());
  hostCanvas.setImage(host.getImage());

 // Steganography secret = new Steganography(this.getSecretImage());
 // secret.getMaskedImage(this.getBits());
 // secretCanvas.setImage(secret.getImage());

  this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  this.pack();

  this.setVisible(true);
 }

 public static void main(String[] args)
 {
  ImageHiding frame = new ImageHiding();
  frame.setVisible(true);
 }

 public class ImageCanvas extends JPanel
 { 
  Image img;

  public void paintComponent(Graphics g)
  {
   g.drawImage(img, 0, 0, this);
  }

  public void setImage(Image img)
  {
   this.img = img;
  }

  public ImageCanvas(Image img)
  {
   this.img = img;

   this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
  }
 }
}

class Steganography
{
 BufferedImage image;

 public void getMaskedImage(int bits)
 {
  int[] imageRGB = image.getRGB(0, 0, image.getWidth(null), image.getHeight(null), null, 0, image.getWidth(null));

  int maskBits = (int)(Math.pow(2, bits)) - 1 << (8 - bits);
  int mask = (maskBits << 24) | (maskBits << 16) | (maskBits << 8) | maskBits;

  for (int i = 0; i < imageRGB.length; i++)
  {
   imageRGB[i] = imageRGB[i] & mask;
  }

  image.setRGB(0, 0, image.getWidth(null), image.getHeight(null), imageRGB, 0, image.getWidth(null));
 }

 public void encode(byte[] textByteArray, int encodeBits)   // encode will take in a byte array (text) instead of originally (BufferedImage)
 {
   
  int[] imageRGB = image.getRGB(0, 0, image.getWidth(null), image.getHeight(null), null, 0, image.getWidth(null));

  int encodeByteMask = (int)(Math.pow(2, encodeBits)) - 1 << (8 - encodeBits);
  int encodeMask = (encodeByteMask << 24) | (encodeByteMask << 16) | (encodeByteMask << 8) | encodeByteMask;

  int decodeByteMask = ~(encodeByteMask >>> (8 - encodeBits)) & 0xFF;
  int hostMask = (decodeByteMask << 24) | (decodeByteMask << 16) | (decodeByteMask << 8) | decodeByteMask;

  
  for (int i = 0; i < imageRGB.length; i++)
  {
    int encodeData = ((char)textByteArray[i] & encodeMask) >>> (8 - encodeBits);   // Will Encode the text byte array..
   
   imageRGB[i] = (imageRGB[i] & hostMask) | (encodeData & ~hostMask);   // stored into imageRGB[i]...
  }
  
  image.setRGB(0, 0, image.getWidth(null), image.getHeight(null), imageRGB, 0, image.getWidth(null));
 }
 
 public Image getImage()
 {
  return image;
 }

 public Steganography(BufferedImage image)
 {
  this.image = image;
 }
}
