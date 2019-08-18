/***************************************************************
* file: Camera.java
* author: J. Woong, P. Leon; using code from 3D Viewing lecture, Tony Diaz
* class: CS 445 – Computer Graphics
*
* assignment: Final Checkpoint
* date last modified: 6/1/2016
*
* purpose: To set up a camera for the game, includes rendering all the shapes
* 
****************************************************************/ 
package Minecraft;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;

import java.util.Random;

public class Camera
{
    private Vector3f position = null;
    private Vector3f lPosition = null;
    private float yRotation = 0.0f;
    private float xRotation = 0.0f;

    /**
     * method: Camera
     * purpose: initializes the camera position and lPosition
     * @param x
     * @param y
     * @param z 
     */
    public Camera(float x, float y, float z)
    {
        position = new Vector3f(x, y, z);
        lPosition = new Vector3f(x, y, z);
        lPosition.x = 0f;
        lPosition.y = 15f;
        lPosition.z = 0f;
    }

    /**
     * method: yaw
     * purpose: update the yRotation by adding amount to it
     * @param amount 
     */
    public void yaw(float amount)
    {
        yRotation += amount;
    }

    /**
     * method: pitch
     * purpose: update the xRotation by adding amount to it
     * @param amount 
     */
    public void pitch(float amount)
    {
        xRotation -= amount;
    }
    
