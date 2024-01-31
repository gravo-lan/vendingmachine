//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
//
// Class          : vendingMachine.Model
//
// Author         : Richard E. Pattis
//                  Computer Science Department
//                  Carnegie Mellon University
//                  5000 Forbes Avenue
//                  Pittsburgh, PA 15213-3891
//                  e-mail: pattis@cs.cmu.edu
//
// Maintainer     : Author
//
//
// Description:
//
//   The Model for the VendingMachine package implements the guts of the
// vending machine: it responds to presses of buttons created by the
// Conroller (deposit, cancel, buy), and tells the View when it needs
// to update its display (calling the update in view, which calls the
// accessor methods in these classes)
// 
//   Note that "no access modifier" means that the method is package
// friendly: this means the member is public to all other classes in
// the calculator package, but private elsewhere.
//
// Future Plans   : More Comments
//                  Increase price as stock goes down
//                  Decrease price if being outsold by competition
//                  Allow option to purchase even if full change cannot 
//                    be returned (purchaser pays a premium to quench thirst)
//                  Allow user to enter 2 x money and gamble: 1/2 time
//                    all money returned with product; 1/2 time no money and
//                    no product returned
//
// Program History:
//   9/20/01: R. Pattis - Operational for 15-100
//   2/10/02: R. Pattis - Fixed Bug in change-making method
//
//
//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////


package vendingMachine;


import java.lang.Math;
import java.util.*;


public class Model {
	//Define fields (all instance variables)
	
	private View view;         // Model must tell View when to update itself
	
	private int cokeLeft, pepsiLeft, quartersLeft, dimesLeft, nickelsLeft, quartersChange, dimesChange,nickelsChange;
	private final int cokePrice, pepsiPrice;
	private double deposited;
	private String message;
	
	//I defined about 10 more fields
	
