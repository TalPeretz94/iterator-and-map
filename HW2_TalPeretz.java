import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class HW2_TalPeretz {

	private static final String FILEMAPWORKER = "workers.dat";
	
	private static final int NAMESIZE = 15;
	private static final int DEPSIZE = 20;
	private static final int HEADSIZE = 15;

	private static final int FILEDEPSIZE = Character.BYTES * (NAMESIZE + DEPSIZE + HEADSIZE) + Integer.BYTES;
	private static final int FILESTRINGSIZE = Character.BYTES * (NAMESIZE + DEPSIZE) + Integer.BYTES;

	/** Create the list of workers */
	public static ArrayList<Worker<?>> createList(char c) {
		final String[] aNames = { "Elvis", "Samba", "Bamba", "Bisli", "Kinder Bueno","Elvis" };
		final String[] aDepNames = { "Software Engineering", "Mechanical Engineering",
				"Industrial And Medical Engineering", "Electrical Engineering", "Electrical Engineering","Software Engineering" };
		final String[] aDepHeads = { "Boss1", "Boss2", "Boss3", "Boss4", "Boss4","Boss1" };
		final int[] aSalaries = { 1000, 2000, 3000, 4000, 1000,9999 };

		ArrayList<Worker<?>> lst = new ArrayList<>();

		for (int i = 0; i < aNames.length; i++) {
			if (c == '1')
				// dep as class Department
				lst.add(new Worker<Department>(aNames[i], new Department(aDepNames[i], aDepHeads[i]), aSalaries[i]));
			else
				// dep as String
				lst.add(new Worker<String>(aNames[i], aDepNames[i], aSalaries[i]));
		}

		return lst;
	}

	public static void printEmployye(ArrayList<Worker<?>> workersArr) {
		for (int i = 0; i < workersArr.size(); i++)
			System.out.println(workersArr.get(i).toString());
	}

	// 2.3
	public static <K> Map<Integer, K> createMap(ArrayList<K> workersArr) {

		int index = 0;
		Set<K> tree = new TreeSet<>(workersArr);
		Map<Integer, K> workerMap = new TreeMap<>();
		Iterator<K> it = tree.iterator();
		while (it.hasNext()) {
			index++;
			workerMap.put(index, it.next());//move the workers from the set to the map
		}
		return workerMap;

	}

	// 2.4
	public static void printMapBackWard(Map<?, ?> workerMap) {
		ArrayList<?> WorkersArr = new ArrayList<>(workerMap.entrySet());
		ListIterator<?> lit = WorkersArr.listIterator(WorkersArr.size());
		while (lit.hasPrevious()) {

			Entry<?, ?> entry = (Entry<?, ?>) lit.previous();
			System.out.println(entry.getKey() + ":" + entry.getValue());

		}

	}

	// 2.5
	public static void saveMapToFile(Map<?, ?> workerMap, char ch, String fileName)
			throws FileNotFoundException, IOException {

		try (DataOutputStream o1 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)))) {

			for (Object worker : workerMap.values()) {
				saveOneWorkerMap(o1, (Worker<?>) worker, ch);
			}

		}

	}

	// 2.5
	public static void saveOneWorkerMap(DataOutput dop, Worker<?> worker, char ch) throws IOException {

		FixedLengthStringIO.writeFixedLengthString(worker.getName(), NAMESIZE, dop);
		if (ch == '1') { //check if the worker with Department as String or Department and save according to this
			FixedLengthStringIO.writeFixedLengthString(((Department) worker.getDep()).getDepName(), DEPSIZE, dop);
			FixedLengthStringIO.writeFixedLengthString(((Department) worker.getDep()).getDepHead(), HEADSIZE, dop);

		} else {
			FixedLengthStringIO.writeFixedLengthString(((String) worker.getDep()), DEPSIZE, dop);
		}
		dop.writeInt((worker.getSalary()));

	}

	// 2.6
	public static Worker<?> getOneWorkerFileMap(DataInput dip, char ch) throws IOException {

		int salary;
		String name, dep, depHead = null;

		name = FixedLengthStringIO.readFixedLengthString(NAMESIZE, dip);
		if (ch == '1') {
			dep = FixedLengthStringIO.readFixedLengthString(DEPSIZE, dip);
			depHead = FixedLengthStringIO.readFixedLengthString(HEADSIZE, dip);

		} else {
			dep = FixedLengthStringIO.readFixedLengthString(DEPSIZE, dip);
		}
		salary = dip.readInt();

		if (ch == '1') {
			return new Worker<>(name, new Department(dep, depHead), salary);

		}
		return new Worker<>(name, dep, salary);
	}

	// 2.6
	public static void readMapFromFile(char ch, String fileName) throws FileNotFoundException, IOException {

		BufferedInputStream bu;
		try (DataInputStream dis = new DataInputStream(bu = new BufferedInputStream(new FileInputStream(fileName)))) {

			while (bu.available() > 0) {
				Worker<?> worker = getOneWorkerFileMap(dis, ch);
				System.out.println(" " + worker);
			}
		}
	}

	// 2.7
	public static void sortFile(String fileName, char ch, Comparator<Worker<?>> co)// according to bubleSort
			throws FileNotFoundException, IOException {

		int useSize = ch == '1' ? FILEDEPSIZE : FILESTRINGSIZE;// if the file
																// have dep name
																// and head name
																// or only
																// string dep
		try (RandomAccessFile f = new RandomAccessFile(fileName, "rw")) {

			long numOfObj = f.length() / useSize;
			for (int i = (int) (numOfObj - 1); i > 0; i--) {
				for (int j = 0; j < i; j++) {
					f.seek(j * useSize);// placed on the worker place
					Worker<?> worker1 = getOneWorkerFileMap(f, ch);// get 1
																	// worker
					Worker<?> worker2 = getOneWorkerFileMap(f, ch);// get 2
																	// worker
					if (co.compare(worker1, worker2) > 0) {// change places if
															// true
						f.seek(j * useSize);
						saveOneWorkerMap(f, worker2, ch);
						saveOneWorkerMap(f, worker1, ch);// swap

					}
				}
			}

		}

	}

	// 2.10
	public static void checkIterator(ListIterator<Worker<?>> listIterator) throws FileNotFoundException, IOException {

		System.out.println("File content FORWARD with ListIterator: \n ");
		while (listIterator.hasNext()) {
			System.out.println(listIterator.next());
		}
		System.out.println("\nFile content BACKWARD with ListIterator: \n ");
		while (listIterator.hasPrevious()) {
			System.out.println(listIterator.previous());
		}
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {

		Scanner s = new Scanner(System.in);

		System.out.println("prees 1 for dep as class department,any othor key for dep as string");

		char ch = s.next().charAt(0);

		ArrayList<Worker<?>> WorkersArr = createList(ch);
		System.out.println("ArrayList content:  \n");
		printEmployye(WorkersArr);

		try {

			Map<?, ?> workerMap = new TreeMap<>();
			workerMap = createMap(WorkersArr);
			System.out.println("Map content backward, order by worker's name: \n");
			printMapBackWard(workerMap);
			System.out.println("file content \n");
			saveMapToFile(workerMap, ch, FILEMAPWORKER);
			readMapFromFile(ch, FILEMAPWORKER);

			sortFile(FILEMAPWORKER, ch, new Comparator<Worker<?>>() {

				@Override
				public int compare(Worker<?> w1, Worker<?> w2) {
					int x = w1.getSalary() - w2.getSalary();
					return x == 0 ? w1.getName().compareTo(w2.getName()) : x;
				}

			});
			System.out.println("File content after sorting:  \n");
			readMapFromFile(ch, FILEMAPWORKER);
			ListIterator<Worker<?>> listIterator = MyListIterator(ch, 0, FILEMAPWORKER);
			 System.out.println("\n checkIterator: \n");
			checkIterator(listIterator);
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static ListIterator<Worker<?>> MyListIterator(char ch, int index, String fileName)
			throws FileNotFoundException, IOException {
		return new MyListIterator(ch, index, fileName);
	}

	private static class MyListIterator implements ListIterator<Worker<?>> {

		int index = 0;
		char ch;
		RandomAccessFile f;
		int useSize;
		int lastWorker = -1;
		long numOfObj;

		public MyListIterator(char ch, int index, String fileName) throws IOException {
			this.ch = ch;
			this.index = index;
			this.f = new RandomAccessFile(fileName, "rw");
			this.useSize = ch == '1' ? FILEDEPSIZE : FILESTRINGSIZE;
			this.numOfObj = f.length() / useSize;

		}

		@Override
		public void add(Worker<?> worker) {

			try {
				ArrayList<Worker<?>> tempArrWorkers = toArray();
				tempArrWorkers.add(index, worker);
				toFile(tempArrWorkers);
				index++;
				lastWorker = -1;
				numOfObj++;// to know that now we add one worker so we have one extra worker in the file

			} catch (IOException e1) {

				e1.printStackTrace();
			}

		}

		@Override
		public boolean hasNext() {
			return index < numOfObj;

		}

		@Override
		public boolean hasPrevious() {

			return index != 0;
		}

		@Override
		public Worker<?> next() {

			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			Worker<?> workerTemp = null;

			try {
				f.seek(useSize * index); //for jumping to the right place according to the worker department type and the index
				workerTemp = getOneWorkerFileMap(f, ch);
			} catch (IOException e) {

				e.printStackTrace();
			}
			lastWorker = index;
			index++;
			return workerTemp;

		}

		@Override
		public int nextIndex() {
			return index;
		}

		@Override
		public Worker<?> previous() {

			if (!hasPrevious()) {
				throw new NoSuchElementException();
			}

			Worker<?> workerTemp = null;
			index--;
			try {
				f.seek(index * useSize);
				workerTemp = getOneWorkerFileMap(f, ch);
			} catch (IOException e) {

				e.printStackTrace();
			}
			lastWorker = index;
			return workerTemp;

		}

		@Override
		public int previousIndex() {
			return index - 1;
		}

		@Override
		public void remove() {
			if (lastWorker == -1) {// check if the previous function was not add
									// or remove
				throw new IllegalStateException();
			}
			try {
				ArrayList<Worker<?>> tempArrWorkers = toArray();
				tempArrWorkers.remove(lastWorker);
				toFile(tempArrWorkers);
				index = lastWorker;
				lastWorker = -1;
				numOfObj--;

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void set(Worker<?> worker) {
			if (lastWorker == -1) {
				throw new IllegalStateException();
			}
			try {
				f.seek(useSize * lastWorker);
				saveOneWorkerMap(f, worker, ch);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

		private ArrayList<Worker<?>> toArray() throws IOException {
			ArrayList<Worker<?>> tempArrWorkers = new ArrayList<Worker<?>>();
			f.seek(0);
			while (f.getFilePointer() < f.length()) {
				tempArrWorkers.add(getOneWorkerFileMap(f, ch));
			}
			return tempArrWorkers;
		}

		private void toFile(ArrayList<Worker<?>> tempArrWorkers) throws IOException {
			f.setLength(0);
			for (Worker<?> worker : tempArrWorkers) {
				saveOneWorkerMap(f, worker, ch);
			}

		}

	}

}
