/***************************************************************
* file: Block.java
* author: J. Woong, P. Leon; using code from From Basic to Code lecture, Tony Diaz
* class: CS 445 – Computer Graphics
*
* assignment: Final Checkpoint
* date last modified: 6/1/2016
*
* purpose: To set up a block class to match up a block type with its id
* 
****************************************************************/ 
package Minecraft;

public class Block 
{
    private boolean isActive;
    private BlockType type;
    private float x,y,z;
    
    public enum BlockType
    {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5),
        BlockType_Ironstone(6),
        BlockType_Diamondstone(7),
        BlockType_Coalstone(8),
        BlockType_Goldstone(9);
        
        private int BlockID;
        
        BlockType(int i)
        {
            BlockID = i;
        }
        
        public int getID()
        {
            return BlockID;
        }
        
        public void setID(int i)
        {
            BlockID = i;
        }
    }
    
    /**
     * method Block
     * purpose: assign a block a type
     * @param type 
     */
    public Block(BlockType type)
    {
        this.type = type;
    }
    
    /**
     * method setCoords
     * purpose: set the location of the block
     * @param x
     * @param y
     * @param z 
     */
    public void setCoords(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * method: isActive
     * purpose: check whether is active or not
     * @return 
     */
    public boolean isActive()
    {
        return isActive;
    }
    
    /**
     * method: setActive
     * purpose: to make a block active or turn off it's active state
     * @param active 
     */
    public void setActive(boolean active)
    {
        isActive = active;
    }
    
    /**
     * method: getID
     * purpose: return the id of the block
     * @return 
     */
    public int getID()
    {
        return type.getID();
    }
    
}
