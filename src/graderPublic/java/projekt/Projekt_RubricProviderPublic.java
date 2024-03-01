package projekt;

import org.sourcegrade.jagr.api.rubric.*;
import org.tudalgo.algoutils.tutor.general.jagr.RubricUtils;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import projekt.controller.GameControllerTest;
import projekt.controller.PlayerControllerTest;
import projekt.model.EdgeImplTest;
import projekt.model.HexGridImplTest;
import projekt.model.IntersectionImplTest;
import projekt.model.PlayerImplTest;

public class Projekt_RubricProviderPublic implements RubricProvider {

    @Override
    public Rubric getRubric() {
        return Rubric.builder()
            .title("Projekt | Die Siedler von Catan")
            .addChildCriteria(
                Criterion.builder()
                    .shortDescription("H1 | Implementierung des Modells")
                    .addChildCriteria(
                        Criterion.builder()
                            .shortDescription("H1.1 | Inventarsystem des Players")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Die Methoden getResources, addResource und addResources funktionieren wie beschrieben")
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.and(
                                            JUnitTestRef.ofMethod(() -> PlayerImplTest.InventorySystem.class.getDeclaredMethod("testGetResources")),
                                            JUnitTestRef.ofMethod(() -> PlayerImplTest.InventorySystem.class.getDeclaredMethod("testAddResource", JsonParameterSet.class)),
                                            JUnitTestRef.ofMethod(() -> PlayerImplTest.InventorySystem.class.getDeclaredMethod("testAddResources", JsonParameterSet.class))
                                        ))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode hasResource funktioniert wie beschrieben")
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> PlayerImplTest.InventorySystem.class.getDeclaredMethod("testHasResources", JsonParameterSet.class)))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Die Methoden removeResource und removeResources funktionieren wie beschrieben")
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.and(
                                            JUnitTestRef.ofMethod(() -> PlayerImplTest.InventorySystem.class.getDeclaredMethod("testRemoveResource", JsonParameterSet.class)),
                                            JUnitTestRef.ofMethod(() -> PlayerImplTest.InventorySystem.class.getDeclaredMethod("testRemoveResources", JsonParameterSet.class))
                                        ))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode getTradeRatio funktioniert wie beschrieben")
                                    .minPoints(0)
                                    .maxPoints(3)
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> PlayerImplTest.class.getDeclaredMethod("testGetTradeRatio")))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build())
                            .build(),
                        Criterion.builder()
                            .shortDescription("H1.2 | Entwicklungskarten des Players")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Die Methoden getDevelopmentCards, addDevelopmentCard und removeDevelopmentCard funktionieren wie beschrieben")
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.and(
                                            JUnitTestRef.ofMethod(() -> PlayerImplTest.DevelopmentCards.class.getDeclaredMethod("testGetDevelopmentCards", JsonParameterSet.class)),
                                            JUnitTestRef.ofMethod(() -> PlayerImplTest.DevelopmentCards.class.getDeclaredMethod("testAddDevelopmentCard", JsonParameterSet.class)),
                                            JUnitTestRef.ofMethod(() -> PlayerImplTest.DevelopmentCards.class.getDeclaredMethod("testRemoveDevelopmentCard", JsonParameterSet.class))
                                        ))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Die Methoden getTotalDevelopmentCards und getKnightsPlayed funktionieren wie beschrieben")
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.and(
                                            JUnitTestRef.ofMethod(() -> PlayerImplTest.DevelopmentCards.class.getDeclaredMethod("testGetTotalDevelopmentCards", JsonParameterSet.class)),
                                            JUnitTestRef.or(
                                                JUnitTestRef.ofMethod(() -> PlayerImplTest.DevelopmentCards.class.getDeclaredMethod("testGetKnightsPlayed_viaField", JsonParameterSet.class)),
                                                JUnitTestRef.ofMethod(() -> PlayerImplTest.DevelopmentCards.class.getDeclaredMethod("testGetKnightsPlayed_viaMethod", JsonParameterSet.class))
                                            )
                                        ))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build())
                            .build(),
                        Criterion.builder()
                            .shortDescription("H1.3 | Alle Wege führen nach...")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Methode getRoads funktioniert wie beschrieben")
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> HexGridImplTest.class.getDeclaredMethod("testGetRoads", JsonParameterSet.class)))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode addRoad funktioniert wie beschrieben")
                                    .minPoints(0)
                                    .maxPoints(3)
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> HexGridImplTest.class.getDeclaredMethod("testAddRoad")))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode getIntersections funktioniert wie beschrieben")
                                    .minPoints(0)
                                    .maxPoints(2)
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> EdgeImplTest.class.getDeclaredMethod("testGetIntersections", JsonParameterSet.class)))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode connectsTo funktioniert wie beschrieben")
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> EdgeImplTest.class.getDeclaredMethod("testConnectsTo", JsonParameterSet.class)))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode getConnectedRoads funktioniert wie beschrieben")
                                    .minPoints(0)
                                    .maxPoints(2)
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> EdgeImplTest.class.getDeclaredMethod("testGetConnectedRoads", JsonParameterSet.class)))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build())
                            .build(),
                        Criterion.builder()
                            .shortDescription("H1.4 | Alle Wege führen nach...")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Methode placeVillage funktioniert wie beschrieben")
                                    .minPoints(0)
                                    .maxPoints(2)
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.and(
                                            JUnitTestRef.ofMethod(() -> IntersectionImplTest.class.getDeclaredMethod("testPlaceVillage_noRoadCheck", JsonParameterSet.class)),
                                            JUnitTestRef.ofMethod(() -> IntersectionImplTest.class.getDeclaredMethod("testPlaceVillage_roadCheck", JsonParameterSet.class))
                                        ))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode upgradeSettlement funktioniert wie beschrieben")
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> IntersectionImplTest.class.getDeclaredMethod("testUpgradeSettlement", JsonParameterSet.class)))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build())
                            .build())
                    .build(),
                Criterion.builder()
                    .shortDescription("H2 | Implementierung des Controllers")
                    .addChildCriteria(
                        Criterion.builder()
                            .shortDescription("H2.1 | Ready. Set. Go.")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Methode firstRound funktioniert wie beschrieben")
                                    .minPoints(0)
                                    .maxPoints(2)
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> GameControllerTest.class.getDeclaredMethod("testFirstRound")))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Method regularTurn funktioniert wie beschrieben")
                                    .maxPoints(0)
                                    .maxPoints(2)
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> GameControllerTest.class.getDeclaredMethod("testRegularTurn")))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode diceRollSeven funktioniert wie beschrieben")
                                    .minPoints(0)
                                    .maxPoints(2)
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> GameControllerTest.class.getDeclaredMethod("testDiceRollSeven")))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build())
                            .build(),
                        Criterion.builder()
                            .shortDescription("H2.2 | Die Würfel sind gefallen... wer bekommt nun Rohstoffe?")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Methode distributeResources funktioniert wie beschrieben")
                                    .minPoints(0)
                                    .maxPoints(3)
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> GameControllerTest.class.getDeclaredMethod("testDistributeResources")))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build())
                            .build(),
                        Criterion.builder()
                            .shortDescription("H2.3 | Jetzt wird gehandelt...")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Methode acceptTradeOffer funktioniert wie beschrieben")
                                    .maxPoints(0)
                                    .maxPoints(3)
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> PlayerControllerTest.class.getDeclaredMethod("testAcceptTradeOffer", JsonParameterSet.class)))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode tradeWithBank funktioniert wie beschrieben")
                                    .maxPoints(0)
                                    .maxPoints(3)
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> PlayerControllerTest.class.getDeclaredMethod("testTradeWithBank", JsonParameterSet.class)))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode offerTrade funktioniert wie beschrieben")
                                    .maxPoints(0)
                                    .maxPoints(3)
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> GameControllerTest.class.getDeclaredMethod("testOfferTrade", JsonParameterSet.class)))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build())
                            .build(),
                        Criterion.builder()
                            .shortDescription("H2.4 | Baumeisterische Eskapaden: Errichte Dörfer und Straßen")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Methode canBuildVillage funktioniert wie beschrieben")
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> PlayerControllerTest.class.getDeclaredMethod("testCanBuildVillage", JsonParameterSet.class)))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode canBuildRoad funktioniert wie beschrieben")
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> PlayerControllerTest.class.getDeclaredMethod("testCanBuildRoad", JsonParameterSet.class)))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode buildVillage funktioniert wie beschrieben")
                                    .minPoints(0)
                                    .maxPoints(2)
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> PlayerControllerTest.class.getDeclaredMethod("testBuildVillage", JsonParameterSet.class)))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode buildRoad funktioniert wie beschrieben")
                                    .minPoints(0)
                                    .maxPoints(2)
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> PlayerControllerTest.class.getDeclaredMethod("testBuildRoad", JsonParameterSet.class)))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build())
                            .build(),
                        Criterion.builder()
                            .shortDescription("H2.5 | Urbanisierung: Vom beschaulichen Dorf zur aufstrebenden Stadt")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Methode upgradeVillage funktioniert wie beschrieben")
                                    .minPoints(0)
                                    .maxPoints(2)
                                    .grader(Grader.testAwareBuilder()
                                        .requirePass(JUnitTestRef.ofMethod(() -> PlayerControllerTest.class.getDeclaredMethod("testUpgradeVillage", JsonParameterSet.class)))
                                        .pointsFailedMin()
                                        .pointsPassedMax()
                                        .build())
                                    .build())
                            .build())
                    .build(),
                Criterion.builder()
                    .shortDescription("H3 | Implementierung der View")
                    .addChildCriteria(
                        Criterion.builder()
                            .shortDescription("H3.1 | Was darf wo gebaut werden?")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Methode updateBuildVillageButtonState funktioniert wie beschrieben")
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode updateUpgradeVillageButtonState funktioniert wie beschrieben")
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode updateBuildRoadButtonState funktioniert wie beschrieben")
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode buildVillageButtonAction funktioniert wie beschrieben")
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode upgradeVillageButtonAction funktioniert wie beschrieben")
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode buildRoadButtonAction funktioniert wie beschrieben")
                                    .build())
                            .build(),
                        Criterion.builder()
                            .shortDescription("H3.2 | Wann darf ich was machen?")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Methode updateUIBasedOnObjective funktioniert wie beschrieben")
                                    .minPoints(0)
                                    .maxPoints(2)
                                    .build())
                            .build(),
                        Criterion.builder()
                            .shortDescription("H3.3 | Spiel erstellen")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Methode removePlayer funktioniert wie beschrieben")
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode createAddPlayerButton funktioniert wie beschrieben")
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode createRemovePlayerButton funktioniert wie beschrieben")
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode createPlayerColorPicker funktioniert wie beschrieben")
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode createBotOrPlayerSelector funktioniert wie beschrieben")
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Methode initCenter funktioniert wie beschrieben")
                                    .build())
                            .build(),
                        Criterion.builder()
                            .shortDescription("H3.4 | Karten auswählen")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Für jede wählbare Ressource gibt es eine Eingabemöglichkeit")
                                    .minPoints(0)
                                    .maxPoints(4)
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Jede wählbare Ressource wird dargestellt")
                                    .minPoints(0)
                                    .maxPoints(4)
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Der Dialog kann erst geschlossen werden, wenn die korrekte Menge an Ressourcen ausgewählt wurde")
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Der Dialog gibt die ausgewählten Ressourcen korrekt zurück, wenn er geschlossen wird")
                                    .build())
                            .build())
                    .build(),
                Criterion.builder()
                    .shortDescription("H4 | Weiterführende Aufgaben")
                    .addChildCriteria(
                        Criterion.builder()
                            .shortDescription("H4.1 | Weiterentwicklung des GUIs")
                            .minPoints(0)
                            .maxPoints(5)
                            .grader(RubricUtils.manualGrader(5))
                            .build(),
                        Criterion.builder()
                            .shortDescription("H4.2 | Neuer Rohstoff")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Der neue Rohstoff wurde in das ResourceType Enum hinzugefügt")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Es wurde ein neuer Tile.Type hinzugefügt, welches den neuen Rohstoff produziert")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Der neu hinzugefügt Tile-Typ erscheint auf dem Spielfeld (HexGrid)")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Es gibt einen Hafen, der einen günstigen Handel mit dem neuen Rohstoff ermöglicht")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Der neue Rohstoff hat ein Icon und eine Farbe")
                                    .grader(RubricUtils.manualGrader())
                                    .build())
                            .build(),
                        Criterion.builder()
                            .shortDescription("H4.3 | Neue Gebäudestruktur")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Die neue Gebäudestruktur wurde dem Settlement.Type Enum hinzugefügt")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Die Kosten wurden in Config.SETTLEMENT_BUILDING_COST hinzugefügt")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Im PlayerController wurden mindestens die Methoden zum Bau und zum Überprüfen, ob gebaut werden kann, implementiert")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Die neue Gebäudestruktur hat ein Icon")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Die neue Gebäudestruktur bereichert das Spiel und ist nicht zu stark oder zu schwach")
                                    .grader(RubricUtils.manualGrader())
                                    .build())
                            .build(),
                        Criterion.builder()
                            .shortDescription("H4.4 | Neue Entwicklungskarte")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Die neue Entwicklungskarte wurde dem DevelopmentCardType Enum hinzugefügt")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Die Kosten für den Kauf der neuen Entwicklungskarte wurden in Config.DEVELOPMENT_CARD_COST hinzugefügt")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Die neue Entwicklungskarte hat ein Icon")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Die neue Entwicklungskarte bereichert das Spiel und ist nicht zu stark oder zu schwach")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Je nach Komplexität der Entwicklungskarte kann hier noch ein weiterer Punkt vergeben werden")
                                    .grader(RubricUtils.manualGrader())
                                    .build())
                            .build(),
                        Criterion.builder()
                            .shortDescription("H4.5 | Neue Spielmechanik")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Die neue Spielmechanik wurde in das Spiel integriert und ist verständlich dokumentiert")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Die neue Spielmechanik bereichert das Spiel und ist nicht zu stark oder zu schwach")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Je nach Komplexität der Mechanik können hier noch bis zu drei weitere Punkte vergeben werden")
                                    .minPoints(0)
                                    .maxPoints(3)
                                    .grader(RubricUtils.manualGrader(3))
                                    .build())
                            .build(),
                        Criterion.builder()
                            .shortDescription("H4.6 | AI als Gegner?")
                            .addChildCriteria(
                                Criterion.builder()
                                    .shortDescription("Die Strategie des Computergegners ist gut dokumentiert und sinnvoll")
                                    .minPoints(0)
                                    .maxPoints(2)
                                    .grader(RubricUtils.manualGrader(2))
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Der Computergegner ist implementiert und kann über das Menü ausgewählt werden")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Der Computergegner führt nur erlaubte Aktionen aus")
                                    .grader(RubricUtils.manualGrader())
                                    .build(),
                                Criterion.builder()
                                    .shortDescription("Wenn man zwei Computergegner gegeneinander spielen lässt, gewinnt einer der beiden")
                                    .grader(RubricUtils.manualGrader())
                                    .build())
                            .build())
                    .build(),
                Criterion.builder()
                    .shortDescription("Misc")
                    .minPoints(0)
                    .maxPoints(0)
                    .addChildCriteria(Criterion.builder()
                        .shortDescription("Alle Klassen-, Methoden- und Attribut-Signaturen sind unverändert")
                        .minPoints(-1)
                        .maxPoints(1)
                        .grader(Grader.testAwareBuilder()
                            .requirePass(JUnitTestRef.ofMethod(() -> SanityCheck.class.getDeclaredMethod("test", JsonParameterSet.class)))
                            .pointsFailedMin()
                            .pointsPassedMax()
                            .build())
                        .build())
                    .build())
            .build();
    }
}
