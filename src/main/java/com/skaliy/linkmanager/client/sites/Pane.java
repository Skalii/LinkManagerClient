package com.skaliy.linkmanager.client.sites;

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
    private ImageView imageIcon, imageLinkShare;

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
        pane = new AnchorPane();
        pane.setPrefWidth(470);
        pane.setPrefHeight(240);
        pane.getStyleClass().add("anchor-sites");

        imageIcon = new ImageView(new Image(connect.getLinkIcon()));
        imageIcon.setFitWidth(20);
        imageIcon.setFitHeight(20);
        imageIcon.setLayoutX(10);
        imageIcon.setLayoutY(6);

        labelSite = new Label(connect.getTitle());
        labelSite.setPrefWidth(400);
        labelSite.setLayoutX(40);
        labelSite.setLayoutY(5);
        labelSite.setFont(Font.font("Calibri", FontWeight.BOLD, 20));

        textInfo = new TextArea();
        textInfo.setWrapText(true);
        if (connect.getInfo() != null) {
            for (int i = 0; i < connect.getInfo().length; i++) {
                if (!Objects.equals(connect.getInfo()[i], null))
                    textInfo.setText(textInfo.getText() + connect.getInfo()[i]);
            }
            textInfo.setPrefWidth(450);
            textInfo.setPrefHeight(160);
            textInfo.setLayoutX(10);
            textInfo.setLayoutY(40);
            textInfo.setEditable(false);
        }

        labelSiteOpenBrow = new Label("Открыть в браузере");
        labelSiteOpenBrow.setCursor(Cursor.HAND);
        labelSiteOpenBrow.setLayoutX(10);
        labelSiteOpenBrow.setLayoutY(215);
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
        labelSectionCategory.setLayoutX(labelSiteOpenBrow.getLayoutX() + labelSiteOpenBrow.getText().length() * 5 + 15);
        labelSectionCategory.setLayoutY(215);
        labelSectionCategory.setFont(Font.font("Calibri", FontWeight.BOLD, 12));

        imageLinkShare = new ImageView();
        imageLinkShare.setFitWidth(20);
        imageLinkShare.setFitHeight(20);
        imageLinkShare.setLayoutX(pane.getPrefWidth() - 30);
        imageLinkShare.setLayoutY(210);
        imageLinkShare.getStyleClass().add("image-view-control");
        imageLinkShare.getStyleClass().add("image-view-link-share");
        imageLinkShare.setOnMouseClicked(event -> {
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

        pane.getChildren().addAll(imageIcon, labelSite, textInfo,
                labelSiteOpenBrow, labelSectionCategory, imageLinkShare);
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