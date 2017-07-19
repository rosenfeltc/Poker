/* This is the Game class that contains the main method which is used to execute/play the game. The game class uses its own variables and 
 * static methods as well as the methods of the other classes in the same package.
 *  Coded by Christopher Rosenfelt for CSI 213*/
package homework1;

import javax.swing.JOptionPane;

public class Game
{
	// Define some constants that can be adjusted to change some of the game settings if necessary
	// Initial bet (aka the blind) is set at 5% of the initial balance (in this case $5)
	final static double INITIAL_BALANCE = 100;
	final static double INITIAL_BET = INITIAL_BALANCE * 0.05;
	final static int HAND_SIZE = 5;
	final static String[] OPTIONS = {"Fold", "Call"};
	
	public static void main(String[] args)
	{
		// Create the deck of cards, the computer player (the dealer), and the necessary in-game variables
		Deck deck = new Deck();
		Player dealer = new Player("Dealer", INITIAL_BALANCE, HAND_SIZE);
		boolean odd = true;
		double pot;
		String name;
		
		// Start the game, get player's name and initialize the player
		JOptionPane.showMessageDialog(null, "Welcome to 5 card Poker!");
		name = JOptionPane.showInputDialog("What is your name?");
		Player player = new Player(name, INITIAL_BALANCE, HAND_SIZE);
		JOptionPane.showMessageDialog(null, "Your starting balance is $" + player.getBalance() +"\n\nBest of luck, " + player.getName() +"!");
		
		// Game execution happens during the while loop as long as both players have a balance remaining
		while(dealer.getBalance() != 0 && player.getBalance() != 0)
		{
			// Beginning of each round, add new cards to the deck and shuffle them, set pertinent variables to starting values for each new round
			pot = newRound(deck, dealer, player);
			
			// Blind round
			pot = blindRound(odd, pot, dealer, player);
			
			// Deal the cards
			dealCards(deck, pot, dealer, player);
			
			// Post Blind round as long as nobody has gone all in during the blind round
			if(!dealer.getAllIn() && !player.getAllIn())
			{
				pot = postBlindRound(odd, pot, dealer, player);
			}
			
			// Next round of betting as long as nobody has folded or gone all in
			if(!dealer.getFold() && !player.getFold() && !dealer.getAllIn() && !player.getAllIn())
			{
				pot = nextRound(odd, pot, dealer, player);
			}
			
			// Potential final betting round if previous round had a raise and nobody has folded or gone all in
			if((dealer.getRaise() || player.getRaise()) && !dealer.getFold() && !player.getFold() && !dealer.getAllIn() && !player.getAllIn())
			{
				pot = finalRaiseRound(pot, dealer, player);
			}
			
			// Potential all in round if nobody folded
			if((dealer.getAllIn() || player.getAllIn()) && !dealer.getFold() && !player.getFold())
			{
				pot = allInRound(pot, dealer, player);
			}
			
			// Decision round
			decision(pot, dealer, player);
			
			//Change who goes first in the next round
			odd = !odd;
		}
		
		// Display the results of the game
		JOptionPane.showMessageDialog(null, "The final standings\n\n" + dealer.getName() + "'s balance: $" + dealer.getBalance() + "\n\n" +
				player.getName() + "'s balance: $" + player.getBalance());
		
		// Who won the game?
		if(dealer.getBalance() == 0)
		{
			JOptionPane.showMessageDialog(null, "Congratulations " + player.getName() + " you've won!");
		}
		else
		{
			JOptionPane.showMessageDialog(null,"Sorry " + player.getName() + " you've lost.");
		}
	}
	
	// Object variables are passed by reference so the deck, dealer and player objects are pointing to the same
	// instances that were initialized in the main method
	public static int newRound(Deck deck, Player dealer, Player player)
	{
		// Preparing variables for the new round
		deck.addCards();
		deck.shuffle();
		dealer.newRound();
		player.newRound();
		
		// The initial display for each round
		JOptionPane.showMessageDialog(null, "The current standings\n\n" + dealer.getName() + "'s balance: $" + dealer.getBalance() + "\n\n" +
										player.getName() + "'s balance: $" + player.getBalance());
		JOptionPane.showMessageDialog(null, "The cards are being shuffled...");
		
		// Pot begins at zero each round
		return 0;
	}
	
