/***************************************************************
* file: Project1.java
* author: Pablo Leon
* class: CS 445 - Computer Graphics
*
* assignment: program 1
* date last modified: 4/06/2016
*
* purpose: This program accepts coordinates from a text file and 
* renders either a line, circle, or ellipse according to the file
*
****************************************************************/ 
package project1;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import static java.lang.System.*;
import java.io.File;
import java.util.Scanner;
import org.lwjgl.input.Keyboard;

public class Project1 {

    public static void main(String[] args) 
    {
        Project1 project = new Project1();
        project.start();
    }
    
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
        Display.setTitle("Project 1");
        Display.create();
    }
    
    private void initGL(){
        glClearColor(0.0f,0.0f,0.0f,0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0,640,0,480,1,-1);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT,GL_NICEST);
    }
    
    private void render(){
        while(!Keyboard.isKeyDown(Keyboard.KEY_Q)){
            try{
                glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                glPointSize(1);
                //Reading file
                File file = new File("coordinates.txt");
                try{
                Scanner scan = new Scanner(file);
                
                while(scan.hasNextLine()){
                    String line = scan.nextLine().trim();
                    String[] vert = line.split("\\s|,");
                    char type = vert[0].charAt(0);
                    System.out.println(type);
                    switch(type){
                        
                        case 'l':
                            glColor3f(1.0f,0.0f,0.0f);//red
                            float x0 = Float.parseFloat(vert[1]);
                            float y0 = Float.parseFloat(vert[2]);
                            float x1 = Float.parseFloat(vert[3]);
                            float y1 = Float.parseFloat(vert[4]);
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
                            break;
                            
                        case 'c':
                            glColor3f(0.0f,0.0f,1.0f);//blue
                            double x = Double.parseDouble(vert[1]);
                            double y = Double.parseDouble(vert[2]);
                            double rad = Double.parseDouble(vert[3]);
                            glBegin(GL_POINTS);
                            double degree = 0.0;
                            while (degree < 360)
                            {
                 //backspacced the next to lines to fit them when printing                
                 double xC = x + (rad * Math.cos(Math.toRadians(degree)));
                 double yC = y + (rad * Math.sin(Math.toRadians(degree)));
                                glVertex2f((float)xC,(float)yC);
                                degree = degree + 1.0;
                            }
                            glEnd();
                            Display.update();
                            Display.sync(60);
                            break;
                            
                        case 'e':
                            glColor3f(0.0f,1.0f,0.0f);//green
                            float x3 = Float.parseFloat(vert[1]);
                            float y3 = Float.parseFloat(vert[2]);
                            float radX = Float.parseFloat(vert[3]);
                            float radY = Float.parseFloat(vert[4]);
                            glBegin(GL_POINTS);
                            double degre = 0.0;
                            while (degre < 360)
                            {
                //backspacced the next to lines to fit them when printing 
                double xE = x3 + (radX * Math.cos(Math.toRadians(degre)));
                double yE = y3 + (radY * Math.sin(Math.toRadians(degre)));
                                glVertex2f((float)xE,(float)yE);
                                degre = degre + 1.0;
                            }
                            glEnd();
                            Display.update();
                            Display.sync(60);
                            break;
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
}