import java.util.*;
import java.io.*;
public class Main {
    static int[] dr = {0, 1, 0, -1};//우하좌상
    static int[] dc = {1, 0, -1, 0};//우하좌상
    static int N, M;
    static int[][] map;
    static int cnt;
    static boolean[][] v;
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        map = new int[N][N];
        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        System.out.println(solution());

    }

    static int solution() {
        int ans = 0;
        int r = 0;
        int c = 1;
        int d = 0;
        int[] dice = {1,2,6,5};
        int cur = 3;
        while(0 < M--) {
            //점수얻기
            v = new boolean[N][N];
            cnt = 1;
            v[r][c] = true;
            getScore(r, c);
            ans += (map[r][c] * cnt);


            //다음 위치 구하기
            if(cur > map[r][c]) {
                d = (d + 1) % 4;
            } else if(cur < map[r][c]) {
                d = (d + 3) % 4;
            }
            int nr = r + dr[d];
            int nc = c + dc[d];
            if(0 > nr || nr >= N || 0 > nc || nc >= N) {
                d = (d + 2) % 4;
                nr = r + dr[d];
                nc = c + dc[d];
            }
            r = nr;
            c = nc;

            int next = dice[d];
            dice[(d + 2) % 4] = cur;
            dice[d] = 7 - cur;
            cur = next;
        }
        return ans;
    }
    static void getScore(int r, int c) {
        for(int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            if(0 <= nr && nr < N && 0 <= nc && nc < N && !v[nr][nc] && map[nr][nc] == map[r][c]) {
                cnt++;
                v[nr][nc] = true;
                getScore(nr, nc);
            }
        }
    }
}