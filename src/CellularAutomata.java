import java.util.Scanner;
import java.lang.Math;

public class CellularAutomata
{
   public static void main(String[] args)
   {
      int rule;
      String userInput;
      Automaton myAutomaton;
      Scanner inputStream = new Scanner(System.in);

      //get the user's input and do a sanity check
      rule = -1;
      do
      {
         System.out.print("Enter Rule (0 - 255): ");
         userInput = inputStream.nextLine();
         try
         {
            rule = Integer.parseInt(userInput);
         }
         catch (NumberFormatException error)
         {
            System.out.println("Sorry, numbers only please.");
            continue;
         }
      } 
      while (rule < 0 || rule > 255);

      //now create the Automaton and use a loop to display 100 gens
      myAutomaton = new Automaton(rule);
      System.out.println("   start");
      for (int k = 0; k < 100; k++)
      {
         System.out.println( myAutomaton.toStringCurrentGen() );
         myAutomaton.propogateNewGeneration();
      }
      System.out.println("   end");
      inputStream.close();
   }
   
}

class Automaton
{
   //declare some constants so i don't have to use numbers later
   public final static int MAX_DISPLAY_WIDTH = 121;
   public final static int DEFAULT_WIDTH = 79;
   public final static int DEFAULT_RULE = 0;
   public final static int MIN_RULE_NUMBER = 0;
   public final static int MAX_RULE_NUMBER = 255;
   public final static int NUM_RULES = 8;
   
   private boolean rules[];
   private String thisGen;
   private String extremeBit;
   private int displayWidth;
   
   public Automaton(int rule)
   {
      //the setRule() method does another sanity check for me
      if ( !setRule(rule) )
         setRule(DEFAULT_RULE);
      //in later versions this constructor could take a width value as well
      setDisplayWidth(DEFAULT_WIDTH);
      //to avoid reuising code i just call the resetFirstGen() method to 
      //set the seed gen and extreme bit
      resetFirstGen();
   }
   
   public boolean setRule(int newRule)
   {
      StringBuffer binaryString;
      
      //again test the rule for sanity
      if ( (newRule < MIN_RULE_NUMBER) || (newRule > MAX_RULE_NUMBER) )
         return false;
      
      //the toBinaryString method gives me a string with the binary
      //representation of my integer
      binaryString = new StringBuffer(Integer.toBinaryString(newRule));
      
      //but we still need to fill out the binary string with zeroes so we have
      //a full eight digits
      while (binaryString.length() < NUM_RULES)
         binaryString.insert(0, '0');
      rules = new boolean[NUM_RULES];
      
      //now just add booleans to our array based on the binary string
      for (int k = 0; k < NUM_RULES; k++)
      {
         if (binaryString.charAt(k) == '1')
            rules[k] = true;
         else
            rules[k] = false;
      }
      return true;
   }
   
   public boolean setDisplayWidth(int width)
   {
      //check for an even number and reduce if it is
      if (width % 2 == 0)
         width--;
      //then just sanity check it against max width
      if (width > MAX_DISPLAY_WIDTH)
         return false;
      displayWidth = width;
      return true;
   }
   
   public void propogateNewGeneration()
   {
      String tempGen, threeChar;
      String legend[] = {"***", "** ", "* *", "*  ", " **", " * ", 
            "  *", "   "
      };
      
      //we pad thisGen with extremeBits
      thisGen = extremeBit + extremeBit + thisGen + extremeBit + extremeBit;
      
      //to apply the rules, i have to split thisGen into strings of 3 chars
      //i loop through thisGen to do so
      tempGen = "";
      for (int k = 0; k < thisGen.length() - 2; k++)
      {
         threeChar = "" + thisGen.charAt(k) + thisGen.charAt(k+1) 
               + thisGen.charAt(k+2);
         //after the strings of three are created, I do a linear search through
         //the legend array and the apply the corresponding rule
         for (int j = 0; j < legend.length; j++)
            if ( threeChar.equals(legend[j]) )
               tempGen += boolToString(rules[j]);
      }
      //finally i have the new thisGen
      thisGen = tempGen;
      
      //before ending, i have to see what the new extremeBit is
      String extremeThree = extremeBit + extremeBit + extremeBit;
      if (extremeThree.equals(legend[0]))
         extremeBit = boolToString(rules[0]);
      else
         extremeBit = boolToString(rules[NUM_RULES - 1]);
   }
   
   public String toStringCurrentGen()
   {
      String returnString, paddingString;
      int sideGap;
      
      //sideGap will be a useful number, it tells me what is the difference
      //between the displayWidth and thisGen's length, and divides by 2 
      //because i'm centering thisGen
      sideGap = (displayWidth - thisGen.length()) / 2;
      
      //this is the case for thisGen being longer than displayWidth
      if (sideGap < 0)
      {
         returnString = thisGen.substring(thisGen.length() - displayWidth 
               - Math.abs(sideGap), thisGen.length() - Math.abs(sideGap));
         return returnString;
      }
      
      //if thisGen is shorter or equal to displayWidth, we pad it
      paddingString = "";
      for (int k = 0; k < sideGap; k++)
         paddingString += extremeBit;
      returnString = paddingString + thisGen + paddingString;
      return returnString;
   }
   
   //this resets the seed
   public void resetFirstGen()
   {
      thisGen = "*";
      extremeBit = " ";
   }
   
   //i devised this private method because i reused this code a couple times
   private String boolToString(boolean rule)
   {
      if (rule == true)
         return "*";
      else
         return " ";
   }   
}
