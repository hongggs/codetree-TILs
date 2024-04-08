import java.util.*;
import java.io.*;
public class Main {
	static final int[] dr = {-1, 0, 1, 0};//위쪽, 오른쪽, 아래쪽, 왼쪽
	static final int[] dc = {0, 1, 0, -1};
	static int L, N, Q;
	static int[][] map;
	static int[][] markedMap;
	static Soldier[] soldiers;
	static int[] originK;
	static boolean[] v;
	static class Soldier {
		int r, c, h, w, k;

		public Soldier(int r, int c, int h, int w, int k) {
			super();
			this.r = r;
			this.c = c;
			this.h = h;
			this.w = w;
			this.k = k;
		}

		@Override
		public String toString() {
			return "Soldier [r=" + r + ", c=" + c + ", h=" + h + ", w=" + w + ", k=" + k + "]";
		}
		
	}
	public static void main(String[] args) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());
		map = new int[L + 1][L + 1];
		soldiers = new Soldier[N + 1];
		originK = new int[N + 1];
		v = new boolean[N + 1];
		for(int i = 1; i <= L; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 1; j <= L; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		for(int i = 1; i <= N; i++)  {
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			int h = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());
			originK[i] = k;
			soldiers[i] = new Soldier(r, c, h, w, k);
		}
		
		for(int i = 0; i < Q; i++) {
			st = new StringTokenizer(br.readLine());
			int index = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			mark();
			v = new boolean[N + 1];
			v[index] = true;
			if(canMove(index, d)) {
				soldiers[index].r = soldiers[index].r + dr[d];
				soldiers[index].c = soldiers[index].c + dc[d];
				v[index] = false;
				for(int j = 1; j <= N; j++) {
					if(v[j]) {
						soldiers[j].r = soldiers[j].r + dr[d];
						soldiers[j].c = soldiers[j].c + dc[d];
						soldiers[j].k -= getDamage(j);
					}
				}
			}
		}
		
		int ans = 0;
		for(int i = 1; i <= N; i++) {
			if(soldiers[i].k > 0) {
				ans += originK[i] - soldiers[i].k;
			}
		}
		System.out.println(ans);
	}
	
	static int getDamage(int index) {
		int result = 0;
		for(int i = 0; i < soldiers[index].h; i++) {
			for(int j = 0; j < soldiers[index].w; j++) {
				if(map[soldiers[index].r + i][soldiers[index].c + j] == 1) {
					result++;
				}
			}
		}
		return result;
	}
	
	static void mark() {
		markedMap = new int[L + 1][L + 1];
		for(int a = 1; a <= N; a++) {
			if(soldiers[a].k <= 0) {
				continue;
			}
			for(int i = 0; i < soldiers[a].h; i++) {
				for(int j = 0; j < soldiers[a].w; j++) {
					markedMap[soldiers[a].r + i][soldiers[a].c + j] = a;
				}
			}
		}
	}
	
	static boolean canMove(int index, int d) {
		int nr = soldiers[index].r + dr[d];
		int nc = soldiers[index].c + dc[d];
		for(int i = 0; i < soldiers[index].h; i++) {
			for(int j = 0; j < soldiers[index].w; j++) {
				if(1 > nr + i || nr + i > L || 1 > nc + j || nc + j > L || map[nr + i][nc + j] == 2) {
					return false;
				}
				if(markedMap[nr + i][nc + j] > 0 && !v[markedMap[nr + i][nc + j]]) {
					v[markedMap[nr + i][nc + j]] = true;
					if(!canMove(markedMap[nr + i][nc + j], d)) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
}