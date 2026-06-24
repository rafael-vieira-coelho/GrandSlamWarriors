package com.grandslamwarriors.game.leveldata;

import com.grandslamwarriors.game.tiles.TileType;

public class AustralianLevelData {
    public static final TileType E = TileType.EMPTY;
    public static final TileType G = TileType.GROUND;
    public static final TileType P = TileType.PLATFORM;
    public static final TileType K = TileType.SPAWN_KANGAROO;
    public static final TileType C = TileType.SPAWN_CROCODILE;
    public static final TileType F = TileType.SPAWN_FLYING;
    public static final TileType DE = TileType.DOOR_ENTRY;
    public static final TileType DX = TileType.DOOR_EXIT;
    public static final TileType B = TileType.SPAWN_BOSS;

    public static final TileType[][] MAP = new TileType[8][150];

    static {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 150; x++) MAP[y][x] = E;
        }

        // --- CHÃO E LACUNAS (Reduzidas para viabilidade) ---
        for (int x = 0; x < 150; x++) {
            // Buracos reduzidos de 7-10 blocos para no máximo 4-5 blocos
            if (!((x > 15 && x < 19) || (x > 45 && x < 50) || (x > 80 && x < 85) || (x > 115 && x < 120))) {
                MAP[7][x] = G;
            }
        }

        // --- ESTRUTURAS VERTICAIS E MAIS BLOCOS AÉREOS ---
        // Coluna 1
        for(int y=4; y<7; y++) MAP[y][25] = G;
        MAP[4][24] = G; MAP[4][26] = G;

        // Coluna 2 (Ultra Alta - Row 1)
        for(int y=1; y<7; y++) MAP[y][60] = G;
        MAP[1][59] = G; MAP[1][61] = G;

        // Coluna 3
        for(int y=3; y<7; y++) MAP[y][100] = G;
        MAP[3][99] = G; MAP[3][101] = G;

        // --- CAMINHO DE PLATAFORMAS NAS ALTURAS (Rows 1, 2, 3) ---
        for(int x=50; x<140; x+=10) {
            int y = (x/10) % 3 + 1; // Alterna entre Row 1, 2 e 3
            MAP[y][x] = P; MAP[y][x+1] = P; MAP[y][x+2] = P;

            // Adiciona um bloco intermediário para ajudar a subir do chão
            MAP[y+2][x-4] = P;
        }

        // --- PASSAGENS SECRETAS / TÚNEIS ---
        for(int x=70; x<78; x++) {
            MAP[7][x] = G; // Chão sólido na Row 7
            MAP[5][x] = G; // Teto na Row 5 (Espaço na Row 6 para andar/agachar)
        }
        // Limpa qualquer bloco que possa estar no caminho da porta
        MAP[6][74] = DE;
        MAP[6][139] = DX;
        // Remove blocos acima/abaixo imediatos se existirem (garantir que DE/DX fiquem em TileType.EMPTY)
        // No AustralianLevelData as coordenadas são fixas, então DE/DX substituirão o que houver lá.

        for(int x=130; x<140; x++) {
            MAP[5][x] = G;
            MAP[7][x] = G;
        }

        // --- REFORÇO DE BLOCOS AÉREOS GERAIS ---
        int[] extraX = {8, 20, 42, 65, 88, 108, 125, 142};
        for(int x : extraX) {
            MAP[5][x] = P; MAP[5][x+1] = P;
            MAP[3][x+5] = P; MAP[3][x+6] = P;
        }

        // --- INIMIGOS ---
        int[] kX = {12, 30, 50, 75, 95, 110, 135};
        for(int x : kX) MAP[6][x] = K;

        int[] cX = {20, 42, 65, 88, 120, 145};
        for(int x : cX) MAP[6][x] = C;

        // Inimigo desafiador no topo
        MAP[0][60] = K;
        // Inimigos voadores (Novos)
        MAP[1][30] = F;
        MAP[2][75] = F;
        MAP[1][120] = F;

        // --- BOSS ---
        MAP[6][145] = B;
    }
}
