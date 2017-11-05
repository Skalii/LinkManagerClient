package com.skaliy.linkmanager.sites;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static javafx.scene.paint.Color.BLUE;

public class Pane {
    @FXML
    private Label labelSite, labelSiteOpenBrow, labelSectionCategory;

    @FXML
    private AnchorPane pane;

    @FXML
    private ImageView imageIcon, imageShare, imageLink;

    @FXML
    private TextArea textInfo;

    private String section, category;
    private Connect connect;

    public Pane(Connect connect, String section, String category) {
        this.connect = connect;
        this.section = section;
        this.category = category;
        createAnchorPane();
    }

    private void createAnchorPane() {
        imageIcon = new ImageView(new Image(connect.getLinkIcon()));
        imageIcon.setFitHeight(20);
        imageIcon.setFitWidth(20);
        imageIcon.setLayoutY(6);
        imageIcon.setLayoutX(10);

        labelSite = new Label(connect.getTitle());
        labelSite.setPrefWidth(400);
        labelSite.setLayoutY(5);
        labelSite.setLayoutX(40);
        labelSite.setFont(Font.font("Calibri", FontWeight.BOLD, 20));

        textInfo = new TextArea();
        textInfo.setWrapText(true);
        if (connect.getInfo() != null) {
            for (int i = 0; i < connect.getInfo().length; i++) {
                if (!Objects.equals(connect.getInfo()[i], null))
                    textInfo.setText(textInfo.getText() + connect.getInfo()[i]);
            }
            textInfo.setPrefHeight(160);
            textInfo.setPrefWidth(450);
            textInfo.setLayoutY(40);
            textInfo.setLayoutX(10);
            textInfo.setEditable(false);
        }

        imageLink = new ImageView();
        imageLink.setFitHeight(20);
        imageLink.setFitWidth(20);
        imageLink.setLayoutY(210);
        imageLink.setLayoutX(15);
        imageLink.getStyleClass().add("image-view-control");
        imageLink.getStyleClass().add("image-view-link");
        imageLink.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Адрес сайта");
            alert.setHeaderText(connect.getTitle());
            alert.getButtonTypes().setAll(ButtonType.OK);

            TextField textLink = new TextField(connect.getLinkSite());
            textLink.setAlignment(Pos.CENTER);
            textLink.setPrefHeight(50);
            textLink.setPrefWidth(220);
            textLink.setLayoutY(15);
            textLink.setLayoutX(15);
            textLink.setEditable(false);

            AnchorPane pane = new AnchorPane(textLink);
            pane.setPrefHeight(100);
            pane.setPrefWidth(250);

            alert.getDialogPane().setContent(pane);
            alert.showAndWait();
        });

        labelSiteOpenBrow = new Label("Открыть в браузере");
        labelSiteOpenBrow.setCursor(Cursor.HAND);
        labelSiteOpenBrow.setLayoutY(215);
        labelSiteOpenBrow.setLayoutX(45);
        labelSiteOpenBrow.setFont(Font.font("Calibri", FontWeight.BOLD, 12));
        labelSiteOpenBrow.setTextFill(BLUE);
        labelSiteOpenBrow.setOnMouseClicked(event -> {
            try {
                Desktop.getDesktop().browse(new URI(connect.getLinkSite()));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });

        labelSectionCategory = new Label(" | " + section + " | " + category);
        labelSectionCategory.setLayoutY(215);
        labelSectionCategory.setLayoutX(45 + labelSiteOpenBrow.getText().length() * 5 + 15);
        labelSectionCategory.setFont(Font.font("Calibri", FontWeight.BOLD, 12));

        imageShare = new ImageView();
        imageShare.setFitHeight(20);
        imageShare.setFitWidth(20);
        imageShare.setLayoutY(215);
        imageShare.setLayoutX(440);
        imageShare.getStyleClass().add("image-view-control");
        imageShare.getStyleClass().add("image-view-share");

        pane = new AnchorPane();
        pane.setPrefHeight(240);
        pane.setPrefWidth(470);
        pane.getStyleClass().add("anchor-sites");

        pane.getChildren().addAll(imageIcon, labelSite, textInfo,
                imageLink, labelSiteOpenBrow, labelSectionCategory, imageShare);
    }

    public AnchorPane getPane() {
        return this.pane;
    }

    public Label getLabelSite() {
        return labelSite;
    }

    public void setLabelSite(Label labelSite) {
        this.labelSite = labelSite;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return connect.getTitle();
    }

    public String getLinkSite() {
        return connect.getLinkSite();
    }
}
