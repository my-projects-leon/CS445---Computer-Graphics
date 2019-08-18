
package pkgfinal.project;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import static java.lang.System.*;
import java.io.File;
import java.util.Scanner;
import org.lwjgl.input.Keyboard;
import java.util.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.glu.GLU;


public class FinalProject {
    private FPCameraController fp = new FPCameraController(0f,0f,0f);
private DisplayMode displayMode;

    public class Vector3Float {
        public float x, y, z;
        public Vector3Float(int x, int y, int z){
            this.x=x;
            this.y=y;
            this.z=z;
        }
    }
    
    
    public static void main(String[] args) {
       FinalProject project = new FinalProject();
       project.start();
    }
    
    public FPCameraController(float x, float y, float z)
{
//instantiate position Vector3f to the x y z params.
position = new Vector3f(x, y, z);
lPosition = new Vector3f(x,y,z);
lPosition.x = 0f;
lPosition.y = 15f;
lPosition.z = 0f;
}
    public void yaw(float amount)
    {
    //increment the yaw by the amount param
    yaw += amount;
    }
    //increment the camera's current yaw rotation
    public void pitch(float amount)
    {
    //increment the pitch by the amount param
    pitch -= amount;
    }
    
    public void walkForward(float distance)
    {
    float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
    float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
    position.x -= xOffset;
    position.z += zOffset;
    }
    
    public void walkBackwards(float distance)
    {
    float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
    float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
    position.x += xOffset;
    position.z -= zOffset;
    }
    
    public void strafeLeft(float distance)
    {
    float xOffset = distance * (float)Math.sin(Math.toRadians(yaw-90));
    float zOffset = distance * (float)Math.cos(Math.toRadians(yaw-90));
    position.x -= xOffset;
    position.z += zOffset;
    }
    
    public void strafeRight(float distance)
    {
    float xOffset = distance * (float)Math.sin(Math.toRadians(yaw+90));
    float zOffset = distance * (float)Math.cos(Math.toRadians(yaw+90));
    position.x -= xOffset;
    position.z += zOffset;
    }
    
    //moves the camera up relative to its current rotation (yaw)
    public void moveUp(float distance)
    {
    position.y -= distance;
    }
    //moves the camera down
    public void moveDown(float distance)
    {
    position.y += distance;
    }
    
    public void lookThrough()
    {
    //roatate the pitch around the X axis
    glRotatef(pitch, 1.0f, 0.0f, 0.0f);
    //roatate the yaw around the Y axis
    glRotatef(yaw, 0.0f, 1.0f, 0.0f);
    //translate to the position vector's location
    glTranslatef(position.x, position.y, position.z);
    }
    
