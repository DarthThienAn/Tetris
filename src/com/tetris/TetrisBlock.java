package com.tetris;

import android.util.Log;

public class TetrisBlock 
{
    private static final String TAG = "TetrisView";
	
	private int blockType;
    private static final int IBLOCK = 0;
    private static final int JBLOCK = 1;
    private static final int LBLOCK = 2;
    private static final int OBLOCK = 3;
    private static final int SBLOCK = 4;
    private static final int ZBLOCK = 5;
    private static final int TBLOCK = 6;
	
    private int mOrientation = FACEUP;
    private static final int FACEUP = 0;
    private static final int FACERIGHT = 1;
    private static final int FACEDOWN = 2;
    private static final int FACELEFT = 3;
	
    private static final int SOUTH = 2;
    private static final int EAST = 3;
    private static final int WEST = 4;
/*		public Coordinate first;
	public Coordinate second;
	public Coordinate third;
	public Coordinate fourth;*/
	public int x1;
	public int x2;
	public int x3;
	public int x4;
	public int y1;
	public int y2;
	public int y3;
	public int y4;
	
	public TetrisBlock(int newx1, int newy1, int newx2, int newy2, int newx3, int newy3, int newx4, int newy4) 
	{
/*    		first = new Coordinate(newx1, newy1);
		second = new Coordinate(newx2, newy2);
		third = new Coordinate(newx3, newy3);
		fourth = new Coordinate(newx4, newy4);*/
		x1 = newx1;
		y1 = newy1;
		x2 = newx2;
		y2 = newy2;
		x3 = newx3;
		y3 = newy3;
		x4 = newx4;
		y4 = newy4;
		
		mOrientation = FACEDOWN;
		refreshBlock();
	}
	
	public TetrisBlock(int newx1, int newy1, int newBlockType)
	{
		x1 = newx1;
		y1 = newy1;
		blockType = newBlockType;
		mOrientation = FACEDOWN;
		refreshBlock();
	}
	
//	public void refreshBlock()
//	{
//		switch(blockType)
//		{
//			case IBLOCK:
//			{
//				x2 = x1 + 1;
//				x3 = x1 - 1;
//				x4 = x1 - 2;
//				y2 = y1;
//				y3 = y1;
//				y4 = y1;
//				break;
//			}
//			case JBLOCK:
//			{
//				x2 = x1 - 1;
//				x3 = x1 + 1;
//				x4 = x1 + 1;
//				y2 = y1;
//				y3 = y1;
//				y4 = y1 - 1;
//				break;
//			}
//			case LBLOCK:
//			{
//				x2 = x1 + 1;
//				x3 = x1 - 1;
//				x4 = x1 - 1;
//				y2 = y1;
//				y3 = y1;
//				y4 = y1 - 1;
//				break;
//			}
//			case OBLOCK:
//			{
//				x2 = x1 + 1;
//				x3 = x1;
//				x4 = x1 + 1;
//				y2 = y1;
//				y3 = y1 + 1;
//				y4 = y1 + 1;
//				break;
//			}
//			case SBLOCK:
//			{
//				x2 = x1 - 1;
//				x3 = x1;
//				x4 = x1 + 1;
//				y2 = y1;
//				y3 = y1 - 1;
//				y4 = y1 - 1;
//				break;
//			}
//			case TBLOCK:
//			{
//				x2 = x1;
//				x3 = x1 - 1;
//				x4 = x1 + 1;
//				y2 = y1 + 1;
//				y3 = y1;
//				y4 = y1;
//				break;
//			}
//			case ZBLOCK:
//			{
//				x2 = x1 + 1;
//				x3 = x1;
//				x4 = x1 - 1;
//				y2 = y1;
//				y3 = y1 - 1;
//				y4 = y1 - 1;
//				break;
//			}
//		}
//	}
	
	public void fall()
	{
/*    		first.y = first.y + 1;
		second.y = second.y + 1;
		third.y = third.y + 1;
		fourth.y = fourth.y + 1;*/
		
//		y1 += 1;
//		y2 += 1;
//		y3 += 1;
//		y4 += 1;
		y1 += 1;
		refreshBlock();
	}
	
    public void moveBlock(int mInputDirection)
    {
        switch (mInputDirection)
        {
	        case EAST: {
/*    	    		first.x += 1;
	    		second.x += 1;
	    		third.x += 1;
	    		fourth.x += +1;*/
	    		x1 += 1;
	    		x2 += 1;
	    		x3 += 1;
	    		x4 += 1;
	    		//can also use refreshBlock() method
	            break;
	        }
	        case WEST: {
/*    	    		first.y -= 1;
	    		second.y -= 1;
	    		third.y -= 1;
	    		fourth.y -= +1;*/
	    		x1 -= 1;
	    		x2 -= 1;
	    		x3 -= 1;
	    		x4 -= 1;
	            break;
	        }
	        case SOUTH: {
/*    	    		first.y += 1;
	    		second.y += 1;
	    		third.y += 1;
	    		fourth.y += +1;*/
	    		y1 += 1;
	    		y2 += 1;
	    		y3 += 1;
	    		y4 += 1;
	            break;
	        }
        }
//        checkCollision();
   }

