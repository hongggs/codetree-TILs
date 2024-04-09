import java.util.*;
import java.io.*;
public class Main {
	static final int[] dr = {-1, 1, 0, 0};
	static final int[] dc = {0, 0, -1, 1};
	static int N, M, K;
	static int map[][];
	static int teamMap[][];
	static boolean v[][];
	static Team[] teams;
	static class Team {
		Node head;
		Node tail;
		boolean canMoveHead;
		public Team(Node head) {
			this.head = head;
			canMoveHead = true;
		}
	}
	static class Node {
		int r, c, type;
		Node next;
		Node prev;
		public Node(int r, int c, int type) {
			this.r = r;
			this.c = c;
			this.type = type;
		}
		@Override
		public String toString() {
			return "Node [r=" + r + ", c=" + c + ", type=" + type + "]";
		}
	}
	public static void main(String[] args) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		map = new int[N][N];
		teams = new Team[M];
		teamMap = new int[N][N];
		
		for(int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		initTeams();
		initTeamMap();
		System.out.println(solution());
	}
	
	static int solution() {
		int ans = 0;
		int t = 0;
		while(t < K) {
			//1. 머리사람따라서 이동
			for(int i = 0; i < M; i++) {
				if(teams[i].canMoveHead) {
					move_head(teams[i].tail);
				} else {
					move_tail(teams[i].head);
				}
			}
			//2. 공 던지기
			//4. 머리사람 꼬리사람 변경
			v = new boolean[N][N];
			int x = t % (N - 1);
			int score = 0;
			switch((t / (N - 1)) % 4) {
			case 0:
				for(int i = 0; i < N; i++) {
					if(0 < map[x][i]  && map[x][i] < 4) {
						v[x][i] = true;
						if(teams[teamMap[x][i] - 1].canMoveHead) {
							score = getScore(x, i, 1);
							teams[teamMap[x][i] - 1].canMoveHead = false;
						} else {
							score = getScore(x, i, 3);
							teams[teamMap[x][i] - 1].canMoveHead = true;
						}
						break;
					}
				}
				break;
			case 1:
				for(int i = N - 1; i >= 0; i--) {
					if(0 < map[i][x] && map[i][x] < 4) {
						v[i][x] = true;
						if(teams[teamMap[i][x] - 1].canMoveHead) {
							score = getScore(i, x, 1);
							teams[teamMap[i][x] - 1].canMoveHead = false;
						} else {
							score = getScore(i, x, 3);
							teams[teamMap[i][x] - 1].canMoveHead = false;
						}
						break;
					}
				}
				break;
			case 2:
				x = N - x;
				for(int i = N - 1; i >= 0; i--) {
					if(0 < map[x][i]  && map[x][i] < 4) {
						v[x][i] = true;
						if(teams[teamMap[x][i] - 1].canMoveHead) {
							score = getScore(x, i, 1);
							teams[teamMap[x][i] - 1].canMoveHead = false;
						} else {
							score = getScore(x, i, 3);
							teams[teamMap[x][i] - 1].canMoveHead = true;
						}
						break;
					}
				}
				break;
			case 3:
				x = N - x;
				for(int i = 0; i < N; i++) {
					if(0 < map[i][x] && map[i][x] < 4) {
						v[i][x] = true;
						if(teams[teamMap[i][x] - 1].canMoveHead) {
							score = getScore(i, x, 1);
							teams[teamMap[i][x]].canMoveHead = false;
						} else {
							score = getScore(i, x, 3);
							teams[teamMap[i][x] - 1].canMoveHead = false;
						}
						break;
					}
				}
				break;
			}
			//3. 점수 얻기
			ans += (score * score);
			
			t++;
		}
		return ans;
	}
	
	static void initTeamMap() {
		for(int i = 0; i < M; i++) {
			teamMap[teams[i].head.r][teams[i].head.c] = i + 1;
			checkMap(teams[i].head.r, teams[i].head.c, i + 1);
		}
	}
	
	static void checkMap(int r, int c, int index) {
		for(int i = 0; i < 4; i++) {
			int nr = r + dr[i];
			int nc = c + dc[i];
			if(0 <= nr && nr < N && 0 <= nc && nc < N && 0 < map[nr][nc] && map[nr][nc] < 5 && teamMap[nr][nc] == 0) {
				teamMap[nr][nc] = index;
				checkMap(nr, nc, index);
			}
		}
	}
	
	static void initTeams() {
		int index= 0;
		v = new boolean[N][N];
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				if(map[i][j] == 1 && !v[i][j]) {
					v[i][j] = true;
					Node head = new Node(i, j, map[i][j]);
					head.prev = null;
					teams[index]= new Team(head);
					searchNext(index, head);
					index++;
					if(index == M) {
						return;
					}
				}
			}
		}
	}
	
	static void searchNext(int index, Node node) {
		if(node.type == 3) {
			teams[index].tail = node;
			return;
		}
		for(int i = 0; i < 4; i++) {
			int nr = node.r + dr[i];
			int nc = node.c + dc[i];
			if(0 <= nr && nr < N && 0 <= nc && nc < N && !v[nr][nc] && (map[nr][nc] == 2 || map[nr][nc] == 3)) {
				v[nr][nc] = true;
				Node newNode = new Node(nr, nc, map[nr][nc]);
				node.next = newNode;
				newNode.prev = node;
				searchNext(index, newNode);
				break;
			}
		}
	}
	
	static void move_head(Node node) {
		map[node.r][node.c] = 4;
		Node next = node.prev;
		while(true) {
			if(node.type == 1) {
				for(int i = 0; i < 4; i++) {
					int nr = node.r + dr[i];
					int nc = node.c + dc[i];
					if(0 <= nr && nr < N && 0 <= nc && nc < N && map[nr][nc] == 4) {
						node.r = nr;
						node.c = nc;
						map[nr][nc] = 1;
						break;
					}
				}
				break;
			}
			
			node.r = next.r;
			node.c = next.c;
			map[node.r][node.c] = node.type;
			node = node.prev;
			next = node.prev;
		}
	}
	
	static void move_tail(Node node) {
		map[node.r][node.c] = 4;
		Node next = node.next;
		while(true) {
			if(node.type == 3) {
				for(int i = 0; i < 4; i++) {
					int nr = node.r + dr[i];
					int nc = node.c + dc[i];
					if(0 <= nr && nr < N && 0 <= nc && nc < N && map[nr][nc] == 4) {
						node.r = nr;
						node.c = nc;
						map[nr][nc] = 3;
						break;
					}
				}
				break;
			}
			
			node.r = next.r;
			node.c = next.c;
			map[node.r][node.c] = node.type;
			node = node.next;
			next = node.next;
		}
	}
	
	static int getScore(int r, int c, int goal) {
		if(map[r][c] == goal) {
			return 1;
		}
		
		for(int i = 0; i < 4; i++) {
			int nr = r + dr[i];
			int nc = c + dc[i];
			if(0 <= nr && nr < N && 0 <= nc && nc < N && map[nr][nc] != 4 && map[nr][nc] != 0 && !v[nr][nc]) {
				v[nr][nc] = true;
				return 1 + getScore(nr, nc, goal);
			}
		}
		return 0;
	}
}