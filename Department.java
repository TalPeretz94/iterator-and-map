
public class Department {
	
	private String depName;  
	private String depHead;
	
	
	 
	public Department(String depName, String depHead) {
		super();
		this.depName = depName;
		this.depHead = depHead;
	}
	public String getDepName() {
		return depName;
	}
	public void setDepName(String depName) {
		this.depName = depName;
	}
	public String getDepHead() {
		return depHead;
	}
	public void setDepHead(String depHead) {
		this.depHead = depHead;
	}
	@Override
	public String toString() {
		return String.format("%-35s %-9s", depName, depHead);	}
	
	

}
