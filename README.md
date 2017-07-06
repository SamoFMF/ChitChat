# ChitChat
Chat application for a school project.

# Opis komponent
Spodaj bom opisal vse komponente aplikacije

## Cascade menuji
### `Dokument`
#### Prijava
Prijavi uporabnika.
#### Odjava
Odjavi uporabnika.
#### Izhod
Zapre aplikacijo.

### `Nastavitve`
#### Barva pisave
1. `Imena` ima podmenuja `Moje ime` ter `Ostala`, kjer prvi določi barvo vašega imena in zadnji barvo imen ostalih uporabnikov.
2. `Sporočila` ima podmenuja `Moja sporočila` ter `Ostala`, kjer je ideja enaka kot pri imenih, le da velja za sporočila.
3. Barve so podrobneje opisane spodaj.
#### Velikost pisave
Nastavimo velikost pisave, ki bo veljala za sporočila, ki bodo nastala od časa nastavitve naprej. Enako velja za barvo pisave.
#### Barva ozadja
Omogoči izbiro barve ozadja polja, kamor so izpisana sporočila ter polja, kjer so prikazani dosegljivi uporabniki.
#### Ustvari barvo
Ustvari poljubno barvo s sliderji, ki predstavljajo rdečo, zeleno in modro barvo (RGB). Da se barva uporabi jo je potrebno najprej potrditi in nato izbrati možnost `Poljubna` za željen del teksta.

### `Robot`
#### Zaženi
Zažene robota `Odmev`, ki je odmev izbranega uporabnika in je natančneje opisan spodaj.
#### Nastavi zamik
Nastavi zamik za robota. Ta zamik bo uporabljen za naslednjega robota, ki ga bomo ustvarili. Na že obstoječe ne vpliva.
#### Navodila
Vsebuje kratka navodila in osnovne informacije o robotu.
#### Ustavi
Ustavi vse robote, ki so odmev izbranega uporabnika.

## Zgornja vrstica
### Polje za vzdevek
Sem vpišemo željen vzdevek. Če je polje ob vpisu prazno, se vpišemo s sistemskim uporabniškim imenom.
### `Prijavi!`
Prijavi uporabnika.
### `Odjavi!`
Odjavi uporabnika.

## Preostalo
Na levi se nam izpisujejo sporočila, medtem ko polje na desni prikazuje trenutno dosegljive uporabnike. Če uporabnik ni poslal sporočila že vsaj 5 minut, bo zraven njegovega imena prikazan napis `(Away)`. POZOR: To deluje le lokalno, ker aplikacija konstantno zahteva podatke o novih sporočilih, kar pa posodablja uporabnikovo vrednost `last_active` na strežniku.

#### Polje za izbiro naslovnika
Desno spodaj imamo polje, kamor lahko vnesemo naslovnika sporočila. Če je polje prazno, je sporočilo poslano vsem. To okno se uporablja tudi za ustvarjanje robota `Odmev`.

## Robot `Odmev`
Je objekt, ki sprejme ime uporabnika, ki ga bo imitiral ter zamik s katerim bo ponavljal sporočila. Ime uporabnika določimo preko okna za naslovnika sporočil (uporabi izbrano vrednost v trenutku, ko je ustvarjen). Če nimamo izbranega naslovnika, bo ponavljal naša sporočila. Vsaka oseba ima lahko več `Odmev`-ov, pri čemer mora biti razlika v njihovih zamikih vsaj pol sekunde.
Z gumbom `Ustavi` pobrišemo vse robote, ki so bili odmev izbranega naslovnika. Če se odjavimo, ali aplikacijo zapremo, se vsi roboti uničijo.

## Barve
Na voljo imamo različne barve za pisavo itd., kjer možnost `Poljubna` izbere trenutno potrjeno barvo iz menuja `Nastavitve\Ustvari barvo`. Možnost `Naključna` pa naključno zgenerira 3 vrednosti med 0 in 255, ki predstavljajo vrednosti RGB.
