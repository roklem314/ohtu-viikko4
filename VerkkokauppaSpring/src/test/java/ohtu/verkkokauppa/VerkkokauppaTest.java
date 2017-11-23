package ohtu.verkkokauppa;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class VerkkokauppaTest {

//    Kauppa kauppa;
//    Kirjanpito kirjanpito;
//    Ostoskori ostosk;
//    Pankki bank;
//    Varasto varasto;
//    ViitegeneraattoriInt viitegeneraattori;
    @Before
    public void setUp() {

    }

    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaan() {
        // luodaan ensin mock-oliot
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        // määritellään että viitegeneraattori palauttaa viitten 42
        when(viite.uusi()).thenReturn(42);

        Varasto varasto = mock(Varasto.class);
        // määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        // sitten testattava kauppa 
        Kauppa k = new Kauppa(varasto, pankki, viite);

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu
        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("12345"), eq("33333-44455"), eq(5));

    }

    @Test
    public void ostoksenPaatyttyaPankinMetodiTilisiiroKutsutaanOikealleAsiakkaallaTilinumerollaJaSummalla() {
        // luodaan ensin mock-oliot
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(43);

        Varasto varasto = mock(Varasto.class);

        when(varasto.saldo(1)).thenReturn(12);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.saldo(2)).thenReturn(11);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "mehu", 6));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();

        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("pekka2", "123456");

        verify(pankki).tilisiirto(eq("pekka2"), eq(43), eq("123456"), eq("33333-44455"), eq(11));

    }

    @Test
    public void ostoksenPaatyttyaPankinMetodiTilisiiroKutsutaanOikealleAsiakkaallaTilinumerollaJaSummalla2() {
        // luodaan ensin mock-oliot
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(44);

        Varasto varasto = mock(Varasto.class);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();

        k.lisaaKoriin(1);
        k.lisaaKoriin(1);
        k.tilimaksu("pekka2", "1234567");

        verify(pankki).tilisiirto(eq("pekka2"), eq(44), eq("1234567"), eq("33333-44455"), eq(10));
    }

    @Test
    public void ostoksenPaatyttyaPankinMetodiTilisiiroKutsutaanOikealleAsiakkaallaTilinumerollaJaSummalla3() {
        // luodaan ensin mock-oliot
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(44);

        Varasto varasto = mock(Varasto.class);

        when(varasto.saldo(0)).thenReturn(12);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();

        k.lisaaKoriin(1);
        k.tilimaksu("pekka2", "1234567");

        verify(pankki).tilisiirto(eq("pekka2"), eq(44), eq("1234567"), eq("33333-44455"), eq(0));
    }

    @Test
    public void metodiAloitaAsiointitoimiiOikeinKutsuttaessaUudelleen() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(44);

        Varasto varasto = mock(Varasto.class);

        when(varasto.saldo(1)).thenReturn(11);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka2", "1234567");

        verify(pankki).tilisiirto(eq("pekka2"), eq(44), eq("1234567"), eq("33333-44455"), eq(5));

        when(viite.uusi()).thenReturn(46);
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka3", "12345678");

        verify(pankki).tilisiirto(eq("pekka3"), eq(46), eq("12345678"), eq("33333-44455"), eq(5));

    }

    @Test
    public void kauppaPyytaaUuudenViitteenUudelleMaksutapahtumalle() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(44);

        Varasto varasto = mock(Varasto.class);

        when(varasto.saldo(1)).thenReturn(11);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka2", "1234567");

        verify(viite, times(1)).uusi();

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka2", "1234567");

        verify(viite, times(2)).uusi();

    }
    @Test
    public void ostosPoistetaanKoristaTuotteenSaldoPaivittyy() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(44);

        Varasto varasto = mock(Varasto.class);

        when(varasto.saldo(1)).thenReturn(11);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.poistaKorista(1);


        verify(varasto, times(1)).palautaVarastoon(any());

    }

}
