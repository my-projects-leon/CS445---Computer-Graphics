/***************************************************************
* file: Chunk.java
* author: J. Woong, P. Leon; using code from From Basic to Code, Texture Mapping, 
*         and Noise generation lecture, Tony Diaz
* class: CS 445 – Computer Graphics
*
* assignment: Final Checkpoint
* date last modified: 6/1/2016
*
* purpose: A quicker way to generate the blocky world
* 
****************************************************************/ 
package Minecraft;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk 
{
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private static int [][] yHeight = new int[CHUNK_SIZE][CHUNK_SIZE]; 
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    private int VBOTextureHandle;
    private Texture texture;

    /**
     * method: render
     * purpose: to render to game world
     */
    public void render()
    {
        glPushMatrix();
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glColorPointer(3,GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2,GL_FLOAT,0,0L);
        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
        glPopMatrix();
    }
    
    /**
     * method: rebuildMesh
     * purpose: set up VBOColorHandle, VBOVertexHandle, and VBOTextureHandle array with information about the game world
     * like block texture and randomizing world terrain using simplex noise.
     * @param startX
     * @param startY
     * @param startZ 
     */
    public void rebuildMesh( float startX, float startY, float startZ) 
    {
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE *CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);
        SimplexNoise terrain = new SimplexNoise(100, 0.3, r.nextInt());
        
        int xStart, xEnd, zStart, zEnd, yStart;
        
        xStart = 12; // r.nextInt((CHUNK_SIZE / 2) - 1) + 1;
        xEnd = 21; //r.nextInt(CHUNK_SIZE - xStart) + xStart;
        zStart = 1; //r.nextInt((CHUNK_SIZE / 2) - 1) + 1;
        zEnd = 14; //r.nextInt(CHUNK_SIZE - zStart) + zStart;
        yStart = r.nextInt(3 - 1) + 1;
        for (float x = 0; x < CHUNK_SIZE; x += 1) 
        {
            int i = (int)(xStart + x * ( xEnd - xStart ) / 5);

            for (float z = 0; z < CHUNK_SIZE; z += 1)
            {
                int j = (int)(1 + 1 * ( 15 - 1) / 3);
                int k = (int)(zStart + z * ( zStart - zEnd) / 5);
                
                int max = (int) (startY + (int) (20 * terrain.getNoise(i,j, k) * CUBE_LENGTH));
                
                if( max < 1)
                {
                    max = yStart;
                }
                yHeight[(int)x][(int)z] = max;
                
                for(float y = 0; y <= max; y++)
                {
                    if ((int)y == 0)
                        this.Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Bedrock);
                    else if((int)y >= max - 1 && (int)y < max)
                        this.Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Dirt);
                    else if( (int)y == max)
                    {
                        if(r.nextFloat() > 0.7)
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Grass);
                        else if(r.nextFloat() > 0.3)
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Sand);
                        else
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Water);
                    }
                    
                    VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH), (float)(y*CUBE_LENGTH + (int)(CHUNK_SIZE*.8)), (float) (startZ + z * CUBE_LENGTH)));
                    VertexTextureData.put(createTexCube((float) 0, (float)0,Blocks[(int)(x)][(int) (y)][(int) (z)]));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z])));
                }
            }
        }
        
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /**
     * method: createCubeVertexCol
     * purpose: create an array with the cube color
     * @param CubeColorArray
     * @return 
     */
    private float[] createCubeVertexCol(float[] CubeColorArray) 
    {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) 
        {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }
    
    /**
     * method: createCube
     * purpose: to create a 2 by 2 cube
     * @param x
     * @param y
     * @param z
     * @return 
     */
    public static float[] createCube(float x, float y, float z) 
    {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
        // TOP QUAD
        x + offset, y + offset, z,
        x - offset, y + offset, z,
        x - offset, y + offset, z - CUBE_LENGTH,
        x + offset, y + offset, z - CUBE_LENGTH,
        // BOTTOM QUAD
        x + offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z,
        x + offset, y - offset, z,
        // FRONT QUAD
        x + offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,      
        // BACK QUAD
        x + offset, y - offset, z,
        x - offset, y - offset, z,
        x - offset, y + offset, z,
        x + offset, y + offset, z,
        // LEFT QUAD
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z,
        x - offset, y - offset, z,
        x - offset, y - offset, z - CUBE_LENGTH,
        // RIGHT QUAD
        x + offset, y + offset, z,
        x + offset, y + offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z };
    }
    
    /**
     * method: createTexCube
     * purpose: to perform texture mapping on an given cube
     * @param x
     * @param y
     * @param block
     * @return 
     */
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f/16)/1024f;
        switch (block.getID()) {
            case 1: 
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // TOP!
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // FRONT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                // BACK QUAD
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // LEFT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                // RIGHT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2};
            case 2: //water
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1,
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                // TOP!
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1,
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                // FRONT QUAD
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1,
                // BACK QUAD
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1,
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                // LEFT QUAD
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1,
                // RIGHT QUAD
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1};
            case 3:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // TOP!
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // BACK QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // LEFT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // RIGHT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1};
            case 4:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                // TOP!
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                // FRONT QUAD
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                // BACK QUAD
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                // LEFT QUAD
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                // RIGHT QUAD
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2};
            case 5:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // TOP!
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // FRONT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                // BACK QUAD
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // LEFT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                // RIGHT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2};
            case 6:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*2, y + offset*3,
                x + offset*1, y + offset*3,
                x + offset*1, y + offset*2,
                x + offset*2, y + offset*2,
                // TOP!
                x + offset*2, y + offset*3,
                x + offset*1, y + offset*3,
                x + offset*1, y + offset*2,
                x + offset*2, y + offset*2,
                // FRONT QUAD
                x + offset*1, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*3,
                x + offset*1, y + offset*3,
                // BACK QUAD
                x + offset*2, y + offset*3,
                x + offset*1, y + offset*3,
                x + offset*1, y + offset*2,
                x + offset*2, y + offset*2,
                // LEFT QUAD
                x + offset*1, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*3,
                x + offset*1, y + offset*3,
                // RIGHT QUAD
                x + offset*1, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*3,
                x + offset*1, y + offset*3};
            case 7:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*4,
                x + offset*2, y + offset*4,
                x + offset*2, y + offset*3,
                x + offset*3, y + offset*3,
                // TOP!
                x + offset*3, y + offset*4,
                x + offset*2, y + offset*4,
                x + offset*2, y + offset*3,
                x + offset*3, y + offset*3,
                // FRONT QUAD
                x + offset*2, y + offset*3,
                x + offset*3, y + offset*3,
                x + offset*3, y + offset*4,
                x + offset*2, y + offset*4,
                // BACK QUAD
                x + offset*3, y + offset*4,
                x + offset*2, y + offset*4,
                x + offset*2, y + offset*3,
                x + offset*3, y + offset*3,
                // LEFT QUAD
                x + offset*2, y + offset*3,
                x + offset*3, y + offset*3,
                x + offset*3, y + offset*4,
                x + offset*2, y + offset*4,
                // RIGHT QUAD
                x + offset*2, y + offset*3,
                x + offset*3, y + offset*3,
                x + offset*3, y + offset*4,
                x + offset*2, y + offset*4};
            case 8:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*3,
                x + offset*2, y + offset*3,
                x + offset*2, y + offset*2,
                x + offset*3, y + offset*2,
                // TOP!
                x + offset*3, y + offset*3,
                x + offset*2, y + offset*3,
                x + offset*2, y + offset*2,
                x + offset*3, y + offset*2,
                // FRONT QUAD
                x + offset*2, y + offset*2,
                x + offset*3, y + offset*2,
                x + offset*3, y + offset*3,
                x + offset*2, y + offset*3,
                // BACK QUAD
                x + offset*3, y + offset*3,
                x + offset*2, y + offset*3,
                x + offset*2, y + offset*2,
                x + offset*3, y + offset*2,
                // LEFT QUAD
                x + offset*2, y + offset*2,
                x + offset*3, y + offset*2,
                x + offset*3, y + offset*3,
                x + offset*2, y + offset*3,
                // RIGHT QUAD
                x + offset*2, y + offset*2,
                x + offset*3, y + offset*2,
                x + offset*3, y + offset*3,
                x + offset*2, y + offset*3};
            case 9:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                // TOP!
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                // FRONT QUAD
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                // BACK QUAD
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                // LEFT QUAD
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                x + offset*0, y + offset*3,
                x + offset*1, y + offset*3,
                // RIGHT QUAD
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                x + offset*0, y + offset*3,
                x + offset*1, y + offset*3};
            case 0:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*3, y + offset*9,
                // TOP!
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // BACK QUAD
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                // LEFT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // RIGHT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1};
        }
    return new float[]{};
    }
    
    /**
     * method: getCubeColor
     * purpose: to return an array containing rgb value of (1,1,1)
     * @param block
     * @return 
     */
    private float[] getCubeColor(Block block) 
    {
        return new float[] { 1, 1, 1 };
    }
    
    /**
     * method: Chunk
     * purpose: look for a png file containing the texture for the block and then perform texture mapping,
     * as well as start the rendering process.
     * @param startX
     * @param startY
     * @param startZ 
     */
    public Chunk(int startX, int startY, int startZ) 
    {
        try{
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        }
        catch(Exception e){
            System.out.print("ERROR!");
        }
        r= new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++) 
        {
            for (int z = 0; z < CHUNK_SIZE; z++) 
            {                    
                for (int y = 0; y < CHUNK_SIZE; y++) 
                {
                    

                        if(r.nextFloat()>0.7f)
                        {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                        }   
                        else if(r.nextFloat()>0.6f)
                        {    
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Coalstone);
                        }
                        else if(r.nextFloat()>0.4f)
                        {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Ironstone);
                        }

                        else if(r.nextFloat()>0.2f)
                        {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Goldstone);
                        }
                        else
                        {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Diamondstone);
                        }
                    
                }
            }
        }
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
        
    }
}
