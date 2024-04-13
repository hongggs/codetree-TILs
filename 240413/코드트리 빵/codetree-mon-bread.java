import java.util.*;
import java.io.*;
public class Main {
    static int[] dr = {-1, 0, 0, 1}; //상좌우하
    static int[] dc = {0, -1, 1, 0}; //상좌우하
    static int N, M;
    static int[][] map;
    static boolean[][] v;
    static Point[] players;
    static Store[] stores;
    static ArrayList<Store> bases;
    static class Point {
        int r, c;
        Point(int r, int c) {
            this.r = r;
            this.c = c;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "r=" + r +
                    ", c=" + c +
                    '}';
        }
    }
    static class Store {
        int r, c;
        boolean isVisit;

        Store(int r, int c) {
            this.r = r;
            this.c = c;
            isVisit = false;
        }

        @Override
        public String toString() {
            return "Store{" +
                    "r=" + r +
                    ", c=" + c +
                    ", isVisit=" + isVisit +
                    '}';
        }
    }
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        map = new int[N][N];
        players = new Point[M];
        stores = new Store[M];
        bases = new ArrayList<>();
        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
                if(map[i][j] == 1) {
                    bases.add(new Store(i, j));
                }
            }
        }

        for(int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            stores[i] = new Store(r, c);
        }

        System.out.println(solution());
    }

    static int solution() {
        int t = 0;
        int arriveCnt = 0;
        v = new boolean[N][N];
        while(arriveCnt < M) {
            /**
             * * 1. 격자에 있는 사람들은 가고싶은 편의점 방향으로 1칸 움직임
             *  *      - 우선순위, "상좌우하" 순
             *  *      - 해당 방향으로 움직여서 가고싶은 편의점과의 거리 구하고 그 중 가장 가까운 곳으로 선택!
             *  * 2. 만약 편의점에 도착한다면 해당 편의점에서 멈추게 됨. 이때부터는 다른 사람들은 해당 편의점이 있는 칸 못지나 감!!!
             *  *      - 단, 격자에 있는 사람들이 모두 이동한 뒤 다음 턴부터 적용된
             *  *      - 리스트에 해당 좌표 넣어두고, 다음에 visit배열 처리하기!
             */
            List<Point> arriveList = new ArrayList<>();
            for(int i = 0; i < t && i < M; i++) {
                if(stores[i].isVisit) {
                    continue;
                }
                int dist = Integer.MAX_VALUE;
                int nextR = -1;
                int nextC = -1;
                for(int d = 0; d < 4; d++) {
                    int nr = players[i].r + dr[d];
                    int nc = players[i].c + dc[d];
                    if(0 > nr || nr >= N || 0 > nc || nc >= N || v[nr][nc]) {
                        continue;
                    }
                    if(nr == stores[i].r && nc == stores[i].c) {
                        nextR = nr;
                        nextC = nc;
                        dist = 0;
                        break;
                    } else {
                        int temp = getDist(new Point(nr, nc), new Point(stores[i].r, stores[i].c));
                        if(temp < dist) {
                            nextR = nr;
                            nextC = nc;
                            dist = temp;
                        }
                    }
                }
                players[i].r = nextR;
                players[i].c = nextC;
                if(dist == 0) {
                    arriveList.add(new Point(nextR, nextC));
                    stores[i].isVisit = true;
                }
            }
            for(Point p : arriveList) {
                arriveCnt++;
                v[p.r][p.c] = true;
            }

            /**
             *  * 3. 현재 시간은 t분이고 t <= m을 만족한다면 t번 사람은 자신이 가고 싶은 편의점과 가장 가까이에 있는 베이스 캠프에 들어갑니다.
             *  *      - 우선순위, "상좌우하" 순
             *  *      - 행이 가장 작음
             *  *      - 열이 가장 작음
             *  *      - 이때부터 다른 사람들은 해당 베이스 캠프 칸 못지나감
             *  *      - 바로 visit배열에 체크해주기
             */
            if(t < M) {
                int dist = Integer.MAX_VALUE;
                int baseIndex = -1;
                int nextR = Integer.MAX_VALUE;
                int nextC = Integer.MAX_VALUE;
                for (int i = 0; i < bases.size(); i++) {
                    if (bases.get(i).isVisit) {
                        continue;
                    }
                    int tr = bases.get(i).r;
                    int tc = bases.get(i).c;
                    int temp = getDist(new Point(tr, tc), new Point(stores[t].r, stores[t].c));

                    if (temp < dist) {
                        dist = temp;
                        baseIndex = i;
                        nextR = tr;
                        nextC = tc;
                    } else if (temp == dist) {
                        if (tr < nextR) {
                            baseIndex = i;
                            nextR = tr;
                            nextC = tc;
                        } else if (tr == nextR) {
                            if (tc < nextC) {
                                baseIndex = i;
                                nextR = tr;
                                nextC = tc;
                            }
                        }
                    }
                }
                v[nextR][nextC] = true;
                bases.get(baseIndex).isVisit = true;
                players[t] = new Point(nextR, nextC);
            }
            t++;

        }
        return t;
    }

    static int getDist(Point start, Point target) {
        boolean[][] v_d = new boolean[N][N];
        Queue<int[]> q = new ArrayDeque<>();
        q.offer(new int[]{start.r, start.c, 1});
        v_d[start.r][start.c] = true;
        while(!q.isEmpty()) {
            int[] now = q.poll();
            int r = now[0];
            int c = now[1];
            int d = now[2];
            for(int i = 0; i < 4; i++) {
                int nr = r + dr[i];
                int nc = c + dc[i];
                if(0 <= nr && nr < N && 0 <= nc && nc < N && !v[nr][nc] && !v_d[nr][nc]) {
                    if(nr == target.r && nc == target.c) {
                        return d + 1;
                    }
                    v_d[nr][nc] = true;
                    q.offer(new int[]{nr, nc, d + 1});
                }
            }
        }
        return Integer.MAX_VALUE;
    }

}