	// The blind round, in which one of the players makes the initial blind bet
	public static double blindRound(boolean odd, double pot, Player dealer, Player player)
	{
		// Player bets the initial blind on odd rounds
		if(odd)
		{
			// The blind
			player.setBet(INITIAL_BET);
			// Is the blind equal to or more than available in balance? All in!
			if(player.getBet() >= player.getBalance())
			{
				// Adjust the bet down to what is available
				player.setBet(player.getBalance());
				JOptionPane.showMessageDialog(null, player.getName() + " has gone all in with $" + player.getBet());
				player.setAllIn(true);
			}
			else
			{
				JOptionPane.showMessageDialog(null, player.getName() + " bets blind of $" + player.getBet());
			}
							
			// Adjust pot and player's balance accordingly
			pot += player.getBet();
			player.setBalance(player.getBalance() - player.getBet());
		}
		// Dealer bets the initial blind on even rounds
		else
		{
			// The blind
			dealer.setBet(INITIAL_BET);
			// Is the blind equal to or more than available in balance? All in!
			if(dealer.getBet() >= dealer.getBalance())
			{
				// Adjust the bet down to what is available
				dealer.setBet(dealer.getBalance());
				JOptionPane.showMessageDialog(null, dealer.getName() + " has gone all in with $" + dealer.getBet());
				dealer.setAllIn(true);
			}
			else
			{
				JOptionPane.showMessageDialog(null, dealer.getName() + " bets blind of $" + dealer.getBet());
			}
						
			// Adjust pot and dealer's balance accordingly
			pot += dealer.getBet();
			dealer.setBalance(dealer.getBalance() - dealer.getBet());
		}
		
		return pot;
	}
	
	// Method that deals the cards, calculates the point value and string of the players' hands
	public static void dealCards(Deck deck, double pot, Player dealer, Player player)
	{
		// Deal the cards to player and then to dealer and then calculate their respective values
		JOptionPane.showMessageDialog(null, "\nDealing cards...\n");
		deck.deal(player.getHand());
		deck.deal(dealer.getHand());
		player.calculateValue();
		dealer.calculateValue();
					
		// Display player's hand and general info to the player
		JOptionPane.showMessageDialog(null, player.getName() + ", your hand consists of:" + player.getStringHand() +
										"\n\nThe pot amount is : $" + pot + "\n\nYour current balance is: $" + player.getBalance());
	}
	
