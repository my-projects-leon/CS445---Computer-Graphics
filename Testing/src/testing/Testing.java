/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.io.File;
import java.util.Scanner;

/**
 *
 * @author Pablo
 */
public class Testing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File file = new File("coordinates.txt");
        try{
                Scanner scan = new Scanner(file);
                
                while(scan.hasNextLine()){
                    String line = scan.nextLine().trim();
                    String[] vert = line.split("\\s|,");
                    char type = vert[0].charAt(0);
                    
                    switch(type){
                        case 'l':
                            System.out.println(type);
                            float x0 = Float.parseFloat(vert[1]);
                            float y0 = Float.parseFloat(vert[2]);
                            float x1 = Float.parseFloat(vert[3]);
                            float y1 = Float.parseFloat(vert[4]);
                            System.out.println("First Point:" + x0 + "," + y0);
                            break;
                        case 'c':
                            System.out.println(type);
                            break;
                        case 'e':
                            System.out.println(type);
                            break;
                    }
                    /*System.out.println(type);
                    for (int i = 1; i < vert.length; i++)
                        System.out.print(vert[i]);
                    System.out.println("");*/
                }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
}
