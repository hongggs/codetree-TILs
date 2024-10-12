import java.io.*;
import java.util.*;

public class Main {
    static int[] dr = {-1, 0, 1, 0};//상우하좌
    static int[] dc = {0, 1, 0, -1};
    static int N, M, H, K;
    static int[][] tree;
    static Seeker seeker;
    static Runner[] runners;
    static int dead;
    static class Seeker {
        int r, c, dir, cur, step, depth, score, dirStep;

        public Seeker(int r, int c) {
            this.r = r;
            this.c = c;
            dir = 0;
            cur = 0;
            step = 1;
            depth = 2;
            score = 0;
            dirStep = 1;
        }
    }
    static class Runner {
        int r, c, dir;

        public Runner(int r, int c, int dir) {
            this.r = r;
            this.c = c;
            this.dir = dir;
        }
    }
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine().trim());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        H = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        dead = 0;
        seeker = new Seeker(N / 2, N / 2);
        tree = new int[N][N];
        runners = new Runner[M + 1];
        for(int i = 1; i <= M; i++) {
            st = new StringTokenizer(br.readLine().trim());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            int dir = Integer.parseInt(st.nextToken());
            runners[i] = new Runner(r, c, dir);
        }
        for(int i = 1; i <= H; i++) {
            st = new StringTokenizer(br.readLine().trim());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            tree[r][c] = 1;
        }

        for (int k = 1; k <= K; k++) {
            moveRunners();
            moveSeeker();
            seeker.score += (k * catchRunners());
            if(dead == M) {
                break;
            }
        }

        System.out.println(seeker.score);
    }

    /*
     * [도망자 움직임]
     *   m명 for문
     *   술래와의 거리가 3이하인 도망자만 움직임
     *       현재 바라보고 있는 방향으로 1칸 움직일때 격자 벗어나지 않는 경우
     *           해당 칸에 술래 있으면 움직이지 않음
     *           해당 칸에 아무것도 없거나 or 나무 있으면 움직임
     *       현재 바라보고 있는 방향으로 1칸 움직일때 격자 벗어나는 경우
     *           방향을 반대로 틀어줌
     *           그리고 한칸 이동
     *           술래 없으면 1칸 이동 아니면 안움직임
    *
    * */
    static void moveRunners() {
        for(int i = 1; i <= M; i++) {
            if(runners[i].r == -1) {
                continue;
            }
            if (getDist(runners[i].r, runners[i].c, seeker.r, seeker.c) > 3) {
                continue;
            }
            int nr = runners[i].r + dr[runners[i].dir];
            int nc = runners[i].c + dc[runners[i].dir];
            if(isRange(nr, nc)) {
                if (nr != seeker.r || nc != seeker.c) {
                    runners[i].r = nr;
                    runners[i].c = nc;
                }
            } else {
                runners[i].dir = (runners[i].dir + 2) % 4;
                nr = runners[i].r + dr[runners[i].dir];
                nc = runners[i].c + dc[runners[i].dir];
                if (nr != seeker.r || nc != seeker.c) {
                    runners[i].r = nr;
                    runners[i].c = nc;
                }
            }
        }
    }

    /*
     * [술래 움직임]
     *   1. 술래 이동
     *   달팽이 모양으로 움직임
     *   해당 방향으로 이도
     *   이동 위착 이동 방향 틀어지면 바로 방향 바꾸기
     *   0,0이나 정중앙 도착해도 바로 방향 틀어주기
     *   2. 술래 잡기
     *   술래는 현재 바라보는 방향 기준으로 "현재 칸을 포함해" 3칸 도망자 잡음
     *   나무가 있는 칸의 도망자는 못잡음 그 이후에 있는 도망자는 잡을 수 있음
     *   현재 t턴이라면 t*(현재 턴에서 잡힌 도망자 수)만큼 점수를 얻음
     *   잡힌 도망자 사라짐
    * */
    static void moveSeeker() {
        //1. 이동
        int nr = seeker.r + dr[seeker.dir];
        int nc = seeker.c + dc[seeker.dir];
        seeker.cur++;

        if(nr == 0 && nc == 0) {
            seeker.cur = 0;
            seeker.step = N - 1;
            seeker.dir = 2;
            seeker.depth = 3;
            seeker.dirStep = 3;
        } else if(nr == N/2 && nc == N/2) {
            seeker.cur = 0;
            seeker.step = 1;
            seeker.dir = 0;
            seeker.depth = 2;
            seeker.dirStep = 1;
        }

        if(seeker.cur == seeker.step) {
            seeker.cur = 0;
            seeker.depth--;
            if(seeker.depth == 0) {
                if(seeker.dirStep == 1) {
                    seeker.step++;
                } else {
                    seeker.step--;
                }
                seeker.depth = 2;
            }
            seeker.dir = (seeker.dir + seeker.dirStep) % 4;
        }
        seeker.r = nr;
        seeker.c = nc;
    }

    /*
     * [술래 움직임]
     *   2. 술래 잡기
     *   술래는 현재 바라보는 방향 기준으로 "현재 칸을 포함해" 3칸 도망자 잡음
     *   나무가 있는 칸의 도망자는 못잡음 그 이후에 있는 도망자는 잡을 수 있음
     *   현재 t턴이라면 t*(현재 턴에서 잡힌 도망자 수)만큼 점수를 얻음
     *   잡힌 도망자 사라짐
     * */
    static int catchRunners() {
        int result = 0;
        for(int s = 0; s <= 2; s++) {
            int nr = seeker.r + s * dr[seeker.dir];
            int nc = seeker.c + s * dc[seeker.dir];
            if(isRange(nr, nc)) {
                if(tree[nr][nc] > 0) {
                    continue;
                }
                for (int i = 1; i <= M; i++) {
                    if(runners[i].r == -1) {
                        continue;
                    }
                    if(runners[i].r == nr && runners[i].c == nc) {
                        dead++;
                        result++;
                        runners[i].r = -1;
                    }
                }
            } else {
                break;
            }
        }
        return result;
    }

    static int getDist(int r1, int c1, int r2, int c2) {
        return Math.abs(r1 - r2) + Math.abs(c1 - c2);
    }

    static boolean isRange(int r, int c) {
        return 0 <= r && r < N && 0 <= c && c < N;
    }
}