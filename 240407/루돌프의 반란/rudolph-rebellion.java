import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main{
    static int N, M, P, C, D;
    static final int[] dr = {-1, 0, 1, 0, -1, 1, -1, 1};//상우하좌 -> 루돌프는 8 산타는 4
    static final int[] dc = {0, 1, 0, -1, -1, -1, 1, 1};//상우하좌
    static class Player implements Comparable<Player>{
        int index, r, c;
        double dist;

        public Player(int index, int r, int c, double dist) {
            this.index = index;
            this.r = r;
            this.c = c;
            this.dist = dist;
        }

        @Override
        public int compareTo(Player p) {
            if(p.dist < dist) {
                return 1;
            } else if(p.dist == dist) {
                if(p.r > r) {
                    return 1;
                } else if(p.r == r) {
                    if(p.c > c) {
                        return 1;
                    }
                }
            }

            return -1;
        }
    }

    static int[][] santas;
    static int[] result;

    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        StringBuilder sb = new StringBuilder();

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        D = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(br.readLine());
        int r = Integer.parseInt(st.nextToken()) - 1;
        int c = Integer.parseInt(st.nextToken()) - 1;
        santas = new int[P][4];
        result = new int[P];
        for(int i = 0; i < P; i++) {
            st = new StringTokenizer(br.readLine());
            int index = Integer.parseInt(st.nextToken()) - 1;
            santas[index][0] = Integer.parseInt(st.nextToken()) - 1;
            santas[index][1] = Integer.parseInt(st.nextToken()) - 1;
        }
        solution(r, c);

        for(int i = 0;  i < P; i++) {
            sb.append(result[i] + " ");
        }
        System.out.println(sb);
    }

    static void solution(int r, int c) {
        PriorityQueue<Player> pq;
        int k = 0;
        while(k < M) {
            //루돌프의 움직임
            //1. 가장 가까운 산타 구하기
            pq = new PriorityQueue<>();
            for(int i = 0; i < P; i++) {
                if(santas[i][2] == 2) {
                    continue;
                }
                pq.offer(new Player(i, santas[i][0], santas[i][1], getDist(r, c, santas[i][0], santas[i][1])));
            }

            if(pq.isEmpty()) {
                return;
            }

            //2. 방향 구하기
            int d = -1;
            int nextR = -1;
            int nextC = -1;
            double min = Double.MAX_VALUE;
            Player p = pq.poll();
            for(int i = 0; i < 8; i++) {
                int nr = r + dr[i];
                int nc = c + dc[i];
                if (0 <= nr && nr < N && 0 <= nc && nc < N) {
                    double dist = getDist(p.r, p.c, nr, nc);
                    if (dist < min) {
                        min = dist;
                        d = i;
                        nextR = nr;
                        nextC = nc;
                    }
                }
            }

            //기절한 산타는 못움직임
            //3. 충돌
            if(nextR == p.r && nextC == p.c) {
                result[p.index] += C;
                santas[p.index][2] = 1;
                santas[p.index][3] = k + 2;
                int nr = p.r + (dr[d] * C);
                int nc = p.c + (dc[d] * C);
                if(0 <= nr && nr < N && 0 <= nc && nc < N) {
                    interact(p.index, nr, nc, d);
                } else {
                    santas[p.index][2] = 2;
                }
            }
            //4. 루돌프 이동
            r = nextR;
            c = nextC;

            //산타 움직임
            for(int i = 0;  i < P; i++) {
                if(santas[i][2] > 0) {
                    continue;
                }
                d = -1;
                nextR = -1;
                nextC = -1;
                min = getDist(r, c, santas[i][0], santas[i][1]);
                flag: for(int j = 0; j < 4; j++) {
                    int nr = santas[i][0] + dr[j];
                    int nc = santas[i][1] + dc[j];
                    double dist = getDist(r, c, nr, nc);
                    if(0 <= nr && nr < N && 0 <= nc && nc < N && min > dist) {
                        for(int a = 0;  a < P; a++) {
                            if(nr == santas[a][0] && nc == santas[a][1]) {
                                continue flag;
                            }
                        }
                        min = dist;
                        d = j;
                        nextR = nr;
                        nextC = nc;
                    }
                }

                if(d != -1) {
                    if(r == nextR && c == nextC) {
                        result[i] += D;
                        santas[i][2] = 1;
                        santas[i][3] = k + 2;
                        if(d < 2) {
                            d += 2;
                        } else {
                            d -= 2;
                        }
                        int nr = nextR + (dr[d] * D);
                        int nc = nextC + (dc[d] * D);
                        if(0 <= nr && nr < N && 0 <= nc && nc < N) {
                            interact(i, nr, nc, d);
                        } else {
                            santas[i][2] = 2;
                        }
                    } else {
                        santas[i][0] = nextR;
                        santas[i][1] = nextC;
                    }
                }
            }

            //시간이동
            k++;

            for(int i = 0;  i < P; i++) {
                //점수올리기
                if(santas[i][2] != 2) {
                    result[i] += 1;
                }
                //잠든애들 깨우기
                if(santas[i][2] == 1 && k == santas[i][3]) {
                    santas[i][2] = 0;
                }
            }
        }
    }

    static void interact(int index, int r, int c, int d) {
        for(int i = 0; i < P; i++) {
            if(santas[i][2] == 2) {
                continue;
            }
            if(santas[i][0] == r && santas[i][1] == c) {
                santas[index][0] = r;
                santas[index][1] = c;
                int nr = r + dr[d];
                int nc = c + dc[d];
                if(0 <= nr && nr < N && 0 <= nc && nc < N) {
                    interact(i, nr, nc, d);
                } else {
                    santas[i][2] = 2;
                }
                return;
            }
        }
        santas[index][0] = r;
        santas[index][1] = c;
    }

    static double getDist(int r1, int c1, int r2, int c2) {
        return Math.pow(r1 - r2, 2) + Math.pow(c1 - c2, 2);
    }
}