	// First betting round after the blind was made
	public static double postBlindRound(boolean odd, double pot, Player dealer, Player player)
	{	
		// Dealer makes initial post-blind bet on odd rounds
		if(odd)
		{
			// Determine bet with the makeBet method
			dealer.makeBet(pot, player.getBet());
				
			// Is the dealer's bet equal to or more than what is available in balance? All in!
			if(dealer.getBet() >= dealer.getBalance())
			{
				dealer.setBet(dealer.getBalance());
				// Dealer has gone all in but if amount is less than player's bet then the player needs returned the difference to account
				if(dealer.getBet() < player.getBet())
				{
					pot -= (player.getBet() - dealer.getBet());
					player.setBalance(player.getBalance() + (player.getBet() - dealer.getBet()));
				}
						
				JOptionPane.showMessageDialog(null, dealer.getName() + " has gone all in with $" + dealer.getBet());
				dealer.setAllIn(true);
			}
			else if(dealer.getBet() == 0)
			{
				// Dealer folds
				JOptionPane.showMessageDialog(null, dealer.getName() + " folds.");
				dealer.setFold(true);
			}
			
			// Add bet to the pot and take the bet from the dealer's balance
			pot += dealer.getBet();
			dealer.setBalance(dealer.getBalance() - dealer.getBet());
						
			// In this round this would mean that dealer is just calling the blind
			if(dealer.getBet() == player.getBet())
			{
				JOptionPane.showMessageDialog(null, dealer.getName() + " calls the blind of $" + dealer.getBet());
				dealer.setBet(0);
			}		
			// Check to see if dealer bet/raised after posting the blind
			else if(dealer.getBet() > player.getBet()) 
			{
				JOptionPane.showMessageDialog(null, dealer.getName() + " calls the blind of $" + player.getBet() + " and raises $" + 
												(dealer.getBet() - player.getBet()));
				// Adjust dealer's bet to the amount raised over the blind for next round
				dealer.setBet(dealer.getBet() - player.getBet());
			}
		}
		// Player goes second on even rounds
		else
		{
			player.setBet(Double.parseDouble(JOptionPane.showInputDialog(dealer.getName() + " bet $" + dealer.getBet() +
					"\n\nYour hand consists of:" + player.getStringHand() + "\n\nThe pot balance is $" + pot + 
					"\n\nYour current balance is $" + player.getBalance() + 
					"\n\nHow much would you like to bet? (Enter amount less than bet to fold)")));
			
			// If player bet more than available then all in!
			if(player.getBet() >= player.getBalance())
			{
				player.setBet(player.getBalance());
				// Player has gone all in but if amount is less than dealer bet then dealer needs returned the difference to account
				if(player.getBet() < dealer.getBet())
				{
					pot -= dealer.getBet() - player.getBet();
					dealer.setBalance(dealer.getBalance() + (dealer.getBet() - player.getBet()));
				}
				
				JOptionPane.showMessageDialog(null, player.getName() + " has gone all in with $" + player.getBet());
				player.setAllIn(true);
			}
			// Assuming not all in - that's why else-if statement
			else if(player.getBet() < dealer.getBet())
			{
				// Player folds so bet is adjusted to 0 so it doesn't affect the pot
				JOptionPane.showMessageDialog(null, player.getName() + " folds");
				player.setFold(true);
				player.setBet(0);
			}
			
			// Add bet to the pot and take the bet from the player's balance
			pot += player.getBet();
			player.setBalance(player.getBalance() - player.getBet());
			
			// This means that player called the blind but checked on bet
			if(player.getBet() == dealer.getBet())
			{
				JOptionPane.showMessageDialog(null, player.getName() + " calls the blind of $" + player.getBet());
				player.setBet(0);
			}
			// Check to see if player bet more than just the blind
			else if(player.getBet() > dealer.getBet()) 
			{
				JOptionPane.showMessageDialog(null, player.getName() + " calls the blind of $" + dealer.getBet() + " and raises $" +
												(player.getBet() - dealer.getBet()));
			// Adjust player's bet to the amount that was raised for next round
				player.setBet(player.getBet() - dealer.getBet());
			}
		}
					
		return pot;
	}
	