	public void flip0()
	{
		switch(blockType)
		{
			case IBLOCK:
			{
				x2 = x1 + 1;
				x3 = x1 - 1;
				x4 = x1 - 2;
				y2 = y1;
				y3 = y1;
				y4 = y1;
				break;
			}
			case JBLOCK:
			{
				x2 = x1 - 1;
				x3 = x1 + 1;
				x4 = x1 + 1;
				y2 = y1;
				y3 = y1;
				y4 = y1 - 1;
				break;
			}
			case LBLOCK:
			{
				x2 = x1 + 1;
				x3 = x1 - 1;
				x4 = x1 - 1;
				y2 = y1;
				y3 = y1;
				y4 = y1 - 1;
				break;
			}
			case OBLOCK:
			{
				x2 = x1 + 1;
				x3 = x1;
				x4 = x1 + 1;
				y2 = y1;
				y3 = y1 + 1;
				y4 = y1 + 1;
				break;
			}
			case SBLOCK:
			{
				x2 = x1 - 1;
				x3 = x1;
				x4 = x1 + 1;
				y2 = y1;
				y3 = y1 - 1;
				y4 = y1 - 1;
				break;
			}
			case TBLOCK:
			{
				x2 = x1;
				x3 = x1 - 1;
				x4 = x1 + 1;
				y2 = y1 + 1;
				y3 = y1;
				y4 = y1;
				break;
			}
			case ZBLOCK:
			{
				x2 = x1 + 1;
				x3 = x1;
				x4 = x1 - 1;
				y2 = y1;
				y3 = y1 - 1;
				y4 = y1 - 1;
				break;
			}
		}
	}
	
    public void flip90()
    {
		switch(blockType)
		{
			case IBLOCK:
			{
				x2 = x1;
				x3 = x1;
				x4 = x1;
				y2 = y1 - 1;
				y3 = y1 + 1;
				y4 = y1 + 2;
				break;
			}
			case JBLOCK:
			{
				x2 = x1;
				x3 = x1;
				x4 = x1 + 1;
				y2 = y1 - 1;
				y3 = y1 + 1;
				y4 = y1 + 1;
				break;
			}
			case LBLOCK:
			{
				x2 = x1;
				x3 = x1;
				x4 = x1 + 1;
				y2 = y1 + 1;
				y3 = y1 - 1;
				y4 = y1 - 1;
				break;
			}
			case OBLOCK:
			{
				x2 = x1 + 1;
				x3 = x1;
				x4 = x1 + 1;
				y2 = y1;
				y3 = y1 + 1;
				y4 = y1 + 1;
				break;
			}
			case SBLOCK:
			{
				x2 = x1;
				x3 = x1 + 1;
				x4 = x1 + 1;
				y2 = y1 - 1;
				y3 = y1;
				y4 = y1 + 1;
				break;
			}
			case TBLOCK:
			{
				x2 = x1 - 1;
				x3 = x1;
				x4 = x1;
				y2 = y1;
				y3 = y1 - 1;
				y4 = y1 + 1;
				break;
			}
			case ZBLOCK:
			{
				x2 = x1;
				x3 = x1 + 1;
				x4 = x1 + 1;
				y2 = y1 + 1;
				y3 = y1;
				y4 = y1 - 1;
				break;
			}
		}    
    }
    
    public void flip180()
    {
		switch(blockType)
		{
			case IBLOCK:
			{
				x2 = x1 - 1;
				x3 = x1 + 1;
				x4 = x1 + 2;
				y2 = y1;
				y3 = y1;
				y4 = y1;
				break;
			}
			case JBLOCK:
			{
				x2 = x1 + 1;
				x3 = x1 - 1;
				x4 = x1 - 1;
				y2 = y1;
				y3 = y1;
				y4 = y1 + 1;
				break;
			}
			case LBLOCK:
			{
				x2 = x1 - 1;
				x3 = x1 + 1;
				x4 = x1 + 1;
				y2 = y1;
				y3 = y1;
				y4 = y1 + 1;
				break;
			}
			case OBLOCK:
			{
				x2 = x1 + 1;
				x3 = x1;
				x4 = x1 + 1;
				y2 = y1;
				y3 = y1 + 1;
				y4 = y1 + 1;
				break;
			}
			case SBLOCK:
			{
				x2 = x1 + 1;
				x3 = x1;
				x4 = x1 - 1;
				y2 = y1;
				y3 = y1 + 1;
				y4 = y1 + 1;
				break;
			}
			case TBLOCK:
			{
				x2 = x1;
				x3 = x1 + 1;
				x4 = x1 - 1;
				y2 = y1 - 1;
				y3 = y1;
				y4 = y1;
				break;
			}
			case ZBLOCK:
			{
				x2 = x1 - 1;
				x3 = x1;
				x4 = x1 + 1;
				y2 = y1;
				y3 = y1 + 1;
				y4 = y1 + 1;
				break;
			}
		}    
    }
   
