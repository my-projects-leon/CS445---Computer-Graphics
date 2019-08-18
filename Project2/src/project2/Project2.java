/***************************************************************
* file: Project2.java
* author: Pablo Leon
* class: CS 445 - Computer Graphics
*
* assignment: program 2
* date last modified: 4/20/2016
*
* purpose: This program accepts coordinates from a text file and 
* renders them into filled in polygons after applying a list
* of transformations to them.
****************************************************************/ 
package project2;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import static java.lang.System.*;
import java.io.File;
import java.util.Scanner;
import org.lwjgl.input.Keyboard;
import java.util.*;

public class Project2 
{
    public void start(){
        try{
            createWindow();
            initGL();
            render();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
     private void createWindow()throws Exception{
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(640,480));
        Display.setTitle("Project 2");
        Display.create();
    }
    
    private void initGL(){
        glClearColor(0.0f,0.0f,0.0f,0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-320,320,-240,240,1,-1);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT,GL_NICEST);
    }
    
    private void render()
    {
      while(!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
      {
          try
          {
           glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                glPointSize(1);
                //Reading file
                File file = new File("coordinates.txt");
                try{
                Scanner scan = new Scanner(file);
                String line = scan.nextLine().trim();
                    String[] color = line.split("\\s|,");
                    char type = color[0].charAt(0);
                while(scan.hasNextLine()){
                    List<float[]> poly = new ArrayList<float[]>();
                    System.out.println(type);
                    if(type == 'P')
                    {
                        //read the color of pixels after P
                        float red = Float.parseFloat(color[1]);
                        float green = Float.parseFloat(color[2]);
                        float blue = Float.parseFloat(color[3]);
                        glColor3f(red,green,blue);
                        line = scan.nextLine().trim();
                        String[] vert = line.split("\\s|,");
                        type = vert[0].charAt(0);
                        while(type != 'T')
                        {
                            //add the vertecies into a list of float arrays until T for transitions is hit
                                poly.add(new float[] {Float.parseFloat(vert[0]),Float.parseFloat(vert[1])});
                                line = scan.nextLine().trim();
                                vert = line.split("\\s|,");
                                type = vert[0].charAt(0);   
                        }
                        while(type != 'P' && scan.hasNextLine())
                        {
                           line = scan.nextLine().trim();
                           String[] trans = line.split("\\s|,");
                           type = trans[0].charAt(0);
                           switch(type){
                               
                               case't':
                                   float dx = Float.parseFloat(trans[1]);
                                   float dy = Float.parseFloat(trans[2]);
                                   translation(poly,dx,dy);
                                   break;
                               case'r':
                                   float angle = Float.parseFloat(trans[1]);
                                   float xR = Float.parseFloat(trans[2]);
                                   float yR = Float.parseFloat(trans[3]);
                                   rotate(poly,angle,xR,yR);
                                   break;
                               case's':
                                   float xScale = Float.parseFloat(trans[1]);
                                   float yScale = Float.parseFloat(trans[2]);
                                   float x = Float.parseFloat(trans[3]);
                                   float y = Float.parseFloat(trans[4]);
                                   scale(poly,xScale,yScale,x,y);
                                   break;
                           }
                        }
                        //all the transformations have take effect now to render it
                        //first to render the edges
                        edgeRend(poly);
                        //after the polygon outline is made fill in
                        fill(poly);
                    }
                }
                } catch (Exception ex){
                    ex.printStackTrace();
                }     
          }catch(Exception e){
          }
      }
      Display.destroy();
    }
    
    public static void translation(List<float[]> poly, float dx, float dy){
     for (int i = 0; i < poly.size(); i++) {
         poly.get(i)[0] = dx + poly.get(i)[0];
         poly.get(i)[1] = dy + poly.get(i)[1];
         float[] f = poly.get(i);
         for (int j = 0; j < f.length; j++) {
            System.out.print(f[j] + " ");
         }
         System.out.println();
        }
    }
    
    public static void rotate(List<float[]> poly, float angle, float xR, float yR){
        
    }
    
    public static void scale(List<float[]> poly, float xScale, float yScale, float x, float y){
        
    }
    
    public static void edgeRend(List<float[]> poly){
        for (int i = 0; i < poly.size(); i++) {
        float x0 = poly.get(i)[0];
        float y0 = poly.get(i)[1];
        float x1;
        float y1;
        if(i+1 == poly.size())
        {
            x1 = poly.get(0)[0];
            y1 = poly.get(0)[1];
        }
        else
        {
            x1 = poly.get(i+1)[0];
            y1 = poly.get(i+1)[1];
        }
        if(x1<x0){
            float x = x0;
            float y = y0;
            x0 = x1;
            y0 = y1;
            x1 = x;
            y1 = y;
        }
        float dx = x1 - x0;
        float dy = y1 - y0;
        float m = ((y1-y0)/(x1-x0));

        if(m>=0 && m<=1){//slope M<1
            float d = (2 * dy) - dx;
            float incR = 2*dy;
            float incUR = 2*(dy - dx);
            glBegin(GL_POINTS);
            while(x0<x1){
                glVertex2f(x0,y0);
                if (d >= 0){
                    x0 = x0+1;
                    y0 = y0+1;
                    d = d + incUR;
                }
                else{
                    x0 = x0+1;
                    d = d + incR;
                }
            }
        }
        if(m > 1){//slope M>1
            float d = (2 * dx) - dy;
            float incU = 2*dx;
            float incUR = 2*(dx - dy);
            glBegin(GL_POINTS);
            while(y0<y1){
                glVertex2f(x0,y0);
                if (d >= 0){
                    x0 = x0+1;
                    y0 = y0+1;
                    d = d + incUR;
                }
                else{
                    y0 = y0+1;
                    d = d + incU;
                }
            }
        }
        if(m<0 && m >= -1){//slope M<0
            float d = (2 * (-1*dy)) - dx;
            float incR = 2*(-1*dy);
            float incDR = 2*((-1*dy) - dx);
            glBegin(GL_POINTS);
            while(x0<x1){
                glVertex2f(x0,y0);
                if (d >= 0){
                    x0 = x0+1;
                    y0 = y0-1;
                    d = d + incDR;
                }
                else{
                    x0 = x0+1;
                    d = d + incR;
                }
            }
        }
        if(m < -1){//slope M<-1
            float d = (2 * dx) - (-1*dy);
            float incD = 2*dx;
            float incDR = 2*(dx - (-1*dy));
            glBegin(GL_POINTS);
            while(y0>y1){
                glVertex2f(x0,y0);
                if (d >= 0){
                    x0 = x0+1;
                    y0 = y0-1;
                    d = d + incDR;
                }
                else{
                    y0 = y0-1;
                    d = d + incD;
                }
            }
        }
        glEnd();
        Display.update();
        Display.sync(60);
        }
    }
    
    public static void fill(List<float[]> poly){
        
    }
    
    public static void main(String[] args) 
    {
      Project2 project = new Project2();
        project.start();  
    }
    
}
