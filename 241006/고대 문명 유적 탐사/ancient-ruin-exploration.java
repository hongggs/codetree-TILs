import java.util.*;
import java.io.*;

/**
 * 1. 탐사 진행
 * 1) 5*5 격자에서 3*3 격자 선택
 *      선택된 격자는 시계 방향으로 90도 180도 270도 중 하나의 각도만큼 회전시킬 수 있다.
 *      대신 꼭 회전을 해야한다.
 * 2) 중심좌표를 선택->가운데 좌표임
 *      90도씩 회전하는 함수 만들기!
 * 3) 유물 1차 획득 가치 최대화하고
 *      그러한 방법 중 회전 각도가 가장 작은 방법 선택
 *      그리고 그중 열이 가장 작은 구간을
 *      그리고 열이 같다면 행이 가장 작은 구간 선택
 *
 * 2. 유물 획득
 * 같은 종류 유물 조각이 3개이상 연결된 경우 사라짐
 * 그리고 사라진 부분에 새로운 유적 정보가 나타남
 * 3. 획득 가능한 유물이 없으면 무조건 종료
 */
public class Main {
    static int[] dr = {-1, 1, 0, 0};
    static int[] dc = {0, 0, -1, 1};
    static int N = 5;
    static int K, M;
    static int[][] map;
    static Queue<Integer> q;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine().trim());
        StringBuilder sb = new StringBuilder();

        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        map = new int[N][N];
        q = new ArrayDeque<>();
        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine().trim());
            for(int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        st = new StringTokenizer(br.readLine().trim());
        for(int i = 0; i < M; i++) {
            q.offer(Integer.parseInt(st.nextToken()));
        }

        while(K-- > 0) {
            /*
            *  * 1. 탐사 진행
             * 1) 5*5 격자에서 3*3 격자 선택
             *      선택된 격자는 시계 방향으로 90도 180도 270도 중 하나의 각도만큼 회전시킬 수 있다.
             *      대신 꼭 회전을 해야한다.
             * 2) 중심좌표를 선택->가운데 좌표임
             *      90도씩 회전하는 함수 만들기!
             * 3) 유물 1차 획득 가치 최대화하고
             *      그러한 방법 중 회전 각도가 가장 작은 방법 선택
             *      그리고 그중 열이 가장 작은 구간을
             *      그리고 열이 같다면 행이 가장 작은 구간 선택
             *
            * */
            int r = 5;
            int c = 5;
            int maxScore = 0;
            int maxAngle = 0;
            int[][] nextMap = null;
            for(int i = 1; i < N - 1; i++) {
                for(int j = 1; j < N - 1; j++) {
                    for (int a = 0; a < 3; a++) {
                        int[][] tempMap = copyMap();
                        int tempScore = getScore(i, j, a, tempMap);
                        if(tempScore > maxScore) {
                            maxScore = tempScore;
                            maxAngle = a;
                            r = i;
                            c = j;
                            nextMap = tempMap;
                        } else if(tempScore == maxScore) {
                            if(a < maxAngle) {
                                maxScore = tempScore;
                                maxAngle = a;
                                r = i;
                                c = j;
                                nextMap = tempMap;
                            } else if(a == maxAngle) {
                                if(j < c) {
                                    maxScore = tempScore;
                                    maxAngle = a;
                                    r = i;
                                    c = j;
                                    nextMap = tempMap;
                                } else if(j == c) {
                                    if(i < r) {
                                        maxScore = tempScore;
                                        maxAngle = a;
                                        r = i;
                                        c = j;
                                        nextMap = tempMap;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(maxScore == 0) {
                break;
            } else {
                map = nextMap;
                while(true) {
                    erase();
                    refill();
                    int extra = 0;
                    boolean[][] v = new boolean[N][N];
                    for(int x = 0; x < N; x++) {
                        for(int y = 0; y < N; y++) {
                            if(!v[x][y]) {
                                int temp = getCount(x, y, v, map) + 1;
                                if(temp >= 3) {
                                    extra += temp;
                                }
                            }
                        }
                    }
                    if(extra == 0) {
                        break;
                    } else {
                        maxScore += extra;
                    }
                }
                sb.append(maxScore).append(" ");
            }
        }
        System.out.println(sb);
    }

    static int getScore(int r, int c, int a, int[][] tempMap) {
        int score= 0;
        for(int i = 0; i <= a; i++) {
            for(int j = 0; j < 2; j++) {
                rotate(r, c, tempMap);
            }
        }
        boolean[][] v = new boolean[N][N];
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(!v[i][j]) {
                    int temp = getCount(i, j, v, tempMap) + 1;
                    if(temp >= 3) {
                        score += temp;
                    }
                }
            }
        }

        return score;
    }

    static void rotate(int r, int c, int[][] tempMap) {
        int temp = tempMap[r - 1][c - 1];

        // 왼쪽
        for(int i = r - 1; i < r - 1 + 2; i++) {
            tempMap[i][c - 1] = tempMap[i + 1][c - 1];
        }

        //아래쪽
        for(int i = c - 1; i < c - 1 + 2; i++) {
            tempMap[r + 1][i] = tempMap[r + 1][i + 1];
        }

        //오른쪽
        for(int i = r + 1; i >= r; i--) {
            tempMap[i][c + 1] = tempMap[i - 1][c + 1];
        }

        //위쪽
        tempMap[r - 1][c + 1] = tempMap[r - 1][c];
        tempMap[r - 1][c] = temp;
    }

    static int getCount(int r, int c, boolean[][] v, int[][] tempMap) {
        int temp = 0;
        v[r][c] = true;
        for(int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            if(0 <= nr && nr < N && 0 <= nc && nc < N && !v[nr][nc] && tempMap[nr][nc] == tempMap[r][c]) {
                temp += 1 + getCount(nr, nc, v, tempMap);
            }
        }
        return temp;
    }

    static void erase() {
        boolean[][] v = new boolean[N][N];
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(!v[i][j]) {
                    if(getCount(i, j, v, map) + 1 >= 3) {
                        int x = map[i][j];
                        map[i][j] = 0;
                        fillZero(i, j, x);
                    }
                }
            }
        }
    }

    static void fillZero(int r, int c, int x) {
        for(int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            if(0 <= nr && nr < N && 0 <= nc && nc < N && map[nr][nc] == x) {
                map[nr][nc] = 0;
                fillZero(nr, nc, x);
            }
        }
    }
    static void refill() {
        for(int i = 0; i < N; i++) {
            for(int j = N - 1; j >= 0; j--) {
                if(map[j][i] == 0) {
                    map[j][i] = q.poll();
                    q.offer(map[j][i]);
                }
            }
        }
    }

    static int[][] copyMap() {
        int[][] tempMap = new int[N][N];
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                tempMap[i][j] = map[i][j];
            }
        }
        return tempMap;
    }
}