import java.io.*;
import java.util.*;

public class Main {

    static int[] dr = {-1, -1, 0, 1, 1, 1, 0, -1};//상좌하우
    static int[] dc = {0, -1, -1, -1, 0, 1, 1, 1};
    static int N = 4;
    static int M;
    static int[][] map;
    static int pr, pc;
    static int[][] deadMap;
    static ArrayList<Monster> monsters;
    static Queue<Dead> deads;
    static int maxV;
    static int[][] routes;
    static class Monster {
        int r, c, d;

        public Monster(int r, int c, int d) {
            this.r = r;
            this.c = c;
            this.d = d;
        }
    }

    static class Dead {
        int r, c, time;

        public Dead(int r, int c, int time) {
            this.r = r;
            this.c = c;
            this.time = time;
        }
    }
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine().trim());
        M = Integer.parseInt(st.nextToken());
        int T = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(br.readLine().trim());
        pr = Integer.parseInt(st.nextToken()) - 1;
        pc = Integer.parseInt(st.nextToken()) - 1;

        map = new int[N][N];
        deadMap = new int[N][N];
        monsters = new ArrayList<>();
        deads = new ArrayDeque<>();
        routes = new int[3][2];

        for(int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine().trim());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken()) - 1;
            monsters.add(new Monster(r, c, d));
            map[r][c]++;
        }

        while (T-- > 0) {
            // 몬스터 복제 시도
            ArrayList<Monster> eggs = cloneMonsters();

            //몬스터 이동
            moveMonsters();

            //팩맨이동
            maxV = 0;
            boolean[][] v = new boolean[N][N];
            v[pr][pc] = true;
            movePacman(0, pr, pc, new int[3][2], v, 0);
            if(maxV > 0) {
                for(int i = 0; i < 3; i++) {
                    int r = routes[i][0];
                    int c = routes[i][1];
                    int n = map[r][c];
                    while(n-- > 0) {
                        for(Monster m : monsters) {
                            if (m.r == r && m.c == c) {
                                m.r = -1;
                                deads.offer(new Dead(r, c, 3));
                                deadMap[r][c]++;
                                map[r][c]--;
                            }
                        }
                    }
                }
                pr = routes[2][0];
                pc = routes[2][1];
            }

            //몬스터 시체 소멸
            int size = deads.size();
            while(size-- > 0) {
                Dead d = deads.poll();
                d.time--;
                if(d.time == 0) {
                    deadMap[d.r][d.c]--;
                } else {
                    deads.offer(d);
                }
            }

            //몬스터 복제 완성
            for(Monster m : eggs) {
                monsters.add(m);
                map[m.r][m.c]++;
            }
        }

        int ans = 0;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                ans += map[i][j];
            }
        }
        System.out.println(ans);
    }

    static ArrayList<Monster> cloneMonsters() {
        ArrayList<Monster> newList = new ArrayList<>();
        for(Monster m : monsters) {
            if(m.r == -1) {
                continue;
            }
            newList.add(new Monster(m.r, m.c, m.d));
        }

        return newList;
    }

    static void moveMonsters() {
        for(Monster m : monsters) {
            if(m.r == -1) {
                continue;
            }
            int d = m.d;
            int cnt = 8;
            while(cnt-- > 0) {
                int nr = m.r + dr[d];
                int nc = m.c + dc[d];
                if (0 <= nr && nr < N && 0 <= nc && nc < N && !(nr == pr && nc == pc) && deadMap[nr][nc] == 0) {
                    map[m.r][m.c]--;
                    map[nr][nc]++;
                    m.r = nr;
                    m.c = nc;
                    m.d = d;
                    break;
                }
                d = (d + 1) % 8;
            }
        }
    }

    static void movePacman(int index, int r, int c, int[][] tempRoutes, boolean[][] v, int sum) {
        if (index == 3) {
            if (sum > maxV) {
                maxV = sum;
                for (int i = 0; i < 3; i++) {
                    routes[i][0] = tempRoutes[i][0];
                    routes[i][1] = tempRoutes[i][1];
                }
            }
            return;
        }
        for (int i = 0; i < 8; i += 2) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            if (0 <= nr && nr < N && 0 <= nc && nc < N && !v[nr][nc]) {
                v[nr][nc] = true;
                tempRoutes[index][0] = nr;
                tempRoutes[index][1] = nc;
                movePacman(index + 1, nr, nc, tempRoutes, v, sum + map[nr][nc]);
                v[nr][nc] = false;
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
        System.out.println();
    }
}