	public static double nextRound(boolean odd, double pot, Player dealer, Player player)
	{	
		// Player goes first on next round on odd rounds
		if(odd)
		{
			// Did dealer previously check by posting just the blind?
			if(dealer.getBet() == 0)
			{
				player.setBet(Double.parseDouble(JOptionPane.showInputDialog(dealer.getName() + " checked" +
						"\n\nYour hand consists of:" + player.getStringHand() + "\n\nThe pot balance is $" + pot +
						"\n\nYour current balance is $" + player.getBalance() +
						"\n\nHow much would you like to bet? (Enter amount less than bet to fold)")));
			}
			else
			{
				player.setBet(Double.parseDouble(JOptionPane.showInputDialog(dealer.getName() + " bet $" + dealer.getBet() +
						"\n\nYour hand consists of:" + player.getStringHand() + "\n\nThe pot balance is $" + pot +
						"\n\nYour current balance is $" + player.getBalance() +
						"\n\nHow much would you like to bet? (Enter amount less than bet to fold)")));
			}
					
			if(player.getBet() >= player.getBalance())
			{
				player.setBet(player.getBalance());
				// Player has gone all in but if amount is less than dealer bet then dealer needs returned the difference to account
				if(player.getBet() < dealer.getBet())
				{
					pot -= dealer.getBet() - player.getBet();
					dealer.setBalance(dealer.getBalance() + (dealer.getBet() - player.getBet()));
				}
				
				JOptionPane.showMessageDialog(null, player.getName() + " has gone all in with $" + player.getBet());
				player.setAllIn(true);
			}
			// Assuming not all in
			else if(player.getBet() < dealer.getBet())
			{
				// Player folds so bet is adjusted to 0 so it doesn't affect the pot
				JOptionPane.showMessageDialog(null, player.getName() + " folds");
				player.setFold(true);
				player.setBet(0);
			}
			
			// Add bet to the pot and take the bet from the player's balance
			pot += player.getBet();
			player.setBalance(player.getBalance() - player.getBet());
			
			// Player calls the bet
			if(player.getBet() == dealer.getBet())
			{
				if(dealer.getBet() == 0)
				{
					JOptionPane.showMessageDialog(null, player.getName() + " checks");
				}
				else
				{
					JOptionPane.showMessageDialog(null, player.getName() + " calls the $" + player.getBet());
				}
			}
			// Check to see if player raised with the bet
			else if(player.getBet() > dealer.getBet()) 
			{
				if(dealer.getBet() == 0)
				{
					// Dealer previously checked so this is considered just a bet
					JOptionPane.showMessageDialog(null, player.getName() + " bets $" + player.getBet());
				}
				else
				{
					// Dealer previously raised on the blind so this is a new raise
					JOptionPane.showMessageDialog(null, player.getName() + " calls the $" + dealer.getBet() + " and raises $" +
							(player.getBet() - dealer.getBet()));
				}
				player.setRaise(true);
				player.setBet(player.getBet() - dealer.getBet());
			}
		}
		//Dealer goes first on next round on even rounds
		else
		{
			// Obtain the bet amount from the makeBet method
			dealer.makeBet(pot, player.getBet());
							
			// Is the dealer's bet equal to or more than what is available in balance? All in!
			if(dealer.getBet() >= dealer.getBalance())
			{
				dealer.setBet(dealer.getBalance());
				// Dealer has gone all in but if amount is less than player's bet then the player needs returned the difference to account
				if(dealer.getBet() < player.getBet())
				{
					pot -= (player.getBet() - dealer.getBet());
					player.setBalance(player.getBalance() + (player.getBet() - dealer.getBet()));
				}
									
				JOptionPane.showMessageDialog(null, dealer.getName() + " has gone all in with $" + dealer.getBet());
				dealer.setAllIn(true);
			}
			// Assuming not all in then dealer bet 0 when player previously bet above 0
			else if(dealer.getBet() < player.getBet())
			{
				// Dealer folds
				JOptionPane.showMessageDialog(null, dealer.getName() + " folds.");
				dealer.setFold(true);
			}
			
			// Add bet to the pot and take the bet from the dealer's balance
			pot += dealer.getBet();
			dealer.setBalance(dealer.getBalance() - dealer.getBet());
			
			if(dealer.getBet() == player.getBet())
			{
				if(player.getBet() == 0)
				{
					// Dealer calls the check
					JOptionPane.showMessageDialog(null, dealer.getName() + " checks");
				}
				else
				{
					JOptionPane.showMessageDialog(null, dealer.getName() + " calls $" + dealer.getBet());
				}
			}					
			// Check to see if dealer raised with the bet
			else if(dealer.getBet() > player.getBet()) 
			{
				JOptionPane.showMessageDialog(null, dealer.getName() + " bets $" + dealer.getBet() + " raising $" + 
				(dealer.getBet() - player.getBet()));
				dealer.setRaise(true);
				dealer.setBet(dealer.getBet() - player.getBet());
			}
		}
		
		return pot;
	}
	
