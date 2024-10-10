import java.io.*;
import java.util.*;

public class Main {
    static int[] dr = {-1, 1, 0, 0};//상하좌우
    static int[] dc = {0, 0, -1, 1};
    static int N, M, K;
    static int[][] map;
    static int ans;
    static int[] exit;
    static int[][] players;
    static int[][] playerMap;
    static int escape;
    public static void main(String[] args) throws Exception{
        /*
        * K초 동안 반복
        * M명의 참가자가 미로 탈출
        * map은 N * N
        *   <미로 구성>
        *       - 빈칸: 이동가능
        *       - 벽: 이동 불가, 1~9사이의 내구도, 회전할 때 내구도 1씩 깎임, 내구도 0이되면 빈칸 됨
        *       - 출귀 참가자가 해당 칸 도달하면 즉시 탈출
        * [참가자 이동]
        * 1초마다 한카씩 움직인다.
        * - 두 위치 최단거리는 (Math.abs(r1 -r2) + Math.abs(c1 - c2))
        * - 모든 참가자는 동시에 움직인다.
        * - 상하좌우, 벽없는 곳으로 이동
        * - 움직인 칸은 현재 머물러 있던 칸보다 출구까지의 최단거리가 가까워야 한다.
        * - 움직일 수 있는 칸이 2개 이상이면 상하로 움직이는게 우선
        * - 참가자가 못움직이는 상황이면 안움직임
        * - 한칸에 2명 이상 참가자 가능
        *
        * [미로 회전]
        * - 한 명 이상의 참가자와 출구를 포함한 가장 작은 정사각형 찾기
        * - 가장 자근 크기의 정사각형 2개이상이면 r이 작은 것, c가 작은 것
        * - 선택된 정사각형은 시계 방향으로 90도 회전 & 벽은 내구도가 1씩 깎임
        * */
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine().trim());
        StringBuilder sb = new StringBuilder();

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        map = new int[N][N];
        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine().trim());
            for(int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        players = new int[M][2];
        playerMap = new int[N][N];
        for(int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine().trim());
            players[i][0] = Integer.parseInt(st.nextToken()) - 1;
            players[i][1] = Integer.parseInt(st.nextToken()) - 1;
            playerMap[players[i][0]][players[i][1]]++;
        }

        exit = new int[2];
        st = new StringTokenizer(br.readLine().trim());
        exit[0] = Integer.parseInt(st.nextToken()) - 1;
        exit[1] = Integer.parseInt(st.nextToken()) - 1;
        escape = 0;

        while(K-- > 0 && escape < M) {
            movePlayers();
            rotateMap();
        }
        sb.append(ans).append("\n").append((exit[0] + 1) + " " + (exit[1] + 1));
        System.out.println(sb);
    }

    static void movePlayers() {
        for(int i = 0; i < M; i++) {
            if(players[i][0] == -1) {
                continue;
            }
            int minV = getDist(players[i], exit);
            int dir = -1;
            int[] next = new int[2];
            for(int j = 0; j < 4; j++) {
                next[0] = players[i][0] + dr[j];
                next[1] = players[i][1] + dc[j];
                if (0 <= next[0] && next[0] < N && 0 <= next[1] && next[1] < N && map[next[0]][next[1]] == 0) {
                    int dist = getDist(next, exit);
                    if(dist < minV) {
                        minV = dist;
                        dir = j;
                    }
                }
            }
            next[0] = players[i][0];
            next[1] = players[i][1];
            if(dir != -1) {
                ans++;
                next[0] += dr[dir];
                next[1] += dc[dir];
                playerMap[players[i][0]][players[i][1]]--;
                players[i][0] = next[0];
                players[i][1] = next[1];
                playerMap[players[i][0]][players[i][1]]++;
            }
            if(next[0] == exit[0] && next[1] == exit[1]) {
                playerMap[players[i][0]][players[i][1]]--;
                players[i][0] = -1;
                escape++;
            }
        }
    }

    static void rotateMap() {
        int[] arr = getSquare();
        int r = arr[0];
        int c = arr[1];
        int v = arr[2];
        int[][] newMap = new int[v][v];
        Queue<Integer> updates = new ArrayDeque<>();
        int[] nextExit = new int[2];
        for(int i = 0; i < v; i++) {
            for(int j = 0; j < v; j++) {
                int or = r + (v - 1) - j;
                int oc = c + i;
                if(map[or][oc] > 0) {
                    map[or][oc]--;
                }
                newMap[i][j] = map[or][oc];
                if (or == exit[0] && oc == exit[1]) {
                    nextExit[0] = r + i;
                    nextExit[1] = c + j;
                }
                if(playerMap[or][oc] > 0) {
                    for(int x = 0; x < M; x++) {
                        if(or == players[x][0] && oc == players[x][1]) {
                            playerMap[or][oc]--;
                            players[x][0] = r + i;
                            players[x][1] = c + j;
                            updates.offer(x);
                        }
                    }
                }
            }
        }

        for(int i = 0; i < v; i++) {
            for(int j = 0; j < v; j++) {
                map[r + i][c + j] = newMap[i][j];

            }
        }
        exit = nextExit;
        while(!updates.isEmpty()) {
            int x = updates.poll();
            playerMap[players[x][0]][players[x][1]]++;
        }
    }

    static int[] getSquare() {
        int[] result = new int[3];
        int minV = N;
        for(int i = 0; i < N - 1; i++) {
            for(int j = 0; j < N - 1; j++) {
                int temp = 2;
                while(temp < N) {
                    int er = i + (temp - 1);
                    int ec = j + (temp - 1);
                    if(0 > er || er >= N || 0 > ec || j + ec >= N) {
                        break;
                    }

                    if(i <= exit[0] && exit[0] <= er && j <= exit[1] && exit[1] <= ec) {
                        boolean isPlayer = false;
                        for(int a = i; a<=er; a++) {
                            for (int b = j; b <= ec; b++) {
                                if(playerMap[a][b] > 0) {
                                    isPlayer = true;
                                    break;
                                }
                            }
                        }

                        if(isPlayer && temp < minV) {
                            minV = temp;
                            result[0] = i;
                            result[1] = j;
                            break;
                        }
                    }
                    temp++;
                }
            }
        }
        result[2] = minV;
        return result;
    }

    static int getDist(int[] p1, int[] p2) {
        return Math.abs(p1[0] -p2[0]) + Math.abs(p1[1] - p2[1]);
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
    static void printP() {
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                System.out.print(playerMap[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}