    /**
     * method: walkForward
     * purpose: To move the camera in a forward direction by modifying the x and z coordinates
     * @param distance 
     */
    public void walkForward(float distance)
    {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yRotation));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yRotation));
        position.x -= xOffset;
        position.z += zOffset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x-=xOffset).put(lPosition.y).put(lPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    /**
     * method: walkBackwards
     * purpose: To move the camera in a backward direction, toward the user, by modifying the x and z coordinates
     * @param distance 
     */
    public void walkBackwards(float distance)
    {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yRotation));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yRotation));
        position.x += xOffset;
        position.z -= zOffset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x+=xOffset).put(lPosition.y).put(lPosition.z-=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    /**
     * method: strafeLeft
     * purpose: To move the camera in a left direction, sideway, by modifying the x and z coordinates
     * @param distance 
     */
    public void strafeLeft(float distance)
    {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yRotation - 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yRotation - 90));
        position.x -= xOffset;
        position.z += zOffset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x-=xOffset).put(lPosition.y).put(lPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    /**
     * method: strafeRight
     * purpose: To move the camera in a right direction, sideway, by modifying the x and z coordinates
     * @param distance 
     */
    public void strafeRight(float distance)
    {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yRotation + 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yRotation + 90));
        position.x -= xOffset;
        position.z += zOffset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x-=xOffset).put(lPosition.y).put(lPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    /**
     * method: moveUp
     * purpose: To move the camera in an up direction, toward the ceiling, by modifying the y coordinate
     * @param distance 
     */
    public void moveUp(float distance)
    {
        position.y -= distance;
    }

    /**
     * method: moveDown
     * purpose: To move the camera in an down direction, toward the floor, by modifying the y coordinate
     * @param distance 
     */
    public void moveDown(float distance)
    {
        position.y += distance;
    }
    
    /**
     * method: lookThrough
     * purpose: Transform the view matrix so that the user is looking through the camera
     */
    public void lookThrough()
    {
        glRotatef(xRotation, 1.0f, 0.0f, 0.0f);
        glRotatef(yRotation, 0.0f, 1.0f, 0.0f);
        glTranslatef(position.x, position.y, position.z);
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    /**
     * method: gameLoop
     * purpose: starts the game and continues until the user hit ESC or close the window
     * It also renders all shapes use in the game. It also set up the camera start location, the mouse, and keyboard functions
     */
    public void gameLoop()
    {
        Camera camera = new Camera(10, -40, -15);
        Chunk chunk = new Chunk(20,0,-30);
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f; 
        float lastTime = 0.0f; 
        long time = 0;
        float mouseSensitivity = 0.20f;
        float movementSpeed = .35f;
        Mouse.setGrabbed(true);
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) 
        {
            time = Sys.getTime();
            lastTime = time;

            dx = Mouse.getDX();
            dy = Mouse.getDY();

            camera.yaw(mouseSensitivity * dx);
            camera.pitch(mouseSensitivity * dy);
            
            if (Keyboard.isKeyDown(Keyboard.KEY_W))
            {
                camera.walkForward(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S))
            {
                camera.walkBackwards(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A))
            {
                camera.strafeLeft(movementSpeed);
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_D))
            {
                camera.strafeRight(movementSpeed);
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
            {
                camera.moveUp(movementSpeed);
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            {
                camera.moveDown(movementSpeed);
            }

            glLoadIdentity();
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            chunk.render();
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }
    
    /**
     * method: render
     * purpose: render the shapes of the program
     */
    private void render()
    {
        Random r = new Random();
        
        for( int x = 0; x < 30 * 2; x += 2 )
        {
            for( int z = 0; z < 30 * 2; z+= 2)
            {
                drawCube(x, 0, z);
                
                if( x == 15 * 2 && z == 15 * 2)
                
                    drawCube(x, 2, z);
            }
        }   
        
    }
    
    /**
     * method: drawCube
     * purpose: draw 2 by 2 cubes with a black outline; each side of the cube has different color
     */
    private void drawCube(float x, float y, float z)
    {
        float minX = -1, minY = -1, minZ = -1;
        float maxX = 1, maxY = 1, maxZ = 1;
        
        minX += x; minY += y; minZ += z;
        maxX += x; maxY += y; maxZ += z;
        try {
            glBegin(GL_QUADS);
            glColor3f(0.0f, 1.0f, 0.5f);
            glVertex3f(maxX, maxY, minZ);
            glVertex3f(minX, maxY, minZ);
            glVertex3f(minX, maxY, maxZ);
            glVertex3f(maxX, maxY, maxZ);
            glEnd();
            glBegin(GL_LINE_LOOP);
            glColor3f(0.0f , 0.0f, 0.0f);
            glVertex3f(maxX, maxY, minZ);
            glVertex3f(minX, maxY, minZ);
            glVertex3f(minX, maxY, maxZ);
            glVertex3f(maxX, maxY, maxZ);
            glEnd();
            

            glBegin(GL_QUADS);
            glColor3f(1.0f, 0.5f, 0.0f);        
            glVertex3f(maxX, minY, maxZ);
            glVertex3f(minX, minY, maxZ);
            glVertex3f(minX, minY, minZ);
            glVertex3f(maxX, minY, minZ);
            glEnd();
            glBegin(GL_LINE_LOOP);
            glColor3f(0.0f , 0.0f, 0.0f);
            glVertex3f(maxX, minY, maxZ);
            glVertex3f(minX, minY, maxZ);
            glVertex3f(minX, minY, minZ);
            glVertex3f(maxX, minY, minZ);
            glEnd();

            glBegin(GL_QUADS);
            glColor3f(1.0f, 0.0f, 0.0f);         
            glVertex3f(maxX, maxY, maxZ);
            glVertex3f(minX, maxY, maxZ);
            glVertex3f(minX, minY, maxZ);
            glVertex3f(maxX, minY, maxZ);
            glEnd();
            glBegin(GL_LINE_LOOP);
            glColor3f(0.0f , 0.0f, 0.0f);
            glVertex3f(maxX, maxY, maxZ);
            glVertex3f(minX, maxY, maxZ);
            glVertex3f(minX, minY, maxZ);
            glVertex3f(maxX, minY, maxZ);
            glEnd();

            glBegin(GL_QUADS);
            glColor3f(1.5f, 2.0f, 0.5f);          
            glVertex3f(maxX, minY, minZ);
            glVertex3f(minX, minY, minZ);
            glVertex3f(minX, maxY, minZ);
            glVertex3f(maxX, maxY, minZ);
            glEnd();
            glBegin(GL_LINE_LOOP);
            glColor3f(0.0f , 0.0f, 0.0f);
            glVertex3f(maxX, minY, minZ);
            glVertex3f(minX, minY, minZ);
            glVertex3f(minX, maxY, minZ);
            glVertex3f(maxX, maxY, minZ);
            glEnd();

            glBegin(GL_QUADS);
            glColor3f(0.0f, 1.5f, 0.0f);
            glVertex3f(minX, maxY, maxZ);
            glVertex3f(minX, maxY, minZ);
            glVertex3f(minX, minY, minZ);
            glVertex3f(minX, minY, maxZ);        
            glEnd();
            glBegin(GL_LINE_LOOP);
            glColor3f(0.0f , 0.0f, 0.0f);
            glVertex3f(minX, maxY, maxZ);
            glVertex3f(minX, maxY, minZ);
            glVertex3f(minX, minY, minZ);
            glVertex3f(minX, minY, maxZ); 
            glEnd();

            glBegin(GL_QUADS);
            glColor3f(1.0f, 0f, 1.0f);          
            glVertex3f(maxX, maxY, minZ);
            glVertex3f(maxX, maxY, maxZ);
            glVertex3f(maxX, minY, maxZ);
            glVertex3f(maxX, minY, minZ);
            glEnd();
            glBegin(GL_LINE_LOOP);
            glColor3f(0.0f , 0.0f, 0.0f);
            glVertex3f(maxX, maxY, minZ);
            glVertex3f(maxX, maxY, maxZ);
            glVertex3f(maxX, minY, maxZ);
            glVertex3f(maxX, minY, minZ);
            glEnd();
            
        } catch (Exception e) {
        }
    }
}