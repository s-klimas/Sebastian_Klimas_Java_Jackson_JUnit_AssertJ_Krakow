# Payment Calculator
Aplikacja oblicza optymalny sposób zapłaty za zamówienia w sklepie internetowym, wykorzystując dostępne metody płatności (w tym punkty lojalnościowe) i przypisane do nich rabaty. Celem jest maksymalizacja łącznego rabatu i pełne opłacenie każdego zamówienia.

## Stack technologiczny
 - Java - język w jakim aplikacja jest napisana
 - Jackson - biblioteka, która paruje JSONy na obiekty w aplikacji
 - JUnit5 - biblioteka do testowania
 - AssertJ - biblioteka z dodatkowymi asercjami

## Zasady działania
Algorytm dobiera dla każdego zamówienia najlepszą kombinację metod płatności tak, aby:
 - uzyskać największy możliwy rabat,
 - w pełni opłacić zamówienie,
 - wykorzystać punkty lojalnościowe w pierwszej kolejności, o ile nie prowadzi to do utraty większego rabatu kartowego.

## Zastosowanie
 - Obsługa do 10 000 zamówień i 1000 metod płatności.
 - Uwzględnienie wszystkich zasad przyznawania rabatów:
    - Rabat bankowy tylko przy pełnej płatności kartą danego banku.
    - 10% rabatu za min. 10% wartości zamówienia opłacone punktami.
    - Rabat za pełną płatność punktami (preferowany nad częściowym).
 - Generowanie podsumowania wydatków wg metod płatności.

## Dane wejściowe
Jako dane wejściowe przyjmuje ścieżki do plików: 
  - orders.json - lista zamówień z możliwymi promocjami
  - paymentmethods.json - lista dostępnych metod płatności z rabatami i limitem

## Testowanie
Projekt zawiera testy jednostkowe kluczowych elementów logiki.
