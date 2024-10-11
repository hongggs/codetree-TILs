import java.io.*;
import java.util.*;

public class Main {
    static int[] dr = {-1, 0, 1, 0, 1, 1, -1, -1};//상우하좌
    static int[] dc = {0, 1, 0, -1, 1, -1, 1, -1};
    static int N, M, P, C, D;
    static int[][] map;
    static Santa[] santas;
    static int[] rudolph;
    static int dead;
    static class Santa {
        int r, c, score, sleepTime;

        public Santa(int r, int c) {
            this.r = r;
            this.c = c;
            this.score = 0;
            sleepTime = 0;
        }

        @Override
        public String toString() {
            return "Santa{" +
                "r=" + r +
                ", c=" + c +
                ", score=" + score +
                ", sleepTime=" + sleepTime +
                '}';
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine().trim());
        StringBuilder sb = new StringBuilder();

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        D = Integer.parseInt(st.nextToken());

        map = new int[N][N];
        santas = new Santa[P + 1];

        rudolph = new int[2];
        st = new StringTokenizer(br.readLine().trim());
        rudolph[0] = Integer.parseInt(st.nextToken()) - 1;
        rudolph[1] = Integer.parseInt(st.nextToken()) - 1;

        for(int i = 1; i <= P; i++) {
            st = new StringTokenizer(br.readLine().trim());
            int index = Integer.parseInt(st.nextToken());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            santas[index] = new Santa(r, c);
            map[r][c] = index;
        }

        dead = 0;
        while (M-- > 0) {
            int dir = moveRudolph();
            //* [루돌프가 움직여 충돌]
            //* 해당 산타는 C만큼 점수를 얻음
            //* 동시에 산타는 루돌프가 이동해온 방향으로 C만큼 밀려남
            //* 밀려나는 중에는 충돌 발생x
            //* 밀려난 위치가 게임판 밖이면 탈락
            //* 밀려난 칸에 다른 산타 있으면 상호작용 발생
            if(map[rudolph[0]][rudolph[1]] > 0) {
                int index = map[rudolph[0]][rudolph[1]];
                santas[index].score += C;
                santas[index].sleepTime = 2;
                map[santas[index].r][santas[index].c] = 0;
                int nr = santas[index].r + C * dr[dir];
                int nc = santas[index].c + C * dc[dir];
                interact(nr, nc, index, dir);
            }

            moveSanta();
            if(dead == P) {
                break;
            }

            //[기절 깨우기] & [점수 주기]
            for(int i = 1; i <= P; i++) {
                if(santas[i].r == -1) {
                    continue;
                }
                if(santas[i].sleepTime > 0) {
                    santas[i].sleepTime--;
                }
                santas[i].score++;
            }
        }

        for(int i = 1; i <= P; i++) {
            sb.append(santas[i].score + " ");
        }
        System.out.println(sb);
    }
    static int getDist(int r1, int c1, int r2, int c2) {
        return (r1 - r2) * (r1 - r2) + (c1 - c2) * (c1 - c2);
    }

    /*
    * [루돌프 움직임]
     * 1. 산타 선택
     * 가장 가까운 산타를 향해 1칸 돌진
     *       단, 게임에서 탈락하지 않은 산타!
     * 가장 가까운 산타가 2명 이상이라면 r이 큰 산타
     * r이 동일하면 c가 큰 산타
     * 2. 8방향 중 선택한 산타와 가장 가까워지는 곳으로 돌진
    * */
    static int moveRudolph() {
        int rr = -1;
        int rc = -1;
        int minDist = Integer.MAX_VALUE;
        for(int i = 1; i <= P; i++) {
            if(santas[i].r == -1) {
                continue;
            }
            int dist = getDist(rudolph[0], rudolph[1], santas[i].r, santas[i].c);
            if(dist < minDist) {
                minDist = dist;
                rr = santas[i].r;
                rc = santas[i].c;
            } else if(dist == minDist) {
                if(santas[i].r > rr) {
                    minDist = dist;
                    rr = santas[i].r;
                    rc = santas[i].c;
                } else if(santas[i].r == rr) {
                    if(santas[i].c > rc) {
                        minDist = dist;
                        rr = santas[i].r;
                        rc = santas[i].c;
                    }
                }
            }
        }

        int minDir = -1;
        minDist = Integer.MAX_VALUE;
        for(int i = 0; i < 8; i++) {
            int nr = rudolph[0] + dr[i];
            int nc = rudolph[1] + dc[i];
            if(isRange(nr, nc)) {
                int dist = getDist(rr, rc, nr, nc);
                if(dist < minDist) {
                    minDist = dist;
                    minDir = i;
                }
            }
        }

        rudolph[0] += dr[minDir];
        rudolph[1] += dc[minDir];

        return minDir;
    }

