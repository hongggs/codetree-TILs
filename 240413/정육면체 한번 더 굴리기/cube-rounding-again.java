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
        int c = 0;
        int d = 0;
        int[] dice = {3,2,4,5}; //우하좌상
        int t = 0;
        while(t < M) {
            //1. 이동하기(다음 위치 구하기);
            int nr = r + dr[d];
            int nc = c + dc[d];
            //벽 만남
            if(0 > nr || nr >= N || 0 > nc || nc >= N) {
                d = (d + 2) % 4;
                nr = r + dr[d];
                nc = c + dc[d];
            }

            //2. 점수얻기
            v = new boolean[N][N];
            cnt = 1;
            v[nr][nc] = true;
            getScore(nr, nc);
            ans += map[nr][nc] * cnt;

            int cur = dice[d];
            //3. 다음 위치 구하기
            int nextD = -1;
            if(cur > map[nr][nc]) {
                nextD = (d + 1) % 4;
            } else if(cur == map[nr][nc]) {
                nextD = d;
            } else {
                nextD = (d + 3) % 4;
            }

            r = nr;
            c = nc;
            dice[(d + 2) % 4] = cur;
            dice[d] = 7 - cur;
            d = nextD;

            t++;
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