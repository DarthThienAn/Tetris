package com.tetris;

public class TetrisBlock 
{
    private static final int SOUTH = 2;
    private static final int EAST = 3;
    private static final int WEST = 4;
/*		public Coordinate first;
	public Coordinate second;
	public Coordinate third;
	public Coordinate fourth;*/
	public int firstx;
	public int secondx;
	public int thirdx;
	public int fourthx;
	public int firsty;
	public int secondy;
	public int thirdy;
	public int fourthy;
	
	public TetrisBlock(int newFirstx, int newFirsty, int newSecondx, int newSecondy, int newThirdx, int newThirdy, int newFourthx, int newFourthy) 
	{
/*    		first = new Coordinate(newFirstx, newFirsty);
		second = new Coordinate(newSecondx, newSecondy);
		third = new Coordinate(newThirdx, newThirdy);
		fourth = new Coordinate(newFourthx, newFourthy);*/
		firstx = newFirstx;
		firsty = newFirsty;
		secondx = newSecondx;
		secondy = newSecondy;
		thirdx = newThirdx;
		thirdy = newThirdy;
		fourthx = newFourthx;
		fourthy = newFourthy;
	}
	
	public void fall()
	{
/*    		first.y = first.y + 1;
		second.y = second.y + 1;
		third.y = third.y + 1;
		fourth.y = fourth.y + 1;*/
		
		firsty += 1;
		secondy += 1;
		thirdy += 1;
		fourthy += 1;
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
	    		firstx += 1;
	    		secondx += 1;
	    		thirdx += 1;
	    		fourthx += 1;
	            break;
	        }
	        case WEST: {
/*    	    		first.y -= 1;
	    		second.y -= 1;
	    		third.y -= 1;
	    		fourth.y -= +1;*/
	    		firstx -= 1;
	    		secondx -= 1;
	    		thirdx -= 1;
	    		fourthx -= 1;
	            break;
	        }
	        case SOUTH: {
/*    	    		first.y += 1;
	    		second.y += 1;
	    		third.y += 1;
	    		fourth.y += +1;*/
	    		firsty += 1;
	    		secondy += 1;
	    		thirdy += 1;
	    		fourthy += 1;
	            break;
	        }
        }
//        checkCollision();
   }
    
    public void rotateClockwise(int mOrientation)
    {
        final int FACEUP = 0;
        final int FACERIGHT = 1;
        final int FACEDOWN = 2;
        final int FACELEFT = 3;

/*            fourthx = secondx;
    	fourthy = secondy;
    	secondx = thirdx;
    	secondy = thirdy;*/
    	
    	switch (mOrientation)
    	{
        	case FACEUP:
        	{
        		secondx = firstx + 1;
        		secondy = firsty;
        		thirdx = firstx;
        		thirdy = firsty + 1;
        		fourthx = firstx;
        		fourthy = firsty - 1;
        		break;
        	}
        	case FACERIGHT:
        	{
        		secondx = firstx;
        		secondy = firsty + 1;
        		thirdx = firstx - 1;
        		thirdy = firsty;
        		fourthx = firstx + 1;
        		fourthy = firsty;
        		break;
        	}
        	case FACEDOWN:
        	{
        		secondx = firstx - 1;
        		secondy = firsty;
        		thirdx = firstx;
        		thirdy = firsty - 1;
        		fourthx = firstx;
        		fourthy = firsty + 1;
        		break;
        	}
        	case FACELEFT:
        	{
        		secondx = firstx;
        		secondy = firsty - 1;
        		thirdx = firstx + 1;
        		thirdy = firsty;
        		fourthx = firstx - 1;
        		fourthy = firsty;
        		break;
        	}
    	}
    	
    	
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