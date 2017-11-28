package com.skaliy.linkmanager.client.fxapp;

import com.skaliy.linkmanager.client.client.Client;
import com.skaliy.linkmanager.client.sites.Connect;
import com.skaliy.linkmanager.client.sites.Pane;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Controller {
    @FXML
    private ImageView imageTitle, imageMinimize, imageClose;

    @FXML
    private MenuItem menuNewWindow, menuCloseWindow, menuHide, menuExit, menuAbout;

    @FXML
    private AnchorPane anchorSitesParent, anchorNavSort, anchorNavSearch, anchorNavProfile;

    @FXML
    private ComboBox<String> comboSections, comboCategories;

    @FXML
    private Label labelSort, labelProfileTitle, labelReg, labelVerifyError;

    @FXML
    private TextField textSearch, textLogin, textRegLogin, textRegEmail, textRegFName, textRegLName;

    @FXML
    private PasswordField textPassword, textRegPass1, textRegPass2;

    @FXML
    private Button buttonProfileLogin, buttonProfileLogOut, buttonProfileBookmarks, buttonReg;

    private static ObservableList<Connect> listConnectInfoBase, listConnectProgramming, listConnectReading;
    private ObservableList<Pane> listPaneAll, listPaneCurrent,
            listPaneInfoBase, listPaneProgramming, listPaneReading;

    private String[] currentProfile, sections, categoryInfobase, categoryProgramming, categoryReading;

    private Client client;

    private boolean[] isBookmark;
    private int countBookmark = -1;

    public void initialize() throws InterruptedException {
        client = new Client("localhost", 7777);
        Thread thread = new Thread(client);
        thread.start();

        while (true) {
            try {
                if (client.isOpen())
                    break;
            } catch (NullPointerException e) {
                Thread.sleep(50);
            }
        }

        int indexThisStage = Main.quant;

        imageMinimize.setOnMouseClicked(event -> Main.parent[indexThisStage].setIconified(true));
        imageClose.setOnMouseClicked(event -> {
            Platform.setImplicitExit(true);
            Main.systemTray[indexThisStage].remove(Main.trayIcon[indexThisStage]);
            Main.parent[indexThisStage].close();
        });

        menuNewWindow.setOnAction(event -> {
            try {
                new Main().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        menuCloseWindow.setOnAction(event -> {
            Platform.setImplicitExit(true);
            Main.systemTray[indexThisStage].remove(Main.trayIcon[indexThisStage]);
            Main.parent[indexThisStage].close();

            boolean exit = true;
            for (int i = 0; i < Main.parent.length; i++) {
                try {
                    if (Main.parent[i].isShowing() || Main.parent[i].isIconified()) {
                        exit = false;
                    }
                } catch (NullPointerException ignored) {
                }
            }
            if (exit) {
                System.exit(0);
            }
        });
        menuHide.setOnAction(event -> Main.parent[indexThisStage].hide());
        menuExit.setOnAction(event -> System.exit(0));
        menuAbout.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("О программе");
            alert.setHeaderText("Менеджер ссылок");
            alert.getButtonTypes().setAll(ButtonType.OK);

            Label label = new Label("Самостоятельная работа\nСтудента гр.341(б)\nСкалий Дмитрия");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER);
            label.setPrefHeight(70);
            label.setPrefWidth(220);
            label.setLayoutY(15);
            label.setLayoutX(15);

            AnchorPane pane = new AnchorPane(label);
            pane.setPrefHeight(100);
            pane.setPrefWidth(250);

            alert.getDialogPane().setContent(pane);
            alert.showAndWait();
        });

        sections = setSections();
        categoryInfobase = setCategory(1);
        categoryProgramming = setCategory(2);
        categoryReading = setCategory(3);
        isBookmark = new boolean[Integer.parseInt(client.query("get_count_links").get(0)[0])];


        if (listConnectInfoBase == null)
            listConnectInfoBase = setListConnects(1);
        listPaneInfoBase = setListPanes(listConnectInfoBase, 1);

        if (listConnectProgramming == null)
            listConnectProgramming = setListConnects(2);
        listPaneProgramming = setListPanes(listConnectProgramming, 2);


        listPaneReading = FXCollections.observableArrayList();

        setListCurrentSites(listPaneInfoBase, listPaneProgramming, listPaneReading);
        setListPaneAll(listPaneInfoBase, listPaneProgramming, listPaneReading);

        // TODO: 25.10.2017 QUANTITY SITES
        System.out.println("\nКоличество сайтов: " + listPaneAll.size());


        textSearch.setOnKeyReleased(event -> searchSites(textSearch.getText()));

        labelReg.setOnMouseClicked(event -> setAnchorNavRegistration());
        verifyAuthorization();
        buttonProfileLogOut.setOnAction(event -> logOut());
        buttonProfileBookmarks.setOnAction(event -> {
            try {
                setListBookmarkSites(
                        listPaneInfoBase, listPaneProgramming, listPaneReading);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        comboSections.getItems().setAll("- выбор -");
        comboSections.getItems().addAll(sections);
        comboCategories.getItems().setAll("- выбор -");

        comboSections.setOnAction(eventSort -> {

            textSearch.setText("");

            comboCategories.setDisable(false);
            comboCategories.getItems().remove(1, comboCategories.getItems().size());

            switch (comboSections.getSelectionModel().getSelectedIndex()) {
                case 0:
                    setListCurrentSites(listPaneInfoBase, listPaneProgramming, listPaneReading);
                    comboCategories.getSelectionModel().clearAndSelect(0);
                    comboCategories.setDisable(true);
                    break;
                case 1:
                    comboCategories.getItems().addAll(categoryInfobase);
                    setListCurrentSites(listPaneInfoBase, sections[0], true);

                    comboCategories.setOnAction(eventCriterion -> {
                        textSearch.setText("");
                        switch (comboCategories.getSelectionModel().getSelectedIndex()) {
                            case 0:
                                setListCurrentSites(listPaneInfoBase, sections[0], true);
                                break;
                            case 1:
                                setListCurrentSites(listPaneInfoBase, categoryInfobase[0], false);
                                break;
                            case 2:
                                setListCurrentSites(listPaneInfoBase, categoryInfobase[1], false);
                                break;
                            case 3:
                                setListCurrentSites(listPaneInfoBase, categoryInfobase[2], false);
                                break;
                            case 4:
                                setListCurrentSites(listPaneInfoBase, categoryInfobase[3], false);
                                break;
                        }
                    });
                    break;
                case 2:
                    comboCategories.getItems().addAll(categoryProgramming);
                    setListCurrentSites(listPaneProgramming, sections[1], true);

                    comboCategories.setOnAction(eventCriterion -> {
                        textSearch.setText("");
                        switch (comboCategories.getSelectionModel().getSelectedIndex()) {
                            case 0:
                                setListCurrentSites(listPaneProgramming, sections[1], true);
                                break;
                            case 1:
                                setListCurrentSites(listPaneProgramming, categoryProgramming[0], false);
                                break;
                            case 2:
                                setListCurrentSites(listPaneProgramming, categoryProgramming[1], false);
                                break;
                            case 3:
                                setListCurrentSites(listPaneProgramming, categoryProgramming[2], false);
                                break;
                            case 4:
                                setListCurrentSites(listPaneProgramming, categoryProgramming[3], false);
                                break;
                        }
                    });
                    break;
                case 3:
                    comboCategories.getItems().addAll(categoryReading);
                    setListCurrentSites(listPaneReading, sections[2], true);

                    comboCategories.setOnAction(eventCriterion -> {
                        textSearch.setText("");
                        switch (comboCategories.getSelectionModel().getSelectedIndex()) {
                            case 0:
                                setListCurrentSites(listPaneReading, sections[2], true);
                                break;
                            case 1:
                                setListCurrentSites(listPaneReading, categoryReading[0], false);
                                break;
                            case 2:
                                setListCurrentSites(listPaneReading, categoryReading[1], false);
                                break;
                            case 3:
                                setListCurrentSites(listPaneReading, categoryReading[2], false);
                                break;
                            case 4:
                                setListCurrentSites(listPaneReading, categoryReading[3], false);
                                break;
                        }
                    });
                    break;
            }
        });
    }

    private void searchSites(String search) {
        anchorSitesParent.getChildren().remove(0, anchorSitesParent.getChildren().size());
        int layoutY = 10;

        if (!Objects.equals(search, "")) {
            for (int i = 0, index = 0; i < listPaneCurrent.size(); i++)
                if (listPaneCurrent.get(i).getSection().toLowerCase().contains(search.toLowerCase())
                        || listPaneCurrent.get(i).getCategory().toLowerCase().contains(search.toLowerCase())
                        || listPaneCurrent.get(i).getTitle().toLowerCase().contains(search.toLowerCase())
                        || listPaneCurrent.get(i).getLinkSite().toLowerCase().contains(search.toLowerCase())) {
                    anchorSitesParent.getChildren().add(listPaneCurrent.get(i).getPane());
                    anchorSitesParent.getChildren().get(index).setLayoutY(layoutY);
                    anchorSitesParent.getChildren().get(index++).setLayoutX(10);
                    layoutY += 255;
                }
        } else for (int i = 0; i < listPaneCurrent.size(); i++) {
            anchorSitesParent.getChildren().add(listPaneCurrent.get(i).getPane());
            anchorSitesParent.getChildren().get(i).setLayoutY(layoutY);
            anchorSitesParent.getChildren().get(i).setLayoutX(10);
            layoutY += 255;
        }
        anchorSitesParent.setPrefHeight(
                anchorSitesParent.getChildren().size() > 0
                        ? (anchorSitesParent.getChildren().size() * 255 + 10)
                        : 0);
    }

    private String[] setSections() {
        ArrayList<String[]> records = client.query("get_sections");
        String[] result = new String[records.size()];

        for (int i = 0; i < records.size(); i++) {
            result[i] = records.get(i)[0];
        }

        return result;
    }

    private String[] setCategory(int index) {
        ArrayList<String[]> records = client.query("get_category_p-" + index);
        String[] result = new String[records.size()];

        for (int i = 0; i < records.size(); i++)
            result[i] = records.get(i)[0];

        return result;
    }

    private void setListPaneAll(ObservableList<Pane>... sites) {
        listPaneAll = FXCollections.observableArrayList();
        for (int i = 0, index = 0; i < sites.length; i++)
            for (int j = 0; j < sites[i].size(); j++, index++)
                listPaneAll.add(sites[i].get(j));
    }

    private void setListCurrentSites(ObservableList<Pane>... sites) {
        anchorSitesParent.getChildren().remove(0, anchorSitesParent.getChildren().size());
        listPaneCurrent = FXCollections.observableArrayList();
        int layoutY = 10;

        for (int i = 0, index = 0; i < sites.length; i++) {
            for (int j = 0; j < sites[i].size(); j++, index++) {
                anchorSitesParent.getChildren().add(sites[i].get(j).getPane());
                anchorSitesParent.getChildren().get(index).setLayoutY(layoutY);
                anchorSitesParent.getChildren().get(index).setLayoutX(10);
                layoutY += 255;
                listPaneCurrent.add(sites[i].get(j));
            }
        }
        anchorSitesParent.setPrefHeight(anchorSitesParent.getChildren().size() > 0
                ? (anchorSitesParent.getChildren().size() * 255 + 10)
                : 0);
    }

    private void setListCurrentSites(ObservableList<Pane> sites,
                                     String equal, boolean section) {
        anchorSitesParent.getChildren().remove(0, anchorSitesParent.getChildren().size());
        listPaneCurrent = FXCollections.observableArrayList();
        int layoutY = 10;

        if (section) {
            for (int i = 0, j = 0; i < sites.size(); i++)
                if (Objects.equals(sites.get(i).getSection(), equal)) {
                    anchorSitesParent.getChildren().add(sites.get(i).getPane());
                    anchorSitesParent.getChildren().get(j).setLayoutY(layoutY);
                    anchorSitesParent.getChildren().get(j++).setLayoutX(10);
                    layoutY += 255;
                    listPaneCurrent.add(sites.get(i));
                }
        } else
            for (int i = 0, j = 0; i < sites.size(); i++)
                if (Objects.equals(sites.get(i).getCategory(), equal)) {
                    anchorSitesParent.getChildren().add(sites.get(i).getPane());
                    anchorSitesParent.getChildren().get(j).setLayoutY(layoutY);
                    anchorSitesParent.getChildren().get(j++).setLayoutX(10);
                    listPaneCurrent.add(sites.get(i));
                    layoutY += 255;
                }
        anchorSitesParent.setPrefHeight(anchorSitesParent.getChildren().size() > 0
                ? (anchorSitesParent.getChildren().size() * 255 + 10)
                : 0);
    }

    private ObservableList<Connect> setListConnects(int id_section) {
        ObservableList<Connect> connectSites = FXCollections.observableArrayList();

        ArrayList<String[]> infoBase = client.query(
                "get_list_connects_p-" + id_section);

        int i = 0;
        for (String[] anInfoBase : infoBase) {
            connectSites.add(new Connect(anInfoBase[2], anInfoBase[3], anInfoBase[4]));

            // TODO: 23.10.2017 CONNECTED TO SITE
            System.out.println("Подключен сайт " + connectSites.get(i++).getLinkSite());
        }

        return connectSites;
    }

    private ObservableList<Pane> setListPanes(ObservableList<Connect> connectSites, int id_section) {
        ObservableList<Pane> anchorSites = FXCollections.observableArrayList();

        ArrayList<String[]> infoBase = client.query("get_list_from_section_p-" + id_section);

        for (int i = 0; i < connectSites.size(); i++) {
            anchorSites.add(
                    new Pane(connectSites.get(i),
                            sections[id_section - 1],
                            client.query("get_category_from_p-" + infoBase.get(i)[1]).get(0)[0]));

            anchorSites.get(i).getPane().getChildren().add(createBookmark());

            // TODO: 23.10.2017 CREATE PANE FOR SITE
            System.out.println("Создана панель для сайта " + anchorSites.get(i).getLinkSite());
        }
        return anchorSites;
    }

    private void setListBookmarkSites(ObservableList<Pane>... sites) throws SQLException {
        comboSections.getSelectionModel().clearSelection();
        comboCategories.getItems().remove(1, comboCategories.getItems().size());
        comboCategories.setDisable(true);

        textSearch.setText("");

        anchorSitesParent.getChildren().remove(0, anchorSitesParent.getChildren().size());
        listPaneCurrent = FXCollections.observableArrayList();
        int layoutY = 10;

        ArrayList<String[]> bookmarks = client.query("get_bookmarks_from_p-" + currentProfile[0]);

        // TODO: 25.10.2017 BOOKMARKS
        System.out.println("Сайты в закладках " + currentProfile[1] + ": " + Arrays.deepToString(new ArrayList[]{bookmarks}));

        for (int i = 0, index = 0; i < sites.length; i++)
            for (int j = 0; j < sites[i].size(); j++)
                for (int k = 0; k < bookmarks.size(); k++)
                    if (Objects.equals(sites[i].get(j).getLinkSite(), bookmarks.get(k)[0])) {
                        anchorSitesParent.getChildren().add(sites[i].get(j).getPane());
                        anchorSitesParent.getChildren().get(index).setLayoutY(layoutY);
                        anchorSitesParent.getChildren().get(index).setLayoutX(10);
                        layoutY += 255;
                        listPaneCurrent.add(sites[i].get(j));
                        index++;
                    }

        anchorSitesParent.setPrefHeight(anchorSitesParent.getChildren().size() > 0
                ? (anchorSitesParent.getChildren().size() * 255 + 10)
                : 0);
    }

    private ImageView createBookmark() {
        ImageView imageBookmark = new ImageView();
        imageBookmark.setFitHeight(20);
        imageBookmark.setFitWidth(20);
        imageBookmark.setLayoutY(7);
        imageBookmark.setLayoutX(442);
        imageBookmark.getStyleClass().add("image-view-control");
        imageBookmark.getStyleClass().add("image-view-bookmark");

        int thisBookmark = ++countBookmark;
        imageBookmark.setOnMouseClicked(event -> {
            if (currentProfile != null) {
                imageBookmark.getStyleClass().remove(2);

                if (!isBookmark[thisBookmark]) {
                    isBookmark[thisBookmark] = true;
                    imageBookmark.getStyleClass().add("image-view-bookmark-selected");
                    try {
                        addBookmark(thisBookmark);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                isBookmark[thisBookmark] = false;
                imageBookmark.getStyleClass().add("image-view-bookmark");
                try {
                    removeBookmark(thisBookmark);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        return imageBookmark;
    }

    private void addBookmark(int indexBookmark) throws SQLException {
        client.query(false,
                "add_bookmark," + currentProfile[0]
                        + "," + listPaneAll.get(indexBookmark).getLinkSite());

        currentProfile[7] = client.query("get_bookmarks_ids_from_p-" + currentProfile[0]).get(0)[0];

        // TODO: 25.10.2017 ADD BOOKMARK
        System.out.println("Добавлена закладка " + currentProfile[1] + ": " + listPaneAll.get(indexBookmark).getLinkSite() +
                "\nСайты в закладках  " + currentProfile[1] + ": " + Arrays.deepToString(new ArrayList[]{client.query(
                "get_bookmarks_from_p-" + currentProfile[0])}));
    }

    private void removeBookmark(int indexBookmark) throws SQLException {
        client.query(false,
                "delete_bookmark," + currentProfile[0]
                        + "," + listPaneAll.get(indexBookmark).getLinkSite());

        currentProfile[7] = client.query("get_bookmarks_ids_from_p-" + currentProfile[0]).get(0)[0];

        // TODO: 25.10.2017 REMOVE BOOKMARK
        System.out.println("Удалена закладка  " + currentProfile[1] + ": " + listPaneAll.get(indexBookmark).getLinkSite() +
                "\nСайты в закладках " + currentProfile[1] + ": " + Arrays.deepToString(new ArrayList[]{client.query(
                "get_bookmarks_from_p-" + currentProfile[0])}));
    }

    private void verifyAuthorization() {
        String[] verifications = new String[2];
        boolean[] isVerificated = new boolean[2];

        buttonProfileLogin.setOnAction(event -> {

            if (!textLogin.getText().isEmpty()) {
                if (!textPassword.getText().isEmpty()) {
                    labelVerifyError.setVisible(false);

                    verifications[0] = textLogin.getText();

                    isVerificated[0] = Objects.equals(verifications[0],
                            client.query("get_login_p-" + verifications[0]).get(0)[0]);

                    if (isVerificated[0]) {

                        verifications[1] = textPassword.getText();

                        isVerificated[1] = Objects.equals(verifications[1],
                                client.query("get_pass_login_c-L = '" + verifications[0]
                                        + "';P = '" + verifications[1] + "'").get(0)[0]);

                        if (isVerificated[1]) {

                            currentProfile = client.query("get_profile_c-L = '" + verifications[0]
                                    + "';P = '" + verifications[1] + "'").get(0);

                            setAnchorNavProfile(true);

                            ArrayList<String[]> bookmarks = client.query("get_bookmarks_from_p-" + currentProfile[0]);

                            for (int j = 0; j < listPaneAll.size(); j++)
                                for (String[] bookmark : bookmarks)
                                    if (Objects.equals(listPaneAll.get(j).getLinkSite(), bookmark[0])) {
                                        isBookmark[j] = true;
                                        int size = listPaneAll.get(j).getPane().getChildren().size();
                                        listPaneAll.get(j).getPane().getChildren().get(size - 1).getStyleClass().remove(2);
                                        listPaneAll.get(j).getPane().getChildren().get(size - 1).getStyleClass().add("image-view-bookmark-selected");
                                    }

                            // TODO: 25.10.2017 LOGIN
                            System.out.println("Выполнен вход: " + Arrays.toString(currentProfile));

                            for (int i = 0; i < 2; i++) {
                                verifications[i] = "";
                                isVerificated[i] = false;
                            }

                            buttonProfileLogin.setVisible(false);

                        } else {
                            labelVerifyError.setText("Неправильный логин или пароль");
                            labelVerifyError.setVisible(true);
                        }

                    } else {
                        labelVerifyError.setText("Неправильный логин или пароль");
                        labelVerifyError.setVisible(true);
                    }

                } else {
                    labelVerifyError.setText("Введите пароль");
                    labelVerifyError.setVisible(true);
                }
            } else {
                labelVerifyError.setText("Введите логин");
                labelVerifyError.setVisible(true);
            }

        });

        textLogin.setOnAction(buttonProfileLogin.getOnAction());
        textPassword.setOnAction(buttonProfileLogin.getOnAction());
    }

    private void logOut() {
        buttonProfileLogin.setVisible(true);

        comboSections.getSelectionModel().clearSelection();
        comboCategories.getItems().remove(1, comboCategories.getItems().size());
        comboCategories.setDisable(true);

        // TODO: 25.10.2017 LOGOUT
        System.out.println("Выход: " + Arrays.toString(currentProfile));

        currentProfile = null;
        setAnchorNavProfile(false);

        for (int j = 0; j < listPaneAll.size(); j++) {
            isBookmark[j] = false;
            int size = listPaneAll.get(j).getPane().getChildren().size();
            listPaneAll.get(j).getPane().getChildren().get(size - 1).getStyleClass().remove(2);
            listPaneAll.get(j).getPane().getChildren().get(size - 1).getStyleClass().add("image-view-bookmark");
        }

        setListCurrentSites(listPaneInfoBase, listPaneProgramming, listPaneReading);

        textSearch.setText("");
    }

    private void setAnchorNavProfile(boolean isLogin) {
        textLogin.setText("");
        textPassword.setText("");

        if (isLogin) {
            labelReg.setVisible(false);

            labelProfileTitle.setText("Добро пожаловать, " +
                    (!currentProfile[4].isEmpty() && !currentProfile[3].isEmpty()
                            ? (currentProfile[4] + " " + currentProfile[3])
                            : currentProfile[1])
                    + "!");
            labelProfileTitle.setFont(new Font(12));
            labelProfileTitle.setLayoutX(15);
            labelProfileTitle.setLayoutY(15);
            labelProfileTitle.setPrefWidth(205);

            labelReg.setVisible(false);

            textLogin.setVisible(false);
            textPassword.setVisible(false);

            buttonProfileLogOut.setVisible(true);
            buttonProfileBookmarks.setVisible(true);
            return;
        }

        labelReg.setVisible(true);

        labelProfileTitle.setText("Авторизация");
        labelProfileTitle.setFont(new Font(14));
        labelProfileTitle.setLayoutX(70);
        labelProfileTitle.setLayoutY(50);
        labelProfileTitle.setPrefWidth(Region.USE_COMPUTED_SIZE);

        textLogin.setVisible(true);
        textPassword.setVisible(true);

        buttonProfileLogOut.setVisible(false);
        buttonProfileBookmarks.setVisible(false);

        if (buttonReg.isVisible()) {
            buttonReg.setVisible(false);

            textRegLogin.setText("");
            textRegEmail.setText("");
            textRegFName.setText("");
            textRegLName.setText("");
            textRegPass1.setText("");
            textRegPass2.setText("");

            textRegLogin.setVisible(false);
            textRegEmail.setVisible(false);
            textRegFName.setVisible(false);
            textRegLName.setVisible(false);
            textRegPass1.setVisible(false);
            textRegPass2.setVisible(false);

            labelReg.setText("Регистрация");
            labelReg.setTextFill(Paint.valueOf("#3a3cda"));
            labelReg.setLayoutX(72);
            labelReg.setLayoutY(255);
        }
    }

    private void setAnchorNavRegistration() {
        labelProfileTitle.setText("Регистрация");
        labelProfileTitle.setLayoutX(75);
        labelProfileTitle.setLayoutY(2);
        labelProfileTitle.setFont(new Font(12));

        labelReg.setVisible(false);
        textLogin.setVisible(false);
        textPassword.setVisible(false);
        buttonProfileLogin.setVisible(false);
        labelVerifyError.setVisible(false);

        buttonProfileLogOut.setVisible(true);
        buttonReg.setVisible(true);
        textRegLogin.setVisible(true);
        textRegEmail.setVisible(true);
        textRegFName.setVisible(true);
        textRegLName.setVisible(true);
        textRegPass1.setVisible(true);
        textRegPass2.setVisible(true);

        textRegLogin.setText("");
        textRegEmail.setText("");
        textRegFName.setText("");
        textRegLName.setText("");
        textRegPass1.setText("");
        textRegPass2.setText("");

        buttonReg.setOnAction(event -> {
            if (textRegLogin.getText().isEmpty() || textRegEmail.getText().isEmpty()
                    || textRegPass1.getText().isEmpty() || textRegPass2.getText().isEmpty()) {
                labelReg.setVisible(true);
                labelReg.setText("Обязательные поля пустые");
                labelReg.setTextFill(Paint.valueOf("#d83a3a"));
                labelReg.setLayoutX(27);
                labelReg.setLayoutY(255);
            } else {
                labelReg.setVisible(false);
                labelReg.setText("Регистрация");
                labelReg.setTextFill(Paint.valueOf("#3a3cda"));
                labelReg.setLayoutX(72);
                labelReg.setLayoutY(255);

                ArrayList<String[]> profiles = client.query("get_logins_emails");

                boolean validated = false;

                for (int i = 0; i < profiles.size(); i++) {
                    if (Objects.equals(profiles.get(i)[0], textRegLogin.getText())) {
                        textRegLogin.setText("");
                        labelReg.setVisible(true);
                        labelReg.setText("Этот логин уже существует");
                        labelReg.setTextFill(Paint.valueOf("#d83a3a"));
                        labelReg.setLayoutX(20);
                        labelReg.setLayoutY(255);

                        validated = false;
                        break;
                    }

                    labelReg.setVisible(false);
                    labelReg.setText("Регистрация");
                    labelReg.setTextFill(Paint.valueOf("#3a3cda"));
                    labelReg.setLayoutX(72);
                    labelReg.setLayoutY(255);

                    validated = true;
                }

                if (validated) {

                    for (int i = 0; i < profiles.size(); i++) {
                        if (Objects.equals(profiles.get(i)[1], textRegEmail.getText())) {
                            textRegEmail.setText("");
                            labelReg.setVisible(true);
                            labelReg.setText("Этот email уже зарегистрирован");
                            labelReg.setTextFill(Paint.valueOf("#d83a3a"));
                            labelReg.setLayoutX(10);
                            labelReg.setLayoutY(255);

                            validated = false;

                            break;
                        }

                        labelReg.setVisible(false);
                        labelReg.setText("Регистрация");
                        labelReg.setTextFill(Paint.valueOf("#3a3cda"));
                        labelReg.setLayoutX(72);
                        labelReg.setLayoutY(255);

                        validated = true;
                    }

                    if (validated) {
                        if (!Objects.equals(textRegPass1.getText(), textRegPass2.getText())) {
                            labelReg.setVisible(true);
                            labelReg.setText("Пароли не совпадают");
                            labelReg.setTextFill(Paint.valueOf("#d83a3a"));
                            labelReg.setLayoutX(45);
                            labelReg.setLayoutY(255);

                            validated = false;
                        } else {
                            labelReg.setVisible(false);
                            labelReg.setText("Регистрация");
                            labelReg.setTextFill(Paint.valueOf("#3a3cda"));
                            labelReg.setLayoutX(72);
                            labelReg.setLayoutY(255);

                            validated = true;
                        }

                        if (validated) {
                            client.query(false,
                                    "add_profile," +
                                            textRegLogin.getText() + "," +
                                            textRegPass1.getText() + "," +
                                            textRegLName.getText() + "," +
                                            textRegFName.getText() + "," +
                                            textRegEmail.getText());

                            // TODO: 25.10.2017 REGISTRATION
                            System.out.println("Добавлен пользователь: " + textRegLogin.getText());

                            setAnchorNavProfile(false);
                            buttonProfileLogin.setVisible(true);
                        }
                    }
                }
            }
        });
    }

}