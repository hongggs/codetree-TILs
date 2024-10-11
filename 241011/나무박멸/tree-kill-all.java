import java.util.*;
import java.io.*;

public class Main {
    static int[] dr = {-1, 1, 0, 0, -1, -1, 1, 1};//상하좌우
    static int[] dc = {0, 0, -1, 1, -1, 1, -1, 1};
    static int N, M, K, C;
    static int[][] map;
    static int[][] drug;
    static class Point {
        int r, c, w;

        public Point(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine().trim());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());

        map = new int[N][N];
        drug = new int[N][N];
        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine().trim());
            for(int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        int ans = 0;
        while (M-- > 0) {
            grow();
            spread();
            ans += decreaseTree();
            decreaseDrug();
        }
        System.out.println(ans);
    }

    /**
     * [나무 성장]
     *  인접한 4개의 칸 중 나무가 있는 칸의 수만큼 나무 성장
     *  성장은 동시에 일어남
     */
    static void grow() {
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if (map[i][j] > 0) {
                    int cnt = 0;
                    for (int d = 0; d < 4; d++) {
                        int nr = i + dr[d];
                        int nc = j + dc[d];
                        if(isRange(nr, nc) && map[nr][nc] > 0) {
                            cnt++;
                        }
                    }
                    map[i][j] += cnt;
                }
            }
        }
    }

    /**
     * [나무 번식]
     *   기존에 있던 나무들은 인접한 4개의 칸 중 "벽, 다른 나무, 제초제 모두 없는 칸"에 번식
     *   (각 칸의 나무 그루 수 / 번식이 가능한 칸) 개수만큼 번식, 나눌 때 생기는 나머지는 버림
     *   번식의 과정은 모든 나무 동시에 일어남
     */
    static void spread() {
        ArrayList<Point> newTrees = new ArrayList<>();
        int start = 0;
        for(int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j] > 0) {
                    int cnt = 0;
                    for (int d = 0; d < 4; d++) {
                        int nr = i + dr[d];
                        int nc = j + dc[d];
                        if(isRange(nr, nc) && map[nr][nc] == 0 && drug[nr][nc] == 0) {
                            newTrees.add(new Point(nr, nc));
                            cnt++;
                        }
                    }
                    int end = start + cnt;
                    while(start < end) {
                        newTrees.get(start).w = map[i][j] / cnt;
                        start++;
                    }
                }
            }
        }

        for(int i = 0; i < newTrees.size(); i++) {
            Point p = newTrees.get(i);
            map[p.r][p.c] += p.w;
        }
    }

    /**
     * [제초제]
     *   가장 많은 나무 박멸되는 칸에 제초제 뿌림
     *   나무가 없는 칸에 뿌리면 아무 일 일어나지 않음
     *   나무가 있는 칸에 뿌리면 4개의 대각선 방향으로 k칸만큼 전파
     *       단 나무가 아예 없는 칸이나 벽이 있으면 제초제 멈춤
     *   제초제가 뿌린칸은 c년만큼 제초제 남아있다가 c+1년에 사라짐
     *   제초제가 뿌려진 칸에 다시 제초제가 뿌려지면 새로 뿌려진해부터 다시 c년동안 제초제 유지
     *   박멸되는 칸 개수가 같으면 r이 작은 순 -> c가 작은 순
     */
    static int decreaseTree() {
        int rr = -1;
        int rc = -1;
        int maxV = -1;
        for(int i = 0; i < N; i++) {
            for(int j = 0 ; j < N; j++) {
                if(map[i][j] == -1) continue;
                int count = getCount(i, j);
                if(count > maxV) {
                    maxV = count;
                    rr = i;
                    rc = j;
                }
            }
        }

        map[rr][rc] = 0;
        drug[rr][rc] = C + 1;
        for(int d = 4; d < 8; d++) {
            for(int k = 1; k <= K; k++) {
                int nr = rr + k * dr[d];
                int nc = rc + k * dc[d];
                if(!isRange(nr, nc) || map[nr][nc] == -1) {
                    break;
                }
                if(map[nr][nc] == 0) {
                    drug[nr][nc] = C + 1;
                    break;
                }
                map[nr][nc] = 0;
                drug[nr][nc] = C + 1;
            }
        }

        return maxV;
    }

    static int getCount(int r, int c) {
        if(map[r][c] == 0) {
            return 0;
        }

        int sum = map[r][c];
        for(int d = 4; d < 8; d++) {
            for(int k = 1; k <= K; k++) {
                int nr = r + k * dr[d];
                int nc = c + k * dc[d];
                if(!isRange(nr, nc) || map[nr][nc] <= 0) {
                    break;
                }
                sum += map[nr][nc];
            }
        }

        return sum;
    }

    static void decreaseDrug() {
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(drug[i][j] > 0) {
                    drug[i][j]--;
                }
            }
        }
    }

    static boolean isRange(int r, int c) {
        return 0 <= r && r < N && 0 <= c && c < N;
    }
}