    static boolean isRange(int r, int c) {
        return 0 <= r && r < N && 0 <= c && c < N;
    }

    /*
     * [상호작용]
     * 밀려난 칸에 산타 있으면 그 산타는 1칸 해당 방향으로 밀려남
     * 연쇄적으로 반복
     * 게임판으로 밀려나온 산타는 탈락
    * */
    static void interact(int r, int c, int index, int dir) {
        if(!isRange(r, c)) {
            santas[index].r = -1;
            dead++;
            return;
        }

        if(map[r][c] == 0) {
            santas[index].r = r;
            santas[index].c = c;
            map[r][c] = index;
        } else {
            int next = map[r][c];
            santas[index].r = r;
            santas[index].c = c;
            map[r][c] = index;
            int nr = r + dr[dir];
            int nc = c + dc[dir];
            interact(nr, nc, next, dir);
        }
    }

    /*
     * [산타 움직임]
     * for
     * 산타는 1번부터 P번까지 순서대로 움직임
     * continue
     * 기절 or 탈락 산타 못움직임
     * 1. 이동 방향 선택
     *   산타는 루돌프랑 가장 가까워지는 방향으로 1칸 이동 => 4방 탐색
     *   다른 산타 있거나 or 게임판 밖으로는 움직임xxx
     *   움직일 수 있는 칸이 없다면 산타는 움직이지 않음
     *   움직일 수 있는 칸이 있어도 루돌프랑 가까워질 수 없다면 안움직임
     *   가능한 방향이 여러개면 상우하좌 우선순위로 동작
    *
    * */
    static void moveSanta() {
        for(int i = 1; i <= P; i++) {
            if(santas[i].r == -1 || santas[i].sleepTime > 0) {
                continue;
            }
            int dir = -1;
            int minDist = getDist(santas[i].r, santas[i].c, rudolph[0], rudolph[1]);
            for(int d = 0; d < 4; d++) {
                int nr = santas[i].r + dr[d];
                int nc = santas[i].c + dc[d];
                if(isRange(nr, nc) && map[nr][nc] == 0) {
                    int dist = getDist(nr, nc, rudolph[0], rudolph[1]);
                    if(dist < minDist) {
                        minDist = dist;
                        dir = d;
                    }
                }
            }
            if(dir != -1) {
                map[santas[i].r][santas[i].c] = 0;
                santas[i].r += dr[dir];
                santas[i].c += dc[dir];
                map[santas[i].r][santas[i].c] = i;

                /*
                * * [산타가 움직여서 충돌]
                 * 산타는 D만큼 점수를 얻음
                 * 산타는 자신이 이동해온 "반대" 방향으로 D만큼 밀려남
                 * 밀려나는 중에는 충돌 발생x
                 * 밀려난 위치가 게임판 밖이면 탈락
                 * 밀려난 칸에 다른 산타 있으면 상호작용 발생
                * */
                if(santas[i].r == rudolph[0] && santas[i].c == rudolph[1]) {
                    santas[i].score += D;
                    map[santas[i].r][santas[i].c] = 0;
                    santas[i].sleepTime = 2;
                    dir = (dir + 2) % 4;
                    int nr = rudolph[0] + D * dr[dir];
                    int nc = rudolph[1] + D * dc[dir];
                    interact(nr, nc, i, dir);
                }
            }
        }
    }

    static void print() {
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }
}