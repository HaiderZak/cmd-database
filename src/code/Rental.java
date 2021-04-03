package code;

import java.sql.Date;

public class Rental {
	public Integer archID;
	public Integer overdueFee;
	public Integer amountOwing;
	public boolean has_paid;
	public boolean has_checked;
	public Date date;
	public Rental(int archID, int overdueFee, int amountOwing, boolean has_paid, boolean has_checked, Date date) {
		this.archID = archID;
		this.overdueFee = overdueFee;
		this.amountOwing = amountOwing;
		this.has_paid = has_paid;
		this.has_checked = has_checked;
		this.date = date;
	}
	public int getArchID() {
		return archID;
	}
	public int getOverdueFee() {
		return overdueFee;
	}
	public int getAmountOwing() {
		return amountOwing;
	}
	public boolean getHasPaid() {
		return has_paid;
	}
	public boolean getHasChecked() {
		return has_checked;
	}
	public Date getDate() {
		return date;
	}
}