	// Method that is called only if there was a raise in nextRound and no all ins or folds have happened
	public static double finalRaiseRound(double pot, Player dealer, Player player)
	{	
		// Was dealer the one to raise?
		if(dealer.getRaise())
		{
			// Use option dialog box to limit the user to either folding or calling, raising is not allowed
			player.setBet(JOptionPane.showOptionDialog(null, dealer.getName() + " raised $" + dealer.getBet() +
					"\n\nYour hand consists of:" + player.getStringHand() + "\n\nThe pot balance is $" + pot +
					"\n\nYour current balance is $" + player.getBalance(), "Please select option",
					JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, OPTIONS, OPTIONS[0]));
			if(player.getBet() == 0)
			{
				// Player pressed the fold option
				player.setFold(true);
			}
			else
			{
				// Adjust bet back to the right amount since the call option of the dialog box automatically returns 1
				player.setBet(dealer.getBet());
						
				// Check to see if player was forced to go all in but do not set all in variable to true since this is last round of betting
				if(player.getBet() >= player.getBalance())
				{
					player.setBet(player.getBalance());
					// Player has gone all in but if amount is less than dealer bet then dealer needs returned the difference to account
					if(player.getBet() < dealer.getBet())
					{
						pot -= dealer.getBet() - player.getBet();
						dealer.setBalance(dealer.getBalance() + (dealer.getBet() - player.getBet()));
					}
					JOptionPane.showMessageDialog(null, player.getName() + " calls and goes all in with: $" + player.getBet());
				}
				else
				{
					JOptionPane.showMessageDialog(null, player.getName() + " calls $" + player.getBet());
				}
							
				// Add bet to the pot and take the bet from the player's balance
				pot += player.getBet();
				player.setBalance((player.getBalance() - player.getBet()));
			}
		}
		// Did the player raise?
		else
		{
			// Call the makeBet method to calculate dealer's bet
			dealer.makeBet(pot, player.getBet());
						
			// Dealer calls only, since not allowed to raise again
			if(dealer.getBet() >= player.getBet())
			{
				// Adjusting the bet back to just a call
				dealer.setBet(player.getBet());
								
				// Ensure dealer has the amount to call otherwise all in
				if(dealer.getBet() >= dealer.getBalance())
				{
					dealer.setBet(dealer.getBalance());
					// Dealer has gone all in but if amount is less than player bet then player needs returned the difference to account
					if(dealer.getBet() < player.getBet())
					{
						pot -= player.getBet() - dealer.getBet();
						player.setBalance(player.getBalance() + (player.getBet() - dealer.getBet()));
					}
					JOptionPane.showMessageDialog(null, dealer.getName() + " calls and goes all in with $" + dealer.getBet());
				}
				else
				{
					JOptionPane.showMessageDialog(null, dealer.getName() + " calls $" + dealer.getBet());
				}
								
				// Add bet to the pot and take the bet from the player's balance
				pot += dealer.getBet();
				dealer.setBalance(dealer.getBalance() - dealer.getBet());
			}
			else
			{
				// Dealer chose to fold
				JOptionPane.showMessageDialog(null, dealer.getName() + " folds");
				dealer.setFold(true);
			}
		}			
	return pot;
	}
	
