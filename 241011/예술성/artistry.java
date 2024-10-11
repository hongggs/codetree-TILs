import java.util.*;
import java.io.*;
public class Main {
    static int[] dr = {-1, 1, 0, 0};
    static int[] dc = {0, 0, -1, 1};
    static int N;
    static int[][] map;
    static int K = 3;
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        N = Integer.parseInt(br.readLine().trim());
        map = new int[N][N];
        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine().trim());
            for(int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        int ans = getScore();
        while(K-- > 0) {
            rotate();
            int x = getScore();
            ans += x;
        }
        System.out.println(ans);
    }

    static int getScore() {
        int score = 0;

        int[][] group = new int[N][N];
        HashMap<Integer, int[]> groupCountMap = new HashMap<>();
        int index = 1;
        //그룹 구하기
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(group[i][j] == 0) {
                    groupCountMap.put(index, new int[]{getGroup(i, j, group, index) + 1, map[i][j]});
                    index++;
                }
            }
        }
        //점수 구하기
        for(int k = 1; k < index; k++) {
            int[] adjCount = new int[index];
            for(int i = 0; i < N; i++) {
                for(int j = 0; j < N; j++) {
                    if(group[i][j] == k) {
                        for(int d = 0; d < 4; d++) {
                            int nr = i + dr[d];
                            int nc = j + dc[d];
                            if (0 <= nr && nr < N && 0 <= nc && nc < N && group[nr][nc] > k) {
                                adjCount[group[nr][nc]]++;
                            }
                        }
                    }
                }
            }
            for(int i = 1; i < index; i++) {
                if(adjCount[i] > 0) {
                    score += (groupCountMap.get(k)[0] + groupCountMap.get(i)[0]) * groupCountMap.get(k)[1] * groupCountMap.get(i)[1] * adjCount[i];
                }
            }
        }
        return score;
    }

    static int getGroup(int r, int c, int[][] group, int mark) {
        int sum = 0;
        group[r][c] = mark;

        for(int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            if(0 <= nr && nr < N && 0 <= nc && nc < N && group[nr][nc] == 0 && map[nr][nc] == map[r][c]) {
                sum += (1 + getGroup(nr, nc, group, mark));
            }
        }
        return sum;
    }

    static void rotate() {
        int M = N / 2;
        //십자 회전
        int[][] newMap = new int[N][N];
        newMap[M][M] = map[M][M];
        for(int i = 0; i < M; i++) {
            newMap[M][i] = map[i][M];
            newMap[i][M] = map[M][N - 1 - i];
            newMap[M][N - 1 - i] = map[N - 1 - i][M];
            newMap[N - 1 - i][M] = map[M][i];
        }
        //4개의 사각형 회전
        for(int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                newMap[i][j] = map[M - 1 - j][i];
                newMap[i][j + M + 1] = map[M - 1 - j][i + M + 1];
                newMap[i + M + 1][j] = map[N - 1 - j][i];
                newMap[i + M + 1][j + M + 1] = map[N - 1 - j][i + M + 1];
            }
        }
        map = newMap;
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