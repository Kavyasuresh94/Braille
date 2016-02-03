//Segmentation

import java.io.*; //for File
import java.awt.image.BufferedImage; //for BufferedImage
import javax.imageio.ImageIO; //for ImageIO
import java.awt.Color;
import java.awt.image.DataBufferByte;
import java.util.Arrays;

class colorTest{
	public static void main(String[] args) throws Exception{
		File e = new File("f.jpg");
		System.out.println("File name: " + e);
		BufferedImage bi = ImageIO.read(e);
		
		int w = bi.getWidth();
		int h = bi.getHeight();
		int[][] r = new int[w][h];
		int[][] g = new int[w][h];
		int[][] b = new int[w][h];
		int[][] rgb = new int[w][h];
		int[][] output = new int[w][h];
		double y;
		
		
		for(int i=0; i<w; i++){
			for(int j=0; j<h; j++){
				int clr = bi.getRGB(i,j);
				rgb[i][j] = clr;
				r[i][j] = (clr & 0x00ff0000) >> 16;
				g[i][j] = (clr & 0x0000ff00) >> 8;
				b[i][j] = (clr & 0x000000ff);
				
				//y = 128 + (int)((0.5*r[i][j] - 0.418688*g[i][j] - 0.081312*b[i][j]));
				y = (int)((0.299*r[i][j]) + (0.587*g[i][j]) + (0.114*b[i][j])); //makes r g b values same
				//System.out.print(y + " "); //printing y values before shifting
				//y = (y << 8)|y;
				//y = (y << 8)|y;
				
				if(y>105 && y<128){
					output[i][j] = 0xffffff; // white
				}
				else
					output[i][j] = 0x000000; // black
				
				//bi.setRGB(i,j,output[i][j]);
			}
			
		}
		
		BufferedImage seg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		
		for(int i=0; i<w; i++){
				for(int j=0; j<h; j++){
					int x = output[i][j]; // this array is used for segmentation(turning it b&w)
					seg.setRGB(i,j,x);
				}
			}
		
		File o = new File("ftest.jpg");
		try{
			ImageIO.write(seg,"jpg",o); // renderedImage, format, fileObject
		}
		catch(IOException ex){
			ex.printStackTrace(); 
		}
		
		
		// erosion starts
		int[][] copyOutput = new int[w][h]; // new array for erosion
		for(int i=0; i<w; i++){
			for(int j=0; j<h; j++){
				copyOutput[i][j] = output[i][j];
			}
		}
		
		int[][] new1Output = new int[w][h];
		for(int i=1; i<w-1; i++){
			for(int j=1; j<h-1; j++){
				if(copyOutput[i-1][j-1]==copyOutput[i][j] && copyOutput[i-1][j]==copyOutput[i][j] && copyOutput[i-1][j+1]==copyOutput[i][j] && copyOutput[i][j-1]==copyOutput[i][j] && copyOutput[i][j+1]==copyOutput[i][j] && copyOutput[i+1][j-1]==copyOutput[i][j] && copyOutput[i+1][j]==copyOutput[i][j] && copyOutput[i+1][j+1]==copyOutput[i][j]){
					new1Output[i][j] = copyOutput[i][j];	
				} 
				else {
					new1Output[i][j] = 0x000000; // value for black, rgb integer value
				}
			}
		}

		int[][] newOutput = new int[w][h];
		for(int i=1; i<w-1; i++){
			for(int j=1; j<h-1; j++){
				if(new1Output[i-1][j-1]==copyOutput[i][j] && new1Output[i-1][j]==copyOutput[i][j] && new1Output[i-1][j+1]==copyOutput[i][j] && new1Output[i][j-1]==copyOutput[i][j] && new1Output[i][j+1]==copyOutput[i][j] && new1Output[i+1][j-1]==copyOutput[i][j] && new1Output[i+1][j]==copyOutput[i][j] && new1Output[i+1][j+1]==copyOutput[i][j]){
					newOutput[i][j] = new1Output[i][j];	
				} 
				else {
					newOutput[i][j] = 0x000000; // value for black, rgb integer value
				}
			}
		}
		
		BufferedImage ero = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		
		for(int i=0; i<w; i++){
				for(int j=0; j<h; j++){
					int x = newOutput[i][j];
					ero.setRGB(i,j,x);
				}
			}
		
		File u = new File("ferosion.jpg");
		try{
			ImageIO.write(ero,"jpg",u); 
		}
		catch(IOException ex){
			ex.printStackTrace(); 
		}
		//erosion ends
		
		//dilation starts
		int[][] dilOutput = new int[w][h];
		for(int i=1; i<w-1; i++){
			for(int j=1; j<h-1; j++){
				if((newOutput[i-1][j-1]== 16777215) && (newOutput[i-1][j]== 16777215) && (newOutput[i-1][j+1]== 16777215) && (newOutput[i][j-1]== 16777215) && (newOutput[i][j+1]==16777215) && (newOutput[i+1][j-1]== 16777215) && (newOutput[i+1][j]== 16777215) && (newOutput[i+1][j+1]== 16777215)){
					dilOutput[i][j] = 16777215;	
				} 
				else {
					dilOutput[i][j] = 0x000000; // value for black, rgb integer value
				}
			}
		} 
	
	
		int[][] dil1Output = new int[w][h];
		for(int i=1; i<w-1; i++){
			for(int j=1; j<h-1; j++){
				if((dilOutput[i-1][j-1]== 16777215) && (dilOutput[i-1][j]== 16777215) && (dilOutput[i-1][j+1]== 16777215) && (dilOutput[i][j-1]== 16777215) && (dilOutput[i][j+1]==16777215) && (dilOutput[i+1][j-1]== 16777215) && (dilOutput[i+1][j]== 16777215) && (dilOutput[i+1][j+1]== 16777215)){
					dil1Output[i][j] = 16777215;	
				} 
				else {
					dil1Output[i][j] = 0x000000; // value for black, rgb integer value
				}
			}
		}

		/* int[][] dil2Output = new int[w][h];
		for(int i=1; i<w-1; i++){
			for(int j=1; j<h-1; j++){
				if((dil1Output[i-1][j-1]== 16777215) && (dil1Output[i-1][j]== 16777215) && (dil1Output[i-1][j+1]== 16777215) && (dil1Output[i][j-1]== 16777215) && (dil1Output[i][j+1]==16777215) && (dil1Output[i+1][j-1]== 16777215) && (dil1Output[i+1][j]== 16777215) && (dil1Output[i+1][j+1]== 16777215)){
					dil2Output[i][j] = 16777215;	
				} 
				else {
					dil2Output[i][j] = 0x000000; // value for black, rgb integer value
				}
			}
		}  */
		
		/* int[][] dil3Output = new int[w][h];
		for(int i=1; i<w-1; i++){
			for(int j=1; j<h-1; j++){
				if((dil2Output[i-1][j-1]== 16777215) && (dil2Output[i-1][j]== 16777215) && (dil2Output[i-1][j+1]== 16777215) && (dil2Output[i][j-1]== 16777215) && (dil2Output[i][j+1]==16777215) && (dil2Output[i+1][j-1]== 16777215) && (dil2Output[i+1][j]== 16777215) && (dil2Output[i+1][j+1]== 16777215)){
					dil3Output[i][j] = 16777215;	
				} 
				else {
					dil3Output[i][j] = 0x000000; // value for black, rgb integer value
				}
			}
		}  */
		
		BufferedImage dil = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		
		for(int i=0; i<w; i++){
				for(int j=0; j<h; j++){
					int x = dil1Output[i][j];
					dil.setRGB(i,j,x);
				}
			}
		
		File a = new File("fdilation.jpg");
		try{
			ImageIO.write(dil,"jpg",a); 
		}
		catch(IOException ex){
			ex.printStackTrace(); 
		}
		// dilation ends
	}
}