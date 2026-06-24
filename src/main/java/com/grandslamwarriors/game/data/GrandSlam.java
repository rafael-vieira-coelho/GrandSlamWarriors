package com.grandslamwarriors.game.data;

import com.grandslamwarriors.game.screens.TouristPoint;

public enum GrandSlam {
    AUSTRALIAN_OPEN(1, "Australian Open", "backgrounds/aus_bg.png", TouristPoint.SYDNEY_OPERA_HOUSE,
        new String[]{"kangaroo"}, "fire_rain"),
    ROLAND_GARROS(2, "Roland Garros", "backgrounds/rg_bg.png", TouristPoint.EIFFEL_TOWER,
        new String[]{"clay_monster", "goblin"}, "clay_slow"),
    WIMBLEDON(3, "Wimbledon", "backgrounds/wim_bg.png", TouristPoint.BUCKINGHAM_PALACE,
        new String[]{"grass_cutter"}, "slippery"),
    US_OPEN(4, "US Open", "backgrounds/us_bg.png", TouristPoint.STATUE_OF_LIBERTY,
        new String[]{"sewer_gas"}, "gas");

    public final int ordem;
    public final String nome;
    public final String bgPath;
    public final TouristPoint pontoTuristico;
    public final String[] inimigos;
    public final String mecanica;

    GrandSlam(int ordem, String nome, String bgPath, TouristPoint ponto, String[] inimigos, String mecanica) {
        this.ordem = ordem;
        this.nome = nome;
        this.bgPath = bgPath;
        this.pontoTuristico = ponto;
        this.inimigos = inimigos;
        this.mecanica = mecanica;
    }

    public GrandSlam getNext() {
        GrandSlam[] values = GrandSlam.values();
        int nextIndex = this.ordinal() + 1;
        if (nextIndex < values.length) {
            return values[nextIndex];
        }
        return null; // Fim da jornada
    }
}
