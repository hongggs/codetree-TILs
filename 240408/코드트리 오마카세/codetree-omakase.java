import java.util.*;
import java.io.*;
public class Main {
	static int L;
	static Map<String, ArrayList<Sushi>> sushiMap;
	static ArrayList<Customer> customers;
	static class Sushi {
		int time, x;

		public Sushi(int time, int x) {
			this.time = time;
			this.x = x;
		}
	}
	
	static class Customer implements Comparable<Customer>{
		int time, x, remain;
		String name;
		public Customer(int time, int x, int remain, String name) {
			this.time = time;
			this.x = x;
			this.remain = remain;
			this.name = name;
		}
		@Override
		public int compareTo(Customer o) {
			return time - o.time;
		}
	}
	public static void main(String[] args) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		StringBuilder sb = new StringBuilder();
		L = Integer.parseInt(st.nextToken());
		int N = Integer.parseInt(st.nextToken());
		sushiMap = new HashMap<>();
		customers = new ArrayList<>();
		int time, x, n;
		String name;
		for(int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			int cmd = Integer.parseInt(st.nextToken());
			switch(cmd) {
			
			case 100:
				time = Integer.parseInt(st.nextToken());
				x = Integer.parseInt(st.nextToken());
				name = st.nextToken();
				sushiMap.putIfAbsent(name, new ArrayList<>());
				sushiMap.get(name).add(new Sushi(time, x));
				break;
			case 200:
				time = Integer.parseInt(st.nextToken());
				x = Integer.parseInt(st.nextToken());
				name = st.nextToken();
				n = Integer.parseInt(st.nextToken()); 
				customers.add(new Customer(time, x, n, name));
				break;
			case 300:
				time = Integer.parseInt(st.nextToken());
				checkState(time);
				sb.append(customers.size() + " " + getSushiCount() + "\n");
				break;
			}
		}
		System.out.println(sb);
	}
	static void checkState(int M) {
		if(customers.isEmpty()) {
			return;
		}
		int t = customers.get(0).time;
		while(t <= M) {
			for(int i = 0; i < customers.size(); i++) {
				Customer now = customers.get(i);
				if(customers.get(i).time > t) {
					break;
				}
				if(sushiMap.get(now.name) == null) {
					continue;
				}
				for(int j = sushiMap.get(now.name).size() - 1; j >= 0; j--) {
					Sushi s = sushiMap.get(now.name).get(j);
					int loc = (s.x + (t - s.time)) % L;
					if(now.x == loc) { 
						now.remain--;
						sushiMap.get(now.name).remove(j);
						if(now.remain == 0) {
							customers.remove(i--);
							break;
						}
					}
				}
			}
			t++;
		}
	}
	
	static int getSushiCount() {
		int result = 0;
		for(ArrayList<Sushi> a : sushiMap.values()) {
			result += a.size();
		}
		return result;
	}
}