package com.grandslamwarriors.game.leveldata;

import com.grandslamwarriors.game.tiles.TileType;

public class WimbledonLevelData {
    public static final TileType E = TileType.EMPTY;
    public static final TileType G = TileType.GROUND;
    public static final TileType P = TileType.PLATFORM;
    public static final TileType S = TileType.SLIPPERY_GRASS;
    public static final TileType Q = TileType.SPAWN_QUEEN_GUARD;
    public static final TileType C = TileType.SPAWN_GRASS_CUTTER;
    public static final TileType F = TileType.SPAWN_FLYING;
    public static final TileType DE = TileType.DOOR_ENTRY;
    public static final TileType DX = TileType.DOOR_EXIT;
    public static final TileType B = TileType.SPAWN_BOSS;

    public static final TileType[][] MAP = new TileType[8][150];

    static {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 150; x++) MAP[y][x] = E;
        }

        // --- GRAMA ESCORREGADIA ---
        for (int x = 0; x < 150; x++) {
            if (!((x > 30 && x < 35) || (x > 75 && x < 80) || (x > 110 && x < 115))) {
                MAP[7][x] = (x % 15 < 6) ? S : G;
            }
        }

        // --- ESTRUTURAS DE JARDIM REALEZA ---
        for(int x=20; x<140; x+=30) {
            for(int y=4; y<7; y++) MAP[y][x] = G;
            MAP[4][x-1] = G; MAP[4][x+1] = G; // Topo decorado
        }

        // --- PLATAFORMAS FLUTUANTES (WIMBLEDON) ---
        for(int x=10; x<140; x+=12) {
            int y = (x % 24 == 0) ? 2 : 4;
            MAP[y][x] = P; MAP[y][x+1] = P;
        }

        MAP[6][50] = DE; // Porta escondida atrás de uma coluna
        MAP[6][130] = DX; // Saída no final do jardim

        // --- INIMIGOS ---
        for(int x=15; x<140; x+=20) MAP[6][x] = Q;
        for(int x=25; x<140; x+=25) MAP[6][x] = C;

        // Inimigos voadores (Novos)
        MAP[1][40] = F;
        MAP[2][90] = F;
        MAP[1][135] = F;

        // --- BOSS ---
        MAP[6][145] = B;
    }
}