    public void gameLoop()
{
FPCameraController camera = new FPCameraController(0, 0, 0);
float dx = 0.0f;
float dy = 0.0f;
float dt = 0.0f; //length of frame
float lastTime = 0.0f; // when the last frame was
long time = 0;
float mouseSensitivity = 0.09f;
float movementSpeed = .35f;
//hide the mouse
Mouse.setGrabbed(true);
while (!Display.isCloseRequested() &&
!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
{
time = Sys.getTime();
lastTime = time;
//distance in mouse movement
//from the last getDX() call.
dx = Mouse.getDX();
//distance in mouse movement
//from the last getDY() call.
dy = Mouse.getDY();
//controll camera yaw from x movement fromt the mouse
camera.yaw(dx * mouseSensitivity);
//controll camera pitch from y movement fromt the mouse
camera.pitch(dy * mouseSensitivity);
//when passing in the distance to move
//we times the movementSpeed with dt this is a time scale
//so if its a slow frame u move more then a fast frame
//so on a slow computer you move just as fast as on a fast computer
if (Keyboard.isKeyDown(Keyboard.KEY_W))//move forward
{
camera.walkForward(movementSpeed);
}
if (Keyboard.isKeyDown(Keyboard.KEY_S))//move backwards
{
camera.walkBackwards(movementSpeed);
}
if (Keyboard.isKeyDown(Keyboard.KEY_A))//strafe left 
{
camera.strafeLeft(movementSpeed);
}
if (Keyboard.isKeyDown(Keyboard.KEY_D))//strafe right 
{
camera.strafeRight(movementSpeed);
}
if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))//move up 
{
camera.moveUp(movementSpeed);
}
if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
camera.moveDown(movementSpeed);
}
//set the modelview matrix back to the identity
glLoadIdentity();
//look through the camera before you draw anything
camera.lookThrough();
glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//you would draw your scene here.
render();
//draw the buffer to the screen
Display.update();
Display.sync(60);
}
Display.destroy();
}

    public void start()
    {
        try{
            createWindow();
            initGL();
            render();
            fp.gameLoop();//render();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private void createWindow()throws Exception{
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(640,480));
        Display.setTitle("Final Project");
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
    
    public void render ()
    {
        try{
        glBegin(GL_QUADS);
        //Top
        glColor3f(0.0f,0.0f,1.0f);
        glVertex3f( 1.0f, 1.0f,-1.0f);
        glVertex3f(-1.0f, 1.0f,-1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glVertex3f( 1.0f, 1.0f, 1.0f);
        //Bottom
        glVertex3f( 1.0f,-1.0f, 1.0f);
        glVertex3f(-1.0f,-1.0f, 1.0f);
        glVertex3f(-1.0f,-1.0f,-1.0f);
        glVertex3f( 1.0f,-1.0f,-1.0f);
        //Front
        glVertex3f( 1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f,-1.0f, 1.0f);
        glVertex3f( 1.0f,-1.0f, 1.0f);
        //Back
        glVertex3f( 1.0f,-1.0f,-1.0f);
        glVertex3f(-1.0f,-1.0f,-1.0f);
        glVertex3f(-1.0f, 1.0f,-1.0f);
        glVertex3f( 1.0f, 1.0f,-1.0f);
        //Left
        glVertex3f(-1.0f, 1.0f,1.0f);
        glVertex3f(-1.0f, 1.0f,-1.0f);
        glVertex3f(-1.0f,-1.0f,-1.0f);
        glVertex3f(-1.0f,-1.0f, 1.0f);
        //Right
        glVertex3f( 1.0f, 1.0f,-1.0f);
        glVertex3f( 1.0f, 1.0f, 1.0f);
        glVertex3f( 1.0f,-1.0f, 1.0f);
        glVertex3f( 1.0f,-1.0f,-1.0f);
        glEnd();
        glBegin(GL_LINE_LOOP);
        //Top
        glColor3f(0.0f,0.0f,0.0f);
        glVertex3f( 1.0f, 1.0f,-1.0f);
        glVertex3f(-1.0f, 1.0f,-1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glVertex3f( 1.0f, 1.0f, 1.0f);
        glEnd();
        glBegin(GL_LINE_LOOP);
        //Bottom
        glVertex3f( 1.0f,-1.0f, 1.0f);
        glVertex3f(-1.0f,-1.0f, 1.0f);
        glVertex3f(-1.0f,-1.0f,-1.0f);
        glVertex3f( 1.0f,-1.0f,-1.0f);
        glEnd();
        glBegin(GL_LINE_LOOP);
        //Front
        glVertex3f( 1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f,-1.0f, 1.0f);
        glVertex3f( 1.0f,-1.0f, 1.0f);
        glEnd();
        glBegin(GL_LINE_LOOP);
        //Back
        glVertex3f( 1.0f,-1.0f,-1.0f);
        glVertex3f(-1.0f,-1.0f,-1.0f);
        glVertex3f(-1.0f, 1.0f,-1.0f);
        glVertex3f( 1.0f, 1.0f,-1.0f);
        glEnd();
        glBegin(GL_LINE_LOOP);
        //Left
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f,-1.0f);
        glVertex3f(-1.0f,-1.0f,-1.0f);
        glVertex3f(-1.0f,-1.0f, 1.0f);
        glEnd();
        glBegin(GL_LINE_LOOP);
        //Right
        glVertex3f( 1.0f, 1.0f,-1.0f);
        glVertex3f( 1.0f, 1.0f, 1.0f);
        glVertex3f( 1.0f,-1.0f, 1.0f);
        glVertex3f( 1.0f,-1.0f,-1.0f);
        glEnd();
        }catch(Exception e){
        }
    }
}
