import java.util.*;
import java.io.*;
public class Main {
	static final int[] dr = {-1, 1, 0, 0};
	static final int[] dc = {0, 0, -1, 1};
	static int N, M, K;
	static int[][] map;
	static Point[] players;
	static Point exit;
	static class Point implements Comparable<Point>{
		int r, c, w;

		public Point(int r, int c) {
			this.r = r;
			this.c = c;
		}
		
		public Point(int r, int c, int w) {
			this.r = r;
			this.c = c;
			this.w = w;
		}

		@Override
		public int hashCode() {
			return Objects.hash(c, r);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			return c == other.c && r == other.r;
		}

		@Override
		public int compareTo(Point o) {
			if(o.w < w) {
				return 1;
			} else if(o.w == w) {
				if(o.r < r) {
					return 1;
				} else if(o.r == r) {
					if(o.c < c) {
						return 1;
					}
				}
			}
			return -1;
		}

		@Override
		public String toString() {
			return "Point [r=" + r + ", c=" + c + ", w=" + w + "]";
		}	
	}
	
	public static void main(String[] args) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		StringBuilder sb = new StringBuilder();
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		map = new int[N][N];
		players = new Point[M];
		for(int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		int r, c;
		for(int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			r = Integer.parseInt(st.nextToken()) - 1;
			c = Integer.parseInt(st.nextToken()) - 1;
			players[i] = new Point(r, c);
		}
		
		st = new StringTokenizer(br.readLine());
		r = Integer.parseInt(st.nextToken()) - 1;
		c = Integer.parseInt(st.nextToken()) - 1;
		exit = new Point(r, c);
		
		sb.append(solution()).append("\n");
		sb.append((exit.r + 1) + " " + (exit.c + 1));
		System.out.println(sb);
	}
	
	static int solution() {
		int ans = 0;
		int t = 0;
		PriorityQueue<Point> pq;
		while(t < K) {
			pq = new PriorityQueue<>();
			//1.참가자 move
			flag: for(int i = 0; i < M; i++) {
				if(players[i].r == -1) {
					continue;
				}
				int dist = getDist(players[i], exit);
				int nextR = players[i].r;
				int nextC = players[i].c;
				for(int d = 0; d < 4; d++) {
					int nr = players[i].r + dr[d];
					int nc = players[i].c + dc[d];
					if(0 <= nr && nr < N && 0 <= nc && nc < N && map[nr][nc] == 0) {
						if(getDist(new Point(nr,nc), exit) < dist) {
							dist = getDist(new Point(nr,nc), exit);
							if(dist == 0) {
								players[i].r = -1;
								ans++;
								continue flag;
							}
							nextR = nr;
							nextC = nc;
						}
					}
				}
				if(players[i].r != nextR || players[i].c != nextC) {
					ans++;
				}
				players[i].r = nextR;
				players[i].c = nextC;
				pq.offer(new Point(nextR, nextC, dist));
			}
			
			//2. 미로 회전
			//	정사각형 구하기
			Point p = pq.poll();
			int k = Math.max(Math.abs(p.r - exit.r) + 1, Math.abs(p.c - exit.c) + 1);
			int sr = Math.min(exit.r, p.r) - (k - (Math.abs(exit.r - p.r) + 1));
			if(sr < 0) {
				sr = 0;
			}
			int sc = Math.min(exit.c, p.c) - (k - (Math.abs(exit.c - p.c) + 1));
			if(sc < 0) {
				sc = 0;
			}
			
			//	회전하기
			int[] newExit = null;
			HashMap<Integer, int[]> updateList = new HashMap<>();
			int temp[][] = new int[k][k];
			int step = 0;
			for(int i = sr; i < sr + k; i++) {
				int mc = k - (i - sr) - 1;
				for(int j = sc; j < sc + k; j++) {
					int mr = step;
					if(map[i][j] > 0) {
						temp[mr][mc] = map[i][j] - 1;
						step = (step + 1) % k;
						continue;
					}
					
					if(exit.r == i && exit.c == j) {
						newExit = new int[] {mr + sr, mc + sc};
						step = (step + 1) % k;
						continue;
					}
					
					for(int z = 0; z < M; z++) {
						if(players[z].r == i && players[z].c == j) {
							updateList.put(z, new int[] {mr + sr, mc + sc});
						}
					}
					step = (step + 1) % k;
				}
			}
			
			for(int i = 0;  i < k; i++) {
				for(int j = 0;  j < k; j++) {
					map[sr + i][sc + j] = temp[i][j];
				}
			}
			
			
			for(Integer i : updateList.keySet()) {
				players[i].r = updateList.get(i)[0];
				players[i].c = updateList.get(i)[1];
			}
			
			exit.r = newExit[0];
			exit.c = newExit[1];
			
			t++;
		}
		return ans;
	}
	
	static int getDist(Point p1, Point p2) {
		return Math.abs(p1.r - p2.r) + Math.abs(p1.c - p2.c);
	}
}