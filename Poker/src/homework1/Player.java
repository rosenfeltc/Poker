/* This is the Player class that is used to create a Player object represented by an array of cards representing the player's hand as well as
 * a String value representing the player's name and a double balance representing the player's balance. The methods in this class are makeBet,
 * a private method called sort that is used by the method calculateValue as well as some necessary getters and setters.
 *  Coded by Christopher Rosenfelt for CSI 213*/
package homework1;
//Import random class for the extra credit betting portion
import java.util.Random;

public class Player 
{
	private Card[] hand;
	private String name, cards;
	private double balance, bet;
	private boolean fold, raise, allIn;
	private int value;
	
	
	// Constructor that takes a string name, as well as an initial player balance, and an integer for the size of the playing hand
	public Player(String name, double balance, int handSize)
	{
		this.name = name;
		this.balance = balance;
		this.hand = new Card[handSize];
		this.cards = "";
		this.bet = 0;
		this.fold = false;
		this.raise = false;
		this.allIn = false;
		this.value = 0;
	}
	
	// Getter for the name of the player object
	public String getName()
	{
		return this.name;
	}
	
	// Getter for the balance of the player
	public double getBalance()
	{
		return this.balance;
	}
	
	// Setter for the balance of the player that will be continuously adjusted during the game
	public void setBalance(double amount)
	{
		this.balance = amount;
	}
	
	// Getter for the bet of the player
	public double getBet()
	{
		return this.bet;
	}
	
	// Setter for the bet of the player
	public void setBet(double bet)
	{
		this.bet = bet;
	}
	
	// Getter for the boolean fold
	public boolean getFold()
	{
		return this.fold;
	}
	
	// Setter for the boolean fold
	public void setFold(boolean fold)
	{
		this.fold = fold;
	}
	
	// Getter for the boolean raise
	public boolean getRaise()
	{
		return this.raise;
	}
	
	// Setter for the boolean raise
	public void setRaise(boolean raise)
	{
		this.raise = raise;
	}
	
	// Getter for the boolean allIn
	public boolean getAllIn()
	{
		return this.allIn;
	}
	
	// Setter for the boolean allIn
	public void setAllIn(boolean allIn)
	{
		this.allIn = allIn;
	}
	
	// Create a string that contains the cards in the hand used by method calculateValue
	private void handToString()
	{
		for(int i = 0; i < hand.length; i++)
		{
			cards += "\n" + hand[i].toString();
		}
	}
	
	// Getter for the string that contains the cards in the hand
	public String getStringHand()
	{
		return cards;
	}
	
	// Return card array so that cards can be dealt to it
	public Card[] getHand()
	{
		return this.hand;
	}
	
	// Getter for the value of the hand, used to determine the winner
	public int getHandValue()
	{
		return this.value;
	}
	
	// Initializes variables for the start of a new round
	public void newRound()
	{
		this.cards = "";
		this.bet = 0;
		this.allIn = false;
		this.fold = false;
		this.raise = false;
		this.value = 0;
	}
	// Sort the hand using bubble sort, not the most time efficient sorting algorithm at big O of n squared however
	// since the hand sizes are only of size 5 then it's not a very big deal
	private void sort()
	{
		// Temporary card variable for switching the cards
		Card temp;
		
		// Bubble sort using the value of the Cards in the hand
		for(int i = 0; i < this.hand.length; i++)
		{
			for(int j = 0; j < this.hand.length - 1 - i; j++)
			{
				if(this.hand[j].getValue() > this.hand[j+1].getValue())
				{
					temp = this.hand[j+1];
					this.hand[j+1] = this.hand[j];
					this.hand[j] = temp;
				}
			}
		}
	}
	
