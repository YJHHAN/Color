import au.com.bytecode.opencsv.CSVReader;

import java.awt.*;
import java.io.*;

//바꿔야 할 부분 : convert의 주소, wrier의 주소

public class converter {
    public static void main(String[] args) throws IOException {
        float[][] value;
        String[] type;

        converter ins = new converter();

        String csvFile = "C:/Users/YJ/Desktop/Color/Extracting_color.csv";

        ins.convert(csvFile);

        value=ins.gethsb();
        type=ins.getType();
        int index = ins.getNum();

        String filename = "C:/Users/YJ/Desktop/Color/src/main/resources/target3.csv";
        ins.writer(value,type,index, filename);
    }

    float[] hsb;
    int datasize;


    private float[] HSB(int r, int g, int b) {
        hsb = Color.RGBtoHSB(r, g, b, null);
        //float hue,saturation,brightness;
        //hsb[0]=hue , hsb[1] = saturation, hsb[2]=brightness
        return hsb;
    }

    public void writer(float[][] value, String[] type, int datasize, String filename) throws FileNotFoundException {
        //PrintWriter pw = new PrintWriter(new File("hsb_lyk_4.csv"));
        PrintWriter pw = new PrintWriter(new File(filename));

        StringBuilder sb = new StringBuilder();

        int[] typenum= new int[datasize];


        for(int a=0; a<type.length; a++){
            if(type[a].equals("spring")) { typenum[a] = 0;}
            else if(type[a].equals("summer")) {typenum[a] = 1;}
            else if(type[a].equals("fall")) {typenum[a] = 2;}
            else if(type[a].equals("winter")) {typenum[a] = 3;}
            else if(type[a].equals("unpredicted")){typenum[a]=-1;}
        }

        for(int i=0;i<value.length;i++) {
            sb.append(typenum[i]);
            sb.append(',');
            sb.append(value[i][0]);
            sb.append(',');
            sb.append(value[i][1]);
            sb.append(',');
            sb.append(value[i][2]);
            sb.append(',');
            sb.append( rgb2[i][1]);
            sb.append(',');
            sb.append(rgb2[i][2]);
            sb.append(',');
            sb.append(rgb2[i][3]);
            sb.append('\n');
        }
        pw.write(sb.toString());
        pw.close();
    }


    public float[][] gethsb() throws IOException {
        return this.value;
    }

    public String[] getType(){return this.type;}

    float[][] value;
    String[] type;
    int[] rgb = new int[4];
    int[][] rgb2;

    public void convert(String csvFile) throws IOException {

        //String csvFile = "C:/Users/YJ/Desktop/color-thief-java-master/color-thief-java-master/Extracting_color.csv";

        CSVReader reader = null;
            reader = new CSVReader(new FileReader(csvFile));
            String[] line;
            float[] get = new float[3];
            int index=0;

            while ((line = reader.readNext()) != null) {
                datasize++;
            }
            value = new float[datasize][3];
            type = new String[datasize];
            rgb2  =new int[datasize][4];

            reader = new CSVReader(new FileReader(csvFile));

            if(datasize==1){
                line=reader.readNext();
                type[index]=line[0];
                rgb[1]=Integer.parseInt(line[1]);
                rgb[2]=Integer.parseInt(line[2]);
                rgb[3]=Integer.parseInt(line[3]);
                get = HSB(rgb[1],rgb[2],rgb[3]);
                value[index][0]=get[0];
                value[index][1]=get[1];
                value[index][2]=get[2];
            }
            else {
                while ((line = reader.readNext()) != null) {
                    type[index] = line[0];
                    rgb[1] = Integer.parseInt(line[1]);
                    rgb[2] = Integer.parseInt(line[2]);
                    rgb[3] = Integer.parseInt(line[3]);
                    rgb2[index][1] = rgb[1];
                    rgb2[index][2] = rgb[2];
                    rgb2[index][3] = rgb[3];
                    get = HSB(rgb[1], rgb[2], rgb[3]);
                    value[index][0] = get[0];
                    value[index][1] = get[1];
                    value[index][2] = get[2];
                    index++;
                }
            }
    }

    public int getNum() throws IOException {
        int num;
        num = this.datasize;
        return num;
    }

}

