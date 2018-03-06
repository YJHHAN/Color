/*
 * Java Color Thief
 * by Sven Woltmann, Fonpit AG
 * 
 * http://www.androidpit.com
 * http://www.androidpit.de
 *
 * License
 * -------
 * Creative Commons Attribution 2.5 License:
 * http://creativecommons.org/licenses/by/2.5/
 *
 * Thanks
 * ------
 * Lokesh Dhakar - for the original Color Thief JavaScript version
 * available at http://lokeshdhakar.com/projects/color-thief/
 */

import de.androidpit.colorthief.ColorThief;
import de.androidpit.colorthief.MMCQ.CMap;
import de.androidpit.colorthief.MMCQ.VBox;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ColorThiefTest {



    public static void main(String[] args) throws IOException {
        //String path = "examples-test.html";

        ColorThiefTest test = new ColorThiefTest();

        //test.test("examples/img/photo1.jpg");
        //test.test("examples/img/photo2.jpg");
        //test.test("examples/img/photo3.jpg");
        //test.test("examples/img/4.jpg");

        //test.saveToHTMLFile("examples/img/3.jpg");

        //test.getHEX("C:/Users/YJ/Desktop/image/target.jpg");
        test.getHEX("C:/Users/YJ/Desktop/color-thief-java-master/examples/img/photo1.jpg");

        //test.saveToHTMLFile("examples/img/4.jpg");

        System.out.println("Finished.");
    }

  /*  public int getHue(int red, int green, int blue) {

        float min = Math.min(Math.min(red, green), blue);
        float max = Math.max(Math.max(red, green), blue);

        float hue = 0f;
        if (max == red) {
            hue = (green - blue) / (max - min);

        } else if (max == green) {
            hue = 2f + (blue - red) / (max - min);

        } else {
            hue = 4f + (red - green) / (max - min);
        }

        hue = hue * 60;
        if (hue < 0) hue = hue + 360;

        return Math.round(hue);
    } */




    private StringBuilder sb;

    private ColorThiefTest() {
        sb = new StringBuilder();
        printStyleHeader();
    }



    //추가시작

    private static Color brightness(Color c, double scale) {
        int r = Math.min(255, (int) (c.getRed() * scale));
        int g = Math.min(255, (int) (c.getGreen() * scale));
        int b = Math.min(255, (int) (c.getBlue() * scale));
        return new Color(r,g,b);
    }

    //추가시작

    private int Volume(VBox vbox){

        int volume;
        volume = vbox.volume(false);
        return volume;

    }
    private int Count(VBox vbox){

        int count;
        count = vbox.count(false);
        return count;

    }
    private float[] Percentage(long[] multiply){
        int length = multiply.length;
        long temp;
        long sum=0;
        float[] result = new float[length];

        for(int i=0; i<length;i++){
            temp = multiply[i];
            sum= temp + sum;
        }
        for(int i=0; i<length;i++){
            result[i]= (((multiply[i])/(float)sum))*100;
        }

        return result;

    }

    /*private int[] calc (BufferedImage img1){
        int[] getter = ColorThief.getColor(img1);
        for(int i=0; i<getter.length; i++) {
            System.out.print(getter[i]+", ");
        }
        return getter;
    }*/


    private void getHEX(String file) throws IOException, FileNotFoundException {

        PrintWriter pw = new PrintWriter(new File("Extracting_color.csv"));
        StringBuilder sb1 = new StringBuilder();


        BufferedImage img1 = ImageIO.read(new File(file));

        int colorCount=6;

        CMap result1 = ColorThief.getColorMap(img1, colorCount);
        VBox[] Colors = new VBox[colorCount];

        //퍼센트로 추가
        VBox dominantColor = result1.vboxes.get(0);
        int[] volume = new int[colorCount];
        int[] count = new int[colorCount];
        long[] multiply = new long[colorCount];
        float[] percentResult;// = new float[colorCount];

        int[] rgb1;
        String[] rgbHexString1= new String[colorCount];

        //int[] getter = ColorThief.getColor(img1);

        //sb1.append("Type");
        //sb1.append(',');

        /*
        sb1.append("R");
        sb1.append(',');
        sb1.append("G");
        sb1.append(',');
        sb1.append("B");
        sb1.append('\n');
        */



        int[] getter = ColorThief.getColor(img1);

        sb1.append("unpredicted");
        sb1.append(',');
        sb1.append(getter[0]);
        sb1.append(',');
        sb1.append(getter[1]);
        sb1.append(',');
        sb1.append(getter[2]);
        sb1.append('\n');





        /*
        sb1.append("1st_RGBcode");
        sb1.append(',');
        sb1.append("1st_percentage");
        sb1.append(',');
        sb1.append("2nd_RGBcode");
        sb1.append(',');
        sb1.append("2nd_percentage");
        sb1.append(',');
        sb1.append("3rd_RGBcode");
        sb1.append(',');
        sb1.append("3rd_percentage");
        sb1.append(',');
        sb1.append("4th_RGBcode");
        sb1.append(',');
        sb1.append("4th_percentage");
        sb1.append(',');
        sb1.append("5th_RGBcode");
        sb1.append(',');
        sb1.append("5th_percentage");
        sb1.append('\n');
        */

        int k=0;
        for (VBox vbox : result1.vboxes) {
            volume[k]=Volume(vbox);
            count[k]=Count(vbox);
            multiply[k] = volume[k]*count[k];
            k++;
        }


           percentResult= Percentage(multiply);


        for(int j=0; j<colorCount-1; j++) {
            Colors[j] = result1.vboxes.get(j);
            rgb1 = Colors[j].avg(false);
            rgbHexString1[j] = createRGBHexString(rgb1);

            System.out.print(rgbHexString1[j] +"     ");

            sb1.append(rgbHexString1[j]);
            sb1.append(',');
            sb1.append(percentResult[j]);
            sb1.append(',');

        }

            System.out.println();
           // sb1.append('\n');


            pw.write(sb1.toString());
            pw.close();
            System.out.println("done!");


    }


    //추가끝



    /**
     * Prints a style header.
     */
    private void printStyleHeader() {
        sb.append(
                "<style>div.color{width:4em;height:4em;float:left;margin:0 1em 1em 0;}"
                        + "th{text-align:left}"
                        + "td{vertical-align:top;padding-right:1em}</style>");
    }

    /**
     * Tests the color thief with the image at the given path name.
     * 
     * @param pathname
     *            the image path name
     * 
     * @throws IOException
     *             if an I/O error occurs
     */
    private void test(String pathname) throws IOException {
        System.out.println("Analyzing image " + pathname + "...");

        sb.append("<h1>Image: &quot;").append(pathname).append("&quot</h1>");
        sb.append("<p><img src=\"").append(pathname).append("\" style=\"max-width:100%\"></p>");

        BufferedImage img = ImageIO.read(new File(pathname));

        // The dominant color is taken from a 5-map
        sb.append("<h2>Dominant Color</h2>");
        CMap result = ColorThief.getColorMap(img, 10);
        VBox dominantColor = result.vboxes.get(0);
        printVBox(dominantColor);

        // Get the full palette
        sb.append("<h2>Palette</h2>");
        result = ColorThief.getColorMap(img, 10);

        //밑에 5줄 추가로 최다색상 rgb 뽑아냄
        /*
        int[] getter = ColorThief.getColor(img);
        for(int i=0; i<getter.length; i++) {
            System.out.print(getter[i]+", ");
        }
        System.out.println();
        */

        for (VBox vbox : result.vboxes) {
            printVBox(vbox);
            //System.out.println(vbox);
        }
        //System.out.println(result.vboxes);
    }

    /**
     * Prints HTML code for a VBox.
     * 
     * @param vbox
     *            the vbox
     */
    private void printVBox(VBox vbox) {
        int[] rgb = vbox.avg(false);

        // Create color String representations
        String rgbString = createRGBString(rgb);
        String rgbHexString = createRGBHexString(rgb);

        sb.append("<div>");

        // Print color box
        sb
                .append("<div class=\"color\" style=\"background:") //
                .append(rgbString)
                .append(";\"></div>");

        // Print table with color code and VBox information
        sb.append(
                "<table><tr><th>Color code:</th>" //
                        + "<th>Volume &times pixel count:</th>" //
                        + "<th>VBox:</th></tr>");

        // Color code
        sb
                .append("<tr><td>") //
                .append(rgbString)
                .append(" / ")
                .append(rgbHexString)
                .append("</td>");

        // Volume / pixel count
        int volume = vbox.volume(false);
        int count = vbox.count(false);
        sb
                .append("<td>")
                .append(String.format("%,d", volume))
                .append(" &times; ")
                .append(String.format("%,d", count))
                .append(" = ")
                .append(String.format("%,d", volume * count))
                .append("</td>");

        // VBox
        sb
                .append("<td>") //
                .append(vbox.toString())
                .append("</td></tr></table>");

        // Stop floating
        sb.append("<div style=\"clear:both\"></div>");

        sb.append("</div>");
    }

    /**
     * Creates a string representation of an RGB array.
     * 
     * @param rgb
     *            the RGB array
     * 
     * @return the string representation
     */
    private String createRGBString(int[] rgb) {
        return "rgb(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")";
    }

    /**
     * Creates an HTML hex color code for the given RGB array (e.g. <code>#ff0000</code> for red).
     * 
     * @param rgb
     *            the RGB array
     * 
     * @return the HTML hex color code
     */
    private String createRGBHexString(int[] rgb) {
        String rgbHex = Integer.toHexString(rgb[0] << 16 | rgb[1] << 8 | rgb[2]);

        // Left-pad with 0s
        int length = rgbHex.length();
        if (length < 6) {
            rgbHex = "00000".substring(0, 6 - length) + rgbHex;
        }

        return "#" + rgbHex;
    }

    /**
     * Saves the test results in an HTML file.
     * 
     * @param fileName
     *            the file name
     */
    private void saveToHTMLFile(String fileName) {
        System.out.println("Saving HTML file...");
        try {
            Files.write(Paths.get(fileName), sb.toString().getBytes());
        } catch (IOException ex) {
            System.out.println("Error saving HTML file: " + ex.getMessage());
        }
    }

}