	// Very important method that deals with Part I of the problem. First it calls the private method handToString to save the player's
	// hand as a String. Then it figures out what type of hand the player is holding, the value of the hand, and stores it in the 
	// value variable. The value variable can later be called through its getter in order to compare it to the value of a different 
	// player's hand to determine the winner
	public void calculateValue()
	{
		// Save hand as string
		handToString();
		
		// Declare and initialize the variables that will be needed to figure out and calculate the hand
		boolean aceLow = false;
		boolean fullHouse = false;
		boolean fourOfAKind = false;
		boolean threeOfAKind = false;
		boolean twoPair = false;
		boolean pair = false;
		boolean currentCopy = false;
		
		int straight = 0;
		int flush = 0;
		int position1 = 0;
		int position2 = 0;
		
		// Adjust the value of any potential Aces from a value of 1 to 14, which will make calculating the value of the hand much easier.
		for(int i = 0; i < hand.length; i++)
		{
			if(this.hand[i].getValue() == 1)
			{
				this.hand[i].setValue(14);
			}
		}
		
		// Sort the cards in ascending value
		// A sorted hand allows us to figure out what kind of hand we are holding much easier with a loop
		sort();

		// Figure out what hand we have always keeping in mind that the hand was sorted with Ace only being high
		for(int i = 0; i < hand.length - 1; i++)
		{
			// Figure out if the current card is the same value as the successor card in order to determine what kind of "paired hand"
			// we might have, checking for pair, two-pair, three of a kind, full house and four of a kind
			if(this.hand[i].getValue() == this.hand[i+1].getValue())
			{
				// Found our first pair, keep track of the position to calculate value later
				if(!currentCopy && !pair)
				{
					position1 = i;
					currentCopy = true;
					pair = true;
				}
				// Found our second pair since our first pair is showing as having been found
				else if(!currentCopy && pair)
				{
					position2 = i;
					currentCopy = true;
					twoPair = true;
				}
				// Turns out our pair is actually a three of a kind so adjust variables accordingly
				else if(currentCopy && pair && !twoPair)
				{
					pair = false;
					position2 = i;
					threeOfAKind = true;
				}
				// Turns out the second pair in our two pair is actually a three of a kind
				else if(currentCopy && pair && twoPair)
				{
					twoPair = false;
					position2 = i;
					threeOfAKind = true;
				}
				//  Turns out our three of a kind was actually four of a kind so adjust variables accordingly
				else if(currentCopy && threeOfAKind)
				{
					position2 = i;
					threeOfAKind = false;
					fourOfAKind = true;
				}
				
				// Check for full house, placed as a separate if because need to always check at each iteration since
				// a full house can be done with the first two cards being a pair and the last three being a three of a kind
				// but also vice-versa
				if(pair && threeOfAKind)
				{
					fullHouse = true;
				}
			}
			// Switch the currentCopy boolean to false since the next successive card was not a copy in order to look for a new copy
			else
			{
					currentCopy = false;
			}
			
			// Check to see if we have a straight, this is done independently of the check for pairs 
			// Figure out if our hand has an Ace-low straight and adjust respective variables accordingly
			if(this.hand[0].getValue() == 2 && this.hand[1].getValue() == 3 && this.hand[2].getValue() == 4 && 
				this.hand[3].getValue() == 5 && this.hand[4].getValue() == 14)
			{
				straight = 5;
				aceLow = true;
			}
			// Check for other straight possibilities
			else if(this.hand[i].getValue() == (this.hand[i+1].getValue() - 1))
			{
				// First sign of a potential straight adjust by adding 2
				if(straight == 0)
				{
					straight += 2;
				}
				else
				{
					straight += 1;
				}
			}
			
			// Check for flush independently of checking for pairs or straights
			if(this.hand[i].getSuit() == this.hand[i+1].getSuit())
			{
				// First sign of a potential flush adjust by adding 2
				if(flush == 0)
				{
					flush += 2;
				}
				else
				{
					flush += 1;
				}
			}
		}
		
		// Assign points to the hand checking from best hand to worst
		// Do we have a straight flush?
		if(flush == 5 && straight == 5)
		{
			// Is it an ace low straight?
			if(aceLow)
			{
				this.value = 577;
			}
			else
			{
				this.value = 572 + this.hand[4].getValue();
			}
		}
		// Do we have a four of a kind?
		else if(fourOfAKind)
		{
			this.value = 558 + this.hand[position2].getValue();
		}
		// Do we have a full house?
		else if(fullHouse)
		{
			// position2 is where the three of a kind is and position1 is where the pair is
			this.value = 363 + 13 * this.hand[position2].getValue() + this.hand[position1].getValue();
		}
		// Do we have a flush?
		else if(flush == 5)
		{
			// Value is based on the highest card in hand, which is the last card since hand is sorted in ascending order
			this.value = 349 + this.hand[4].getValue();
		}
		// Do we have a straight?
		else if(straight == 5)
		{
			// is it ace low?
			if(aceLow)
			{
				this.value = 340;
			}
			else
			{
				// Value is based on the highest card in hand, which is the last card since hand is sorted in ascending order
				this.value = 335 + this.hand[4].getValue();
			}
		}
		// Do we have a three of a kind?
		else if(threeOfAKind)
		{
			this.value = 321 + this.hand[position2].getValue();
		}
		// Do we have a two pair?
		else if(twoPair)
		{
			// position2 points to the value of the highest pair while position1 points to the value of the lowest pair
			this.value = 28 + 20 * this.hand[position2].getValue() + this.hand[position1].getValue();
		}
		// Do we have a pair?
		else if(pair)
		{
			this.value = 14 + this.hand[position1].getValue();
		}
		// We only have a high card
		else
		{
			// High card is the last card in our hand since our hand is sorted in ascending order
			this.value = this.hand[4].getValue();
		}
	}
	
	// Method that is used only by the computer player aka the "dealer" to determine its bet
	public void makeBet(double pot, double currentBet)
	{
		// Random generator for extra credit portion
		Random generator = new Random();
		int random1, random2;
		
		// Current bet is the passed in value from the players bet, however if player checked (passed in a bet of 0) then we don't
		// want the computer to be forced to bet 0 so adjust the bet to the initial value of 5
		if(currentBet == 0)
		{
			currentBet = 5;
		}
		// Used riskFactor equation from homework a = 1000 and b = -1
		double riskFactor = 1000 * pot / this.balance - 1 * this.value;
		
		// Really good hand
		if(riskFactor < -300)
		{
			this.bet = currentBet * 5;
		}
		// Very good hand
		else if(riskFactor < -249)
		{
			this.bet = currentBet * 3;
		}
		// Good hand
		else if(riskFactor < -149)
		{
			this.bet = currentBet * 2;
		}
		// Decent hand
		else if(riskFactor < 51)
		{
			this.bet = currentBet * 1.5;
		}
		// OK hand
		else if(riskFactor < 188)
		{
			this.bet = currentBet;
		}
		// Not a good hand
		else
		{
			// Rationally should fold but randomly might bluff
			random1 = (generator.nextInt(100) + 1);
			random2 = (generator.nextInt(100) + 1);
			
			// 96% of being rational, aka 4% chance of bluffing
			if(random1 > 4)
			{
				this.bet = 0;
			}
			else
			{
				// Equal chance of bluffing by calling or by two different raising options
				if(random2 > 66)
				{
					this.bet = currentBet * 2;
				}
				else if(random2 > 33)
				{
					this.bet = currentBet * 1.5;
				}
				else
				{
					this.bet = currentBet;
				}
			}
		}
	}
}
