
public class Worker <T>implements Comparable<Worker>{
	
	private String name;
	private T dep; 
	private int salary;
	
	
	public Worker(String name, T dep, int salary) {
		super();
		this.name = name;
		this.dep = dep;
		this.salary = salary;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public T getDep() {
		return this.dep;
	}
	public void setDep(T dep) {
		this.dep = dep; 
	}
	public int getSalary() {
		return salary;
	}
	public void setSalary(int salary) {
		this.salary = salary;
	}
	@Override
	public String toString() {
		return String.format("%-20s%-38s%10d", name, dep.toString(), salary);
		}
	
	@Override
	public int compareTo(Worker other) {
		if(this.getName().compareTo(other.getName())==0){
			return -1;
		}
		return this.getName().compareTo(other.getName());
	}
	


}
