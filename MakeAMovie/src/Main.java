import java.awt.Color;

import acm.graphics.GImage;
import acm.program.GraphicsProgram;

public class Main extends GraphicsProgram {

	/**
	 * Initializer. Prints the welcome messages.
	 * Do <b>not</b> modify.
	 */
	public void init(){
		println("Welcome to Make a Movie!");
		println("Start by either creating a new project or opening an existing one.");
	}
	
	/**
	 * Entry method for your implementation.
	 */
	public void run(){

	}
	
	/**
	 * Convert a given pixel to a given color.
	 * @param pixel <code>int</code> value of a pixel.
	 * @param c A <code>Color</code> class indicating a color. 
	 * @return <code>int</code> value of the generated pixel.
	 */
	public int generatePixelFromColor(int pixel, Color c){
		if(GImage.getAlpha(pixel) > 0){
			pixel = GImage.createRGBPixel(c.getRed(), c.getGreen(), c.getBlue(), GImage.getAlpha(pixel));
		}
		return pixel;
	}
	
	/**
	 * Generate the corresponding <code>Color</code> object given a <code>String</code>.
	 * @param str <code>String</code> value representing a color.
	 * @return <code>Color</code> object of the color represented by <code>str</code>,
	 * if no corresponding color is found return <code>Color.BLACK</code>.
	 */
	public Color generateColorFromString(String str){
		Color color;
		try {
		    Field field = Color.class.getField(str);
		    color = (Color)field.get(null);
		} catch (Exception e) {
		    color = Color.BLACK;
		}
		return color;
	}
	
	/**
	 * File extension of project files.
	 */
	public static String FILE_TYPE = ".txt";
	
	/**
	 * File extension of image fles.
	 */
	public static String IMAGE_TYPE = ".png";
	
	/**
	 * Constant <code>String</code> used to prompt user for commands.
	 */
	public static String CLI_INPUT_STR = "MakeAMovie -> ";
	
}
