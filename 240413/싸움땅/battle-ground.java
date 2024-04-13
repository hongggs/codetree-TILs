import java.util.*;
import java.io.*;
public class Main {
    static int[] dr = {-1,0,1,0};//상우하좌;
    static int[] dc = {0,1,0,-1};
    static int N, M, K;
    static int[][] p;
    static PriorityQueue<Integer>[][] map;
    static Player[] players;
    static int[] ans;
    static class Player {
        int r, c, d, s;
        int gun;

        public Player(int r, int c, int d, int s) {
            this.r = r;
            this.c = c;
            this.d = d;
            this.s = s;
            gun = 0;
        }

        public int getPower() {
            return gun + s;
        }
    }
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        StringBuilder sb = new StringBuilder();
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        map = new PriorityQueue[N][N];
        p = new int[N][N];
        players = new Player[M];
        ans = new int[M];

        int temp = 0;
        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < N; j++) {
                map[i][j] = new PriorityQueue<>(Collections.reverseOrder());
                temp = Integer.parseInt(st.nextToken());
                if(temp > 0) {
                    map[i][j].offer(temp);
                }
            }
        }

        for(int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken());
            int s = Integer.parseInt(st.nextToken());
            players[i] = new Player(r, c, d, s);
            p[r][c] = i + 1;
        }

        solution();

        for(int i = 0; i< M; i++) {
            sb.append(ans[i]).append(" ");
        }
        System.out.println(sb);
    }

    static void solution() {
        while(0 < K--) {
            for(int i = 0; i < M; i++) {
                //1. 이동하기
                int nr = players[i].r + dr[players[i].d];
                int nc = players[i].c + dc[players[i].d];
                if(0 > nr ||  nr >= N || 0 > nc || nc >= N) {
                    players[i].d = (players[i].d + 2) % 4;
                    nr = players[i].r + dr[players[i].d];
                    nc = players[i].c + dc[players[i].d];
                }
                p[players[i].r][players[i].c] = 0;
                //2.이동한 방향에 플레이어x -> 최고 총 구하기
                if(p[nr][nc] == 0) {
                    players[i].r = nr;
                    players[i].c = nc;
                    p[nr][nc] = i + 1;
                    players[i].gun = getBestGun(nr, nc, i);
                } else {
                    //2. 대결
                    //1)승자 찾아서 점수 부여
                    //2)진사람 이동
                    //3)이긴 사람 이동
                    int cp = p[nr][nc] - 1;
                    int iPower = players[i].getPower();
                    int cPower = players[cp].getPower();
                    int winner = 0;
                    int loser = 0;
                    if(iPower > cPower) { //i이김
                        winner = i;
                        loser = cp;
                    } else if(iPower == cPower) {
                        if(players[i].s > players[cp].s) { //i이김
                            winner = i;
                            loser = cp;
                        }else {//cp이김
                            winner = cp;
                            loser = i;
                        }
                    } else { //cp이김
                        winner = cp;
                        loser = i;
                    }
                    ans[winner] += Math.abs(iPower - cPower);
                    loserMove(nr, nc, loser);
                    players[winner].r = nr;
                    players[winner].c = nc;
                    p[nr][nc] = winner + 1;
                    players[winner].gun = getBestGun(nr, nc, winner);
                }
            }

        }
    }
    static void loserMove(int r, int c, int i) {
        if(players[i].gun > 0) {
            map[r][c].offer(players[i].gun);
            players[i].gun = 0;
        }
        int nr = 0;
        int nc = 0;
        while(true) {
            nr = r + dr[players[i].d];
            nc = c + dc[players[i].d];
            if (0 > nr || nr >= N || 0 > nc || nc >= N || p[nr][nc] > 0) {
                players[i].d = (players[i].d + 1) % 4;
            } else {
                break;
            }
        }
        players[i].r = nr;
        players[i].c = nc;
        players[i].gun = getBestGun(nr, nc, i);
        p[nr][nc] = i + 1;
    }
    static int getBestGun(int r, int c, int i) {
        int old = players[i].gun;
        if(!map[r][c].isEmpty()) {
            if(map[r][c].peek() > old) {
                if(old != 0) {
                    map[r][c].offer(old);
                }
                return map[r][c].poll();
            }
        }
        return old;
    }
}