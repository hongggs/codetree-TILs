import java.io.*;
import java.util.*;
public class Main {
	/*
	 * 블럭 이동
	 * 	red의 열이나 yellow의 행 한 줄이 다 채워진 경우 그 줄 비우기
	 * 		확인
	 * 		점수 1점 획득
	 * 		위에꺼 끌어 내리기
	 * 연한부분에 최종적으로 타일이 있다면
	 * 	타일들이 차지하는 열이나 행의 개수만큼 지워지고 내려감
	 * 
	 * 
	 * */
	static int N = 4;
	static int M = 4 + 2;
	static int[][] dr = {{}, {0}, {0, 0}, {0, 1}};
	static int[][] dc = {{}, {0}, {0, 1}, {0, 0}};
	static int[][] red;
	static int[][] yellow;
	public static void main(String[] args) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		StringBuilder sb = new StringBuilder();
		
		red = new int[N][M];
		yellow = new int[M][N];
		int score = 0;
		int K = Integer.parseInt(br.readLine().trim());
		for(int k = 0; k < K; k++) {
			st = new StringTokenizer(br.readLine().trim());
			int t = Integer.parseInt(st.nextToken());
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			moveRed(t, r, 0);
			while(bombRed()) {
				score++;
			}
			removeRangeRed(r, c);
			while(bombRed()) {
				score++;
			}
			
			moveYellow(t, 0, c);
			while(bombYellow()) {
				score++;
			}
			removeRangeYellow(r, c);
			while(bombYellow()) {
				score++;
			}
						
		}
		int cnt = 0;
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < M; j++) {
				if(red[i][j] == 1) {
					cnt++;
				}
			}
		}
		for(int i = 0; i < M; i++) {
			for(int j = 0; j < N; j++) {
				if(yellow[i][j] == 1) {
					cnt++;
				}
			}
		}
		
		sb.append(score).append("\n").append(cnt);
		System.out.println(sb);
		
	}
	
	static void moveRed(int t, int r, int c) {
		flag: while(true) {
			for(int i = 0; i < dr[t].length; i++) {
				int nr = r + dr[t][i];
				int nc = c + dc[t][i];
				if(0 > nr || nr >= N || 0 > nc || nc >= M) {
					break flag;
				}
				if(red[nr][nc] == 1) {
					break flag;
				}
			}
			c++;
		}
		c--;
		for(int i = 0; i < dr[t].length; i++) {
			int nr = r + dr[t][i];
			int nc = c + dc[t][i];
			red[nr][nc] = 1;
		}
		
	}
	
	static void moveYellow(int t, int r, int c) {
		flag: while(true) {
			for(int i = 0; i < dr[t].length; i++) {
				int nr = r + dr[t][i];
				int nc = c + dc[t][i];
				if(0 > nr || nr >= M || 0 > nc || nc >= N) {
					break flag;
				}
				if(yellow[nr][nc] == 1) {
					break flag;
				}
			}
			r++;
		}
		r--;
		for(int i = 0; i < dr[t].length; i++) {
			int nr = r + dr[t][i];
			int nc = c + dc[t][i];
			yellow[nr][nc] = 1;
		}
		
	}
	
	static boolean bombRed() {
		boolean result = false;
		flag: for(int i = 0; i < M; i++) {
			for(int j = 0; j < N; j++) {
				if(red[j][i] == 0) {
					continue flag;
				}
			}
			result = true;
			for(int j = 0; j < N; j++) {
				red[j][i] = 0;
			}
			for(int j = 0; j < N; j++) {
				for(int a = i; a >= 1; a--) {
					red[j][a] = red[j][a - 1];
					red[j][a - 1] = 0;
				}
			}
			break;
		}
		return result;
	}
	
	static boolean bombYellow() {
		boolean result = false;
		flag: for(int i = 0; i < M; i++) {
			for(int j = 0; j < N; j++) {
				if(yellow[i][j] == 0) {
					continue flag;
				}
			}
			result = true;
			for(int j = 0; j < N; j++) {
				yellow[i][j] = 0;
			}
			for(int j = 0; j < N; j++) {
				for(int a = i; a >= 1; a--) {
					yellow[a][j] = yellow[a - 1][j];
					yellow[a - 1][j] = 0;
				}
			}
			break;
		}
		return result;
	}
	
	
	static void removeRangeRed(int r, int c) {
		int cnt = 0;
		if(red[r][0] == 1 && red[r][1] == 1) {
			cnt = 2;
		} else if(r + 1 < N && red[r][1] == 1 && red[r + 1][1] == 1) {
			cnt = 1;
		} else if(red[r][1] == 1) {
			cnt = 1;
		}
		
		if(cnt == 0) {
			return;
		}
		
		for(int s = 0; s < cnt; s++) {
			for(int j = 0; j < N; j++) {
				red[j][M - 1 - s] = 0;
			}
		}
		
		while(cnt-- > 0) {
			for(int i = 0; i < N; i++) {
				for(int j = M - 1; j >= 1; j--) {
					red[i][j] = red[i][j - 1];
					red[i][j - 1] = 0;
				}
			}
		}
	}
	
	static void removeRangeYellow(int r, int c) {
		int cnt = 0;
		if(yellow[0][c] == 1 && yellow[1][c] == 1) {
			cnt = 2;
		} else if(c + 1 < N && yellow[1][c] == 1 && yellow[1][c + 1] == 1) {
			cnt = 1;
		} else if(yellow[1][c] == 1) {
			cnt = 1;
		}
		
		if(cnt == 0) {
			return;
		}
		
		for(int s = 0; s < cnt; s++) {
			for(int j = 0; j < N; j++) {
				yellow[M - 1 - s][j] = 0;
			}
		}
		
		while(cnt-- > 0) {
			for(int i = 0; i < N; i++) {
				for(int j = M - 1; j >= 1; j--) {
					yellow[j][i] = yellow[j - 1][i];
					yellow[j - 1][i] = 0;
				}
			}
		}
	}
	
	static void print() {
		System.out.println("red");
		for(int i = 0; i < N; i++) {
			for(int j= 0; j < M; j++) {
				System.out.print(red[i][j] + " ");
			}
			System.out.println();
		}
		
		System.out.println("yellow");
		for(int i = 0; i < M; i++) {
			for(int j= 0; j < N; j++) {
				System.out.print(yellow[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

}