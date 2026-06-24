package com.grandslamwarriors.game.leveldata;

import com.grandslamwarriors.game.tiles.TileType;

public class USOpenLevelData {
    public static final TileType E = TileType.EMPTY;
    public static final TileType G = TileType.GROUND;
    public static final TileType P = TileType.PLATFORM;
    public static final TileType GAS = TileType.GAS;
    public static final TileType S = TileType.SPAWN_RAT;
    public static final TileType K = TileType.SPAWN_SKYSCRAPER;
    public static final TileType F = TileType.SPAWN_FLYING;
    public static final TileType DE = TileType.DOOR_ENTRY;
    public static final TileType DX = TileType.DOOR_EXIT;
    public static final TileType B = TileType.SPAWN_BOSS;

    public static final TileType[][] MAP = new TileType[8][150];

    static {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 150; x++) MAP[y][x] = E;
        }

        // --- CHÃO DE CIDADE (US OPEN) ---
        for (int x = 0; x < 150; x++) {
            if (x % 25 < 5) continue; // Buracos regulares simulando vãos entre prédios
            MAP[7][x] = G;
        }

        // --- COLUNAS (ARRANHA-CÉUS) ---
        int[] skyX = {40, 80, 120};
        for(int x : skyX) {
            for(int y=1; y<7; y++) MAP[y][x] = G;
            MAP[1][x-1] = G; MAP[1][x+1] = G; // Topo mais largo
        }

        // --- TÚNEIS DE METRÔ (CROUCH) ---
        for(int x=15; x<35; x++) {
            MAP[7][x] = G;
            MAP[5][x] = G; // Teto baixo
        }

        // Área com gás perigoso no túnel
        MAP[6][25] = GAS;

        // --- PLATAFORMAS FLUTUANTES (MARIO STYLE) ---
        for(int x=5; x<145; x+=15) {
            int y = (x % 30 == 0) ? 2 : 4;
            MAP[y][x] = P; MAP[y][x+1] = P; MAP[y][x+2] = P;
        }

        // --- INIMIGOS ---
        for(int x=10; x<140; x+=15) MAP[6][x] = K;
        for(int x=20; x<140; x+=25) MAP[3][x] = S; // Gás voador

        // Inimigos voadores do tipo 3 (Enemy3)
        MAP[1][30] = F;
        MAP[2][85] = F;
        MAP[1][130] = F;

        MAP[6][80] = DE; // Porta no túnel de metrô
        MAP[6][140] = DX; // Saída no final do nível

        // --- BOSS ---
        MAP[6][148] = B;
    }
}
