import java.util.*;
import java.io.*;

public class Main {
    static final int[] dr = {-1, 0, 1, 0};//상우하좌
    static final int[] dc = {0, 1, 0, -1};
    static final int[] dir = {1, -1};//우좌(2), 하상(1)
    static int N, M, H, K;
    static int map[][];
    static Runner[] runners;
    static Seeker seeker;
    static class Seeker {
        int r, c, d, now, goal, step;
        boolean flag;
        Seeker() {
            r = N / 2;
            c = N / 2;
            d = 0;
            goal = 1;
            step = 2;
            flag = false;
        }
        void move_front() {
            r = r + dr[d];
            c = c + dc[d];
            now++;
            if(now == goal) {
                step--;
                now = 0;
                d = (d + 1) % 4;
            }

            if(step == 0) {
                if(goal == N - 2) {
                    goal++;
                    step = 3;
                } else {
                    goal++;
                    step = 2;
                }
            }

            if(r == 0 && c == 0) {
                flag = true;
                goal = N - 1;
                step = 3;
                now = 0;
                d = 2;
            }
        }

        void move_back() {
            r = r + dr[d];
            c = c + dc[d];
            now++;
            if(now == goal) {
                step--;
                now = 0;
                d = (d + 3) % 4;
            }

            if(step == 0) {
                goal--;
                step = 2;
            }

            if(r == N/2 && c == N/2) {
                flag = false;
                goal = 1;
                step = 2;
                now = 0;
                d = 0;
            }
        }
    }
    static class Runner {
        int r, c, type, d;

        public Runner(int r, int c, int type) {
            this.r = r;
            this.c = c;
            this.type = type;
            d = 0;
        }

        public void updateD() {
            d = (d + 1) % 2;
        }
    }
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        H = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        map = new int[N][N];
        runners = new Runner[M];
        for(int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            int type = Integer.parseInt(st.nextToken());
            runners[i] = new Runner(r, c, type);
        }

        for(int i = 0; i < H; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            map[r][c] = 1;
        }

        seeker = new Seeker();
        System.out.println(solution());
    }

    static int solution() {
        int ans = 0;
        int t = 1;
        while(t <= K) {
            //1.도망자 이동
            for(int i = 0; i < M; i++) {
                if(runners[i].r == -1) {
                    continue;
                }
                if(getDist(seeker.r, seeker.c, runners[i].r, runners[i].c) > 3) {
                    continue;
                }
                int nr, nc;
                if(runners[i].type == 1) {
                    nr = runners[i].r;
                    nc = runners[i].c + dir[runners[i].d];
                } else {
                    nr = runners[i].r + dir[runners[i].d];
                    nc = runners[i].c;
                }

                if(0 <= nr && nr < N && 0 <= nc && nc < N) {
                    if (seeker.r != nr || seeker.c != nc) {
                        runners[i].r = nr;
                        runners[i].c = nc;
                    }
                } else {
                    runners[i].updateD();
                    if(runners[i].type == 1) {
                        nr = runners[i].r;
                        nc = runners[i].c + dir[runners[i].d];
                    } else {
                        nr = runners[i].r + dir[runners[i].d];
                        nc = runners[i].c;
                    }
                    if (seeker.r != nr || seeker.c != nc) {
                        runners[i].r = nr;
                        runners[i].c = nc;
                    }
                }
            }
            //2.술래활동
            if(seeker.flag) {
                seeker.move_back();
            } else {
                seeker.move_front();
            }

            int cnt = 0;
            for(int s = 0; s < 3; s++) {
                int nr = seeker.r + (dr[seeker.d] * s);
                int nc = seeker.c + (dc[seeker.d] * s);
                if(0 <= nr && nr < N && 0 <= nc && nc < N && map[nr][nc] != 1) {
                    for(int j = 0; j < M; j++) {
                        if(runners[j].r == -1) {
                            continue;
                        }
                        if(nr == runners[j].r && nc == runners[j].c) {
                            cnt++;
                            runners[j].r = -1;
                        }
                    }
                }
            }
            ans += (cnt * t);
            t++;
        }
        return ans;
    }

    static int getDist(int r1, int c1, int r2, int c2) {
        return Math.abs(r1 - r2) + Math.abs(c1 - c2);
    }
}