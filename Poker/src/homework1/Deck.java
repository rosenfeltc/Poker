/* This is the Deck class that is used to create a Deck of Card object represented by an array of cards as well as an integer value representing
 *  how many cards are currently in the Deck.The methods in this class are addCards, shuffle, and deal. 
 *  Coded by Christopher Rosenfelt for CSI 213*/
package homework1;
// Import Random class for shuffling
import java.util.Random;

public class Deck
{
	private Card[] cards;
	private int howMany;
	
	// Card constructor that initializes an array of 52 Cards and an integer of 52 to keep track of how many cards are left in the deck
	public Deck()
	{
		this.cards = new Card[52];
		this.howMany = 52;
	}
	
	// Method that adds all the poker cards to a deck in a systematic and sequential order (by suit first and then ascending value)
	public void addCards()
	{
		// Ensure deck has 52 cards each round. This is important because during each round cards are dealt to players.
		this.howMany = 52;
		
		for(int i = 0; i < this.howMany; i++)
		{
			// Add cards in order by suit and ascending value, division operator + 1 for the suit and modulo operator + 1 for the value	
			this.cards[i] = new Card((i / 13) + 1, (i % 13) + 1);
		}
	}
	
	// Method that shuffles the cards randomly by moving the card at each position in the deck randomly to another position of the deck
	// switching the two cards
	public void shuffle()
	{
		// Need a random generator with a random integer as well as temporary Card variable
		// to shuffle the cards by randomly changing their positions in the deck
		Random generator = new Random();
		int random;
		Card temp;
		
		for(int i = 0; i < this.howMany; i++)
		{
			// Bounded random generator, parameter passed in is exclusive so provides random numbers from 0 - 51 which corresponds with index
			random = generator.nextInt(howMany);
			// Switch the position of the card at i index with a card at a random position
			temp = this.cards[random];
			this.cards[random] = this.cards[i];
			this.cards[i] = temp;
		}
	}
	
	// Important method that is used to deal the cards from the deck to the player and the dealer
	// It updates howMany to reflect the fact that a card dealt to a player is no longer part of the deck
	public void deal(Card[] hand)
	{
		for(int i = 0; i < hand.length; i++)
		{
			// since "--" is before the variable, it updates the number first and then goes into the index
			hand[i] = this.cards[--this.howMany];
		}
	}
}
