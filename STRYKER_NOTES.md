# Stryker-Update

## Was geändert wurde

- Die Stryker-Mutation von `Files.exists(...)` zu dem nicht kompilierbaren `Files.forall(...)` wird vermieden. Die Persistenzklassen prüfen Verzeichnisse mit `Files.isDirectory(...)` und XML-Dateien mit `Files.isRegularFile(...)`.
- Die bisher nicht ausgeführten Specs wurden repariert: `TurnState` wurde korrekt importiert. Damit werden jetzt auch `ControllerV2Spec`, `MementoSpec` und `AppModuleSpec` entdeckt.
- Es gibt zusätzliche Tests für Controller-Fehlerpfade, Siegzustände, rote/gelbe Kartenregeln, leere Spielzustände sowie JSON-/XML-Roundtrips.
- Die Persistenz wurde dabei fachlich korrigiert: JSON speichert ohne nicht lesbaren Wrapper, XML lädt `.xml`-Dateien, gespeicherte Karten erhalten beim Laden wieder ihre Besitzer-ID und gelöschte Mementos melden den echten Pfad an den Undo-Manager.

## Mutationsumfang

Stryker mutiert die testbare Fachlogik (`AppModule`, `controller`, `model`). Die grafische/interaktive View-Schicht und der Startpunkt werden bewusst nicht mutiert: Sie benötigen GUI-/Konsolen-Integrationstests und enthalten keine Kernspielregeln.

## Ausführen

```bash
sbt clean test
sbt stryker
open target/stryker4s-report/index.html
```

Für dieses Projekt ist JDK 21 empfehlenswert (entspricht dem Dockerfile). Die Warnungen von Java 24 aus der ursprünglichen Ausgabe sind nicht der Stryker-Abbruch; der Abbruch kam vom nicht kompilierbaren `Files.forall`-Mutanten.
