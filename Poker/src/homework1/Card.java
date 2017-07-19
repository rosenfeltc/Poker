/* This is the Card class that is used to create a Card object containing two integer values to represent the suit and the value of each card.
 * The methods in this class are the toString method, as well as some getters and setters that will be necessary for the functioning of the game. 
 * Coded by Christopher Rosenfelt for CSI 213*/

package homework1;

public class Card
{
	private int suit;
	private int value;
	
	// Card constructor that requires two integer variables representing the suit and value, respectively, of the card
	public Card(int s, int v)
	{
		this.suit = s;
		this.value = v;
	}
	
	// Method that returns the Card's suit and value as a string (in words)
	public String toString()
	{
		String card = "";
		
		// Assign the string value of the card based on the integer value
		if(this.value == 1 || this.value == 14)
		{
			card += "Ace";
		}
		else if(this.value > 1 && this.value < 11)
		{
			card += this.value;
		}
		else if(this.value == 11)
		{
			card += "Jack";
		}
		else if(this.value == 12)
		{
			card += "Queen";
		}
		else if(this.value == 13)
		{
			card += "King";
		}
		else
		{
			System.out.println("Invalid card value!");
			System.exit(1);
		}
		
		// All cards will contain " of " in between its value and its suit
		card += " of ";
		
		// Assign 1 of the 4 suits as a string based on the suit integer value
		if(this.suit == 1)
		{
			card += "Spades";
		}
		else if(this.suit == 2)
		{
			card += "Clubs";
		}
		else if(this.suit == 3)
		{
			card += "Hearts";
		}
		else if(this.suit == 4)
		{
			card += "Diamonds";
		}
		else
		{
			System.out.println("Invalid suit!");
			System.exit(2);
		}
		
		return card;	
	}
	
	// Obtain the value of the card. Useful for sorting and obtaining the value of the hand
	public int getValue()
	{
		return this.value;
	}
	
	// Obtain the suit of the card. Useful for obtaining the value of the hand i.e. check if hand is a "flush"
	public int getSuit()
	{
		return this.suit;
	}
	
	// Change the value of the card, will be used only to change the Ace card from a value of 1 to 14 to better
	// calculate the hand value in the Player Class
	protected void setValue(int v)
	{
		this.value = v;
	}
}
