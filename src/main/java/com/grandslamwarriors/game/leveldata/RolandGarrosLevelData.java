package com.grandslamwarriors.game.leveldata;

import com.grandslamwarriors.game.tiles.TileType;

public class RolandGarrosLevelData {
    public static final TileType E = TileType.EMPTY;
    public static final TileType G = TileType.GROUND;
    public static final TileType P = TileType.PLATFORM;
    public static final TileType S = TileType.SLOW_CLAY;
    public static final TileType K = TileType.SPAWN_GOBLIN;
    public static final TileType M = TileType.SPAWN_CLAY_MONSTER;
    public static final TileType F = TileType.SPAWN_FLYING;
    public static final TileType DE = TileType.DOOR_ENTRY;
    public static final TileType DX = TileType.DOOR_EXIT;
    public static final TileType BOSS = TileType.SPAWN_BOSS;

    public static final TileType[][] MAP = new TileType[8][150];

    static {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 150; x++) MAP[y][x] = E;
        }

        // --- CHÃO COM MUITO SAIBRO LENTO ---
        for (int x = 0; x < 150; x++) {
            // Buracos reduzidos para consistência
            if (!((x > 25 && x < 29) || (x > 70 && x < 75) || (x > 120 && x < 125))) {
                MAP[7][x] = (x % 10 < 4) ? S : G;
            }
        }

        // --- COLUNAS DE SAIBRO ---
        for(int y=4; y<7; y++) MAP[y][40] = G;
        for(int y=3; y<7; y++) MAP[y][90] = G;
        for(int y=2; y<7; y++) MAP[y][140] = G;

        // --- PASSAGENS E LABIRINTO DE PLATAFORMAS ---
        // Seção "Crouch" obrigatória
        for(int x=50; x<65; x++) {
            MAP[7][x] = G;
            MAP[5][x] = G; // Teto
        }

        // Zig-zag vertical
        MAP[5][15] = P; MAP[5][16] = P;
        MAP[3][18] = P; MAP[3][19] = P;
        MAP[1][21] = P; MAP[1][22] = P;

        MAP[6][24] = DE; // Porta de entrada no túnel
        MAP[6][142] = DX; // Porta de saída após a última coluna

        // --- INIMIGOS ---
        int[] bX = {10, 30, 55, 75, 95, 115, 135};
        for(int x : bX) MAP[6][x] = K;

        int[] mX = {20, 45, 80, 105, 145};
        for(int x : mX) MAP[6][x] = M;
        // Voadores
        MAP[1][35] = F;
        MAP[2][100] = F;
        MAP[1][20] = F;
        MAP[2][65] = F;
        MAP[1][110] = F;

        // --- BOSS ---
        MAP[6][148] = BOSS;
    }
}
