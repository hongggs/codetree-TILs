import java.util.*;
import java.io.*;
public class Main {
    static int[] dr = {-1, 1, 0, 0};
    static int[] dc = {0, 0, -1, 1};
    static int N;
    static int[][] map;
    static int K = 3;
    static Queue<int[]> adjPoints;
    static int adjCnt;
    public static void main(String[] args) throws Exception {
        /*
        * 그림: n*n
        * 한칸에 1이상 10이하의 숫자
        * 예술성 점수: 모든 그룹 쌍의 조화로움의 합
        *       (그룹 a에 속한 칸의 수 + 그룹 b에 속한 칸의 수 )
        *           x 그룹 a를 이루고 있는 숫자 값 x 그룹 b를 이루고 있는 숫자 값
        *           x 그룹 a와 그룹 b가 서로 맞닿아 있는 변의 수
        * [예술성 점수 구하기]
        *   - 그룹a 구하기
        *       v2새로 생성, v1과 v2 방문체크하면서 움직이기
        *       v1에 방문안했으면 해당 부분 이어진 구역 정하기 => 그룹a에 속한 칸의 수, 그룹a 숫자 값
        *   - 그룹a과 인접한 그룹b구하기
        *       v1, v2에 방문안하고 v1과 인접했으면 탐색 시작
        *       이어진 부분 구하기 => 그룹b에 속한 칸의 수, 그룹b 숫자 값
        *   - 그룹b와 서로 맞닿아 있는 변의 수 구하기
        *       그룹a에서 그룹 b랑 맞닿아 끝난 부분 모두 조회
        *
        * [회전]
        * 십자 모양 반시계 방향 회전
        * 십자 모양 제외한 4개 정사각형 시계방향 90도씩 회전
        *
        * => 초기 예술점수 + 1회전 + 2회전 + 3회전
        * */
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        N = Integer.parseInt(br.readLine().trim());
        map = new int[N][N];
        adjPoints = new ArrayDeque<>();
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

        int[][] main = new int[N][N];
        int index = 1;
        //그룹 a 구하기
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(main[i][j] == 0) {
                    boolean[][] sub = new boolean[N][N];
                    int a = getSizeOfPaintMain(i, j, main, index) + 1;
                    while(!adjPoints.isEmpty()) {
                        int[] p = adjPoints.poll();
                        if(sub[p[0]][p[1]]) {
                            continue;
                        }
                        adjCnt = 0;
                        int b = getSizeOfPaintSub(p[0], p[1], sub, main, index) + 1;
//                        System.out.println("a = " + a);
//                        System.out.println("b = " + b);
//                        System.out.println("main = " + map[i][j]);
//                        System.out.println("sub = " + map[p[0]][p[1]]);
//                        System.out.println("adjCnt = " + adjCnt);
                        score = score + ((a + b) * map[i][j] * map[p[0]][p[1]] * adjCnt);

                    }
                    index++;
                }
            }
        }
        return score;
    }

    static int getSizeOfPaintMain(int r, int c, int[][] v, int mark) {
        int sum = 0;
        v[r][c] = mark;

        for(int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            if(0 <= nr && nr < N && 0 <= nc && nc < N && v[nr][nc] == 0) {
                if(map[nr][nc] == map[r][c]) {
                    sum += (1 + getSizeOfPaintMain(nr, nc, v, mark));
                } else {
                    adjPoints.offer(new int[]{nr, nc});
                }
            }
        }
        return sum;
    }

    static int getSizeOfPaintSub(int r, int c, boolean[][] v, int[][] main, int mark) {
        int sum = 0;
        v[r][c] = true;

        for(int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            if(0 <= nr && nr < N && 0 <= nc && nc < N && !v[nr][nc]) {
                if(map[nr][nc] == map[r][c]) {
                    sum += (1 + getSizeOfPaintSub(nr, nc, v, main, mark));
                } else {
                    if(main[nr][nc] == mark) {
                        adjCnt++;
                    }
                }
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