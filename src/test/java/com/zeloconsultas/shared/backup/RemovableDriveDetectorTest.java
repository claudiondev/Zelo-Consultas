package com.zeloconsultas.shared.backup;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// A descricao do tipo de unidade que o Windows devolve muda de palavra
// dependendo da versao/idioma: "Disco removivel"/"Removable Disk" em
// versoes mais antigas, "Unidade de USB"/"USB Drive" em builds mais novos
// do Windows 11 (foi o caso real encontrado testando num pendrive de
// verdade — a deteccao so reconhecia a palavra antiga e nao achava nada).
class RemovableDriveDetectorTest {

    @Test
    void reconheceDescricaoAntigaDeDiscoRemovivel() {
        assertThat(RemovableDriveDetector.ehRemovivel("Disco removivel")).isTrue();
        assertThat(RemovableDriveDetector.ehRemovivel("Removable Disk")).isTrue();
    }

    @Test
    void reconheceDescricaoNovaDeUnidadeUsb() {
        assertThat(RemovableDriveDetector.ehRemovivel("Unidade de USB")).isTrue();
        assertThat(RemovableDriveDetector.ehRemovivel("USB Drive")).isTrue();
    }

    @Test
    void ignoraDiscoLocalFixo() {
        assertThat(RemovableDriveDetector.ehRemovivel("Disco Local")).isFalse();
        assertThat(RemovableDriveDetector.ehRemovivel("Local Disk")).isFalse();
    }

    @Test
    void ignoraDescricaoNula() {
        assertThat(RemovableDriveDetector.ehRemovivel(null)).isFalse();
    }
}
