/***************************************************************
* file: Minecraft.java
* author: J. Woong, P. Leon; using code from 3D Viewing lecture, Tony Diaz
* class: CS 445 – Computer Graphics
*
* assignment: Final Checkpoint
* date last modified: 6/1/2016
*
* purpose: Initializes the main window and starts the gameloop
* 
****************************************************************/ 
package Minecraft;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Minecraft
{
    private Camera fp = new Camera(0f,0f,0f);
    private DisplayMode displayMode;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    
    /**
     * method: start
     * purpose: starts the openGL program
     */
    public void start() 
    {
        try {
        createWindow();
        initGL();
        fp.gameLoop();
        } catch (Exception e) {
        e.printStackTrace();
        }
    }

     /**
     * method: createWindow
     * purpose: Set up the display of the program with width of 640 and length of 480
     * the display is set to the center of the screen
     * 
     */
    private void createWindow() throws Exception
    {
        Display.setFullscreen(false);
        DisplayMode d[] =
        Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) 
        {
            if (d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) 
            {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("World Name");
        Display.create();
    }
    
    /**
     * method: initGL
     * purpose: Set up OpenGL, with a light blue background
     */
    private void initGL() 
    {
        glClearColor(0.0f, 0.9f, 1.0f, 0.0f);
        glDepthFunc(GL_LESS);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState (GL_TEXTURE_COORD_ARRAY);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); //sets our light’s position
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);//sets our specular light
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);//sets our diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);//sets our ambient light
        glEnable(GL_LIGHTING);//enables our lighting
        glEnable(GL_LIGHT0);//enables light0
    }
    //creates and positions the light source, as well as adds light inside the blocks
    private void initLightArrays() 
    {
    lightPosition = BufferUtils.createFloatBuffer(4);
    lightPosition.put(-150.0f).put(-75.0f).put(-150.0f).put(0.0f).flip();
    whiteLight = BufferUtils.createFloatBuffer(4);
    whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }
    
    /**
     * method: mn
     * purpose: starts the program
     */
    public static void main(String[] args) 
    {
        Minecraft world1= new Minecraft();
        world1.start();
    }
}


