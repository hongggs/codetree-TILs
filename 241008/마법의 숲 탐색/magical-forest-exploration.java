import java.util.*;
import java.io.*;

/**
 *
 * 십자모향 골렘(5칸), 중앙칸을 제외하고 4칸은 골렘의 출구, 탑승은 어디서나 할 수 있지만 나갈때는 정해진 출구만 가능
 * [골렘 이동] (가장 남쪽에 위치할때까지 반복)
 * 1. 남쪽으로 내려감
 *      골렘 중심 기준 (+1, -1), (+2, 0), (+1, +1) 위치가 비어 있어야 이동 가능
 * 2. 1이 불가능하면 서쪽 방향으로 "회전하며" 내려감
 *      서쪽으로 이동
 *           골렘 중심 기준 (-1, -1), (0, -2), (+1, -1) 위치가 비어 있어야 이동 가능
 *           가능하면 골렘 중심 이동
 *      남쪽으로 이동(회전)
 *          서쪽으로 이동한 골렘 중심 기준 (+1, -1), (+2, 0) 위치가 비어 있어야 이동 가능
 *          출구 반시계 방향 회전: (d + 3) % 4
 * 3. 2가 불가능하면 동쪽 방향으로 "회전하며" 내려감
 *      동쪽으로 이동
 *          골렘 중심 기준 (-1, +1), (0, +2), (+1, +1) 위치가 비어 있어야 이동 가능
 *          가능하면 골렘 중심 이동
 *       남쪽으로 이동
 *          동쪽으로 이동한 골렘 중심 기준 (+2, 0), (+1 +1) 위치가 비어 있어야 이동 가능
 *          출구 시계 방향 회전: (d + 1) % 4
 * [숲이 꽉차면]
 * 이동한 최종 위치가 숲을 벗어났으면 다른 골렘들 다 없앰 그리고 다시 이동! continue;
 * [정령 이동 (bfs)]
 * 1. 현재 위치에서 자신이 이 동가능한 가장 남쪽으로 이동한 후 result 최대값으로 update
 * 2. 출구로 이동
 * 3. 출구와 인접한 모든 골렘 q에 넣기
 */
public class Main {

    static int[] dr = {-1, 0, 1, 0};//북동남서, 상우하좌
    static int[] dc = {0, 1, 0, -1};
    static int R, C, K;
    static int[][] map;
    static Space[] spaces;
    static class Space {
        int r, c, d;

        public Space(int r, int c, int d) {
            this.r = r;
            this.c = c;
            this.d = d;
        }
    }
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine().trim());
        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        map = new int[R + 3][C];
        spaces = new Space[K + 1];
        for(int i = 1; i <= K; i++) {
            st = new StringTokenizer(br.readLine().trim());
            spaces[i] = new Space(1,
                Integer.parseInt(st.nextToken()) - 1, Integer.parseInt(st.nextToken()));
        }

        int ans = 0;
        for(int i = 1; i <= K; i++) {
            //[골렘 이동]
            moveSpace(i);
            //[숲이 꽉 찬지 확인]
            if(spaces[i].r <= 3) {
                map = new int[R + 3][C + 1];
                continue;
            }

            //이동한 골렘 표시
            map[spaces[i].r][spaces[i].c] = i;
            for(int j = 0; j < 4; j++) {
                map[spaces[i].r + dr[j]][spaces[i].c + dc[j]] = i;
            }

            //[정령 이동]
            ans += moveRobot(i);
        }
        System.out.println(ans);
    }

    static void moveSpace(int index) {
        int r = spaces[index].r;
        int c = spaces[index].c;
        int d = spaces[index].d;
        while(true) {
            // 1. 남쪽으로 내려감
            if (isValid(r + 1, c - 1) && isValid(r + 2, c) && isValid(r + 1, c + 1) ) {
                r = r + 1;
                c = c;
                continue;
            }

            // 2. 1이 불가능하면 서쪽 방향으로 "회전하며" 내려감
            if (isValid(r - 1, c - 1) && isValid(r, c - 2) && isValid(r + 1, c - 1)) {
                if (isValid(r + 1, c - 1 - 1) && isValid(r + 2, c - 1)) {
                    r = r - 1;
                    c = c - 1;
                    d = (d + 3) % 4;
                    continue;
                }
            }

            // 3. 2가 불가능하면 동쪽 방향으로 "회전하며" 내려감
            if (isValid(r - 1, c + 1) && isValid(r, c + 2) && isValid(r + 1, c + 1)) {
                if (isValid(r + 2, c + 1) && isValid(r + 1, c + 1 + 1)) {
                    r = r + 1;
                    c = c + 1;
                    d = (d + 1) % 4;
                    continue;
                }
            }
            break;
        }
        spaces[index].r = r;
        spaces[index].c = c;
        spaces[index].d = d;
    }

    static boolean isValid(int r, int c) {
        return 0 <= r && r < R + 3 && 0 <= c && c < C && map[r][c] == 0;
    }

    static int moveRobot(int index) {
        int result = spaces[index].r + 1;
        boolean[] v = new boolean[K + 1];
        Queue<int[]> q = new ArrayDeque<>();
        v[index] = true;
        q.offer(new int[]{spaces[index].r + dr[spaces[index].d], spaces[index].c + dc[spaces[index].d]});
        while(!q.isEmpty()) {
            int[] now = q.poll();
            for(int i = 0; i < 4; i++) {
                int nr = now[0] + dr[i];
                int nc = now[1] + dc[i];
                if(0 <= nr && nr < R + 3 && 0 <= nc && nc < C && map[nr][nc] > 0 && !v[map[nr][nc]]) {
                    v[map[nr][nc]] = true;
                    result = Math.max(result, spaces[map[nr][nc]].r + 1);
                    q.offer(new int[]{spaces[map[nr][nc]].r + dr[spaces[map[nr][nc]].d],
                        spaces[map[nr][nc]].c + dc[spaces[map[nr][nc]].d]});
                }
            }
        }
        return result - 2;

    }

    static void print() {
        for(int i = 0; i < R + 3; i++) {
            for(int j = 0; j < C; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}