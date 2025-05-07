package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.application.Platform;
import java.util.Arrays;
import java.util.List;

public class SecondaryController {
    @FXML
    private Button btn00, btn01, btn02, btn10, btn11, btn12, btn20, btn21, btn22;
    @FXML
    private Label textLabel;

    private SimpleClient client;
    int currentTurn;

    public void initialize() {
        client = SimpleClient.getClient();
        currentTurn = client.getCurrentTurn();
        EventBus.getDefault().register(this);

        List<Button> allButtons = Arrays.asList(btn00, btn01, btn02, btn10, btn11, btn12, btn20, btn21, btn22);
        for (Button b : allButtons) {
            removeButtonGlow(b);
        }

        System.out.println("Current Player Number: " + client.getPlayerNumber() + " secondaryController");
    }



    @FXML
    public void fillBoardOnClick(ActionEvent event) throws IOException {
        if (client.getPlayerNumber() == currentTurn) {
            Button currentButton = (Button) event.getSource();
            String btnID = currentButton.getId(); // e.g., "btn00"
            int row = btnID.charAt(3) - '0';
            int col = btnID.charAt(4) - '0';
            client.sendMoveToServer(row, col);
        } else {
            System.out.println("it's not your turn");
        }
    }

    @Subscribe
    public void Win(String winner) {
        Platform.runLater(() -> {
            if (winner.startsWith("Winner")) {
                disableAllButtons();
                if (winner.contains("X")) {
                    if (client.getPlayerNumber() == 1) {
                        textLabel.setText("You Win!");
                    } else {
                        textLabel.setText("You Lose!");
                    }
                } else {
                    if (client.getPlayerNumber() == 1) {
                        textLabel.setText("You Lose!");
                    } else {
                        textLabel.setText("You Win!");
                    }
                }
            } else if (winner.startsWith("Draw")) {
                disableAllButtons();
                textLabel.setText("It's a Draw!");
            }
        });
    }

    @Subscribe
    public void onMoveEvent(MoveEvent move) {
        Platform.runLater(() -> {
            Button button = getButtonByPosition(move.getRow(), move.getCol());
            if (button != null) {
                button.setText(String.valueOf(move.getMark()));

                button.setStyle(
                        "-fx-text-fill: " + (move.getMark() == 'X' ? "blue" : "red") + ";" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 24px;" +
                                "-fx-background-insets: 0;" +
                                "-fx-focus-color: transparent;" +
                                "-fx-faint-focus-color: transparent;" +
                                "-fx-background-color: transparent;" +
                                "-fx-effect: none;"
                );

                button.setDisable(true);
            }
        });
    }


    @Subscribe
    public void onTurnUpdate(String turnInfo) {
        if (turnInfo.startsWith("TURN")) {
            currentTurn = Integer.parseInt(turnInfo.split(" ")[1]);
            Platform.runLater(this::updateButtonGlowForTurn);
        }
    }

    private void removeButtonGlow(Button button) {
        button.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent; -fx-effect: none;");
        button.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                button.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent; -fx-effect: none;");
            }
        });
    }


    private void updateButtonGlowForTurn() {
        Button[] allButtons = {
                btn00, btn01, btn02,
                btn10, btn11, btn12,
                btn20, btn21, btn22
        };


        for (Button btn : allButtons) {


            if (!btn.isDisabled()) {

                if (client.getPlayerNumber() == currentTurn) {
                    btn.setStyle("-fx-border-color: skyblue; -fx-border-width: 3; -fx-border-radius: 5;");
                } else {
                    btn.setStyle(""); // remove glow if it's not your turn
                }
            } else {
                btn.setStyle(""); // also clear style for disabled buttons
            }
        }
    }


    private Button getButtonByPosition(int row, int col) {
        if (row == 0 && col == 0) return btn00;
        if (row == 0 && col == 1) return btn01;
        if (row == 0 && col == 2) return btn02;
        if (row == 1 && col == 0) return btn10;
        if (row == 1 && col == 1) return btn11;
        if (row == 1 && col == 2) return btn12;
        if (row == 2 && col == 0) return btn20;
        if (row == 2 && col == 1) return btn21;
        if (row == 2 && col == 2) return btn22;
        return null;
    }

    public void disableAllButtons() {
        btn00.setDisable(true);
        btn01.setDisable(true);
        btn02.setDisable(true);
        btn10.setDisable(true);
        btn11.setDisable(true);
        btn12.setDisable(true);
        btn20.setDisable(true);
        btn21.setDisable(true);
        btn22.setDisable(true);
    }
}