	//Define constructor
	public Model() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Vending Machine Initialization\n  Enter quarters   to start(10): ");
		quartersLeft = scanner.nextInt();
		System.out.print("  Enter dimes   to start(10): ");
		dimesLeft = scanner.nextInt();
		System.out.print("  Enter nickels   to start(10): ");
		nickelsLeft = scanner.nextInt();
		System.out.print("  Enter pepsi   to start(5): ");
		pepsiLeft = scanner.nextInt();
		System.out.print("  Enter coke   to start(5): ");
		cokeLeft = scanner.nextInt();
		System.out.print("  Enter pepsi cost in cents(85): ");
		pepsiPrice = scanner.nextInt();
		System.out.print("  Enter coke  cost in cents(95): ");
		cokePrice = scanner.nextInt();
		quartersChange = 0;
		dimesChange=0;
		nickelsChange = 0;
		deposited = 0.25 * quartersLeft + 0.1 * dimesLeft + 0.05 * nickelsLeft;
	}


	public void buy(String product) {
		switch (product.toLowerCase()) {
			case "coke" -> {
				if (cokeLeft<1) {
					message = "Out of Coke";
				}
				else if (canMakeChange((int) (deposited * 100) - cokePrice)) message = "Cannot make correct change: ";
				else if (deposited*100>cokePrice) {
					cokeLeft--;
					message = "Coke Bought: ";
					buyHelper(cokePrice);
				}
				else message = "Deposit more money";
			}
			case "pepsi" -> {
				if (pepsiLeft<1) message = "Out of Pepsi";
				else if (canMakeChange((int) (deposited * 100) - cokePrice)) message = "Cannot make correct change: ";
				else if (deposited*100>pepsiPrice) {
					pepsiLeft--;
					message = "Pepsi Bought: ";
					buyHelper(pepsiPrice);
				}
				else message = "Deposit more money";
			}
		}
		view.update();
	}

	//Refer to the view (used to call update after each button press)
	public void addView(View v)
	{view = v;}
	
	//Define required methods: mutators (setters) and accessors (getters)
	
	//Represent "interesting" state of vending machine
	public String toString()
	{
		return "Vending Machine State: \n" +
			"  Coke     Left      = " + cokeLeft     + "\n" +
			"  Pepsi    Left      = " + pepsiLeft    + "\n" +
			"  Quarters Left      = " + quartersLeft + "\n" +
			"  Dimes    Left      = " + dimesLeft    + "\n" +
			"  Nickels  Left      = " + nickelsLeft  + "\n";
		//Display any other instance variables that you declare too
	}

	public int getPepsiPrice() {
		return pepsiPrice;
	}

	public int getCokePrice() {
		return cokePrice;
	}

	public int getPepsiLeft() {
		return pepsiLeft;
	}

	public int getCokeLeft() {
		return cokeLeft;
	}

	public void cancel() {
		message="Cancelled: ";
		if (nickelsLeft>0&&dimesLeft>0&&quartersLeft>0) message+="returning =";
		if (quartersLeft>0) {
			if (quartersLeft == 1) message += "  1 quarter";
			else message += "  " + quartersLeft + " quarters";
		}
		if (dimesLeft>0) {
			if(dimesLeft == 1) message+= "  1 dime";
			else message += "  " + dimesLeft + " dimes";
		}
		if (nickelsLeft>0) {
			if (nickelsLeft == 1) message+= "  1 nickel";
			else message += "  " + nickelsLeft + " nickels";
		}
		quartersLeft=0;
		dimesLeft=0;
		nickelsLeft=0;
		deposited=0;
		view.update();
	}

	public void deposit(int amount) {
		switch (amount) {
			case 5 -> nickelsLeft++;
			case 10 -> dimesLeft++;
			case 25 -> quartersLeft++;
		}
		deposited+= (double) amount /100;
		message = amount + " cents deposited";
		view.update();
	}

	public String getDeposited() {
		if (Math.round(deposited*100)%100==0) return "$" + (double) Math.round(deposited*100)/100 + "0";
		else if (Math.round(deposited*100)%10==0) return "$" + (double) Math.round(deposited*100)/100 + "0";
		else return "$" + (double) Math.round(deposited*100)/100;
	}

	public String getMessage() {
		return message;
	}

	//Define helper methods

	private void buyHelper(int price) {
		int i = price;
		cokeLeft--;
		deposited-= (double) price /100;
		while (i>25&&quartersLeft>0) {
			i-=25;
			quartersLeft--;
		}
		while (i>10&&dimesLeft>0) {
			i-=10;
			dimesLeft--;
		}
		while (i>5&&nickelsLeft>0) {
			i-=5;
			nickelsLeft--;
		}
		if (deposited>0) {
			message+="change =";
			int q = 0,d = 0,n = 0;
			while(deposited>=0.25) {
				q++;
				deposited-=0.25;
			}
			while(deposited>=0.1) {
				d++;
				deposited-=0.1;
			}
			while(Math.round(deposited*100)>=5) {
				n++;
				deposited-=0.05;
			}
			if (q>0) {
				if (q==1) message+=" 1 quarter";
				else message+=" " + q + " quarters";
			}
			if (d>0) {
				if (d==1) message+=" 1 dime";
				else message+=" " + d + " dimes";
			}
			if (n>0) {
				if (n==1) message+=" 1 nickel";
				else message+=" " + n + " nickels";
			}
			System.out.println(deposited);
		}
		else message+="(no change)";
		view.update();
	}

	private boolean canMakeChange(int amount) {
		int originalAmount = amount;
		int maxQChange = Math.min(quartersLeft, amount / 25);

		for(quartersChange = maxQChange; quartersChange >= 0; --quartersChange) {
			quartersLeft -= quartersChange;
			amount -= 25 * quartersChange;
			dimesChange = Math.min(dimesLeft, amount / 10);
			dimesLeft -= dimesChange;
			amount -= 10 * dimesChange;
			nickelsChange = Math.min(nickelsLeft, amount / 5);
			nickelsLeft -= nickelsChange;
			amount -= 5 * nickelsChange;
			if (amount == 0) {
				return false;
			}

			quartersLeft += quartersChange;
			dimesLeft += dimesChange;
			nickelsLeft += nickelsChange;
			amount = originalAmount;
		}

		return true;
	}
}
