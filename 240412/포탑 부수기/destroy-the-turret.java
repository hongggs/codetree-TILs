import java.io.*;
import java.util.*;
public class Main {
    static final int[] dr = {0, 1, 0, -1, -1, -1, 1, 1,}; //우하좌상
    static final int[] dc = {1, 0, -1, 0, 1, -1, 1, -1};
    static int N, M, K;
    static int[][][] map;
    static boolean[][] v;
    static class Point {
        int r, c;

        public Point(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        map = new int[N][M][2];
        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < M; j++) {
                map[i][j][0] = Integer.parseInt(st.nextToken());
            }
        }
        solution();
        int ans = Integer.MIN_VALUE;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                ans = Math.max(ans, map[i][j][0]);
            }
        }
        System.out.println(ans);

    }

    static void solution() {
        int t = 1;
        while(t <= K) {
            boolean[][] v = new boolean[N][M];
            // 1. 공격자 선정
            Point ap = getAttacker();
            if(ap.r == -1) {
                return;
            }
            // 2. 공격
            //1)자신을 제외한 가장 강한 포탑 선택
            Point cp = getCompetitor();
            if(ap.r == cp.r && ap.c == cp.c) {
                return;
            }
            v[ap.r][ap.c] = true;
            v[cp.r][cp.c] = true;
            map[ap.r][ap.c][0] += (N + M);
            map[ap.r][ap.c][1] = t;
            map[cp.r][cp.c][1] = t;
            int damage = map[ap.r][ap.c][0];

            // 2)레이저공격
            Node laserNode = doLaserAttack(new Node(ap.r, ap.c, null), cp);
            if(laserNode != null) {
                map[laserNode.r][laserNode.c][0] = Math.max(map[laserNode.r][laserNode.c][0] - damage, 0); //laserNode == cp
                damage /= 2;
                laserNode = laserNode.prev;
                while(laserNode.prev != null) {
                    v[laserNode.r][laserNode.c] = true;
                    map[laserNode.r][laserNode.c][0] = Math.max(map[laserNode.r][laserNode.c][0] - damage, 0);
                    laserNode = laserNode.prev;
                }
            } else {
                map[cp.r][cp.c][0] = Math.max(map[cp.r][cp.c][0] - damage, 0);
                damage /= 2;
                for(int i = 0; i < 8; i++) {
                    int nr = validatePoint(cp.r + dr[i]);
                    int nc = validatePoint(cp.c + dc[i]);
                    if(!v[nr][nc] && map[nr][nc][0] > 0) {
                        v[nr][nc] = true;
                        map[nr][nc][0] = Math.max(map[nr][nc][0]- damage, 0);
                    }
                }
            }

            for(int i = 0; i < N; i++) {
                for(int j = 0; j < M; j++) {
                    if(!v[i][j] && map[i][j][0] > 0) {
                        map[i][j][0]++;
                    }
                }
            }
            t++;
        }


    }

    static class Node {
        int r, c;
        Node prev;
        public Node(int r, int c, Node prev) {
            this.r = r;
            this.c = c;
            this.prev = prev;
        }

    }
    static Node doLaserAttack(Node start, Point target) { //bfs
        Queue<Node> q = new ArrayDeque<>();
        boolean[][] v = new boolean[N][M];
        v[start.r][start.c] = true;
        q.offer(start);
        while(!q.isEmpty()) {
            Node now = q.poll();
            for(int i = 0; i < 4; i++) {
                int nr = validatePoint(now.r + dr[i]);
                int nc = validatePoint(now.c + dc[i]);

                if(!v[nr][nc] && map[nr][nc][0] > 0) {
                    v[nr][nc] = true;
                    if(nr == target.r && nc == target.c) {
                        return new Node(nr, nc, now);
                    }
                    q.offer(new Node(nr, nc, now));
                }
            }
        }

        return null;
    }

    static Point getAttacker() {
        Point ap = new Point(-1, -1);
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(0 < map[i][j][0] && map[i][j][0] < min) {
                    min = map[i][j][0];
                    ap.r = i;
                    ap.c = j;
                } else if(0 < map[i][j][0] && map[i][j][0] == min) {
                    if(map[ap.r][ap.c][1] < map[i][j][1]) {
                        min = map[i][j][0];
                        ap.r = i;
                        ap.c = j;
                    } else if (map[ap.r][ap.c][1] == map[i][j][1]) {
                        if(ap.r + ap.c < i + j) {
                            min = map[i][j][0];
                            ap.r = i;
                            ap.c = j;
                        } else if(ap.r + ap.c == i + j) {
                            if(ap.c < j) {
                                min = map[i][j][0];
                                ap.r = i;
                                ap.c = j;
                            }
                        }
                    }
                }
            }
        }

        return ap;
    }

    static Point getCompetitor() {
        Point cp = new Point(-1, -1);
        int max = Integer.MIN_VALUE;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(0 < map[i][j][0] && map[i][j][0] > max) {
                    max = map[i][j][0];
                    cp.r = i;
                    cp.c = j;
                } else if(0 < map[i][j][0] && map[i][j][0] == max) {
                    if(map[cp.r][cp.c][1] > map[i][j][1]) {
                        max = map[i][j][0];
                        cp.r = i;
                        cp.c = j;
                    } else if (map[cp.r][cp.c][1] == map[i][j][1]) {
                        if(cp.r + cp.c > i + j) {
                            max = map[i][j][0];
                            cp.r = i;
                            cp.c = j;
                        } else if(cp.r + cp.c == i + j) {
                            if(cp.c > j) {
                                max = map[i][j][0];
                                cp.r = i;
                                cp.c = j;
                            }
                        }
                    }
                }
            }
        }
        return cp;
    }

    static int validatePoint(int x) {
        if(x < 0) {
            return N - 1;
        } else if(x >= N) {
            return 0;
        }
        return x;
    }
}