    public void flip270()
    {
		switch(blockType)
		{
			case IBLOCK:
			{
				x2 = x1;
				x3 = x1;
				x4 = x1;
				y2 = y1 + 1;
				y3 = y1 - 1;
				y4 = y1 - 2;
				break;
			}
			case JBLOCK:
			{
				x2 = x1;
				x3 = x1;
				x4 = x1 + 1;
				y2 = y1 + 1;
				y3 = y1 - 1;
				y4 = y1 - 1;
				break;
			}
			case LBLOCK:
			{
				x2 = x1;
				x3 = x1;
				x4 = x1 - 1;
				y2 = y1 - 1;
				y3 = y1 + 1;
				y4 = y1 + 1;
				break;
			}
			case OBLOCK:
			{
				x2 = x1 + 1;
				x3 = x1;
				x4 = x1 + 1;
				y2 = y1;
				y3 = y1 + 1;
				y4 = y1 + 1;
				break;
			}
			case SBLOCK:
			{
				x2 = x1;
				x3 = x1 - 1;
				x4 = x1 - 1;
				y2 = y1 + 1;
				y3 = y1;
				y4 = y1 - 1;
				break;
			}
			case TBLOCK:
			{
				x2 = x1 + 1;
				x3 = x1;
				x4 = x1;
				y2 = y1;
				y3 = y1 + 1;
				y4 = y1 - 1;
				break;
			}
			case ZBLOCK:
			{
				x2 = x1;
				x3 = x1 - 1;
				x4 = x1 - 1;
				y2 = y1 - 1;
				y3 = y1;
				y4 = y1 + 1;
				break;
			}
		}    
    }
    
    public void refreshBlock()
    {
    	Log.v(TAG, " " + blockType);
    	
    	switch(mOrientation)
		{
			case FACEUP:
			{
				flip0();
				break;
			}
			case FACERIGHT: 
			{
				flip90();
				break;
			}
			case FACEDOWN: 
			{
				flip180();
				break;
			}
			case FACELEFT: 
			{
				flip270();
				break;
			}
		}        
        
/*            x4 = x2;
    	y4 = y2;
    	x2 = x3;
    	y2 = y3;*/
    	
//    	switch (mOrientation)
//    	{
//        	case FACEUP:
//        	{
//        		x2 = x1 + 1;
//        		y2 = y1;
//        		x3 = x1;
//        		y3 = y1 + 1;
//        		x4 = x1;
//        		y4 = y1 - 1;
//        		break;
//        	}
//        	case FACERIGHT:
//        	{
//        		x2 = x1;
//        		y2 = y1 + 1;
//        		x3 = x1 - 1;
//        		y3 = y1;
//        		x4 = x1 + 1;
//        		y4 = y1;
//        		break;
//        	}
//        	case FACEDOWN:
//        	{
//        		x2 = x1 - 1;
//        		y2 = y1;
//        		x3 = x1;
//        		y3 = y1 - 1;
//        		x4 = x1;
//        		y4 = y1 + 1;
//        		break;
//        	}
//        	case FACELEFT:
//        	{
//        		x2 = x1;
//        		y2 = y1 - 1;
//        		x3 = x1 + 1;
//        		y3 = y1;
//        		x4 = x1 - 1;
//        		y4 = y1;
//        		break;
//        	}
//    	}
    	
    	
    }

    public void rotateClockwise()
    {
    	mOrientation = (mOrientation + 1) % 4;
    }
    
    /**
     * Simple class containing two integer values and a comparison function.
     * There's probably something I should use instead, but this was quick and
     * easy to build.
     * 
     */
/*        private class Coordinate {
        public int x;
        public int y;

        public Coordinate(int newX, int newY) {
            x = newX;
            y = newY;
        }*/

/*        public boolean equals(Coordinate other) {
            if (x == other.x && y == other.y) {
                return true;
            }
            return false;
        }*/

/*            @Override
        public String toString() {
            return "Coordinate: [" + x + "," + y + "]";
        }
    }*/
    
}