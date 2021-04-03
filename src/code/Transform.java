package code;

public class Transform {
	Integer b_arch_ID;
	Integer a_arch_ID;
	Integer empID;
	String tDate;
	public Transform(Integer b_arch_ID, Integer a_arch_ID, Integer empID, String tDate){
		this.b_arch_ID = b_arch_ID;
		this.a_arch_ID = a_arch_ID;
		this.empID = empID;
		this.tDate = tDate;
	}
	public int getBArchID() {
		return b_arch_ID;
	}
	public int getAArchID() {
		return a_arch_ID;
	}
	public int getEmpID() {
		return empID;
	}
	public String getDate() {
		return tDate;
	}
}