	// Method that is only activated if either player was forced to go all in
	public static double allInRound(double pot, Player dealer, Player player)
	{
		// Did player go all in?
		if(player.getAllIn())
		{
			// Call the makeBet method to calculate the dealer's bet
			dealer.makeBet(pot, player.getBet());
						
			// Dealer calls only, since not allowed to raise when player has gone all in
			if(dealer.getBet() >= player.getBet())
			{
				// Adjusting the bet back to just a call
				dealer.setBet(player.getBet());
							
				// Ensure dealer has the amount to call otherwise dealer is all in
				if(dealer.getBet() >= dealer.getBalance())
				{
					dealer.setBet(dealer.getBalance());
					// Dealer has gone all in but if amount is less than player bet then player needs returned the difference to account
					if(dealer.getBet() < player.getBet())
					{
						pot -= player.getBet() - dealer.getBet();
						player.setBalance(player.getBalance() + (player.getBet() - dealer.getBet()));
					}
					JOptionPane.showMessageDialog(null, dealer.getName() + " calls and goes all in with $" + dealer.getBet());
				}
				else
				{
					JOptionPane.showMessageDialog(null, dealer.getName() + " calls $" + dealer.getBet());
				}
							
				// Add bet to the pot and take the bet from the player's balance
				pot += dealer.getBet();
				dealer.setBalance(dealer.getBalance() - dealer.getBet());
			}
			else
			{
				JOptionPane.showMessageDialog(null, dealer.getName() + " folds");
				dealer.setFold(true);
			}
		}
		// Did dealer go all in?
		else
		{
			player.setBet(JOptionPane.showOptionDialog(null, dealer.getName() + " went all in with $" + dealer.getBet() +
					"\n\nYour hand consists of:" + player.getStringHand() + "\n\nThe pot balance is $" + pot +
					"\n\nYour current balance is $" + player.getBalance(), "Please select option",
					JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, OPTIONS, OPTIONS[0]));
			if(player.getBet() == 0)
			{
				// Player pressed the fold option
				player.setFold(true);
			}
			else
			{
				// Adjust bet back to the right amount since the call option automatically returns 1
				player.setBet(dealer.getBet());
				
				// Check to see if player was forced to go all in
				if(player.getBet() >= player.getBalance())
				{
					player.setBet(player.getBalance());
					// Player has gone all in but if amount is less than dealer bet then dealer needs returned the difference to account
					if(player.getBet() < dealer.getBet())
					{
						pot -= dealer.getBet() - player.getBet();
						dealer.setBalance(dealer.getBalance() + (dealer.getBet() - player.getBet()));
					}
					JOptionPane.showMessageDialog(null, player.getName() + " calls and goes all in with: $" + player.getBet());
				}
				else
				{
					JOptionPane.showMessageDialog(null, player.getName() + " calls $" + player.getBet());
				}
					
				// Add bet to the pot and take the bet from the player's balance
				pot += player.getBet();
				player.setBalance((player.getBalance() - player.getBet()));
			}
		}
		
		return pot;
	}
	
	public static void decision(double pot, Player dealer, Player player)
	{
		// Check for any folds
		if(player.getFold() || dealer.getFold())
		{
			if(player.getFold())
			{
				// Distribute the pot amount to dealer's balance
				JOptionPane.showMessageDialog(null, dealer.getName() + " wins due to " + player.getName() + "'s fold");
				dealer.setBalance(dealer.getBalance() + pot);
			}
			else
			{
				// Distribute the pot amount to player's balance
				JOptionPane.showMessageDialog(null, player.getName() + " wins due to " + dealer.getName() + "'s fold");
				player.setBalance(player.getBalance() + pot);
			}
		}
		// If no folds have happened
		else
		{
			// Display the cards for both hands
			JOptionPane.showMessageDialog(null, dealer.getName() + "'s hand is:" + dealer.getStringHand() + "\n\n" + player.getName() +
											"'s hand is:" + player.getStringHand());
			// Use the previously calculated hand values to determine the winner and distribute the pot accordingly
			if(dealer.getHandValue() > player.getHandValue())
			{
				// Dealer wins, pot is added to dealer's balance
				JOptionPane.showMessageDialog(null, dealer.getName() + " wins the hand");
				dealer.setBalance(dealer.getBalance() + pot);
			}
			else if(player.getHandValue() > dealer.getHandValue())
			{
				// Player wins, pot is added to player's balance
				JOptionPane.showMessageDialog(null, player.getName() + " wins the hand");
				player.setBalance(player.getBalance() + pot);
			}
			else
			{
				// It's a tie, player and dealer get half the pot
				JOptionPane.showMessageDialog(null, "The hand is a draw!");
				player.setBalance(player.getBalance() + pot / 2);
				dealer.setBalance(dealer.getBalance() + pot / 2);
			}
		}
	}
}
