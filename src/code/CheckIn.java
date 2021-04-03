package code;

public class CheckIn {
	Integer sin;
	Integer arch_id;
	Integer emp_id;
	public CheckIn(Integer sin, Integer arch_id, Integer emp_id) {
		this.sin = sin;
		this.arch_id = arch_id;
		this.emp_id = emp_id;
	}
	public int getSIN() {
		return sin;
	}
	public int getArchID() {
		return arch_id;
	}
	public int getEmpID() {
		return emp_id;
	}
}
